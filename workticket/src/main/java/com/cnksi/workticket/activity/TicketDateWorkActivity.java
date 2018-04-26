package com.cnksi.workticket.activity;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.cnksi.workticket.R;
import com.cnksi.workticket.base.TicketBaseActivity;
import com.cnksi.workticket.databinding.ActivityTicketDateWorkBinding;
import com.cnksi.workticket.view.TicketDateSelectDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;


public class TicketDateWorkActivity extends TicketBaseActivity implements OnDateSelectedListener {

    ActivityTicketDateWorkBinding binding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date_work;
    }

    @Override
    public void initUI() {
        binding = (ActivityTicketDateWorkBinding) rootDataBinding;
        binding.calendar.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        binding.calendar.setDateSelected(Calendar.getInstance(),true);
        binding.calendar.setTileHeightDp(38);
        binding.calendar.setTopbarVisible(true);
        binding.calendar.setOnDateChangedListener(this);
        binding.calendar.addDecorator(new TicketDateSelectDecorator(this));
        binding.calendar.setWeekDayTextAppearance(R.style.CustomTextAppearance);
        binding.calendar.setHeaderTextAppearance(R.style.CustomTextAppearance);
        initClick();
    }

    @Override
    public void initData() {

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (selected) {

            widget.invalidateDecorators();
        }
    }

    public void initClick() {

        binding.back.setOnClickListener(view -> {
            binding.calendar.goToPrevious();
//            Log.i("Tag", binding.calendar.getCurrentDate().getYear() + "--" + binding.calendar.getCurrentDate().getMonth() + "");
        });
        binding.go.setOnClickListener(view -> {
//            Log.i("Tag", binding.calendar.getCurrentDate().getYear() + "--" + binding.calendar.getCurrentDate().getMonth() + "");
            binding.calendar.goToNext();
        });

        binding.expand.setOnClickListener(view -> {
            if (model == CalendarMode.WEEKS.name()) {
                binding.calendar.state().edit().setCalendarDisplayMode(CalendarMode.MONTHS).commit();
                model = CalendarMode.MONTHS.name();
            } else {
                model = CalendarMode.WEEKS.name();
                binding.calendar.state().edit().setCalendarDisplayMode(CalendarMode.WEEKS).commit();
            }
        });
        binding.calendar.setOnMonthChangedListener((widget, date) -> {

            Log.i("Tag",date.getYear()+"--"+date.getMonth()+"--"+date.getDay()+"month");

        });

        binding.calendar.setOnDateChangedListener((widget, date, selected) -> {
            Log.i("Tag",date.getYear()+"--"+date.getMonth()+"--"+date.getMonth()+(selected==true?"true":"false")+"date");
        });

        binding.calendar.setOnRangeSelectedListener((widget, dates) -> {
            Log.i("Tag",dates.get(0).getYear()+dates.get(0).getMonth()+dates.get(0).getMonth()+"range");
        });
    }
    String model=CalendarMode.WEEKS.name();
}
