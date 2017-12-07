package com.cnksi.sjjc.adapter.holder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by kkk on 2017/9/25.
 */

public class ItemHolder extends RecyclerView.ViewHolder {

    private ViewDataBinding mDataBinding;

    public ItemHolder(ViewDataBinding dataBinding) {
        super(dataBinding.getRoot());
        this.mDataBinding = dataBinding;
    }

    public ViewDataBinding getDataBinding() {
        return mDataBinding;
    }
}
