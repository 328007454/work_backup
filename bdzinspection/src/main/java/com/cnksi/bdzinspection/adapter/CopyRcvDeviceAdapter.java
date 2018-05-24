package com.cnksi.bdzinspection.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.HashSet;
import java.util.List;

public class CopyRcvDeviceAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {

    private int currentSelectedPosition;

    private ItemClickListener<DbModel> itemClickListener;

    private HashSet<String> copyDeviceIds = new HashSet<>();

    public CopyRcvDeviceAdapter(List<DbModel> data, int layoutId) {
        super(layoutId, data);
    }

    public void setCopyDeviceModel(HashSet<String> deviceIds) {
        copyDeviceIds = deviceIds;
    }

    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.currentSelectedPosition = currentSelectedPosition;
        getRecyclerView().scrollToPosition(currentSelectedPosition);
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener<DbModel> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void pre() {
        if (currentSelectedPosition > 0) {
            itemClickListener.onItemClick(null, getData().get(--currentSelectedPosition),
                    currentSelectedPosition);
        }
    }

    public void next() {
        if (currentSelectedPosition < getItemCount() - 1) {
            itemClickListener.onItemClick(null, getData().get(++currentSelectedPosition), currentSelectedPosition);
        }
    }

    public boolean isLast() {
        if (getItemCount() == 0) {
            return true;
        }
        return currentSelectedPosition == getItemCount() - 1;
    }

    public boolean isFirst() {
        return currentSelectedPosition == 0;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final DbModel item) {
        final int position = helper.getAdapterPosition();
        TextView txtDevice = helper.getView(R.id.tv_device_name);
        ImageView ibCopy = helper.getView(R.id.ibt_copy_pen);
        helper.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);
        txtDevice.setText(item.getString("name"));
        txtDevice.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
        RelativeLayout relativeLayout = helper.getView(R.id.rl_device_container);
        relativeLayout.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(105));
        relativeLayout.setMinimumWidth(AutoUtils.getPercentWidthSizeBigger(270));
        if (currentSelectedPosition == position) {
            txtDevice.setTextColor(mContext.getResources().getColor(R.color.xs_white));
//			txtDevice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_white_unfinish, 0, 0, 0);
            ibCopy.setImageResource(R.drawable.xs_ic_white_unfinish);
            helper.getView(R.id.rl_device_container)
                    .setBackgroundResource(R.drawable.xs_copy_all_value_item_selected_background);
        } else {
            // 有一项抄录变绿
            if (copyDeviceIds.contains(item.getString("deviceid"))) {
                ibCopy.setImageResource(R.drawable.xs_ic_black_finish);
                txtDevice.setTextColor(mContext.getResources().getColor(R.color.xs_global_text_color));
                relativeLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.xs_device_black_border_background_selector));
            } else {
                relativeLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.xs_device_green_border_background_selector));
                txtDevice.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
                ibCopy.setImageResource(R.drawable.xs_ic_green_unfinish);
                helper.getView(R.id.rl_device_container)
                        .setBackgroundResource(R.drawable.xs_copy_all_value_item_unselected_background_selector);
            }
        }


        helper.itemView.setOnClickListener(v -> {
            itemClickListener.onItemClick(v, item, position);
            currentSelectedPosition = position;
        });
    }


}
