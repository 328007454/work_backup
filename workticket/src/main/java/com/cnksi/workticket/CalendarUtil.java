package com.cnksi.workticket;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haibin.calendarview.CalendarView;

/**
 * @author Mr.K  on 2018/4/28.
 * @decrption 日历工具
 */

public class CalendarUtil implements CalendarView.OnDateSelectedListener, CalendarView.OnYearChangeListener, CalendarView.OnMonthChangeListener {


    private Activity activity;
    private DateSelectListener dateSelectListener;

    private TextView txtDate;
    private CalendarView calendarView;
    private RecyclerView groupRecyclerView;


    public void setOnDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
    }


    public interface DateSelectListener {
        /**
         * 日历选中接口回i掉
         *
         * @param calendar 当前日历
         * @param isClick  是否被点击
         */
        void onDateSelect(com.haibin.calendarview.Calendar calendar, boolean isClick);
    }

    public CalendarUtil(Activity activity) {
        this.activity = activity;
    }

    public CalendarUtil addCalendarView(ViewGroup group, int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.ticket_work_date, group, false);
        txtDate = view.findViewById(R.id.txt_date);
        ImageView imgDateRight = view.findViewById(R.id.date_right);
        ImageView imgDateLeft = view.findViewById(R.id.date_left);
        TextView txtDateToday = view.findViewById(R.id.date_today);
        calendarView = view.findViewById(R.id.calendarView);
        groupRecyclerView = view.findViewById(R.id.recyclerView);
        calendarView.setOnDateSelectedListener(this);
        calendarView.setOnMonthChangeListener(this);
        calendarView.setOnYearChangeListener(this);

        imgDateLeft.setOnClickListener(v -> calendarView.scrollToPre(true));

        imgDateRight.setOnClickListener(v -> calendarView.scrollToNext(true));

        txtDateToday.setOnClickListener(v -> {
            calendarView.scrollToCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay());
        });
        txtDate.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");

        if (group instanceof LinearLayout) {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
            ll.topMargin = 45;
            group.addView(view);
        }
        return this;
    }

    @Override
    public void onDateSelected(com.haibin.calendarview.Calendar calendar, boolean isClick) {
        if (null != dateSelectListener) {
            dateSelectListener.onDateSelect(calendar, isClick);
        }
    }

    @Override
    public void onYearChange(int year) {

    }

    @Override
    public void onMonthChange(int year, int month) {
        txtDate.setText(year + "年" + month + "月");
    }

    public CalendarView getCalendarView() {


        return calendarView;
    }


    public RecyclerView getGroupRecyclerView() {
        return groupRecyclerView;
    }
}
