package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.CopyValueActivity2;
import com.cnksi.bdzinspection.activity.SingleSpaceCopyActivity;
import com.cnksi.bdzinspection.activity.xian.XNewDeviceDetailsActivity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.daoservice.ReportSnwsdService;
import com.cnksi.bdzinspection.daoservice.SpacingLastlyService;
import com.cnksi.bdzinspection.databinding.XsDialogCopySnwsdBinding;
import com.cnksi.bdzinspection.model.ReportSnwsd;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.other.ReQuestLocationDialog;
import com.cnksi.bdzinspection.utils.NextDeviceUtils;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingGroupService;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.DefectInfo;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.QWERKeyBoardUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.GPSUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;


/**
 * 设备列表fragment
 *
 * @author lyndon
 */
public class DeviceListFragment extends BaseFragment implements QWERKeyBoardUtils.keyWordChangeListener {
    public static final String TAG = "DeviceListFragment";
    public final static int SORT_SPACING = 0x100;
    public final static int RFID = SORT_SPACING + 1;
    Map<String, List<String>> spaceCopyDeviceMap = new HashMap<>();
    LinkedHashMap<String, SpaceGroupItem> spaceGroupMap;
    private ViewHolder rootHolder;
    private List<MultiItemEntity> data;
    private DeviceAdapter adapter;
    private RecyclerView recyclerView;
    private HashSet<String> copyDeviceIdList = new HashSet<>();
    private DbModel locationSpace;
    private DbModel locationDevice;
    private HashSet<String> spacingIds = new HashSet<>();

