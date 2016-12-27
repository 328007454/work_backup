package com.cnksi.core.Base;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/21
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "RecyclerViewHolder";

    private SparseArray<View> childViews;

    private View rootView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.rootView = itemView;
        this.childViews = new SparseArray<View>();
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
    public RecyclerViewHolder setText(int viewId, String text) {
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
    public RecyclerViewHolder setImageResource(int viewId, int drawableId) {
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
    public RecyclerViewHolder setImageBitmap(int viewId, Bitmap bm) {
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
    public RecyclerViewHolder setImageByUrl(int viewId, String url) {
        ImageView imageView = getView(viewId);
        x.image().bind(imageView, url);
        return this;
    }


}
