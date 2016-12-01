package com.cnksi.sjjc.inter;

import android.view.View;

import com.cnksi.sjjc.bean.CopyItem;

/**
 * Created by han on 2016/10/28.
 */

public interface ItemLongClickListener<T>{

    void itemLongClick(View v , T t, int position, CopyItem item);
}
