package com.cnksi.sjjc.activity.batteryactivity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.daoservice.BatteryGroupService;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BatteryService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskExtendService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.databinding.DialogTipsBinding;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Battery;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.TaskExtend;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.common.utils.StringUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.activity.ImageDetailsActivity;
import com.cnksi.sjjc.adapter.BatteryAdapter;
import com.cnksi.sjjc.adapter.BatteryImageAdapter;
import com.cnksi.sjjc.bean.BatteryInstrument;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.databinding.ActivityBatteryBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.BatteryInstrumentService;
import com.cnksi.sjjc.service.BatteryRecordService;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yj
 *         蓄电池检测界面
 *         2016/8/9 15:56
 */
public class BatteryTestActivity extends BaseActivity {
    public static final String TAG = "BatteryTestActivity";
    public static final int LOAD_BATTERY_SUCCESS = 0x001;
    public static final int LOAD_BATTERY_FAILURE = LOAD_BATTERY_SUCCESS + 1;
    public static final int LOAD_BATTERY_RECORD_SUCCESS = LOAD_BATTERY_FAILURE + 1;
    public static final int DELETE_IMAGE = LOAD_BATTERY_RECORD_SUCCESS + 1;
    public static final int EDIT_BATTERY_INFO = 0x100;
    public static final int BATTERY_DEVICEDEFECT_ACTIVITY = EDIT_BATTERY_INFO + 1;
    private List<Battery> batteryList;
    private List<BatteryRecord> batteryImageList;
    private BatteryImageAdapter batteryImageAdapter;
    private Battery currentBattery;

    private int currentCheckType;

    private Map<String, BatteryRecord> batteryRecordMap = new HashMap<String, BatteryRecord>();

    private TaskExtend taskExpand;

    private InspectionType inspectionType;

    public static final String[] checkType = new String[]{"电压检测", "内阻检测"};

    private BatteryRecord selectBatteryRecord;
    //当前报告
    private Report currentReport;
    private Task task;
    private Map<String, Integer> voltageMap = new HashMap<String, Integer>();
    private Map<String, Integer> resistanceMap = new HashMap<String, Integer>();
    private String currentBatterId;
    private Map<String, ArrayList<String>> basicInforMap = new HashMap<String, ArrayList<String>>();
    //当前报告下记录的蓄电池组基础信息
    private Map<String, BatteryGroup> batteryGroupList = new HashMap<>();
//    private List<BatteryGroup> batteryGroupList;

    private String type = "";

    /**
     * 测试仪器名称
     */
    private String testInstrumentName;
    /**
     * 测试仪器名称ID
     */
    private String testInstrumentNameId;

