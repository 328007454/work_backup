package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.cnksi.bdzinspection.model.CopyType;

import java.util.Collection;
import java.util.List;


/**
 * Created by han on 2017/5/15.
 */

public class CopyTypeAdapter extends BaseAdapter {
    private List<CopyType> copyTypes;
    private String status;
    private List<String> typeCounts;
    private int selectedPosition;

    public CopyTypeAdapter(Context context, Collection data, int layoutId, String status) {
        super(context, data, layoutId);
        if (status.equalsIgnoreCase("1")) {
            typeCounts = (List<String>) data;
        } else if (status.equalsIgnoreCase("0")) {
            copyTypes = (List<CopyType>) data;
        }
        this.status = status;
    }

    @Override
    public void convert(ViewHolder holder, Object item, int position) {
        TextView textView = holder.getView(R.id.tv_type_name);
        if (status.equalsIgnoreCase("0")) {
            textView.setText(TextUtils.isEmpty(copyTypes.get(position).name) ? "" : copyTypes.get(position).name);
            if (selectedPosition == position) {
                textView.setTextColor(context.getResources().getColor(R.color.xs_global_base_color));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.xs_spinner_text969f9e));
            }
        }
        if (status.equalsIgnoreCase("1")) {
            textView.setText(TextUtils.isEmpty(typeCounts.get(position)) ? "" : typeCounts.get(position));
            if (selectedPosition == position) {
                textView.setTextColor(context.getResources().getColor(R.color.xs_global_base_color));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.xs_spinner_text969f9e));
            }
        }

    }

    public void setPosition(int typeSelectPosition) {
        this.selectedPosition = typeSelectPosition;
        notifyDataSetChanged();
    }
}
