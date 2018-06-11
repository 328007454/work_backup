package com.cnksi.common.view;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.cnksi.common.R;
import com.cnksi.common.adapter.PeopleAdapter;
import com.cnksi.common.base.BaseDialogFragment;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.databinding.CommonDialogPersonBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.BaseModel;
import com.cnksi.common.model.ReportSignname;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * 人员对话框
 *
 * @author Mr.K  on 2018/6/8.
 */

public class PeopleDialog extends BaseDialogFragment {

    private CommonDialogPersonBinding personBinding;


    @Override
    public void initData() {

    }

    @Override
    public int setLayout() {
        return R.layout.common_dialog_person;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ScreenUtils.getScreenWidth(getContext()) * 7 / 9, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class Builder {
        private String departmentId;
        private PeopleDialog peopleDialog;
        private PeopleAdapter peopleAdapter;
        private ItemClickListener<DbModel> itemClickListener;

        public Builder() {
            peopleDialog = new PeopleDialog();
        }

        public Builder setDeptId(String departmentId) {
            this.departmentId = departmentId;
            return this;
        }


        public Builder setItemClickListener(ItemClickListener<DbModel> itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public PeopleDialog getPeopleDialog() {
            return peopleDialog;
        }


        public Builder loadData() {
            ExecutorManager.executeTaskSerially(() -> {
                List<DbModel> usersList = UserService.getInstance().getAllUserByDeptId(departmentId);
                peopleDialog.getActivity().runOnUiThread(() -> {
                    peopleDialog.personBinding = (CommonDialogPersonBinding) peopleDialog.dataBinding;
                    peopleDialog.personBinding.lvContainer.setLayoutManager(new LinearLayoutManager(peopleDialog.getContext()));
                    peopleDialog.personBinding.btnAddPerson.setOnClickListener(v -> {
                        String newName = peopleDialog.personBinding.etName.getText().toString().trim();
                        if (TextUtils.isEmpty(newName)) {
                            ToastUtils.showMessage("请输入名字");
                            return;
                        }
                        DbModel model = new DbModel();
                        model.add("username", newName);
                        model.add(ReportSignname.DEPTID, "-1");
                        model.add(ReportSignname.DEPTNAME, "");
                        model.add(ReportSignname.ACCOUNT, BaseModel.getPrimarykey());
                        usersList.add(model);
                        peopleAdapter.setData(usersList.size() - 1, model);
                        peopleDialog.personBinding.lvContainer.scrollToPosition(usersList.size() - 1);
                        peopleDialog.personBinding.etName.setText("");
                    });
                    peopleAdapter = new PeopleAdapter(R.layout.textview_item, usersList);
                    peopleAdapter.setOnItemClickListener((adapter, view, position) -> {
                        if (itemClickListener != null) {
                            itemClickListener.onClick(view, (DbModel) adapter.getItem(position), position);
                        }
                    });
                    peopleDialog.personBinding.lvContainer.setAdapter(peopleAdapter);
                });
            });
            return this;
        }

    }


}
