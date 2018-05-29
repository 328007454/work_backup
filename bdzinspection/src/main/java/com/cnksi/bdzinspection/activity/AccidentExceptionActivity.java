package com.cnksi.bdzinspection.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityAccidentExceptionBinding;
import com.cnksi.bdzinspection.fragment.AccidentDealFragment;
import com.cnksi.bdzinspection.fragment.MotionRuleFragment;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 运行规定界面
 *
 * @author kkk  on 2017/10/19
 */
public class AccidentExceptionActivity extends BaseActivity implements OnPageChangeListener {

    private String[] titleArray;
    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;
    private XsActivityAccidentExceptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_accident_exception);
        initialUI();
        initFragments();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
        binding.includeTitle.tvTitle.setText("运行规定");
    }

    private void initFragments() {
        titleArray = getResources().getStringArray(R.array.XS_AccidentDeal);
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        MotionRuleFragment ruleFragment = new MotionRuleFragment();
        AccidentDealFragment dealFragment = new AccidentDealFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Config.CURRENT_DEVICE_ID, currentDeviceId);
        ruleFragment.setArguments(bundle);
        dealFragment.setArguments(bundle);
        mFragmentList.add(ruleFragment);
        mFragmentList.add(dealFragment);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragmentList, Arrays.asList(titleArray));
        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.tabStrip.setOnPageChangeListener(this);
        setPagerTabStripValue(binding.tabStrip);
        binding.viewPager.setOffscreenPageLimit(2);
    }


    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> AccidentExceptionActivity.this.finish());

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        binding.includeTitle.tvTitle.setText(titleArray[arg0]);
    }

    /**
     * 对PagerSlidingbinding.tabStriptrip的各项属性进行赋值。
     */
    @Override
    public void setPagerTabStripValue(PagerSlidingTabStrip tabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = mActivity.getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        tabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        int textSize = AutoUtils.getPercentHeightSizeBigger((int) mActivity.getResources().getDimension(R.dimen.xs_accidential_tab_strip_text_size_px));
        tabStrip.setTextSize(textSize);
        // 设置Tab Indicator的颜色
        tabStrip.setIndicatorColor(mActivity.getResources().getColor(R.color.xs_tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabStrip.setSelectedTextColor(mActivity.getResources().getColor(R.color.xs_tab_strip_text_color));
        // 取消点击Tab时的背景色
        tabStrip.setTabBackground(0);
    }

}
