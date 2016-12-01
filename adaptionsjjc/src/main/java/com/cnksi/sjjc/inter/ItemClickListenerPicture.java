package com.cnksi.sjjc.inter;

import android.view.View;

/**
 * Created by han on 2016/6/13.
 */
public interface ItemClickListenerPicture<T> {
    void itemClick(View v, T t, int position,View iView,View view);

    void itemClick(View v, T t, int position);
}
