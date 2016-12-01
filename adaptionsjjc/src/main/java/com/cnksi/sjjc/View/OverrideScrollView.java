package com.cnksi.sjjc.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by han on 2016/8/9.
 */
public class OverrideScrollView extends ScrollView {
    public OverrideScrollView(Context context) {
        super(context);
    }

    public OverrideScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverrideScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
