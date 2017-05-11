package com.cnksi.sjjc.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Battery;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.service.BatteryRecordService;
import com.cnksi.sjjc.util.FunctionUtil;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by han on 2016/5/11.
 * 蓄电池检测\蓄电池内阻检测电池item点击后Activity
 */
public class BatteryDialogActivity extends AppCompatActivity {
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

    public static final String TAG = "BatteryDialogActivity";
    public static final int DRAW_IMAGE = 0x100;
    public static final int DELETE_IMAGE = DRAW_IMAGE + 1;
    public static final int LOAD_DATA = DELETE_IMAGE + 1;
    public static final int LOAD_MORE = LOAD_DATA + 1;
    @ViewInject(R.id.tv_title)
    private TextView txtTitle;

    @ViewInject(R.id.label_voltage)
    private TextView txtVoltage;

    @ViewInject(R.id.layout_image)
    private RelativeLayout layoutImage;

    @ViewInject(R.id.edit_voltage)
    private EditText editVoltage;

    @ViewInject(R.id.image_show)
    private ImageView imageShow;

    @ViewInject(R.id.image_num)
    private TextView txtImageNum;

    @ViewInject(R.id.btn_cancel)
    private Button btCancle;

    @ViewInject(R.id.btn_sure)
    private Button btSure;
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
    private ExecutorService mServerice;
    private String bdzName;
    /**
     * 当前检测类型
     * */
    private String currentInspectionType;

