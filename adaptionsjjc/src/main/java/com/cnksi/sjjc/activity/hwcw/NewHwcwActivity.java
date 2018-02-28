package com.cnksi.sjjc.activity.hwcw;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.core.utils.CToast;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.BaseRecyclerDataBindingAdapter;
import com.cnksi.sjjc.adapter.hwcw.HwcwNewHotPartAdapter;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ActivityHwcwNewBinding;
import com.cnksi.sjjc.databinding.ItemHotDeviceHwcwBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.NewHwcwService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.GsonUtil;
import com.google.gson.Gson;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备测温主界面
 *
 * @author kkk on 2017/12/7.
 */

public class NewHwcwActivity extends BaseActivity implements BaseRecyclerDataBindingAdapter.OnItemClickListener, HwcwNewHotPartAdapter.OnLongItemListener {
    private ActivityHwcwNewBinding mHwcwNewBinding;
    private int realHeight;
    private int childItemNum;
    private HwcwNewHotPartAdapter mHotPartAdapter;
    private List<HwcwLocation> hotLocations = new ArrayList<>();
    private HwcwBaseInfo mHwcwBaseInfo;
    public static final int TO_NEWINFORACTIVITY = 0x111;
    private List<String> selecteDevices = new ArrayList<>();

    /**
     * 当前选择设备
     */
    private DbModel currentDevice;
    private String currentDeviceID;
    private String currentSpaceID;
    private HwcwLocation currentLocatoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHwcwNewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_hwcw_new, null, false);
        setChildView(mHwcwNewBinding.getRoot());
        getIntentValue();
        initData();
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mHwcwBaseInfo = NewHwcwService.getInstance().getBaseInfo(currentReportId);
                if (!TextUtils.isEmpty(mHwcwBaseInfo.id)) {
                    hotLocations = NewHwcwService.getInstance().getAllLocation(mHwcwBaseInfo.id);
                    for (HwcwLocation location : hotLocations) {
                        selecteDevices.add(location.deviceID);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUI();
                    }
                });
            }
        });

    }

    private void initUI() {
        tvTitle.setText(currentBdzName + currentInspectionName + "记录");
        mHotPartAdapter = new HwcwNewHotPartAdapter(mHwcwNewBinding.rlHotrecord, hotLocations, R.layout.item_hwcw_hot_part);
        mHwcwNewBinding.rlHotrecord.setLayoutManager(new LinearLayoutManager(_this));
        mHwcwNewBinding.rlHotrecord.setAdapter(mHotPartAdapter);
        mHotPartAdapter.setOnItemClickListener(this);
        mHotPartAdapter.setOnLongItemClickListener(this);
        mHwcwNewBinding.setBaseInfo(mHwcwBaseInfo);
        if ("精确测温".equals(mHwcwBaseInfo.type)) {
            mHwcwNewBinding.cbJingque.setChecked(true);
        } else {
            mHwcwNewBinding.cbPuce.setChecked(true);
        }
        if ("否".equalsIgnoreCase(mHwcwBaseInfo.isAllBdz)) {
            mHwcwNewBinding.cbAllNo.setChecked(true);
        } else {
            mHwcwNewBinding.cbAllYes.setChecked(true);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                _this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_hotdeivce_name:
                Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
                intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.one);
                intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
                startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE);
                break;
            case R.id.btn_cancel:
                String deviceName = mHwcwNewBinding.etHotdeivceName.getText().toString();
                if (!TextUtils.isEmpty(deviceName)) {
                    DialogUtils.createTipsDialog(_this, getString(R.string.have_not_save_data), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHwcwNewBinding.setSetNull(false);
                            mHwcwNewBinding.llHotPart.removeAllViews();
                            hotRecordClickPositon = -1;
                            childItemNum = 0;
                            mHotPartAdapter.setCurrentClickPosition(true, hotRecordClickPositon);
                        }
                    }, false).show();
                }
                break;
            case R.id.btn_confirm:
                saveNewLocation();
                break;
            case R.id.btn_confirm_save:
                if (saveData()) {
                    Intent intent = new Intent(_this, NewHwcwInforActivity.class);
                    startActivityForResult(intent, TO_NEWINFORACTIVITY);
                } else {
                    CToast.showShort(_this, "请完成所有的基本信息填写！");
                }
                break;
            case R.id.txt_hot_part:
                initHotPartItem();
                break;
            case R.id.aib_delete_hotpart:
                int num = (int) view.getTag();
                mHwcwNewBinding.llHotPart.removeViewAt(num);
                childItemNum--;
                for (int i = 0; i < mHwcwNewBinding.llHotPart.getChildCount(); i++) {
                    ItemHotDeviceHwcwBinding deviceHwcwBinding = (ItemHotDeviceHwcwBinding) mHwcwNewBinding.llHotPart.getChildAt(i).getTag();
                    int tagNum = (int) deviceHwcwBinding.aibDeleteHotpart.getTag();
                    if (tagNum > num) {
                        deviceHwcwBinding.aibDeleteHotpart.setTag(tagNum - 1);
                    }
//                    if (i == childItemNum - 1) {
//                        deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.VISIBLE);
//                    } else {
//                        deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.INVISIBLE);
//                    }
                }
                break;
            case R.id.txt_baseinfo:
                if (mHwcwNewBinding.containerBaseinfo.getVisibility() == View.VISIBLE) {
                    mHwcwNewBinding.txtBaseinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, _this.getResources().getDrawable(R.mipmap.icon_down), null);
                    mHwcwNewBinding.containerBaseinfo.setVisibility(View.GONE);
                } else {
                    mHwcwNewBinding.txtBaseinfo.setCompoundDrawablesWithIntrinsicBounds(null, null, _this.getResources().getDrawable(R.mipmap.icon_up), null);
                    mHwcwNewBinding.containerBaseinfo.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void initHotPartItem() {
        ItemHotDeviceHwcwBinding itemHotDeviceHwcwBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hot_device_hwcw, null, false);
        itemHotDeviceHwcwBinding.aibDeleteHotpart.setTag(childItemNum);
        itemHotDeviceHwcwBinding.getRoot().setTag(itemHotDeviceHwcwBinding);
        mHwcwNewBinding.llHotPart.addView(itemHotDeviceHwcwBinding.getRoot());
//        for (int i = 0; i < mHwcwNewBinding.llHotPart.getChildCount(); i++) {
//            ItemHotDeviceHwcwBinding deviceHwcwBinding = (ItemHotDeviceHwcwBinding) mHwcwNewBinding.llHotPart.getChildAt(i).getTag();
//            if (i == childItemNum) {
//                deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.VISIBLE);
//            } else {
//                deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.INVISIBLE);
//            }
//        }

        childItemNum++;
    }


    private void saveNewLocation() {
        String json = "";
        List<HashMap<String, String>> deviceHotList = new ArrayList<>();
        String deviceName = mHwcwNewBinding.etHotdeivceName.getText().toString().trim();
        String spaceName = mHwcwNewBinding.txtSpaceName.getText().toString().trim();
        String ratedCurrent = mHwcwNewBinding.etElectricity.getText().toString().trim();
        String loadCurrent = mHwcwNewBinding.etLoadElectricity.getText().toString().trim();
        if (TextUtils.isEmpty(deviceName)) {
            CToast.showShort(_this, "请先选择设备");
            return;
        }
        for (int i = 0; i < mHwcwNewBinding.llHotPart.getChildCount(); i++) {
            ItemHotDeviceHwcwBinding itemHotDeviceHwcwBinding = (ItemHotDeviceHwcwBinding) mHwcwNewBinding.llHotPart.getChildAt(i).getTag();
            String hotPart = itemHotDeviceHwcwBinding.etHotPartName.getText().toString();
            String hotTemp = itemHotDeviceHwcwBinding.etHotPartTemp.getText().toString();
            if (TextUtils.isEmpty(hotPart) || TextUtils.isEmpty(hotTemp)) {
                CToast.showShort(_this, "请填写发热部位和发热部位温度");
                return;
            }
            if (!TextUtils.isEmpty(hotPart) || !TextUtils.isEmpty(hotTemp)) {
                HashMap<String, String> hotMap = new HashMap<>();
                hotMap.put("bw_name", TextUtils.isEmpty(hotPart) ? "" : hotPart);
                hotMap.put("wd", TextUtils.isEmpty(hotTemp) ? "" : hotTemp);
                deviceHotList.add(hotMap);
            }
        }
        if (!deviceHotList.isEmpty()) {
            HashMap<String, List<HashMap<String, String>>> listHashMap = new HashMap<>();
            listHashMap.put("result", deviceHotList);
            json = new Gson().toJson(listHashMap);
        }
        if (currentLocatoin == null) {
            HwcwLocation location = new HwcwLocation(mHwcwBaseInfo.id, currentSpaceID, currentDeviceID, deviceName, spaceName, ratedCurrent, loadCurrent, TextUtils.isEmpty(json) ? "" : json.toString());
            hotLocations.add(location);
        } else {
            currentLocatoin.hotPart = TextUtils.isEmpty(json) ? "" : json;
            currentLocatoin.deviceName = deviceName;
            currentLocatoin.deviceID = currentDeviceID;
            currentLocatoin.spacingName = spaceName;
            currentLocatoin.spaceID = currentSpaceID;
            currentLocatoin.ratedCurrent = ratedCurrent;
            currentLocatoin.fhdl = loadCurrent;
        }
        mHwcwNewBinding.llHotPart.removeAllViews();
        mHwcwNewBinding.setSetNull(false);
        currentLocatoin = null;
        initHotPartUI();
        childItemNum = 0;
        hotRecordClickPositon = -1;
    }

    private void initHotPartUI() {
        if (!hotLocations.isEmpty()) {
            mHotPartAdapter.setCurrentClickPosition(false, 0);
            mHotPartAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.ACTIVITY_CHOSE_DEVICE:
                DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                if (model != null) {
                    currentDevice = model;
                    currentDeviceID = currentDevice.getString("deviceid");
                    currentSpaceID = currentDevice.getString("spid");
                    if (selecteDevices.contains(currentDeviceID)) {
                        DialogUtils.createTipsDialog(_this, "该设备已经被新增为发热设备，请在温度记录里点击编辑", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }, false).show();
                    } else {
                        selecteDevices.add(currentDeviceID);
                        mHwcwNewBinding.etHotdeivceName.setText(currentDevice.getString(Device.NAME));
                        mHwcwNewBinding.txtSpaceName.setText(currentDevice.getString("spaceName"));
                        initHotPartItem();
                    }
                }
                break;
            case TO_NEWINFORACTIVITY:
                mHwcwBaseInfo = NewHwcwService.getInstance().getBaseInfo(currentReportId);
                break;
            default:
                break;
        }
    }

    private int hotRecordClickPositon = -1;

    @Override
    public void onAdapterItemClick(View view, Object data, int position) {
        int size = 0;
        if (position == hotRecordClickPositon) {
            CToast.showShort(_this, "当前数据正在编辑中，请确认");
            return;
        } else if (hotRecordClickPositon != -1) {
            CToast.showShort(_this, "正在保存上一条编辑的数据");
            saveNewLocation();
        }
        hotRecordClickPositon = position;
        currentLocatoin = (HwcwLocation) data;
        mHwcwNewBinding.setSetNull(true);
        mHwcwNewBinding.setLocation(currentLocatoin);
        if (!TextUtils.isEmpty(currentLocatoin.hotPart)) {
            HwcwHotPart hotPart = (HwcwHotPart) GsonUtil.resolveJson(currentLocatoin.hotPart);
            for (HwcwHotPart.Result result : hotPart.result) {
                ItemHotDeviceHwcwBinding itemHotDeviceHwcwBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hot_device_hwcw, null, false);
                itemHotDeviceHwcwBinding.aibDeleteHotpart.setTag(childItemNum);
                itemHotDeviceHwcwBinding.getRoot().setTag(itemHotDeviceHwcwBinding);
                mHwcwNewBinding.llHotPart.addView(itemHotDeviceHwcwBinding.getRoot());
                itemHotDeviceHwcwBinding.etHotPartTemp.setText(result.wd);
                itemHotDeviceHwcwBinding.etHotPartName.setText(result.bw_name);
                childItemNum++;
            }
//            for (int i = 0; i < size; i++) {
//                childItemNum = size - 1;
//                ItemHotDeviceHwcwBinding deviceHwcwBinding = (ItemHotDeviceHwcwBinding) mHwcwNewBinding.llHotPart.getChildAt(i).getTag();
//                if (i == childItemNum) {
//                    deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.VISIBLE);
//                } else {
//                    deviceHwcwBinding.aibDeleteHotpart.setVisibility(View.INVISIBLE);
//                }
//            }
        }
        mHotPartAdapter.setCurrentClickPosition(true, position);

    }

    private HwcwLocation delteLocation;
    private List<HwcwLocation> deleteLocations = new ArrayList<>();

    @Override
    public void onLongItemClick(View v, final Object data, final int position) {
        delteLocation = (HwcwLocation) data;
        DialogUtils.createTipsDialog(_this, getString(R.string.delete_data), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delteLocation.dlt = 1;
                deleteLocations.add(delteLocation);
                hotLocations.remove(position);
                mHwcwNewBinding.setSetNull(false);
                mHwcwNewBinding.llHotPart.removeAllViews();
                childItemNum = 0;
                mHotPartAdapter.notifyDataSetChanged();
                selecteDevices.remove(position);
            }
        }, false).show();
    }

    public boolean saveData() {
        String isAllBdz = "", testType = "", temp = "", shidu = "", fengsu = "", testInstrument = "";
        if (mHwcwNewBinding.cbAllYes.isChecked())
            isAllBdz = "是";
        else {
            isAllBdz = "否";
        }
        if (mHwcwNewBinding.cbPuce.isChecked()) {
            testType = "普测";
        } else {
            testType = "精确测温";
        }
        temp = mHwcwNewBinding.etTemperature.getText().toString().trim();
        shidu = mHwcwNewBinding.etHumidity.getText().toString();
        fengsu = mHwcwNewBinding.etWind.getText().toString();
        testInstrument = mHwcwNewBinding.etTestInstrument.getText().toString();
        if (TextUtils.isEmpty(temp) || TextUtils.isEmpty(shidu) || TextUtils.isEmpty(fengsu) || TextUtils.isEmpty(testInstrument)) {
            return false;
        }
        mHwcwBaseInfo.setData(isAllBdz, testType, temp, shidu, fengsu, testInstrument, currentReportId, currentBdzId, currentBdzName);
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomApplication.getDbManager().saveOrUpdate(mHwcwBaseInfo);
                    if (!hotLocations.isEmpty()) {
                        CustomApplication.getDbManager().saveOrUpdate(hotLocations);
                    }
                    if (!deleteLocations.isEmpty()) {
                        CustomApplication.getDbManager().saveOrUpdate(deleteLocations);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }
}