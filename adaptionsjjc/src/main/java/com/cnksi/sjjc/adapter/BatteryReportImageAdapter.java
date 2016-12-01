package com.cnksi.sjjc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.cnksi.core.adapter.BaseRecyclerAdapter;
import com.cnksi.core.adapter.RecyclerHolder;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.inter.ItemClickListener;

import org.xutils.x;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author luoxy
 * @date 2016/6/21
 * @copyRight
 */
public class BatteryReportImageAdapter extends BaseRecyclerAdapter<String> {

    private ItemClickListener<List<String>> itemClickListener;
    public void setItemClickListener(ItemClickListener<List<String>> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private Map<String, List<String>> batteryImageMap;

    public void setBatteryImageMap(Map<String, List<String>> batteryImageMap) {
        this.batteryImageMap = batteryImageMap;
        notifyDataSetChanged();
    }

    public BatteryReportImageAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);

    }


    @Override
    public void convert(RecyclerHolder holder, String item, final int position, boolean isScrolling) {
        holder.setText(R.id.tv_battery_number, item);
        if (null != batteryImageMap && !batteryImageMap.isEmpty()) {
            final List<String> batteryImageList = batteryImageMap.get(item);
            holder.setText(R.id.image_num, "" + batteryImageList.size());
            if (batteryImageList.size() > 1)
                holder.getView(R.id.image_num).setVisibility(View.VISIBLE);
            else
                holder.getView(R.id.image_num).setVisibility(View.GONE);
            x.image().bind((ImageView)holder.getView(R.id.img_battery),Config.RESULT_PICTURES_FOLDER+batteryImageList.get(0), CustomApplication.getLargeImageOptions());
//            ((ImageView) holder.getView(R.id.img_battery)).setImageURI(Uri.fromFile(new File(Config.RESULT_PICTURES_FOLDER + batteryImageList.get(0))));
            holder.getView(R.id.img_battery).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemClickListener)
                        itemClickListener.itemClick(v, batteryImageList, position);
                }
            });
        }
    }
}
