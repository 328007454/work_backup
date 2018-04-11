package com.cnksi.bdzinspection.ywyth;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.activity.DefectControlActivity;
import com.cnksi.bdzinspection.adapter.BatteryDetailsAdapter;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.TaskService;
import com.cnksi.bdzinspection.databinding.XsActivityYwBatteryBinding;
import com.cnksi.bdzinspection.databinding.XsBatteryInputValueDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.model.BatteryDetails;
import com.cnksi.bdzinspection.model.BatteryReport;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DefectLevelUtils;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.KeyBoardUtil.OnKeyBoardStateChangeListener;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.KeyBoardUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 蓄电池组试验的巡检界面
 *
 * @author terry
 */
@SuppressLint("ClickableViewAccessibility")
public class YWBatteryActivity extends BaseActivity implements OnKeyBoardStateChangeListener {

    public static final int UPDATE_BATTERY_STATUS_REQUEST_CODE = 0x112;
    public static final int INIT_BATTERY_INFOR_CODE = UPDATE_BATTERY_STATUS_REQUEST_CODE + 1;
    private boolean isDefectChanged = false;

    private static final String minVoltage = "2";
    private static final String maxVoltage = "2.2";
    private static final String minResistance = "0";
    private static final String maxResistance = "30";

    private boolean taskStatus = false;
    private List<BatteryDetails> mBatteryDetailsList = new ArrayList<BatteryDetails>();
    private BatteryDetailsAdapter mBatteryAdapter = null;
    private Dialog mBatteryDialog = null;

    private int batteryCount = 109;

    /**
     * 当前点击的电池编号
     */
    private BatteryDetails mCurrentBatteryDetails = null;
    /**
     * 上一次的电压等级
     */
    private String lastVoltage = "";
    /**
     * 上一次的内阻
     */
    private String lastResistance = "";
    /**
     * 是否显示了提示记录缺陷的Dialog
     */
    private boolean isShowTipsDialog = false;

