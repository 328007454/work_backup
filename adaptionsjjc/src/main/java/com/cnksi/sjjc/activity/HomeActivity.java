package com.cnksi.sjjc.activity;


import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BdzAdapter;
import com.cnksi.sjjc.adapter.DefectAdapter;
import com.cnksi.sjjc.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.adapter.HomeSafetyToolAdapter;
import com.cnksi.sjjc.adapter.HomeTaskItemAdapter;
import com.cnksi.sjjc.adapter.ViewHolder;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.bean.Bdz;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.ActivityHomePageBinding;
import com.cnksi.sjjc.databinding.BdzPopwindowBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.DefectRecordService;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.PlacedService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.ActivityUtil;
import com.cnksi.sjjc.util.AppUtils;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.TTSUtils;
import com.cnksi.sjjc.view.Banner;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页界面
 *
 * @author han on 2017/3/24.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, ItemClickListener {
    private ActivityHomePageBinding homePageBinding;
    //变电站弹出popwindow
    private BdzPopwindowBinding bdzPopwindowBinding;
    //广告banner图片集合
    private ArrayList<Integer> bannerMapUrl = new ArrayList<>();
    //变电站适配器
    private BdzAdapter bdzAdapter;
    private PopupWindow mPop;
    private List<Bdz> bdzList = new ArrayList<>();
    //缺陷适配器
    private DefectAdapter defectAdapter;
    //危急、严重、一般缺陷集合
    private Map<String, ArrayList<DefectRecord>> mCrisisMap = new HashMap<>();
    private Map<String, ArrayList<DefectRecord>> mSerioutMap = new HashMap<>();
    private Map<String, ArrayList<DefectRecord>> mCommonMap = new HashMap<>();

    //当前选中的变电站id
    private String currentSelectBdzId;
    //主页任务适配器
    private HomeTaskItemAdapter taskItemAdapter;
    private HomeSafetyToolAdapter safetyToolAdapter;
    //变电站listview
    private ListView mPowerStationListView;
    //变电站弹出对话框
    private Dialog mPowerStationDialog = null;
    private AppVersion remoteSjjcAppVersion;
    private AppVersion remoteXunshiAppVersion;
    long time1;
    private static final String TAG = "Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDefaultTitle = false;
        super.onCreate(savedInstanceState);
        homePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);
        changedStatusColor();
        checkIsNeedSync();
        initUpdateSystem();
        inUI();
        initTabs();
        TTSUtils.getInstance().startSpeaking(String.format("欢迎使用%1$s", getString(R.string.app_name)));
        ExecutorManager.executeTaskSerially(() -> {
            DeviceService.getInstance().refreshDeviceHasCopy();
            try {
                CustomApplication.getInstance().getDbManager().execNonQuery("create  index  if not exists index_bdzid_deviceid on copy_result(bdzid,deviceid)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create  index  if not exists index_bdzid on copy_result(bdzid)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create index if not exists 'index_bdzid' on copy_item(bdzid)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create index if not exists  'report_deviceid' on defect_record (`reportid`, `deviceid`)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create  index  if not exists spacing_index on spacing(bdzid)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create  index  if not exists  index_spic_deviceid_type on device(bdzid,spid,device_type)");
                CustomApplication.getInstance().getDbManager().execNonQuery("create  index  if not exists  index_kind on standard_special(kind)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void initUI() {

    }

    private void initUpdateSystem() {
        ExecutorManager.executeTaskSerially(() -> {
            String apkPath = "";
            //增加下载APK文件夹
            SqlInfo info1 = new SqlInfo("select short_name_pinyin from city");
            try {
                PackageInfo info = AppUtils.getLocalPackageInfo(getApplicationContext());
                int version = info.versionCode;
                PackageManager manager = _this.getPackageManager();
                PackageInfo infoXunshi = manager.getPackageInfo("com.cnksi.bdzinspection", 0);
                remoteSjjcAppVersion = CustomApplication.getInstance().getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                remoteXunshiAppVersion = CustomApplication.getInstance().getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code > '" + infoXunshi.versionCode + "'").expr("and file_name like '%xunshi%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                DbModel model = CustomApplication.getInstance().getDbManager().findDbModelFirst(info1);
                if (model != null) {
                    apkPath = Config.BDZ_INSPECTION_FOLDER + "admin/" + model.getString("short_name_pinyin") + "/apk";
                }
            } catch (Exception e) {
                e.printStackTrace();
                apkPath = Config.DOWNLOAD_APP_FOLDER;
            }
            if (remoteSjjcAppVersion != null && remoteXunshiAppVersion != null && !PreferencesUtils.get(AppUtils.IS_SJJC_AREADY_UPDATE, false)) {
//TODO:
            } else if (remoteXunshiAppVersion != null && remoteSjjcAppVersion == null && PreferencesUtils.get(AppUtils.IS_SJJC_AREADY_UPDATE, false)) {
                checkUpdateVersion(apkPath, Config.PCODE, true, "");
            } else if (remoteXunshiAppVersion == null || remoteSjjcAppVersion == null) {
                PreferencesUtils.put(AppUtils.IS_SJJC_AREADY_UPDATE, false);
                checkUpdateVersion(apkPath, Config.PCODE, true, "");
            }
        });

    }

    private void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                transformDefectType();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initBDZDialog();
                        if (bdzAdapter == null) {
                            bdzAdapter = new BdzAdapter(_this, bdzList, R.layout.dialog_content_child_item);
                            bdzPopwindowBinding.lvBzd.setAdapter(bdzAdapter);
                        } else
                            bdzAdapter.setList(bdzList);
                        if (!bdzList.isEmpty() && TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, "")))
                            homePageBinding.bdzName.setText(bdzList.get(0).name);
                        loadDefect();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void transformDefectType() {
        try {
            mCrisisMap.clear();
            mCommonMap.clear();
            mSerioutMap.clear();
            bdzList.clear();
            bdzList = CustomApplication.getInstance().getDbManager().findAll(Bdz.class);
            final List<DefectRecord> defectList = DefectRecordService.getInstance().queryCurrentBdzExistDefectList();
            for (DefectRecord mDefectRecord : defectList) {
                ArrayList<DefectRecord> temp;
                if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                    if (null == (temp = mCrisisMap.get(mDefectRecord.bdzid))) {
                        temp = new ArrayList<>();
                        mCrisisMap.put(mDefectRecord.bdzid, temp);
                    }
                    temp.add(mDefectRecord);
                } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                    if (null == (temp = mSerioutMap.get(mDefectRecord.bdzid))) {
                        temp = new ArrayList<>();
                        mSerioutMap.put(mDefectRecord.bdzid, temp);
                    }
                    temp.add(mDefectRecord);
                } else if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                    if (null == (temp = mCommonMap.get(mDefectRecord.bdzid))) {
                        temp = new ArrayList<>();
                        mCommonMap.put(mDefectRecord.bdzid, temp);
                    }
                    temp.add(mDefectRecord);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void inUI() {
        bannerMapUrl.add(R.mipmap.banner1);
        bannerMapUrl.add(R.mipmap.banner2);
        bannerMapUrl.add(R.mipmap.banner3);
        homePageBinding.banner.setFocusable(false);
        homePageBinding.banner.setBannerStyle(Banner.BannerConfig.CIRCLE_INDICATOR);
        homePageBinding.banner.setIndicatorGravity(Banner.BannerConfig.CENTER);
        homePageBinding.banner.setImages(bannerMapUrl);
        homePageBinding.homeInclude.deviceXunshi.setOnClickListener(this);
        homePageBinding.homeInclude.deviceWeihu.setOnClickListener(this);
        homePageBinding.homeInclude.deviceOperate.setOnClickListener(this);
        homePageBinding.homeInclude.safetyTool.setOnClickListener(this);
        homePageBinding.homeInclude.deviceDefect.setOnClickListener(this);
        homePageBinding.homeInclude.deviceCopy.setOnClickListener(this);
        homePageBinding.homeInclude.deviceTjwt.setOnClickListener(this);
        homePageBinding.homeInclude.deviceSycn.setOnClickListener(this);
        homePageBinding.common.setOnClickListener(this);
        homePageBinding.serious.setOnClickListener(this);
        homePageBinding.crisis.setOnClickListener(this);
        homePageBinding.bdzAllName.setOnClickListener(this);
        bdzPopwindowBinding = BdzPopwindowBinding.inflate(getLayoutInflater(), null, false);
        mPop = new PopupWindow(bdzPopwindowBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        bdzPopwindowBinding.llContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPop.setOutsideTouchable(true);
        homePageBinding.setTypeClick(this);
        taskItemAdapter = new HomeTaskItemAdapter(mActivity, null, homePageBinding.dataContainer);
        taskItemAdapter.setItemClickListener(new ItemClickListener<Task>() {
            @Override
            public void itemClick(View v, Task task, int position) {
                startTask(task);
            }

            @Override
            public void itemLongClick(View v, Task task, int position) {

            }
        });
        safetyToolAdapter = new HomeSafetyToolAdapter(mActivity, null, homePageBinding.dataContainer);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, ""))) {
            currentSelectBdzId = PreferencesUtils.get(Config.LOCATION_BDZID, "");
            String locationBdzName = PreferencesUtils.get(Config.LOCATION_BDZNAME, "");
            homePageBinding.bdzName.setText(TextUtils.isEmpty(locationBdzName) ? "" : locationBdzName);
        }
        loadData();
        for (TaskType tab : tabs) {
            tab.init();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //跳转到设备巡视或者设备维护界面
            case R.id.device_xunshi:
            case R.id.device_weihu:
                ActivityUtil.startDeviceTourActivity(_this, view.getId());
                break;
            //跳转到操作篇
            case R.id.device_operate:
                try {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName(getPackageName(), "com.cnksi.inspe.ui.InspeMainActivity");
                    intent.setComponent(componentName);
                    //参数设置
                    String userAccount = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, (String) null);
                    //intent.putExtra("userid_array", );//①传递登录用户ID
                    intent.putExtra("username_array", userAccount.split(","));//②传递登录用户用户名,①②任选一种即可

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(HomeActivity.this, "模块暂未开放!", Toast.LENGTH_SHORT).show();
                }

                //ActivityUtil.startOperateActivity(_this);
                break;
            //跳转到安全工器具
            case R.id.safety_tool:
                ActivityUtil.startSafetyToolActivity(_this);
                break;
            //跳转到设备缺陷
            case R.id.device_defect:
                ActivityUtil.startDefectControlActivity(_this);
                break;
            //跳转到数据抄录
            case R.id.device_copy:
                ActivityUtil.startShuJuJianCe(_this);
                break;
            //教育培训五通一措
            case R.id.device_tjwt:
                ActivityUtil.startWTYCActiviy(_this);
                break;
            //跳转到数据同步
            case R.id.device_sycn:
                ActivityUtil.startSync(mActivity);
                break;
            //显示变电站列表对话框
            case R.id.bdz_all_name:
                mPowerStationDialog.show();
                break;
            //一般缺陷
            case R.id.common:
                showRecyclerDefect(mCommonMap);
                homePageBinding.common.setSelected(true);
                homePageBinding.crisis.setSelected(false);
                homePageBinding.serious.setSelected(false);
                defectAdapter.setList(mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId));
                break;
            //严重缺陷
            case R.id.serious:
                showRecyclerDefect(mSerioutMap);
                homePageBinding.common.setSelected(false);
                homePageBinding.crisis.setSelected(false);
                homePageBinding.serious.setSelected(true);
                defectAdapter.setList(mSerioutMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mSerioutMap.get(currentSelectBdzId));
                break;
            //危急缺陷
            case R.id.crisis:
                showRecyclerDefect(mCrisisMap);
                homePageBinding.common.setSelected(false);
                homePageBinding.serious.setSelected(false);
                homePageBinding.crisis.setSelected(true);
                defectAdapter.setList(mCrisisMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCrisisMap.get(currentSelectBdzId));
                break;
            default:
                break;
        }
    }

    public void showRecyclerDefect(Map<String, ArrayList<DefectRecord>> defectMap) {
        if (defectMap.get(currentSelectBdzId) == null) {
            homePageBinding.recyDefect.setVisibility(View.GONE);
            homePageBinding.defectContainer.setVisibility(View.VISIBLE);
        } else {
            homePageBinding.recyDefect.setVisibility(View.VISIBLE);
            homePageBinding.defectContainer.setVisibility(View.GONE);
        }
    }

    private void initBDZDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = bdzList.size() > 8 ? DisplayUtils.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        final ViewHolder holder = new ViewHolder(this, null, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, bdzList, R.layout.dialog_content_child_item);
        adapter.setItemClickListener(new ItemClickListener<Bdz>() {
            @Override
            public void itemClick(View v, Bdz bdz, int position) {
                if (!bdz.name.contains("未激活")) {
                    homePageBinding.bdzName.setText(bdz.name);
                    mPowerStationDialog.dismiss();
                    currentSelectBdzId = bdzList.get(position).bdzid;
                    homePageBinding.common.setSelected(true);
                    homePageBinding.serious.setSelected(false);
                    homePageBinding.crisis.setSelected(false);
                    loadDefect();
                } else
                    ToastUtils.showMessage("该变电站未激活");
            }

            @Override
            public void itemLongClick(View v, Bdz bdz, int position) {

            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    public void loadDefect() {
        if (defectAdapter == null) {
            homePageBinding.common.setSelected(true);
            if (!bdzList.isEmpty()) {
                if (TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, "")))
                    currentSelectBdzId = bdzList.get(0).bdzid;
            }
            defectAdapter = new DefectAdapter(_this, mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId), R.layout.exits_defect_layout);
            defectAdapter.setItemClickListener(HomeActivity.this);
            homePageBinding.recyDefect.setLayoutManager(new LinearLayoutManager(_this, LinearLayout.HORIZONTAL, false));
            homePageBinding.recyDefect.setAdapter(defectAdapter);
        } else {
            if (TextUtils.isEmpty(currentSelectBdzId) && !bdzList.isEmpty())
                currentSelectBdzId = bdzList.get(0).bdzid;
            defectAdapter.setList(mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId));
        }
        showRecyclerDefect(mCommonMap);
    }

    @Override
    public void itemClick(View v, Object o, int position) {
        DefectRecord defectRecord = (DefectRecord) o;
        if (!TextUtils.isEmpty(defectRecord.pics)) {
            ArrayList<String> listPicDis = StringUtils.stringToList(defectRecord.pics, ",");
            showImageDetails(_this, StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER), false);
        }
    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }


    /**
     * 显示不同任务的工具类。
     */
    private class TaskType {
        TabType type;
        TextView tv;
        List<Task> tasks = new ArrayList<>();
        List<DbModel> safetyTools = new ArrayList<>();

        public TaskType(TextView tv, TabType type) {
            this.type = type;
            this.tv = tv;
            tv.setText(type.zhName);
            //onResume会执行
            // init();
        }

        public void init() {
            ExecutorManager.executeTaskSerially(() -> {
                List<Task> taskList = null;
                switch (type) {
                    case inspection:
                        long time0 = System.currentTimeMillis();
                        taskList = TaskService.getInstance().
                                findTaskListByLimit(3, InspectionType.full.name(), InspectionType.routine.name(), InspectionType.special.name(), InspectionType.professional.name());
                        Log.d("TAG", System.currentTimeMillis() - time0 + "time1");
                        if (taskList != null && taskList.size() > 0)
                            for (Task task : taskList) {
                                try {
                                    Report report = ReportService.getInstance().getReportByTask(task.taskid);
                                    String str = "";
                                    if (report != null) {

                                        if (InspectionType.full.name().equals(task.inspection)) {
                                            long copyTotal = CopyItemService.getInstance().getCopyTotalCount(task.bdzid, task.inspection);
                                            long copyCount = CopyResultService.getInstance().getReportCopyCount(report.reportid);
                                            str = String.format("抄录：%d/%d", copyCount, copyTotal);
                                        }
                                    }
                                    String arrivedStr = PlacedService.getInstance().findPlacedSpace(report == null ? "" : report.reportid, task.bdzid);
                                    if (!TextUtils.isEmpty(arrivedStr) && !task.inspection.contains("special"))
                                        str = str + "   " + "到位  ：" + arrivedStr;
                                    task.remark = str;
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        Log.d("TAG", System.currentTimeMillis() - time0 + "");
                        break;
                    case maintenance:
                        taskList = TaskService.getInstance().
                                findTaskListByLimit(3, InspectionType.maintenance.name(), InspectionType.switchover.name());
                        break;
                    case safetytool:
                        String sql = "SELECT id,num,name,short_name,name_pinyin,status,bdz_id,bdz_name,next_check_time FROM gqj_info where dept_id=? and dlt=0 " +
                                "and status not in('inTest','stop') and  datetime(next_check_time, '-1 month') <= datetime('now', 'localtime', 'start of day') " +
                                " ORDER BY next_check_time limit 3;";
                        SqlInfo sqlInfo = new SqlInfo(sql);
                        sqlInfo.addBindArg(new KeyValue("dept_id", PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, "")));
                        try {
                            safetyTools = CustomApplication.getInstance().getDbManager().findDbModelAll(sqlInfo);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        return;
                    case switching:
                        taskList = TaskService.getInstance().findWorkTicketTask();
                        break;
                }
                final List<Task> temp = taskList;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tasks.clear();
                        if (temp != null && temp.size() > 0) tasks.addAll(temp);
                        if (currentDataType == TaskType.this)
                            taskItemAdapter.setList(tasks);
                    }
                });
            });
        }

        void setSelected(boolean isSelect) {
            if (tv.isSelected() != isSelect)
                tv.setSelected(isSelect);
        }

        void setOnClickListener(View.OnClickListener listener) {
            tv.setOnClickListener(listener);
        }
    }

    private enum TabType {
        inspection("设备巡视"), maintenance("设备维护"), switching("倒闸操作"), safetytool("安全工器具");
        String zhName;

        TabType(String zhName) {
            this.zhName = zhName;
        }
    }


    private TaskType currentDataType;
    private TaskType[] tabs;

    private void initTabs() {
        tabs = new TaskType[4];
        tabs[0] = new TaskType(homePageBinding.tvDeviceInspection, TabType.inspection);
        tabs[1] = new TaskType(homePageBinding.tvDeviceMaintenance, TabType.maintenance);
        tabs[2] = new TaskType(homePageBinding.tvTransferSwitching, TabType.switching);
        tabs[3] = new TaskType(homePageBinding.tvOperations, TabType.safetytool);
        select(tabs[0]);
        for (final TaskType tab : tabs) {
            tab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(tab);
                }
            });
        }
    }

    private void select(TaskType dataType) {
        if (currentDataType == dataType) return;
        homePageBinding.dataContainer.removeAllViews();
        currentDataType = dataType;
        for (TaskType tab : tabs) {
            tab.setSelected(tab == dataType);
        }
        if (currentDataType.type == TabType.safetytool) {
            taskItemAdapter.removeAllViews();
            safetyToolAdapter.setList(currentDataType.safetyTools);
        } else
            taskItemAdapter.setList(currentDataType.tasks);
    }

    /**
     * 跳转到巡视的开始任务界面
     */
    private void startTask(Task task) {
        CustomApplication.closeDbConnection();
        Intent intent = new Intent();
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        ComponentName componentName;
        if ("workticket".equals(task.inspection))
            componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
        else {
            componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
            intent.putExtra(Config.IS_FROM_SJJC, true);
        }
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection.split("_|-")[0]);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection.split("_|-")[0]);
        intent.putExtra("task_id", task.taskid);
        intent.setComponent(componentName);
        startActivity(intent);
    }

    /**
     * 根据判断本地数据库是否有设备来判断是否需要二次同步。
     */
    private void checkIsNeedSync() {
        try {
            if (DeviceService.getInstance().selector().count() == 0) {
                DialogUtils.createTipsDialog(mActivity, "检测到本地设备数据为空，是否需要同步数据？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityUtil.startSync(mActivity);
                    }
                }, false).show();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0) {
                    exitSystem();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}