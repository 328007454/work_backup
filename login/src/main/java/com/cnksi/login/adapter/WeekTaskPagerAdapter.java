package com.cnksi.login.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mr.K on 2018/7/4.
 */

public class WeekTaskPagerAdapter<T> extends android.support.v4.app.FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    private List<String> titles;

    public WeekTaskPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
