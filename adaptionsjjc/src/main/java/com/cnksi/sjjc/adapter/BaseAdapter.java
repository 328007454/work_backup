package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import com.zhy.autolayout.utils.AutoUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lyndon on 2016/9/1.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected Collection<T> data;

    public Context context;

    private int layoutId;

    public BaseAdapter(Context context, Collection<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    public BaseAdapter(Context context, Collection<T> data) {
        this.context = context;
        this.data = data;
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
        ViewHolder holder = null;
        if (null == convertView){
            holder = ViewHolder.get(context, convertView, parent, layoutId, position);
            AutoUtils.autoSize(holder.getRootView());
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        convert(holder, (T) getItem(position), position);
        return holder.getRootView();
    }

    /**
     * 实现数据赋值
     *
     * @param holder
     * @param item
     * @param position
     */
    public abstract void convert(ViewHolder holder, T item, int position);
}
