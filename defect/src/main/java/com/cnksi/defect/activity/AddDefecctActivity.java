package com.cnksi.defect.activity;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.AcitvityAddDefectBinding;

/**
 * Created by Mr.K on 2018/5/29.
 */

public class AddDefecctActivity extends BaseCoreActivity {
    AcitvityAddDefectBinding binding;

    @Override
    public int getLayoutResId() {
        return R.layout.acitvity_add_defect;
    }

    @Override
    public void initUI() {
        binding = (AcitvityAddDefectBinding) rootDataBinding;
    }


    @Override
    public void initData() {

    }
}
