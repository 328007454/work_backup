package com.cnksi.common.view;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.common.R;
import com.cnksi.common.adapter.InspectionTypeAdapter;
import com.cnksi.common.base.BaseDialogFragment;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.databinding.CommonDialogPersonBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * 巡视类型对话框
 *
 * @author Mr.K  on 2018/6/8.
 */

public class InspectionTypeDialog extends BaseDialogFragment {
    public interface DialogDissMissListener {
        void dialogDiss(List<DbModel> selectTypeModels);
    }

    private CommonDialogPersonBinding typeBinding;
    private static boolean multipleChoice;
    private InspectionTypeAdapter typeAdapter;
    private List<DbModel> selectTypeModels;
    private DialogDissMissListener dialogDissMissListener;

    public void setDialogDissMissListener(DialogDissMissListener dialogDissMissListener) {
        this.dialogDissMissListener = dialogDissMissListener;
    }


    @Override
    public void initUI() {
        typeBinding = (CommonDialogPersonBinding) dataBinding;
        if (multipleChoice) {
            typeBinding.btnConfirm.setVisibility(View.VISIBLE);
            typeBinding.btnConfirm.setOnClickListener(v -> {
                selectTypeModels = typeAdapter.getSelectModels();
                dismiss();
            });
        }
        typeBinding.containerName.setVisibility(View.GONE);
        typeBinding.txtTitle.setText("选择巡视类型");
    }

    @Override
    public int setLayout() {
        return R.layout.common_dialog_person;
    }


    public List<DbModel> getSelectTypeModels() {
        return selectTypeModels;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ScreenUtils.getScreenWidth(getContext()) * 8 / 9, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class Builder {
        private InspectionTypeDialog typeDialog;
        private InspectionTypeAdapter typeAdapter;
        private ItemClickListener<DbModel> itemClickListener;
        private String bdzId;
        private String currentInspection;
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
            typeDialog = new InspectionTypeDialog();
        }

        public Builder(Activity activity, boolean multipleChose) {
            this.activity = activity;
            InspectionTypeDialog.multipleChoice = multipleChose;
            typeDialog = new InspectionTypeDialog();
        }

        public Builder setBdzId(String bdzId) {
            this.bdzId = bdzId;
            return this;
        }

        public Builder setInspection(String currentInspection) {
            this.currentInspection = currentInspection;
            return this;
        }


        public Builder setItemClickListener(ItemClickListener<DbModel> itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public InspectionTypeDialog getTypeDialog() {
            return typeDialog;
        }


        public Builder loadData() {
            ExecutorManager.executeTaskSerially(() -> {
                List<DbModel> usersList = LookupService.getInstance().findAllTypeModel(bdzId, currentInspection);
                activity.runOnUiThread(() -> {
                    typeDialog.typeBinding.lvContainer.setLayoutManager(new LinearLayoutManager(typeDialog.getContext()));
                    typeAdapter = new InspectionTypeAdapter(R.layout.double_textview_item, usersList);
                    typeDialog.typeAdapter = typeAdapter;
                    typeAdapter.setMultiChoice(multipleChoice);
                    if (!multipleChoice) {
                        typeAdapter.setOnItemClickListener((adapter, view, position) -> {
                            if (itemClickListener != null) {
                                itemClickListener.onClick(view, (DbModel) adapter.getItem(position), position);
                            }
                        });
                    }
                    typeDialog.typeBinding.lvContainer.setAdapter(typeAdapter);
                });
            });
            return this;
        }

    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (dialogDissMissListener != null) {
            dialogDissMissListener.dialogDiss(selectTypeModels);
        }
    }
}
