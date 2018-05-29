package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.common.listener.ItemClickOrLongClickListener;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by lyndon on 2016/9/5.
 */
public class CopyDeviceAdapter extends BaseAdapter<DbModel> {

    private int currentSelectedPosition;

    private List<String> copyDeviceList = new ArrayList<>();

    private ItemClickOrLongClickListener<DbModel> itemClickListener;


    public CopyDeviceAdapter(Context context, Collection<DbModel> data, int layoutId) {
        super(context, data, layoutId);
    }

    public void setCopyDeviceModel(List<DbModel> copyDeviceModel) {
        copyDeviceList.clear();
        if (null != copyDeviceModel && !copyDeviceModel.isEmpty()) {
            for (DbModel dbModel : copyDeviceModel) {
                copyDeviceList.add(dbModel.getString("deviceid"));
            }
        }
    }

    public void setItemClickListener(ItemClickOrLongClickListener<DbModel> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewHolder holder, final DbModel item, final int position) {
        TextView txtDevice = holder.getView(R.id.tv_device_name);
        ImageView ibCopy = holder.getView(R.id.ibt_copy_pen);
        RelativeLayout deviceContainer = holder.getView(R.id.rl_device_container);
        txtDevice.setText(item.getString("name"));
        if (currentSelectedPosition == position) {
            deviceContainer.setBackgroundResource(R.drawable.copy_all_value_item_selected_background);
            txtDevice.setTextColor(ContextCompat.getColor(context, android.R.color.white));
//            txtDevice.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_white_unfinish, 0, 0, 0);
            ibCopy.setImageResource(R.drawable.ic_white_unfinish);
        } else {
            deviceContainer
                    .setBackgroundResource(R.drawable.copy_all_value_item_unselected_background_selector);
            txtDevice.setTextColor(ContextCompat.getColor(context, R.color.green_color));
//            txtDevice.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_green_unfinish, 0, 0, 0);
            ibCopy.setImageResource(R.drawable.ic_green_unfinish);
        }

        // 有一项抄录变绿
        if (copyDeviceList.contains(item.getString("deviceid"))) {
//            txtDevice.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_green_finish, 0, 0, 0);// 显示抄录数据笔
            ibCopy.setImageResource(R.drawable.ic_green_finish);
        }

        holder.getRootView().setOnClickListener(v -> {
            if(isClickAble){
                itemClickListener.onClick(v, item, position);
            }
        });
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

    public void setCurrentSelectedPosition(int position) {
        currentSelectedPosition = position;
        notifyDataSetChanged();
    }

    private boolean isClickAble = true;

    public void setClickAble(boolean isClickAble) {
        this.isClickAble = isClickAble;
    }
}