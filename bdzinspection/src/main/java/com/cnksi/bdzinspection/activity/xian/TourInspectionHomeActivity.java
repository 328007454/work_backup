package com.cnksi.bdzinspection.activity.xian;

import android.databinding.DataBindingUtil;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityTourinspectionHomeBinding;
import com.cnksi.common.base.BaseTitleActivity;

/**
 * @author Mr.K  on 2018/7/4.
 */

public class TourInspectionHomeActivity extends BaseTitleActivity {
    XsActivityTourinspectionHomeBinding homeBinding;

    @Override
    public void getRootDataBinding() {
        homeBinding = DataBindingUtil.setContentView(this, R.layout.xs_activity_tourinspection_home);
        initView();
    }

    private void initView() {

    }
}
