package com.cnksi.common.listener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/3/8 16:13
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public abstract class AbstractTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * 实现 TextWatcher
     * @param s
     */
    @Override
   abstract public void afterTextChanged(Editable s) ;
}
