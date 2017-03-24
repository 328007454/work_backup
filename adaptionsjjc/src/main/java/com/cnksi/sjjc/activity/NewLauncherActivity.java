package com.cnksi.sjjc.activity;

import android.content.ComponentName;
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
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.fragment.launcher.MaintenanceFragment;
import com.cnksi.sjjc.fragment.launcher.TourFragment;

import org.xutils.ex.DbException;

import java.util.ArrayList;

/**
 * Created by han on 2017/3/22.
 */

public class NewLauncherActivity extends BaseActivity {
    private ActivityLauncherNewBinding launcherBinding;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private int currentSelectPosition;
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
            switch (id) {
                case R.id.menu_record:
//                    launcherBinding.fragmenPager.setCurrentItem(0);
                    Intent homeIntent = new Intent();
                    homeIntent.setClass(getApplicationContext(), HomeActivity.class);
                    startActivity(homeIntent);

                    break;
                case R.id.menu_tour:
                    launcherBinding.fragmenPager.setCurrentItem(0);
                    break;
                case R.id.menu_maintenance:
                    launcherBinding.fragmenPager.setCurrentItem(1);
                    break;
                case R.id.menu_operate:
                    ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
                    Intent intent2 = new Intent();
                    intent2.setComponent(componentName);
                    intent2.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_USER, ""));
                    intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                    startActivity(intent2);
                    break;

                case R.id.menu_unify:
                    Intent intent1 = new Intent();
                    ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                    String typeName = InspectionType.operation.name();
                    intent1.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName);
                    intent1.putExtra(Config.CURRENT_LOGIN_USER, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_USER, ""));
                    intent1.putExtra(Config.CURRENT_LOGIN_ACCOUNT, (String) PreferencesUtils.get(_this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                    intent1.setComponent(componentName1);
                    startActivity(intent1);
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
        launcherBinding.mainRadioGroup.check(R.id.menu_tour);
        launcherBinding.lancherTitle.exitSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    private void initBaseData() {
        getIntentValue();
        currentSelectPosition = getIntent().getIntExtra("position", 0);
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
                switch (position) {
                    case 0:
                        launcherBinding.mainRadioGroup.check(R.id.menu_tour);
                        break;
                    case 1:
                        launcherBinding.mainRadioGroup.check(R.id.menu_maintenance);
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
        TourFragment tourFragment = new TourFragment();
        MaintenanceFragment maintenanceFragment = new MaintenanceFragment();
        fragmentList.add(tourFragment);
        fragmentList.add(maintenanceFragment);
    }


    @Override
    protected void onResume() {
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
