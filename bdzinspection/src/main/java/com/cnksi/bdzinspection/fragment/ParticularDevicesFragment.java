package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.CopyValueActivity2;
import com.cnksi.bdzinspection.activity.SingleSpaceCopyActivity;
import com.cnksi.bdzinspection.activity.xian.XNewDeviceDetailsActivity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.daoservice.SpacingLastlyService;
import com.cnksi.bdzinspection.daoservice.SpecialMenuService;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.bdzinspection.other.ReQuestLocationDialog;
import com.cnksi.bdzinspection.utils.NextDeviceUtils;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingGroupService;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.DefectInfo;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.QWERKeyBoardUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;

/**
 * Created by han on 2017/2/27.
 */

public class ParticularDevicesFragment extends BaseFragment implements QWERKeyBoardUtils.keyWordChangeListener {

    public static final String TAG = "ParticularDevicesFragment";
    SpecialMenu specialMenu;
    private ViewHolder rootHolder;
    private List<MultiItemEntity> data = new ArrayList<>();

    private DeviceAdapter adapter;
    private RecyclerView recyclerView;

    private ArriveCheckHelper arriveCheckHelper;
    private QWERKeyBoardUtils qwerKeyBoardUtils;
    private SpacingLastly spacingLastly;
    private LinkedHashMap<String, SpaceGroupItem> spaceGroupMap;
    long startTime;
    private HashSet<String> hasQrCodeSpids;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        rootHolder = new ViewHolder(currentActivity, container, R.layout.common_recycler_view, false);
        startTime = System.currentTimeMillis();
        initialUI();
        initSpacingGroup();
        lazyLoad();
//        Log.d(TAG, "onCreateView: " + (System.currentTimeMillis() - startTime));
        return rootHolder.getRootView();
    }

    private void initialUI() {
        getBundleValue();
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.initOtherSerchView(rootHolder.getView(R.id.ll_root_container), this);
        adapter = new DeviceAdapter(currentActivity, data);
        adapter.setCurrentFunctionMode(currentFunctionModel);
        adapter.setCurrentInspectionType(currentInspectionType);
        adapter.setCurrentInspection(currentInspectionType);
        // 跳转设备详情
        adapter.setDeviceClickListener((v, dbModel, position) -> {
            Intent intent = new Intent(currentActivity, XNewDeviceDetailsActivity.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString(DeviceService.DEVICE_ID_KEY));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            ParticularDevicesFragment.this.startActivity(intent);
        });
        // 跳转设备抄录
        adapter.setCopyClickListener((v, dbModel, position) -> {
            Intent intent = new Intent(currentActivity, CopyValueActivity2.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString(DeviceService.DEVICE_ID_KEY));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            intent.putExtra(Config.TITLE_NAME_KEY, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            ParticularDevicesFragment.this.startActivity(intent);
        });
        // 设置间隔点击事件
        adapter.setGroupItemClickListener((v, dbModel, position) -> {
            if (v.getId() == R.id.ibt_copy_pen) {
                PlaySound.getIntance(currentActivity).play(R.raw.input);
                Intent intent = new Intent(currentActivity, SingleSpaceCopyActivity.class);
                intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
                intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
                intent.putExtra(Config.CURRENT_FUNCTION_MODEL, currentFunctionModel);
                ParticularDevicesFragment.this.startActivity(intent);
            } else if (v.getId() == R.id.iv_haslocationed) {
                if (v.getTag() != null) {
                    final ReQuestLocationDialog dialog = ReQuestLocationDialog.getInstance();
                    dialog.setItemClickListener((v1, data, position1) -> {
                        if (v1.getId() == R.id.bt_photo_location) {
                            String photoName = (String) data;
                            arriveCheckHelper.saveLocation(dbModel, null, true, photoName);
                            ToastUtils.showMessage("保存成功");
                            dialog.dismiss();
                        } else if (v1.getId() == R.id.bt_request_location) {
                            BDLocation location = (BDLocation) data;
                            arriveCheckHelper.saveLocation(dbModel, location, true);
                            ToastUtils.showMessage("保存成功");
                            dialog.dismiss();
                        }

                    });
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    dialog.show(fragmentTransaction, "dialog");
                }

            }
        });
        recyclerView = rootHolder.getView(R.id.elv_container);
        final GridLayoutManager manager = new GridLayoutManager(currentActivity, PMSDeviceType.second.equals(currentFunctionModel) ? 2 : 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == DEVICE_ITEM ? 1 : manager.getSpanCount();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        isPrepared = true;
        arriveCheckHelper = new ArriveCheckHelper(currentActivity, adapter, currentReportId, currentInspectionType, currentBdzId, currentFunctionModel);
    }


    @Override
    protected void getBundleValue() {
        super.getBundleValue();
    }

    @Override
    protected void lazyLoad() {
        if (isFirstLoad) {
            searchCurrentDeviceType();
        }
        if (isPrepared && isVisible && isFirstLoad) {
            searChData("");
            isFirstLoad = false;
        }

    }

    private void searchCurrentDeviceType() {
        ExecutorManager.executeTaskSerially(() -> {
            specialMenu = SpecialMenuService.getInstance().findCurrentDeviceType(currentInspectionType);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            queryInfo();
            if (null != arriveCheckHelper) {
                arriveCheckHelper.refreshArrived();
            }
        }
    }

    private HashSet<String> copyDeviceIdList = new HashSet<>();
    Map<String, List<String>> spaceCopyDeviceMap = new HashMap<>();

    private void queryInfo() {
        ExecutorManager.executeTask(() -> {
            // 查寻缺陷
            HashMap<String, DefectInfo> defectmap = DeviceService.getInstance().findDeviceDefect(currentBdzId);
            adapter.setDefectMap(defectmap);
            // 查寻抄录数量
            Map<String, List<String>> copyedMap = DefectRecordService.getInstance().getCopyDevice(currentReportId, currentBdzId, currentFunctionModel, currentInspectionType);
            // 查询当前变电站抄录设备
            List<DbModel> copyDevices = CopyItemService.getInstance().findCopyDeviceId(currentBdzId, currentInspectionType);
            copyDeviceIdList.clear();
            spaceCopyDeviceMap.clear();
            if (null != copyDevices && !copyDevices.isEmpty()) {
                for (DbModel dbModel : copyDevices) {
                    String deviceID = dbModel.getString("deviceid");
                    String deviceSpID = dbModel.getString("spid");
                    copyDeviceIdList.add(deviceID);
                    if (!copyDeviceIdList.contains(deviceSpID)) {
                        copyDeviceIdList.add(deviceSpID);
                    }
                    if (spaceCopyDeviceMap.keySet().contains(deviceSpID)) {
                        if (null != spaceCopyDeviceMap.get(deviceSpID)) {
                            spaceCopyDeviceMap.get(deviceSpID).add(deviceID);
                        }
                    } else {
                        List<String> deviceIDs = new ArrayList<>();
                        deviceIDs.add(deviceID);
                        spaceCopyDeviceMap.put(deviceSpID, deviceIDs);
                    }
                }
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (adapter != null) {
                        adapter.setCopyDeviceIdList(copyDeviceIdList);
                        adapter.setHasQrCodeSpids(hasQrCodeSpids);
                        adapter.setDefectMap(defectmap);
                        adapter.setCopyDeviceMap(copyedMap);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

        });
    }

    List<DbModel> deviceList = new ArrayList<>();

    private void searChData(String keyWord) {
        deviceList.clear();
        data.clear();
        adapter.notifyDataSetChanged();
        adapter.setKeyWord(keyWord);
        ExecutorManager.executeTaskSerially(() -> {
            if (spacingLastly == null && isFirstLoad) {
                //查询之前巡视的间隔点
                spacingLastly = SpacingLastlyService.getInstance().findSpacingLastly(currentAcounts, currentReportId, currentFunctionModel);
            }
            getSpacingLastly();

            deviceList = DeviceService.getInstance().findAllDevice(currentBdzId, keyWord, currentFunctionModel, currentInspectionType, currentReportId, specialMenu.deviceWay);
            hasQrCodeSpids = new HashSet<>();
            LinkedHashMap<String, List<DbModel>> spacingDeviceMap = new LinkedHashMap<>();
            // 转换treeNode
            if (null != deviceList && !deviceList.isEmpty()) {
                for (DbModel dbModel : deviceList) {
                    String spacingId = dbModel.getString("spid");
                    if (!spacingDeviceMap.keySet().contains(spacingId)) {
                        List<DbModel> treeNodeList = new ArrayList<>();
                        treeNodeList.add(dbModel);
                        spacingDeviceMap.put(spacingId, treeNodeList);
                    } else {
                        spacingDeviceMap.get(spacingId).add(dbModel);
                    }
                    if (!TextUtils.isEmpty(dbModel.getString("qrcode"))) {
                        hasQrCodeSpids.add(dbModel.getString("spid"));
                    }
                }
                boolean isEmptyKey = TextUtils.isEmpty(keyWord);
                List<DbModel> sortList = new ArrayList<>(isEmptyKey ? deviceList.size() : 0);
                for (String key : spacingDeviceMap.keySet()) {
                    SpaceItem parentNode = new SpaceItem(spacingDeviceMap.get(key).get(0));
                    parentNode.addAll(spacingDeviceMap.get(key));
                    data.add(parentNode);
                    if (isEmptyKey) {
                        sortList.addAll(spacingDeviceMap.get(key));
                    }
                }
                if (isEmptyKey) {
                    NextDeviceUtils.getInstance().put(currentFunctionModel, sortList);
                }
                if ("second".equals(currentFunctionModel)) {
                    DeviceHandleFunctions.buildSpaceTreeData(data, spaceGroupMap);
                }
            }
            if (!data.isEmpty()) {
                getActivity().runOnUiThread(() -> {
                    if (!TextUtils.isEmpty(keyWord)) {
                        adapter.setShowOnly(false);
                        adapter.expandAll();
                        adapter.notifyDataSetChanged();
                    } else {
                        if (spacingLastly != null && !qwerKeyBoardUtils.isCharMode()) {
                            int[] index = DeviceHandleFunctions.findSpaceIndex(spacingLastly.spid, data);
                            if (-1 != index[0]) {
                                adapter.expand(index[0]);
                            }
                            if (-1 != index[1]) {
                                adapter.expand(index[1]);
                                recyclerView.scrollToPosition(index[1]);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    queryInfo();
                });
            }
        });
    }


    private void initSpacingGroup() {
        ExecutorManager.executeTaskSerially(() -> {
            if ("second".equals(currentFunctionModel) && spaceGroupMap == null) {
                List<SpacingGroup> spacingGroups = SpacingGroupService.getInstance().findSpacingGroup(currentBdzId);
                spaceGroupMap = new LinkedHashMap<>();
                for (SpacingGroup spacingGroup : spacingGroups) {
                    spaceGroupMap.put(spacingGroup.id, new SpaceGroupItem(spacingGroup));
                }
                spaceGroupMap.put(null, new SpaceGroupItem(new SpacingGroup("未拆分小室")));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                // 摇一摇选定间隔刷新
                case Config.SHAKE_SPACE:
                    String spaceId = data.getStringExtra("spacingId");
                    DeviceHandleFunctions.animationMethodSpace(spaceId, this.data, adapter, mHandler, recyclerView);
                    break;
                default:
                    break;
            }
        }
    }

    public void locationSuccess(BDLocation location) {
        try {
            arriveCheckHelper.checkArrived(location);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public SpacingLastly getSpacingLastly() {
        if (adapter == null) {
            return null;
        }
        DbModel spacing = adapter.getLastlySpace();
        if (spacing == null) {
            return null;
        }
        String spaceId = spacing.getString("spid");
        if (null == spacingLastly) {
            spacingLastly = new SpacingLastly(currentReportId, currentAcounts, spaceId, currentFunctionModel);
        } else {
            spacingLastly.spid = spaceId;
        }
        return spacingLastly;
    }

    public void handleSpaceArrivedData() {
        arriveCheckHelper.handleSpaceArrivedData();
    }

    @Override
    public void onChange(View v, String oldKey, String newKey) {
        searChData(newKey);
    }
}
