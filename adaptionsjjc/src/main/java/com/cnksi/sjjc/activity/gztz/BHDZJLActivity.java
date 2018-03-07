package com.cnksi.sjjc.activity.gztz;

import android.os.Bundle;

import com.cnksi.sjjc.activity.BaseActivity;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 10:50
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BHDZJLActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentValue();
        setTitleText(currentBdzName+"保护动作记录");
    }
}


