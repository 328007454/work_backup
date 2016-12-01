package com.cnksi.sjjc.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksi-android on 2016/5/5.
 */
public class WorkTicketActivity  extends  BaseActivity{

    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;
    private List<String> titleArray = null;
    @ViewInject(R.id.tab_strip)
    private PagerSlidingTabStrip mPagerTabStrip;
    @ViewInject(R.id.viewPager)
    private ViewPager mTaskViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private void initUI(){
       setTitleText("110KV地区传输网传输设备清洗");
        setChildView(R.layout.activity_workticket);
    }

    private void initData()
    {

    }


}
