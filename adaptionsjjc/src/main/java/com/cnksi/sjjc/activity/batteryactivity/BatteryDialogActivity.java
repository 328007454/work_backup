package com.cnksi.sjjc.activity.batteryactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.model.Battery;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.databinding.ActivityBatteryItemDialogBinding;
import com.cnksi.sjjc.service.BatteryRecordService;
import com.cnksi.sjjc.util.FunctionUtil;
import com.cnksi.sjjc.util.FunctionUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.cnksi.common.Config.DRAW_IMAGE;
import static com.cnksi.common.Config.LOAD_DATA;


/**
 * Created by han on 2016/5/11.
 * 蓄电池检测\蓄电池内阻检测电池item点击后Activity
 */
public class BatteryDialogActivity extends BaseActivity {


    public static final String TAG = "BatteryDialogActivity";

    /**
     * 电池标号
     */
    private String batteryCode;

    private Battery battery;
    /**
     * 检测类型：电压或者是电阻
     */
    private int batteryCheckType;

    private String currentReportId;

    private BatteryRecord batteryRecord;

    private String imageName;
    /**
     * 异常照片
     */
    private ArrayList<String> exitImageList = new ArrayList<String>();

    private String typeStr = "0";
    /**
     * 以前抄录的电池标号对应的电池集合
     */
    private ArrayList<BatteryRecord> batteryListCode = new ArrayList<>();
    private Map<String, BatteryRecord> batteryRecordMap = new HashMap<String, BatteryRecord>();
    private String bdzName;
    /**
     * 当前检测类型
     */
    private String currentInspectionType;

