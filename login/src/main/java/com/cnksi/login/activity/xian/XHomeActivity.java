package com.cnksi.login.activity.xian;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.api.scanner.Command;
import com.cnksi.api.scanner.FindDevice;
import com.cnksi.api.scanner.Model;
import com.cnksi.api.scanner.SM;
import com.cnksi.api.scanner.SMCallBack;
import com.cnksi.api.scanner.State;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.login.R;
import com.cnksi.login.activity.ScannerSetting;
import com.cnksi.login.adapter.WeekTaskPagerAdapter;
import com.cnksi.login.databinding.LoginActivityHomeBinding;
import com.cnksi.login.fragment.xian.WeekTaskFragment;
import com.cnksi.login.util.ActivityUtil;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mr.K  on 2018/7/4.
 * @decrption 西安巡视主页
 */
@Route(path = "/login/XHomeActivity")
public class XHomeActivity extends BaseTitleActivity implements OnTabSelectListener {

    LoginActivityHomeBinding homeBinding;
    private final List<String> weekTitles = new ArrayList<>();
    private final String[] titleTypes = {InspectionType.SBXS.value, InspectionType.RCWH.value, InspectionType.LHSY.value, InspectionType.QXGL.value, InspectionType.SBJC.value};
    private ArrayList<WeekTaskFragment> mFragments = new ArrayList<>();
    private boolean isNotFirstLoad;
    private SMCallBack smCallBack = new SMCallBack() {

        @Override
        public void onConnecteChange(BluetoothDevice device, int state) {
            if (XHomeActivity.this.isFinishing()) {
                return;
            }
            runOnUiThread(() -> {
                if (state == State.SUCCESS) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        Toast.makeText(XHomeActivity.this, device.getName() + "连接成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (state == State.ERROR) {
                        Toast.makeText(XHomeActivity.this, (device != null ? device.getName() : "") + "连接失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onCommand(int command, int state) {
            if (command == Command.RFID_ON) {
                rfidSet.clear();
            } else if (command == Command.RFID_OFF) {
                if (rfidSet.size() > 0) {
                    //取消计时
                    runOnUiThread(() -> {
                        //处理RFID数据
                        String ids = "";
                        for (String i : rfidSet) {
                            ids += i + "\n";
                        }
                        Toast.makeText(XHomeActivity.this, "RFID:" + ids, Toast.LENGTH_SHORT).show();
                    });
                }
            }

        }

        private Set<String> rfidSet = new HashSet<>();

        @Override
        public void onReadData(String tagId, Object tag, int model) {
            //回调为子线程
            //tagId 获取到QR或者RFID数据,同一次的RFID返回值为多个，根据自己需要进行取消获取
            //来源Model.QR,Model.RFID;
            //tag 保留值为null
            if (model == Model.RFID) {
                rfidSet.add(tagId);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //处理QR
                        Toast.makeText(XHomeActivity.this, "QR:" + tagId, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void onScan(BluetoothDevice device, int state) {

        }
    };

    private String lastScannerAddress;
    private int lastScannerModel;
    private ProgressDialog progressDialog;
    private int qrResId = R.mipmap.icon_scan;
    private int rfidResId = R.mipmap.ic_audio;
    private int deviceResId = R.mipmap.ic_check;

    @Override
    public void getRootDataBinding() {
        changedStatusColor();
        homeBinding = DataBindingUtil.setContentView(this, R.layout.login_activity_home);
        getIntentValue();
        String tip = String.format((String) getText(R.string.welcome_login_user), currentDepartmentName, userName);
        homeBinding.txtLoginName.setText(tip);
        getData();
        initOnClick();
        SM.instance().init(getApplication());
        SM.instance().setDeviceFilter(device -> {
            if (device.getName() != null) {
                if (device.getName().equals(FindDevice.DeviceFilter.FILTER_XC2600) ||
                        device.getName().contains(FindDevice.DeviceFilter.FILTER_ZEBRA_8500)) {
                    return true;
                }
            }
            return false;
        });
        lastScannerAddress = PreferencesUtils.get("lastScannerAddress", "");
        lastScannerModel = PreferencesUtils.get("lastScannerModel", Model.QR);
        if (lastScannerModel == Model.QR) {
            homeBinding.ivScan.setImageResource(qrResId);
        } else {
            homeBinding.ivScan.setImageResource(rfidResId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        lastScannerAddress = PreferencesUtils.get("lastScannerAddress", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotFirstLoad) {
            getData();
        }
        isNotFirstLoad = true;
        SM.instance().register(smCallBack);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SM.instance().unregister(smCallBack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SM.instance().isConnected()) {
            SM.instance().stopConnected();
        }
    }

    private void initView() {
        mFragments.clear();
        if (weekTitles.size() == 0) {
            return;
        }
        for (String title : weekTitles) {
            mFragments.add(WeekTaskFragment.getFragment(title));
        }
        WeekTaskPagerAdapter taskPagerAdapter = new WeekTaskPagerAdapter(getSupportFragmentManager(), mFragments, weekTitles);
        homeBinding.viewPager.setAdapter(taskPagerAdapter);
        homeBinding.stWeek.setViewPager(homeBinding.viewPager);
        homeBinding.stWeek.setOnTabSelectListener(this);

    }

    public void getData() {
        ExecutorManager.executeTaskSerially(() -> {
            weekTitles.clear();
            for (int i = 0; i < titleTypes.length; i++) {
                String title = titleTypes[i];
                if (TextUtils.equals(title, InspectionType.QXGL.value)) {
                    List<DefectRecord> defectRecords = DefectRecordService.getInstance().findAllDefectRecords();
                    putTitle(defectRecords, i, title);
                } else {
                    List<Task> tasks = TaskService.getInstance().getWeekTasks(InspectionType.getTypeFromValue(title));
                    putTitle(tasks, i, title);
                }
            }
            runOnUiThread(() -> {
                initView();
            });
        });
    }


    private void putTitle(List<? extends Object> objects, int position, String title) {
        if (objects != null && !objects.isEmpty()) {
            weekTitles.add(title + "（ " + objects.size() + " ）");
        }
    }


    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    private void initOnClick() {
        homeBinding.includeType.txtSync.setOnClickListener(v -> {
            ActivityUtil.startSync(mActivity);
        });
        homeBinding.includeType.txtTour.setOnClickListener(v -> {
            ActivityUtil.startTourActivity(mActivity, InspectionType.SBXS.name());
        });

        homeBinding.includeType.txtMaintance.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startTourActivity(mActivity, InspectionType.maintenance.name());
        });

        homeBinding.includeType.txtSwitchover.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startTourActivity(mActivity, InspectionType.switchover.name());
        });

        homeBinding.includeType.txtCopy.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startShuJuJianCe(mActivity);
        });
        homeBinding.includeType.txtDefect.setOnClickListener(v -> {
            ActivityUtil.startDefectControlActivity(mActivity);
        });

        homeBinding.includeType.txtWorkTicket.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startTicketDateModel(mActivity);
        });
        homeBinding.includeType.txtOperation.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startOperateActivity(mActivity);
        });
        homeBinding.includeType.txtSafetyTool.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startSafetyToolActivity(mActivity);
        });
        homeBinding.includeType.txtModfyRecord.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
        });

        homeBinding.includeType.txtEducation.setOnClickListener(v -> {
            ToastUtils.showMessage("功能正在开发中。");
            return;
//            ActivityUtil.startWTYCActiviy(mActivity);
        });
        homeBinding.ivScan.setOnClickListener(v -> {
            if (TextUtils.isEmpty(lastScannerAddress)) {
                //设置扫描仪
                startActivity(new Intent(XHomeActivity.this, ScannerSetting.class));
            } else if (!SM.instance().isConnected()) {
                //连接摄像头
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lastScannerAddress);
                if (SM.instance().startConnecte(device)) {
                    progressDialog = new ProgressDialog(XHomeActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("正在连接" + device.getName() + "...");
                    progressDialog.show();
                } else {
                    Toast.makeText(XHomeActivity.this, "连接扫描仪失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (lastScannerModel == Model.RFID) {
                    if (SM.instance().isReadRfid()) {
                        SM.instance().stopReadTag(lastScannerModel);
                    } else {
                        if (!SM.instance().startReadTag(lastScannerModel)) {
                            Toast.makeText(XHomeActivity.this, "RFID调用失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (lastScannerModel == Model.QR) {
                    if (SM.instance().isReadQR()) {
                        SM.instance().stopReadTag(lastScannerModel);
                    } else {
                        if (!SM.instance().startReadTag(lastScannerModel)) {
                            runMobileCamera();
                        }
                    }
                }
            }
        });
        homeBinding.ivScan.setOnLongClickListener(view -> {
            final List<WidgetItemData> menuList = new ArrayList<>();
            menuList.add(new WidgetItemData(qrResId, "扫描二维码"));
            menuList.add(new WidgetItemData(rfidResId, "扫描RFID"));
            menuList.add(new WidgetItemData(deviceResId, "更换扫描设备"));
            new PopItemWindow(XHomeActivity.this)
                    .setDefaultAdapter(menuList)
                    .setOnItemClickListener((adapter, view1, position) -> {
                        switch (position) {
                            case 0:
                                lastScannerModel = Model.QR;
                                PreferencesUtils.put("lastScannerModel", lastScannerModel);
                                homeBinding.ivScan.performClick();
                                homeBinding.ivScan.setImageResource(qrResId);
                                break;
                            case 1:
                                lastScannerModel = Model.RFID;
                                PreferencesUtils.put("lastScannerModel", lastScannerModel);
                                homeBinding.ivScan.performClick();
                                homeBinding.ivScan.setImageResource(rfidResId);
                                break;
                            case 2:
                                startActivity(new Intent(XHomeActivity.this, ScannerSetting.class));
                                break;
                        }
                    })
                    .showAsDropDown(view);
            return true;
        });
    }

    //启动系统二维码扫描
    private void runMobileCamera() {
        Toast.makeText(XHomeActivity.this, "调用系统QR", Toast.LENGTH_SHORT).show();
    }
}
