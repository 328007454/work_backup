package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.adapter.DialogPressAdapter;
import com.cnksi.sjjc.bean.Bdz;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskExtend;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.BdzService;
import com.cnksi.sjjc.service.TaskExtendService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.DialogUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 追加巡检任务
 */
public class AddTaskActivity extends BaseActivity {

    public static final int LOAD_BDZ_DATA = 0x01;

    @ViewInject(R.id.ll_root_container)
    private LinearLayout mLLRootContainer;
    //测试分类
    @ViewInject(R.id.ll_ceshi_container)
    private LinearLayout mCeShiContainer;
    //是否测试内阻容器
    @ViewInject(R.id.ll_neizu_container)
    private LinearLayout mNeiZuContainer;
    //测试类型组
    @ViewInject(R.id.radio_ceshi_type)
    private RadioGroup radioGroupCeShi;
    //内阻测试
    @ViewInject(R.id.radio_ceshi_type)
    private RadioGroup radioGroupNeiZu;

    //内阻测试是
    @ViewInject(R.id.neizu_yes)
    private RadioButton rbNeiYes;
    //内阻测试否
    @ViewInject(R.id.neizu_no)
    private RadioButton rbNeiZuNo;
    //测试分类普测
    @ViewInject(R.id.radio_puce)
    private RadioButton rbPuCe;
    //ce是分类抽测
    @ViewInject(R.id.radio_chouce)
    private RadioButton rbChouCe;


    @ViewInject(R.id.tv_inspection_date)
    private TextView mTvInspectionDate;

    @ViewInject(R.id.tv_select_power_station)
    private TextView mTvBDZ;

    @ViewInject(R.id.tv_select_inspection_type)
    private TextView mTvInspectionType;
    @ViewInject(R.id.tv_yljc)
    private TextView mTvPressureDetection;

    @ViewInject(R.id.type_jiance_ll_container)
    private LinearLayout typeLinearLayout;
    private String mPressureDetectionType = "";
    private String mPressureDetectionName = "";
    private List<Bdz> mBdzList;

    private InspectionType mInspectionType;

    private Dialog mPowerStationDialog = null;
    private Dialog mPressDialog = null;

    private ListView mPowerStationListView;

    private Bdz mTaskBDZ;

    private TaskExtend taskExpand;

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (radioGroup.getId()) {
                case R.id.radio_ceshi_type:
                    if (R.id.radio_puce == i) {
                        radioGroupCeShi.check(R.id.radio_puce);
                    } else {
                        radioGroupCeShi.check(R.id.radio_chouce);
                    }
                    break;
                case R.id.radio_neizu:
                    if (R.id.neizu_yes == i) {
                        radioGroupNeiZu.check(R.id.neizu_yes);
                    } else {
                        radioGroupNeiZu.check(R.id.neizu_no);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_add_task);

        initUI();

        initData();
    }

