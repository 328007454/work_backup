package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.TaskExtendService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.TaskExtend;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DialogPressAdapter;
import com.cnksi.sjjc.databinding.ActivityAddTaskBinding;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 追加巡检任务
 *
 * @author kkk
 */
public class AddTaskActivity extends BaseSjjcActivity {


    public static final int LOAD_BDZ_DATA = 0x01;
    private String mPressureDetectionType = "";
    private String mPressureDetectionName = "";
    private List<Bdz> mBdzList;

    private InspectionType mInspectionType;

    private Dialog mPowerStationDialog = null;
    private Dialog mPressDialog = null;

    private ListView mPowerStationListView;

    private Bdz mTaskBDZ;

    private TaskExtend taskExpand;
    private ActivityAddTaskBinding mTaskBinding;

    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (radioGroup.getId()) {
                case R.id.radio_ceshi_type:
                    if (R.id.radio_puce == i) {
                        mTaskBinding.radioCeshiType.check(R.id.radio_puce);
                    } else {
                        mTaskBinding.radioCeshiType.check(R.id.radio_chouce);
                    }
                    break;
                case R.id.radio_neizu:
                    if (R.id.neizu_yes == i) {
                        mTaskBinding.radioCeshiType.check(R.id.neizu_yes);
                    } else {
                        mTaskBinding.radioCeshiType.check(R.id.neizu_no);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        loadData();
        initOnclick();
    }

    @Override
    protected View getChildContentView() {
        mTaskBinding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        return mTaskBinding.getRoot();
    }

    public void initView() {

        mTitleBinding.tvTitle.setText(R.string.add_inspection_task_str);
        mTaskBinding.tvInspectionDate.setText(DateUtils.getCurrentShortTime());
        //获取检测类型
        String inspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        mInspectionType = InspectionType.get(inspectionType);
        mTaskBinding.tvSelectInspectionType.setText(mInspectionType.value);
        //如果是压力检测则增加选择类型
        if (mInspectionType.equals(InspectionType.SBJC_06)) {
            mTaskBinding.tvYljc.setVisibility(View.VISIBLE);
            mTaskBinding.typeJianceLlContainer.setVisibility(View.VISIBLE);
            initPressDialog();
        }
        //蓄电池检测添加任务
        if (mInspectionType.equals(InspectionType.SBJC_10)) {
            mTaskBinding.llCeshiContainer.setVisibility(View.VISIBLE);
//            mTaskBinding.llNeizuContainer.setVisibility(View.VISIBLE);
            mTaskBinding.radioCeshiType.setOnCheckedChangeListener(checkedChangeListener);
            mTaskBinding.radioCeshiType.setOnCheckedChangeListener(checkedChangeListener);
        }
        //蓄电池内阻检测添加任务
        if (mInspectionType.equals(InspectionType.SBJC_11)) {
            mTaskBinding.llCeshiContainer.setVisibility(View.VISIBLE);
            mTaskBinding.radioCeshiType.setOnCheckedChangeListener(checkedChangeListener);
        }

    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                String bdzId = PreferencesUtils.get(Config.LAST_CHOOSE_BDZ_NAME_KEY, "");
                if (!TextUtils.isEmpty(bdzId)) {
                    mTaskBDZ = BdzService.getInstance().findById(bdzId);
                }
                mBdzList = BdzService.getInstance().findAll();
                mHandler.sendEmptyMessage(LOAD_BDZ_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });

    }

//    @Event(value = {R.id.btn_back, R.id.btn_cancel, R.id.btn_confirm, R.id.tv_select_power_station, R.id.ibtn_select_inspection_date, R.id.tv_yljc, R.id.bdz_contanier, R.id.type_jiance_ll_container})
//    private void onClick(View v) {
//        switch (v.getId()) {
//            //选择变电站
//            case R.id.bdz_contanier:
//            case R.id.tv_select_power_station:
//                if (null != mPowerStationDialog)
//                    mPowerStationDialog.show();
//                else {
//                    ToastUtils.showMessageLong("变电站为空");
//                }
//                break;
//            //选择检测日期
//            case R.id.ibtn_select_inspection_date:
//
//                DialogUtils.showDatePickerDialog(this, (result, position) -> mTaskBinding.tvInspectionDate.setText(result));
//                break;
//            case R.id.btn_cancel:
//            case R.id.btn_back:
//                onBackPressed();
//                break;
//            //保存数据检测任务
//            case R.id.btn_confirm:
//                saveTask();
//                break;
//            case R.id.type_jiance_ll_container:
//            case R.id.tv_yljc://检测类别
//                if (null != mPressDialog) {
//                    mPressDialog.show();
//                }
//                break;
//        }
//    }

