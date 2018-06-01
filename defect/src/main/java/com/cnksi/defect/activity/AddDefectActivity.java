package com.cnksi.defect.activity;

import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.model.Bdz;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.ActivityAddDefectBinding;
import com.cnksi.defect.view.PopWindowCustom;

import java.util.ArrayList;
import java.util.List;

/**
 * 新增缺陷界面
 *
 * @author Mr.K  on 2018/5/29.
 */

public class AddDefectActivity extends BaseTitleActivity {
    private ActivityAddDefectBinding binding;
    private List<Bdz> bdzList = new ArrayList<>();
    /**
     * 缺陷等级
     */
    private String defectLevel;
    private Bdz bdz;

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
        bdzList = BdzService.getInstance().findAllBdzByDp(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
    }


    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {

        } else if (id == R.id.btn_sure) {

        } else if (id == R.id.txt_bdz_name) {
            showBdzWindow();
        } else if (id == R.id.txt_space_name) {

        } else if (id == R.id.iv_select_device) {

        } else if (id == R.id.iv_defect_pic) {

        } else if (id == R.id.ib_take_picture) {

        }
    }

    private void showBdzWindow() {
        new PopWindowCustom.PopWindowBuilder<Bdz>(this).setPopWindowBuilder(bdz -> bdz.name)
                .setWidth(binding.txtBdzName.getWidth())
                .setList(bdzList)
                .setOutSideCancelable(true).
                setItemClickListener((adapter, view1, position) -> {
                    ToastUtils.showMessage(bdzList.get(position).name);
                    bdz = bdzList.get(position);
                    binding.txtBdzName.setText(bdz.name);
                }).setDropDownOfView(binding.txtBdzName).setBackgroundAlpha(0.6f).showAsDropDown(0,10);

    }
}
