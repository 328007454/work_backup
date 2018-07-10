package com.cnksi.bdzinspection.activity.xian;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityTourinspectionHomeBinding;
import com.cnksi.bdzinspection.fragment.xian.XTourFragmentTask;
import com.cnksi.bdzinspection.model.TableEntity;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.WeekTime;
import com.cnksi.common.model.Task;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cnksi.bdzinspection.activity.TaskRemindActivity.ADD_TASK_REQUEST_CODE;

/**
 * @author Mr.K  on 2018/7/4.
 */
@Route(path = "/xian/tourActivity")
public class TourInspectionHomeActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener, XTourFragmentTask.TaskCountInterface {
    XsActivityTourinspectionHomeBinding homeBinding;
    private String[] bottomTabs = {InspectionType.routine.value, InspectionType.full.value, InspectionType.special.value, InspectionType.special_xideng.value, InspectionType.professional.value};
    private String[] topTabs = {WeekTime.today.value, WeekTime.week.value, WeekTime.month.value, WeekTime.layoff.value};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private int[] mBottomUnselectIcon = {R.mipmap.x_ic_routin, R.mipmap.x_ic_full, R.mipmap.x_ic_special, R.mipmap.x_ic_lightoff, R.mipmap.x_ic_profession};
    private int[] mBottomselectIcon = {R.mipmap.x_ic_routin_hover, R.mipmap.x_ic_full_hover, R.mipmap.x_ic_special_hover, R.mipmap.x_ic_lightoff_hover, R.mipmap.x_ic_profession_hover};
    private List<XTourFragmentTask> xTourFragmentTasks = new ArrayList<>();
    private int pageSelectPosition;
    private int bottomTabPostion;
    private String inspectionType;
    private boolean isFirstLoad = true;
    private String fromInspection;

    @Override
    public void getRootDataBinding() {
        changedStatusColor();
        homeBinding = DataBindingUtil.setContentView(this, R.layout.xs_activity_tourinspection_home);
        getIntentValue();
        String tip = String.format((String) getText(R.string.welcome_login_user), currentDepartmentName, userName);
        homeBinding.txtLoginName.setText(tip);
        initView();
        initClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            reFreshUI();
        }
        isFirstLoad = false;
    }

    private void initClick() {
        homeBinding.ivAdd.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, com.cnksi.common.activity.AddTaskActivity.class);
            intent.putExtra(Config.CURRENT_INSPECTION_TYPE, inspectionType);
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
    }

    private void initView() {
        fromInspection = getIntent().getStringExtra(Config.INSPECTION_TYPE);
        if (!TextUtils.equals(fromInspection, InspectionType.SBXS.name())) {
            homeBinding.containerBottomTab.setVisibility(View.GONE);
        }

        for (int topTab = 0; topTab < topTabs.length; topTab++) {
            XTourFragmentTask fragmentTask = XTourFragmentTask.getFragment(topTabs[topTab], topTab);
            xTourFragmentTasks.add(fragmentTask);
            fragmentTask.setTaskCountInterface(this);
            if (TextUtils.equals(fromInspection, InspectionType.SBXS.name())) {
                inspectionType = InspectionType.getTypeFromValue(bottomTabs[bottomTabPostion]);
                fragmentTask.setCurrentInspectionType(inspectionType);
            } else {
                fragmentTask.setCurrentInspectionType(fromInspection);
                inspectionType = fromInspection;
            }
        }
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), xTourFragmentTasks, Arrays.asList(topTabs));
        homeBinding.viewPager.setAdapter(fragmentPagerAdapter);
        homeBinding.stWeek.setViewPager(homeBinding.viewPager);
        homeBinding.viewPager.setOffscreenPageLimit(4);

        for (int iconSize = 0; iconSize < mBottomselectIcon.length; iconSize++) {
            mTabEntities.add(new TableEntity(bottomTabs[iconSize], mBottomselectIcon[iconSize], mBottomUnselectIcon[iconSize]));
        }
        homeBinding.containerBottomTab.setTabData(mTabEntities);

        initClickCallBack();

    }

    private void initClickCallBack() {
        homeBinding.stWeek.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pageSelectPosition = position;

            }

            @Override
            public void onTabReselect(int position) {
                pageSelectPosition = position;
            }
        });

        homeBinding.containerBottomTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                bottomTabPostion = position;
                reFreshUI();
            }

            @Override
            public void onTabReselect(int position) {
                bottomTabPostion = position;
            }
        });
    }


    private void reFreshUI() {
        for (XTourFragmentTask fragmentTask : xTourFragmentTasks) {
            if (TextUtils.equals(fromInspection, InspectionType.SBXS.name())) {
                inspectionType = InspectionType.getTypeFromValue(bottomTabs[bottomTabPostion]);
                fragmentTask.setCurrentInspectionType(inspectionType);
            } else {
                fragmentTask.setCurrentInspectionType(fromInspection);
                inspectionType = fromInspection;
            }
            fragmentTask.initData();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void taskTotal(List<Task> tasks, int fragmentPosition) {
        switch (fragmentPosition) {
            case 0:
                resetTopTab(tasks, fragmentPosition);
                break;
            case 1:
                resetTopTab(tasks, fragmentPosition);
                break;
            case 2:
                resetTopTab(tasks, fragmentPosition);
                break;
            case 3:
                resetTopTab(tasks, fragmentPosition);
                break;
            default:
                break;
        }
    }

    private void resetTopTab(List<Task> tasks, int fragmentPosition) {
        if (tasks != null && !tasks.isEmpty()) {
            topTabs[fragmentPosition] = topTabs[fragmentPosition].split("（")[0];
            topTabs[fragmentPosition] = topTabs[fragmentPosition] + "（" + tasks.size() + "）";
            homeBinding.stWeek.getTitleView(fragmentPosition).setText(topTabs[fragmentPosition]);
        } else {
            if (topTabs[fragmentPosition].contains("（")) {
                topTabs[fragmentPosition] = topTabs[fragmentPosition].split("（")[0];
                homeBinding.stWeek.getTitleView(fragmentPosition).setText(topTabs[fragmentPosition]);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_TASK_REQUEST_CODE:
                    reFreshUI();
                    break;
                default:
                    break;
            }
        }
    }
}
