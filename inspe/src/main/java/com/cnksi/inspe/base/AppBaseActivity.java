package com.cnksi.inspe.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.inspe.R;

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
    private Toolbar toolbar;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getRootDataBinding();

//        toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.include_inspe_title, null, false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.findViewById(R.id.toolbar_title);

            toolbar.findViewById(R.id.toolbar_back_btn).setOnClickListener(toolBarOnClickListener);
            toolbar.findViewById(R.id.toolbar_menu_btn).setOnClickListener(toolBarOnClickListener);
        }

        initUI();
        initData();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(title);
        }
    }

    public void setTitle(CharSequence title, int resBackId) {
        setTitle(title);
        setTitleBackBtn(resBackId);
    }

    public void setTitleBackBtn(int resBackId) {
        if (toolbar != null) {
            toolbar.findViewById(R.id.toolbar_back_btn).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_back_btn).setBackgroundResource(resBackId);
        }
    }

    public void setTitleMenuBtn(int resMenuId) {
        if (toolbar != null) {
            toolbar.findViewById(R.id.toolbar_menu_btn).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_menu_btn).setBackgroundResource(resMenuId);
        }
    }

    private View.OnClickListener toolBarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_back_btn) {
                onBack(view);
            } else if (view.getId() == R.id.toolbar_menu_btn) {
                onMenu(view);
            }
        }
    };

    protected void onBack(View view) {
        finish();
    }

    protected void onMenu(View view) {

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
