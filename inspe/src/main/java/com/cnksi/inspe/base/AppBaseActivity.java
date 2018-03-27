package com.cnksi.inspe.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.fragment.BaseCoreFragment;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 11:23
 */

public abstract class AppBaseActivity extends AppCompatActivity {

    protected final String tag = this.getClass().getSimpleName();
    protected ViewDataBinding rootDataBinding;

//    @Override
//    protected final void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public final void getRootDataBinding() {
//        super.getRootDataBinding();
//    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getRootDataBinding();
        initUI();
        initData();
    }

    public final void getRootDataBinding() {
        rootDataBinding = DataBindingUtil.setContentView(this, getLayoutResId());
        setContentView(rootDataBinding.getRoot());
    }

    public abstract int getLayoutResId();

    public abstract void initUI();

    public abstract void initData();

    protected Context context;

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
