package com.cnksi.workticket.fragment;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.workticket.CalendarUtil;
import com.cnksi.workticket.R;
import com.cnksi.workticket.databinding.TicketFragmentDailyWorkBinding;
import com.haibin.calendarview.Calendar;

/**
 * Created by Mr.K on 2018/5/8.
 */

public class TicketDaliyWorkFragment extends BaseCoreFragment implements CalendarUtil.DateSelectListener {
    TicketFragmentDailyWorkBinding binding;

    @Override
    public int getFragmentLayout() {
        return R.layout.ticket_fragment_daily_work;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding = (TicketFragmentDailyWorkBinding) fragmentDataBinding;
        new CalendarUtil(getActivity()).addCanlendarView(binding.dateContainer, 0);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDateSelect(Calendar calendar, boolean isClick) {
        ToastUtils.showMessage(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
    }
}
