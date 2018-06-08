package com.cnksi.sjjc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhy.autolayout.AutoLinearLayout;

/**
 * Created by han on 2017/1/7.
 */

public class LinearLayoutIntercept extends AutoLinearLayout {
    public LinearLayoutIntercept(Context context) {
        this(context,null);
    }

    public LinearLayoutIntercept(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LinearLayoutIntercept(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return  true;
    }
}
