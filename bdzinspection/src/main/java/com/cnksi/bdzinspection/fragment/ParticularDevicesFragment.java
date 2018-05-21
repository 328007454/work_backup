package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity;
import com.cnksi.bdzinspection.activity.SingleSpaceCopyActivity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.daoservice.DeviceService.DefectInfo;
import com.cnksi.bdzinspection.daoservice.SpacingService;
import com.cnksi.bdzinspection.daoservice.SpecialMenuService;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.model.SpacingGroup;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.bdzinspection.model.tree.SpaceGroupItem;
import com.cnksi.bdzinspection.model.tree.SpaceItem;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.NextDeviceUtils;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.view.keyboard.QWERKeyBoardUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 2017/2/27.
 */

public class ParticularDevicesFragment extends BaseFragment implements QWERKeyBoardUtils.keyWordChangeListener {


    SpecialMenu specialMenu;
    private ViewHolder rootHolder;
    private List<MultiItemEntity> data;
    private DeviceAdapter adapter;
    private RecyclerView recyclerView;

    private ArriveCheckHelper arriveCheckHelper;
    private QWERKeyBoardUtils qwerKeyBoardUtils;
    private SpacingLastly spacingLastly;
    private LinkedHashMap<String, SpaceGroupItem> spaceGroupMap;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;
        rootHolder = new ViewHolder(currentActivity, container, R.layout.xs_fragment_expadable_list_1, false);
        initUI();
        initSpacingGroup();
        lazyLoad();
        return rootHolder.getRootView();
    }

    private void initUI() {
        getBundleValue();
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.init(rootHolder.getView(R.id.ll_root_container), this);
        data = new ArrayList<>();
        adapter = new DeviceAdapter(currentActivity, data);
        adapter.setCurrentFunctionMode(currentFunctionModel);
        adapter.setCurrentInspectionType(currentInspectionType);
        adapter.setCurrentInspection(currentInspectionType);
        // 跳转设备详情
        adapter.setDeviceClickListener((v, dbModel, position) -> {
            Intent intent = new Intent(currentActivity, NewDeviceDetailsActivity.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString("deviceId"));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString("deviceName"));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString("spacingName"));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            startActivity(intent);
        });
        // 跳转设备抄录
        adapter.setCopyClickListener((v, dbModel, position) -> {
            Intent intent = new Intent(currentActivity, CopyValueActivity2.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString("deviceId"));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString("deviceName"));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString("spacingName"));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            intent.putExtra(Config.TITLE_NAME, dbModel.getString("deviceName"));
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            startActivity(intent);
        });
        // 设置间隔点击事件
        adapter.setGroupItemClickListener((v, dbModel, position) -> {
            if (v.getId() == R.id.ibt_copy_pen) {
                PlaySound.getIntance(currentActivity).play(R.raw.input);
                Intent intent = new Intent(currentActivity, SingleSpaceCopyActivity.class);
                intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
                intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString("spacingName"));
                intent.putExtra(Config.CURRENT_FUNCTION_MODEL, currentFunctionModel);
                startActivity(intent);
            }
        });
        recyclerView = rootHolder.getView(R.id.elv_container);
        final GridLayoutManager manager = new GridLayoutManager(currentActivity, "second".equals(currentFunctionModel) ? 2 : 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == DeviceAdapter.DEVICE_ITEM ? 1 : manager.getSpanCount();
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
            queryInfo();
            isFirstLoad = false;
        }

    }

    private void searchCurrentDeviceType() {
        specialMenu = SpecialMenuService.getInstance().findCurrentDeviceType(currentInspectionType);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            queryInfo();
            if (null != arriveCheckHelper)
                arriveCheckHelper.refreshArrived();
        }

    }
    private HashSet<String> copyDeviceIdList = new HashSet<>();// 当前变电站下所有抄录设备
    Map<String, List<String>> spaceCopyDeviceMap = new HashMap<>();
    private void queryInfo() {
        mFixedThreadPoolExecutor.execute(() ->{
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
                    if (!copyDeviceIdList.contains(deviceSpID))
                        copyDeviceIdList.add(deviceSpID);
                    if (spaceCopyDeviceMap.keySet().contains(deviceSpID)) {
                        if (null != spaceCopyDeviceMap.get(deviceSpID))
                            spaceCopyDeviceMap.get(deviceSpID).add(deviceID);
                    } else {
                        List<String> deviceIDs = new ArrayList<>();
                        deviceIDs.add(deviceID);
                        spaceCopyDeviceMap.put(deviceSpID, deviceIDs);
                    }
                }
            }

            getActivity().runOnUiThread(() -> {
                adapter.setCopyDeviceIdList(copyDeviceIdList);
                adapter.setDefectMap(defectmap);
                adapter.setCopyDeviceMap(copyedMap);
                adapter.notifyDataSetChanged();
            });

        });


    }


    private void searChData(String keyWord) {
        List<DbModel> deviceList;
        data.clear();
        adapter.notifyDataSetChanged();
        adapter.setKeyWord(keyWord);
        if (spacingLastly == null && isFirstLoad) {
            //查询之前巡视的间隔点
            spacingLastly = SpacingService.getInstance().findSpacingLastly(currentAcounts, currentReportId, currentFunctionModel);
        }
        getSpacingLastly();
        if ("select_device".equalsIgnoreCase(specialMenu.deviceWay)) {
            // 特巡设备
            deviceList = DeviceService.getInstance().findAllParticularDevice(currentBdzId, keyWord, currentFunctionModel, currentReportId, specialMenu.deviceWay);
        } else
            // 查询设备及设备所在间隔
            deviceList = DeviceService.getInstance().findAllDevice(currentBdzId, keyWord, currentFunctionModel, currentInspectionType, specialMenu.deviceWay);
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
            }
            boolean isEmptyKey = TextUtils.isEmpty(keyWord);
            List<DbModel> sortList = new ArrayList<>(isEmptyKey ? deviceList.size() : 0);
            for (String key : spacingDeviceMap.keySet()) {
                SpaceItem parentNode = new SpaceItem(spacingDeviceMap.get(key).get(0));
                parentNode.addAll(spacingDeviceMap.get(key));
                data.add(parentNode);
                if (isEmptyKey) sortList.addAll(spacingDeviceMap.get(key));
            }
            if (isEmptyKey) NextDeviceUtils.getInstance().put(currentFunctionModel, sortList);
            if ("second".equals(currentFunctionModel)) {
                Functions.buildTreeData(data, spaceGroupMap);
            }
        }
        if (!data.isEmpty()) {
            if (!TextUtils.isEmpty(keyWord)) {
                adapter.setShowOnly(false);
                adapter.expandAll();
            } else {
                if (spacingLastly != null && !qwerKeyBoardUtils.isCharMode()) {
                    int index[] = Functions.findSpaceIndex(spacingLastly.spid, data);
                    if (-1 != index[0]) adapter.expand(index[0]);
                    if (-1 != index[1]) {
                        adapter.expand(index[1]);
                        recyclerView.scrollToPosition(index[1]);
                    } else adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }


    private void initSpacingGroup() {
        if ("second".equals(currentFunctionModel) && spaceGroupMap == null) {
            List<SpacingGroup> spacingGroups = SpacingService.getInstance().findSpacingGroup(currentBdzId);
            spaceGroupMap = new LinkedHashMap<>();
            for (SpacingGroup spacingGroup : spacingGroups) {
                spaceGroupMap.put(spacingGroup.id, new SpaceGroupItem(spacingGroup));
            }
            spaceGroupMap.put(null, new SpaceGroupItem(new SpacingGroup("未拆分小室")));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                // 摇一摇选定间隔刷新
                case Config.SHAKE_SPACE:
                    String spaceId = data.getStringExtra("spacingId");
                    Functions.animationMethodSpace(spaceId, this.data, adapter, mHandler,recyclerView);
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
        if (adapter == null) return null;
        DbModel spacing = adapter.getLastlySpace();
        if (spacing == null) return null;
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
