package com.cnksi.defect.activity;

import android.content.Intent;
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
    private List<Bdz> bdzList = new ArrayList<>();
    private DefectContentAdapter defectContentAdapter;
    /**
     * 变电站实体model
     */
    private Bdz bdzModel;
    private int defectLevel = 0;
    private List<DefectRecord> defectRecords = new ArrayList<>();
    private List<String> defectTypes;
    private List<DbModel> userModels;
    private String userName = "全部";


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
        currentDepartmentName = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_NAME, "");
        defectTypes = Arrays.asList(getResources().getStringArray(R.array.DefectTypeArray));

        ExecutorManager.executeTaskSerially(() -> {
            bdzList = BdzService.getInstance().findAllBdzByDp(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
            bdzList.add(new Bdz("-1", "全部"));
            userModels = UserService.getInstance().getAllUserByDeptId(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
            DbModel userModel = new DbModel();
            userModel.add(Users.USERNAME, "全部");
            userModel.add(Users.ACCOUNT, "-1");
            userModels.add(userModel);
            mHandler.post(() -> {
                if (bdzList.size() > 0) {
                    bdzModel = bdzList.get(bdzList.size() - 1);
                    defectControlBinding.bdzName.setText(bdzModel.name);
                }
                defectControlBinding.txtPeopleName.setText(userModels.get(userModels.size() - 1).getString(Users.USERNAME));
                defectControlBinding.defectType.setText(defectTypes.get(0));
                search();
            });
        });
    }


    private void search() {
        ExecutorManager.executeTaskSerially(() -> {
            defectRecords = DefectRecordService.getInstance().queryCurrentBdzExistDefectList(bdzModel == null ? "" : bdzModel.bdzid, userName
                    , defectLevel, currentDepartmentName);
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
            new PopWindowCustom.PopWindowBuilder<Bdz>(this).setPopWindowBuilder(bdz -> bdz.name)
                    .setWidth(defectControlBinding.bdzContanier.getWidth() + 60)
                    .setList(bdzList)
                    .setOutSideCancelable(true).
                    setItemClickListener((adapter, view1, position) -> {
                        ToastUtils.showMessage(bdzList.get(position).name);
                        bdzModel = bdzList.get(position);
                        defectControlBinding.bdzName.setText(bdzModel.name);
                        search();
                    }).setDropDownOfView(defectControlBinding.bdzContanier).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);
        } else if (i == R.id.defect_type_container) {
            new PopWindowCustom.PopWindowBuilder<String>(this).setPopWindowBuilder(s -> s)
                    .setWidth(defectControlBinding.defectTypeContainer.getWidth() + 60)
                    .setList(defectTypes).setOutSideCancelable(true)
                    .setItemClickListener((adapter, view1, position) -> {
                        String defectType = defectTypes.get(position);
                        defectLevel = 2 * position;
                        ToastUtils.showMessage(defectType);
                        defectControlBinding.defectType.setText(defectTypes.get(position));
                        search();
                    }).setDropDownOfView(defectControlBinding.defectTypeContainer).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);
        } else if (i == R.id.container_people) {
            new PopWindowCustom.PopWindowBuilder<DbModel>(this).setPopWindowBuilder(model -> model.getString(Users.USERNAME))
                    .setWidth(defectControlBinding.containerPeople.getWidth() + 60)
                    .setList(userModels).setOutSideCancelable(true)
                    .setItemClickListener((adapter, view1, position) -> {
                        DbModel userModel = userModels.get(position);
                        userName = userModel.getString(Users.USERNAME);
                        ToastUtils.showMessage(userModel.getString(Users.USERNAME));
                        defectControlBinding.txtPeopleName.setText(userModels.get(position).getString(Users.USERNAME));
                        search();
                    }).setDropDownOfView(defectControlBinding.containerPeople).setBackgroundAlpha(0.6f).showAsDropDown(-30, 10);
        } else if (i == R.id.add_defect) {
            Intent intent = new Intent(this, AddDefecctActivity.class);
        }
    }


}
