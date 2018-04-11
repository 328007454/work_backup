package com.cnksi.bdzinspection.inter;

import android.view.View;

/**
 * adapter item 长按事件
 * 
 * @author lyndon
 *
 * @param <T>
 */
public interface ItemLongClickListener<T> {
	void onItemLongClick(View v, T t, int position);
}
