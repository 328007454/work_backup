package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.model.CopyType;
import com.cnksi.common.utils.ViewHolder;

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
        if ("1".equalsIgnoreCase(status)) {
            typeCounts = (List<String>) data;
        } else if ("0".equalsIgnoreCase(status)) {
            copyTypes = (List<CopyType>) data;
        }
        this.status = status;
    }

    @Override
    public void convert(ViewHolder holder, Object item, int position) {
        TextView textView = holder.getView(R.id.tv_type_name);
        if ("0".equalsIgnoreCase(status)) {
            textView.setText(TextUtils.isEmpty(copyTypes.get(position).name) ? "" : copyTypes.get(position).name);
            if (selectedPosition == position) {
                textView.setTextColor(context.getResources().getColor(R.color.xs_global_base_color));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.xs_spinner_text969f9e));
            }
        }
        if ("1".equalsIgnoreCase(status)) {
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
