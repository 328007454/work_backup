package com.cnksi.bdzinspection.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.daoservice.LookupService;
import com.cnksi.bdzinspection.databinding.XsActivityFullDeviceListBinding;
import com.cnksi.bdzinspection.fragment.DeviceListFragment;
import com.cnksi.bdzinspection.inter.DialogInputClickListener;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.utils.DialogUtil;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.ShakeListener;
import com.cnksi.bdzinspection.utils.ShakeListener.OnShakeListener;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sync.KSyncConfig;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.ksynclib.KSync.SYNC_ERROR;
import static com.cnksi.ksynclib.KSync.SYNC_INFO;
import static com.cnksi.ksynclib.KSync.SYNC_START;
import static com.cnksi.ksynclib.KSync.SYNC_SUCCESS;

/**
 * @author lyndon
 * @date 2016-10-18
 */
public class FullDeviceListActivity extends BaseActivity implements OnPageChangeListener, OnShakeListener {
    public static final int SHAKE_CODE = -16;
    public static final int FRAGMENT_POSITION = 1;
    public static final int SPACE_POSITION = FRAGMENT_POSITION + 11;
    LocationUtil.LocationHelper locationHelper;
    private List<Lookup> deviceTypes;
    private List<DeviceListFragment> fragmentList;
    private List<String> fragmentTitleList;
    private int currentPosition;
    private DeviceListFragment currentFragment;
    private ShakeListener shakeListener;
    private boolean shaking;
    private boolean isLeader = false;
    /**
     * 当前设备类型以及点击的间隔数据
     */

    private Task currentTask;

