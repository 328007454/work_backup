package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by han on 2016/6/17.
 */
public class BatteryAdapter extends BaseAdapter<String> {


    private Map<String, BatteryRecord> exitRecordMap = new HashMap<String, BatteryRecord>();

    private ItemClickListener<String> itemClickListener;

    //当前是电阻还是电压测试
    public int checkType;

    public BatteryAdapter(Context context, Collection<String> data, int layoutId, int checkType) {
        super(context, data, layoutId);
        this.checkType = checkType;
    }

    public void setCheckType(int checkType) {
        this.checkType = checkType;
        notifyDataSetChanged();
    }


    public void setRecordMap(Map<String, BatteryRecord> exitRecordMap) {
        this.exitRecordMap = exitRecordMap;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener<String> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewHolder holder, final String item, final int position) {
        holder.setText(R.id.tv_battery_name, item);
        holder.getView(R.id.rl_battery_container).setSelected(false);
        holder.getView(R.id.tv_voltage).setVisibility(View.GONE);
        TextView batteryName = holder.getView(R.id.tv_battery_name);
//        context.getResources().getColor(R.color.green_color)
        batteryName.setTextColor(ContextCompat.getColor(context,R.color.green_color));
        BatteryRecord record = exitRecordMap.get(item);

        if (0 == checkType) {
            if (null != record && !TextUtils.isEmpty(record.voltage)) {
                holder.getView(R.id.tv_voltage).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_voltage, record.voltage + "V");
                holder.getView(R.id.rl_battery_container).setSelected(true);
                batteryName.setTextColor(Color.WHITE);
            }
        } else {
            if (null != record && !TextUtils.isEmpty(record.resistance)) {
                holder.getView(R.id.tv_voltage).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_voltage, record.resistance + "mΩ");
                holder.getView(R.id.rl_battery_container).setSelected(true);
                batteryName.setTextColor(Color.WHITE);
            }
        }
        holder.getView(R.id.rl_battery_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != itemClickListener) {
                    itemClickListener.itemClick(view, item, position);
                }
            }
        });


    }


}
