package com.cnksi.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.RelayoutUtil;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.x;


/**
 * 创建一个viewHolder
 */
public class ViewHolder {

    public static final String TAG = "ViewHolder";

    private SparseArray<View> childViews;

    private  View rootView;

    private int position;

    /**
     * 生成一个adapter的ViewHolder
     *
     * @param context
     * @param parent
     * @param layoutId
     * @param position
     */
    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        RelayoutUtil.reLayoutViewHierarchy(this.rootView);
        this.childViews = new SparseArray<View>();
        this.position = position;
        rootView.setTag(this);
    }
    public ViewHolder(){}

    public void setRootView(View view){
        this.rootView = view;
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
        RelayoutUtil.reLayoutViewHierarchy(this.rootView);
        this.childViews = new SparseArray<View>();
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
            AutoUtils.autoSize(holder.getRootView());
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return holder;
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
    public <T extends View> T getView(int id) {
        View view = rootView.findViewById(id);
        if (null == view) {
            Log.e(TAG, "没有找到id为" + rootView.getContext().getResources().getResourceEntryName(id) + "的控件");
            return null;
        } else {
            childViews.put(id, view);
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
    public ViewHolder setText(int viewId, String text) {
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
        x.image().bind(imageView, url);
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
    public void setVisable(int viewId, int mode) {
        View v = getView(viewId);
        if (v != null) {
            v.setVisibility(mode);
        }
    }


}


