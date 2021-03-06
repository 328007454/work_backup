package com.cnksi.login.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.vo.TaskStatistic;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.login.databinding.FragmentMaintenanceBinding;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.NewLaunchTaskItemBinding;

import java.util.List;

/**
 * Created by han on 2017/3/22.
 */

public class MaintenanceFragment extends BaseCoreFragment {
    FragmentMaintenanceBinding maintenanceBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        maintenanceBinding = FragmentMaintenanceBinding.inflate(inflater);
        return maintenanceBinding.getRoot();
    }

    @Override
    public int getFragmentLayout() {
        return 0;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
    }

    @Override
    public void onResume() {
        super.onResume();
        maintenanceBinding.setEvent(this);
        initData();
    }

    /**
     * XML中调用方法
     *
     * @param type
     */
    public void gotoTaskList(String type) {
        Intent intent4 = new Intent();
        intent4.setClass(getActivity(), TaskRemindActivity.class);
        intent4.putExtra(Config.CURRENT_INSPECTION_TYPE, type);
        intent4.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent4.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent4.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        startActivity(intent4);
        PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE, type);
        PreferencesUtils.get(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.valueOf(type).value);
    }

    protected void initData() {
        ExecutorManager.executeTaskSerially(new Runnable() {
            @Override
            public void run() {
                final TaskStatistic maintenance = TaskService.getInstance().getTaskStatistic(InspectionType.maintenance.name());
                final TaskStatistic switchover = TaskService.getInstance().getTaskStatistic(InspectionType.switchover.name());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        maintenanceBinding.setSwitchover(switchover);
                        maintenanceBinding.setMaintenance(maintenance);
                        final int percent = (int) ((maintenance.getMonthTaskFinish() + switchover.getMonthTaskFinish())
                                / (maintenance.getMonthTaskCount() + switchover.getMonthTaskCount() + 0.0f) * 100);
                        mHandler.post(new Runnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                maintenanceBinding.progress.setProgress(i <= percent ? i++ : percent);
                                if (i <= percent) {
                                    mHandler.postDelayed(this, 100);
                                }
                            }
                        });
                    }
                });
            }
        });
        ExecutorManager.executeTaskSerially(new MainRunnable(InspectionType.maintenance));
        ExecutorManager.executeTaskSerially(new MainRunnable(InspectionType.switchover));
    }

    class MainRunnable implements Runnable {
        private InspectionType inspectionType;

        public MainRunnable(InspectionType inspectionType) {
            this.inspectionType = inspectionType;
        }

        @Override
        public void run() {

            final List<Task> taskList = TaskService.getInstance().findTaskListByLimit(3, inspectionType.name());
            mHandler.post(() -> {
                LinearLayout layout = InspectionType.maintenance == inspectionType ? maintenanceBinding.llMaintenance : maintenanceBinding.llSwitchover;
                ListView listView = InspectionType.maintenance == inspectionType ? maintenanceBinding.listMaintenance : maintenanceBinding.listSwitchover;
                if (taskList == null || taskList.size() == 0) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    listView.setAdapter(new TaskItemAdapter(taskList));
                    // TODO: 2017/3/23
                }
            });
        }
    }

    class TaskItemAdapter extends BaseBindingAdapter<NewLaunchTaskItemBinding, Task> {
        GradientDrawable drawableDone, drawableUndo;
        int colorDone = Color.parseColor("#01ce7f"), colorUndo = Color.parseColor("#fd5f54");

        public TaskItemAdapter(List<Task> data) {
            super(mActivity, data, R.layout.new_launch_task_item);
            drawableDone = new GradientDrawable();
            drawableDone.setShape(GradientDrawable.OVAL);
            drawableDone.setColor(colorDone);
            drawableUndo = new GradientDrawable();
            drawableUndo.setShape(GradientDrawable.OVAL);
            drawableUndo.setColor(colorUndo);
        }

        @Override
        public void convert(NewLaunchTaskItemBinding binding, final Task item, int position) {
            binding.tvBdz.setText(item.bdzname);
            binding.tvName.setText(item.inspection_name);
            binding.tvDate.setText(DateUtils.getFormatterTime(item.schedule_time));
            boolean isDone = TaskStatus.done.name().equals(item.status);
            binding.tvStatus.setText(isDone ? "已完成" : "未完成");
            binding.tvStatus.setTextColor(isDone ? colorDone : colorUndo);
            binding.ivStatus.setBackground(isDone ? drawableDone : drawableUndo);
            binding.getRoot().setOnClickListener(v -> startTask(item));
        }

        private void startTask(Task task) {
            Intent intent = new Intent();
            intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
            intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
            intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
//            ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindFragment");
            intent.setClass(getActivity(), TaskRemindActivity.class);
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection.split("_|-")[0]);
//            intent.setComponent(componentName);
            intent.putExtra(Config.IS_FROM_SJJC, true);
            intent.putExtra("task_id", task.taskid);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
}
