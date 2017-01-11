package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;

/**
 * Created by han on 2016/6/13.
 */
public class DiscoverHoleAdapter extends BaseAdapter<String> {
    private ItemClickListener<String> itemClickListener;

    public void setItemClickListener(ItemClickListener<String> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public DiscoverHoleAdapter(Context context, Collection<String> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final String item, final int position) {
        TextView tvPosition = holder.getView(R.id.tv_child_item);
//        context.getResources().getColor(R.color.green_color)
        tvPosition.setTextColor(ContextCompat.getColor(context,R.color.green_color));
        tvPosition.setText(item);
        tvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClick(view,item,position);
            }
        });
    }
}
