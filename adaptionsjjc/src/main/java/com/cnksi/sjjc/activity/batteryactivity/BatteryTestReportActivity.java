package com.cnksi.sjjc.activity.batteryactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.daoservice.BatteryGroupService;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BatteryService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskExtendService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Battery;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.TaskExtend;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseReportActivity;
import com.cnksi.sjjc.activity.HomeActivity;
import com.cnksi.sjjc.activity.ImageDetailsActivity;
import com.cnksi.sjjc.adapter.BatteryReportImageAdapter;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.databinding.BatteryTestLayoutBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.BatteryRecordService;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by han on 2016/6/17.
 * 蓄电池检测报告
 */
public class BatteryTestReportActivity extends BaseReportActivity {
    public static final int LOAD_DATA_FAILURE = 0x1000;
    public static final int LOAD_BATTERY = LOAD_DATA_FAILURE + 1;
    public static final int LOAD_BATTERY_FAILURE = LOAD_BATTERY + 1;
    public static final int ANIMATION = LOAD_BATTERY_FAILURE + 1;
    public static final int VIBRATOR = ANIMATION + 1;
    public static final String TAG = "BatteryReport";
    private Report report;
    private TaskExtend taskExtend;
    private List<Battery> batteryGroup;
    private List<BatteryRecord> batteryRecordList;
    private Map<String, Integer> voltageMap = new HashMap<String, Integer>();
    private Map<String, Integer> resistanceMap = new HashMap<String, Integer>();
    private Map<String, Map<String, List<String>>> groupImageList = new HashMap<String, Map<String, List<String>>>();
    private BatteryGroup batteryGroups;
    private String type;
    private String typeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        /**
         * <b>NOTE :</b>设备检测CURRENT_TASK_ID不能存放在intent中传递，通过缓存传递
         */
        if (null != bundle && !TextUtils.isEmpty(bundle.getString(Config.CURRENT_TASK_ID))) {
            typeName = bundle.getString(Config.CURRENT_INSPECTION_TYPE_NAME);
            PreferencesUtils.put("typename", typeName);
            currentInspectionType = InspectionType.SBJC_10.name();
            currentBdzId = bundle.getString(Config.CURRENT_BDZ_ID);
            currentBdzName = bundle.getString(Config.CURRENT_BDZ_NAME);
            currentReportId = bundle.getString(Config.CURRENT_REPORT_ID);
            currentTaskId = bundle.getString(Config.CURRENT_TASK_ID);
            currentInspectionName = InspectionType.get(currentInspectionType).value;
            type = bundle.getString(Config.CURRENT_INSPECTION_TYPE);
            String currentFolder = bundle.getString(Config.CURRENT_FILENAME);
            PreferencesUtils.put(Config.PICTURE_PREFIX, currentFolder);
            PreferencesUtils.put(Config.CURRENT_MAINTANENCE_BATTERY, type);
            PreferencesUtils.put(Config.CURRENT_BDZ_ID, currentBdzId);
            PreferencesUtils.put(Config.CURRENT_BDZ_NAME, currentBdzName);
            PreferencesUtils.put(Config.CURRENT_REPORT_ID, currentReportId);
            PreferencesUtils.put(Config.CURRENT_TASK_ID, currentTaskId);
            PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
            PreferencesUtils.put(Config.CURRENT_INSPECTION_NAME, InspectionType.get(currentInspectionType).value);
        } else {
            type = PreferencesUtils.get(Config.CURRENT_MAINTANENCE_BATTERY, "");
        }
        mBtnBack.setOnClickListener(v -> {
            typeName = PreferencesUtils.get("typename", "");
            boolean xudianchi = typeName.contains(Config.XUDIANCHI) && (typeName.contains(Config.DIANYA) || typeName.contains(Config.NEIZU));
            if (xudianchi && !getIntent().getBooleanExtra(Config.IS_FROM_SJJC, false)) {
                PreferencesUtils.put(Config.CURRENT_MAINTANENCE_BATTERY, "");
            }
            ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);

            onBackPressed();
        });
        mBtnRight.setOnClickListener(v -> {
            if ("maintenance_xdcdyjc".equalsIgnoreCase(type)) {
                Intent intent = new Intent(BatteryTestReportActivity.this, HomeActivity.class);
                startActivity(intent);
                onBackPressed();
            } else {
                ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);
                onBackPressed();
            }
        });
        loadData();
        initOnClick();
    }


    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    private BatteryTestLayoutBinding binding;

    @Override
    public View setReportView() {
        binding = BatteryTestLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                batteryGroups = BatteryGroupService.getInstance().getAllGroup(currentReportId).get(0);
                report = ReportService.getInstance().getReportById(currentReportId);
                taskExtend = TaskExtendService.getInstance().findTaskExtendByTaskId(currentTaskId);
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(LOAD_DATA_FAILURE);
            }
        });
        ExecutorManager.executeTaskSerially(() -> {
            try {
                batteryGroup = BatteryService.getInstance().getAllBattery(currentBdzId);
                if (null != batteryGroup && !batteryGroup.isEmpty()) {
                    for (Battery battery : batteryGroup) {
                        voltageMap.put(battery.bid, 0);
                        resistanceMap.put(battery.bid, 0);

                        Map<String, List<String>> imageMap = new HashMap<String, List<String>>();
                        groupImageList.put(battery.bid, imageMap);
                    }
                }
                batteryRecordList = BatteryRecordService.getInstance().getAllBatteryRecord(currentReportId);
                if (null != batteryRecordList && !batteryRecordList.isEmpty()) {
                    for (BatteryRecord record : batteryRecordList) {
                        if (!TextUtils.isEmpty(record.voltage)) {
                            int voltageCount = voltageMap.get(record.battaryGroup);
                            voltageMap.put(record.battaryGroup, voltageCount + 1);
                        }
                        if (!TextUtils.isEmpty(record.resistance)) {
                            int resistanceCount = resistanceMap.get(record.battaryGroup);
                            resistanceMap.put(record.battaryGroup, resistanceCount + 1);
                        }
                        if (!TextUtils.isEmpty(record.resistanceImages) || !TextUtils.isEmpty(record.voltageImages)) {
                            List<String> imageList = getRecordImageList(record);
                            Map<String, List<String>> imageMap = groupImageList.get(record.battaryGroup);
                            if (imageMap.keySet().contains(record.battary_code)) {
                                imageMap.get(record.battary_code).addAll(imageList);
                            } else {
                                imageMap.put(record.battary_code, imageList);
                            }
                        }
                    }
                }

                mHandler.sendEmptyMessage(LOAD_BATTERY);
            } catch (DbException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(LOAD_BATTERY_FAILURE);
            }
        });

    }

    private List<String> getRecordImageList(BatteryRecord record) {
        List<String> voltageList = StringUtils.stringToList(record.voltageImages);
        List<String> resistanceList = StringUtils.stringToList(record.resistanceImages);
        voltageList.addAll(resistanceList);
        return voltageList;
    }

    private void initPage() {
        binding.txtColusion.setText(batteryGroups.analysisResult == null ? "" : batteryGroups.analysisResult);
        binding.startTime.setText(report.starttime);
        binding.endTime.setText(report.endtime);
        binding.inspectionPerson.setText(batteryGroups.testManager == null ? "" : batteryGroups.testManager);
        binding.testPerson.setText(batteryGroups.testPersons == null ? "" : batteryGroups.testPersons);
        binding.temperature.setText(report.temperature + "℃");
        binding.testType.setText(taskExtend.sbjcIsAllCheck == 0 ? "普测" : "抽测");
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA_FAILURE:
                ToastUtils.showMessage("加载报告数据出错");
                break;
            case LOAD_DATA:
                initPage();
                break;
            case LOAD_BATTERY_FAILURE:
                ToastUtils.showMessage("加载电池数据出错");
                break;
            case LOAD_BATTERY:
                initRecordPage();
                break;
            default:
                break;
        }
    }

    private void initOnClick() {
        binding.continueInspection.setOnClickListener(view -> {
            Intent intent = new Intent(this, BatteryTestActivity.class);
            intent.putExtra(Config.IS_FROM_SJJC, getIntent().getBooleanExtra(Config.IS_FROM_SJJC, false));
            startActivity(intent);
            this.finish();
        });
    }

    private void initRecordPage() {
        if (null != batteryGroup && !batteryGroup.isEmpty()) {
            int size = batteryGroup.size();
            for (int i = 0; i < size; i++) {
                Battery battery = batteryGroup.get(i);
                ViewHolder holder = new ViewHolder(this, null, R.layout.item_battery_report, false);
                AutoUtils.auto(holder.getRootView());
                holder.setText(R.id.battery_group, battery.name);
                holder.setText(R.id.voltage_record, voltageMap.get(battery.bid) + "/" + battery.amount);
                holder.setText(R.id.resistance_record, resistanceMap.get(battery.bid) + "/" + battery.amount);
                //蓄电池内阻测试隐藏电压
                if (InspectionType.SBJC_11.name().equals(report.inspection)) {
                    holder.getView(R.id.layout_voltage).setVisibility(View.GONE);
                    holder.setText(R.id.voltage_record, resistanceMap.get(battery.bid) + "/" + battery.amount);
                } else {
                    if (taskExtend.sbjcResistanceCheck == 0) {
                        holder.getView(R.id.layout_resistance).setVisibility(View.GONE);
                    }
                }
                
                binding.layoutBattery.addView(holder.getRootView());
                if (i == size - 1) {
                    holder.getView(R.id.line).setVisibility(View.GONE);
                }

                Map<String, List<String>> imageList = groupImageList.get(battery.bid);
                RecyclerView recyclerView = holder.getView(R.id.img_recyclerView);
                if (!imageList.isEmpty()) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    TreeSet<String> keys = new TreeSet<String>();
                    keys.addAll(imageList.keySet());
                    BatteryReportImageAdapter imageAdapter = new BatteryReportImageAdapter(recyclerView, keys, R.layout.battery_pictures_item);
                    imageAdapter.setItemClickListener(new ItemClickListener<List<String>>() {
                        @Override
                        public void itemClick(View v, List<String> imageList, int position) {
                            Intent intent = new Intent(mActivity, ImageDetailsActivity.class);
                            intent.putExtra(Config.CURRENT_IMAGE_POSITION, 0);
                            intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);
                            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, StringUtils.addStrToListItem(imageList, Config.RESULT_PICTURES_FOLDER));
                            intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, false);
                            startActivity(intent);
                        }

                        @Override
                        public void itemLongClick(View v, List<String> strings, int position) {

                        }
                    });
                    recyclerView.setAdapter(imageAdapter);
                    imageAdapter.setBatteryImageMap(imageList);
                } else {
                    holder.getView(R.id.label_image).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }
    }


}
