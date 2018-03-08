package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;

import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzqkBinding;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 10:15
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BHDZQKActivity extends BaseActivity {
    ActivityGztzBhdzqkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGztzBhdzqkBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        setTitleText(currentBdzName + "保护动作情况");
        initView();
    }

    private void initView() {
        binding.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(_this, BHDZJLActivity.class);
            startActivity(intent);
        });
        binding.btnPre.setOnClickListener(v -> finish());
        binding.chzdzqk.setType("chzdzqk");
        binding.bhmc.setType("bhmc");
    }
}
