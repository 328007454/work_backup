package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cnksi.sjjc.inter.ItemClickListener;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 基础adapter<br/>
 * 所有子类必须实现{@link #convert(D, T, int)}<br/>
 * Created by luoxy on 16/4/15.
 */
public abstract class BaseLinearBindingAdapter<D extends ViewDataBinding, T> extends android.widget.BaseAdapter {

    public List<T> data;

    public Context context;

    private int layoutId;

    List<View> detachViews = new LinkedList<>();

    public LinearLayout container;

    public void setItemClickListener(ItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    ItemClickListener<T> itemClickListener;

    public BaseLinearBindingAdapter(Context context, List<T> data, LinearLayout container, int layoutId) {
        this.context = context;
        this.data = data;
        this.container = container;
        container.removeAllViews();
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
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        D binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, parent, false);
            AutoUtils.autoSize(binding.getRoot());
        } else {
            binding = DataBindingUtil.findBinding(convertView);
        }
        final T t = getItem(position);
        if (itemClickListener != null) {
            binding.getRoot().setOnClickListener(v -> itemClickListener.itemClick(v, t, position));
            binding.getRoot().setOnLongClickListener(v -> {
                itemClickListener.itemLongClick(v, t, position);
                return false;
            });
        }
        convert(binding, t, position);
        return binding.getRoot();
    }

    /**
     * 实现数据赋值
     *
     * @param holder
     * @param item
     * @param position
     */
    public abstract void convert(D holder, T item, int position);


    @Override
    public void notifyDataSetChanged() {
        int viewCount = container.getChildCount();
        int size = getCount();
        for (int i = 0; i < size; i++) {
            if (i < viewCount) {
                getView(i, container.getChildAt(i), container);
            } else {
                View v = null;
                if (detachViews.size() > 0) {
                    v = detachViews.get(0);
                    detachViews.remove(0);
                }
                v = getView(i, v, container);
                container.addView(v);
            }
        }
        if (viewCount > size) {
            for (int i = viewCount - 1; i >= size; i--) {
                detachViews.add(container.getChildAt(i));
                container.removeViewAt(i);
            }

        }
        //
    }

    public void removeAllViews() {
        for (int i = 0; i < container.getChildCount(); i++) {
            detachViews.add(container.getChildAt(i));
        }
        container.removeAllViews();
    }
}
