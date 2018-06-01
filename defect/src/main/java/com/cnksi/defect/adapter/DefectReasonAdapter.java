package com.cnksi.defect.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.defect.R;

import java.util.Collection;

/**
 * @author Mr.K  on 2018/5/31.
 * @decrption 缺陷原因
 */

public class DefectReasonAdapter extends BaseAdapter<Lookup> {


    public DefectReasonAdapter(Context context, Collection<Lookup> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Lookup item, int position) {
        holder.setText(R.id.txt_name, TextUtils.isEmpty(item.v)?"":item.v);
        ((TextView) holder.getView(R.id.txt_name)).setTextColor(context.getResources().getColor(R.color.xs_green_color));
    }
}
