package com.cnksi.common.view;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.common.R;
import com.cnksi.common.adapter.BdzAdapter;
import com.cnksi.common.base.BaseDialogFragment;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.databinding.CommonDialogPersonBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Bdz;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;

import java.util.List;

/**
 * 变电站对话框
 *
 * @author Mr.K  on 2018/6/8.
 */

public class BdzDialog extends BaseDialogFragment {

    private CommonDialogPersonBinding bdzBinding;

    @Override
    public void initUI() {
        bdzBinding = (CommonDialogPersonBinding) dataBinding;
        bdzBinding.containerName.setVisibility(View.GONE);
        bdzBinding.txtTitle.setText("选择变电站");
    }

    @Override
    public int setLayout() {
        return R.layout.common_dialog_person;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ScreenUtils.getScreenWidth(getContext())*8/9, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class Builder {
        private String departmentId;
        private BdzDialog bdzDialog;
        private BdzAdapter bdzAdapter;
        private ItemClickListener<Bdz> itemClickListener;
        private Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
            bdzDialog = new BdzDialog();
        }

        public Builder setDeptId(String departmentId) {
            this.departmentId = departmentId;
            return this;
        }


        public Builder setItemClickListener(ItemClickListener<Bdz> itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public BdzDialog getBdzDialog() {
            return bdzDialog;
        }


        public Builder loadData() {
            ExecutorManager.executeTaskSerially(() -> {
                List<Bdz> usersList = BdzService.getInstance().findAllBdzByDp(departmentId);
                activity.runOnUiThread(() -> {
                    bdzDialog.bdzBinding = (CommonDialogPersonBinding) bdzDialog.dataBinding;
                    bdzDialog.bdzBinding.lvContainer.setLayoutManager(new LinearLayoutManager(bdzDialog.getContext()));
                    bdzAdapter = new BdzAdapter(R.layout.textview_item, usersList);
                    bdzAdapter.setOnItemClickListener((adapter, view, position) -> {
                        if (itemClickListener != null) {
                            itemClickListener.onClick(view, (Bdz) adapter.getItem(position), position);
                        }
                    });
                    bdzDialog.bdzBinding.lvContainer.setAdapter(bdzAdapter);
                });
            });
            return this;
        }

    }


}
