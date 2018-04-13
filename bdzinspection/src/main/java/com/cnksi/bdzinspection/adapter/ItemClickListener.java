package com.cnksi.bdzinspection.adapter;

import android.view.View;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2016/11/29 16:04
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public interface  ItemClickListener<T>{
    void onClick(View v, T data, int position);
}
