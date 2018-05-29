package com.cnksi.common.listener;

import android.view.View;

/**
 * adapter item 长按事件
 * 
 * @author lyndon
 *
 * @param <T>
 */
public interface ItemLongClickListener<T> {
	void onLongClick(View v, T t, int position);
}
