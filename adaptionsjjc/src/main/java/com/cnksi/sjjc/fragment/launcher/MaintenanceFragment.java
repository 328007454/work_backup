package com.cnksi.sjjc.fragment.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BaseBindingAdapter;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskStatistic;
import com.cnksi.sjjc.databinding.FragmentMaintenanceBinding;
import com.cnksi.sjjc.databinding.NewLaunchTaskItemBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.service.TaskService;

import org.xutils.ex.DbException;

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
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
        //   maintenanceBinding.ivMaintenanceList.setOnClickListener();
        maintenanceBinding.setEvent(this);
    }

    /**
     * XML中调用方法
     *
     * @param typeName
     */
    public void gotoTaskList(String typeName) {
        CustomApplication.closeDbConnection();
        Intent intent4 = new Intent();
        ComponentName componentName4 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
        intent4.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName);
        intent4.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(mCurrentActivity, Config.CURRENT_LOGIN_USER, ""));
        intent4.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(mCurrentActivity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent4.setComponent(componentName4);
        startActivity(intent4);
    }

    @Override
    protected void initData() {
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                final TaskStatistic result = TaskService.getInstance().getTaskStatistic(InspectionType.maintenance.name());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        maintenanceBinding.setMaintenance(result);
                    }
                });
            }
        });
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                final TaskStatistic result = TaskService.getInstance().getTaskStatistic(InspectionType.switchover.name());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        maintenanceBinding.setSwitchover(result);
                    }
                });
            }
        });
        mExcutorService.execute(new MainRunnable(InspectionType.maintenance));
        mExcutorService.execute(new MainRunnable(InspectionType.switchover));
    }

    class MainRunnable implements Runnable {
        private InspectionType inspectionType;

        public MainRunnable(InspectionType inspectionType) {
            this.inspectionType = inspectionType;
        }

        @Override
        public void run() {

            List<Task> result = null;
            try {
                result = TaskService.getInstance().findTaskListByLimit(inspectionType.name(), 3);
            } catch (DbException e) {
                CLog.e(e);
            }
            final List<Task> taskList = result;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout layout = InspectionType.maintenance == inspectionType ? maintenanceBinding.llMaintenance : maintenanceBinding.llSwitchover;
                    ListView listView = InspectionType.maintenance == inspectionType ? maintenanceBinding.listMaintenance : maintenanceBinding.listSwitchover;
                    if (taskList == null || taskList.size() == 0) {
                        layout.setVisibility(View.GONE);
                    } else {
                        layout.setVisibility(View.VISIBLE);
                        listView.setAdapter(new TaskItemAdapter(taskList));
                        // TODO: 2017/3/23
                    }
                }
            });
        }
    }

    class TaskItemAdapter extends BaseBindingAdapter<NewLaunchTaskItemBinding, Task> {
        GradientDrawable drawableDone, drawableUndo;
        int colorDone = Color.parseColor("#01ce7f"), colorUndo = Color.parseColor("#fd5f54");

        public TaskItemAdapter(List<Task> data) {
            super(mCurrentActivity, data, R.layout.new_launch_task_item);
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
            boolean isDone = Task.TaskStatus.done.name().equals(item.status);
            binding.tvStatus.setText(isDone ? "已完成" : "未完成");
            binding.tvStatus.setTextColor(isDone ? colorDone : colorUndo);
            binding.ivStatus.setBackground(isDone ? drawableDone : drawableUndo);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTask(item);
                }
            });
        }

        private void startTask(Task task) {
            CustomApplication.closeDbConnection();
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection.split("_|-")[0]);
            intent.setComponent(componentName);
            intent.putExtra("task_id", task.taskid);
            startActivity(intent);
        }
    }


}
