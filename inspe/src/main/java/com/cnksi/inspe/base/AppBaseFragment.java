package com.cnksi.inspe.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.fragment.BaseCoreFragment;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 14:24
 */

public abstract class AppBaseFragment extends Fragment {
    protected final String tag = getClass().getSimpleName();

    protected ViewDataBinding fragmentDataBinding;

//    @Override
//    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View fragmentRootView = null;
        if (fragmentDataBinding == null) {
            fragmentDataBinding = DataBindingUtil.inflate(inflater, getFragmentLayout(), container, false);
            fragmentRootView = fragmentDataBinding.getRoot();
            lazyLoad();
        } else {
            fragmentRootView = fragmentDataBinding.getRoot();
        }

        return fragmentRootView;
    }

    /**
     * fragment布局文件
     *
     * @return
     */
    public abstract int getFragmentLayout();

    protected abstract void lazyLoad();
}
