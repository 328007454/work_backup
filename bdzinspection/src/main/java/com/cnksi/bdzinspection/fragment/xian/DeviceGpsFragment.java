package com.cnksi.bdzinspection.fragment.xian;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.databinding.XFragmentDeviceGpsBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;

/**
 * Created by Mr.K on 2018/7/7.
 */

public class DeviceGpsFragment extends BaseFragment {
    XFragmentDeviceGpsBinding gpsBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       gpsBinding = XFragmentDeviceGpsBinding.inflate(inflater);

        return gpsBinding.getRoot();
    }

    @Override
    protected void lazyLoad() {

    }
}