    private String typeName;
    private List<String> batteryCodeList;
    private BatteryAdapter batteryCodeAdapter;
    private ActivityBatteryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBatteryBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        Bundle bundle = getIntent().getExtras();
        /*
         * <b>NOTE :</b>设备检测CURRENT_TASK_ID不能存放在intent中传递，通过缓存传递
         */
        if (null != bundle && !TextUtils.isEmpty(bundle.getString(Config.CURRENT_TASK_ID))) {

            typeName = bundle.getString(Config.CURRENT_INSPECTION_TYPE_NAME);
            PreferencesUtils.put("typename", typeName);
            currentInspectionType = typeName.contains(Config.DIANYA) ? InspectionType.SBJC_10.name() : InspectionType.SBJC_11.name();
            currentBdzId = bundle.getString(Config.CURRENT_BDZ_ID);
            currentBdzName = bundle.getString(Config.CURRENT_BDZ_NAME);
            currentReportId = bundle.getString(Config.CURRENT_REPORT_ID);
            currentTaskId = bundle.getString(Config.CURRENT_TASK_ID);
            type = bundle.getString(Config.CURRENT_MAINTANENCE_BATTERY);
            String currentFolder = bundle.getString(Config.CURRENT_FILENAME);
            PreferencesUtils.put(Config.PICTURE_PREFIX, currentFolder);
            PreferencesUtils.put(Config.CURRENT_MAINTANENCE_BATTERY, type);
            PreferencesUtils.put(Config.CURRENT_BDZ_ID, currentBdzId);
            PreferencesUtils.put(Config.CURRENT_BDZ_NAME, currentBdzName);
            PreferencesUtils.put(Config.CURRENT_REPORT_ID, currentReportId);
            PreferencesUtils.put(Config.CURRENT_TASK_ID, currentTaskId);
            PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
            PreferencesUtils.put(Config.CURRENT_INSPECTION_NAME, InspectionType.get(currentInspectionType).value);
            PreferencesUtils.put(Config.IS_FROM_PMS, true);
        }
        getIntentValue();
        inspectionType = InspectionType.get(currentInspectionType);
        initView();
        loadData();
        initOnClick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void initView() {

        if (InspectionType.SBJC_11 == inspectionType) {
            binding.voltageTotal.setVisibility(View.GONE);
            binding.resistanceTotal.setVisibility(View.VISIBLE);
            binding.layoutSingleVoltage.setVisibility(View.GONE);
            currentCheckType = 1;
        }
        mTitleBinding.tvRight.setVisibility(View.VISIBLE);

        mTitleBinding.tvRight.setText("完成检测");
        mTitleBinding.tvRight.setBackgroundResource(R.drawable.red_button_background_selector);
        mTitleBinding.tvRight.setTextColor(Color.WHITE);
        if (PreferencesUtils.get(Config.IS_FROM_PMS, false)) {
            mTitleBinding.tvRight.setVisibility(View.GONE);
            PreferencesUtils.put(Config.IS_FROM_PMS, false);
        } else {
            mTitleBinding.tvRight.setVisibility(View.VISIBLE);
        }
        mTitleBinding.tvTitle.setText(inspectionType.value);
        mTitleBinding.btnBack.setOnClickListener(view -> {
            try {
                new Float(binding.etCurrentTempreture.getText().toString());
                currentReport.temperature = binding.etCurrentTempreture.getText().toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!cacheBatteryInfor()) {
                ToastUtils.showMessage("基本信息输入有误，请核对");
                return;
            }
            try {
                currentReport.checkType = taskExpand.sbjcIsAllCheck;
                ReportService.getInstance().saveOrUpdate(currentReport);
            } catch (DbException e) {
                e.printStackTrace();
                Log.i("BatteryTestActivity", "返回键数据保存出错");
            }
            typeName = PreferencesUtils.get("typename", "");
            boolean xudianchi = typeName.contains(Config.XUDIANCHI) && (typeName.contains(Config.DIANYA) || typeName.contains(Config.NEIZU));
            if (xudianchi && task.status.equalsIgnoreCase("done") && !getIntent().getBooleanExtra(Config.IS_FROM_SJJC, false)) {
                PreferencesUtils.put(Config.CURRENT_MAINTANENCE_BATTERY, "");
                Intent intent = new Intent();
                ComponentName componentName;
                componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindFragment");
                intent.setComponent(componentName);
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
                startActivity(intent);
            }
            KeyBoardUtils.closeKeybord(_this);
            BatteryTestActivity.this.finish();
        });
        batteryCodeList = new ArrayList<String>();
        batteryCodeAdapter = new BatteryAdapter(this, batteryCodeList, R.layout.battery_item, currentCheckType);
        batteryCodeAdapter.setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void itemClick(View v, String batteryCode, int position) {
                Intent intent = new Intent(mActivity, BatteryDialogActivity.class);
                intent.putExtra(Config.CURRENT_TASK_ID, currentTaskId);
                intent.putExtra(Config.CURRENT_BATTERY_NUM, batteryCode);
                intent.putExtra(Config.CURRENT_FUNCTION_MODEL, currentCheckType);
                intent.putExtra(Config.CURRENT_BDZ_NAME, currentBdzName);
                intent.putExtra(Config.CURRENT_BATTERY_ZU, currentBattery);
                intent.putExtra(Config.CURRENT_REPORT_ID, currentReportId);
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
                intent.putExtra(Config.CURRENT_TEST_TYPE, taskExpand.sbjcIsAllCheck);
                startActivityForResult(intent, EDIT_BATTERY_INFO);
            }

            @Override
            public void itemLongClick(View v, String s, int position) {

            }
        });

        binding.batteryCordGrid.setAdapter(batteryCodeAdapter);
        binding.batteryCordGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                binding.layoutBaseInfor.setVisibility(View.GONE);
                binding.ivHideShow.setBackgroundResource(R.mipmap.icon_down);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        batteryImageList = new ArrayList<BatteryRecord>();
        batteryImageAdapter = new BatteryImageAdapter(this, batteryImageList, R.layout.battery_pictures_item);
        batteryImageAdapter.setItemClickListener(new ItemClickListener<BatteryRecord>() {
            @Override
            public void itemClick(View v, BatteryRecord batteryRecord, int position) {
                showImage(batteryRecord);
            }

            @Override
            public void itemLongClick(View v, BatteryRecord batteryRecord, int position) {

            }
        });

        binding.batteryImageGrid.setAdapter(batteryImageAdapter);
    }


    public void loadData() {
        //1、查询当前变电站下电池组
        ExecutorManager.executeTaskSerially(() -> {
            try {
//                    batteryGroupList = BatteryGroupService.getInstance().getAllGroup(currentReportId);
                currentReport = ReportService.getInstance().findById(currentReportId);
                task = TaskService.getInstance().findById(currentReport.taskid);
                currentReport.inspection = currentInspectionType;
                ReportService.getInstance().saveOrUpdate(currentReport);
                taskExpand = TaskExtendService.getInstance().findTaskExtendByTaskId(currentTaskId);
                batteryList = BatteryService.getInstance().getAllBattery(currentBdzId);
                if (null != batteryList && !batteryList.isEmpty()) {
                    for (Battery battery : batteryList) {
                        voltageMap.put(battery.bid, 0);
                        resistanceMap.put(battery.bid, 0);
                    }
                }
                if (null != batteryList && !batteryList.isEmpty()) {
                    mHandler.sendEmptyMessage(LOAD_BATTERY_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(LOAD_BATTERY_FAILURE);
                }
            } catch (DbException e) {
                e.printStackTrace();
                Log.e(TAG, "初始化数据出现错误");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.etCurrentTempreture.setText(StringUtils.getTransformTep(binding.etCurrentTempreture.getText().toString()));
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            //加载电池组数据成功
            case LOAD_BATTERY_SUCCESS:
                binding.currentTestType.setText((taskExpand.sbjcIsAllCheck == 0) ? "普测" : "抽测");
                binding.etCurrentTempreture.setText(TextUtils.isEmpty(currentReport.temperature) ? "" : currentReport.temperature);
                initBatteryGroup();
//                initCheckGroup();
                break;
            case LOAD_BATTERY_FAILURE:
                ToastUtils.showMessage("该变电站下暂无电池组数据");
                break;
            //加载当前电池组记录成功
            case LOAD_BATTERY_RECORD_SUCCESS:
                if (voltageMap.keySet().contains(currentBattery.bid) || resistanceMap.keySet().contains(currentBattery.bid)) {
                    String voltage = "已抄录\t" + voltageMap.get(currentBattery.bid) + "/" + currentBattery.amount;
                    String resistance = "已抄录\t" + resistanceMap.get(currentBattery.bid) + "/" + currentBattery.amount;
                    SpannableString spanVoltage = new SpannableString(voltage);
                    SpannableString spanResistance = new SpannableString(resistance);
//                    getResources().getColor(R.color.radio_text_checked_color)
                    spanVoltage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_this, R.color.radio_text_checked_color)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanResistance.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_this, R.color.radio_text_checked_color)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanVoltage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_this, R.color.red_unpressed_color)), 3, spanVoltage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanResistance.setSpan(new ForegroundColorSpan(ContextCompat.getColor(_this, R.color.red_unpressed_color)), 3, resistance.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    binding.resistanceTotal.setText(spanResistance);
                    binding.voltageTotal.setText(spanVoltage);

                }
                batteryCodeAdapter.setRecordMap(batteryRecordMap);
                batteryImageAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

//    private void initCheckGroup() {
//        if (inspectionType == InspectionType.SBJC_10 && taskExpand.sbjcResistanceCheck == 1) {
//            binding.layoutCheck.setVisibility(View.VISIBLE);
//            for (int i = 0; i < 2; i++) {
//                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tab, null, false);
//                textView.setTag(i);
//                textView.setText(checkType[i]);
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(520, LinearLayout.LayoutParams.MATCH_PARENT);
//                textView.setLayoutParams(p);
//                binding.binding.layoutCheckTab.addView(textView);
//                textView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        changeCheckTab(v);
//                    }
//                });
//                if (i == 0) changeCheckTab(textView);
//            }
//        } else {
//            if (inspectionType == InspectionType.SBJC_11){
//                currentCheckType = 1;
//                binding.voltageTotal.setVisibility(View.GONE);
//                binding.resistanceTotal.setVisibility(View.VISIBLE);
//            } else{
//                binding.resistanceTotal.setVisibility(View.GONE);
//            }
//            batteryCodeAdapter.setCheckType(currentCheckType);
//            binding.layoutCheck.setVisibility(View.GONE);
//        }
//    }

    /**
     * 初始化电池组数据
     */
    private void initBatteryGroup() {
        int index = 0;
        binding.layoutBattery.setMinimumWidth(DisplayUtils.getInstance().getWidth());
        for (final Battery battery : batteryList) {
            ViewHolder holder = new ViewHolder(this, null, R.layout.item_battery_group, false);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            p.weight = 1;
            holder.getRootView().setLayoutParams(p);
            AutoUtils.autoSize(holder.getRootView());
            holder.setText(R.id.battery, battery.name);
            binding.layoutBattery.addView(holder.getRootView());
            holder.getRootView().setTag(battery);
            holder.getRootView().setOnClickListener(v -> {
                if (cacheBatteryInfor()) {
                    changeBatteryTab(v);
                } else {
                    ToastUtils.showMessage("基本信息输入有误，请核对");
                    return;
                }
            });
            if (index == 0) {
                changeBatteryTab(holder.getRootView());
            }
            index++;
        }
        if (1 == batteryList.size()) {
            binding.containerHorizantal.setVisibility(View.GONE);
            binding.layoutBattery.setVisibility(View.GONE);
        }
    }

    /**
     * tab切换更换电池组
     */
    private void changeBatteryTab(View view) {
        int tabSize = binding.layoutBattery.getChildCount();
        for (int i = 0; i < tabSize; i++) {
            View tab = binding.layoutBattery.getChildAt(i);
            if (tab.equals(view)) {
                tab.setSelected(true);
                currentBattery = (Battery) view.getTag();

                currentBatterId = currentBattery.bid;
                setBatteryInfor(currentBattery);
                //蓄电池测试默认电压测试，内阻测试默认内阻测试
                if (inspectionType == InspectionType.SBJC_10) {
//                    if (1 == taskExpand.sbjcResistanceCheck)
//                        changeCheckTab(binding.binding.layoutCheckTab.getChildAt(0));
                } else {
                    currentCheckType = 1;
                }
            } else {
                tab.setSelected(false);
            }
        }

        int batteryCodeCount = currentBattery.amount;
        batteryCodeList.clear();
        for (int code = 1; code <= batteryCodeCount; code++) {
            if (code < 10) {
                batteryCodeList.add("00" + code);
            } else if (code < 100) {
                batteryCodeList.add("0" + code);
            } else {
                batteryCodeList.add("" + code);
            }
        }
        batteryCodeAdapter.notifyDataSetChanged();
        loadCurrentBatteryRecord();
    }

    /**
     * 初始化蓄电池的基础值
     */
    private void setBatteryInfor(Battery currentBattery) {
        BatteryGroup group = batteryGroupList.get(currentBatterId);
        if (null == group) {
            group = BatteryGroupService.getInstance().getBatteryGroup(currentReportId, currentBatterId);
            if (null == group) {
                group = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, currentBatterId);
            }
            batteryGroupList.put(currentBatterId, group);
        }
        binding.etCurrentTempreture.setText(StringUtils.getTransformTep(binding.etCurrentTempreture.getText().toString()));
        String zuDuanVoltage = group.zuDuanVoltage == null ? currentBattery.voltage : group.zuDuanVoltage + "";
        String zuDuanElectricity = group.chargeElectricity == null ? currentBattery.dl : group.chargeElectricity + "";
        String singleVoltage = group.singleVoltage == null ? currentBattery.singleVoltage : group.singleVoltage + "";

        binding.groupVoltage.setText(zuDuanVoltage);
        binding.chargeCurrent.setText(zuDuanElectricity);
        binding.singleVoltage.setText(singleVoltage);

        binding.etFactVoltage.setText(group.fcdySc == null ? "" : group.fcdySc + "");
        binding.etSystemVoltage.setText(group.fcdyZlxt == null ? "" : group.fcdyZlxt + "");
        binding.etLoadElectric.setText(group.loadElectricty == null ? "" : group.loadElectricty + "");
        binding.etFuchongElectricty.setText(group.fcElectricty == null ? "" : group.fcElectricty + "");
        binding.etZhengMuxianVoltage.setText(group.zVoltage == null ? "" : group.zVoltage + "");
        binding.etFuMuxianVoltage.setText(group.fVoltage == null ? "" : group.fVoltage);
        binding.etTestInstrument.setText(TextUtils.isEmpty(group.testInstrument) ? "" : group.testInstrument);


    }

    @Override
    protected void onStop() {
        super.onStop();
        cacheBatteryInfor();
    }

    /**
     * 存储当前的蓄电池的值
     */
    private boolean cacheBatteryInfor() {
        /*
         * 1、
         * 2、電池組是否存在記錄
         * 3、更改記錄電池
         */
        if (TextUtils.isEmpty(currentBatterId)) {
            return false;
        }
        BatteryGroup batteryGroup = batteryGroupList.get(currentBatterId);
        return saveGroupBaseInfor(batteryGroup);

//        if (batteryGroupList.isEmpty()) {
//            batteryGroup = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, currentBatterId);
//            saveGroupBaseInfor(batteryGroup);
//            batteryGroupList.add(batteryGroup);
//        } else {
//            if (batteryList.size() == batteryGroupList.size()) {
//                for (BatteryGroup group : batteryGroupList) {
//                    if (currentBatterId.equalsIgnoreCase(group.battaryGroup)) {
//                        batteryGroup = group;
//                        saveGroupBaseInfor(batteryGroup);
//                        return;
//                    }
//                }
//            } else {
//                for (BatteryGroup group : batteryGroupList) {
//                    if (currentBatterId.equalsIgnoreCase(group.battaryGroup)) {
//                        batteryGroup = group;
//                        saveGroupBaseInfor(batteryGroup);
//                        return;
//                    } else {
//                        batteryGroup = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, currentBatterId);
//                        saveGroupBaseInfor(batteryGroup);
//                        batteryGroupList.add(batteryGroup);
//                    }
//                }
//            }
//        }
    }


    /**
     * 保存当前基础信息
     */
    private boolean saveGroupBaseInfor(BatteryGroup batteryGroup) {

        if (StringUtils.checkTemprature(binding.groupVoltage.getText().toString()) && StringUtils.checkTemprature(binding.chargeCurrent.getText().toString())
                && StringUtils.checkTemprature(binding.singleVoltage.getText().toString()) && StringUtils.checkTemprature(binding.etFactVoltage.getText().toString())
                && StringUtils.checkTemprature(binding.etSystemVoltage.getText().toString()) && StringUtils.checkTemprature(binding.etLoadElectric.getText().toString())
                && StringUtils.checkTemprature(binding.etFuchongElectricty.getText().toString()) && StringUtils.checkTemprature(binding.etZhengMuxianVoltage.getText().toString())
                && StringUtils.checkTemprature(binding.etFuMuxianVoltage.getText().toString())) {
            batteryGroup.zuDuanVoltage = StringUtils.getTransformTep(binding.groupVoltage.getText().toString().replace("V", ""));
            batteryGroup.chargeElectricity = StringUtils.getTransformTep(binding.chargeCurrent.getText().toString().replace("A", ""));
            batteryGroup.singleVoltage = StringUtils.getTransformTep(binding.singleVoltage.getText().toString().replace("V", ""));
            batteryGroup.fcdySc = StringUtils.getTransformTep(binding.etFactVoltage.getText().toString());
            batteryGroup.fcdyZlxt = StringUtils.getTransformTep(binding.etSystemVoltage.getText().toString());
            batteryGroup.loadElectricty = StringUtils.getTransformTep(binding.etLoadElectric.getText().toString());
            batteryGroup.fcElectricty = StringUtils.getTransformTep(binding.etFuchongElectricty.getText().toString());
            batteryGroup.zVoltage = StringUtils.getTransformTep(binding.etZhengMuxianVoltage.getText().toString());
            batteryGroup.fVoltage = StringUtils.getTransformTep(binding.etFuMuxianVoltage.getText().toString());
            batteryGroup.testInstrument = binding.etTestInstrument.getText().toString().trim();
        } else {

            return false;
        }
        try {
            BatteryGroupService.getInstance().saveOrUpdate(batteryGroup);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

    private Double toDouble(String s) {
        if (TextUtils.isEmpty(s.trim())) {
            return null;
        }
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


//    private void changeCheckTab(View view) {
//        int tabSize = binding.binding.layoutCheckTab.getChildCount();
//        for (int i = 0; i < tabSize; i++) {
//            View tab = binding.binding.layoutCheckTab.getChildAt(i);
//            if (tab.equals(view)) {
//                tab.setSelected(true);
//
//                TranslateAnimation animation = new TranslateAnimation(binding.tabIcon.getLeft(), view.getLeft(), 0, 0);
//                animation.setFillAfter(true);
//                animation.setInterpolator(new AccelerateDecelerateInterpolator());
//                binding.tabIcon.startAnimation(animation);
//                currentCheckType = (Integer) view.getTag();
//                batteryCodeAdapter.setCheckType(currentCheckType);
//
//            } else tab.setSelected(false);
//        }
//    }

    /**
     * 查询当前电池组存在的数据
     */
    private void loadCurrentBatteryRecord() {
        ExecutorManager.executeTaskSerially(() -> {
            batteryRecordMap.clear();
            batteryImageList.clear();
            voltageMap.put(currentBattery.bid, 0);
            resistanceMap.put(currentBattery.bid, 0);
            try {
                List<BatteryRecord> list = BatteryRecordService.getInstance().getBatteryRecord(currentReportId, currentBattery.bid);
                if (null != list && !list.isEmpty()) {
                    for (BatteryRecord record : list) {
                        batteryRecordMap.put(record.battary_code, record);
                        if (!TextUtils.isEmpty(record.voltageImages) || !TextUtils.isEmpty(record.resistanceImages)) {
                            batteryImageList.add(record);
                        }
                    }
                }
                if (null != list && !list.isEmpty()) {
                    for (BatteryRecord record : list) {
                        if (!TextUtils.isEmpty(record.voltage)) {
                            int voltageCount = voltageMap.get(record.battaryGroup);
                            voltageMap.put(record.battaryGroup, voltageCount + 1);
                        }
                        if (!TextUtils.isEmpty(record.resistance)) {
                            int resistanceCount = resistanceMap.get(record.battaryGroup);
                            resistanceMap.put(record.battaryGroup, resistanceCount + 1);
                        }
                    }
                }
                mHandler.sendEmptyMessage(LOAD_BATTERY_RECORD_SUCCESS);
            } catch (DbException e) {
                Log.e(TAG, "查询当前电池组存在数据出错");
                e.printStackTrace();
            }
        });

    }

    private void initOnClick() {
        mTitleBinding.tvRight.setOnClickListener(view -> {
            if (cacheBatteryInfor()) {
                saveOrUpdateReport();
            } else {
                ToastUtils.showMessage("基本信息输入有误，请核对");
                return;
            }
        });
        binding.layoutImage.setOnClickListener(view -> {

            if (binding.layoutBaseInfor.getVisibility() == View.GONE) {
                binding.layoutBaseInfor.setVisibility(View.VISIBLE);
                binding.ivHideShow.setBackgroundResource(R.mipmap.icon_up);
            } else {
                binding.layoutBaseInfor.setVisibility(View.GONE);
                binding.ivHideShow.setBackgroundResource(R.mipmap.icon_down);
            }
        });
        binding.ivGoBtdefect.setOnClickListener(view -> {
            Intent intent = new Intent(_this, BatteryDeviceDetectActivity.class);
            startActivityForResult(intent, BATTERY_DEVICEDEFECT_ACTIVITY);
        });
    }

    private Dialog mSureDialog;
    private DialogTipsBinding mTipsBinding;

    private void showSureDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        if (mSureDialog == null) {
            mTipsBinding = DialogTipsBinding.inflate(LayoutInflater.from(getApplicationContext()));
            mSureDialog = DialogUtils.creatDialog(mActivity, mTipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mTipsBinding.tvDialogTitle.setText("提示");
        mTipsBinding.tvDialogContent.setText("是否完成本次检查");
        mSureDialog.show();
        mTipsBinding.btnCancel.setOnClickListener(view -> mSureDialog.dismiss());

        mTipsBinding.btnSure.setOnClickListener(view -> {
            saveOrUpdateReport();
            mSureDialog.dismiss();
        });

    }

    private String selectDeviceNum;

    private void saveOrUpdateReport() {
        currentReport.checkType = taskExpand.sbjcIsAllCheck;
        currentReport.endtime = DateUtils.getCurrentLongTime();
        String valueTemp = binding.etCurrentTempreture.getText().toString();
        if (TextUtils.isEmpty(valueTemp)) {
            ToastUtils.showMessage("请输入环境温度");
            return;
        }
        try {
            new Float(valueTemp);
        } catch (Exception ex) {
            ex.printStackTrace();
            ToastUtils.showMessage("温度输入非法");
            return;
        }
        if ((-99.9f > new Float(valueTemp) || new Float(valueTemp) > 99.99)) {
            ToastUtils.showMessage("温度在-99.9℃到99.9℃");
            return;
        }
        currentReport.temperature = binding.etCurrentTempreture.getText().toString();
        currentReport.checkType = taskExpand.sbjcIsAllCheck;
        if (inspectionType == InspectionType.SBJC_10) {
            currentReport.resistanceIsCheck = taskExpand.sbjcResistanceCheck;
        }
        try {
            ReportService.getInstance().saveOrUpdate(currentReport);
            if (!TextUtils.isEmpty(selectDeviceNum)) {
                BatteryInstrumentService.getInstance().saveOrUpdateObject(testInstrumentNameId, selectDeviceNum);
            }
            Intent intent = new Intent(this, BatteryFinishActivity.class);
            startActivity(intent);
        } catch (DbException e) {
            Log.e(TAG, "clickEvent: 保存报告出错");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_BATTERY_INFO:
                    String batteryCode = data.getStringExtra("batteryCode");
                    ArrayList<String> images = data.getStringArrayListExtra("images");
                    String value = data.getStringExtra("value");
                    if ("1".equalsIgnoreCase(String.valueOf(taskExpand.sbjcIsAllCheck))) {
                        saveData(batteryCode, images, value);
                    }
                    loadCurrentBatteryRecord();
                    break;
                case DELETE_IMAGE:
                    ArrayList<String> deleteImageList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String image : deleteImageList) {
                        String absoluteImageName = image.replace(Config.RESULT_PICTURES_FOLDER, "");
                        String realImageName = image.substring(image.lastIndexOf(File.separatorChar) + 1, image.length());

                        if (realImageName.startsWith("DY")) {
                            selectBatteryRecord.voltageImages = selectBatteryRecord.voltageImages.replace(absoluteImageName, "").replace(",,", ",");
                        } else {
                            selectBatteryRecord.resistanceImages = selectBatteryRecord.resistanceImages.replace(absoluteImageName, "").replace(",,", ",");
                        }
                    }
                    if (!TextUtils.isEmpty(selectBatteryRecord.voltageImages)) {
                        if (selectBatteryRecord.voltageImages.startsWith(",")) {
                            selectBatteryRecord.voltageImages = selectBatteryRecord.voltageImages.substring(1, selectBatteryRecord.voltageImages.length());
                        } else if (selectBatteryRecord.voltageImages.endsWith(",")) {
                            selectBatteryRecord.voltageImages = selectBatteryRecord.voltageImages.substring(0, selectBatteryRecord.voltageImages.length() - 1);
                        }
                    }
                    if (!TextUtils.isEmpty(selectBatteryRecord.resistanceImages)) {
                        if (selectBatteryRecord.resistanceImages.startsWith(",")) {
                            selectBatteryRecord.resistanceImages = selectBatteryRecord.resistanceImages.substring(1, selectBatteryRecord.resistanceImages.length());
                        } else if (selectBatteryRecord.resistanceImages.endsWith(",")) {
                            selectBatteryRecord.resistanceImages = selectBatteryRecord.resistanceImages.substring(0, selectBatteryRecord.resistanceImages.length() - 1);
                        }
                    }
                    if (TextUtils.isEmpty(selectBatteryRecord.voltageImages) && TextUtils.isEmpty(selectBatteryRecord.resistanceImages)) {
                        batteryImageList.remove(selectBatteryRecord);
                    }
                    try {
                        BatteryRecordService.getInstance().saveOrUpdate(selectBatteryRecord);
                        batteryImageAdapter.notifyDataSetChanged();
                    } catch (DbException e) {
                        e.printStackTrace();
                        Log.e(TAG, "保存数据错误");
                    }
                    break;
                case BATTERY_DEVICEDEFECT_ACTIVITY:
                    testInstrumentName = data.getStringExtra(BatteryInstrument.CSYQMC);
                    testInstrumentNameId = data.getStringExtra(BatteryInstrument.ID);
                    selectDeviceNum = data.getStringExtra(BatteryInstrument.SELECT_NUM);
                    if (!TextUtils.isEmpty(testInstrumentName)) {
                        binding.etTestInstrument.setText(testInstrumentName);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void saveData(String batteryCode, ArrayList<String> imageList, String value) {
        BatteryRecord batteryRecord = batteryRecordMap.get(batteryCode);
        if (null == batteryRecord && (!TextUtils.isEmpty(value) || !imageList.isEmpty())) {
            batteryRecord = new BatteryRecord(currentReportId, currentBdzId, currentBdzName, currentBattery.bid, batteryCode, currentInspectionType, taskExpand.sbjcIsAllCheck);
        }
        String images = StringUtils.ArrayListToString(imageList);
        if (null != batteryRecord) {
            if (currentCheckType == 0) {
                batteryRecord.voltage = value;
                batteryRecord.voltageImages = images;
            } else {
                batteryRecord.resistance = value;
                batteryRecord.resistanceImages = images;
            }
            try {
                batteryRecord.last_modify_time = DateUtils.getCurrentLongTime();
                BatteryRecordService.getInstance().saveOrUpdate(batteryRecord);
            } catch (DbException e) {
                e.printStackTrace();
                Log.e(TAG, "保存数据错误");
            }
        }
    }

    private void showImage(BatteryRecord batteryRecord) {
        selectBatteryRecord = batteryRecord;
        String voltageImages = batteryRecord.voltageImages == null ? "" : batteryRecord.voltageImages;
        String resistanceImages = batteryRecord.resistanceImages == null ? "" : batteryRecord.resistanceImages;
        List<String> voltageList = com.cnksi.core.utils.StringUtils.stringToList(voltageImages);
        List<String> resistanceList = com.cnksi.core.utils.StringUtils.stringToList(resistanceImages);
        voltageList.addAll(resistanceList);
        Intent intent = new Intent(mActivity, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, 0);
        intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);
        intent.putStringArrayListExtra(Config.IMAGEURL_LIST, com.cnksi.core.utils.StringUtils.addStrToListItem(voltageList, Config.RESULT_PICTURES_FOLDER));
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        startActivityForResult(intent, DELETE_IMAGE);
    }
}