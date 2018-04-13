package com.cnksi.bdzinspection.inter;

import android.view.View;

/**
 * item 点击事件
 * 
 * @author lyndon
 *
 */
public interface ItemClickListener<T> {
	void onItemClick(View v, T t, int position);
}
