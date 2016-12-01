package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.inter.ItemClickListener;

import org.xutils.x;

import java.util.Collection;
import java.util.List;

/**
 * Created by han on 2016/6/19.
 */
public class BatteryImageAdapter extends BaseAdapter<BatteryRecord> {
    private ItemClickListener<BatteryRecord> itemClickListener;

    public void setItemClickListener(ItemClickListener<BatteryRecord> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BatteryImageAdapter(Context context, Collection<BatteryRecord> data, int layoutId) {
        super(context, data, layoutId);

    }

    @Override
    public void convert(ViewHolder holder, final BatteryRecord item, final int position) {

        String voltageImages = item.voltageImages == null ? "" : item.voltageImages;
        String resistanceImages = item.resistanceImages == null ? "" : item.resistanceImages;
        List<String> voltageList = StringUtils.string2List(voltageImages);
        List<String> resistanceList = StringUtils.string2List(resistanceImages);
        voltageList.addAll(resistanceList);
        holder.setVisable(R.id.tv_battery_number, View.VISIBLE);
        holder.setText(R.id.tv_battery_number, item.battary_code);
        holder.setText(R.id.image_num, voltageList.size() + "");

        if (1 >= voltageList.size()) {
            holder.setVisable(R.id.image_num, View.GONE);
        } else {
            holder.setVisable(R.id.image_num, View.VISIBLE);
        }
        if (!voltageList.isEmpty()) {
            holder.setVisable(R.id.img_battery,View.VISIBLE);
            x.image().bind(((ImageView) holder.getView(R.id.img_battery)), Config.RESULT_PICTURES_FOLDER + voltageList.get(0), CustomApplication.getImageOPtions());
        }else {
            ((ImageView) holder.getView(R.id.img_battery)).setImageBitmap(null);
            holder.setVisable(R.id.tv_battery_number, View.GONE);
            holder.setVisable(R.id.img_battery,View.GONE);
        }
//        ((ImageView) holder.getView(R.id.img_battery)).setImageURI(Uri.fromFile(new File(Config.RESULT_PICTURES_FOLDER + voltageList.get(0))));
        holder.getView(R.id.img_battery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) itemClickListener.itemClick(v, item, position);
            }
        });
    }
}
