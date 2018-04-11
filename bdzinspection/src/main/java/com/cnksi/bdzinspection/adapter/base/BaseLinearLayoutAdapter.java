package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.adapter.ViewHolder;

import java.util.List;
import java.util.Stack;

/**
 * 基础adapter<br/>
 * 所有子类必须实现{@link #convert(ViewHolder, Object, int)}<br/>
 */
public abstract class BaseLinearLayoutAdapter<T> extends android.widget.BaseAdapter {

    public List<T> data;

    public Context context;

    private int layoutId;

    Stack<View> detachViews = new Stack<>();

    public LinearLayout container;

    public BaseLinearLayoutAdapter(Context context, List<T> data, LinearLayout container, int layoutId) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context, convertView, parent, layoutId, position);
         T t =  getItem(position);
        convert(holder, t, position);
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
}
