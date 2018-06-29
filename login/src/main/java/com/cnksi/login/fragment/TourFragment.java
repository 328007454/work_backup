package com.cnksi.login.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.vo.TaskStatistic;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.login.R;
import com.cnksi.login.databinding.FragmentTourBinding;
import com.cnksi.nari.NariActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 设备巡视
 * Created by han on 2017/3/22.
 */

public class TourFragment extends BaseCoreFragment {
    FragmentTourBinding tourBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tourBinding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.fragment_tour, container, false);
        AutoUtils.auto(tourBinding.getRoot());
        return tourBinding.getRoot();
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

    int i = 0;

    @Override
    public void onResume() {
        super.onResume();
        tourBinding.setEvent(this);
        initData();

    }


    protected void initData() {
        ExecutorManager.executeTaskSerially(new Runnable() {
            @Override
            public void run() {
                final TaskStatistic result = TaskService.getInstance().getTaskStatistic("");
                final List<Task> rountTasks = TaskService.getInstance().getUnDoTask(InspectionType.routine.name());
                final List<Task> fullTask = TaskService.getInstance().getUnDoTask(InspectionType.full.name());
                final List<Task> specialCount = TaskService.getInstance().getUnDoSpecialTask(InspectionType.special.name());
                final List<Task> lightCout = TaskService.getInstance().getUnDoTask(InspectionType.special_xideng.name());
                final List<Task> professionalCout = TaskService.getInstance().getUnDoTask(InspectionType.professional.name());
                int totalPercent = (int) (((float) result.getMonthTaskFinish() / result.getMonthTaskCount()) * 100);
                final int percent = totalPercent;
                getActivity().runOnUiThread(() -> {
                    tourBinding.setResult(result);
                    tourBinding.setFullCount(fullTask.size() + "");
                    tourBinding.setRounteCount(rountTasks.size() + "");
                    tourBinding.setSpecailCount(specialCount.size() + "");
                    tourBinding.setLightCount(lightCout.size() + "");
                    tourBinding.setProfessionalCount(professionalCout.size() + "");

                });
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tourBinding.progress.setProgress(i <= percent ? i++ : percent);
                        if (i <= percent) {
                            mHandler.postDelayed(this, 100);
                        }
                    }
                });
            }
        });
    }

    /**
     * 跳转到对应的任务列表界面
     */
    public void toTaskPlan(String inspectionType) {
        PreferencesUtils.put(Config.KEY_SYNC_URL, Config.SYNC_URL);
        PreferencesUtils.put(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
        Intent intent = new Intent();
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.LAST_CHOOSE_BDZ_NAME_KEY, PreferencesUtils.get(Config.LAST_CHOOSE_BDZ_NAME_KEY, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_NAME, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_NAME, ""));
        intent.putExtra(Config.KEY_SYNC_URL, Config.SYNC_URL);
        intent.putExtra(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
        PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE, inspectionType);
        PreferencesUtils.get(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.get(inspectionType).value);
        if (TextUtils.isEmpty(inspectionType)) {
            intent.setClass(getActivity(), NariActivity.class);
        } else {

            intent.setClass(getActivity(), TaskRemindActivity.class);
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE, inspectionType);
        }
        startActivity(intent);
    }
}
