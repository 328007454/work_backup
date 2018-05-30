package com.cnksi.defect.activity;

import android.view.View;

import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.defect.databinding.ActivityOperateDefectBinding;

/**
 * Created by Mr.K on 2018/5/30.
 */

public class OperateDefectActivity extends BaseTitleActivity {
    ActivityOperateDefectBinding binding;

    @Override
    protected View getChildContentView() {
        binding = ActivityOperateDefectBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void initUI() {
        setTitleText("缺陷管控");

        initFragments();
    }

    private void initFragments() {


    }
}
