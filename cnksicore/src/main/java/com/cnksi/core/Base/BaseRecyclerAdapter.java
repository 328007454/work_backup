package com.cnksi.core.Base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/21
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Collection<T> data;

    private Context context;

    private int layoutId;

    public BaseRecyclerAdapter(Context context, Collection<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(context, parent, layoutId, false);
        return new RecyclerViewHolder(viewHolder.getRootView());
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        convert(holder, (T) getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private Object getItem(int position) {
        ArrayList arrayList = new ArrayList(data);
        return arrayList.get(position);
    }

    /**
     * 实现数据赋值
     *
     * @param holder
     * @param item
     * @param position
     */
    public abstract void convert(RecyclerViewHolder holder, T item, int position);
}
