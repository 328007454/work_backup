package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.KeyBoardUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.View.WeatherView;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Spacing;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.Transceiver;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.SpacingService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.service.TransceiverService;
import com.cnksi.sjjc.util.FunctionUtil;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ironGe on 2016/6/12.
 * 收发信息界面
 */
public class GetSendLetterActivity extends BaseActivity {
    public static final String TAG = GetSendLetterActivity.class.getSimpleName();
    public static final int LOAD_DEVICE_SUCCESS = 0x100;
    public static final int LOAD_DEVICE_FAILURE = 0x101;
    private final static int ACTION_IMAGE = 0x300;

    @ViewInject(R.id.weatherView1)
    private WeatherView weather;

    @ViewInject(R.id.tab_strip)
    private HorizontalScrollView horizontalScrollView;

    @ViewInject(R.id.tab_container)
    private LinearLayout tabContainer;

    @ViewInject(R.id.image_location)
    private ImageView imageLocation;

    @ViewInject(R.id.tv_spacing)
    private TextView tvSpacing;

    @ViewInject(R.id.edit_send_level)
    private EditText editSendLevel;

    @ViewInject(R.id.edit_receive_level)
    private EditText editReceiveLevel;

    @ViewInject(R.id.radio_channel)
    private RadioGroup radioGroupChannel;

    @ViewInject(R.id.show_pic)
    private ImageView imageShowPic;

    @ViewInject(R.id.pic_num)
    private TextView txtPicNumber;

    @ViewInject(R.id.edit_remark)
    private EditText editRemark;

