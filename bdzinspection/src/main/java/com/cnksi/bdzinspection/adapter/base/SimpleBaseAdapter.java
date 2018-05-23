package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cnksi.core.utils.BitmapUtils;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class SimpleBaseAdapter extends BaseAdapter {
    protected Context mContext = null;
    protected LayoutInflater mInflater = null;
    protected List<? extends Object> dataList = null;
    protected BitmapUtils mBitmapUtils;
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

    /**
     * 初始化图片加载器
     *
     * @param context
     */
    public void initBitmapUtils(Context context) {
    }

    /**
     */
    public void setScaleDownBitmapSize(int scaleDown) {
    }

    /**
     * 缩放至指定宽高
     *
     * @param width
     * @param height
     */
    public void setBitmapSize(int width, int height) {
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
