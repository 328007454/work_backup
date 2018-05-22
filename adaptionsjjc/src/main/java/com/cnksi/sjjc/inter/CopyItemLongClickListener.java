package com.cnksi.sjjc.inter;

import android.view.View;

import com.cnksi.common.model.CopyItem;


public interface  CopyItemLongClickListener<T> {
	void onItemLongClick(View v, T t, int position, CopyItem item);
}
