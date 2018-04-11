package com.cnksi.bdzinspection.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cnksi.bdzinspection.R;
import com.cnksi.xscore.xsutils.BitmapHelp;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.task.Priority;

import java.util.List;

@SuppressWarnings("deprecation")
public abstract class SimpleBaseAdapter extends BaseAdapter {
    protected Context mContext = null;
    protected LayoutInflater mInflater = null;
    protected List<? extends Object> dataList = null;
    protected BitmapUtils mBitmapUtils;
    protected BitmapDisplayConfig mBitmapConfig;
    protected BitmapSize bitmapSize;
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
        mBitmapUtils = BitmapHelp.getInstance().getBitmapUtils(context);
        mBitmapUtils.configDefaultLoadingImage(R.drawable.xs_ic_app);
        mBitmapUtils.configDefaultLoadFailedImage(R.drawable.xs_ic_app);
        mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
        mBitmapUtils.configMemoryCacheEnabled(true);
    }

    /**
     */
    public void setScaleDownBitmapSize(int scaleDown) {
        mBitmapConfig = new BitmapDisplayConfig();
        bitmapSize = new BitmapSize(width, height).scaleDown(scaleDown);
        mBitmapConfig.setBitmapMaxSize(bitmapSize);
        mBitmapConfig.setPriority(Priority.UI_TOP);
        mBitmapConfig.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.xs_ic_app));
    }

    /**
     * 缩放至指定宽高
     *
     * @param width
     * @param height
     */
    public void setBitmapSize(int width, int height) {
        mBitmapConfig = new BitmapDisplayConfig();
        bitmapSize = new BitmapSize(width, height);
        mBitmapConfig.setBitmapMaxSize(bitmapSize);
        mBitmapConfig.setAutoRotation(true);
        mBitmapConfig.setPriority(Priority.UI_TOP);
        mBitmapConfig.setLoadFailedDrawable(mContext.getResources().getDrawable(R.drawable.xs_ic_app));
    }

    public void setList(List<? extends Object> dataList) {
        this.dataList = dataList;
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public Object getItem(int position) {
        return dataList != null ? dataList.get(position) : null;
    }


    public long getItemId(int position) {
        return position;
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);

    protected @ColorInt
    int getColor(@ColorRes int colorRes) {
        return mContext.getResources().getColor(colorRes);
    }

}
