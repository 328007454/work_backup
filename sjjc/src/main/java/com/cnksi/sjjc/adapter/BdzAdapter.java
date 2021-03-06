package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.sjjc.R;

import java.util.Collection;


/**
 * Created by han on 2017/3/26.
 */

public class BdzAdapter extends BaseAdapter<Bdz> {

    public BdzAdapter(Context context, Collection<Bdz> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Bdz item, int position) {

        ((TextView) holder.getView(R.id.tv_child_item)).setText(item.name);

    }
}
