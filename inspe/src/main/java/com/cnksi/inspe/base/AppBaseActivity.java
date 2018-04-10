package com.cnksi.inspe.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.inspe.R;
import com.cnksi.inspe.db.UserService;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 11:23
 */

public abstract class AppBaseActivity extends AppCompatActivity {

    protected final String tag = this.getClass().getSimpleName();
    protected ViewDataBinding rootDataBinding;
    private UserService userService;// = UserService.getInstance();

    protected UserService getUserService() {
        if (userService == null) {
            synchronized (this) {
                if (userService == null) {
                    userService = UserService.getInstance();
                }
            }
        }

        return userService;
    }

    //    @Override
//    protected final void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public final void getRootDataBinding() {
//        super.getRootDataBinding();
//    }
    protected TextView titleTxt;
    protected ImageButton toolbar_back_btn, toolbar_menu_btn;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
        context = this;
        getRootDataBinding();

        titleTxt = (TextView) findViewById(R.id.toolbar_title);
        toolbar_back_btn = (ImageButton) findViewById(R.id.toolbar_back_btn);
        toolbar_menu_btn = (ImageButton) findViewById(R.id.toolbar_menu_btn);
        if (toolbar_back_btn != null) {
            toolbar_back_btn.setOnClickListener(toolBarOnClickListener);
        }
        if (toolbar_menu_btn != null) {
            toolbar_menu_btn.setOnClickListener(toolBarOnClickListener);
        }

        initUI();

        initData();

    }

    @Override
    public void setTitle(CharSequence title) {
        if (titleTxt != null) {
            titleTxt.setText(title);
        }
    }

    public void setTitle(CharSequence title, int resBackId) {
        setTitle(title);
        setTitleBackBtn(resBackId);
    }

    public void setTitle(CharSequence title, int resBackId, int menuResId) {
        setTitle(title);
        setTitleBackBtn(resBackId);
        setTitleMenuBtn(menuResId);
    }

    public void setTitleBackBtn(int resBackId) {
        if (toolbar_back_btn != null) {
            toolbar_back_btn.setVisibility(View.VISIBLE);
            toolbar_back_btn.setImageResource(resBackId);
        }
    }

    public void setTitleMenuBtn(int resMenuId) {
        if (toolbar_menu_btn != null) {
            toolbar_menu_btn.setVisibility(View.VISIBLE);
            toolbar_menu_btn.setImageResource(resMenuId);
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
