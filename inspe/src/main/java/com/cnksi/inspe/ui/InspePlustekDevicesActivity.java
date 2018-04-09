package com.cnksi.inspe.ui;

import android.content.Intent;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;

/**
 * 精益化评价-检查的设备展示
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:22
 */
public class InspePlustekDevicesActivity extends AppBaseActivity {


    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustekdevices;
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {
        startActivity(new Intent(this, InspePlustekStandardActivity.class));
    }
}
