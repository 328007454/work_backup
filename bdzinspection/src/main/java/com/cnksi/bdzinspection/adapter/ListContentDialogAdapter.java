package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.common.model.Battery;
import com.cnksi.common.model.DevicePart;
import com.cnksi.common.model.Spacing;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class ListContentDialogAdapter extends SimpleBaseAdapter {

    public static final String LIST_STRING_TYPE = "list_string_type";
    public static final String LIST_DEVICEPART_TYPPE = "list_devicepart_type";
    public static final String LIST_BATTERY_TYPE = "list_battery_type";
    public static final String LIST_SPACING_TYPE = "list_spacing_type";

    private String listDataType = LIST_STRING_TYPE;

    public ListContentDialogAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    public void setListStringDataType(String listDataType) {
        this.listDataType = listDataType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        XsDialogContentChildItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsDialogContentChildItemBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(120));
        if (LIST_BATTERY_TYPE.equalsIgnoreCase(listDataType)) {
            Battery battery = (Battery) getItem(position);
            itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
            itemBinding.tvChildItem.setText(battery.name);
        } else if (LIST_DEVICEPART_TYPPE.equalsIgnoreCase(listDataType)) {
            DevicePart part = (DevicePart) getItem(position);
            itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
            itemBinding.tvChildItem.setText(part.name);
        } else if (LIST_SPACING_TYPE.equalsIgnoreCase(listDataType)) {
            Spacing spacing = (Spacing) getItem(position);
            itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
            itemBinding.tvChildItem.setText(spacing.name);
        } else {
            itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
            itemBinding.tvChildItem.setText(getItem(position).toString());
        }

        return itemBinding.getRoot();
    }
}
