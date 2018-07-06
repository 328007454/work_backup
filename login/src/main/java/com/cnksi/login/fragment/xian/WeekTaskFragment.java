package com.cnksi.login.fragment.xian;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.czp.OperateTaskListActivity;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.defect.activity.OperateDefectActivity;
import com.cnksi.login.R;
import com.cnksi.login.adapter.HomeWeekDefectAdapter;
import com.cnksi.bdzinspection.adapter.xian.HomeWeekTaskAdapter;
import com.cnksi.login.databinding.LoginFragmentWeekTaskBinding;

import java.util.List;

/**
 * @author Mr.K on 2018/7/4.
 */

public class WeekTaskFragment extends BaseFragment {
    LoginFragmentWeekTaskBinding weekTaskBinding;
    private String title;
    private List<Task> tasks;
    private List<DefectRecord> defectRecords;
    private HomeWeekTaskAdapter taskAdapter;
    private HomeWeekDefectAdapter defectAdapter;

    public static WeekTaskFragment getFragment(String titleName) {
        WeekTaskFragment weekTaskFragment = new WeekTaskFragment();
        weekTaskFragment.title = titleName.split("（")[0];
        return weekTaskFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        weekTaskBinding = LoginFragmentWeekTaskBinding.inflate(inflater, container, false);
        return weekTaskBinding.getRoot();
    }

    @Override
    protected void lazyLoad() {
        ExecutorManager.executeTaskSerially(() -> {
            if (TextUtils.equals(InspectionType.getTypeFromValue(title), InspectionType.QXGL.name())) {
                defectRecords = DefectRecordService.getInstance().findAllDefectRecords();
            } else {
                tasks = TaskService.getInstance().getWeekTasks(InspectionType.getTypeFromValue(title));
            }
            getActivity().runOnUiThread(() -> {
                if (defectRecords != null && !defectRecords.isEmpty() && TextUtils.equals(InspectionType.getTypeFromValue(title), InspectionType.QXGL.name())) {
                    defectAdapter = new HomeWeekDefectAdapter(mActivity, R.layout.adapter_defect_item, defectRecords);
                    weekTaskBinding.rcy.setLayoutManager(new LinearLayoutManager(getContext()));
                    weekTaskBinding.rcy.setAdapter(defectAdapter);
                } else {
                    if (tasks == null) {
                        return;
                    }
                    taskAdapter = new HomeWeekTaskAdapter(R.layout.x_home_task_item, tasks);
                    weekTaskBinding.rcy.setLayoutManager(new LinearLayoutManager(getContext()));
                    weekTaskBinding.rcy.setAdapter(taskAdapter);
                }
                initItemClick();
            });
        });
    }

    private void initItemClick() {
        if (defectAdapter != null) {
            defectAdapter.setOnItemClickListener((adapter, view, position) -> {
                DefectRecord data = (DefectRecord) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), OperateDefectActivity.class);
                intent.putExtra(Device.DEVICEID, data.deviceid);
                intent.putExtra(Bdz.BDZID, data.bdzid);
                intent.putExtra(Config.DEFECT_COUNT_KEY, Config.SINGLE);
                intent.putExtra(DefectRecord.DEFECTID, data.defectid);
                intent.putExtra(Config.CURRENT_REPORT_ID, data.reportid);
                startActivity(intent);
            });
        }

        if (taskAdapter != null) {
            taskAdapter.setOnItemClickListener((adapter, view, position) -> {
                startTask((Task) adapter.getItem(position));
            });
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
            intent.setClass(getActivity(), OperateTaskListActivity.class);
        } else {
            intent.setClass(getActivity(), com.cnksi.bdzinspection.activity.TaskRemindActivity.class);
            intent.putExtra(Config.IS_FROM_SJJC, true);
        }
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE, task.inspection);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection_name);
        intent.putExtra("task_id", task.taskid);
        startActivity(intent);
    }


}
