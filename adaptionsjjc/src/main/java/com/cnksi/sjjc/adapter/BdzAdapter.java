package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Bdz;

import java.util.Collection;


/**
 * Created by han on 2017/3/26.
 */

public class BdzAdapter extends BaseAdapter {

    public BdzAdapter(Context context, Collection data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Object item, int position) {

        ((TextView) holder.getView(R.id.tv_child_item)).setText(((Bdz) item).name);

    }
}
