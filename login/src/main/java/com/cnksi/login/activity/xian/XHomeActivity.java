package com.cnksi.login.activity.xian;

import android.databinding.DataBindingUtil;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.login.R;
import com.cnksi.login.databinding.LoginActivityHomeBinding;
import com.cnksi.login.fragment.xian.WeekTaskFragment;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Mr.K  on 2018/7/4.
 * @decrption 西安巡视主页
 */
@Route(path = "/login/XHomeActivity")
public class XHomeActivity extends BaseTitleActivity implements OnTabSelectListener {

    LoginActivityHomeBinding homeBinding;
    private final String[] weekTitles = {InspectionType.SBXS.value, InspectionType.RCWH.value,InspectionType.LHSY.value,InspectionType.QXGL.value};
    private ArrayList<WeekTaskFragment> mFragments = new ArrayList<>();

    @Override
    public void getRootDataBinding() {
        homeBinding = DataBindingUtil.setContentView(this, R.layout.login_activity_home);
        initView();
    }

    private void initView() {
        getIntentValue();
        for (String title : weekTitles) {
            mFragments.add(WeekTaskFragment.getFragment(title));
        }
        FragmentPagerAdapter taskPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragments, Arrays.asList(weekTitles));
        homeBinding.viewPager.setAdapter(taskPagerAdapter);
        homeBinding.stWeek.setViewPager(homeBinding.viewPager);
        homeBinding.stWeek.setOnTabSelectListener(this);

    }


    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
