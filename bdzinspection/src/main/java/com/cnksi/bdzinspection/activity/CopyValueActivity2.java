package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.databinding.XsActivityCopy2Binding;
import com.cnksi.bdzinspection.databinding.XsActivityCopyDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Device;
import com.cnksi.bdzinspection.model.TreeNode;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.CopyHelper;
import com.cnksi.bdzinspection.utils.CopyViewUtil.KeyBordListener;
import com.cnksi.bdzinspection.utils.DefectUtils;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.KeyBoardUtil.OnKeyBoardStateChangeListener;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.bdzinspection.utils.ShowHistroyDialogUtils;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.GPSUtils;
import com.cnksi.xscore.xsutils.KeyBoardUtils;
import com.lidroid.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity.UPDATE_DEVICE_DEFECT_REQUEST_CODE;

/**
 * 设备抄录
 *
 * @author lyndon
 */
public class CopyValueActivity2 extends BaseActivity implements KeyBordListener {

    public final int LOAD_COPY_FINSIH = 0x10;
    protected int currentKeyBoardState = KeyBoardUtil.KEYBORAD_HIDE;
    LocationUtil.LocationHelper locationHelper;
    private List<TreeNode> data;

    private CopyHelper copyViewUtil;
    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    //传递预设缺陷内容
    private String transDefectContent = "";
    private List<DefectRecord> mExistDefectList = new ArrayList<>();
    private Dialog defectDialog;
    private XsDialogTipsBinding tipsBinding;

    private List<EditText> editTextList;
    private List<CopyItem> copyItemList;

