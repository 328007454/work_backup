package com.cnksi.workticket.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.cnksi.workticket.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by Mr.K on 2018/4/23.
 */

public class TicketDateSelectDecorator implements DayViewDecorator {
    private Drawable drawable;

    public TicketDateSelectDecorator(Activity context){
        drawable = context.getResources().getDrawable(R.drawable.date_select_selector);

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
