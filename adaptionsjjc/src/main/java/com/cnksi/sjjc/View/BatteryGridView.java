package com.cnksi.sjjc.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by han on 2016/6/17.
 */
public class BatteryGridView extends GridView {
    public BatteryGridView(Context context) {
        super(context);
    }

    public BatteryGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BatteryGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

       return false;
    }
}