    private int editFocusPosition;
    private XsActivityCopy2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_copy2);

        initLocation();
        initUI();
        initData();
        initOnClick();
    }

    private XsActivityCopyDialogBinding copyDialogBinding;

    private void initUI() {
        getIntentValue();
        creatDefectDialog();
        data = new ArrayList<>();
        copyViewUtil = new CopyHelper(currentActivity, currentReportId, currentBdzId, currentInspectionType);
        copyViewUtil.setKeyBordListener(this);
        // 抄录项长按弹出看不清输入对话框
        copyViewUtil.setItemLongClickListener((v, result, position, item) -> {
            copyDialogBinding = XsActivityCopyDialogBinding.inflate(getLayoutInflater());
            dialog = DialogUtils.createDialog(currentActivity, copyDialogBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            copyDialogBinding.etCopyValues.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
            // holder.etInput.setText("看不清");
//            holder.remark = holder.etInput.getText().toString();
            //隐藏自定义键盘
            hideKeyBord();
            dialog.show();
            copyDialogBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
            copyDialogBinding.btnSure.setOnClickListener(view -> {
                saveRemarkData(result, copyDialogBinding.etCopyValues, item);
            });
        });
        copyViewUtil.setItemClickListener((v, item, position) -> {
            // 显示历史曲线
            hideKeyBord();
            ShowHistroyDialogUtils.showHistory(currentActivity, item);
        });

        DbModel device = DeviceService.getInstance().findDeviceById(currentDeviceId);
        copyViewUtil.loadDevice(device);
        binding.includeTitle.tvTitle.setText(device.getString(Device.NAME));
        // 判断设备是否是一次设备，一次设备开启定位判断距离
        if (Device.isOnceDevice(device)) {
            // 设备没定位直接抄录
            if (!Device.hasGPSInfo(device))
                CToast.showShort(currentActivity, "设备没有定位");
            else {
                if (GPSUtils.isOPen(currentActivity))
                    locationHelper.start();
                else
                    CToast.showShort(currentActivity, "请开启GPS定位");
            }
        }

    }

    private void saveRemarkData(CopyResult result, TextView etInput, CopyItem item) {
        if (item.type_key.equalsIgnoreCase("youwei"))
            result.valSpecial = null;
        if ("Y".equals(item.val)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val = "-1";
            } else {
                result.val = "";
            }
        } else if ("Y".equals(item.val_a)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_a = "-1";
            } else {
                result.val_a = "";
            }
        } else if ("Y".equals(item.val_b)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_b = "-1";
            } else {
                result.val_b = "";
            }
        } else if ("Y".equals(item.val_c)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_c = "-1";
            } else {
                result.val_c = "";
            }
        } else if ("Y".equals(item.val_o)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_o = "-1";
            } else {
                result.val_o = "";
            }
        }
        result.remark = TextUtils.isEmpty(etInput.getText().toString()) ? "" : (TextUtils.isEmpty(result.remark) ? etInput.getText().toString() + "," : etInput.getText().toString());
        dialog.dismiss();
        copyViewUtil.createCopyView(currentActivity, data, binding.copyContainer);
    }

    private void initData() {
        // 查询抄录标准
        mFixedThreadPoolExecutor.execute(() -> {
            searchDefect();
            data = copyViewUtil.loadItem();
            mHandler.sendEmptyMessage(LOAD_COPY_FINSIH);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWindowOverLayPermission();
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_COPY_FINSIH:
                if (!data.isEmpty()) {
                    copyViewUtil.createCopyView(this, data, binding.copyContainer);
                    editTextList = copyViewUtil.getAllEditText();
                    copyItemList = copyViewUtil.getAllItems();
                }
                break;

            default:
                break;
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        // 初始化搜索模块，注册事件监听
        locationHelper = LocationUtil.getInstance().getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                copyViewUtil.judgeDistance(location, binding.shadom, binding.shadomTip);
            }
        }).setKeep(true);
        locationHelper.start();
    }

    private void initOnClick() {
        binding.btnFinish.setOnClickListener(view -> {
            save();
        });
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            save();
        });
    }

    private void save() {
        if (editFocusPosition + 1 == editTextList.size()) {
            CopyItem copyItem = copyItemList.get(editFocusPosition);
            String description = "抄录" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")");
            if (!TextUtils.isEmpty(copyItem.max) || !TextUtils.isEmpty(copyItem.min)) {
                description += "(" + (TextUtils.isEmpty(copyItem.min) ? "" : copyItem.min) + "-" + (TextUtils.isEmpty(copyItem.max) ? "" : copyItem.max) + ")";
            }
            onViewFocusChange(editTextList.get(editFocusPosition), copyItemList.get(editFocusPosition), null, false, "抄录" + description, editTextList);
        }
        onBackPressed();
        KeyBoardUtils.closeKeybord(currentActivity);
    }


    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems) {
        if (item.type_key.contains("youwei")) {// 如果有油温抄录项则直接弹出系统键盘可以输入分数-----yangjun
            v.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            KeyBoardUtils.openKeybord(v, getApplicationContext());
            hideKeyBord();
            binding.llKeyboardHelpLayout.setVisibility(View.GONE);
        } else {
            if (null == mKeyBoardUtil) {
                createKeyBoardView(binding.llRootContainer);
                mKeyBoardUtil.setOnValueChangeListener(new OnKeyBoardStateChangeListener() {
                    @Override
                    public void onKeyBoardStateChange(int state) {
                        if (currentKeyBoardState != state) {
                            currentKeyBoardState = state;
                            switch (state) {
                                case KeyBoardUtil.KEYBORAD_HIDE: // 键盘隐藏
                                    binding.llKeyboardHelpLayout.setVisibility(View.GONE);
                                    break;
                                case KeyBoardUtil.KEYBORAD_SHOW: // 键盘显示
                                    binding.llKeyboardHelpLayout.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onKeyBoardNextInput(EditText editText) {
                        int position = editTextList.indexOf(editText);
                        editFocusPosition = position;
                        if (position < editTextList.size() - 1) {
                            EditText nextEditText = editTextList.get(position + 1);
                            editText.clearFocus();
                            nextEditText.requestFocus();
                            onViewFocus(nextEditText, copyItemList.get(position + 1), copyViewUtil.getCopyResultMap().get(copyItemList.get(position + 1)), editTextList, copyItemList);
                        } else {
                            binding.llKeyboardHelpLayout.setVisibility(View.GONE);
                            hideKeyBord();
                        }
                    }
                });
            }
            KeyBoardUtils.closeKeybord(v, getApplicationContext());
            mKeyBoardUtil.setCurrentEditText(v, item.min, item.max, item.description + item.unit);
            mKeyBoardUtil.showKeyboard();
            showCursor(v);
        }
    }

    @Override
    public void hideKeyBord() {
        if (null != mKeyBoardUtil && currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
            mKeyBoardUtil = null;
        }
    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {
        String val = v.getText().toString().trim();
        if (hasFocus || TextUtils.isEmpty(val)) {
            return;
        }
        List<String> rs = new ArrayList<>();
        if (DefectUtils.calcCopyBound(item, copyViewUtil.getCopyResultMap().get(item.id), val, mExistDefectList, rs)) {
            tipsBinding.tvDialogContent.setText(rs.get(1));
            transDefectContent = rs.get(0);
            if (null != defectDialog)
                defectDialog.show();

        } else {

        }
    }

    public void creatDefectDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
        defectDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipsBinding.tvDialogTitle.setText("警告");
        tipsBinding.btnCancel.setText("否");
        tipsBinding.btnSure.setText("是");
        tipsBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defectDialog.dismiss();
            }
        });
        tipsBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBord();
                defectDialog.dismiss();
                Intent intent = new Intent(currentActivity, AddNewDefectActivity.class);
                setIntentValue(intent);
                startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
            }
        });
    }

    /**
     * 查询当前设备抄录项是否记录有上下限抄录缺陷
     */
    public void searchDefect() {
        mExistDefectList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId, currentReportId);
    }

    /**
     * 设置需要传递的值
     *
     * @param intent
     */
    private void setIntentValue(Intent intent) {
        intent.putExtra(Config.DEFECT_COUNT_KEY, transDefectContent);
        intent.putExtra(Config.CURRENT_DEVICE_ID, currentDeviceId);
        intent.putExtra(Config.CURRENT_DEVICE_NAME, currentDeviceName);
        intent.putExtra(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
        intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, currentDevicePartName);
        intent.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
        intent.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
        intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
    }


    @Override
    public void finish() {
        hideKeyBord();
        if (copyViewUtil.saveAll()) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    protected void onStop() {
        hideKeyBord();
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mKeyBoardUtil && requestCode == BaseActivity.PERMISSION_WINDOW)
            mKeyBoardUtil.showKeyboard();
        if (requestCode == UPDATE_DEVICE_DEFECT_REQUEST_CODE) {
            searchDefect();
        }
    }

    @Override
    protected void onPause() {
        locationHelper.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationHelper.destory();
        super.onDestroy();
    }

}