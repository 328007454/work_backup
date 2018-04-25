package com.cnksi.inspe.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ksi-android on 2016/4/29.
 */
public class MyScrollVListView extends RecyclerView {
    public MyScrollVListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public MyScrollVListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyScrollVListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
