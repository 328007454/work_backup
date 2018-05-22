package com.cnksi.bdzinspection.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.TaskService;
import com.cnksi.bdzinspection.databinding.XsActivityInspectionTaskRemindBinding;
import com.cnksi.bdzinspection.fragment.TaskRemindFragment;
import com.cnksi.bdzinspection.fragment.TaskRemindFragment.OnFragmentEventListener;
import com.cnksi.bdzinspection.inter.GrantPermissionListener;
import com.cnksi.bdzinspection.model.InspectionPrepared;
import com.cnksi.bdzinspection.model.ReportSignname;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.bdzinspection.utils.PermissionUtil;
import com.cnksi.bdzinspection.ywyth.YWWorkflowActivity;
import com.cnksi.bdzinspection.ywyth.YunweiReportActivity;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.sync.KSyncConfig;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cnksi.ksynclib.KSync.SYNC_ERROR;
import static com.cnksi.ksynclib.KSync.SYNC_INFO;
import static com.cnksi.ksynclib.KSync.SYNC_START;
import static com.cnksi.ksynclib.KSync.SYNC_SUCCESS;


/**
 * 任务提醒界面
 *
 * @author terry on 2017/10/19
 */
@SuppressLint("HandlerLeak")
public class TaskRemindActivity extends BaseActivity implements OnPageChangeListener, OnFragmentEventListener, GrantPermissionListener {

