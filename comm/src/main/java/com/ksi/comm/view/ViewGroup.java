package com.ksi.comm.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/24 09:28
 */
public class ViewGroup extends LinearLayout {
    public ViewGroup(Context context) {
        super(context);
    }

    public ViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
