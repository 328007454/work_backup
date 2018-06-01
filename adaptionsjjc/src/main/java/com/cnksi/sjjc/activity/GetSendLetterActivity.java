package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Transceiver;
import com.cnksi.sjjc.databinding.ActivityGetSendLetterBinding;
import com.cnksi.sjjc.service.TransceiverService;
import com.cnksi.sjjc.util.FunctionUtil;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * Created by ironGe on 2016/6/12.
 * 收发信息界面
 */
public class GetSendLetterActivity extends BaseSjjcActivity {
    public static final String TAG = GetSendLetterActivity.class.getSimpleName();
    public static final int LOAD_DEVICE_SUCCESS = 0x100;
    public static final int LOAD_DEVICE_FAILURE = 0x101;
    private final static int ACTION_IMAGE = 0x300;


    ActivityGetSendLetterBinding binding;
    //收发信机设备
    private List<Device> transceiverDeviceList;

    private Device currentDevice;

    private Transceiver currentTransceiver;

    private Report report;

    private String imageName;

    //当前报告存在的收发信机记录
    private Map<String, Transceiver> exitTransceiverMap = new HashMap<String, Transceiver>();
    //存放 key:设备名称  value间隔
    private Map<String, Spacing> transceiveSpacing = new HashMap<String, Spacing>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetSendLetterBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        initView();
        loadData();
        mTitleBinding.tvTitle.setText("收发信机测试");
        initOnclick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                report = ReportService.getInstance().getReportById(currentReportId);
                //查询收发信机设备的集合
                transceiverDeviceList = DeviceService.getInstance().findTransceiverDevice(currentBdzId);
                if (null != transceiverDeviceList && !transceiverDeviceList.isEmpty()) {
                    mHandler.sendEmptyMessage(LOAD_DEVICE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(LOAD_DEVICE_FAILURE);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    public void initView() {
        binding.radioChannel.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radio_normal) {
                binding.llpic.setVisibility(View.GONE);
            } else {
                binding.llpic.setVisibility(View.VISIBLE);
            }
        });
        if (null == transceiverDeviceList || transceiverDeviceList.isEmpty()) {
            return;
        }
        int deviceCount = transceiverDeviceList.size();
        for (int i = 0; i < deviceCount; i++) {
            Device transceiverDevice = transceiverDeviceList.get(i);
            try {
                //查询设备所在间隔
                Spacing deviceSpacing = SpacingService.getInstance().findById(transceiverDevice.spid);
                if (null != deviceSpacing) {
                    transceiveSpacing.put(transceiverDevice.deviceid, deviceSpacing);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            //设置item的格式和点击动画
            View view = LayoutInflater.from(this).inflate(R.layout.item_tab, null, false);
            TextView textView = view.findViewById(R.id.tv_tab);
//            textView.setTextSize(AutoUtils.getPercentHeightSizeBigger(44));
            AutoUtils.autoSize(view);
            textView.setTag(transceiverDevice);
//            textView.setText(Html.fromHtml(transceiverDevice.name));
            textView.setText(transceiverDevice.name);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(AutoUtils.getPercentWidthSizeBigger(400), LinearLayout.LayoutParams.MATCH_PARENT);
//            textView.setLayoutParams(p);
            p.leftMargin = AutoUtils.getPercentHeightSizeBigger(30);
            view.setLayoutParams(p);
            binding.tabContainer.addView(view);
            view.setOnClickListener(v -> {
                TranslateAnimation animation = new TranslateAnimation(binding.imageLocation.getLeft(), v.getLeft(), 0, 0);
                animation.setFillAfter(true);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                binding.imageLocation.startAnimation(animation);
                saveCurrentPage();
                changeTab(v);
                if (v.getLeft() + v.getWidth() - binding.tabStrip.getScrollX() >= DisplayUtils.getInstance().getWidth()) {
                    binding.tabStrip.smoothScrollBy(v.getLeft() + v.getWidth() - DisplayUtils.getInstance().getWidth() + 150, 0);
                } else if (v.getLeft() <= binding.tabStrip.getScrollX()) {
                    binding.tabStrip.smoothScrollBy(v.getLeft() - 150 - binding.tabStrip.getScrollX(), 0);
                }
            });
        }
        changeTab(binding.tabContainer.getChildAt(0));
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DEVICE_SUCCESS:
                initView();
                break;
            case LOAD_DEVICE_FAILURE:
                ToastUtils.showMessage("未查询到收发信机设备");
                break;
            default:
        }
    }

