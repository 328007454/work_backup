package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsBatteryItemBinding;
import com.cnksi.bdzinspection.model.BatteryDetails;
import com.cnksi.common.Config;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

public class BatteryDetailsAdapter extends SimpleBaseAdapter {

    private boolean isNeedCopyResistance = false;

    public BatteryDetailsAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    /**
     * 是否需要抄录内阻
     *
     * @param isNeedCopyResistance
     */
    public void setNeedResistance(boolean isNeedCopyResistance) {
        this.isNeedCopyResistance = isNeedCopyResistance;
    }

    private XsBatteryItemBinding itemBinding = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BatteryDetails mBatteryItem = (BatteryDetails) getItem(position);
        if (convertView == null) {

            itemBinding = XsBatteryItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }


        itemBinding.tvBatteryName.setText(mBatteryItem.battery_number);
        itemBinding.tvVoltage.setText(mBatteryItem.voltage + "V");

        if (isNeedCopyResistance) {
            if (!TextUtils.isEmpty(mBatteryItem.resistance) && !TextUtils.isEmpty(mBatteryItem.voltage)) {
                itemBinding.tvVoltage.setVisibility(View.VISIBLE);
                setBatteryItemCopyedBackground(convertView, mBatteryItem);
            } else {
                itemBinding.tvVoltage.setVisibility(View.GONE);
                setBatteryItemNoCopyBackground(convertView, mBatteryItem);
            }
        } else {
            if (!TextUtils.isEmpty(mBatteryItem.voltage)) {
                itemBinding.tvVoltage.setVisibility(View.VISIBLE);
                setBatteryItemCopyedBackground(convertView, mBatteryItem);
            } else {
                itemBinding.tvVoltage.setVisibility(View.GONE);
                setBatteryItemNoCopyBackground(convertView, mBatteryItem);
            }
        }

        return itemBinding.getRoot();
    }


    /**
     * 设置抄录后有缺陷的背景
     *
     * @param convertView
     * @param mBatteryItem
     */
    private void setBatteryItemCopyedBackground( View convertView, BatteryDetails mBatteryItem) {
        itemBinding.tvBatteryName.setTextColor(mContext.getResources().getColor(android.R.color.white));
        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_battery_yellow_border_background_selector);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_battery_orange_border_background_selector);
        } else if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_battery_red_border_background_selector);
        } else {
            convertView.setBackgroundResource(R.drawable.xs_battery_green_background_selector);
        }
    }

    private void setBatteryItemNoCopyBackground( View convertView, BatteryDetails mBatteryItem) {
        itemBinding.tvBatteryName.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_device_yellow_border_background_selector);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_device_orange_border_background_selector);
        } else if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mBatteryItem.defectLevel)) {
            convertView.setBackgroundResource(R.drawable.xs_device_red_border_background_selector);
        } else {
            convertView.setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        }
    }

}
