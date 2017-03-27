package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.databinding.ActivityLauncherNewBinding;
import com.cnksi.sjjc.fragment.launcher.MaintenanceFragment;
import com.cnksi.sjjc.fragment.launcher.TourFragment;
import com.cnksi.sjjc.util.ActivityUtil;

import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by han on 2017/3/22.
 */

public class NewLauncherActivity extends BaseActivity {
    private ActivityLauncherNewBinding launcherBinding;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private int currentSelectPosition;
    private boolean isFromHomeActivity = true;
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
            switch (id) {
                case R.id.menu_record:
                    Intent homeIntent = new Intent();
                    homeIntent.setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);
                    NewLauncherActivity.this.finish();
                    break;
                case R.id.menu_tour:
                    launcherBinding.fragmenPager.setCurrentItem(0);
                    break;
                case R.id.menu_maintenance:
                    launcherBinding.fragmenPager.setCurrentItem(1);
                    break;
                case R.id.menu_operate:
                    isFromHomeActivity =false;
                    ActivityUtil.startOperateActivity(_this);
                    break;

                case R.id.menu_unify:
                    isFromHomeActivity = false;
                    ActivityUtil.startUnifyActivity(_this);
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
        initUI();
        initFragments();
        setFragmentToPager();
        initBaseData();

    }

    private void initUI() {
        launcherBinding.mainRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
        launcherBinding.lancherTitle.exitSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compeletlyExitSystem();
            }
        });
    }

    private void initBaseData() {
        getIntentValue();
        String date = com.cnksi.core.utils.DateUtils.getCurrentTime("yyy年MM月dd日") + " " + DateUtils.getCurrentWeekDay();
        launcherBinding.lancherTitle.txtTime.setText(date);
        launcherBinding.lancherTitle.txtPerson.setText(PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_USER, ""));
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String deparmentId = PreferencesUtils.get(_this, Config.CURRENT_DEPARTMENT_ID, "");
                try {
                    final Department department = CustomApplication.getDbManager().selector(Department.class).where(Department.ID, "=", deparmentId).and(Department.DLT, "<>", 1).findFirst();
                    if (null != department)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                launcherBinding.lancherTitle.txtTeam.setText(department.name);
                            }
                        });
                } catch (DbException e) {
                }
            }
        });
    }

    private void setFragmentToPager() {
        launcherBinding.fragmenPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        launcherBinding.fragmenPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentSelectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private void initFragments() {
        TourFragment tourFragment = new TourFragment();
        MaintenanceFragment maintenanceFragment = new MaintenanceFragment();
        fragmentList.add(tourFragment);
        fragmentList.add(maintenanceFragment);
    }


    @Override
    protected void onResume() {
        if (getIntent() != null && isFromHomeActivity) {
            currentSelectPosition = getIntent().getIntExtra("position", 0);
        }
        super.onResume();
        switch (currentSelectPosition) {
            case 0:
                launcherBinding.fragmenPager.setCurrentItem(0);
                launcherBinding.menuTour.setChecked(true);
                break;
            case 1:
                launcherBinding.fragmenPager.setCurrentItem(1);
                launcherBinding.menuMaintenance.setChecked(true);
                break;
            default:
                break;
        }
    }
}
