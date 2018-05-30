package com.cnksi.common.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by lyndon on 2016/9/1.
 */
public abstract class BaseBindingAdapter<D extends ViewDataBinding, T> extends android.widget.BaseAdapter {
    protected List<T> data;
    public Context context;
    private int layoutId;

    public BaseBindingAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return null == data ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setList(List<T> data) {
        this.data = data;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        D binding;
        if (null == convertView) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, parent, false);
            AutoUtils.autoSize(binding.getRoot());
        } else {
            binding = DataBindingUtil.findBinding(convertView);
        }
        convert(binding, getItem(position), position);
        return binding.getRoot();
    }

    /**
     * 实现数据赋值
     *
     * @param binding
     * @param item
     * @param position
     */
    public abstract void convert(D binding, T item, int position);
}