    public void initOnclick() {
        mTaskBinding.bdzContanier.setOnClickListener(v -> {
            if (null != mPowerStationDialog) {
                mPowerStationDialog.show();
            } else {
                ToastUtils.showMessageLong("变电站为空");
            }
        });
        mTaskBinding.tvSelectPowerStation.setOnClickListener(v -> {
            if (null != mPowerStationDialog) {
                mPowerStationDialog.show();
            } else {
                ToastUtils.showMessageLong("变电站为空");
            }
        });
        mTaskBinding.ibtnSelectInspectionDate.setOnClickListener(v -> DialogUtils.showDatePickerDialog(mActivity, (result, position) -> mTaskBinding.tvInspectionDate.setText(result)));
        mTitleBinding.btnBack.setOnClickListener(v -> AddTaskActivity.this.onBackPressed());
        mTaskBinding.btnConfirm.setOnClickListener(v -> AddTaskActivity.this.saveTask());
        mTaskBinding.btnCancel.setOnClickListener(v -> AddTaskActivity.this.onBackPressed());
        mTaskBinding.typeJianceLlContainer.setOnClickListener(v -> {
            if (null != mPressDialog) {
                mPressDialog.show();
            }
        });
        mTaskBinding.tvYljc.setOnClickListener(v -> {
            if (null != mPressDialog) {
                mPressDialog.show();
            }
        });
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            //初始化变电站
            case LOAD_BDZ_DATA:
                if (null != mTaskBDZ) {
                    mTaskBinding.tvSelectPowerStation.setText(mTaskBDZ.name);
                }
                initBDZDialog();
                break;
            default:
                break;
        }
    }

    private void initBDZDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = mBdzList.size() > 8 ? DisplayUtils.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(this, mTaskBinding.llRootContainer, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, mBdzList, R.layout.dialog_content_child_item);
        //设置adapter的listView点击事件
        adapter.setItemClickListener((v, bdz, position) -> {
            if (!bdz.name.contains("未激活")) {
                mTaskBDZ = bdz;
                mTaskBinding.tvSelectPowerStation.setText(bdz.name);
                mPowerStationDialog.dismiss();
            } else {
                ToastUtils.showMessage("该变电站未激活");
            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    private void initPressDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(getApplicationContext(), mTaskBinding.llRootContainer, R.layout.content_list_dialog, false);
        ListView mListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, "请选择压力检测类型");
        final String[] types = {InspectionType.SBJC_06_sf6.name(), InspectionType.SBJC_06_water.name(), InspectionType.SBJC_06_gas.name()};
        final String[] names = {InspectionType.SBJC_06_sf6.value, InspectionType.SBJC_06_water.value, InspectionType.SBJC_06_gas.value};

        DialogPressAdapter adapter = new DialogPressAdapter(this, Arrays.asList(names), R.layout.dialog_content_child_item);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            mPressureDetectionType = types[position];
            mPressureDetectionName = names[position];
            mTaskBinding.tvYljc.setText(mPressureDetectionName);
            mPressDialog.dismiss();
        });
        mPressDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    /**
     * 存储数据检测任务
     */
    private void saveTask() {
        Task task;
        if (null == mTaskBDZ) {
            ToastUtils.showMessage(R.string.please_select_power_station_str);
            return;
        }

        String inspectionDate = mTaskBinding.tvInspectionDate.getText().toString().trim();
        if (TextUtils.isEmpty(inspectionDate)) {
            ToastUtils.showMessage(R.string.please_select_inspection_date_str);
            return;
        }
        if (!DateUtils.compareDate(inspectionDate, DateUtils.getCurrentShortTime(), DateUtils.yyyy_MM_dd)) {
            ToastUtils.showMessage(R.string.please_select_after_today_date_str);
            mTaskBinding.tvInspectionDate.setText("");
            return;
        }

        if (mInspectionType.equals(InspectionType.SBJC_06)) {
            if (TextUtils.isEmpty(mPressureDetectionType)) {
                ToastUtils.showMessage("请选择压力检测类型");
                return;
            }
            int todaySameTaskCount = TaskService.getInstance().getCountSameTaks(mPressureDetectionType, mTaskBDZ.bdzid, inspectionDate);
            if (todaySameTaskCount > 0) {
                ToastUtils.showMessage("计划列表中已经有该任务了，请返回查看哟！");
                return;
            }
            task = new Task(UUID.randomUUID().toString(), mTaskBDZ.bdzid, mTaskBDZ.name, mPressureDetectionType, mPressureDetectionName,
                    DateUtils.getFormatterTime(inspectionDate + " 00:00:00", DateUtils.yyyy_MM_dd_HH_mm_ss),
                    TaskStatus.undo.name());
        } else {
            int todaySameTaskCount = TaskService.getInstance().getCountSameTaks(mInspectionType.name(), mTaskBDZ.bdzid, inspectionDate);
            if (todaySameTaskCount > 0) {
                ToastUtils.showMessage("计划列表中已经有该任务了，请返回查看哟！");
                return;
            }
            task = new Task(UUID.randomUUID().toString(), mTaskBDZ.bdzid, mTaskBDZ.name, mInspectionType.name(), mInspectionType.value,
                    DateUtils.getFormatterTime(inspectionDate + " 00:00:00", DateUtils.yyyy_MM_dd_HH_mm_ss),
                    TaskStatus.undo.name());
            if (mInspectionType.equals(InspectionType.SBJC_10)) {
                taskExpand = new TaskExtend(task.taskid);
                if (mTaskBinding.neizuYes.isChecked()) {
                    taskExpand.sbjcResistanceCheck = 1;
                } else if (mTaskBinding.neizuNo.isChecked()) {
                    taskExpand.sbjcResistanceCheck = 0;
                }
                if (mTaskBinding.radioChouce.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 1;
                } else if (mTaskBinding.radioPuce.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 0;
                }

            }

            //蓄电池内阻测试检测
            if (mInspectionType.equals(InspectionType.SBJC_11)) {
                taskExpand = new TaskExtend(task.taskid);
                if (mTaskBinding.radioChouce.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 1;
                } else if (mTaskBinding.radioPuce.isChecked()) {
                    taskExpand.sbjcIsAllCheck = 0;
                }
            }
        }
        try {
            TaskService.getInstance().saveOrUpdate(task);
            if (taskExpand != null) {
                TaskExtendService.getInstance().saveOrUpdate(taskExpand);
            }
            PreferencesUtils.put(Config.LAST_CHOOSE_BDZ_NAME_KEY, mTaskBDZ.bdzid);
            setResult(RESULT_OK);
            this.finish();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


}
