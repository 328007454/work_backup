package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Transceiver;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.SpacingService;
import com.cnksi.sjjc.service.TransceiverService;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ironGe on 2016/6/16.
 */
public class GetSendLetterReportActivity extends BaseReportActivity {

    @ViewInject(R.id.ll_exception_pic)
    private LinearLayout llExceptionPic;

    @ViewInject(R.id.tv_start_time)
    private TextView tvStartTime;

    @ViewInject(R.id.tv_end_time)
    private TextView tvEndTime;

    @ViewInject(R.id.tv_person)
    private TextView tvPerson;

    @ViewInject(R.id.tv_weather)
    private TextView tvWeather;

    @ViewInject(R.id.tv_test_conclusion)
    private TextView tvTestConclusion;

    @ViewInject(R.id.tv_test_condition)
    private TextView tvTestCondition;

    @ViewInject(R.id.ll_exception)
    private LinearLayout llException;

    Report currentReport;
    private List<Transceiver> exceptionList;
    private List<Transceiver> transceiverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View setReportView() {
        return getLayoutInflater().inflate(R.layout.report_get_send_letter, null);
    }

    void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    currentReport = ReportService.getInstance().getReportById(currentReportId);
                    exceptionList = TransceiverService.getInstance().findExceptionTransceiver(currentReportId);
                    transceiverList = TransceiverService.getInstance().findExitTransceiver(currentReportId);
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.e("this", "初始化数据出错");
                }
            }
        });
    }

    @Event({R.id.tv_look_details})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_look_details:
                //查看详情
                Intent intent1 = new Intent(_this, GetSendLetterActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                if (currentReport != null) {
                    tvStartTime.setText(currentReport.starttime);
                    tvEndTime.setText(currentReport.endtime);
                    tvPerson.setText(currentReport.persons);
                    tvWeather.setText(currentReport.tq);
                    tvTestConclusion.setText(currentReport.jcqk);
                    tvTestCondition.setText(doColor(currentReport.remain_problems));
                    if (currentReport.remain_problems != null && !currentReport.remain_problems.isEmpty()) {
                        tvTestCondition.append("\n");
                    }
                    appendNormalCondition();
                }
                showExceptionPic();
                break;
            default:
                break;
        }
    }

    /**
     * 展示异常图片
     */
    public void showExceptionPic() {
        llException.setVisibility(View.GONE);
        if (null != exceptionList && !exceptionList.isEmpty()) {
            for (Transceiver transceiver : exceptionList) {
                final String img = transceiver.images;
                if (!TextUtils.isEmpty(img)) {
                    llException.setVisibility(View.VISIBLE);
                    final List<String> imageList = StringUtils.string2List(img);
                    ViewHolder holder = new ViewHolder(this, null, R.layout.item_exception_pic, false);
                    AutoUtils.autoSize(holder.getRootView());
                    if (1 < imageList.size()) {
                        holder.setText(R.id.tv_exception_num, imageList.size() + "");
                        holder.setVisable(R.id.tv_exception_num, View.VISIBLE);
                    }
                    String spName = SpacingService.getInstance().findSpacingByDeviceId(transceiver.deviceId);

                    holder.setText(R.id.tv_device_name, spName + "——" + transceiver.deviceName);
                    x.image().bind((ImageView) holder.getView(R.id.iv_exception_pic), Config.RESULT_PICTURES_FOLDER + imageList.get(0), CustomApplication.getLargeImageOptions());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    p.leftMargin = AutoUtils.getPercentHeightSizeBigger(60);
                    llExceptionPic.addView(holder.getRootView(), p);
                    //点击显示大图
                    //声明一个存放图片完整路径的集合
                    final ArrayList<String> imgs = new ArrayList<String>();
                    if (imageList != null && (imageList.size() > 0)) {
                        for (int i = 0; i < imageList.size(); i++) {
                            imgs.add(Config.RESULT_PICTURES_FOLDER + imageList.get(i));
                        }
                        holder.getView(R.id.iv_exception_pic).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showImageDetails(_this, imgs);
                            }
                        });
                    }
                }
            }
        }
    }


    /**
     * 测试情况补充：正常的数据
     */
    private void appendNormalCondition() {
        if (transceiverList != null && !transceiverList.isEmpty()) {
            for (Transceiver transceiver : transceiverList) {
                if (transceiver.channelStatus != 1) {
                    String spacingName = SpacingService.getInstance().findSpacingByDeviceId(transceiver.deviceId);
                    tvTestCondition.append(spacingName + "----" + transceiver.deviceName + "正常\n");
                }
            }
        }
    }

    /**
     * 异常文字变色
     */
    public SpannableStringBuilder doColor(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        String filter = "异常";
        int index = str.indexOf(filter);
        SpannableStringBuilder builder = new SpannableStringBuilder(str);

        while (index >= 0) {
//            getResources().getColor(R.color.red_unpressed_color)
            ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(_this,R.color.red_unpressed_color));
            builder.setSpan(redSpan, index, index + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = str.indexOf(filter, index + 2);
        }
        return builder;
    }

}
