package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;

import java.util.Collection;

/**
 * Created by han on 2017/4/25.
 */

public class DefectTypeAdapter extends BaseAdapter<String> {
    public DefectTypeAdapter(Context context, Collection data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String item, int position) {
        ((TextView) (holder.getView(R.id.defect_type_name))).setText(item.toString());
    }
}