    //设备id与对应的DbModel
    private HashMap<String, DbModel> deviceDbModelMap = new HashMap<>();
    private ArriveCheckHelper arriveCheckHelper;
    private QWERKeyBoardUtils qwerKeyBoardUtils;
    private SpacingLastly spacingLastly;
    private HashSet<String> hasQrCodeSpids;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isFirstLoad = true;
        rootHolder = new ViewHolder(currentActivity, container, R.layout.common_recycler_view, false);
        long startTime = System.currentTimeMillis();
        initialUI();
        initSpacingGroup();
        lazyLoad();
        Log.d(TAG, "onCreateView: " + (System.currentTimeMillis() - startTime));
        return rootHolder.getRootView();
    }


    private void initialUI() {
        getBundleValue();
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.initOtherSerchView(rootHolder.getView(R.id.ll_root_container), this);
        data = new ArrayList<>();
        adapter = new DeviceAdapter(currentActivity, data);
        adapter.setCurrentFunctionMode(currentFunctionModel);
        adapter.setCurrentInspectionType(currentInspectionType);
        // 设置间隔点击事件
        adapter.setGroupItemClickListener((View v, DbModel dbModel, int position) -> {
            if (v.getId() == R.id.ibt_copy_pen) {
                PlaySound.getIntance(currentActivity).play(R.raw.input);
                Intent intent = new Intent(currentActivity, SingleSpaceCopyActivity.class);
                intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
                intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
                intent.putExtra(Config.CURRENT_FUNCTION_MODEL, currentFunctionModel);
                startActivity(intent);
            } else if (v.getId() == R.id.iv_haslocationed) {
                if (v.getTag() != null) {
                    final ReQuestLocationDialog dialog = ReQuestLocationDialog.getInstance();
                    dialog.setItemClickListener((v1, data, position1) -> {
                        if (v1.getId() == R.id.bt_photo_location) {
                            String photoName = (String) data;
                            arriveCheckHelper.saveLocation(dbModel, null, true, photoName);
                            ToastUtils.showMessage("保存成功");
                        } else if (v1.getId() == R.id.bt_request_location) {
                            BDLocation location = (BDLocation) data;
                            arriveCheckHelper.saveLocation(dbModel, location, true);
                            ToastUtils.showMessage("保存成功");
                        }

                    });
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    dialog.show(fragmentTransaction, "dialog");
                }
            }else if (v.getId() == R.id.txt_defect_count) {
                Intent intent = new Intent(mActivity, com.cnksi.defect.activity.DefectControlActivity.class);
                String spid = dbModel.getString("spid");
                intent.putExtra(Config.CURRENT_SPACING_ID, spid);
                startActivity(intent);
            }

        });
        // 设置间隔长按事件,间隔定位
        adapter.setGroupItemLongClickListener((v, item, position) -> {
            locationSpace = item;
            requestLocation(true);
        });
        // 设置设备长按事件,定位
        adapter.setDeviceItemLongClickListener((v, item, position) -> {
            locationDevice = item;
            requestLocation(false);
        });
        // 跳转设备详情
        adapter.setDeviceClickListener((v, dbModel, position) -> {
            NextDeviceUtils.getInstance().setLastIndex(-1);
//            Intent intent = new Intent(currentActivity, NewDeviceDetailsActivity.class);
            Intent intent = new Intent(currentActivity, XNewDeviceDetailsActivity.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString(DeviceService.DEVICE_ID_KEY));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            startActivity(intent);
        });
        // 跳转设备抄录
        adapter.setCopyClickListener((v, dbModel, position) -> {
            Intent intent = new Intent(currentActivity, CopyValueActivity2.class);
            intent.putExtra(Config.CURRENT_DEVICE_ID, dbModel.getString(DeviceService.DEVICE_ID_KEY));
            intent.putExtra(Config.CURRENT_DEVICE_NAME, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            intent.putExtra(Config.CURRENT_SPACING_ID, dbModel.getString(Spacing.SPID));
            intent.putExtra(Config.CURRENT_SPACING_NAME, dbModel.getString(DeviceService.SPACING_NAME_KEY));
            intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
            intent.putExtra(Device.DTID, dbModel.getString("dtid"));
            intent.putExtra(Config.TITLE_NAME_KEY, dbModel.getString(DeviceService.DEVICE_NAME_KEY));
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            startActivity(intent);
        });
        adapter.setGroupItemListener((v, data, position) -> showCopyWSDDialog(data, position));
        recyclerView = rootHolder.getView(R.id.elv_container);
        final GridLayoutManager manager = new GridLayoutManager(currentActivity, "second".equals(currentFunctionModel) ? 2 : 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == DEVICE_ITEM ? 1 : manager.getSpanCount();
            }
        });
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(manager);
        isPrepared = true;
        arriveCheckHelper = new ArriveCheckHelper(currentActivity, adapter, currentReportId, currentInspectionType, currentBdzId, currentFunctionModel);

    }

    private void requestLocation(final boolean isSpace) {
        if (!GPSUtils.isOPen(currentActivity)) {
            ToastUtils.showMessage("请打开GPS再进行定位！");
        } else {
            CustomerDialog.showProgress(currentActivity, R.string.xs_locating_str);
            LocationUtil.getInstance().getLocalHelper(new LocationListener() {
                @Override
                public void locationSuccess(BDLocation location) {
                    CustomerDialog.dismissProgress();
                    showLocationInfoDialog(location, isSpace);
                }

                @Override
                public void locationFailure(int code, String message) {
                    if (code == LocationListener.ERROR_TIMEOUT) {
                        CustomerDialog.dismissProgress();
                        ToastUtils.showMessage("请求定位超时,请确保打开GPS。");
                    }
                }
            }).setTimeout(10).start();
        }
    }


    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            searChData("");
            queryInfo();
        }
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

    private void queryInfo() {
        ExecutorManager.executeTask(() -> {
            // 查寻缺陷
            final HashMap<String, DefectInfo> defectmap = DeviceService.getInstance().findDeviceDefect(currentBdzId);
            // 查寻抄录数量
            final Map<String, List<String>> copyedMap = DefectRecordService.getInstance().getCopyDevice(currentReportId, currentBdzId, currentFunctionModel, currentInspectionType);
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

            mHandler.post(() -> {
                isFirstLoad = false;
                adapter.setCopyDeviceIdList(copyDeviceIdList);
                adapter.setDefectMap(defectmap);
                adapter.setCopyDeviceMap(copyedMap);
                adapter.notifyDataSetChanged();
            });
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

    private void searChData(String keyWord) {
        data.clear();
        adapter.notifyDataSetChanged();
        adapter.setKeyWord(keyWord);
        adapter.setCurrentInspection(currentInspectionType);
        ExecutorManager.executeTaskSerially(() -> {


            if (spacingLastly == null) {
                spacingLastly = SpacingLastlyService.getInstance().findSpacingLastly(currentAcounts, currentReportId, currentFunctionModel);
            }
            getSpaceLastly();
            // 查询设备及设备所在间隔
            List<DbModel> deviceList = DeviceService.getInstance().findAllDeviceByName(currentBdzId, keyWord, currentFunctionModel, currentInspectionType, currentReportId, "");
            LinkedHashMap<String, List<DbModel>> spacingDeviceMap = new LinkedHashMap<>();
            hasQrCodeSpids = new HashSet<>();
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
                    if (!spacingIds.contains(spacingId)) {
                        spacingIds.add(spacingId);
                    }
                    deviceDbModelMap.put(dbModel.getString(DeviceService.DEVICE_ID_KEY), dbModel);
                    if (!TextUtils.isEmpty(dbModel.getString("qrcode"))) {
                        hasQrCodeSpids.add(dbModel.getString("spid"));
                    }
                }
                boolean isEmptyKey = TextUtils.isEmpty(keyWord);
                List<DbModel> sortList = new ArrayList<>(isEmptyKey ? deviceList.size() : 0);
                for (Map.Entry<String, List<DbModel>> entry : spacingDeviceMap.entrySet()) {
                    SpaceItem parentNode = new SpaceItem(entry.getValue().get(0));
                    parentNode.addAll(entry.getValue());
                    data.add(parentNode);
                    if (isEmptyKey) {
                        sortList.addAll(entry.getValue());
                    }
                }
                if (isEmptyKey) {
                    NextDeviceUtils.getInstance().put(currentFunctionModel, sortList);
                }
                if ("second".equals(currentFunctionModel)) {
                    DeviceHandleFunctions.buildSpaceTreeData(data, spaceGroupMap);
                    reportSnwsds = ReportSnwsd.getAllCopySNWSD(currentReportId);
                    adapter.setCopySNWSD(reportSnwsds);
                }
            }
            if (!data.isEmpty()) {
                getActivity().runOnUiThread(() -> {
                    adapter.setHasQrCodeSpids(hasQrCodeSpids);
                    if (!TextUtils.isEmpty(keyWord)) {
                        adapter.setShowOnly(false);
                        adapter.expandAll();
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.setShowOnly(true);
                        if (!qwerKeyBoardUtils.isCharMode() && spacingLastly != null) {
                            int[] index = DeviceHandleFunctions.findSpaceIndex(spacingLastly.spid, data);
                            if (-1 != index[0]) {
                                adapter.expand(index[0]);
                            }
                            if (-1 != index[1]) {
                                adapter.expand(index[1]);
                                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(index[1], 0);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                // 间隔排序回调，重新加载一次数据即可
                case SORT_SPACING:
                    isFirstLoad = true;
                    lazyLoad();
                    break;
                // 摇一摇选定间隔刷新
                case Config.SHAKE_SPACE:
                    String spaceId = data.getStringExtra("spacingId");
                    animationMethodSpace(spaceId);
                    break;
                case RFID:
                    String rfId = data.getStringExtra("spacingId");
                    if (spacingIds.contains(rfId)) {
                        animationMethodSpace(rfId);
                    } else if (deviceDbModelMap.containsKey(rfId)) {
                        animationDeviceMethod(rfId);
                    }
                default:
                    break;
            }
        }
    }

    public void animationMethodSpace(String spid) {
        DeviceHandleFunctions.animationMethodSpace(spid, data, adapter, mHandler, recyclerView);
    }

    public void animationDeviceMethod(String rfId) {
        DbModel model = deviceDbModelMap.get(rfId);
        DeviceHandleFunctions.animationDeviceMethod(model, data, adapter, mHandler);
    }

    public void locationSuccess(BDLocation location) {
        try {
            arriveCheckHelper.checkArrived(location);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 显示定位信息Dialog
     */
    private void showLocationInfoDialog(final BDLocation location, final boolean isSpace) {
        final Dialog dialog = new Dialog(currentActivity, R.style.dialog);
        ViewHolder holder = new ViewHolder(currentActivity, null, R.layout.xs_dialog_location_info, false);
        AutoUtils.autoSize(holder.getRootView());
        holder.setText(R.id.tv_dialog_title, getString(R.string.xs_location_infor_str));
        // 设置间隔或者设备名称

        String content;
        if (isSpace) {
            content = getString(R.string.xs_spacing_name_format_str, locationSpace.getString(DeviceService.SPACING_NAME_KEY));
        } else {
            content = getString(R.string.xs_device_name_format_str, locationDevice.getString(DeviceService.DEVICE_NAME_KEY));
        }
        TextView txtSpaceName = holder.getView(R.id.tv_spacing_name);
        txtSpaceName.setText(StringUtils.changePartTextColor(currentActivity, content, R.color.xs_global_base_color, 5, content.length()));
        // 设置gps信息
        content = getString(R.string.xs_gps_info_format_str, String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
        TextView txtGpsInfo = holder.getView(R.id.tv_gps_info);
        txtGpsInfo.setText(StringUtils.changePartTextColor(currentActivity, content, R.color.xs_global_base_color, 6, content.length()));
        // 设置精度信息
        content = getString(R.string.xs_location_radius_format_str, String.valueOf(location.getRadius()));
        TextView txtRadius = holder.getView(R.id.tv_location_radius);
        txtRadius.setText(StringUtils.changePartTextColor(currentActivity, content, R.color.xs_global_base_color, 5, content.length()));
        // 设置卫星个数
        content = getString(R.string.xs_satellite_number_format_str, String.valueOf(location.getSatelliteNumber() <= 0 ? 0 : location.getSatelliteNumber()));
        TextView txtSatelliteNumber = holder.getView(R.id.tv_satellite_number);
        txtSatelliteNumber.setText(StringUtils.changePartTextColor(currentActivity, content, R.color.xs_global_base_color, 5, content.length()));

        OnClickListener dialogOnClick = v -> {
            if (R.id.btn_confirm == v.getId()) {
                arriveCheckHelper.saveLocation(isSpace ? locationSpace : locationDevice, location, isSpace);
            }
            dialog.dismiss();
        };
        holder.getView(R.id.btn_cancel).setOnClickListener(dialogOnClick);
        holder.getView(R.id.btn_confirm).setOnClickListener(dialogOnClick);

        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        dialog.setContentView(holder.getRootView(), new ViewGroup.LayoutParams(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
    }


    public SpacingLastly getSpaceLastly() {
        if (adapter == null) {
            return null;
        }
        DbModel model = adapter.getLastlySpace();
        if (model == null) {
            return null;
        }
        String spaceId = model.getString("spid");
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

    XsDialogCopySnwsdBinding mSnwsdBinding;
    Dialog dialogCopySNWSD;
    HashSet<ReportSnwsd> reportSnwsds = new HashSet<ReportSnwsd>();
    boolean update = false;
    ReportSnwsd snwsd = null;

    private void showCopyWSDDialog(SpacingGroup data, int position) {
        if (null == mSnwsdBinding) {
            mSnwsdBinding = XsDialogCopySnwsdBinding.inflate(LayoutInflater.from(currentActivity));
        }
        if (null == dialogCopySNWSD) {
            dialogCopySNWSD = DialogUtils.createDialog(currentActivity, mSnwsdBinding.getRoot(), ScreenUtils.getScreenWidth(currentActivity) * 7 / 9, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }
        for (ReportSnwsd snwsds : reportSnwsds) {
            if (data.id.equalsIgnoreCase(snwsds.groupID)) {
                update = true;
                snwsd = snwsds;
                break;
            } else {
                snwsd = null;
            }
        }
        mSnwsdBinding.tvDialogTitle.setText(TextUtils.isEmpty(data.name) ? "" : data.name);
        if (snwsd != null) {
            mSnwsdBinding.editTemp.setText(TextUtils.isEmpty(snwsd.wd) ? "" : snwsd.wd);
            mSnwsdBinding.editSd.setText(TextUtils.isEmpty(snwsd.sd) ? "" : snwsd.sd);
        } else {
            mSnwsdBinding.editSd.setText("");
            mSnwsdBinding.editTemp.setText("");
        }
        dialogCopySNWSD.show();
        mSnwsdBinding.btnCancel.setOnClickListener(view -> dialogCopySNWSD.dismiss());
        mSnwsdBinding.btnSure.setOnClickListener(view -> {
            String wd = mSnwsdBinding.editTemp.getText().toString();
            String sd = mSnwsdBinding.editSd.getText().toString();
            try {
                if (update && null != snwsd) {
                    snwsd.updateCopyValue(wd, sd);
                } else {
                    snwsd = new ReportSnwsd(currentReportId, currentBdzId, currentBdzName, data, wd, sd);
                }
                if (!update && !snwsd.judgeValueNormal(wd, sd)) {
                    ToastUtils.showMessage("温度：-99.9-99.9；湿度：0-100");
                    return;
                }
                ReportSnwsdService.getInstance().saveOrUpdate(snwsd);
                reportSnwsds.add(snwsd);
                dialogCopySNWSD.dismiss();
                mSnwsdBinding.editTemp.setText("");
                mSnwsdBinding.editSd.setText("");
                adapter.setCopySNWSD(reportSnwsds);
                adapter.notifyDataSetChanged();
            } catch (DbException e) {
                e.printStackTrace();
                dialogCopySNWSD.dismiss();
            }
        });

    }


}