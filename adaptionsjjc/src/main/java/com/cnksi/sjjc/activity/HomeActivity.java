package com.cnksi.sjjc.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.adapter.HomeTaskItemAdapter;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.ActivityHomePageBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.TaskService;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by han on 2017/3/24.
 */

public class HomeActivity extends BaseActivity {
    ActivityHomePageBinding homePageBinding;
    HomeTaskItemAdapter taskItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homePageBinding = ActivityHomePageBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(homePageBinding.getRoot());
        initUI();
        initData();
        initTabs();
    }

    private void initUI() {
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


    private void initData() {

    }

//    public void goToInspectionPage(int id) {
//        Intent intent = null;
//        switch (id) {
//            case R.id.device_xunshi:
//            case R.id.device_weihu:
//                intent = new Intent(_this, NewLauncherActivity.class);
//                intent.putExtra("position", id == R.id.device_xunshi ? 0 : 1);
//                startActivity(intent);
//                break;
//            case R.id.device_operate:
//                ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
//                Intent intent2 = new Intent();
//                intent2.setComponent(componentName);
//                intent2.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_USER, ""));
//                intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_ACCOUNT, ""));
//                break;
//            case R.id.device_unify:
//                break;
//            case R.id.device_defect:
//                break;
//            case R.id.device_copy:
//                break;
//            case R.id.device_tjwt:
//                break;
//            case R.id.device_sycn:
//                break;
//            default:
//                break;
//        }
//    }


    class TaskType {
        TabType type;
        TextView tv;
        List<Task> tasks = new ArrayList<>();

        public TaskType(TextView tv, TabType type) {
            this.type = type;
            this.tv = tv;
            tv.setText(type.zhName);
            init();
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
                            break;
                        case maintenance:
                            taskList = TaskService.getInstance().
                                    findTaskListByLimit(3, InspectionType.maintenance.name(), InspectionType.switchover.name());
                            break;
                        case operations:
                            taskList = TaskService.getInstance().
                                    findTaskListByLimit(3, InspectionType.operation.name());
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
        intent.setComponent(componentName);
        intent.putExtra("task_id", task.taskid);
        startActivity(intent);
    }
}