    public static final int ADD_TASK_REQUEST_CODE = 0x112;
    private static final String SWITCH_MAINTANCE_MODEL = "complex";
    private static final String MAINTENANCE_XDCDYJC = "maintenance_xdcdyjc";
    private static final String SBJC_10 = "SBJC_10";
    String[] functionModelArray = {Config.UNFINISH_MODEL, Config.FINISHED_MODEL, Config.ALL_TASK_MODEL, Config.OVER_DUE_MODEL};
    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<TaskRemindFragment> mFragmentList = null;
    private String[] titleArray = null;
    /**
     * 主菜单界面点击的巡检类型
     */
    private InspectionType currentSelectInspectionType;
    /**
     * 当前选中的position
     */
    private int currentSelectedPosition = 0;
    private boolean containSwitchOverMaintance = false;
    XsActivityInspectionTaskRemindBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String taskId = getIntent().getStringExtra("task_id");
        String deparmentId = getIntent().getStringExtra(Config.CURRENT_DEPARTMENT_ID);
        currentDepartmentName = getIntent().getStringExtra(Config.CURRENT_DEPARTMENT_NAME);
        Config.SYNC_URL_VALUE = getIntent().getStringExtra(Config.KEY_SYNC_URL);
        Config.SYNC_APP_ID_VALUE = getIntent().getStringExtra(Config.KEY_SYNC_APP_ID);
        PreferencesUtils.put(currentActivity, Config.CURRENT_DEPARTMENT_ID, deparmentId);
        PreferencesUtils.put(currentActivity, Config.CURRENT_DEPARTMENT_NAME, currentDepartmentName);
        if (!TextUtils.isEmpty(taskId)) {
            Task task = TaskService.getInstance().findById(taskId);
            if (task != null) {
                startTask(task);
                finish();
                return;
            } else {
                CToast.showShort(currentActivity, "没有找到对应的任务！！");
            }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_inspection_task_remind);
//        setContentView(R.layout.xs_activity_inspection_task_remind);
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, Config.permissions);
        initialUI();
        initOnClick();
    }

    private void initialUI() {
        getIntentValue();
        binding.includeTitle.ibtnExit.setVisibility(View.VISIBLE);
        binding.includeTitle.ibtnExit.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.xs_icon_sync_selector));
        String inspectionName = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        // 防止由于系统垃圾回收导致其他界面返回获取不到当前巡检类型报错
        if (!TextUtils.isEmpty(inspectionName)) {
            PreferencesUtils.put(currentActivity, Config.CURRENT_INSPECTION_TYPE_NAME, inspectionName);
        } else {
            PreferencesUtils.getString(currentActivity, Config.CURRENT_INSPECTION_TYPE_NAME, "full");
            inspectionName = InspectionType.full.value.toString();
        }
        currentSelectInspectionType = InspectionType.get(inspectionName);
        containSwitchOverMaintance = currentSelectInspectionType.name().contains("switchover") || currentSelectInspectionType.name().contains("maintenance");
        if (containSwitchOverMaintance) {
            binding.includeTitle.ibtnExit.setVisibility(View.GONE);
        }
        binding.includeTitle.tvTitle.setText(R.string.xs_inspection_task_remind_str);
        titleArray = getResources().getStringArray(R.array.XS_TaskTitleArray);
        initFragmentList();
    }

    private void initFragmentList() {
        mFragmentList = new ArrayList<>();
        for (int i = 0; i < titleArray.length; i++) {
            TaskRemindFragment mTaskFragment = new TaskRemindFragment();
            mTaskFragment.setOnFragmentEventListener(this);
            Bundle args = new Bundle();
            args.putString(Config.CURRENT_FUNCTION_MODEL, functionModelArray[i]);
            args.putString(Config.CURRENT_INSPECTION_TYPE_NAME, currentSelectInspectionType.name());
            mTaskFragment.setArguments(args);
            mFragmentList.add(mTaskFragment);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArray));
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.tabStrip.setOnPageChangeListener(this);
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(4);

    }

    public void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.includeTitle.ibtnExit.setOnClickListener(view -> {
            CustomerDialog.showProgress(currentActivity, "正在同步任务...", true, false);
            KSyncConfig.getInstance().getKNConfig(currentActivity, mHandler).upload();
        });
        binding.addTask.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, AddTaskActivity.class);
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, currentSelectInspectionType.name());
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentSelectedPosition = position;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_TASK_REQUEST_CODE:
                    updateTaskStatus();
                    binding.viewPager.setCurrentItem(0, false);
                    break;
                case Config.TURN_SUCCESS:
                    updateTaskStatus();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            String type = intent.getStringExtra(Config.CURRENT_INSPECTION_TYPE);
            if (!TextUtils.isEmpty(type) && type.equals(SBJC_10)) {
                isNeedUpdateTaskStatus = true;
            }
            if (intent.hasExtra(Config.CURRENT_INSPECTION_TYPE_NAME)) {
                setIntent(intent);
                initialUI();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedUpdateTaskStatus) {
            updateTaskStatus();
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case SYNC_START://开始同步
                String messageStart = (String) msg.obj;
                if (messageStart.contains("开始上传数据")) {
                    CToast.showShort(currentActivity, "开始同步数据");
                }
                break;
            case SYNC_INFO:
                break;
            case SYNC_SUCCESS://同步成功后刷新列表界面
                String messageSuccess = (String) msg.obj;
                Log.d("Tag", messageSuccess);
                if (messageSuccess.contains("上传完成")) {
                    KSyncConfig.getInstance().getKNConfig(currentActivity, mHandler).downLoad();
                } else if (messageSuccess.contains("同步完成")) {
                    CToast.showShort(currentActivity, "数据同步完成");
                    CustomerDialog.dismissProgress();
                    for (TaskRemindFragment taskRemindFragment : mFragmentList) {
                        taskRemindFragment.searchData();
                    }
                }

                break;
            case SYNC_ERROR://同步错误
                String messageError = (String) msg.obj;
                CToast.showShort(currentActivity, messageError);
                CustomerDialog.dismissProgress();
                for (TaskRemindFragment taskRemindFragment : mFragmentList) {
                    taskRemindFragment.searchData();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void updateTaskStatus() {
        for (int i = 0, count = mFragmentList.size(); i < count; i++) {
            if (i == currentSelectedPosition) {
                mFragmentList.get(i).isRepeatLoad = true;
            } else {
                mFragmentList.get(i).setIsFirstLoad(true);
            }
            mFragmentList.get(i).updateTask(true);
        }
        isNeedUpdateTaskStatus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void allPermissionsGranted() {
        PreferencesUtils.put(this, Config.PERMISSION_STASTUS, true);
        // 检测是否进行版本升级
//        checkUpdateVersion(Config.DOWNLOAD_APP_FOLDER, Config.PCODE);
        SystemConfig.init();
    }

    @Override
    public void startTask(Task mTask) {
        Bdz bdz = null;
        try {
            bdz = XunshiApplication.getDbUtils().selector(Bdz.class).where(Bdz.BDZID, "=", mTask.bdzid).findFirst();
            // 创建变电站下目录
            if (!FileUtils.isFileExists(Config.BDZ_INSPECTION_FOLDER + bdz.folder)) {
                FileUtils.initFile(new String[]{Config.BDZ_INSPECTION_FOLDER + bdz.folder});
            }
            PreferencesUtils.put(currentActivity, Config.PICTURE_PREFIX, bdz.folder + "/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        if (mTask.isFinish()) {
            Report mReport = null;
            try {
                mReport = XunshiApplication.getDbUtils().selector(Report.class).where(Report.TASK_ID, "=", mTask.taskid).findFirst();
                if (mReport == null) {
                    CToast.showShort(currentActivity, "异常任务，找不到对应的报告！");
                    return;
                }
                PreferencesUtils.put(currentActivity, Config.CURRENT_REPORT_ID, mReport.reportid);
            } catch (DbException e) {
                e.printStackTrace();
            }
            intent.putExtra(Config.IS_FROM_TASK_REMIND, true);
            if (!TextUtils.isEmpty(mTask.type) && Config.InspectionType.operation.name().equalsIgnoreCase(mTask.type)) {
                // TODO:跳转到运维一体化报告界面
                intent.setClass(currentActivity, YunweiReportActivity.class);
            } else {
                // 蓄电池电压检测,跳转数据监测蓄电池检测
                if (MAINTENANCE_XDCDYJC.equals(mTask.inspection)) {
                    intent = new Intent();
                    ComponentName componentName = new ComponentName("com.cnksi.sjjc", "com.cnksi.sjjc.activity.batteryactivity.BatteryTestReportActivity");
                    intent.putExtra(Config.CURRENT_BDZ_ID, mTask.bdzid);
                    intent.putExtra(Config.CURRENT_TASK_ID, mTask.taskid);
                    intent.putExtra(Config.CURRENT_REPORT_ID, mReport.reportid);
                    intent.putExtra(Config.CURRENT_BDZ_NAME, mTask.bdzname);
                    intent.putExtra(Config.CURRENT_INSPECTION_TYPE, mTask.inspection);
                    intent.putExtra(Config.CURRENT_FILENAME, PreferencesUtils.getString(currentActivity, Config.PICTURE_PREFIX, ""));
                    intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, mTask.inspection_name);
                    intent.putExtra(Config.IS_FROM_SJJC, getIntent().getBooleanExtra(Config.IS_FROM_SJJC, false));
                    intent.setComponent(componentName);
                }
                if (mTask.inspection.contains(InspectionType.switchover.name()) || mTask.inspection.contains(InspectionType.maintenance.name())) {
                    intent.setClass(currentActivity, NewRegularReportActivity.class);
                } else {
                    intent.setClass(currentActivity, InspectionReportActivity.class);
                }
            }
        } else {
            if (Config.InspectionType.operation.name().equalsIgnoreCase(mTask.type)) {
                // 运维一体化
                intent.setClass(currentActivity, YWWorkflowActivity.class);
                generateReport(mTask);
            } else {
                // 定期切换试验、定期维护不显示巡检前准备的界面；
                // 保护压板及定值核对
                //开始任务时就生成报告 不然各处引用报告的 问题太多
                generateReport(mTask);
                if (mTask.inspection.contains(InspectionType.switchover.name()) || mTask.inspection.contains(InspectionType.maintenance.name()) || mTask.inspection.equalsIgnoreCase(InspectionType.battery.name())) {
                    if (SystemConfig.getSwitchMenuConfirmStyle().equalsIgnoreCase(SystemConfig.NONE)) {
                        intent.setClass(currentActivity, New1RegularSwitchActivity1.class);
                    } else {
                        intent.setClass(currentActivity, New1RegularSwitchActivity2.class);
                    }
                } else {
                    boolean containsUser = false;
                    String reportId = null;
                    List<InspectionPrepared> prepareds = null;
                    try {
                        prepareds = XunshiApplication.getDbUtils().selector(InspectionPrepared.class).where(InspectionPrepared.TASKID, "=", mTask.taskid).findAll();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (null != prepareds) {
                        currentAcounts = PreferencesUtils.getString(currentActivity, Config.CURRENT_LOGIN_ACCOUNT, "");
                        String[] accounts = currentAcounts.split(",");
                        for (InspectionPrepared prepared : prepareds) {
                            if (prepared.contains(accounts)) {
                                reportId = prepared.reportID;
                                containsUser = true;
                                break;
                            }
                        }
                    }
                    if (containsUser && !TextUtils.isEmpty(reportId)) {
                        PreferencesUtils.put(currentActivity, Config.CURRENT_REPORT_ID, reportId);
                        if (mTask.inspection.contains("special")) {
                            intent.setClass(currentActivity, ParticularDeviceListActivity.class);
                        } else {
                            intent.setClass(currentActivity, FullDeviceListActivity.class);
                        }
                    } else {
                        intent.setClass(currentActivity, InspectionReadyActivity.class);
                    }
                }
            }
        }
        PreferencesUtils.put(currentActivity, Config.CURRENT_INSPECTION_TYPE, mTask.inspection);
        PreferencesUtils.put(currentActivity, Config.CURRENT_INSPECTION_TYPE_NAME, mTask.inspection_name);
        PreferencesUtils.put(currentActivity, Config.CURRENT_BDZ_ID, mTask.bdzid);
        PreferencesUtils.put(currentActivity, Config.CURRENT_BDZ_NAME, mTask.bdzname);
        PreferencesUtils.put(currentActivity, Config.CURRENT_TASK_ID, mTask.taskid);
        PreferencesUtils.put(currentActivity, Task.SCHEDULE_TIME, mTask.schedule_time);
        PreferencesUtils.put(currentActivity, Task.SCHEDULE_ENDTIME, mTask.schedule_endtime);
        currentActivity.startActivityForResult(intent, Config.TURN_SUCCESS);
    }


    /**
     * 生成报告
     * <p>
     * 1.先查询任务对应的报告是否存在，不存在新生成一个报告
     */
    private void generateReport(Task mTask) {
        try {
            Report mReport = XunshiApplication.getDbUtils().selector(Report.class).where(Report.TASK_ID, "=", mTask.taskid).findFirst();
            String loginPerson = PreferencesUtils.getString(currentActivity, Config.CURRENT_LOGIN_USER, "");
            if (mReport == null) {
                mReport = new Report(mTask.taskid, mTask.bdzid, mTask.bdzname, mTask.inspection, loginPerson);
            }
            mReport.setOtherReport(mTask.bdzid, mTask.bdzname, mTask.inspection, loginPerson);
            String reportSwitchId = PreferencesUtils.getString(currentActivity, mTask.inspection + "_" + mTask.bdzid, "");
            mReport.repSwithoverId = reportSwitchId;
            mReport.inspectionValue = mTask.inspection_name;
            mReport.reportSource = Config.REPORT;
            mReport.departmentId = PreferencesUtils.getString(currentActivity, Config.CURRENT_DEPARTMENT_ID, "");
            XunshiApplication.getDbUtils().saveOrUpdate(mReport);

            ReportSignname reportSignname = XunshiApplication.getDbUtils().selector(ReportSignname.class).where(ReportSignname.REPORTID, "=", mReport.reportid).findFirst();
            if (reportSignname == null) {
                String currentAcounts = PreferencesUtils.getString(currentActivity, Config.CURRENT_LOGIN_ACCOUNT, "");
                List<DbModel> defaultUesrs = DepartmentService.getInstance().findUserForCurrentUser(currentAcounts);
                for (DbModel dbModel : defaultUesrs) {
                    reportSignname = new ReportSignname(mReport.reportid, Config.Role.worker.name(), dbModel);
                    XunshiApplication.getDbUtils().saveOrUpdate(reportSignname);
                }
            }
            if (mReport != null) {
                PreferencesUtils.put(currentActivity, Config.CURRENT_REPORT_ID, mReport.reportid);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
