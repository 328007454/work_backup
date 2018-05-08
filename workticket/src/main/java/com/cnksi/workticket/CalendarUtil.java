package com.cnksi.workticket;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.workticket.databinding.TicketDateTitleBinding;
import com.cnksi.workticket.view.TicketDateSelectDecorator;
import com.cnksi.workticket.view.recyclerview.GroupRecyclerView;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;

/**
 * Created by Mr.K on 2018/4/28.
 */

public class CalendarUtil implements CalendarView.OnDateSelectedListener, CalendarView.OnYearChangeListener, CalendarView.OnMonthChangeListener {


    private Activity activity;
    private DateSelectListener dateSelectListener;

    private View view;
    private TextView txtDate;
    private ImageView imgDateRight;
    private ImageView imgDateLeft;
    private TextView txtDateToday;
    private CalendarLayout calendarLayout;
    private CalendarView calendarView;
    private GroupRecyclerView groupRecyclerView;


    public void setOnDateSelectListener(DateSelectListener dateSelectListener) {
        this.dateSelectListener = dateSelectListener;
    }


    public interface DateSelectListener {
        void onDateSelect(com.haibin.calendarview.Calendar calendar, boolean isClick);
    }

    public CalendarUtil(Activity activity) {
        this.activity = activity;
    }

    public CalendarUtil addCanlendarView(ViewGroup group, int position) {
        view = LayoutInflater.from(activity).inflate(R.layout.ticket_work_date, group, false);
        txtDate = view.findViewById(R.id.txt_date);
        imgDateLeft = view.findViewById(R.id.date_right);
        imgDateRight = view.findViewById(R.id.date_left);
        txtDateToday = view.findViewById(R.id.date_today);
        calendarLayout = view.findViewById(R.id.calendarLayout);
        calendarView = view.findViewById(R.id.calendarView);
        groupRecyclerView = view.findViewById(R.id.recyclerView);
        calendarView.setOnDateSelectedListener(this);
        calendarView.setOnMonthChangeListener(this);
        calendarView.setOnYearChangeListener(this);

        imgDateLeft.setOnClickListener(v -> {
            calendarView.scrollToPre(true);
        });

        imgDateRight.setOnClickListener(v -> {
            calendarView.scrollToNext(true);
        });

        txtDateToday.setOnClickListener(v -> {
            calendarView.scrollToCalendar(calendarView.getCurYear(), calendarView.getCurMonth(), calendarView.getCurDay());
        });
        txtDate.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");

        if (group instanceof LinearLayout) {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);
            ll.topMargin = 45;
            group.addView(view);
        } else {

        }
        initCalendarStyle();
        return this;
    }

    private void initCalendarStyle() {


    }

    public CalendarUtil addCalendarView(ViewGroup view) {
        addCanlendarView(view, 0);
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
}
