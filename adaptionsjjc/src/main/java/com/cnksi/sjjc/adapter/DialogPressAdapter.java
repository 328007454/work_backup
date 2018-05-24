package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;

import java.util.Collection;

/**
 * 压力检测dialog
 */
public class DialogPressAdapter extends BaseAdapter<String> {



    public DialogPressAdapter(Context context, Collection<String> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final String item, final int position) {
        TextView tvBDZ = holder.getView(R.id.tv_child_item);
//        context.getResources().getColor(R.color.green_color)
        tvBDZ.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        tvBDZ.setText(item);
    }
}
