package com.cnksi.sjjc.fragment.launcher;

import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskStatistic;
import com.cnksi.sjjc.databinding.FragmentTourBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.service.TaskService;

import java.util.List;

/**
 * Created by han on 2017/3/22.
 */

public class TourFragment extends BaseCoreFragment {
    FragmentTourBinding tourBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tourBinding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.fragment_tour, container, false);
        return tourBinding.getRoot();
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
        tourBinding.setEvent(this);
    }

    int i = 0;

    @Override
    protected void initData() {
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Task> totalTasks = TaskService.getInstance().getAllTask();
                List<Task> finishTotalTasks = TaskService.getInstance().getFinishedTask();
                int totalPercent = finishTotalTasks.size() % totalTasks.size();
                final int percent = 57;
                final TaskStatistic result = TaskService.getInstance().getTaskStatistic("");
                final List<Task> rountTasks = TaskService.getInstance().getUnDoTask(InspectionType.routine.name());
                final List<Task> fullTask = TaskService.getInstance().getUnDoTask(InspectionType.full.name());
                final List<Task> specialCount = TaskService.getInstance().getUnDoSpecialTask(InspectionType.special.name());
                final List<Task> lightCout = TaskService.getInstance().getUnDoTask(InspectionType.special_xideng.name());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tourBinding.setResult(result);
                        tourBinding.setFullCount(fullTask.size() + "");
                        tourBinding.setRounteCount(rountTasks.size() + "");
                        tourBinding.setSpecailCount(specialCount.size() + "");
                        tourBinding.setLightCount(lightCout.size() + "");

                    }
                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tourBinding.progress.setProgress(i <= percent ? ++i : percent);
                        if (i < percent)
                            mHandler.postDelayed(this, 100);
                    }
                });
            }
        });
    }

    public void toTaskPlan(String inspectionType) {
        CustomApplication.closeDbConnection();
        Intent intent = new Intent();
        ComponentName componentName4 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, inspectionType);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(mCurrentActivity, Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(mCurrentActivity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.setComponent(componentName4);
        startActivity(intent);
    }
}
