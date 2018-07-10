package com.cnksi.bdzinspection.fragment.xian;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.databinding.XFragmentDeviceCopyBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;

/**
 * Created by Mr.K on 2018/7/7.
 */

public class DeviceCopyFragment extends BaseFragment {
    XFragmentDeviceCopyBinding copyBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        copyBinding = XFragmentDeviceCopyBinding.inflate(inflater);

        return copyBinding.getRoot();
    }

    @Override
    protected void lazyLoad() {

    }
}
