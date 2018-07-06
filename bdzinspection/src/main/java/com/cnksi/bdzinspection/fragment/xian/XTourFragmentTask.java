package com.cnksi.bdzinspection.fragment.xian;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.xian.HomeWeekTaskAdapter;
import com.cnksi.bdzinspection.databinding.BdzinspectionFragmentTourTaskBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.WeekTime;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.K on 2018/7/4.
 */

public class XTourFragmentTask extends BaseFragment {
    BdzinspectionFragmentTourTaskBinding tourTaskBinding;
    private String title;
    private String inspectionType;
    private List<Task> taskList = new ArrayList<>();
    private HomeWeekTaskAdapter homeWeekTaskAdapter;
    private TaskCountInterface countInterface;
    private int fragmentPosition;

    public interface TaskCountInterface {
        void taskTotal(List<Task> tasks, int fragmentPosition);
    }

    public void setTaskCountInterface(TaskCountInterface taskCountInterface) {
        this.countInterface = taskCountInterface;
    }

    public static XTourFragmentTask getFragment(String titleName, int fragmentPosition) {
        XTourFragmentTask xTourFragmentTask = new XTourFragmentTask();
        xTourFragmentTask.title = titleName.split("（")[0];
        xTourFragmentTask.fragmentPosition = fragmentPosition;
        return xTourFragmentTask;
    }

    public void setCurrentInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tourTaskBinding = BdzinspectionFragmentTourTaskBinding.inflate(inflater);
        initData();
        return tourTaskBinding.getRoot();
    }

    public void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            taskList = TaskService.getInstance().findTaskByTime(inspectionType, WeekTime.getTyeName(title.split("（")[0]));
            if (mActivity == null) {
                return;
            }
            mActivity.runOnUiThread(() -> {
                if (countInterface != null) {
                    countInterface.taskTotal(taskList, fragmentPosition);
                }
            });
        });
    }

    @Override
    public void lazyLoad() {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(() -> {
            if (countInterface != null) {
                countInterface.taskTotal(taskList, fragmentPosition);
            }

            if (taskList == null) {
                ToastUtils.showMessage("当前没有数据");
                return;
            }
            homeWeekTaskAdapter = new HomeWeekTaskAdapter(R.layout.x_home_task_item, taskList);
            tourTaskBinding.rcy.setLayoutManager(new LinearLayoutManager(getContext()));
            tourTaskBinding.rcy.setAdapter(homeWeekTaskAdapter);
            initClickCallBack();
        });
    }

    private void initClickCallBack() {
        homeWeekTaskAdapter.setOnItemClickListener((adapter, view, position) -> {
            startTask((Task) adapter.getItem(position));
        });
    }

    /**
     * 跳转到巡视的开始任务界面
     */
    private void startTask(Task task) {
        Intent intent = new Intent();
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.setClass(getActivity(), com.cnksi.bdzinspection.activity.TaskRemindActivity.class);
        intent.putExtra(Config.IS_FROM_SJJC, true);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection_name);
        intent.putExtra("task_id", task.taskid);
        startActivity(intent);
    }

}
