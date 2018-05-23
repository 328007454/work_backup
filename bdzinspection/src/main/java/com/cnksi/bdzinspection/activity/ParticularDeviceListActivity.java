package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.LookupService;
import com.cnksi.bdzinspection.databinding.XsActivityParticularInspectionBinding;
import com.cnksi.bdzinspection.fragment.ParticularDevicesFragment;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.ShakeListener;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sync.KSyncConfig;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.ksynclib.KSync.SYNC_ERROR;
import static com.cnksi.ksynclib.KSync.SYNC_INFO;
import static com.cnksi.ksynclib.KSync.SYNC_START;
import static com.cnksi.ksynclib.KSync.SYNC_SUCCESS;


/**
 * 特殊巡检设备列表界面
 * Created by han on 2017/2/27.
 */

public class ParticularDeviceListActivity extends BaseActivity implements ViewPager.OnPageChangeListener, ShakeListener.OnShakeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_particular_inspection);
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
            bundle.putBoolean(Config.SEARCH_DEVICE_KEY, look.k.equals(Config.SEARCH_DEVICE_KEY));
            bundle.putBoolean(Config.IS_PARTICULAR_INSPECTION, true);
            // 设置键盘事件
            fragment.setArguments(bundle);
            fragmentTitleList.add(look.v);
            fragmentList.add(fragment);
        }
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, fragmentTitleList);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        setPagerTabStripValue(binding.tabStrip);
        binding.tabStrip.setTabPaddingLeftRight(37);
        binding.tabStrip.setShouldExpand(false);
        binding.tabStrip.setOnPageChangeListener(this);
        onPageSelected(0);

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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        currentFragment = fragmentList.get(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void initOnClick() {

        binding.ibtnCancel.setOnClickListener(view -> onBackPressed());

        binding.btnFinishInspection.setOnClickListener(view -> {
            if (SystemConfig.isDevicePlaced()) {
                fragmentList.get(0).handleSpaceArrivedData();
            }

            if (currentTask.isMember()) {
                DialogUtils.showSureTipsDialog(currentActivity, null, "作为分组巡视成员,点击确认后会同步本次巡视任务", "确认并同步", "取消", new OnViewClickListener() {
                    @Override
                    public void onClick(View v) {
                        super.onClick(v);
                        CustomerDialog.showProgress(currentActivity, "正在上传任务", true, false);
                        KSyncConfig.getInstance().getKNConfig(currentActivity, mHandler).upload();
                    }
                });
            } else {
                Intent intent = new Intent(currentActivity, GenerateReportActivity.class);
                showTipsDialog(binding.llRootContainer, intent);
            }
        });

        binding.ibtnAdd.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            Intent intent = new Intent(currentActivity, CopyAllValueActivity2.class);
            startActivity(intent);
        });


    }


    @Override
    synchronized public void onShake() {
        if (currentPosition != 0) {
            ToastUtils.showMessage( "摇一摇功能只能在一次设备列表使用");
        } else {
            if (!shaking) {
                shaking = true;
                CustomerDialog.showProgress(this, R.string.xs_locating_str);
                LocationUtil.getInstance().getLocalHelper(new LocationListener() {
                    @Override
                    public void locationSuccess(BDLocation location) {
                        CustomerDialog.dismissProgress();
                        Intent intent = new Intent(currentActivity, LocationSpacingActivity.class);
                        intent.putExtra(Config.CURRENT_FUNCTION_MODEL, "one");
                        intent.putExtra(Config.CURRENT_LATLNG, new LatLng(location));
                        currentActivity.startActivityForResult(intent, Config.SHAKE_SPACE);
                    }

                    @Override
                    public void locationFailure(int code, String message) {
                        if (code == -16) {
                            shaking = false;
                            CustomerDialog.dismissProgress();
                            ToastUtils.showMessage( "摇一摇定位失败！");
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
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case SYNC_START:
                String messageStart = (String) msg.obj;
                ToastUtils.showMessage( messageStart);
                break;
            case SYNC_INFO:
                break;
            case SYNC_SUCCESS:
                String messageSuccess = (String) msg.obj;
                ToastUtils.showMessage( messageSuccess);
                CustomerDialog.dismissProgress();
                ExitThisAndGoLauncher();
                break;
            case SYNC_ERROR:
                ToastUtils.showMessage( "请检查网络，在主页手动同步");
                CustomerDialog.dismissProgress();
                ExitThisAndGoLauncher();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(currentActivity, "再点一次，退出巡视页面", Toast.LENGTH_SHORT).show();
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
                        XunshiApplication.getDbUtils().saveOrUpdate(saveList);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                });
            }
            super.onBackPressed();
        }
    }
}