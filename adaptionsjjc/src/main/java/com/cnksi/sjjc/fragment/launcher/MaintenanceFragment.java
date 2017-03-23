package com.cnksi.sjjc.fragment.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskStatistic;
import com.cnksi.sjjc.databinding.FragmentMaintenanceBinding;
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
        //    mExcutorService.execute();
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
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Task> result = null;
                try {
                    result = TaskService.getInstance().findTaskListByLimit(InspectionType.maintenance.name(), 3);
                } catch (DbException e) {
                    CLog.e(e);
                }
                mHandler.post(new MainRunnable(result));
            }
        });
    }

    class MainRunnable implements Runnable {
        List<Task> taskList;

        public MainRunnable(List<Task> task) {
            this.taskList = task;
        }

        @Override
        public void run() {
            if (taskList == null || taskList.size() == 0) {
                maintenanceBinding.llMaintenance.setVisibility(View.GONE);
            } else {
                maintenanceBinding.llMaintenance.setVisibility(View.VISIBLE);
                //maintenanceBinding.listMaintenance.setAdapter();
                // TODO: 2017/3/23
            }
        }
    }

    //   class TaskItemAdapter extends BaseBindingAdapter

}
