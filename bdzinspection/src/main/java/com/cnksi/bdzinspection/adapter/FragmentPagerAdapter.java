package com.cnksi.bdzinspection.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {

	private List<? extends  Fragment> fragmentList;
	private List<String> titleArray;
	private FragmentManager fm;

	public FragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public FragmentPagerAdapter(FragmentManager fm, List<? extends Fragment> fragmentList2, List<String> titleArray) {
		this(fm);
		this.fragmentList = fragmentList2;
		this.titleArray = titleArray;
	}

	public void setTitleArray(List<String> titleArray) {
		this.titleArray = titleArray;
	}

	public FragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
		this(fm);
		this.fragmentList = fragmentList;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragmentList != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragmentList) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragmentList = fragments;
		notifyDataSetChanged();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if (titleArray != null) {
			return titleArray.get(position);
		}
		return "";
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		return super.instantiateItem(container, position);
	}
}
