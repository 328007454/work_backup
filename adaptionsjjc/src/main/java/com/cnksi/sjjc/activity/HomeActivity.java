package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.View.Banner;
import com.cnksi.sjjc.adapter.BdzAdapter;
import com.cnksi.sjjc.adapter.DefectAdapter;
import com.cnksi.sjjc.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.adapter.HomeTaskItemAdapter;
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
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.TTSUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by han on 2017/3/24.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, ItemClickListener {
    private ActivityHomePageBinding homePageBinding;
    private BdzPopwindowBinding bdzPopwindowBinding;
    private ArrayList<Integer> bannerMapUrl = new ArrayList<>();
    private BdzAdapter bdzAdapter;
    private PopupWindow mPop;
    private List<Bdz> bdzList = new ArrayList<>();
    private int[] location = new int[2];
    private DefectAdapter defectAdapter;
    private Map<String, ArrayList<DefectRecord>> mCrisisMap = new HashMap<>();
    private Map<String, ArrayList<DefectRecord>> mSerioutMap = new HashMap<>();
    private Map<String, ArrayList<DefectRecord>> mCommonMap = new HashMap<>();
    private String currentSelectBdzId;
    private HomeTaskItemAdapter taskItemAdapter;
    private ListView mPowerStationListView;
    private Dialog mPowerStationDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedStatusColor();
        homePageBinding = ActivityHomePageBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(homePageBinding.getRoot());
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeviceService.getInstance().refreshDeviceHasCopy();
            }
        });
        initUI();
        initTabs();
        TTSUtils.getInstance().startSpeaking(String.format("欢迎使用%1$s", getString(R.string.app_name)));
    }

    ArrayList<DefectRecord> recordCrisis;
    ArrayList<DefectRecord> recordSerious;
    ArrayList<DefectRecord> recordCommon;

    private void loadData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mCrisisMap.clear();
                    mCommonMap.clear();
                    mSerioutMap.clear();
                    bdzList.clear();
                    bdzList = CustomApplication.getDbManager().findAll(Bdz.class);
                    final List<DefectRecord> defectList = DefectRecordService.getInstance().queryCurrentBdzExistDefectList();
                    for (DefectRecord mDefectRecord : defectList) {
                        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                            if (mCrisisMap.get(mDefectRecord.bdzid) == null) {
                                recordCrisis = new ArrayList<DefectRecord>();
                                recordCrisis.add(mDefectRecord);
                            } else
                                recordCrisis.add(mDefectRecord);
                            mCrisisMap.put(mDefectRecord.bdzid, recordCrisis);
                        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                            if (mSerioutMap.get(mDefectRecord.bdzid) == null) {
                                recordSerious = new ArrayList<DefectRecord>();
                                recordSerious.add(mDefectRecord);
                            } else
                                recordSerious.add(mDefectRecord);
                            mSerioutMap.put(mDefectRecord.bdzid, recordSerious);
                        } else if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                            if (mCommonMap.get(mDefectRecord.bdzid) == null) {
                                recordCommon = new ArrayList<DefectRecord>();
                                recordCommon.add(mDefectRecord);
                            } else
                                recordCommon.add(mDefectRecord);
                            mCommonMap.put(mDefectRecord.bdzid, recordCommon);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initBDZDialog();
                            if (bdzAdapter == null) {
                                bdzAdapter = new BdzAdapter(_this, bdzList, R.layout.dialog_content_child_item);
                                bdzPopwindowBinding.lvBzd.setAdapter(bdzAdapter);
                            } else
                                bdzAdapter.setList(bdzList);
                            if (defectAdapter == null) {
                                homePageBinding.common.setSelected(true);
                                if (!bdzList.isEmpty())
                                    currentSelectBdzId = bdzList.get(0).bdzid;
                                showRecyclerDefect(mCommonMap);
                                defectAdapter = new DefectAdapter(_this, mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId), R.layout.exits_defect_layout);
                                defectAdapter.setItemClickListener(HomeActivity.this);
                                homePageBinding.recyDefect.setLayoutManager(new LinearLayoutManager(_this, LinearLayout.HORIZONTAL, false));
                                homePageBinding.recyDefect.setAdapter(defectAdapter);
                            }
                            homePageBinding.bdzName.setText(bdzList.get(0).name);
                        }
                    });
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
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
        homePageBinding.homeInclude.deviceUnify.setOnClickListener(this);
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
        taskItemAdapter = new HomeTaskItemAdapter(mCurrentActivity, null, homePageBinding.dataContainer);
        taskItemAdapter.setItemClickListener(new ItemClickListener<Task>() {
            @Override
            public void itemClick(View v, Task task, int position) {
                startTask(task);
            }

            @Override
            public void itemLongClick(View v, Task task, int position) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (TaskType tab : tabs) {
            tab.init();
        }
        loadData();
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.device_xunshi:
            case R.id.device_weihu:
                ActivityUtil.startDeviceTourActivity(_this, view.getId());
                break;
            case R.id.device_operate:
                ActivityUtil.startOperateActivity(_this);
                break;
            case R.id.device_unify:
                ActivityUtil.startUnifyActivity(_this);
                break;
            case R.id.device_defect:
                //undo
                break;
            case R.id.device_copy:
                ActivityUtil.startShuJuJianCe(_this);
                break;
            case R.id.device_tjwt:
                ActivityUtil.startWTYCActiviy(_this);
                break;
            case R.id.device_sycn:
                ActivityUtil.startSync(mCurrentActivity);
                break;
            case R.id.bdz_all_name:
                mPowerStationDialog.show();
                break;
            case R.id.common:
                showRecyclerDefect(mCommonMap);
                homePageBinding.common.setSelected(true);
                homePageBinding.crisis.setSelected(false);
                homePageBinding.serious.setSelected(false);
                defectAdapter.setList(mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId));
                break;
            case R.id.serious:
                showRecyclerDefect(mSerioutMap);
                homePageBinding.common.setSelected(false);
                homePageBinding.crisis.setSelected(false);
                homePageBinding.serious.setSelected(true);
                defectAdapter.setList(mSerioutMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mSerioutMap.get(currentSelectBdzId));
                break;
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
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = bdzList.size() > 8 ? DisplayUtil.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        final ViewHolder holder = new ViewHolder(this, null, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, bdzList, R.layout.dialog_content_child_item);
        adapter.setItemClickListener(new ItemClickListener<Bdz>() {
            @Override
            public void itemClick(View v, Bdz bdz, int position) {
                if (!bdz.name.contains("未激活")) {
                    homePageBinding.bdzName.setText(bdz.name + "");
                    mPowerStationDialog.dismiss();
                    currentSelectBdzId = bdzList.get(position).bdzid;
                    homePageBinding.common.setSelected(true);
                    homePageBinding.serious.setSelected(false);
                    homePageBinding.crisis.setSelected(false);
                    showRecyclerDefect(mCommonMap);
                    defectAdapter.setList(mCommonMap.get(currentSelectBdzId) == null ? new ArrayList<DefectRecord>() : mCommonMap.get(currentSelectBdzId));
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

    @Override
    public void itemClick(View v, Object o, int position) {
        DefectRecord defectRecord = (DefectRecord) o;
        if (!TextUtils.isEmpty(defectRecord.pics)) {
            ArrayList<String> listPicDis = com.cnksi.core.utils.StringUtils.string2List(defectRecord.pics);
            showImageDetails(_this, com.cnksi.core.utils.StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER), false);
        }
    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }


    class TaskType {
        TabType type;
        TextView tv;
        List<Task> tasks = new ArrayList<>();

        public TaskType(TextView tv, TabType type) {
            this.type = type;
            this.tv = tv;
            tv.setText(type.zhName);
            //onResume会执行
            // init();
        }

        public void init() {
            mExcutorService.execute(new Runnable() {
                @Override
                public void run() {
                    List<Task> taskList = null;
                    switch (type) {
                        case inspection:
                            taskList = TaskService.getInstance().
                                    findTaskListByLimit(3, InspectionType.full.name(), InspectionType.routine.name(), InspectionType.special.name());
                            if (taskList != null && taskList.size() > 0)
                                for (Task task : taskList) {
                                    try {
                                        Report report = ReportService.getInstance().getReportByTask(task.taskid);
                                        String str = "";
                                        if (report != null) {

                                            if (InspectionType.full.name().equals(task.inspection)) {
                                                long copyTotal = CopyItemService.getInstance().getCopyItemCount(task.bdzid);
                                                long copyCount = CopyResultService.getInstance().getReportCopyCount(report.reportid);
                                                str = String.format("抄录：%d/%d", copyCount, copyTotal);
                                            }
                                        }
                                        String arrivedStr = PlacedService.getInstance().findPlacedSpace("", task.bdzid);
                                        if (!TextUtils.isEmpty(arrivedStr) && !task.inspection.contains("special"))
                                            str = str + "   " + "到位：" + arrivedStr;
                                        task.remark = str;
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }
                            break;
                        case maintenance:
                            taskList = TaskService.getInstance().
                                    findTaskListByLimit(3, InspectionType.maintenance.name(), InspectionType.switchover.name());
                            break;
                        case operations:
                            taskList = TaskService.getInstance().
                                    findOperationTaskByLimit(3);
                            break;
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
                }
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

    enum TabType {
        inspection("设备巡视"), maintenance("设备维护"), switching("倒闸操作"), operations("运维一体化");
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
        tabs[3] = new TaskType(homePageBinding.tvOperations, TabType.operations);
        //  tabs[3] = new DataType(binding.tvAcceptanceReport, PicType.acceptance_report);
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

        currentDataType = dataType;
        for (TaskType tab : tabs) {
            tab.setSelected(tab == dataType);
        }
        taskItemAdapter.setList(currentDataType.tasks);
    }

    private void startTask(Task task) {
        CustomApplication.closeDbConnection();
        Intent intent = new Intent();
        ComponentName componentName;
        if ("workticket".equals(task.inspection))
            componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
        else
            componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection.split("_|-")[0]);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection.split("_|-")[0]);
        intent.putExtra("task_id", task.taskid);
        intent.setComponent(componentName);
        startActivity(intent);
    }
}
