package com.cnksi.defect.activity;

import android.view.View;

import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.ActivityAddDefectBinding;

/**
 * 新增缺陷界面
 *
 * @author Mr.K  on 2018/5/29.
 */

public class AddDefectActivity extends BaseTitleActivity {
    private ActivityAddDefectBinding binding;
    /**
     * 缺陷等级
     */
    private String defectLevel;

    @Override
    public void initUI() {
        setTitleText("新增缺陷");
    }

    @Override
    protected View getChildContentView() {
        binding = ActivityAddDefectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initData() {

    }


    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {

        } else if (id == R.id.btn_sure) {

        } else if (id == R.id.txt_bdz_name) {

        } else if (id == R.id.txt_space_name) {

        } else if (id == R.id.iv_select_device) {

        } else if (id == R.id.iv_defect_pic) {

        } else if (id == R.id.ib_take_picture) {

        }
    }
}
