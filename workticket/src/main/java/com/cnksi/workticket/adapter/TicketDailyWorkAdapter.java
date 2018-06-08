package com.cnksi.workticket.adapter;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.workticket.R;
import com.cnksi.workticket.bean.TicketTimeZone;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.enum_ticket.TicketStatusEnum;

import java.util.List;

/**
 * Created by Mr.K on 2018/5/9.
 */

public class TicketDailyWorkAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public TicketDailyWorkAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.ticket_date_daily_record);
        addItemType(TYPE_LEVEL_1, R.layout.ticket_date_daily_record_child_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                TicketTimeZone zone = (TicketTimeZone) item;
                helper.setText(R.id.time_zone, zone.timeZone);
                break;
            case TYPE_LEVEL_1:
                WorkTicketOrder order = (WorkTicketOrder) item;
                helper.setText(R.id.txt_bdz_name, order.bdzName);
                helper.setText(R.id.txt_dept_name, "工作单位：" + order.workUnit);
                List<String> ticketTypes = StringUtils.stringToList(order.ticketType, ",");
                StringBuilder builder = new StringBuilder();
                for (String str : ticketTypes) {
                    builder.append(TicketStatusEnum.getValue(str)).append(" ");
                }
                helper.setText(R.id.txt_work_type_name, "工作类型：" + order.workType + "类工作" + (builder.toString()));
                helper.setText(R.id.txt_charge_name, "工作负责人：" + order.chargePerson);
                helper.setText(R.id.txt_phone_name, "联系方式：" + order.phoneNum);
                break;
            default:
                break;
        }

    }
//    public TicketDailyWorkAdapter(Context context) {
//        super(context);
//    }

//    @Override
//    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
//        return new TicketViewHolder(mInflater.inflate(R.layout.ticket_date_daily_record_child_item, parent, false));
//    }
//
//    @Override
//    protected void onBindViewHolder(RecyclerView.ViewHolder holder, WorkTicketOrder item, int position) {
//        TicketViewHolder ticketViewHolder = (TicketViewHolder) holder;
//        ticketViewHolder.txtBdzName.setText(item.bdzName);
//        ticketViewHolder.txtDeptName.setText("工作单位：" + item.deptId);
//        ticketViewHolder.txtWorkTypeName.setText("工作类型：" + item.workType);
//        ticketViewHolder.txtChargeName.setText("工作负责人：" + item.chargePerson);
//        ticketViewHolder.txtPhone.setText("联系方式：" + item.phoneNum);
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
//
//    }


//    private static class TicketViewHolder extends RecyclerView.ViewHolder {
//        private TextView txtBdzName,
//                txtDeptName, txtWorkTypeName, txtChargeName, txtPhone;
//
//        private TicketViewHolder(View itemView) {
//            super(itemView);
//            txtBdzName = (TextView) itemView.findViewById(R.id.txt_bdz_name);
//            txtDeptName = (TextView) itemView.findViewById(R.id.txt_dept_name);
//            txtWorkTypeName = (TextView) itemView.findViewById(R.id.txt_work_type_name);
//            txtChargeName = (TextView) itemView.findViewById(R.id.txt_charge_name);
//            txtPhone = (TextView) itemView.findViewById(R.id.txt_phone_name);
//
//        }
//    }
}
