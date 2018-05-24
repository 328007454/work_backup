package com.cnksi.workticket.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.workticket.CalendarUtil;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;
import com.cnksi.workticket.adapter.TicketDailyWorkAdapter;
import com.cnksi.workticket.bean.TicketTimeZone;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.databinding.TicketFragmentDailyWorkBinding;
import com.cnksi.workticket.db.WorkTicketOrderService;
import com.cnksi.workticket.enum_ticket.TicketTimeEnum;
import com.haibin.calendarview.Calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Mr.K on 2018/5/8.
 */

public class TicketDailyWorkFragment extends BaseCoreFragment implements CalendarUtil.DateSelectListener {
    TicketFragmentDailyWorkBinding binding;
    private List<WorkTicketOrder> futureOrders = new ArrayList<>();
    private CalendarUtil calendarUtil;
    private String currentDate;
    private String maxCurrentDate;
    private RecyclerView groupRecyclerView;
    LinkedHashMap<String, List<WorkTicketOrder>> map = new LinkedHashMap<>();
    List<String> titles = new ArrayList<>();
    List<MultiItemEntity> entities = new ArrayList<>();
    private TicketDailyWorkAdapter dailyWorkAdapter;

    private boolean isFirstLoad = true;

    @Override
    public int getFragmentLayout() {
        return R.layout.ticket_fragment_daily_work;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding = (TicketFragmentDailyWorkBinding) fragmentDataBinding;
        calendarUtil = new CalendarUtil(getActivity()).addCanlendarView(binding.dateContainer, 0);
        calendarUtil.setOnDateSelectListener(this);
        groupRecyclerView = calendarUtil.getGroupRecyclerView();
        currentDate = calendarUtil.getCalendarView().getCurYear() + "-" + calendarUtil.getCalendarView().getCurMonth() + "-" + calendarUtil.getCalendarView().getCurDay() + " 00:00:00";
        maxCurrentDate = calendarUtil.getCalendarView().getCurYear() + "-" + calendarUtil.getCalendarView().getCurMonth() + "-" + calendarUtil.getCalendarView().getCurDay() + " 23:59:59";
        currentDate = DateUtils.getFormatterTime(currentDate, "yyyy-MM-dd HH:MM:SS");
        maxCurrentDate = DateUtils.getFormatterTime(maxCurrentDate, "yyyy-MM-dd HH:MM:SS");
        titles.add(TicketTimeEnum.region_10to11.value);
        titles.add(TicketTimeEnum.region_11to12.value);
        titles.add(TicketTimeEnum.region_14to15.value);
        titles.add(TicketTimeEnum.region_15to16.value);
        titles.add(TicketTimeEnum.region_16to17.value);
        initData();

    }

    private void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            futureOrders = WorkTicketOrderService.getInstance().getCurrentDateWork(Config.userAccount, Config.deptID, currentDate, maxCurrentDate);
            map.clear();
            entities.clear();
            if (futureOrders == null) {
                return;
            }
            HashMap<String, TicketTimeZone> zoneHashMap = new HashMap<>();
            for (WorkTicketOrder order : futureOrders) {
                TicketTimeZone timeZone = null;
                if (zoneHashMap.get(order.workVal) != null) {
                    timeZone = zoneHashMap.get(order.workVal);
                } else {
                    timeZone = new TicketTimeZone(order.workVal);
                    zoneHashMap.put(order.workVal, timeZone);
                    entities.add(timeZone);
                }


                if (TextUtils.equals(order.workVal, TicketTimeEnum.region_10to11.value)) {
                    timeZone.addSubItem(order);
                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_11to12.value)) {

                    timeZone.addSubItem(order);
                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_14to15.value)) {

                    timeZone.addSubItem(order);
                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_15to16.value)) {
                    timeZone.addSubItem(order);

                } else if (TextUtils.equals(order.workVal, TicketTimeEnum.region_16to17.value)) {
                    timeZone.addSubItem(order);
                }
            }


            TicketDailyWorkFragment.this.getActivity().runOnUiThread(() -> {
                if (dailyWorkAdapter == null) {
                    dailyWorkAdapter = new TicketDailyWorkAdapter(entities);
                    dailyWorkAdapter.expandAll();
                    groupRecyclerView.setLayoutManager(new LinearLayoutManager(TicketDailyWorkFragment.this.getContext()));
                    groupRecyclerView.setAdapter(dailyWorkAdapter);
                } else {
                    dailyWorkAdapter.expandAll();
                    dailyWorkAdapter.notifyDataSetChanged();

                }
            });
        });
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = !isFirstLoad;
        } else {
            initData();
        }
    }

    @Override
    public void onDateSelect(Calendar calendar, boolean isClick) {
        ToastUtils.showMessage(calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay());
        currentDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay() + " 00:00:00";
        maxCurrentDate = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay() + " 23:59:59";
        currentDate = DateUtils.getFormatterTime(currentDate, "yyyy-MM-dd HH:MM:SS");
        maxCurrentDate = DateUtils.getFormatterTime(maxCurrentDate, "yyyy-MM-dd HH:MM:SS");
        initData();
    }
}