    // 当前的电池报告
    private BatteryReport mCurrentBatteryReport = null;
    // 是否抄录内阻
    private boolean isCopyInternalResistance = true;
    private XsActivityYwBatteryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_yw_battery);
        initUI();
        initData();
        initOnClick();
    }


    private void initUI() {
        getIntentValue();
        taskStatus = TaskService.getInstance().getTaskStatusForBoolean(currentTaskId);
        TTSUtils.getInstance().startSpeaking(getString(R.string.xs_speak_content_format_str, getString(R.string.xs_yw_battery_title)));
        binding.includeTitle.tvTitle.setText(R.string.xs_yw_battery_title);
        String _t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        binding.time.append(StringUtils.changePartTextColor(currentActivity, _t, R.color.xs_green_color, 0, _t.length()));
        binding.clr.append(StringUtils.changePartTextColor(currentActivity, mApp.getUsers().username, R.color.xs_green_color, 0, mApp.getUsers().username.length()));
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 查询已选择的电池组
                    mCurrentBatteryReport = CustomApplication.getDbUtils().findFirst(Selector.from(BatteryReport.class).where(BatteryReport.REPORTID, "=", currentReportId));
                    if (mCurrentBatteryReport == null) {
                        mCurrentBatteryReport = new BatteryReport(currentReportId);
                    }
                    CustomApplication.getDbUtils().saveOrUpdate(mCurrentBatteryReport);
                    initBatteryDetails();
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 初始化电池列表信息 1.初始化电池信息时 查询相应电池记录的最高缺陷信息
     */
    private void initBatteryDetails() {
        List<BatteryDetails> _temp = DefectRecordService.getInstance().queryAllRecordBattery(currentReportId);
        for (int i = 1; i < batteryCount; i++) {
            BatteryDetails mBatteryDetails = null;
            if (i < 10) {
                mBatteryDetails = new BatteryDetails("00" + String.valueOf(i));
            } else if (i < 100) {
                mBatteryDetails = new BatteryDetails("0" + String.valueOf(i));
            } else {
                mBatteryDetails = new BatteryDetails(String.valueOf(i));
            }
            // DbModel mBatteryDbModel = DefectRecordService.getInstance().queryMaxDefectLevelByBatteryId(mBatteryDetails.battery_number, currentBdzId, currentReportId);
            // if (mBatteryDbModel != null) {
            // mBatteryDetails.id = mBatteryDbModel.getString(BatteryDetails.ID);
            // mBatteryDetails.batteryid = mBatteryDbModel.getString(BatteryDetails.BATTERYID);
            // mBatteryDetails.voltage = mBatteryDbModel.getString(BatteryDetails.VOLTAGE);
            // mBatteryDetails.resistance = mBatteryDbModel.getString(BatteryDetails.RESISTANCE);
            // mBatteryDetails.reportid = mBatteryDbModel.getString(BatteryDetails.REPORTID);
            // mBatteryDetails.defectLevel = TextUtils.isEmpty(mBatteryDbModel.getString(DefectRecord.DEFECTLEVEL)) ? "" : mBatteryDbModel.getString(DefectRecord.DEFECTLEVEL);
            // // 判断是否都抄录了数据
            // judgementHasCopyData(mBatteryDetails);
            // }
            mBatteryDetailsList.add(mBatteryDetails);
        }

        for (BatteryDetails dbModel : _temp) {
            int i = Integer.valueOf(dbModel.battery_number) - 1;
            if (i >= mBatteryDetailsList.size()) {
                continue;
            } else {
                mBatteryDetailsList.get(i).voltage = dbModel.voltage;
                mBatteryDetailsList.get(i).resistance = dbModel.resistance;
                judgementHasCopyData(mBatteryDetailsList.get(i));
            }
        }

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mBatteryAdapter == null) {
                    mBatteryAdapter = new BatteryDetailsAdapter(currentActivity, mBatteryDetailsList);
                    mBatteryAdapter.setNeedResistance(true);
                    binding.gvContainer.setAdapter(mBatteryAdapter);
                } else {
                    mBatteryAdapter.setNeedResistance(true);
                    mBatteryAdapter.setList(mBatteryDetailsList);
                }

                break;
            case UPDATE_BATTERY_STATUS_REQUEST_CODE:
                queryBatteryStatus();
                break;
            default:
                break;
        }
    }


    public void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> onBackPressed());
        binding.btnFinishInspection.setOnClickListener(view -> onBackPressed());
        binding.gvContainer.setOnItemClickListener((parent, view, position, id) -> {
            mCurrentBatteryDetails = (BatteryDetails) parent.getItemAtPosition(position);
            if (taskStatus) {
                if (mCurrentBatteryDetails.hasCopyed) {
                    showBatteryDialog(mCurrentBatteryDetails);
                }
            } else {
                showBatteryDialog(mCurrentBatteryDetails);
            }
        });
    }

    /**
     * 显示抄录电池电压和内阻的数据
     */
    private XsBatteryInputValueDialogBinding dialogBinding;

    private void showBatteryDialog(BatteryDetails mBatteryDetails) {
        if (mBatteryDialog == null) {
            dialogBinding = XsBatteryInputValueDialogBinding.inflate(getLayoutInflater());
            mBatteryDialog = DialogUtils.createDialog(currentActivity, dialogBinding.getRoot(), ScreenUtils.getScreenWidth(currentActivity) * 9 / 10, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        dialogBinding.tvDialogTitle.setText(mBatteryDetails.battery_number);
        dialogBinding.unit.setText(R.string.xs_resistance_munit_str);
        dialogBinding.rlAddNewDefect.setVisibility(View.GONE);
        if (isCopyInternalResistance) {
            dialogBinding.rlResistanceContainer.setVisibility(View.VISIBLE);
        } else {
            dialogBinding.rlResistanceContainer.setVisibility(View.GONE);
        }
        // 恢复数据

        String lastV = PreferencesUtils.getString(currentActivity, "V" + mBatteryDetails.battery_number, "");
        String lastR = PreferencesUtils.getString(currentActivity, "R" + mBatteryDetails.battery_number, "");
        dialogBinding.etVoltage.setText(TextUtils.isEmpty(lastV) ? (TextUtils.isEmpty(mBatteryDetails.voltage) ? lastVoltage : mBatteryDetails.voltage) : lastV);
        dialogBinding.etResistance.setText(TextUtils.isEmpty(lastR) ? (TextUtils.isEmpty(mBatteryDetails.resistance) ? lastResistance : mBatteryDetails.resistance) : lastR);
        dialogBinding.etVoltage.requestFocus();
        dialogBinding.etVoltage.setInputType(InputType.TYPE_NULL);
        dialogBinding.etResistance.setInputType(InputType.TYPE_NULL);
        Selection.setSelection(dialogBinding.etVoltage.getEditableText(), dialogBinding.etVoltage.getEditableText().length());

        showCustomerKeyBoard(dialogBinding.etVoltage, minVoltage, maxVoltage);

        mBatteryDialog.show();
        mBatteryDialog.setOnDismissListener(dialog -> {
            if (mKeyBoardUtil != null) {
                mKeyBoardUtil.hideKeyboard();
            }
        });

        dialogBinding.btnConfirm.setOnClickListener(view -> saveBatteryCopyValue(dialogBinding.etVoltage, dialogBinding.etResistance, mBatteryDetails));
        dialogBinding.btnAddNewDefect.setOnClickListener(view -> jumpToDefect(mBatteryDetails));
        dialogBinding.rlAddNewDefect.setOnClickListener(view -> jumpToDefect(mBatteryDetails));

        dialogBinding.etVoltage.setOnFocusChangeListener((view, b) -> {
            showCustomerKeyBoard(dialogBinding.etVoltage, minVoltage, maxVoltage);
        });

        dialogBinding.etResistance.setOnFocusChangeListener((view, b) -> {
            showCustomerKeyBoard(dialogBinding.etResistance, minResistance, maxResistance);
        });

        dialogBinding.etResistance.setOnTouchListener((view, motionEvent) -> {
            showCustomerKeyBoard(dialogBinding.etResistance, minResistance, maxResistance);
            return false;
        });

    }

    public void jumpToDefect(BatteryDetails mBatteryDetails) {
        saveBatteryCopyValue(dialogBinding.etVoltage, dialogBinding.etResistance, mBatteryDetails);
        Intent intent = new Intent(currentActivity, DefectControlActivity.class);
        intent.putExtra(Config.IS_FROM_BATTERY, true);
        intent.putExtra(Config.CURRENT_DEVICE_ID, mBatteryDetails.battery_number);
        intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, mBatteryDetails.battery_number);
        intent.putExtra(Config.IS_SHOW_DEFECT_REASON, true);
        startActivityForResult(intent, UPDATE_BATTERY_STATUS_REQUEST_CODE);
    }

    /**
     * 显示自定义键盘
     */
    private void showCustomerKeyBoard(EditText mEditText, String smallValue, String bigValue) {
        if (taskStatus) {// 如果任务已完成 直接return；
            return;
        }
        if (mKeyBoardContainerView == null) {
            createKeyBoardView(binding.llRootContainer);
            mKeyBoardUtil.setOnValueChangeListener(this);
        }
        mKeyBoardUtil.setCurrentEditText(mEditText, smallValue, bigValue);
        mKeyBoardUtil.showKeyboard();
        showCursor(mEditText);
    }
    

    /**
     * 保存电池的抄录数据
     */
    private void saveBatteryCopyValue(EditText mEtVoltage, EditText mEtResistance, BatteryDetails mBatteryDetails) {
        // 电压
        String voltage = mEtVoltage.getText().toString().trim();
        // 电阻
        String resistance = mEtResistance.getText().toString().trim();
        lastVoltage = mBatteryDetails.voltage = voltage;
        lastResistance = mBatteryDetails.resistance = resistance;
        PreferencesUtils.put(currentActivity, "V" + mBatteryDetails.battery_number, lastVoltage);
        PreferencesUtils.put(currentActivity, "R" + mBatteryDetails.battery_number, lastResistance);
        // 判断电池数据是否抄录
        if (isCopyInternalResistance) {
            if (!TextUtils.isEmpty(mBatteryDetails.resistance) && !TextUtils.isEmpty(mBatteryDetails.voltage)) {
                mBatteryDetails.hasCopyed = true;
            } else {
                mBatteryDetails.hasCopyed = false;
                if (TextUtils.isEmpty(mBatteryDetails.resistance)) {
                    CToast.showShort(currentActivity, R.string.xs_please_copy_resistance_data_str);
                } else {
                    CToast.showShort(currentActivity, R.string.xs_please_copy_voltage_data_str);
                }
                return;
            }
        } else {
            if (!TextUtils.isEmpty(mBatteryDetails.voltage)) {
                mBatteryDetails.hasCopyed = true;
            } else {
                mBatteryDetails.hasCopyed = false;
                CToast.showShort(currentActivity, R.string.xs_please_copy_voltage_data_str);
                return;
            }
        }
        // 保存抄录的数量
        try {
            if (TextUtils.isEmpty(mBatteryDetails.id)) {
                mBatteryDetails.id = FunctionUtils.getPrimarykey();
            }
            mBatteryDetails.reportid = currentReportId;
            CustomApplication.getDbUtils().saveOrUpdate(mBatteryDetails);
            PreferencesUtils.put(currentActivity, Config.CURRENT_REPORT_ID + currentReportId, true);
        } catch (DbException e1) {
            e1.printStackTrace();
        }
        mBatteryAdapter.notifyDataSetChanged();
        KeyBoardUtils.closeKeybord(mEtVoltage, currentActivity);
        // 如果电压或者电阻超过标准 则提示保存缺陷
        try {
            boolean isValueNormal = true;
            String defectLevel = Config.GENERAL_LEVEL;
            String defectContent = "抄录数据不正常";
            float inputResistanceValue = Float.parseFloat(TextUtils.isEmpty(resistance) ? "0" : resistance);
            float inputVoltageValue = Float.parseFloat(TextUtils.isEmpty(voltage) ? "0" : voltage);
            // 判断电压不正常数据的缺陷等级
            if (inputVoltageValue > Float.parseFloat(maxVoltage)) {
                defectLevel = DefectLevelUtils.getDefectLevel(Float.parseFloat(maxVoltage), Float.parseFloat(minVoltage), inputVoltageValue, true);
                isValueNormal = false;
            }
            if (inputVoltageValue < Float.parseFloat(minVoltage)) {
                defectLevel = DefectLevelUtils.getDefectLevel(Float.parseFloat(maxVoltage), Float.parseFloat(minVoltage), inputVoltageValue, false);
                isValueNormal = false;
            }
            // 判断内阻不正常数据的缺陷等级
            if (inputResistanceValue > Float.parseFloat(maxResistance)) {
                defectLevel = DefectLevelUtils.getDefectLevel(Float.parseFloat(maxResistance), Float.parseFloat(minVoltage), inputResistanceValue, true);
                isValueNormal = false;
            }
            if (inputResistanceValue < Float.parseFloat(minResistance)) {
                defectLevel = DefectLevelUtils.getDefectLevel(Float.parseFloat(maxResistance), Float.parseFloat(minVoltage), inputResistanceValue, false);
                isValueNormal = false;
            }
            if (!isValueNormal) {
                // 保存有缺陷的电压到SharePreference
                PreferencesUtils.put(currentActivity, mBatteryDetails.battery_number, voltage);
                showBatteryTipsDialog(defectLevel, defectContent);
                TTSUtils.getInstance().startSpeaking(getString(R.string.xs_found_defect_str));
                isShowTipsDialog = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBatteryDialog.dismiss();
    }

    /**
     * 判断是否抄录了数据
     */
    private void judgementHasCopyData(BatteryDetails mBatteryDetails) {
        if (isCopyInternalResistance) {
            if (!TextUtils.isEmpty(mBatteryDetails.resistance) && !TextUtils.isEmpty(mBatteryDetails.voltage)) {
                mBatteryDetails.hasCopyed = true;
            } else {
                mBatteryDetails.hasCopyed = false;
            }
        } else {
            if (!TextUtils.isEmpty(mBatteryDetails.voltage)) {
                mBatteryDetails.hasCopyed = true;
            } else {
                mBatteryDetails.hasCopyed = false;
            }
        }
    }

    /**
     * 抄录数据后提示是否记录缺陷信息
     */
    private XsDialogTipsBinding tipsBinding;
    public void showBatteryTipsDialog(String defectLevel, String defectContent) {
        if (!isShowTipsDialog) {
            if (tipsDialog == null) {
                int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
                int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 3 / 10;// LinearLayout.LayoutParams.WRAP_CONTENT
                tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
                tipsDialog = DialogUtils.createDialog(currentActivity,tipsBinding.getRoot(),dialogWidth,dialogHeight);
            }
            tipsBinding.tvDialogTitle.setText(R.string.xs_dialog_tips_str);
           tipsBinding.tvDialogContent.setText(getString(R.string.xs_dialog_tips_record_format_defect_str, defectContent, defectLevel));
           tipsBinding.btnSure.setText("是");
           tipsBinding.btnCancel.setText("否");
            tipsDialog.show();
            tipsDialog.setOnDismissListener(dialog -> isShowTipsDialog = false);
        }
        
        tipsBinding.btnCancel.setOnClickListener(view -> {
            saveDefect(defectLevel, defectContent);

            mHandler.sendEmptyMessage(UPDATE_BATTERY_STATUS_REQUEST_CODE);
            TTSUtils.getInstance().startSpeaking(getString(R.string.xs_save_defect_success_str));
        });
        tipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });
    }

  

    /**
     * 保存电池的缺陷信息
     */
    private void saveDefect(String defectLevel, String defectContent) {
        defectLevel = DefectLevelUtils.convertDefectLevel2Code(defectLevel);
        // 电池记录的缺陷 间隔id和名称 存的是电池组的id和名称 设备Id和名称 以及 设备部件id和名称 存的都是电池的编号 如 001
        DefectRecord record = new DefectRecord(currentReportId, // 报告id
                currentBdzId, // 变电站id
                currentBdzName, // 变电站名称
                mCurrentBatteryDetails.battery_number, // 间隔ID
                mCurrentBatteryDetails.battery_number, // 间隔名称
                mCurrentBatteryDetails.battery_number, // 设备id
                mCurrentBatteryDetails.battery_number, // 设备名称
                mCurrentBatteryDetails.battery_number, // 设备部件id
                mCurrentBatteryDetails.battery_number, // 设备部件名称
                defectLevel, // 缺陷级别
                defectContent, // 缺陷描述
                "1", // 巡视标准id
                ""// pics图片
        );
        try {
            CustomApplication.getDbUtils().save(record);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询电池记录缺陷的状态 根据当前选择的电池组 和电池编号查询 电池的缺陷状态
     */
    private void queryBatteryStatus() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DbModel mBatteryDbModel = DefectRecordService.getInstance().queryMaxDefectLevelByBatteryId(mCurrentBatteryDetails.battery_number, currentBdzId, currentReportId);
                mCurrentBatteryDetails.defectLevel = mBatteryDbModel == null ? "" : mBatteryDbModel.getString(DefectRecord.DEFECTLEVEL);
                isDefectChanged = true;
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPDATE_BATTERY_STATUS_REQUEST_CODE:
                    queryBatteryStatus();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        if (mKeyBoardUtil != null) {
            mKeyBoardUtil.hideKeyboard();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (isDefectChanged) {
            setResult(RESULT_OK);
        }
        this.finish();
    }

    /**
     * 根据键盘的显示与否 动态展示dialog的位置
     */
    @Override
    public void onKeyBoardStateChange(int state) {
        Window mWindow = mBatteryDialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        switch (state) {
            case KeyBoardUtil.KEYBORAD_HIDE:
                lp.gravity = Gravity.CENTER;
                lp.y = 0;
                break;
            case KeyBoardUtil.KEYBORAD_SHOW:
                lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                lp.y = 400;
                break;
            default:
                break;
        }
        mWindow.setAttributes(lp);
    }

    @Override
    public void onKeyBoardNextInput(EditText editText) {

    }
}