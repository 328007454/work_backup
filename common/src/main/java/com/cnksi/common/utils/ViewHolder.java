package com.cnksi.common.utils;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lyndon on 2016/9/1.
 */
public class ViewHolder {
    public static final String TAG = "ViewHolder";

    private View rootView;

    private int position;

    private ViewDataBinding dataBinding;

    private Object tag;

    /**
     * 生成一个adapter的ViewHolder
     *
     * @param context
     * @param parent
     * @param layoutId
     * @param position
     */
    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.position = position;
        rootView.setTag(this);
    }

    /**
     * 获取一个viewHolder
     *
     * @param context
     * @param parent
     * @param layoutId
     * @param attachToRoot 是否添加到parent中
     */
    public ViewHolder(Context context, ViewGroup parent, int layoutId, boolean attachToRoot) {
        this.rootView = LayoutInflater.from(context).inflate(layoutId, parent, attachToRoot);
        rootView.setTag(this);
    }

    /**
     * 获得一个viewHolder
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder(context, parent, layoutId, position);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return holder;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * 获取根容器
     *
     * @return
     */
    public View getRootView() {
        return this.rootView;
    }

    /**
     * 获取容器中的某个控件
     *
     * @param id
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        View view = rootView.findViewById(id);
        if (null == view) {
            Log.e(TAG, "没有找到id为" + rootView.getContext().getResources().getResourceEntryName(id) + "的控件");
            return null;
        } else {
            return (T) view;
        }
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }


    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setImageByUrl(int viewId, String url) {
        ImageView imageView = getView(viewId);
        return this;
    }

    /**
     * 获取item中list中的位置
     *
     * @return
     */
    public int getItemPosition() {
        return position;
    }

    /**
     * 设置视图显示模式
     *
     * @param viewId
     * @param mode
     */
    public void setVisibility(int viewId, int mode) {
        View v = getView(viewId);
        if (v != null) {
            v.setVisibility(mode);
        }
    }

    public ViewDataBinding getDataBinding() {
        return dataBinding;
    }

    public void setVisable(int viewId, int visible) {
        getView(viewId).setVisibility(visible);

    }
}