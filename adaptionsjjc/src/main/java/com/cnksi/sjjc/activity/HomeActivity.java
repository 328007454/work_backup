package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cnksi.sjjc.adapter.HomeTaskItemAdapter;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.ActivityHomePageBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.service.TaskService;

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
    }

    private void initUI() {
        homePageBinding.setTypeClick(this);
        taskItemAdapter = new HomeTaskItemAdapter(mCurrentActivity, null, homePageBinding.dataContainer);
    }


    private void initData() {
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Task> taskList = TaskService.getInstance().findTaskListByLimit(3, InspectionType.full.name(), InspectionType.routine.name(), InspectionType.special.name());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        taskItemAdapter.setList(taskList);
                    }
                });
            }
        });
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
        String type;
        TextView tv;
        List<Task> tasks;

        public TaskType(TextView tv, String type) {
            this.type = type;
            this.tv = tv;
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
        inspection, maintenance, switching, operations
    }


    private TaskType currentDataType;
    private TaskType[] tabs;

    private void initTabs() {
        tabs = new TaskType[4];
        tabs[0] = new TaskType(homePageBinding.tvDeviceInspection, "inspection");
        tabs[1] = new TaskType(homePageBinding.tvDeviceMaintenance, "maintenance");
        tabs[2] = new TaskType(homePageBinding.tvTransferSwitching, "switching");
        tabs[3] = new TaskType(homePageBinding.tvOperations, "operations");
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
}
