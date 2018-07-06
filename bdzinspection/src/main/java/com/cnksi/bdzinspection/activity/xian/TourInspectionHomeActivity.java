package com.cnksi.bdzinspection.activity.xian;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityTourinspectionHomeBinding;
import com.cnksi.bdzinspection.fragment.xian.XTourFragmentTask;
import com.cnksi.bdzinspection.model.TableEntity;
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

/**
 * @author Mr.K  on 2018/7/4.
 */

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

    @Override
    public void getRootDataBinding() {
        changedStatusColor();
        homeBinding = DataBindingUtil.setContentView(this, R.layout.xs_activity_tourinspection_home);
        getIntentValue();
        initView();
    }

    private void initView() {
        for (int topTab = 0; topTab < topTabs.length; topTab++) {
            XTourFragmentTask fragmentTask = XTourFragmentTask.getFragment(topTabs[topTab], topTab);
            xTourFragmentTasks.add(fragmentTask);
            fragmentTask.setTaskCountInterface(this);
            fragmentTask.setCurrentInspectionType(InspectionType.getTypeFromValue(bottomTabs[bottomTabPostion]));
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
                for (XTourFragmentTask fragmentTask : xTourFragmentTasks) {
                    fragmentTask.setCurrentInspectionType(InspectionType.getTypeFromValue(bottomTabs[bottomTabPostion]));
                    fragmentTask.initData();
                }
            }

            @Override
            public void onTabReselect(int position) {
                bottomTabPostion = position;
            }
        });
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
}