    private XsActivityFullDeviceListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_full_device_list);
        initialUI();
        initialData();
        setHasSortOrSplit();
        initOnClick();
    }


    private void initialData() {
        ExecutorManager.executeTask(() -> currentTask = TaskService.getInstance().findById(currentTaskId));
    }

    private void initialUI() {
        shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(this);
        binding.ibtnAdd.setVisibility(View.VISIBLE);
        binding.ibtnAdd.setImageResource(R.drawable.xs_copy_button_background);
        binding.ibtnAdd.setOnLongClickListener(v -> {
            showChangeDistanceDialog();
            return false;
        });

        binding.btnStartInspection.setText(R.string.xs_finish_inspection_str);

        getIntentValue();
        TTSUtils.getInstance().startSpeaking(getString(R.string.xs_speak_content_format_str, currentInspectionTypeName));
        binding.tvTitle.setText(currentBdzName + "" + currentInspectionTypeName);
        deviceTypes = LookupService.getInstance().findLookupByType(LookUpType.pmsDeviceType.name(), true);
        fragmentTitleList = new ArrayList<>();
        fragmentList = new ArrayList<>();
        for (Lookup look : deviceTypes) {
            DeviceListFragment fragment = new DeviceListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Config.CURRENT_FUNCTION_MODEL, look.k);
            bundle.putString(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
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
        /**
         * 30s定位一次
         */
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
                }
                binding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, signalIcon, 0);
                currentFragment.locationSuccess(location);
            }
        }).setKeep(true);
        locationHelper.start();
    }

    private void setHasSortOrSplit() {
        ExecutorManager.executeTask(() -> {
            try {
                List<DbModel> models = DepartmentService.getInstance().findUserForCurrentUser(currentAcounts);
                if (models != null) {
                    for (DbModel model : models) {
                        if (StringUtilsExt.nullTo(model.getString("type"), "").contains("team_leader")) {
                            isLeader = true;
                            break;
                        }
                    }
                }
                if (isLeader) {
                    runOnUiThread(() -> {
                        if (currentPosition < 2) {
                            binding.ibtnSort.setVisibility(View.VISIBLE);
                        }
                    });
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }


    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        currentFragment = fragmentList.get(position);
        if (position < FRAGMENT_POSITION && isLeader) {
            binding.ibtnSort.setVisibility(View.VISIBLE);
        } else {
            binding.ibtnSort.setVisibility(View.GONE);
        }
    }

    private void initOnClick() {
        binding.ibtnCancel.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.ibtnAdd.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            Intent intent = new Intent(currentActivity, CopyAllValueActivity2.class);
            startActivity(intent);
        });

        binding.ibtnSort.setOnClickListener(view -> {
            Intent intent = new Intent(this, SpaceSortActivity.class);
            intent.putExtra(Config.CURRENT_FUNCTION_MODEL, deviceTypes.get(currentPosition).k);
            intent.putExtra(Config.TITLE_NAME, deviceTypes.get(currentPosition).v);
            startActivityForResult(intent, DeviceListFragment.SORT_SPACING);
        });
        binding.btnStartInspection.setOnClickListener(view -> {
            finishInspection();
        });
        binding.ibtnBluetooth.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, BTDemoActivity.class);
            startActivityForResult(intent1, DeviceListFragment.RFID);
        });
    }

    private void finishInspection() {
        //设备到位模式下 完成之前处理一次间隔到位情况
        if (SystemConfig.isDevicePlaced()) {
            fragmentList.get(0).handleSpaceArrivedData();
        }
        long copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
        long totalCount = CopyItemService.getInstance().getCopyItemCount(currentBdzId, currentInspectionType);

        String s = DeviceService.getInstance().groupImportantDevicePhoto(currentBdzId, currentReportId);
        String tip = String.format(getText(R.string.xs_dialog_tips_finish_str1) + "", copyCount + "", totalCount + "", s);

        if (currentTask.isMember()) {
            DialogUtils.showSureTipsDialog(currentActivity, null, tip + "\n" + "作为分组巡视成员,点击确认后会同步本次巡视任务", "确认并同步", "取消", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    CustomerDialog.showProgress(currentActivity, "正在上传任务", true, false);
                    KSyncConfig.getInstance().getKNConfig(currentActivity, mHandler).upload();
                }
            });
        } else {
            if (isRoutineNotCopy()) {
                showTipsDialog(null, new Intent(this, GenerateReportActivity.class));
            } else {
                tip = tip + (currentTask.isGroupTask() ? "\n" + "确保分组成员已经获取过该任务，否则需通知其获取" : "");
                showTipsDialog(null, new Intent(this, GenerateReportActivity.class), -1, tip, false);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        shakeListener.start();
        currentFragment.onResume();
        locationHelper.resume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        currentFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.SHAKE_SPACE) {
            shaking = false;
        }
    }


    @Override
    public void onShake() {
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
                        if (code == SHAKE_CODE) {
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
    protected void onPause() {
        super.onPause();
        shakeListener.stop();
        locationHelper.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        shakeListener.stop();
        locationHelper.destory();
        super.onDestroy();
    }

    private void showChangeDistanceDialog() {
        DialogUtil.getInstance().showInputCancelDialog(this, 10, InputType.TYPE_CLASS_NUMBER, "修改判断距离", Config.COPY_MAX_DISTANCE + "", "请输入判断距离(m)", "修改", "取消", new DialogInputClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, String result) {
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    try {
                        Float distance = Float.valueOf(result);
                        Config.COPY_MAX_DISTANCE = distance;
                        PreferencesUtils.put( Config.COPY_DISTANCE_KEY, distance);
                    } catch (NumberFormatException e) {
                        ToastUtils.showMessageLong( "请输入数字");
                        return;
                    }
                }
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case SYNC_START:
                ToastUtils.showMessage( "开始上传数据");
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
            for (DeviceListFragment fragment : fragmentList) {
                SpacingLastly lastly = fragment.getSpaceLastly();
                if (lastly == null) {
                    continue;
                } else {
                    saveList.add(lastly);
                }
            }
            if (saveList.size() > 0) {
                ExecutorManager.executeTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            XunshiApplication.getDbUtils().saveOrUpdate(saveList);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            super.onBackPressed();
        }
    }
}