package com.cnksi.inspe.ui;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityDeviceDetailsBinding;

/**
 * Created by Mr.K on 2018/4/9.
 */

public class InspeDeviceDetailsActivity extends AppBaseActivity {
    ActivityDeviceDetailsBinding detailsBinding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_details;
    }

    @Override
    public void initUI() {
        detailsBinding = (ActivityDeviceDetailsBinding) rootDataBinding;
    }

    @Override
    public void initData() {

    }
}
