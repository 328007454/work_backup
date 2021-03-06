package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

@SuppressWarnings("deprecation")
@Deprecated
public abstract class SimpleBaseAdapter extends BaseAdapter {
    protected Context mContext = null;
    protected LayoutInflater mInflater = null;
    protected List<? extends Object> dataList = null;
    protected int width = 0;
    protected int height = 0;

    public SimpleBaseAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public SimpleBaseAdapter(Context context, List<? extends Object> dataList) {
        this(context);
        this.dataList = dataList;
    }


    public void setList(List<? extends Object> dataList) {
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return dataList != null ? dataList.get(position) : null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    protected @ColorInt
    int getColor(@ColorRes int colorRes) {
        return mContext.getResources().getColor(colorRes);
    }

}
