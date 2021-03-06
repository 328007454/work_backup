package com.cnksi.login.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.location.BDLocation;
import com.cnksi.bdloc.DistanceUtil;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.common.Config;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Department;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.adapter.DialogBDZAdapter;
import com.cnksi.login.fragment.MaintenanceFragment;
import com.cnksi.login.fragment.TourFragment;
import com.cnksi.login.util.ActivityUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseSjjcActivity;
import com.cnksi.sjjc.databinding.ActivityLauncherNewBinding;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

@Route(path="/login/NewLauncherActivity")
public class NewLauncherActivity extends BaseSjjcActivity {
    private ActivityLauncherNewBinding launcherBinding;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private int currentSelectPosition;
    public boolean isFromHomeActivity = true;
    private ListView mPowerStationListView;
    private Dialog mPowerStationDialog = null;
    private List<Bdz> bdzList;
    LocationUtil.LocationHelper locationHelper;
    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int id) {
            switch (id) {
                case R.id.menu_record:
                    onBackPressed();
                    break;
                case R.id.menu_tour:
                    launcherBinding.fragmenPager.setCurrentItem(0);
                    break;
                case R.id.menu_maintenance:
                    launcherBinding.fragmenPager.setCurrentItem(1);
                    break;
                case R.id.menu_operate:
                    isFromHomeActivity = false;
                    ActivityUtil.startOperateActivity(mActivity);
                    break;

                case R.id.menu_unify:
                    isFromHomeActivity = false;
                    ActivityUtil.startUnifyActivity(mActivity);
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDefaultTitle = false;
        super.onCreate(savedInstanceState);
        changedStatusColor();
        launcherBinding = DataBindingUtil.setContentView(this, R.layout.activity_launcher_new);
        inUI();
        initFragments();
        setFragmentToHomePager();
        initBaseData();
        launcherBinding.mainRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public void initUI() {

    }


    public void inUI() {
        getIntentValue();
        launcherBinding.mainRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
        launcherBinding.lancherTitle.exitSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showSureTipsDialog(mActivity, null, "是否退出登录?", "确定", "取消", v -> {

                    Intent intent = new Intent(mActivity, LoginActivity.class);
                    startActivity(intent);
                    mActivity.finish();
                });
            }
        });
        launcherBinding.lancherTitle.bdzLocationContainer.setOnClickListener(view -> mPowerStationDialog.show());
        locationHelper = LocationUtil.getInstance().getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                List<DbModel> spacingModels = SpacingService.getInstance().findBdzBySpacing();
                if (spacingModels != null && !spacingModels.isEmpty()) {
                    for (DbModel dbModel : spacingModels) {

                        LatLng bdzLocation = LatLng.valueOf(dbModel.getString(Spacing.LATITUDE), dbModel.getString(Spacing.LONGITUDE));
                        if (bdzLocation == null) {
                            continue;
                        }
                        // 200米范围内
                        if (DistanceUtil.getDistance(currentLocation, bdzLocation) < 200) {
                            String bdzid = dbModel.getString(Spacing.BDZID);
                            try {
                                Bdz bdz = BdzService.getInstance().findById(bdzid);
                                if (bdz != null) {
                                    launcherBinding.lancherTitle.txtBdz.setText(bdz.name);
                                    PreferencesUtils.put(Config.LOCATION_BDZID, bdz.bdzid);
                                    PreferencesUtils.put(Config.LOCATION_BDZNAME, bdz.name);
                                    break;
                                }
                            } catch (DbException e) {
                                Log.d("Tag", e.getMessage());
                            }
                        }
                    }
                }
            }
        }).setPeriod(30);
        locationHelper.start();
    }

    @Override
    public void initData() {

    }

    private void initBaseData() {
        getIntentValue();
        String date = DateUtils.getCurrentTime("yyy年MM月dd日") + " " + DateUtils.getCurrentWeekDay();
        launcherBinding.lancherTitle.txtTime.setText(date);
        launcherBinding.lancherTitle.txtPerson.setText(PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        ExecutorManager.executeTaskSerially(() -> {
            String deparmentId = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, "");
            try {
                bdzList = BdzService.getInstance().findAllBdzByDp(currentDepartmentId);
                final Department department = DepartmentService.getInstance().findDepartmentById(deparmentId);
                if (null != department) {
                    runOnUiThread(() -> {
                        launcherBinding.lancherTitle.txtTeam.setText(department.name);
                        initBDZDialog();
                        if (!bdzList.isEmpty()) {
                            launcherBinding.lancherTitle.txtBdz.setText(bdzList.isEmpty() ? "" : bdzList.get(0).name);
                            PreferencesUtils.put(Config.LAST_CHOOSE_BDZ_NAME_KEY, bdzList.get(0).bdzid);
                            PreferencesUtils.put(Config.LOCATION_BDZID, bdzList.get(0).bdzid);
                            PreferencesUtils.put(Config.LOCATION_BDZNAME, bdzList.get(0).name);
                            PreferencesUtils.put(Config.CURRENT_DEPARTMENT_NAME, department.name);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setFragmentToHomePager() {
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
            isFromHomeActivity = false;
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
        locationHelper.resume();
    }

    private void initBDZDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        final int dialogHeight = bdzList.size() > 8 ? DisplayUtils.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        final ViewHolder holder = new ViewHolder(this, null, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, bdzList, R.layout.dialog_content_child_item);
        adapter.setItemClickListener((v, bdz, position) -> {
            if (!bdz.name.contains("未激活")) {
                launcherBinding.lancherTitle.txtBdz.setText(bdz.name);
                PreferencesUtils.put(Config.LAST_CHOOSE_BDZ_NAME_KEY, bdz.bdzid);
                PreferencesUtils.put(Config.LOCATION_BDZID, bdz.bdzid);
                PreferencesUtils.put(Config.LOCATION_BDZNAME, bdz.name);
                mPowerStationDialog.dismiss();
                locationHelper.stop();
            } else {
                ToastUtils.showMessage("该变电站未激活");
            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationHelper.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.destory();
    }
}
