package com.cnksi.workticket.fragment;

import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;
import com.cnksi.workticket.adapter.TicketWorkRecordAdapter;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.databinding.TicketDialogTipsBinding;
import com.cnksi.workticket.databinding.TicketFragmentWorkRecordBinding;
import com.cnksi.workticket.db.WorkTicketDbManager;
import com.cnksi.workticket.db.WorkTicketOrderService;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.workticket.util.DialogUtil;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K  on 2018/5/8.
 * @decrption 工作表记录展示
 */

public class TicketWorkRecordFragment extends BaseCoreFragment implements TicketWorkRecordAdapter.OnItemClickListener {
    TicketFragmentWorkRecordBinding binding;
    private TicketWorkRecordAdapter recordAdapter;
    private List<WorkTicketOrder> orders = new ArrayList<>();
    private List<WorkTicketOrder> historyOrders;
    private List<WorkTicketOrder> futureOrders;
    private boolean isFirstLoad = true;
    private Dialog cancelDialog;
    private TicketDialogTipsBinding tipsBinding;

    @Override
    public int getFragmentLayout() {
        return R.layout.ticket_fragment_work_record;
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding = (TicketFragmentWorkRecordBinding) fragmentDataBinding;
        initData();
        initOnClick();
        initCancelTicketDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
        } else {
            initData();
        }
    }

    private void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            futureOrders = WorkTicketOrderService.getInstance().getFutureWorkOverCurrentTime(Config.deptID, DateUtils.getCurrentLongTime());
            historyOrders = WorkTicketOrderService.getInstance().getHistoryWorkOverCurrentTime(Config.deptID, DateUtils.getCurrentLongTime());
            if (futureOrders != null && !futureOrders.isEmpty()) {
                orders.clear();
                orders.addAll(futureOrders);
            }
            getActivity().runOnUiThread(() -> {
                recordAdapter = new TicketWorkRecordAdapter(R.layout.ticket_adapter_record_history, orders);
                recordAdapter.setShowStyle("future");
                recordAdapter.setOnItemClickListener(this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                binding.recWork.setLayoutManager(linearLayoutManager);
                binding.recWork.setAdapter(recordAdapter);
            });
        });
    }

    @Override
    protected void lazyLoad() {
    }

    private String styleStatus = "future";

    private void initOnClick() {
        binding.future.setOnClickListener(v -> {
            orders.clear();
            if (futureOrders != null && !futureOrders.isEmpty()) {
                orders.addAll(futureOrders);
            }
            styleStatus = "future";
            recordAdapter.setShowStyle(styleStatus);
            recordAdapter.notifyDataSetChanged();
            binding.future.setTextColor(ContextCompat.getColor(getContext(), R.color.core_bg_white_ffffff));
            binding.future.setBackgroundResource(R.drawable.ticket_05c8b8_10px_corner_shape);

            binding.history.setTextColor(ContextCompat.getColor(getContext(), R.color.ticket_color_c0bab9));
            binding.history.setBackgroundResource(R.drawable.ticket_c0bab9_10px_line_shape);

        });

        binding.history.setOnClickListener(v -> {
            orders.clear();
            if (historyOrders != null && !historyOrders.isEmpty()) {
                orders.addAll(historyOrders);
            }
            styleStatus = "history";
            recordAdapter.setShowStyle(styleStatus);
            recordAdapter.notifyDataSetChanged();

            binding.history.setTextColor(ContextCompat.getColor(getContext(), R.color.core_bg_white_ffffff));
            binding.history.setBackgroundResource(R.drawable.ticket_05c8b8_10px_corner_shape);

            binding.future.setTextColor(ContextCompat.getColor(getContext(), R.color.ticket_color_c0bab9));
            binding.future.setBackgroundResource(R.drawable.ticket_c0bab9_10px_line_shape);
        });
    }

    @Override
    public void itemClick(View v, Object item, int position) {
        WorkTicketOrder order = (WorkTicketOrder) item;
        String date = order.workDate.substring(0, order.workDate.length() - 8);
        tipsBinding.txtTips.setText("是否取消" + date + order.workVal + "运维班检修工作？");

        if (cancelDialog != null) {
            cancelDialog.show();
            tipsBinding.yes.setOnClickListener(view -> {
                if (TextUtils.equals("future", styleStatus)) {
                    futureOrders.remove(item);
                    orders.clear();
                    orders.addAll(futureOrders);
                } else if (TextUtils.equals("history", styleStatus)) {
                    historyOrders.remove(item);
                    orders.clear();
                    orders.addAll(historyOrders);
                }
                recordAdapter.notifyDataSetChanged();
                cancelDialog.dismiss();
                order.dlt = "1";
                try {
                    WorkTicketDbManager.getInstance().getTicketManager().saveOrUpdate(order);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    private void initCancelTicketDialog() {
        tipsBinding = TicketDialogTipsBinding.inflate(LayoutInflater.from(getContext()));
        cancelDialog = new DialogUtil().createDialog(getContext(), tipsBinding.getRoot(), ScreenUtils.getScreenWidth(getContext()) * 7 / 9, ViewGroup.LayoutParams.WRAP_CONTENT);
        tipsBinding.no.setOnClickListener(v -> cancelDialog.dismiss());
    }
}
