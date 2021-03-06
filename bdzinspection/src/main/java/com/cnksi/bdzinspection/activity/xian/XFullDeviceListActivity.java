package com.cnksi.bdzinspection.activity.xian;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BTDemoActivity;
import com.cnksi.bdzinspection.activity.CopyAllValueActivity2;
import com.cnksi.bdzinspection.activity.GenerateReportActivity;
import com.cnksi.bdzinspection.activity.LocationSpacingActivity;
import com.cnksi.bdzinspection.activity.SpaceSortActivity;
import com.cnksi.bdzinspection.daoservice.SpacingLastlyService;
import com.cnksi.bdzinspection.databinding.XsActivityFullDeviceBinding;
import com.cnksi.bdzinspection.fragment.DeviceListFragment;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.bdzinspection.other.ReQuestLocationDialog;
import com.cnksi.bdzinspection.utils.ShakeListener;
import com.cnksi.bdzinspection.utils.ShakeListener.OnShakeListener;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.listener.AbstractPageChangeListener;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sync.KSyncConfig;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.core.utils.Cst.ACTION_IMAGE;
import static com.cnksi.ksynclib.KSync.SYNC_DOWN_DATA_SUCCESS;
import static com.cnksi.ksynclib.KSync.SYNC_ERROR_DATA_DOWNLOAD;
import static com.cnksi.ksynclib.KSync.SYNC_ERROR_DATA_UPLOAD;
import static com.cnksi.ksynclib.KSync.SYNC_INFO;
import static com.cnksi.ksynclib.KSync.SYNC_START;
import static com.cnksi.ksynclib.KSync.SYNC_UP_DATA_SUCCESS;

/**
 * @author lyndon
 * @date 2016-10-18
 */
public class XFullDeviceListActivity extends BaseActivity implements OnShakeListener {
    public static final int SHAKE_CODE = -16;
    public static final int FRAGMENT_POSITION = 1;
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

    private XsActivityFullDeviceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_full_device);
        initialUI();
        initialData();
        setHasSortOrSplit();
        initOnClick();
    }


    private void initialData() {
        ExecutorManager.executeTask(() -> currentTask = TaskService.getInstance().findById(currentTaskId));
    }

    private void initialUI() {
        getIntentValue();
        shakeListener = new ShakeListener(this);
        shakeListener.setOnShakeListener(this);
        binding.ibtnAdd.setVisibility(View.VISIBLE);
        binding.ibtnAdd.setImageResource(R.drawable.xs_copy_button_background);
        binding.ibtnAdd.setOnLongClickListener(v -> {
            showChangeDistanceDialog();
            return false;
        });

        binding.btnStartInspection.setText(R.string.xs_finish_inspection_str);
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
        binding.stWeek.setViewPager(binding.viewPager);
        AbstractPageChangeListener pageChangeListener = new AbstractPageChangeListener() {

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
        };
        binding.viewPager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);
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
                    default:
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


    private void initOnClick() {
        binding.ibtnCancel.setOnClickListener(view -> onBackPressed());

        binding.ibtnAdd.setOnClickListener(view -> {
            PlaySound.getIntance(mActivity).play(R.raw.input);
            Intent intent = new Intent(mActivity, CopyAllValueActivity2.class);
            startActivity(intent);
        });

        binding.ibtnSort.setOnClickListener(view -> {
            Intent intent = new Intent(this, SpaceSortActivity.class);
            intent.putExtra(Config.CURRENT_FUNCTION_MODEL, deviceTypes.get(currentPosition).k);
            intent.putExtra(Config.TITLE_NAME_KEY, deviceTypes.get(currentPosition).v);
            startActivityForResult(intent, DeviceListFragment.SORT_SPACING);
        });
        binding.btnStartInspection.setOnClickListener(view -> finishInspection());
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
        long totalCount = CopyItemService.getInstance().getCopyTotalCount(currentBdzId, currentInspectionType);

        String s = DeviceService.getInstance().groupImportantDevicePhoto(currentBdzId, currentReportId);
        String tip = String.format(getText(R.string.xs_dialog_tips_finish_str1) + "", copyCount + "", totalCount + "", s);

        if (currentTask.isMember()) {
            DialogUtils.showSureTipsDialog(mActivity, null, tip + "\n" + "作为分组巡视成员,点击确认后会同步本次巡视任务", "确认并同步", "取消", v -> {
                CustomerDialog.showProgress(mActivity, "正在上传任务", true, false);
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
            if (isRoutineNotCopy()) {
                showTipsDialog(new Intent(this, GenerateReportActivity.class));
            } else {
                tip = tip + (currentTask.isGroupTask() ? "\n" + "确保分组成员已经获取过该任务，否则需通知其获取" : "");
                showTipsDialog(new Intent(this, GenerateReportActivity.class), -1, tip, false);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        shakeListener.start();
        if (currentFragment != null) {
            currentFragment.onResume();
        }
        locationHelper.resume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_IMAGE) {
            ReQuestLocationDialog.getInstance().onActivityResult(requestCode,resultCode,data);
        } else {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == Config.SHAKE_SPACE) {
            shaking = false;
        }
    }


    @Override
    public void onShake() {
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
                        if (code == SHAKE_CODE) {
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
        Dialog dialog = new Dialog(mActivity, com.cnksi.common.R.style.dialog);
        ViewHolder holder = new ViewHolder(mActivity, null, R.layout.xs_dialog_input_1, false);
        final EditText editText = holder.getView(R.id.edit);
        TextView tvTitle = holder.getView(R.id.tv_dialog_title);
        editText.setHint("修改判断距离");
        editText.setText(Config.COPY_MAX_DISTANCE + "");
        tvTitle.setText("请输入判断距离(m)");
        InputFilter[] filters = {new InputFilter.LengthFilter(10)};
        editText.setFilters(filters);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        Button btnYes = holder.getView(R.id.btn_sure);
        Button btnCancel = holder.getView(R.id.btn_cancel);
        btnCancel.setText("取消");
        btnYes.setText("修改");
        View.OnClickListener dialogClick = v -> {
            if (v.getId() == R.id.btn_sure) {
                try {
                    Float distance = Float.valueOf(editText.getText().toString());
                    Config.COPY_MAX_DISTANCE = distance;
                    PreferencesUtils.put(Config.COPY_DISTANCE_KEY, distance);
                } catch (NumberFormatException e) {
                    ToastUtils.showMessageLong("请输入数字");
                    return;
                }
            }
            dialog.dismiss();
        };

        btnYes.setOnClickListener(dialogClick);
        btnCancel.setOnClickListener(dialogClick);
        dialog.setContentView(holder.getRootView());
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(com.cnksi.common.R.style.DialogAnim);
        dialog.show();
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case SYNC_START:
                ToastUtils.showMessage("开始上传数据");
                break;
            case SYNC_INFO:
                break;
            case SYNC_DOWN_DATA_SUCCESS:
            case SYNC_UP_DATA_SUCCESS:
                String messageSuccess = (String) msg.obj;
                ToastUtils.showMessage(messageSuccess);
                CustomerDialog.dismissProgress();
                ExitThisAndGoLauncher();
                break;
            case SYNC_ERROR_DATA_UPLOAD:
            case SYNC_ERROR_DATA_DOWNLOAD:
                ToastUtils.showMessage("请检查网络，在主页手动同步");
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
            Toast.makeText(mActivity, "再点一次，退出巡视页面", Toast.LENGTH_SHORT).show();
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