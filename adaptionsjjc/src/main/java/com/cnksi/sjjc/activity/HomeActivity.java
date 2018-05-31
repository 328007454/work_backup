package com.cnksi.sjjc.activity;


import android.app.Dialog;
import android.content.Intent;
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

import com.cnksi.bdzinspection.czp.OperateTaskListActivity;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BdzAdapter;
import com.cnksi.sjjc.adapter.DefectAdapter;
import com.cnksi.sjjc.adapter.HomeSafetyToolAdapter;
import com.cnksi.sjjc.adapter.HomeTaskItemAdapter;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.databinding.ActivityHomePageBinding;
import com.cnksi.sjjc.databinding.BdzPopwindowBinding;
import com.cnksi.sjjc.service.PlacedService;
import com.cnksi.sjjc.util.ActivityUtil;
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
public class HomeActivity extends BaseSjjcActivity implements View.OnClickListener, ItemClickListener {
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
        inUI();
        initTabs();
        TTSUtils.getInstance().startSpeaking(String.format("欢迎使用%1$s", getString(R.string.app_name)));
        ExecutorManager.executeTaskSerially(() -> {
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


    private void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                HomeActivity.this.transformDefectType();
                HomeActivity.this.runOnUiThread(() -> {
                    HomeActivity.this.initBDZDialog();
                    if (bdzAdapter == null) {
                        bdzAdapter = new BdzAdapter(mActivity, bdzList, R.layout.dialog_content_child_item);
                        bdzPopwindowBinding.lvBzd.setAdapter(bdzAdapter);
                    } else {
                        bdzAdapter.setList(bdzList);
                    }
                    if (!bdzList.isEmpty() && TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, ""))) {
                        homePageBinding.bdzName.setText(bdzList.get(0).name);
                    }
                    HomeActivity.this.loadDefect();
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
        homePageBinding.homeInclude.txtTicketLauncher.setOnClickListener(this);
        bdzPopwindowBinding = BdzPopwindowBinding.inflate(getLayoutInflater(), null, false);
        mPop = new PopupWindow(bdzPopwindowBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        bdzPopwindowBinding.llContainer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPop.setOutsideTouchable(true);
        homePageBinding.setTypeClick(this);
        taskItemAdapter = new HomeTaskItemAdapter(mActivity, null, homePageBinding.dataContainer);
        taskItemAdapter.setItemClickListener((v, task, position) -> startTask(task));
        safetyToolAdapter = new HomeSafetyToolAdapter(mActivity, null, homePageBinding.dataContainer);
    }

    @Override
    public void initData() {

    }

    boolean isFirstLoad = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad) {
            if (!TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, ""))) {
                currentSelectBdzId = PreferencesUtils.get(Config.LOCATION_BDZID, "");
                String locationBdzName = PreferencesUtils.get(Config.LOCATION_BDZNAME, "");
                homePageBinding.bdzName.setText(TextUtils.isEmpty(locationBdzName) ? "" : locationBdzName);
            }
            loadData();
            for (TaskType tab : tabs) {
                tab.init();
            }
            checkUpdate();
        } else {
            isFirstLoad = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //跳转到设备巡视或者设备维护界面
            case R.id.device_xunshi:
            case R.id.device_weihu:
                ActivityUtil.startDeviceTourActivity(mActivity, view.getId());
                break;
            //跳转到操作篇
            case R.id.device_operate:
                ActivityUtil.startOperateActivity(mActivity);
                break;
            //跳转到安全工器具
            case R.id.safety_tool:
                ActivityUtil.startSafetyToolActivity(mActivity);
                break;
            //跳转到设备缺陷
            case R.id.device_defect:
                ActivityUtil.startDefectControlActivity(mActivity);
                break;
            //跳转到数据抄录
            case R.id.device_copy:
                ActivityUtil.startShuJuJianCe(mActivity);
                break;
            //教育培训五通一措
            case R.id.device_tjwt:
                ActivityUtil.startWTYCActiviy(mActivity);
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
            case R.id.txt_ticket_launcher:
                ActivityUtil.startTicketDateModel(mActivity);
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
        adapter.setItemClickListener((v, bdz, position) -> {
            if (!bdz.name.contains("未激活")) {
                homePageBinding.bdzName.setText(bdz.name);
                mPowerStationDialog.dismiss();
                currentSelectBdzId = bdzList.get(position).bdzid;
                homePageBinding.common.setSelected(true);
                homePageBinding.serious.setSelected(false);
                homePageBinding.crisis.setSelected(false);
                loadDefect();
            } else {
                ToastUtils.showMessage("该变电站未激活");
            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    public void loadDefect() {
        if (defectAdapter == null) {
            homePageBinding.common.setSelected(true);
            if (!bdzList.isEmpty()) {
                if (TextUtils.isEmpty(PreferencesUtils.get(Config.LOCATION_BDZID, ""))) {
                    currentSelectBdzId = bdzList.get(0).bdzid;
                }
            }
            defectAdapter = new DefectAdapter(mActivity, mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId), R.layout.exits_defect_layout);
            defectAdapter.setItemClickListener(this);
            homePageBinding.recyDefect.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayout.HORIZONTAL, false));
            homePageBinding.recyDefect.setAdapter(defectAdapter);
        } else {
            if (TextUtils.isEmpty(currentSelectBdzId) && !bdzList.isEmpty()) {
                currentSelectBdzId = bdzList.get(0).bdzid;
            }
            defectAdapter.setList(mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId));
        }
        showRecyclerDefect(mCommonMap);
    }

    @Override
    public void onClick(View v, Object o, int position) {
        DefectRecord defectRecord = (DefectRecord) o;
        if (!TextUtils.isEmpty(defectRecord.pics)) {
            ArrayList<String> listPicDis = StringUtils.stringToList(defectRecord.pics, ",");
            showImageDetails(mActivity, StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER), false);
        }
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
                        if (taskList != null && taskList.size() > 0) {
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
                                    if (!TextUtils.isEmpty(arrivedStr) && !task.inspection.contains("special")) {
                                        str = str + "   " + "到位  ：" + arrivedStr;
                                    }
                                    task.remark = str;
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
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
                    default:
                }
                final List<Task> temp = taskList;
                mHandler.post(() -> {
                    tasks.clear();
                    if (temp != null && temp.size() > 0) {
                        tasks.addAll(temp);
                    }
                    if (currentDataType == TaskType.this) {
                        taskItemAdapter.setList(tasks);
                    }
                });
            });
        }

        void setSelected(boolean isSelect) {
            if (tv.isSelected() != isSelect) {
                tv.setSelected(isSelect);
            }
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
            tab.setOnClickListener(v -> select(tab));
        }
    }

    private void select(TaskType dataType) {
        if (currentDataType == dataType) {
            return;
        }
        homePageBinding.dataContainer.removeAllViews();
        currentDataType = dataType;
        for (TaskType tab : tabs) {
            tab.setSelected(tab == dataType);
        }
        if (currentDataType.type == TabType.safetytool) {
            taskItemAdapter.removeAllViews();
            safetyToolAdapter.setList(currentDataType.safetyTools);
        } else {
            taskItemAdapter.setList(currentDataType.tasks);
        }
    }

    /**
     * 跳转到巡视的开始任务界面
     */
    private void startTask(Task task) {

        Intent intent = new Intent();
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        if ("workticket".equals(task.inspection)) {
            intent.setClass(this, OperateTaskListActivity.class);
        } else {
            intent.setClass(this, com.cnksi.bdzinspection.activity.TaskRemindActivity.class);
            intent.putExtra(Config.IS_FROM_SJJC, true);
        }
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection.split("_|-")[0]);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection.split("_|-")[0]);
        intent.putExtra("task_id", task.taskid);
        startActivity(intent);
    }

    /**
     * 根据判断本地数据库是否有设备来判断是否需要二次同步。
     */
    private void checkIsNeedSync() {
        try {
            if (DeviceService.getInstance().selector().count() == 0) {
                DialogUtils.createTipsDialog(mActivity, "检测到本地设备数据为空，是否需要同步数据？", v -> ActivityUtil.startSync(mActivity), false).show();
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