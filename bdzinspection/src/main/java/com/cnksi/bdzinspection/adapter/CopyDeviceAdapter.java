package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.listener.ItemClickListener;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class CopyDeviceAdapter extends BaseAdapter<DbModel> {

    private int currentSelectedPosition;

    private ItemClickListener<DbModel> itemClickListener;

    private HashSet<String> copyDeviceIds = new HashSet<>();

    public void setCopyDeviceModel(HashSet<String> deviceIds) {
        copyDeviceIds = deviceIds;
    }

    public CopyDeviceAdapter(Context context, Collection<DbModel> data, int layoutId) {
        super(context, data, layoutId);
    }

    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.currentSelectedPosition = currentSelectedPosition;
        this.notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener<DbModel> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewHolder holder, final DbModel item, final int position) {
        TextView txtDevice = holder.getView(R.id.tv_device_name);
        ImageView ibCopy = holder.getView(R.id.ibt_copy_pen);
        holder.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);
        txtDevice.setText(item.getString("name_short"));
        txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
        RelativeLayout relativeLayout = holder.getView(R.id.rl_device_container);
        relativeLayout.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(105));
        relativeLayout.setMinimumWidth(AutoUtils.getPercentWidthSizeBigger(270));
        if (currentSelectedPosition == position) {
            txtDevice.setTextColor(context.getResources().getColor(R.color.xs_white));
//			txtDevice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_white_unfinish, 0, 0, 0);
            ibCopy.setImageResource(R.drawable.ic_white_unfinish);
            holder.getView(R.id.rl_device_container)
                    .setBackgroundResource(R.drawable.xs_copy_all_value_item_selected_background);
        } else {
            // 有一项抄录变绿
            if (copyDeviceIds.contains(item.getString("deviceid"))) {
                ibCopy.setImageResource(R.drawable.xs_ic_black_finish);
                txtDevice.setTextColor(context.getResources().getColor(R.color.xs_global_text_color));
                relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.xs_device_black_border_background_selector));
            } else {
                relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.xs_device_green_border_background_selector));
                txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
                ibCopy.setImageResource(R.drawable.ic_green_unfinish);
                holder.getView(R.id.rl_device_container)
                        .setBackgroundResource(R.drawable.xs_copy_all_value_item_unselected_background_selector);
            }
        }


        holder.getRootView().setOnClickListener(v -> itemClickListener.onClick(v, item, position));
    }

    public void pre() {
        if (currentSelectedPosition > 0) {
            itemClickListener.onClick(null, new ArrayList<>(data).get(currentSelectedPosition - 1),
                    currentSelectedPosition - 1);
        }
    }

    public void next() {
        if (currentSelectedPosition < data.size() - 1) {
            itemClickListener.onClick(null, new ArrayList<>(data).get(currentSelectedPosition + 1),
                    currentSelectedPosition + 1);
        }
    }

    public boolean isLast() {
        if (data.isEmpty()) {
            return true;
        }
        return currentSelectedPosition == data.size() - 1;
    }

    public boolean isFirst() {
        return currentSelectedPosition == 0;
    }

}
