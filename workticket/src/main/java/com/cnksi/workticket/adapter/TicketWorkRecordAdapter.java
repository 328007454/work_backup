package com.cnksi.workticket.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.workticket.R;
import com.cnksi.workticket.bean.WorkTicketOrder;

import java.util.List;

/**
 * Created by Mr.K on 2018/5/8.
 */

public class TicketWorkRecordAdapter extends BaseQuickAdapter<WorkTicketOrder, BaseViewHolder> {

    private String style;

    public void setShowStyle(String style) {
        this.style = style;
    }

    public TicketWorkRecordAdapter(int layoutResId, @Nullable List<WorkTicketOrder> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkTicketOrder item) {
        String date = item.workDate.substring(0, item.workDate.length() - 8);
        TextView status = helper.getView(R.id.txt_status_name);
        helper.setText(R.id.txt_date, date + item.workVal);
        helper.setText(R.id.txt_bdz_name, "变电站：" + item.bdzName);
        helper.setText(R.id.txt_people_name, "负责人：" + item.chargePerson + "  " + item.phoneNum);
        helper.setText(R.id.txt_content_name, "工作内容：" + item.content);

        if (TextUtils.equals("future", style)) {
            status.setText("取消工作");
            status.setTextColor(mContext.getResources().getColor(R.color.core_bg_white_ffffff));
            status.setBackgroundResource(R.drawable.ticket_ff7575_10px_corner_shape);

        } else if (TextUtils.equals("history", style)) {
            status.setText("已完成");
            status.setTextColor(mContext.getResources().getColor(R.color.ticket_color_01ce7f));
            status.setBackgroundColor(mContext.getResources().getColor(R.color.core_bg_white_ffffff));
        }

        status.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.itemClick(v, item, helper.getAdapterPosition());
            }
        });
    }


    public interface OnItemClickListener {
        void itemClick(View v, Object item, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
