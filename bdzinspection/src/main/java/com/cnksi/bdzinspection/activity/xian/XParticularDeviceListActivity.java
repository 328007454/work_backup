package com.cnksi.bdzinspection.activity.xian;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.CopyAllValueActivity2;
import com.cnksi.bdzinspection.activity.GenerateReportActivity;
import com.cnksi.bdzinspection.activity.LocationSpacingActivity;
import com.cnksi.bdzinspection.daoservice.SpacingLastlyService;
import com.cnksi.bdzinspection.databinding.XsActivityParticularInspectionBinding;
import com.cnksi.bdzinspection.fragment.ParticularDevicesFragment;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.utils.ShakeListener;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.listener.AbstractPageChangeListener;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sync.KSyncConfig;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;


/**
 * 特殊巡检设备列表界面
 * Created by han on 2017/2/27.
 */

public class XParticularDeviceListActivity extends BaseActivity implements ShakeListener.OnShakeListener {

    Task currentTask;
    private List<Lookup> lookups = null;
    private List<ParticularDevicesFragment> fragmentList;
    private List<String> fragmentTitleList;
    private int currentPosition;
    private ParticularDevicesFragment currentFragment;
    private boolean shaking;
    private ShakeListener shakeListener;
    private LocationUtil.LocationHelper locationHelper;

