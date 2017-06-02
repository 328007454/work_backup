package com.cnksi.sjjc.activity;

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
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.KeyBoardUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BatteryAdapter;
import com.cnksi.sjjc.adapter.BatteryImageAdapter;
import com.cnksi.sjjc.bean.Battery;
import com.cnksi.sjjc.bean.BatteryGroup;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskExtend;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.BatteryGroupService;
import com.cnksi.sjjc.service.BatteryRecordService;
import com.cnksi.sjjc.service.BatteryService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskExtendService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.DialogUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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

    private List<Battery> batteryList;

    @ViewInject(R.id.layout_battery)
    private LinearLayout layoutBattery;

    @ViewInject(R.id.container_horizantal)
    private HorizontalScrollView containerHorizantal;

    //当前测试类型
    @ViewInject(R.id.current_test_type)
    private TextView txtCheckType;
    //组电电压
    @ViewInject(R.id.group_voltage)
    private EditText txtVoltage;
    //充电电流
    @ViewInject(R.id.charge_current)
    private EditText txtCurrent;

    @ViewInject(R.id.battery_cord_grid)
    private GridView batteryCodeGrid;
    private List<String> batteryCodeList;
    private BatteryAdapter batteryCodeAdapter;

    @ViewInject(R.id.battery_image_grid)
    private GridView batteryImageGrid;
    private List<BatteryRecord> batteryImageList;
    private BatteryImageAdapter batteryImageAdapter;

    @ViewInject(R.id.layout_check)
    private LinearLayout layoutCheck;

    @ViewInject(R.id.layout_check_tab)
    private LinearLayout layoutCheckTab;

    @ViewInject(R.id.tab_icon)
    private ImageView imageIcon;

    @ViewInject(R.id.et_current_tempreture)
    private EditText txtTempreture;
    //实全电池浮充电压
    @ViewInject(R.id.et_fact_voltage)
    private EditText txtFactVoltage;

    //直流系统监测全电池浮充电压
    @ViewInject(R.id.et_system_voltage)
    private EditText txtSystemVoltage;

    //负荷电流
    @ViewInject(R.id.et_load_electric)
    private EditText txtLoadElect;

    //浮充电流
    @ViewInject(R.id.et_fuchong_electricty)
    private EditText txtFuChongEle;

    //正母线对地电压值
    @ViewInject(R.id.et_zheng_muxian_voltage)
    private EditText txtZVoltage;

    //负母线对地电压值
    @ViewInject(R.id.et_fu_muxian_voltage)
    private EditText txtFVoltage;

    //内阻数量统计
    @ViewInject(R.id.resistance_total)
    private TextView txtResistanceTotal;
    //电压统计
    @ViewInject(R.id.voltage_total)
    private TextView txtVoltageTotal;

    //箭头控制基础信息显示
    @ViewInject(R.id.iv_hide_show)
    private ImageView ivShow;

    //基础信息容器
    @ViewInject(R.id.layout_base_infor)
    private LinearLayout baseInforLayout;

    //单只标称电压字段容器
    @ViewInject(R.id.layout_single_voltage)
    private LinearLayout singleVoltageLayout;

    //单只标称电压字段容器
    @ViewInject(R.id.single_voltage)
    private EditText txtSingleVoltage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_battery);
        x.view().inject(this);
        Bundle bundle = getIntent().getExtras();
        /*
         * <b>NOTE :</b>设备检测CURRENT_TASK_ID不能存放在intent中传递，通过缓存传递
         */
        if (null != bundle && !TextUtils.isEmpty(bundle.getString(Config.CURRENT_TASK_ID))) {
            currentInspectionType = InspectionType.SBJC_10.name();
            currentBdzId = bundle.getString(Config.CURRENT_BDZ_ID);
            currentBdzName = bundle.getString(Config.CURRENT_BDZ_NAME);
            currentReportId = bundle.getString(Config.CURRENT_REPORT_ID);
            currentTaskId = bundle.getString(Config.CURRENT_TASK_ID);
            type = bundle.getString(Config.CURRENT_MAINTANENCE_BATTERY);
            String currentFolder = bundle.getString(Config.CURRENT_FILENAME);
            PreferencesUtils.put(_this, Config.PICTURE_PREFIX, currentFolder);
            PreferencesUtils.put(_this, Config.CURRENT_MAINTANENCE_BATTERY, type);
            PreferencesUtils.put(_this, Config.CURRENT_BDZ_ID, currentBdzId);
            PreferencesUtils.put(_this, Config.CURRENT_BDZ_NAME, currentBdzName);
            PreferencesUtils.put(_this, Config.CURRENT_REPORT_ID, currentReportId);
            PreferencesUtils.put(_this, Config.CURRENT_TASK_ID, currentTaskId);
            PreferencesUtils.put(_this, Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
            PreferencesUtils.put(_this, Config.CURRENT_INSPECTION_NAME, InspectionType.get(currentInspectionType).value);
        }
        getIntentValue();
        inspectionType = InspectionType.get(currentInspectionType);
        initUI();
        initData();
    }

    private void initUI() {
        if (InspectionType.SBJC_11 == inspectionType) {
            txtVoltageTotal.setVisibility(View.GONE);
            txtResistanceTotal.setVisibility(View.VISIBLE);
            singleVoltageLayout.setVisibility(View.GONE);
            currentCheckType = 1;
        }
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成检测");
        tvRight.setBackgroundResource(R.drawable.red_button_background_selector);
        tvRight.setTextColor(Color.WHITE);
        tvTitle.setText(inspectionType.value);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentReport.temperature = txtTempreture.getText().toString();
                cacheBatteryInfor();
                try {
                    ReportService.getInstance().saveOrUpdate(currentReport);
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.i("BatteryTestActivity", "返回键数据保存出错");
                }
                if ("maintenance_xdcdyjc".equalsIgnoreCase(PreferencesUtils.getString(_this, Config.CURRENT_MAINTANENCE_BATTERY, "")) && task.status.equalsIgnoreCase("done")) {
                    PreferencesUtils.put(_this, Config.CURRENT_MAINTANENCE_BATTERY, "");
                    Intent intent = new Intent();
                    ComponentName componentName;
                    componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                    intent.setComponent(componentName);
                    intent.putExtra(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
                    startActivity(intent);
                }
                KeyBoardUtils.closeKeybord(_this);
                BatteryTestActivity.this.finish();
            }
        });
        batteryCodeList = new ArrayList<String>();
        batteryCodeAdapter = new BatteryAdapter(this, batteryCodeList, R.layout.battery_item, currentCheckType);
        batteryCodeAdapter.setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void itemClick(View v, String batteryCode, int position) {
                Intent intent = new Intent(mCurrentActivity, BatteryDialogActivity.class);
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
        batteryCodeGrid.setAdapter(batteryCodeAdapter);
        batteryCodeGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                baseInforLayout.setVisibility(View.GONE);
                ivShow.setBackgroundResource(R.mipmap.icon_down);
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
        batteryImageGrid.setAdapter(batteryImageAdapter);
    }


    private void initData() {
        //1、查询当前变电站下电池组
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                    if (null != batteryList && !batteryList.isEmpty())
                        mHandler.sendEmptyMessage(LOAD_BATTERY_SUCCESS);
                    else mHandler.sendEmptyMessage(LOAD_BATTERY_FAILURE);
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.e(TAG, "初始化数据出现错误");
                }
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            //加载电池组数据成功
            case LOAD_BATTERY_SUCCESS:
                txtCheckType.setText((taskExpand.sbjcIsAllCheck == 0) ? "普测" : "抽测");
                txtTempreture.setText(TextUtils.isEmpty(currentReport.temperature) ? "" : currentReport.temperature);
                initBatteryGroup();
