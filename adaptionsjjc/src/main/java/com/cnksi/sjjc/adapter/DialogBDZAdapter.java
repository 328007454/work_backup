package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.cnksi.common.model.Bdz;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;

/**
 * Created by luoxy on 16/4/29.
 */
public class DialogBDZAdapter extends BaseAdapter<Bdz> {

    private ItemClickListener<Bdz> itemClickListener;

    public void setItemClickListener(ItemClickListener<Bdz> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DialogBDZAdapter(Context context, Collection<Bdz> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final Bdz item, final int position) {
        TextView tvBDZ = holder.getView(R.id.tv_child_item);
//        context.getResources().getColor(R.color.green_color)
        tvBDZ.setTextColor(ContextCompat.getColor(context,R.color.green_color));
        tvBDZ.setText(item.name);
        holder.getRootView().setOnClickListener(v -> {
            if (null != itemClickListener) {
                itemClickListener.itemClick(v, item, position);
            }
        });
    }
}
