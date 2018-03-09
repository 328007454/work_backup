package com.cnksi.sjjc.inter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 16:13
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
   abstract public void afterTextChanged(Editable s) ;
}
