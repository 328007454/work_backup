package com.cnksi.sjjc.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.Config;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.sjjc.R;

import java.util.List;
import java.util.Map;

/**
 * @author luoxy
 * @date 2016/6/21
 * @copyRight
 */
public class BatteryReportImageAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    private ItemClickListener<List<String>> itemClickListener;

    public BatteryReportImageAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    public void setItemClickListener(ItemClickListener<List<String>> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private Map<String, List<String>> batteryImageMap;

    public void setBatteryImageMap(Map<String, List<String>> batteryImageMap) {
        this.batteryImageMap = batteryImageMap;
        notifyDataSetChanged();
    }


    @Override
    protected void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.tv_battery_number, item);
        if (null != batteryImageMap && !batteryImageMap.isEmpty()) {
            final List<String> batteryImageList = batteryImageMap.get(item);
            holder.setText(R.id.image_num, "" + batteryImageList.size());
            if (batteryImageList.size() > 1) {
                holder.getView(R.id.image_num).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.image_num).setVisibility(View.GONE);
            }
            Bitmap bitmap = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + batteryImageList.get(0));
            ((ImageView) holder.getView(R.id.img_battery)).setImageBitmap(bitmap);
            holder.getView(R.id.img_battery).setOnClickListener(v -> {
                if (null != itemClickListener) {
                    itemClickListener.onClick(v, batteryImageList, holder.getAdapterPosition());
                }
            });
        }
    }
}