    /**
     * 开启svg格式图片兼容
     * */
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        View titleView = this.findViewById(android.R.id.title);
//        titleView.setVisibility(View.GONE);
        setContentView(R.layout.activity_battery_item_dialog);
        x.view().inject(this);
        mServerice = Executors.newCachedThreadPool();
        editVoltage.setInputType(InputType.TYPE_CLASS_NUMBER);
        getIntentValue();
        initUI();
        initData();
//        //指定电池输入类型
//        String inputType = "1234567890.";
//        editVoltage.setKeyListener(DigitsKeyListener.getInstance(inputType));
    }

    /**
     * 适配布局控件
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }
    private void initUI() {
        if ("0".equalsIgnoreCase(typeStr) && !batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))) {
            btCancle.setText(getResources().getString(R.string.dialog_sure_str));
            btSure.setText(getResources().getString(R.string.next));
        }

    }

    private void getIntentValue() {
        batteryCode = getIntent().getStringExtra(Config.CURRENT_BATTERY_NUM);
        batteryCheckType = getIntent().getIntExtra(Config.CURRENT_FUNCTION_MODEL, 0);
        battery = (Battery) getIntent().getSerializableExtra(Config.CURRENT_BATTERY_ZU);
        currentReportId = getIntent().getStringExtra(Config.CURRENT_REPORT_ID);
        typeStr = String.valueOf(getIntent().getIntExtra(Config.CURRENT_TEST_TYPE, 0));
        currentInspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE);
        bdzName = getIntent().getStringExtra(Config.CURRENT_BDZ_NAME);
        txtTitle.setText(batteryCode);
        if (0 == batteryCheckType) {
            txtVoltage.setText("电压(V)");
        } else {
            txtVoltage.setText("内阻(mΩ)");
        }
    }



    private void initData() {
        mServerice.execute(new Runnable() {
            @Override
            public void run() {
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
                editVoltage.setText(batteryRecord.voltage);
                imageStr = batteryRecord.voltageImages;
            } else {
                editVoltage.setText(batteryRecord.resistance);
                imageStr = batteryRecord.resistanceImages;
            }
            if (TextUtils.isEmpty(imageStr)) layoutImage.setVisibility(View.GONE);
            else {
                exitImageList = StringUtils.string2List(imageStr);
                showImageThumb();
            }
        } else {
            layoutImage.setVisibility(View.GONE);
        }

    }

    @Event({R.id.take_picture, R.id.btn_cancel, R.id.btn_sure, R.id.image_show})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture://拍照
                if (0 == batteryCheckType) {
                    imageName = FunctionUtil.getCurrentImageName(this, "DY" + FunctionUtils.getCurrentImageName());
                } else {
                    imageName = FunctionUtil.getCurrentImageName(this, "DZ" + FunctionUtils.getCurrentImageName());
                }
                FunctionUtils.takePicture(this, imageName, Config.RESULT_PICTURES_FOLDER);
                break;
            case R.id.btn_cancel:
                if ("0".equalsIgnoreCase(typeStr) && !batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))) {
                    saveData();
                    finishDialog();
                } else {
                    finish();
                }

                break;
            case R.id.btn_sure://取消或者确定按钮
                if ("0".equalsIgnoreCase(typeStr)) {
                    if (batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))
                            ||(2==String.valueOf(battery.amount).length()&&batteryCode.substring(1).equalsIgnoreCase(String.valueOf(battery.amount)))
                            ||(1==String.valueOf(battery.amount).length()&&batteryCode.substring(2).equalsIgnoreCase(String.valueOf(battery.amount)))) {
                        Toast.makeText(this, "当前电池数为最后一节了", Toast.LENGTH_LONG).show();
                        saveData();
                        finishDialog();
                        return;
                    }
                    saveData();
                    setChangedBatteryCode();
                } else {
                    finishDialog();
                }

                break;
            case R.id.image_show://查看图片
                Intent intent = new Intent(this, ImageDetailsActivity.class);
                intent.putExtra(Config.CURRENT_IMAGE_POSITION, 0);
                intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);
                intent.putStringArrayListExtra(Config.IMAGEURL_LIST, StringUtils.addStrToListItem(exitImageList, Config.RESULT_PICTURES_FOLDER));
                intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, true);
                startActivityForResult(intent, DELETE_IMAGE);
                break;
        }
    }

    /**
     * 结束DialogActivity
     */
    private void finishDialog() {
        Intent data = new Intent();
        data.putExtra("batteryCode", batteryCode);
        data.putExtra("images", exitImageList);
        data.putExtra("value", editVoltage.getText().toString().trim());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * 设置改变过后的电池信息
     */
    private void setChangedBatteryCode() {
        if (batteryCode.startsWith("00")) {
            if (batteryCode.equalsIgnoreCase("009"))
                batteryCode = "010";
            else
                batteryCode = String.valueOf("00" + (Integer.valueOf(batteryCode.substring(batteryCode.length() - 1)) + 1));
        } else if (batteryCode.startsWith("0")) {
            if (batteryCode.equalsIgnoreCase("099"))
                batteryCode = "100";
            else
                batteryCode = String.valueOf("0" + (Integer.valueOf(batteryCode.substring(batteryCode.length() - 2)) + 1));
        } else {
            batteryCode = String.valueOf(Integer.valueOf(batteryCode) + 1);
        }
        if (batteryCode.equalsIgnoreCase(String.valueOf(battery.amount))
                ||(2==String.valueOf(battery.amount).length()&&batteryCode.substring(1).equalsIgnoreCase(String.valueOf(battery.amount)))
                ||(1==String.valueOf(battery.amount).length()&&batteryCode.substring(2).equalsIgnoreCase(String.valueOf(battery.amount)))) {
            btCancle.setText("取消");
            btSure.setText("确定");
        }
        batteryRecord = batteryRecordMap.get(batteryCode);
        txtImageNum.setText("");
        txtTitle.setText(batteryCode);
        imageShow.setImageURI(null);
        editVoltage.setText("");
        if (0 == batteryCheckType && batteryRecord != null) {
            editVoltage.setText(TextUtils.isEmpty(batteryRecord.voltage) ? "" : batteryRecord.voltage);
        } else if (batteryRecord != null) {
            editVoltage.setText(TextUtils.isEmpty(batteryRecord.resistance) ? "" : batteryRecord.resistance);
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
                case Config.ACTION_IMAGE://拍照返回
                    exitImageList.add(imageName);
                    StringBuffer sb = new StringBuffer(DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8)).append("\n" + battery.name).append("\n" + batteryCode);
                    if (0 == batteryCheckType) {
                        sb.append("---电压异常");
                    } else {
                        sb.append("---内阻异常");
                    }
                    Intent intent = new Intent(this, DrawCircleImageActivity.class);
                    intent.putExtra(Config.CURRENT_IMAGE_NAME, Config.RESULT_PICTURES_FOLDER + imageName);
                    intent.putExtra(Config.PICTURE_CONTENT, sb.toString());
                    startActivityForResult(intent, DRAW_IMAGE);
                    break;
                case DRAW_IMAGE://图片标记
                    showImageThumb();
                    break;
                case DELETE_IMAGE://图片删除
                    ArrayList<String> deleteImageList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String imageUrl : deleteImageList) {
                        exitImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                    }
                    showImageThumb();
                    saveData();
                    break;
            }
        }
    }

    /**
     * 显示图片数量和图片控件
     */
    private void showImageThumb() {
        if (!exitImageList.isEmpty()) {
            layoutImage.setVisibility(View.VISIBLE);
            if (exitImageList.size() > 1) {
                txtImageNum.setVisibility(View.VISIBLE);
                txtImageNum.setText(exitImageList.size() + "");
            } else txtImageNum.setVisibility(View.GONE);
            imageShow.setImageURI(Uri.fromFile(new File(Config.RESULT_PICTURES_FOLDER + exitImageList.get(0))));
        } else {
            layoutImage.setVisibility(View.GONE);
        }
    }

    private void saveData() {
        if ("0".equalsIgnoreCase(typeStr)) {
            if (null == batteryRecord && (!TextUtils.isEmpty(editVoltage.getText().toString().trim()) || !exitImageList.isEmpty()))
                batteryRecord = new BatteryRecord(currentReportId, battery.bdzid, bdzName, battery.bid, batteryCode, currentInspectionType, Integer.valueOf(typeStr));
            try {
                if (null != batteryRecord) {
                    String imageStr = StringUtils.ArrayListToString(exitImageList);
                    if (batteryCheckType == 0) {
                        batteryRecord.voltage = editVoltage.getText().toString().trim();
                        batteryRecord.voltageImages = imageStr;
                    } else {
                        batteryRecord.resistance = editVoltage.getText().toString().trim();
                        batteryRecord.resistanceImages = imageStr;
                    }
                    batteryRecord.last_modify_time = DateUtils.getCurrentLongTime();
                    BatteryRecordService.getInstance().saveOrUpdate(batteryRecord);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
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
                            editVoltage.setText(batteryRecordMap.get(batteryCode).voltage);
                        } else {
                            editVoltage.setText(batteryRecordMap.get(batteryCode).resistance);
                        }
                    }
                    dealPicsAndVoltage(batteryRecord);
                    break;
                case LOAD_MORE:

                    break;
            }
        }
    };

}