    private void initUI() {
        tvTitle.setText(R.string.add_inspection_task_str);
        mTvInspectionDate.setText(DateUtils.getCurrentShortTime());
        //获取检测类型
        String inspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        mInspectionType = InspectionType.get(inspectionType);
        mTvInspectionType.setText(mInspectionType.value);
        //如果是压力检测则增加选择类型
        if (mInspectionType.equals(InspectionType.SBJC_06)) {
            mTvPressureDetection.setVisibility(View.VISIBLE);
            typeLinearLayout.setVisibility(View.VISIBLE);
            initPressDialog();
        }
        //蓄电池检测添加任务
        if (mInspectionType.equals(InspectionType.SBJC_10)) {
            mCeShiContainer.setVisibility(View.VISIBLE);
//            mNeiZuContainer.setVisibility(View.VISIBLE);
            radioGroupNeiZu.setOnCheckedChangeListener(checkedChangeListener);
            radioGroupCeShi.setOnCheckedChangeListener(checkedChangeListener);
        }
        //蓄电池内阻检测添加任务
        if (mInspectionType.equals(InspectionType.SBJC_11)) {
            mCeShiContainer.setVisibility(View.VISIBLE);
            radioGroupCeShi.setOnCheckedChangeListener(checkedChangeListener);
        }

    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    String bdzId = PreferencesUtils.getString(_this, Config.LASTTIEM_CHOOSE_BDZNAME, "");
                    if (!TextUtils.isEmpty(bdzId)) {
                        mTaskBDZ = BdzService.getInstance().findById(bdzId);
                    }
                    mBdzList = BdzService.getInstance().findAll();
                    mHandler.sendEmptyMessage(LOAD_BDZ_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Event(value = {R.id.btn_back, R.id.btn_cancel, R.id.btn_confirm, R.id.tv_select_power_station, R.id.ibtn_select_inspection_date, R.id.tv_yljc, R.id.bdz_contanier, R.id.type_jiance_ll_container})
    private void onClick(View v) {
        switch (v.getId()) {
            //选择变电站
            case R.id.bdz_contanier:
            case R.id.tv_select_power_station:
                if (null != mPowerStationDialog)
                    mPowerStationDialog.show();
                else
                    CToast.showLong(_this, "变电站为空");
                break;
            //选择检测日期
            case R.id.ibtn_select_inspection_date:

                DialogUtils.showDatePickerDialog(this, new DialogUtils.DialogItemClickListener() {
                    @Override
                    public void confirm(String result, int position) {
                        mTvInspectionDate.setText(result);
                    }
                });
                break;
            case R.id.btn_cancel:
            case R.id.btn_back:
                onBackPressed();
                break;
            //保存数据检测任务
            case R.id.btn_confirm:
                saveTask();
                break;
            case R.id.type_jiance_ll_container:
            case R.id.tv_yljc://检测类别
                if (null != mPressDialog) {
                    mPressDialog.show();
                }
                break;
        }
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            //初始化变电站
            case LOAD_BDZ_DATA:
                if (null != mTaskBDZ)
                    mTvBDZ.setText(mTaskBDZ.name);
                initBDZDialog();
                break;
        }
    }

    private void initBDZDialog() {
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = mBdzList.size() > 8 ? DisplayUtil.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(this, mLLRootContainer, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, mBdzList, R.layout.dialog_content_child_item);
        //设置adapter的listView点击事件
        adapter.setItemClickListener(new ItemClickListener<Bdz>() {
            @Override
            public void itemClick(View v, Bdz bdz, int position) {
                if (!bdz.name.contains("未激活")) {
                    mTaskBDZ = bdz;
                    mTvBDZ.setText(bdz.name);
                    mPowerStationDialog.dismiss();
                } else
                    CToast.showShort(_this, "该变电站未激活");
            }

            @Override
            public void itemLongClick(View v, Bdz bdz, int position) {

            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    private void initPressDialog() {
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(this, mLLRootContainer, R.layout.content_list_dialog, false);
        ListView mListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, "请选择压力检测类型");
        final String[] types = {InspectionType.SBJC_06_sf6.name(), InspectionType.SBJC_06_water.name(), InspectionType.SBJC_06_gas.name()};
        final String[] names = {InspectionType.SBJC_06_sf6.value, InspectionType.SBJC_06_water.value, InspectionType.SBJC_06_gas.value};

        DialogPressAdapter adapter = new DialogPressAdapter(this, Arrays.asList(names), R.layout.dialog_content_child_item);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPressureDetectionType = types[position];
                mPressureDetectionName = names[position];
                mTvPressureDetection.setText(mPressureDetectionName);
                mPressDialog.dismiss();
            }
        });
        mPressDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    /**
     * 存储数据检测任务
     */
    private void saveTask() {
        Task task;
        if (null == mTaskBDZ) {
            CToast.showShort(this, R.string.please_select_power_station_str);
            return;
        }

        String inspectionDate = mTvInspectionDate.getText().toString().trim();
        if (TextUtils.isEmpty(inspectionDate)) {
            CToast.showShort(this, R.string.please_select_inspection_date_str);
            return;
        }
        if (!DateUtils.compareDate(inspectionDate, DateUtils.getCurrentShortTime(), Config.dateFormat1)) {
            CToast.showShort(this, R.string.please_select_after_today_date_str);
            mTvInspectionDate.setText("");
            return;
        }

        if (mInspectionType.equals(InspectionType.SBJC_06)) {
            if (TextUtils.isEmpty(mPressureDetectionType)) {
                CToast.showShort(this, "请选择压力检测类型");
                return;
            }
            task = new Task(UUID.randomUUID().toString(), mTaskBDZ.bdzid, mTaskBDZ.name, mPressureDetectionType, mPressureDetectionName,
                    DateUtils.getFormatterTime(inspectionDate + " 00:00:00", Config.dateFormat2),
                    Task.TaskStatus.undo.name());
        } else {
            task = new Task(UUID.randomUUID().toString(), mTaskBDZ.bdzid, mTaskBDZ.name, mInspectionType.name(), mInspectionType.value,
                    DateUtils.getFormatterTime(inspectionDate + " 00:00:00", Config.dateFormat2),
                    Task.TaskStatus.undo.name());
            if (mInspectionType.equals(InspectionType.SBJC_10)) {
                taskExpand = new TaskExtend(task.taskid);
                if (rbNeiYes.isChecked()) {
                    taskExpand.sbjcResistanceCheck = 1;
                } else if (rbNeiZuNo.isChecked()) {
                    taskExpand.sbjcResistanceCheck = 0;
                }
                if (rbChouCe.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 1;
                } else if (rbPuCe.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 0;
                }
            }

            //蓄电池内阻测试检测
            if (mInspectionType.equals(InspectionType.SBJC_11)) {
                taskExpand = new TaskExtend(task.taskid);
                if (rbChouCe.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 1;
                } else if (rbPuCe.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 0;
                }
            }
        }
        try {
            TaskService.getInstance().saveOrUpdate(task);
            if (taskExpand != null) {
                TaskExtendService.getInstance().saveOrUpdate(taskExpand);
            }
            PreferencesUtils.put(_this, Config.LASTTIEM_CHOOSE_BDZNAME, mTaskBDZ.bdzid);
            setResult(RESULT_OK);
            this.finish();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
