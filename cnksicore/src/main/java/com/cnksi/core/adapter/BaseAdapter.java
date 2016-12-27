package com.cnksi.core.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;
import java.util.Collection;

/**
 * 基础adapter<br/>
 * 所有子类必须实现{@link #convert(ViewHolder, Object, int)}<br/>
 * Created by luoxy on 16/4/15.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    public Collection<T> data;

    public  Context context;


    public int layoutId;


    public BaseAdapter(Context context, Collection<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null == data ? null : Arrays.asList(data.toArray()).get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setList(Collection<T> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  holder  = ViewHolder.get(context, convertView, parent, layoutId, position);
        convert(holder, (T) getItem(position), position);
        return holder.getRootView();
    }


    /**
     * 实现数据赋值
     * @param holder
     * @param item
     * @param position
     */
    public abstract void convert(ViewHolder holder, T item, int position);

}
