package com.cnksi.bdzinspection.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/16 15:42
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NoScrollViewPager extends ViewPager {

    private boolean noScroll;
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
		/* return false;//super.onTouchEvent(arg0); */
        if (noScroll) {
            return false;
        } else {
            return super.onTouchEvent(arg0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll) {
            return false;
        } else {
            return super.onInterceptTouchEvent(arg0);
        }
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
}