    @ViewInject(R.id.llpic)
    RelativeLayout llpic;
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
        setChildView(R.layout.activity_get_send_letter);
        x.view().inject(_this);
        tvTitle.setText("收发信机测试");
        getIntentValue();
        initData();
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    report = ReportService.getInstance().getReportById(currentReportId);
                    //查询收发信机设备的集合
                    transceiverDeviceList = DeviceService.getInstance().findTransceiverDevice(currentBdzId);
                    if (null != transceiverDeviceList && !transceiverDeviceList.isEmpty()) {
                        mHandler.sendEmptyMessage(LOAD_DEVICE_SUCCESS);
                    } else
                        mHandler.sendEmptyMessage(LOAD_DEVICE_FAILURE);

                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
        radioGroupChannel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_normal) {
                    llpic.setVisibility(View.GONE);
                } else {
                    llpic.setVisibility(View.VISIBLE);
                }
            }
        });
        if (null == transceiverDeviceList || transceiverDeviceList.isEmpty())
            return;
        int deviceCount = transceiverDeviceList.size();
        for (int i = 0; i < deviceCount; i++) {
            Device transceiverDevice = transceiverDeviceList.get(i);
            try {
                //查询设备所在间隔
                Spacing deviceSpacing = SpacingService.getInstance().findById(Spacing.class, transceiverDevice.spid);
                if (null != deviceSpacing) {
                    transceiveSpacing.put(transceiverDevice.deviceid, deviceSpacing);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            //设置item的格式和点击动画
            View view =  LayoutInflater.from(this).inflate(R.layout.item_tab, null, false);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
//            textView.setTextSize(AutoUtils.getPercentHeightSizeBigger(44));
            AutoUtils.autoSize(view);
            textView.setTag(transceiverDevice);
//            textView.setText(Html.fromHtml(transceiverDevice.name));
            textView.setText(transceiverDevice.name);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(AutoUtils.getPercentWidthSizeBigger(400), LinearLayout.LayoutParams.MATCH_PARENT);
//            textView.setLayoutParams(p);
            p.leftMargin=AutoUtils.getPercentHeightSizeBigger(30);
            view.setLayoutParams(p);
            tabContainer.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation animation = new TranslateAnimation(imageLocation.getLeft(), v.getLeft(), 0, 0);
                    animation.setFillAfter(true);
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    imageLocation.startAnimation(animation);
                    saveCurrentPage();
                    changeTab(v);
                    if (v.getLeft() + v.getWidth() - horizontalScrollView.getScrollX() >= DisplayUtil.getInstance().getWidth())
                        horizontalScrollView.smoothScrollBy(v.getLeft() + v.getWidth() - DisplayUtil.getInstance().getWidth() + 150, 0);
                    else if (v.getLeft() <= horizontalScrollView.getScrollX())
                        horizontalScrollView.smoothScrollBy(v.getLeft() - 150 - horizontalScrollView.getScrollX(), 0);
                }
            });
        }
        changeTab(tabContainer.getChildAt(0));
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DEVICE_SUCCESS:
                initUI();
                break;
            case LOAD_DEVICE_FAILURE:
                CToast.showShort(this, "未查询到收发信机设备");
                break;
        }
    }

    @Event({R.id.btn_back, R.id.btn_finish, R.id.take_pic, R.id.show_pic})
    private void onViewClick(View view) {
        switch (view.getId()) {
            //返回
            case R.id.btn_back:
                KeyBoardUtils.closeKeybord(_this);
                onBackPressed();
                this.finish();
                break;
            //完成任务
            case R.id.btn_finish:
                saveCurrentPage();
                Dialog dialog = new Dialog(this, R.style.dialog);
                ViewHolder holder = new ViewHolder(this, null, R.layout.dialog_test_conclusion, false);
                AutoUtils.autoSize(holder.getRootView());
                dialog.setContentView(holder.getRootView());
                dialogEvent(dialog, holder);
                dialog.show();
                break;
            //拍照
            case R.id.take_pic:
                imageName = FunctionUtil.getCurrentImageName(this);
                FunctionUtil.takePicture(this, imageName, Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
                break;
            //显示大图
            case R.id.show_pic:
                viewPic();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    String picRemark = DateUtils.getCurrentLongTime() + "\n" + report.persons + "\n" + tvSpacing.getText().toString() + "----" + currentTransceiver.deviceName;
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imageName, picRemark);
                    break;
                case LOAD_DATA:
                    savePic();
                    showThumbPic();
                    break;
                //删除后的图片
                case Config.CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> deleteImages = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    ArrayList<String> exitImages = StringUtils.string2List(currentTransceiver.images);
                    for (String image : deleteImages) {
                        String realImage = image.replace(Config.RESULT_PICTURES_FOLDER, "");
                        if (exitImages.contains(realImage)) {
                            exitImages.remove(realImage);
                        }
                    }
                    currentTransceiver.images = StringUtils.ArrayListToString(exitImages);
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
    private void saveCurrentPage() {
        if (null == currentDevice)
            return;
        currentTransceiver.sendLevel = editSendLevel.getText().toString();
        currentTransceiver.receiveLevel = editReceiveLevel.getText().toString();
        currentTransceiver.channelStatus = radioGroupChannel.getCheckedRadioButtonId() == R.id.radio_normal ? 0 : 1;
        currentTransceiver.remark = editRemark.getText().toString();
        try {
            TransceiverService.getInstance().saveOrUpdate(currentTransceiver);
        } catch (DbException e) {
            e.printStackTrace();
        }
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
        for (int i = 0; i < tabContainer.getChildCount(); i++) {
            View childView = tabContainer.getChildAt(i);
            TextView tvView = (TextView) childView.findViewById(R.id.tv_tab);
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
            } else
//                childView.setSelected(false);
                tvView.setSelected(false);
        }

        imageName = null;
        tvSpacing.setText(transceiveSpacing.get(currentDevice.deviceid).name);
        if (null != currentTransceiver) {
            editSendLevel.setText(currentTransceiver.sendLevel);
            editReceiveLevel.setText(currentTransceiver.receiveLevel);
            if (currentTransceiver.channelStatus == 0) {
                radioGroupChannel.check(R.id.radio_normal);
            } else {
                radioGroupChannel.check(R.id.radio_unNormal);
            }
            editRemark.setText(currentTransceiver.remark);
        } else {
            editSendLevel.setText("");
            editReceiveLevel.setText("");
            radioGroupChannel.check(R.id.radio_normal);
            editRemark.setText("");
        }
        showThumbPic();
    }

    /**
     * 显示当前设备记录异常缩略图片
     */
    private void showThumbPic() {
        if (null != currentTransceiver && !TextUtils.isEmpty(currentTransceiver.images)) {
            List<String> images = StringUtils.string2List(currentTransceiver.images);
            if (!images.isEmpty()) {
                imageShowPic.setVisibility(View.VISIBLE);
                if (images.size() > 1) {
                    txtPicNumber.setVisibility(View.VISIBLE);
                    txtPicNumber.setText("" + images.size());
                } else {
                    txtPicNumber.setVisibility(View.GONE);
                }
                mBitmapUtils.bind(imageShowPic, Uri.fromFile(new File(Config.RESULT_PICTURES_FOLDER + images.get(0))).toString());

            }
        } else {
            imageShowPic.setVisibility(View.GONE);
            txtPicNumber.setVisibility(View.GONE);
            txtPicNumber.setText("");
            imageShowPic.setImageBitmap(null);
        }
    }

    /**
     * 查看大图
     */
    private void viewPic() {
        Intent intent = new Intent(this, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, 0);
        intent.putExtra(Config.CANCEL_IMAGEURL_LIST, false);
        ArrayList<String> images = StringUtils.string2List(currentTransceiver.images);
        ArrayList<String> viewImages = new ArrayList<String>();
        for (String image : images) {
            viewImages.add(Config.RESULT_PICTURES_FOLDER + image);
        }
        intent.putStringArrayListExtra(Config.IMAGEURL_LIST, viewImages);
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        startActivityForResult(intent, Config.CANCEL_RESULT_LOAD_IMAGE);
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
            if (sb.length() > 4)
                sb.delete(sb.length() - 4, sb.length());
            report.remain_problems = Html.fromHtml(sb.toString()).toString();
            editRemainProblem.setText(report.remain_problems);
        } catch (DbException e) {
            e.printStackTrace();
            Log.e(TAG, "完成任务查询已有的收发信机记录异常");
        }
        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    //1、修改报告
                    report.jcqk = rbYes.isChecked() ? "正常" : "不正常";
                    report.tq = weather.getSelectWeather();
                    report.endtime = DateUtils.getCurrentLongTime();
                    ReportService.getInstance().saveOrUpdate(report);
                    //2、修改任务
                    Task task = TaskService.getInstance().getTask(currentTaskId);
                    task.status = Task.TaskStatus.done.name();
                    CustomApplication.getDbManager().saveOrUpdate(task);
                    isNeedUpdateTaskState = true;
                    Intent intent = new Intent(_this, GetSendLetterReportActivity.class);
                    startActivity(intent);
                    finish();
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.e(TAG, "完成任务保存数据失败");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        saveCurrentPage();
        super.onBackPressed();
    }
}
