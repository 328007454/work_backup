package com.cnksi.bdzinspection.utils;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.cnksi.bdzinspection.R;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.core.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kkk on 2018/1/29.
 */

public class TimePickerUtils implements View.OnClickListener {

    public static TimePickerUtils pickerUtils;
    public Activity mActivity;
    public TimePickerView pvCustomTime;
    private String time;
    private boolean initWidget;
    private Button mBtnReback;
    private Button mBtnSure;
    private TextView mTxtAddHout;
    private TextView mTxtMinusHour;
    private TextView mTxtAddMinute;
    private TextView mTxtMinusMinute;
    private TextView mTxtTitle;
    private TextView mTxtSumTime;
    private View resLayOutView;
    private boolean isStartTime;
    private String dateString;
    private String secondTime;
    private String mDifferenceTime;

    public interface TimePickerMissListener {
        void returnDateTime(String dateString, boolean isStartTime);
    }

    TimePickerMissListener mMissListener;

    public void setTimePickListener(TimePickerMissListener missListener) {
        this.mMissListener = missListener;
    }

    public static TimePickerUtils getPickerUtils() {
        if (pickerUtils == null) {
            pickerUtils = new TimePickerUtils();
        }
        return pickerUtils;
    }

    private void initWidget(boolean isStartTime) {
        if (!initWidget) {
            mBtnReback = resLayOutView.findViewById(R.id.btn_reback);
            mBtnSure = resLayOutView.findViewById(R.id.btn_sure);
            mTxtAddHout = resLayOutView.findViewById(R.id.txt_add_hour);
            mTxtMinusHour = resLayOutView.findViewById(R.id.txt_minus_hour);
            mTxtAddMinute = resLayOutView.findViewById(R.id.txt_add_minute);
            mTxtMinusMinute = resLayOutView.findViewById(R.id.txt_minus_minute);
            mTxtTitle = resLayOutView.findViewById(R.id.tv_dialog_title);
            mTxtSumTime = resLayOutView.findViewById(R.id.txt_sum_time);
            mBtnReback.setOnClickListener(this);
            mBtnSure.setOnClickListener(this);
            mTxtAddHout.setOnClickListener(this);
            mTxtMinusHour.setOnClickListener(this);
            mTxtAddMinute.setOnClickListener(this);
            mTxtMinusMinute.setOnClickListener(this);
            initWidget = true;
        }
        mTxtTitle.setText(isStartTime ? "开始时间设定" : "结束时间设定");

        if (isStartTime) {
            secondTime = TextUtils.isEmpty(secondTime) ? DateUtils.getCurrentLongTime() : secondTime;
            mDifferenceTime = DateCalcUtils.getTimeDifference(dateString, secondTime);
        } else {
            mDifferenceTime = DateCalcUtils.getTimeDifference(secondTime, dateString);
        }
        SpannableString string = new SpannableString("巡视任务总耗时：" + mDifferenceTime);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#ff000000"));
        string.setSpan(colorSpan, 8, string.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mTxtSumTime.setText(string);
    }


    public void showDialog(Activity activity, String currentTime, String secondTime, boolean isStartTime) {
        this.time = currentTime;
        if (TextUtils.isEmpty(time)) {
            time = DateUtils.getCurrentLongTime();
        }
        this.secondTime = secondTime;
        this.isStartTime = isStartTime;
        if (pvCustomTime == null) {
           mActivity = activity;
            initCustomTimePicker();
            initWidget = false;
        } else {
            Calendar selectedDate = DateCalcUtils.getCalendar(time, DateUtils.yyyy_MM_dd_HH_mm_ss);
            dateString = getTime(selectedDate.getTime());
            pvCustomTime.setDate(selectedDate);
        }
        pvCustomTime.show();
        initWidget(isStartTime);
    }


    private void initCustomTimePicker() {
        Calendar selectedDate = null;//系统当前时间
        if (!TextUtils.isEmpty(time)) {
            selectedDate = DateCalcUtils.getCalendar(time, DateUtils.yyyy_MM_dd_HH_mm_ss);
        } else {
            selectedDate = Calendar.getInstance();
        }
        dateString = getTime(selectedDate.getTime());
        Calendar startDate = Calendar.getInstance();
        startDate.set(2009, 1, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2099, 12, 31);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(mActivity, (date, v) -> {//选中事件回调
            dateString = TimePickerUtils.this.getTime(date);
        }).setTextColorCenter(Color.BLACK)
                .isDialog(true)
                .isCyclic(true)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.xs_dialog_time_picker, v -> resLayOutView = v)
                .setContentSize(20)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(0f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .setDividerType(WheelView.DividerType.FILL)
                .setOutSideCancelable(false)
                .setInsideCancelable(false)
                .setColors(new Integer[]{0xFF24AD9D, 0xFF24AD9D, 0xFF24AD9D, 0xfff, 0xfff, 0xfff})
                .build();

    }


    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_sure) {
            if (null != mMissListener) {
                pvCustomTime.returnData();
                mMissListener.returnDateTime(dateString, isStartTime);
            }

        } else if (i == R.id.btn_reback) {
            Calendar selectedDate = DateCalcUtils.getCalendar(time, DateUtils.yyyy_MM_dd_HH_mm_ss);
            pvCustomTime.setDate(selectedDate);
            pvCustomTime.returnData();

        } else if (i == R.id.txt_add_hour) {
            Calendar date = DateCalcUtils.getAfterDay(dateString, 1, DateUtils.yyyy_MM_dd_HH_mm_ss);
            pvCustomTime.setDate(date);
            pvCustomTime.returnData();

        } else if (i == R.id.txt_minus_hour) {
            Calendar date1 = DateCalcUtils.getAfterDay(dateString, -1, DateUtils.yyyy_MM_dd_HH_mm_ss);
            pvCustomTime.setDate(date1);
            pvCustomTime.returnData();

        } else if (i == R.id.txt_minus_minute) {
            Calendar date2 = DateCalcUtils.getAfterMinute(dateString, -10, DateUtils.yyyy_MM_dd_HH_mm_ss);
            pvCustomTime.setDate(date2);
            pvCustomTime.returnData();

        } else if (i == R.id.txt_add_minute) {
            Calendar date3 = DateCalcUtils.getAfterMinute(dateString, 10, DateUtils.yyyy_MM_dd_HH_mm_ss);
            pvCustomTime.setDate(date3);
            pvCustomTime.returnData();

        } else {
        }
    }

}
