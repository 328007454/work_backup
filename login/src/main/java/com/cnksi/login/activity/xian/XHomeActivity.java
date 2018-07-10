package com.cnksi.login.activity.xian;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.login.R;
import com.cnksi.login.adapter.WeekTaskPagerAdapter;
import com.cnksi.login.databinding.LoginActivityHomeBinding;
import com.cnksi.login.fragment.xian.WeekTaskFragment;
import com.cnksi.login.util.ActivityUtil;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K  on 2018/7/4.
 * @decrption 西安巡视主页
 */
@Route(path = "/login/XHomeActivity")
public class XHomeActivity extends BaseTitleActivity implements OnTabSelectListener {

    LoginActivityHomeBinding homeBinding;
    private final List<String> weekTitles = new ArrayList<>();
    private final String[] titleTypes = {InspectionType.SBXS.value, InspectionType.RCWH.value, InspectionType.LHSY.value, InspectionType.QXGL.value,InspectionType.SBJC.value};
    private ArrayList<WeekTaskFragment> mFragments = new ArrayList<>();
    private boolean isNotFirstLoad;

    @Override
    public void getRootDataBinding() {
        changedStatusColor();
        homeBinding = DataBindingUtil.setContentView(this, R.layout.login_activity_home);
        getIntentValue();
        String tip = String.format((String) getText(R.string.welcome_login_user), currentDepartmentName, userName);
        homeBinding.txtLoginName.setText(tip);
        getData();
        initOnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotFirstLoad) {
            getData();
        }
        isNotFirstLoad = true;
    }

    private void initView() {
        mFragments.clear();
        if (weekTitles.size() == 0) {
            return;
        }
        for (String title : weekTitles) {
            mFragments.add(WeekTaskFragment.getFragment(title));
        }
        WeekTaskPagerAdapter taskPagerAdapter = new WeekTaskPagerAdapter(getSupportFragmentManager(), mFragments, weekTitles);
        homeBinding.viewPager.setAdapter(taskPagerAdapter);
        homeBinding.stWeek.setViewPager(homeBinding.viewPager);
        homeBinding.stWeek.setOnTabSelectListener(this);

    }

    public void getData() {
        ExecutorManager.executeTaskSerially(() -> {
            weekTitles.clear();
            for (int i = 0; i < titleTypes.length; i++) {
                String title = titleTypes[i];
                if (TextUtils.equals(title, InspectionType.QXGL.value)) {
                    List<DefectRecord> defectRecords = DefectRecordService.getInstance().findAllDefectRecords();
                    putTitle(defectRecords, i, title);
                } else {
                    List<Task> tasks = TaskService.getInstance().getWeekTasks(InspectionType.getTypeFromValue(title));
                    putTitle(tasks, i, title);
                }
            }
            runOnUiThread(() -> {
                initView();
            });
        });
    }


    private void putTitle(List<? extends Object> objects, int position, String title) {
        if (objects != null && !objects.isEmpty()) {
            weekTitles.add(title + "（ " + objects.size() + " ）");
        }
    }


    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    private void initOnClick() {
        homeBinding.includeType.txtSync.setOnClickListener(v -> {
            ActivityUtil.startSync(mActivity);
        });
        homeBinding.includeType.txtTour.setOnClickListener(v -> {
            ActivityUtil.startTourActivity(mActivity,InspectionType.SBXS.name());
        });

        homeBinding.includeType.txtMaintance.setOnClickListener(v -> {
            ActivityUtil.startTourActivity(mActivity,InspectionType.maintenance.name());
        });

        homeBinding.includeType.txtSwitchover.setOnClickListener(v -> {
            ActivityUtil.startTourActivity(mActivity,InspectionType.switchover.name());
        });

        homeBinding.includeType.txtCopy.setOnClickListener(v -> {
            ActivityUtil.startShuJuJianCe(mActivity);
        });
        homeBinding.includeType.txtDefect.setOnClickListener(v -> {
            ActivityUtil.startDefectControlActivity(mActivity);
        });

        homeBinding.includeType.txtWorkTicket.setOnClickListener(v -> {
            ActivityUtil.startTicketDateModel(mActivity);
        });
        homeBinding.includeType.txtOperation.setOnClickListener(v -> {
            ActivityUtil.startOperateActivity(mActivity);
        });
        homeBinding.includeType.txtSafetyTool.setOnClickListener(v -> {
            ActivityUtil.startSafetyToolActivity(mActivity);
        });
        homeBinding.includeType.txtModfyRecord.setOnClickListener(v -> {

        });

        homeBinding.includeType.txtEducation.setOnClickListener(v -> {
            ActivityUtil.startWTYCActiviy(mActivity);
        });


    }
}
