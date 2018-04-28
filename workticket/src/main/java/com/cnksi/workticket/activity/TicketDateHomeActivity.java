package com.cnksi.workticket.activity;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.workticket.R;
import com.cnksi.workticket.base.*;
import com.cnksi.workticket.databinding.ActivityTicketDateWorkBinding;
import com.cnksi.workticket.enum_ticket.TicketEnum;
import com.cnksi.workticket.view.TicketDateSelectDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;

/**
 * Created by Mr.K on 2018/4/28.
 */

public class TicketDateHomeActivity extends TicketBaseActivity implements OnDateSelectedListener {

    ActivityTicketDateWorkBinding binding;

    private String[] titleArrays = new String[2];

    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date_work;
    }

    @Override
    public void initUI() {
        binding = (ActivityTicketDateWorkBinding) rootDataBinding;
        titleArrays[0] = TicketEnum.GZJL.value;
        titleArrays[1] = TicketEnum.GZRZ.value;
        binding.tabStrip.setTitleArray(titleArrays);
        setTabStripStyle(binding.tabStrip);
        binding.tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.calendar.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        binding.calendar.setDateSelected(Calendar.getInstance(), true);
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

    String model = CalendarMode.WEEKS.name();

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

            Log.i("Tag", date.getYear() + "--" + date.getMonth() + "--" + date.getDay() + "month");

        });

        binding.calendar.setOnDateChangedListener((widget, date, selected) -> {
            Log.i("Tag", date.getYear() + "--" + date.getMonth() + "--" + date.getMonth() + (selected == true ? "true" : "false") + "date");
        });

        binding.calendar.setOnRangeSelectedListener((widget, dates) -> {
            Log.i("Tag", dates.get(0).getYear() + dates.get(0).getMonth() + dates.get(0).getMonth() + "range");
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (selected) {
            widget.invalidateDecorators();
        }
    }
}
