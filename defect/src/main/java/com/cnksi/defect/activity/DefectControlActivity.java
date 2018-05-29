package com.cnksi.defect.activity;

import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Users;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.adapter.DefectContentAdapter;
import com.cnksi.defect.databinding.ActivityDefectControlBinding;
import com.cnksi.defect.view.PopWindowCustom;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mr.K  on 2017/4/25.
 */

public class DefectControlActivity extends BaseTitleActivity {
    private ActivityDefectControlBinding defectControlBinding;
    private List<DbModel> bdzList = new ArrayList<>();
    private DefectContentAdapter defectContentAdapter;
    /**
     * 变电站实体model
     */
    private DbModel bdzModel;
    private int defectLevel = 0;
    private List<DefectRecord> defectRecords = new ArrayList<>();
    private List<String> defectTypes;
    private List<DbModel> userModels;


    @Override
    protected View getChildContentView() {
        defectControlBinding = ActivityDefectControlBinding.inflate(getLayoutInflater());
        return defectControlBinding.getRoot();
    }


    @Override
    public void initUI() {
        defectControlBinding.setEvent(this);
        setTitleText("缺陷管理");
    }

    @Override
    public void initData() {
        defectTypes = Arrays.asList(getResources().getStringArray(R.array.DefectTypeArray));
        ExecutorManager.executeTaskSerially(() -> {
            bdzList = BdzService.getInstance().findAllBdzByDpDbModel(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
            userModels = UserService.getInstance().getAllUserByDeptId(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
            mHandler.post(() -> {
                if (bdzList.size() > 0) {
                    bdzModel = bdzList.get(0);
                    defectControlBinding.bdzName.setText(bdzModel.getString(Bdz.NAME));
                }
                search();
            });
        });
    }

    private void search() {
        ExecutorManager.executeTaskSerially(() -> {
            defectRecords = DefectRecordService.getInstance().queryCurrentBdzExistDefectList(bdzModel == null ? "" : bdzModel.getString(Bdz.BDZID), defectLevel);
            runOnUiThread(() -> {
                if (defectContentAdapter == null) {
                    defectContentAdapter = new DefectContentAdapter(this, defectRecords);
                    defectControlBinding.lvDefect.setAdapter(defectContentAdapter);
                } else {
                    defectContentAdapter.notifyDataSetChanged();
                }
            });
        });
    }


    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bdz_contanier) {
            new PopWindowCustom.PopWindowBuilder<Bdz>(this).setWidth(defectControlBinding.bdzContanier.getWidth() + 60).setList(bdzList).setOutSideCancelable(true).setDbmodelKey(Bdz.NAME).setItemClickListener((adapter, view1, position) -> {
                ToastUtils.showMessage(bdzList.get(position).getString(Bdz.NAME));
                bdzModel = bdzList.get(position);
                search();
                defectControlBinding.bdzName.setText(bdzModel.getString(Bdz.NAME));
            }).setDropDownOfView(defectControlBinding.bdzContanier).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);
        } else if (i == R.id.defect_type_container) {
            new PopWindowCustom.PopWindowBuilder<Bdz>(this).setWidth(defectControlBinding.defectTypeContainer.getWidth() + 60).setList(defectTypes).setOutSideCancelable(true).setItemClickListener((adapter, view1, position) -> {
                ToastUtils.showMessage(defectTypes.get(position));
                defectControlBinding.defectType.setText(defectTypes.get(position));

            }).setDropDownOfView(defectControlBinding.defectTypeContainer).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);
        } else if (i == R.id.container_people) {
            new PopWindowCustom.PopWindowBuilder<Bdz>(this).setWidth(defectControlBinding.containerPeople.getWidth() + 60).setList(userModels).setOutSideCancelable(true).setDbmodelKey(Users.USERNAME).setItemClickListener((adapter, view1, position) -> {
                ToastUtils.showMessage(userModels.get(position).getString(Users.USERNAME));
                defectControlBinding.txtPeopleName.setText(userModels.get(position).getString(Users.USERNAME));

            }).setDropDownOfView(defectControlBinding.containerPeople).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);

        }
    }


}