    private void initOnclick() {
        mTitleBinding.btnBack.setOnClickListener(view -> {
            KeyBoardUtils.closeKeybord(mActivity);
            onBackPressed();
            finish();
        });

        binding.btnFinish.setOnClickListener(view -> {
            if (saveCurrentPage()) {
                Dialog dialog = new Dialog(GetSendLetterActivity.this, R.style.dialog);
                ViewHolder holder = new ViewHolder(GetSendLetterActivity.this, null, R.layout.dialog_test_conclusion, false);
                AutoUtils.autoSize(holder.getRootView());
                dialog.setContentView(holder.getRootView());
                dialogEvent(dialog, holder);
                dialog.show();
            }
        });
        binding.takePic.setOnClickListener(view -> {
            imageName = FunctionUtil.getCurrentImageName(GetSendLetterActivity.this);
            FunctionUtil.takePicture(GetSendLetterActivity.this, imageName, Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
        });
        binding.showPic.setOnClickListener(view -> viewPic());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    String picRemark = DateUtils.getCurrentLongTime() + "\n" + report.persons + "\n" + binding.tvSpacing.getText().toString() + "----" + currentTransceiver.deviceName;
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imageName, picRemark);
                    break;
                case LOAD_DATA:
                    savePic();
                    showThumbPic();
                    break;
                //删除后的图片
                case Config.CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> deleteImages = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    ArrayList<String> exitImages = com.cnksi.core.utils.StringUtils.string2List(currentTransceiver.images, false);
                    for (String image : deleteImages) {
                        String realImage = image.replace(Config.RESULT_PICTURES_FOLDER, "");
                        if (exitImages.contains(realImage)) {
                            exitImages.remove(realImage);
                        }
                    }
                    currentTransceiver.images = com.cnksi.core.utils.StringUtils.arrayListToString(exitImages);
                    showThumbPic();
                    saveCurrentPage();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 存取当前界面数据
     */
    private boolean saveCurrentPage() {
        String sendLevelStr = binding.editSendLevel.getText().toString();
        String receiveLevelStr = binding.editReceiveLevel.getText().toString();
        if ((!TextUtils.isEmpty(sendLevelStr) || !TextUtils.isEmpty(receiveLevelStr)) && (TextUtils.isEmpty(StringUtilsExt.getDecimalPoint(sendLevelStr)) || TextUtils.isEmpty(StringUtilsExt.getDecimalPoint(receiveLevelStr)))) {
            ToastUtils.showMessage("请输入正确的发信电平或者收信电平");
            return false;
        }
        if (null == currentDevice) {
            ToastUtils.showMessage("当前页面没有设备，记录将不会保存。");
            return true;
        }
        currentTransceiver.sendLevel = StringUtilsExt.getDecimalPoint(sendLevelStr);
        currentTransceiver.receiveLevel = StringUtilsExt.getDecimalPoint(receiveLevelStr);
        currentTransceiver.channelStatus = binding.radioChannel.getCheckedRadioButtonId() == R.id.radio_normal ? 0 : 1;
        currentTransceiver.remark = binding.editRemark.getText().toString();
        try {
            TransceiverService.getInstance().saveOrUpdate(currentTransceiver);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 保存当前设备记录异常图片
     */
    private void savePic() {
        if (TextUtils.isEmpty(currentTransceiver.images)) {
            currentTransceiver.images = TextUtils.isEmpty(imageName) ? "" : imageName;
        } else {
            currentTransceiver.images += TextUtils.isEmpty(imageName) ? "" : "," + imageName;
        }
    }

    /**
     * 更改tab
     *
     * @param v
     */
    private void changeTab(View v) {
        for (int i = 0; i < binding.tabContainer.getChildCount(); i++) {
            View childView = binding.tabContainer.getChildAt(i);
            TextView tvView = childView.findViewById(R.id.tv_tab);
            if (v.equals(childView)) {
//                childView.setSelected(true);
                tvView.setSelected(true);
                currentDevice = (Device) tvView.getTag();
                try {
                    //从缓存中查询当前设备记录
                    currentTransceiver = exitTransceiverMap.get(currentDevice.deviceid);
                    if (null == currentTransceiver) {
                        //从数据库中查询
                        currentTransceiver = TransceiverService.getInstance().findExitTransceiver(currentReportId, currentDevice.deviceid);
                        if (null == currentTransceiver) {
                            //创建对象放入缓存
                            currentTransceiver = new Transceiver(currentReportId, currentBdzId, currentBdzName, currentDevice.deviceid, currentDevice.name);
                        }
                        exitTransceiverMap.put(currentDevice.deviceid, currentTransceiver);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            } else {
//                childView.setSelected(false);
                tvView.setSelected(false);
            }
        }
        imageName = null;
        binding.tvSpacing.setText(transceiveSpacing.get(currentDevice.deviceid).name);
        if (null != currentTransceiver) {
            binding.editSendLevel.setText(currentTransceiver.sendLevel);
            binding.editReceiveLevel.setText(currentTransceiver.receiveLevel);
            if (currentTransceiver.channelStatus == 0) {
                binding.radioChannel.check(R.id.radio_normal);
            } else {
                binding.radioChannel.check(R.id.radio_unNormal);
            }
            binding.editRemark.setText(currentTransceiver.remark);
        } else {
            binding.editSendLevel.setText("");
            binding.editReceiveLevel.setText("");
            binding.radioChannel.check(R.id.radio_normal);
            binding.editRemark.setText("");
        }
        showThumbPic();
    }

    /**
     * 显示当前设备记录异常缩略图片
     */
    private void showThumbPic() {
        if (null != currentTransceiver && !TextUtils.isEmpty(currentTransceiver.images)) {
            List<String> images = com.cnksi.core.utils.StringUtils.string2List(currentTransceiver.images, false);
            if (!images.isEmpty()) {
                binding.showPic.setVisibility(View.VISIBLE);
                if (images.size() > 1) {
                    binding.tvPics.setVisibility(View.VISIBLE);
                    binding.tvPics.setText("" + images.size());
                } else {
                    binding.tvPics.setVisibility(View.GONE);
                }
                Bitmap bitmap = BitmapUtils.getImageThumbnailByHeight(new File(Config.RESULT_PICTURES_FOLDER + images.get(0)).toString(), 100);
                binding.showPic.setImageBitmap(bitmap);
            }
        } else {
            binding.showPic.setVisibility(View.GONE);
            binding.tvPics.setVisibility(View.GONE);
            binding.tvPics.setText("");
            binding.showPic.setImageBitmap(null);
        }
    }

    /**
     * 查看大图
     */
    private void viewPic() {
        ArrayList<String> images =  StringUtils.string2List(currentTransceiver.images, false);
        showImageDetails(this,StringUtils.addStrToListItem(images,Config.RESULT_PICTURES_FOLDER),true);
    }

    /**
     * dialog事件和内容获取
     *
     * @param dialog
     * @param holder
     */
    private void dialogEvent(final Dialog dialog, ViewHolder holder) {
        final RadioButton rbYes = holder.getView(R.id.status_yes);
        rbYes.setChecked(true);
        try {
            StringBuffer sb = new StringBuffer();
            EditText editRemainProblem = holder.getView(R.id.status_result);
            List<Transceiver> transceiverList = TransceiverService.getInstance().findExitTransceiver(currentReportId);
            for (Transceiver transceiver : transceiverList) {
                if (transceiver.channelStatus != 0) {
                    sb.append(String.format("%1$s%2$s异常", transceiveSpacing.get(transceiver.deviceId).name + "----", transceiver.deviceName)).append("<br>");
                }
            }
            if (sb.length() > 4) {
                sb.delete(sb.length() - 4, sb.length());
            }
            report.remain_problems = Html.fromHtml(sb.toString()).toString();
            editRemainProblem.setText(report.remain_problems);
        } catch (DbException e) {
            e.printStackTrace();
            Log.e(TAG, "完成任务查询已有的收发信机记录异常");
        }
        holder.getView(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        holder.getView(R.id.btn_confirm).setOnClickListener(v -> {
            dialog.dismiss();
            try {
                //1、修改报告
                report.jcqk = rbYes.isChecked() ? "正常" : "不正常";
                report.tq = binding.weatherView1.getSelectWeather();
                report.endtime = DateUtils.getCurrentLongTime();
                ReportService.getInstance().saveOrUpdate(report);
                //2、修改任务
                Task task = TaskService.getInstance().findById(currentTaskId);
                task.status = TaskStatus.done.name();
                TaskService.getInstance().saveOrUpdate(task);
                isNeedUpdateTaskStatus = true;
                Intent intent = new Intent(mActivity, GetSendLetterReportActivity.class);
                startActivity(intent);
                finish();
            } catch (DbException e) {
                e.printStackTrace();
                Log.e(TAG, "完成任务保存数据失败");
            }
        });
    }

    @Override
    public void onBackPressed() {
        saveCurrentPage();
        super.onBackPressed();
    }
}