    private XsActivityParticularInspectionBinding binding;

    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_particular_inspection);
        getIntentValue();
        initialUI();
        initialData();
        initOnClick();
    }


    private void initialData() {
        ExecutorManager.executeTask(() -> currentTask = TaskService.getInstance().findById(currentTaskId));
    }

    private void initialUI() {
        if (SystemConfig.isSpecialInspectionNeedCopy()) {
            binding.ibtnAdd.setVisibility(View.VISIBLE);
            binding.ibtnAdd.setImageResource(R.drawable.xs_copy_button_background);
        }
        shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(this);

        binding.tvTitle.setText(currentBdzName + currentInspectionTypeName);
        TTSUtils.getInstance().startSpeaking(getString(R.string.xs_speak_content_format_str, currentInspectionTypeName));
        // 从Lookup表中查询电压等级
        lookups = LookupService.getInstance().findLookupByType(LookUpType.pmsDeviceType.name(), true);

        fragmentTitleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        for (Lookup look : lookups) {
            ParticularDevicesFragment fragment = new ParticularDevicesFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Config.CURRENT_FUNCTION_MODEL, look.k);
            bundle.putString(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
            bundle.putBoolean(Config.IS_PARTICULAR_INSPECTION, true);
            // 设置键盘事件
            fragment.setArguments(bundle);
            fragmentTitleList.add(look.v);
            fragmentList.add(fragment);
        }
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.stWeek.setViewPager(binding.viewPager);
        AbstractPageChangeListener pageChangeListener = new AbstractPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                currentFragment = fragmentList.get(position);
            }

        };
        binding.tabStrip.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);

        locationHelper = LocationUtil.getInstance().getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                int i = location.getSatelliteNumber() / 4;
                int signalIcon = 0;
                switch (i) {
                    case 0:
                        signalIcon = 0;
                        break;
                    case 1:
                        signalIcon = R.drawable.xs_ic_signal1;
                        break;
                    case 2:
                        signalIcon = R.drawable.xs_ic_signal2;
                        break;
                    case 3:
                        signalIcon = R.drawable.xs_ic_signal3;
                        break;
                    case 4:
                        signalIcon = R.drawable.xs_ic_signal4;
                        break;
                    default:
                }
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, signalIcon, 0);
                currentFragment.locationSuccess(location);
            }
        }).setKeep(true);
        locationHelper.start();
    }


    private void initOnClick() {

        binding.ibtnCancel.setOnClickListener(view -> XParticularDeviceListActivity.this.onBackPressed());

        binding.btnFinishInspection.setOnClickListener(view -> {
            if (SystemConfig.isDevicePlaced()) {
                fragmentList.get(0).handleSpaceArrivedData();
            }
            if (currentTask.isMember()) {
                DialogUtils.showSureTipsDialog(mActivity, null, "作为分组巡视成员,点击确认后会同步本次巡视任务", "确认并同步", "取消", v -> {
                    CustomerDialog.showProgress(mActivity, "正在上传任务", true, false);
                    KSyncConfig.getInstance().getKNConfig(new KSyncConfig.SyncListener() {

                        @Override
                        public void start() {
                            ToastUtils.showMessage("开始上传数据！");
                        }

                        @Override
                        public void onSuccess(KSyncConfig.Type errorType) {
                            ToastUtils.showMessage("数据上传成功");
                            CustomerDialog.dismissProgress();
                            ExitThisAndGoLauncher();
                        }

                        @Override
                        public void onError(KSyncConfig.Type errorType, String errorMsg) {
                            if (errorType == KSyncConfig.Type.network) {
                                ToastUtils.showMessage("请检查网络，在主页手动同步");
                            } else {
                                ToastUtils.showMessage("上传失败，请在主页手动同步！错误提示：" + errorMsg);
                            }
                            CustomerDialog.dismissProgress();
                            ExitThisAndGoLauncher();
                        }
                    }).upload();
                });
            } else {
                Intent intent = new Intent(mActivity, GenerateReportActivity.class);
                showTipsDialog(intent);
            }
        });

        binding.ibtnAdd.setOnClickListener(view -> {
            PlaySound.getIntance(mActivity).play(R.raw.input);
            Intent intent = new Intent(mActivity, CopyAllValueActivity2.class);
            startActivity(intent);
        });


    }


    @Override
    synchronized public void onShake() {
        if (currentPosition != 0) {
            ToastUtils.showMessage("摇一摇功能只能在一次设备列表使用");
        } else {
            if (!shaking) {
                shaking = true;
                CustomerDialog.showProgress(this, R.string.xs_locating_str);
                LocationUtil.getInstance().getLocalHelper(new LocationListener() {
                    @Override
                    public void locationSuccess(BDLocation location) {
                        CustomerDialog.dismissProgress();
                        Intent intent = new Intent(mActivity, LocationSpacingActivity.class);
                        intent.putExtra(Config.CURRENT_FUNCTION_MODEL, "one");
                        intent.putExtra(Config.CURRENT_LAT_LNG_KEY, new LatLng(location));
                        mActivity.startActivityForResult(intent, Config.SHAKE_SPACE);
                    }

                    @Override
                    public void locationFailure(int code, String message) {
                        if (code == -16) {
                            shaking = false;
                            CustomerDialog.dismissProgress();
                            ToastUtils.showMessage("摇一摇定位失败！");
                        }
                    }
                }).setTimeout(10).start();
                PlaySound.getIntance(this).play(R.raw.swing_appear);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.SHAKE_SPACE) {
            shaking = false;
        }
    }

    @Override
    protected void onDestroy() {
        shakeListener.stop();
        locationHelper.destory();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.resume();
        long endTime = System.currentTimeMillis();
        Log.d("Tag", "showTime:---" + (endTime - startTime));
    }


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(mActivity, "再点一次，退出巡视页面", Toast.LENGTH_SHORT).show();
        } else {
            final List<SpacingLastly> saveList = new ArrayList<>();
            for (ParticularDevicesFragment fragment : fragmentList) {
                SpacingLastly lastly = fragment.getSpacingLastly();
                if (lastly == null) {
                    continue;
                } else {
                    saveList.add(lastly);
                }
            }
            if (saveList.size() > 0) {
                ExecutorManager.executeTask(() -> {
                    try {
                        SpacingLastlyService.getInstance().saveOrUpdate(saveList);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                });
            }
            super.onBackPressed();
        }
    }
}