package com.cnksi.common.base;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.common.R;
import com.cnksi.common.databinding.IncludeTitleBinding;
import com.cnksi.common.utils.KeyBoardUtils;


/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/23
 */
public abstract class BaseTitleActivity extends BaseActivity {

    public boolean isDefaultTitle = true;

    protected IncludeTitleBinding mTitleBinding;

    @Override
    public void getRootDataBinding() {
        if (isDefaultTitle) {
            mTitleBinding = DataBindingUtil.setContentView(mActivity, R.layout.include_title);
            View v = getChildContentView();
            if (v != null) {
                setChildView(v);
            }
        } else {
            super.getRootDataBinding();
        }
    }

    /**
     * 获取标题以下的布局
     *
     * @return
     */
    protected View getChildContentView() {
        return null;
    }


    /**
     * 设置container内容
     *
     * @param layoutResID
     */
    public void setChildView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, mTitleBinding.rootContainer, false);
        setChildView(view);
    }

    /**
     * 设置container内容
     *
     * @param
     */
    public void setChildView(View view) {
        mTitleBinding.rootContainer.addView(view);
        mTitleBinding.btnBack.setOnClickListener(v -> {
            KeyBoardUtils.closeKeybord(mActivity);
            onBackPressed();
        });

    }

    /**
     * 设置container内容
     *
     * @param view
     * @param params
     */
    public void setChildView(View view, ViewGroup.LayoutParams params) {
        mTitleBinding.rootContainer.addView(view, params);
    }


    public void setTitleText(CharSequence str) {
        mTitleBinding.tvTitle.setText(str);
    }


}
