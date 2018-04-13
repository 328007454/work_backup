package com.cnksi.bdzinspection.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.cnksi.bdzinspection.R;
import com.zhy.core.utils.AutoUtils;

/**
 * Created by han on 2017/1/8.
 */

public class CustomRadioButton extends RadioButton {
    private int mDrawableSize;// xml文件中设置的大小

    public CustomRadioButton(Context context) {
        this(context, null, 0);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.XS_CustomRadioButton);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.XS_CustomRadioButton_drawableSize) {
                mDrawableSize = a.getDimensionPixelSize(R.styleable.XS_CustomRadioButton_drawableSize, 50);
                mDrawableSize = AutoUtils.getPercentHeightSizeBigger(mDrawableSize);

            } else if (attr == R.styleable.XS_CustomRadioButton_drawableTop) {
                drawableTop = a.getDrawable(attr);

            } else if (attr == R.styleable.XS_CustomRadioButton_drawableBottom) {
                drawableRight = a.getDrawable(attr);

            } else if (attr == R.styleable.XS_CustomRadioButton_drawableRight) {
                drawableBottom = a.getDrawable(attr);

            } else if (attr == R.styleable.XS_CustomRadioButton_drawableLeft) {
                drawableLeft = a.getDrawable(attr);

            } else {
            }
        }
        a.recycle();

        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);

    }

    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
                                                        Drawable top, Drawable right, Drawable bottom) {

        if (left != null) {
            left.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (right != null) {
            right.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (top != null) {
            top.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        setCompoundDrawables(left, top, right, bottom);
    }

}
