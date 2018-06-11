package com.cnksi.common.base;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * @author  Mr.K on 2018/6/8.
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment {
    protected ViewDataBinding dataBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        dataBinding = DataBindingUtil.inflate(inflater,setLayout(),container,false);
        initData();
        return dataBinding.getRoot();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    /**
     * 初始化数据用的
     */
    public abstract void initData();


    /**
     * UI布局
     * @return 返回布局文件layout
     */
    public abstract int setLayout();
}
