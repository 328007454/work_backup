package com.cnksi.sjjc.fragment.launcher;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.FragmentUnifyBinding;

/**
 * Created by han on 2017/3/22.
 */

public class UnifyFragment extends BaseCoreFragment {
    FragmentUnifyBinding unifyBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        unifyBinding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.fragment_unify,container,false);
        return unifyBinding.getRoot();
    }
    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initData() {

    }
}
