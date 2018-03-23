package com.zhy.autolayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zhy.autolayout.utils.AutoLayoutHelper;

/**
 * Created by kkk on 2017/12/7.
 */

public class AutoConstraintLayout extends ConstraintLayout {

    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoConstraintLayout(Context context)
    {
        super(context);
    }

    public AutoConstraintLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public AutoConstraintLayout.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new AutoConstraintLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
    }

    public static class LayoutParams extends ConstraintLayout.LayoutParams
            implements AutoLayoutHelper.AutoLayoutParams
    {
        private AutoLayoutInfo mAutoLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs)
        {
            super(c, attrs);

            mAutoLayoutInfo = AutoLayoutHelper.getAutoLayoutInfo(c, attrs);
        }

        public LayoutParams(int width, int height)
        {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source)
        {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source)
        {
            super(source);
        }

        public LayoutParams(AutoConstraintLayout.LayoutParams source)
        {
            super((ViewGroup.MarginLayoutParams) source);

        }

        public LayoutParams(AutoFrameLayout.LayoutParams source)
        {
            super(source);
        }

        @Override
        public AutoLayoutInfo getAutoLayoutInfo()
        {
            return mAutoLayoutInfo;
        }


    }
}