//                initCheckGroup();
                break;
            case LOAD_BATTERY_FAILURE:
                CToast.showShort(this, "该变电站下暂无电池组数据");
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
                    txtResistanceTotal.setText(spanResistance);
                    txtVoltageTotal.setText(spanVoltage);
                }
                batteryCodeAdapter.setRecordMap(batteryRecordMap);
                batteryImageAdapter.notifyDataSetChanged();
                break;
        }
    }

//    private void initCheckGroup() {
//        if (inspectionType == InspectionType.SBJC_10 && taskExpand.sbjcResistanceCheck == 1) {
//            layoutCheck.setVisibility(View.VISIBLE);
//            for (int i = 0; i < 2; i++) {
//                TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_tab, null, false);
//                textView.setTag(i);
//                textView.setText(checkType[i]);
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(520, LinearLayout.LayoutParams.MATCH_PARENT);
//                textView.setLayoutParams(p);
//                layoutCheckTab.addView(textView);
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
//                txtVoltageTotal.setVisibility(View.GONE);
//                txtResistanceTotal.setVisibility(View.VISIBLE);
//            } else{
//                txtResistanceTotal.setVisibility(View.GONE);
//            }
//            batteryCodeAdapter.setCheckType(currentCheckType);
//            layoutCheck.setVisibility(View.GONE);
//        }
//    }

    /**
     * 初始化电池组数据
     */
    private void initBatteryGroup() {
        int index = 0;
        layoutBattery.setMinimumWidth(DisplayUtil.getInstance().getWidth());
        for (final Battery battery : batteryList) {
            ViewHolder holder = new ViewHolder(this, null, R.layout.item_battery_group, false);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            p.weight = 1;
            holder.getRootView().setLayoutParams(p);
            AutoUtils.autoSize(holder.getRootView());
            holder.setText(R.id.battery, battery.name);
            layoutBattery.addView(holder.getRootView());
            holder.getRootView().setTag(battery);
            holder.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cacheBatteryInfor();
                    changeBatteryTab(v);
                }
            });
            if (index == 0)
                changeBatteryTab(holder.getRootView());
            index++;
        }
        if (1 == batteryList.size()) {
            containerHorizantal.setVisibility(View.GONE);
            layoutBattery.setVisibility(View.GONE);
        }
    }

    /**
     * tab切换更换电池组
     */
    private void changeBatteryTab(View view) {
        int tabSize = layoutBattery.getChildCount();
        for (int i = 0; i < tabSize; i++) {
            View tab = layoutBattery.getChildAt(i);
            if (tab.equals(view)) {
                tab.setSelected(true);
                currentBattery = (Battery) view.getTag();

                currentBatterId = currentBattery.bid;
                setBatteryInfor(currentBattery);
                //蓄电池测试默认电压测试，内阻测试默认内阻测试
                if (inspectionType == InspectionType.SBJC_10) {
//                    if (1 == taskExpand.sbjcResistanceCheck)
//                        changeCheckTab(layoutCheckTab.getChildAt(0));
                } else currentCheckType = 1;
            } else tab.setSelected(false);
        }

        int batteryCodeCount = currentBattery.amount;
        batteryCodeList.clear();
        for (int code = 1; code <= batteryCodeCount; code++) {
            if (code < 10) batteryCodeList.add("00" + code);
            else if (code < 100) batteryCodeList.add("0" + code);
            else batteryCodeList.add("" + code);
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
            if (null == group)
                group = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, currentBatterId);
            batteryGroupList.put(currentBatterId, group);
        }
        String zuDuanVoltage = group.zuDuanVoltage==null ? currentBattery.voltage : group.zuDuanVoltage+"";
        String zuDuanElectricity = group.chargeElectricity==null ? currentBattery.dl : group.chargeElectricity+"";
        String singleVoltage = group.singleVoltage==null? currentBattery.singleVoltage : group.singleVoltage+"";
        txtVoltage.setText(zuDuanVoltage);
        txtCurrent.setText(zuDuanElectricity);
        txtSingleVoltage.setText(singleVoltage);
        txtFactVoltage.setText(group.fcdySc == null ? "" : group.fcdySc+"");
        txtSystemVoltage.setText(group.fcdyZlxt == null ? "" : group.fcdyZlxt+"");
        txtLoadElect.setText(group.loadElectricty == null ? "" : group.loadElectricty+"");
        txtFuChongEle.setText(group.fcElectricty == null ? "" : group.fcElectricty+"");
        txtZVoltage.setText(group.zVoltage == null ? "" : group.zVoltage+"");
        txtFVoltage.setText(group.fVoltage == null ? "" : group.fVoltage);

    }

    /**
     * 存储当前的蓄电池的值
     */
    private void cacheBatteryInfor() {
        /*
         * 1、
         * 2、電池組是否存在記錄
         * 3、更改記錄電池
         */
        if (TextUtils.isEmpty(currentBatterId))
            return;
        BatteryGroup batteryGroup = batteryGroupList.get(currentBatterId);
        saveGroupBaseInfor(batteryGroup);

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

    @Override
    protected void onStop() {
        super.onStop();
        cacheBatteryInfor();
    }

    /**
     * 保存当前基础信息
     */
    private void saveGroupBaseInfor(BatteryGroup batteryGroup) {
        batteryGroup.zuDuanVoltage =toDouble(txtVoltage.getText().toString().replace("V", ""));
        batteryGroup.chargeElectricity =toDouble(txtCurrent.getText().toString().replace("A", ""));
        batteryGroup.singleVoltage =toDouble( txtSingleVoltage.getText().toString().replace("V", ""));
        batteryGroup.fcdySc =toDouble( txtFactVoltage.getText().toString());
        batteryGroup.fcdyZlxt =toDouble(  txtSystemVoltage.getText().toString());
        batteryGroup.loadElectricty =toDouble( txtLoadElect.getText().toString());
        batteryGroup.fcElectricty =toDouble( txtFuChongEle.getText().toString());
        batteryGroup.zVoltage =toDouble( txtZVoltage.getText().toString());
        batteryGroup.fVoltage = txtFVoltage.getText().toString();
        try {
            BatteryGroupService.getInstance().saveOrUpdate(batteryGroup);
//            Log.i(TAG, "保存数据成功--蓄电池组基础信息" + currentBatterId);
        } catch (DbException e) {
            e.printStackTrace();
//            Log.i(TAG, "保存数据错误--蓄电池组基础信息" + currentBatterId);
        }
    }
    private Double toDouble(String s)
    {
        if (TextUtils.isEmpty(s.trim())) return null;
        try {
          return   Double.parseDouble(s);
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


//    private void changeCheckTab(View view) {
//        int tabSize = layoutCheckTab.getChildCount();
//        for (int i = 0; i < tabSize; i++) {
//            View tab = layoutCheckTab.getChildAt(i);
//            if (tab.equals(view)) {
//                tab.setSelected(true);
//
//                TranslateAnimation animation = new TranslateAnimation(imageIcon.getLeft(), view.getLeft(), 0, 0);
//                animation.setFillAfter(true);
//                animation.setInterpolator(new AccelerateDecelerateInterpolator());
//                imageIcon.startAnimation(animation);
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
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }

    @Event({R.id.finish_record, R.id.tv_right, R.id.layout_image})
    private void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.finish_record:
//                if (TextUtils.isEmpty(txtTempreture.getText().toString())) {
//                    CToast.showShort(this, "请输入蓄电池温度");
//                    return;
//                }
//                showSureDialog();
                break;
            case R.id.tv_right:
                cacheBatteryInfor();
                saveOrUpdateReport();
                return;

//                if (TextUtils.isEmpty(txtTempreture.getText().toString())) {
//                    CToast.showShort(this, "请输入蓄电池温度");
//                    return;
//                }
//                showSureDialog();
//                break;
            case R.id.layout_image:
                if (baseInforLayout.getVisibility() == View.GONE) {
                    baseInforLayout.setVisibility(View.VISIBLE);
                    ivShow.setBackgroundResource(R.mipmap.icon_up);
                } else {
                    baseInforLayout.setVisibility(View.GONE);
                    ivShow.setBackgroundResource(R.mipmap.icon_down);
                }

                break;
            default:
                break;
        }
    }

    private SureHolder mSureHolder;
    private Dialog mSureDialog;

    private void showSureDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 9 / 10;
        if (mSureHolder == null) {
            mSureHolder = new SureHolder();
        }
        if (mSureDialog == null) {
            mSureDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_tips, mSureHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
        mSureHolder.tvTitle.setText("提示");
        mSureHolder.tvContent.setText("是否完成本次检查");
        mSureDialog.show();
        mSureHolder.btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSureDialog.dismiss();
            }
        });

        mSureHolder.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrUpdateReport();
                mSureDialog.dismiss();
            }
        });

    }

    private void saveOrUpdateReport() {
        currentReport.endtime = DateUtils.getCurrentLongTime();
        currentReport.temperature = txtTempreture.getText().toString();
        currentReport.checkType = taskExpand.sbjcIsAllCheck;
        if (inspectionType == InspectionType.SBJC_10) {
            currentReport.resistanceIsCheck = taskExpand.sbjcResistanceCheck;
        }
        try {
            ReportService.getInstance().saveOrUpdate(currentReport);

            Intent intent = new Intent(this, BatteryFinishActivity.class);
            startActivity(intent);
        } catch (DbException e) {
            Log.e(TAG, "clickEvent: 保存报告出错");
            e.printStackTrace();
        }
    }

    class SureHolder {
        @ViewInject(R.id.tv_dialog_title)
        private TextView tvTitle;
        @ViewInject(R.id.tv_dialog_content)
        private TextView tvContent;

        @ViewInject(R.id.btn_cancel)
        private Button btCancle;
        @ViewInject(R.id.btn_sure)
        private Button btFinish;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_BATTERY_INFO:
                    String batteryCode = data.getStringExtra("batteryCode");
                    ArrayList<String> images = data.getStringArrayListExtra("images");
                    String value = data.getStringExtra("value");
                    if ("1".equalsIgnoreCase(String.valueOf(taskExpand.sbjcIsAllCheck)))
                        saveData(batteryCode, images, value);
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
                        if (selectBatteryRecord.voltageImages.startsWith(","))
                            selectBatteryRecord.voltageImages = selectBatteryRecord.voltageImages.substring(1, selectBatteryRecord.voltageImages.length());
                        else if (selectBatteryRecord.voltageImages.endsWith(","))
                            selectBatteryRecord.voltageImages = selectBatteryRecord.voltageImages.substring(0, selectBatteryRecord.voltageImages.length() - 1);
                    }
                    if (!TextUtils.isEmpty(selectBatteryRecord.resistanceImages)) {
                        if (selectBatteryRecord.resistanceImages.startsWith(","))
                            selectBatteryRecord.resistanceImages = selectBatteryRecord.resistanceImages.substring(1, selectBatteryRecord.resistanceImages.length());
                        else if (selectBatteryRecord.resistanceImages.endsWith(","))
                            selectBatteryRecord.resistanceImages = selectBatteryRecord.resistanceImages.substring(0, selectBatteryRecord.resistanceImages.length() - 1);
                    }
                    if (TextUtils.isEmpty(selectBatteryRecord.voltageImages) && TextUtils.isEmpty(selectBatteryRecord.resistanceImages))
                        batteryImageList.remove(selectBatteryRecord);
                    try {
                        BatteryRecordService.getInstance().saveOrUpdate(selectBatteryRecord);
                        batteryImageAdapter.notifyDataSetChanged();
                    } catch (DbException e) {
                        e.printStackTrace();
                        Log.e(TAG, "保存数据错误");
                    }
                    break;
            }
        }
    }

    private void saveData(String batteryCode, ArrayList<String> imageList, String value) {
        BatteryRecord batteryRecord = batteryRecordMap.get(batteryCode);
        if (null == batteryRecord && (!TextUtils.isEmpty(value) || !imageList.isEmpty()))
            batteryRecord = new BatteryRecord(currentReportId, currentBdzId, currentBdzName, currentBattery.bid, batteryCode, currentInspectionType, taskExpand.sbjcIsAllCheck);
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
        List<String> voltageList = StringUtils.string2List(voltageImages);
        List<String> resistanceList = StringUtils.string2List(resistanceImages);
        voltageList.addAll(resistanceList);
        Intent intent = new Intent(mCurrentActivity, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, 0);
        intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);
        intent.putStringArrayListExtra(Config.IMAGEURL_LIST, StringUtils.addStrToListItem(voltageList, Config.RESULT_PICTURES_FOLDER));
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        startActivityForResult(intent, DELETE_IMAGE);
    }
}