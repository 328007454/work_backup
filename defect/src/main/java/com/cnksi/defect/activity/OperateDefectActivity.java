package com.cnksi.defect.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.ActivityOperateDefectBinding;
import com.cnksi.defect.fragment.EliminateDefectFragment;
import com.cnksi.defect.fragment.TrackDefectFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mr.K on 2018/5/30.
 */

public class OperateDefectActivity extends BaseTitleActivity implements ViewPager.OnPageChangeListener {
    private ActivityOperateDefectBinding binding;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected View getChildContentView() {
        binding = ActivityOperateDefectBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void initUI() {
        setTitleText("缺陷管控");

        initFragments();
    }

    private void initFragments() {
        TrackDefectFragment trackDefectFragment = new TrackDefectFragment();
        fragments.add(trackDefectFragment);

        EliminateDefectFragment eliminateDefectFragment = new EliminateDefectFragment();
        fragments.add(eliminateDefectFragment);

        String[] titleArray = getResources().getStringArray(R.array.Defect_Indicator);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragments, Arrays.asList(titleArray));
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.tabStrip.setOnPageChangeListener(this);
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(2);
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
}