    /**
     * 开启svg格式图片兼容
     * */
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ActivityBatteryItemDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_battery_item_dialog);
        getIntentValue();
        initView();
        loadData();
        initOnClick();
    }




    private void initView() {
        if ("0".equalsIgnoreCase(typeStr) && !batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))) {
            binding.btnCancel.setText(getResources().getString(R.string.dialog_sure_str));
            binding.btnSure.setText(getResources().getString(R.string.next));
        }
    }

    @Override
    protected void getIntentValue() {
        batteryCode = getIntent().getStringExtra(Config.CURRENT_BATTERY_NUM);
        batteryCheckType = getIntent().getIntExtra(Config.CURRENT_FUNCTION_MODEL, 0);
        battery = (Battery) getIntent().getSerializableExtra(Config.CURRENT_BATTERY_ZU);
        currentReportId = getIntent().getStringExtra(Config.CURRENT_REPORT_ID);
        typeStr = String.valueOf(getIntent().getIntExtra(Config.CURRENT_TEST_TYPE, 0));
        currentInspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE);
        bdzName = getIntent().getStringExtra(Config.CURRENT_BDZ_NAME);
        binding.tvTitle.setText(batteryCode);

        if (0 == batteryCheckType) {
            binding.labelVoltage.setText("电压(V)");
        } else {
            binding.labelVoltage.setText("内阻(mΩ)");
        }
    }


    private void loadData() {
        ExecutorManager.executeTask(() -> {
            try {
                batteryRecord = BatteryRecordService.getInstance().getBatteryRecord(currentReportId, battery.bid, batteryCode);
                batteryListCode = (ArrayList<BatteryRecord>) BatteryRecordService.getInstance().getBatteryRecordLatest(currentReportId, battery.bid, false);
                if ("0".equalsIgnoreCase(typeStr)) {
                    if (null != batteryListCode && !batteryListCode.isEmpty()) {
                        for (BatteryRecord record : batteryListCode) {
                            batteryRecordMap.put(record.battary_code, record);
                        }
                    }
                }
                handler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
                Log.e(TAG, "查询数据出错");
            }
        });

    }

    /**
     * 得到图片的数量和电压值
     */
    private void dealPicsAndVoltage(BatteryRecord batteryRecord) {
        exitImageList.clear();
        if (null != batteryRecord) {
            String imageStr = "";

            if (0 == batteryCheckType) {
                binding.editVoltage.setText(batteryRecord.voltage);
                imageStr = batteryRecord.voltageImages;
            } else {
                binding.editVoltage.setText(batteryRecord.resistance);
                imageStr = batteryRecord.resistanceImages;
            }

            if (TextUtils.isEmpty(imageStr)) {
                binding.layoutImage.setVisibility(View.GONE);
            } else {
                exitImageList = StringUtils.stringToList(imageStr);
                showImageThumb();
            }
        } else {
            binding.layoutImage.setVisibility(View.GONE);
        }

    }

    private void initOnClick() {
        binding.takePicture.setOnClickListener(view -> {
            if (0 == batteryCheckType) {
                imageName = FunctionUtil.getCurrentImageName(BatteryDialogActivity.this, "DY" + FunctionUtils.getCurrentImageName());
            } else {
                imageName = FunctionUtil.getCurrentImageName(BatteryDialogActivity.this, "DZ" + FunctionUtils.getCurrentImageName());
            }
            FunctionUtils.takePicture(BatteryDialogActivity.this, imageName, Config.RESULT_PICTURES_FOLDER);
        });
        binding.imageShow.setOnClickListener(view -> {
            ImageDetailsActivity.with(mActivity).setShowDelete(true).setImageUrlList(StringUtils.addStrToListItem(exitImageList, Config.RESULT_PICTURES_FOLDER))
                    .setPosition(0).start();

        });
        binding.btnCancel.setOnClickListener(view -> {
            if ("0".equalsIgnoreCase(typeStr) && !batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))) {
                if (saveData()) {
                    finishDialog();
                } else {
                    ToastUtils.showMessage("请输入正确的值!");
                    return;
                }
            } else {
                finish();
            }
        });

        binding.btnSure.setOnClickListener(view -> {
            if ("0".equalsIgnoreCase(typeStr)) {
                if (batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))
                        || (2 == String.valueOf(battery.amount).length() && batteryCode.substring(1).equalsIgnoreCase(String.valueOf(battery.amount)))
                        || (1 == String.valueOf(battery.amount).length() && batteryCode.substring(2).equalsIgnoreCase(String.valueOf(battery.amount)))) {
                   ToastUtils.showMessage( "当前电池数为最后一节了");
                    saveData();
                    finishDialog();
                    return;
                }
                if (saveData()) {
                    setChangedBatteryCode();
                } else {
                    ToastUtils.showMessage("请输入正确的值!");
                    return;
                }
            } else {
                finishDialog();
            }

        });
    }

    /**
     * 结束DialogActivity
     */
    private void finishDialog() {
        Intent data = new Intent();
        data.putExtra("batteryCode", batteryCode);
        data.putExtra("images", exitImageList);
        data.putExtra("value", binding.editVoltage.getText().toString().trim());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 设置改变过后的电池信息
     */
    private void setChangedBatteryCode() {

        if (batteryCode.startsWith("00")) {
            if (batteryCode.equalsIgnoreCase("009")) {
                batteryCode = "010";
            } else {
                batteryCode = String.valueOf("00" + (Integer.valueOf(batteryCode.substring(batteryCode.length() - 1)) + 1));
            }
        } else if (batteryCode.startsWith("0")) {
            if (batteryCode.equalsIgnoreCase("099")) {
                batteryCode = "100";
            } else {
                batteryCode = String.valueOf("0" + (Integer.valueOf(batteryCode.substring(batteryCode.length() - 2)) + 1));
            }
        } else {
            batteryCode = String.valueOf(Integer.valueOf(batteryCode) + 1);
        }
        if (batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))
                || (2 == String.valueOf(battery.amount).length() && batteryCode.substring(1).equalsIgnoreCase(String.valueOf(battery.amount)))
                || (1 == String.valueOf(battery.amount).length() && batteryCode.substring(2).equalsIgnoreCase(String.valueOf(battery.amount)))) {
            binding.btnCancel.setText("取消");
            binding.btnSure.setText("确定");
        }
        batteryRecord = batteryRecordMap.get(batteryCode);
        binding.imageNum.setText("");
        binding.tvTitle.setText(batteryCode);

        binding.imageShow.setImageURI(null);
        binding.editVoltage.setText("");
        if (0 == batteryCheckType && batteryRecord != null) {
            binding.editVoltage.setText(TextUtils.isEmpty(batteryRecord.voltage) ? "" : batteryRecord.voltage);
        } else if (batteryRecord != null) {
            binding.editVoltage.setText(TextUtils.isEmpty(batteryRecord.resistance) ? "" : batteryRecord.resistance);
        }
        try {
            batteryRecord = BatteryRecordService.getInstance().getBatteryRecord(currentReportId, battery.bid, batteryCode);
            dealPicsAndVoltage(batteryRecord);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Config.ACTION_IMAGE:
                    //拍照返回
                    exitImageList.add(imageName);
                    StringBuffer sb = new StringBuffer(DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm)).append("\n" + battery.name).append("\n" + batteryCode);
                    if (0 == batteryCheckType) {
                        sb.append("---电压异常");
                    } else {
                        sb.append("---内阻异常");
                    }
                    DrawCircleImageActivity.with(this).setPath(Config.RESULT_PICTURES_FOLDER + imageName)
                            .setTxtContent(sb.toString()).setRequestCode(DRAW_IMAGE).start();
                    break;
                case DRAW_IMAGE:
                    //图片标记
                    showImageThumb();
                    break;
                case Config.CANCEL_RESULT_LOAD_IMAGE:
                    //图片删除
                    ArrayList<String> deleteImageList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String imageUrl : deleteImageList) {
                        exitImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                    }
                    showImageThumb();
                    saveData();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示图片数量和图片控件
     */
    private void showImageThumb() {

        if (!exitImageList.isEmpty()) {
            binding.layoutImage.setVisibility(View.VISIBLE);
            if (exitImageList.size() > 1) {
                binding.imageNum.setVisibility(View.VISIBLE);
                binding.imageNum.setText(exitImageList.size() + "");
            } else {
                binding.imageNum.setVisibility(View.GONE);
            }
            binding.imageShow.setImageURI(Uri.fromFile(new File(Config.RESULT_PICTURES_FOLDER + exitImageList.get(0))));
        } else {
            binding.layoutImage.setVisibility(View.GONE);
        }
    }

    private boolean saveData() {
        if ("0".equalsIgnoreCase(typeStr)) {
            String value = binding.editVoltage.getText().toString().trim();
            try {
                if (new Float(value) > 9999) {
                    return false;
                }
                if (!TextUtils.isEmpty(value)) {
                    value = String.valueOf(Double.parseDouble(binding.editVoltage.getText().toString().trim()));
                }
            } catch (NumberFormatException ex) {
                return false;
            }
            if (null == batteryRecord && (!TextUtils.isEmpty(value) || !exitImageList.isEmpty())) {
                batteryRecord = new BatteryRecord(currentReportId, battery.bdzid, bdzName, battery.bid, batteryCode, currentInspectionType, Integer.valueOf(typeStr));
            }
            try {
                if (null != batteryRecord) {
                    String imageStr = StringUtils.arrayListToString(exitImageList);
                    if (batteryCheckType == 0) {
                        batteryRecord.voltage = StringUtilsExt.getDecimalPoint(value, 3);
                        batteryRecord.voltageImages = imageStr;
                    } else {
                        batteryRecord.resistance = StringUtilsExt.getDecimalPoint(value, 3);
                        batteryRecord.resistanceImages = imageStr;
                    }
                    batteryRecord.last_modify_time = DateUtils.getCurrentLongTime();
                    BatteryRecordService.getInstance().saveOrUpdate(batteryRecord);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(R.anim.activity_close, 0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_DATA:
                    if (null != batteryRecordMap.get(batteryCode) && "0".equalsIgnoreCase(typeStr)) {
                        if (0 == batteryCheckType) {
                            binding.editVoltage.setText(batteryRecordMap.get(batteryCode).voltage);
                        } else {
                            binding.editVoltage.setText(batteryRecordMap.get(batteryCode).resistance);
                        }
                    }
                    dealPicsAndVoltage(batteryRecord);
                    break;
                default:
            }
        }
    };

}
