package com.cnksi.sjjc.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.cnksi.core.utils.CToast;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.databinding.ActivityLauncherNewBinding;
import com.cnksi.sjjc.fragment.launcher.HomePageFragment;
import com.cnksi.sjjc.fragment.launcher.MaintenanceFragment;
import com.cnksi.sjjc.fragment.launcher.OperateFragment;
import com.cnksi.sjjc.fragment.launcher.TourFragment;
import com.cnksi.sjjc.fragment.launcher.UnifyFragment;

import java.util.ArrayList;

/**
 * Created by han on 2017/3/22.
 */

public class NewLauncherActivity extends BaseActivity {
    private ActivityLauncherNewBinding launcherBinding;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
            switch (id) {
                case R.id.menu_tour:
                    CToast.showLong(mCurrentActivity, "1");
                    launcherBinding.fragmenPager.setCurrentItem(0);
                    break;
                case R.id.menu_maintenance:
                    launcherBinding.fragmenPager.setCurrentItem(1);
                    CToast.showLong(mCurrentActivity, "2");
                    break;

                case R.id.menu_record:
                    launcherBinding.fragmenPager.setCurrentItem(2);
                    CToast.showLong(mCurrentActivity, "3");
                    break;
                case R.id.menu_operate:
                    launcherBinding.fragmenPager.setCurrentItem(3);
                    CToast.showLong(mCurrentActivity, "4");
                    break;

                case R.id.menu_unify:
                    launcherBinding.fragmenPager.setCurrentItem(4);
                    CToast.showLong(mCurrentActivity, "5");
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcherBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.activity_launcher_new, null, false);
        setContentView(launcherBinding.getRoot());
        launcherBinding.mainRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
        initFragments();
        setFragmentToPager();
        launcherBinding.lancherTitle.txtTeam.setText("乐山供电公司二班");
        launcherBinding.lancherTitle.txtBdz.setText("桃乡变电站");
    }

    private void setFragmentToPager() {
        launcherBinding.fragmenPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        launcherBinding.fragmenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        launcherBinding.mainRadioGroup.check(R.id.menu_tour);
                        break;
                    case 1:
                        launcherBinding.mainRadioGroup.check(R.id.menu_maintenance);
                        break;
                    case 2:
                        launcherBinding.mainRadioGroup.check(R.id.menu_record);
                        break;
                    case 3:
                        launcherBinding.mainRadioGroup.check(R.id.menu_operate);
                        break;
                    case 4:
                        launcherBinding.mainRadioGroup.check(R.id.menu_unify);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initFragments() {
        HomePageFragment homeFragment = new HomePageFragment();
        TourFragment tourFragment = new TourFragment();
        MaintenanceFragment maintenanceFragment = new MaintenanceFragment();
        OperateFragment operateFragment = new OperateFragment();
        UnifyFragment unifyFragment = new UnifyFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(tourFragment);
        fragmentList.add(maintenanceFragment);
        fragmentList.add(operateFragment);
        fragmentList.add(unifyFragment);
    }

}
