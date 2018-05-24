package com.cnksi.bdzinspection.utils;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.AddPersonAdapter;
import com.cnksi.bdzinspection.adapter.SelectionPersonAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogAddPersonBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkk on 2017/12/29.
 */

public class SelectPersonUtil implements ItemClickListener {
    public static SelectPersonUtil selectPersonUtil;
    private SelectionPersonAdapter mPersonAdapter;
    private Dialog mAddPersonDialog;
    private AddPersonAdapter dialogAdpeter;
    private Activity mActivity;
    private List<DbModel> selecModels = new ArrayList<>();
    private List<DbModel> allUsers = new ArrayList<>();
    private RecyclerView selectRecy;
    private List<DbModel> membersModels = new ArrayList<>();

    public static SelectPersonUtil getInstance() {
        if (selectPersonUtil == null) {
            selectPersonUtil = new SelectPersonUtil();
        }
        return selectPersonUtil;
    }

    public SelectPersonUtil() {
    }

    public SelectPersonUtil setRecyWidget(Activity activity, RecyclerView view, List<DbModel> data, int layoutID, RecyclerView.LayoutManager layoutManager) {
        selecModels.addAll(data);
        for (DbModel dbModel : selecModels) {
            dbModel.add("delete", "false");
        }
        if (mPersonAdapter == null) {
            mPersonAdapter = new SelectionPersonAdapter(view, selecModels, layoutID);
            mPersonAdapter.setOnItemClickListener(this);
        }
        this.mActivity = activity;
        view.setLayoutManager(layoutManager);
        view.setAdapter(mPersonAdapter);
        this.selectRecy = view;


        return this;
    }


    public void disPlayAllPerson(final List<DbModel> datas, View showAddPersonWidget) {
        this.allUsers = datas;
        showAddPersonWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPersonDialog();
            }
        });
    }


    private XsDialogAddPersonBinding addPersonBinding;

    public void showAddPersonDialog() {
        if (mAddPersonDialog == null) {
            int dialogWidth = com.cnksi.core.utils.ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(mActivity) * 6 / 10;
            addPersonBinding = XsDialogAddPersonBinding.inflate(LayoutInflater.from(mActivity));
            mAddPersonDialog = DialogUtils.createDialog(mActivity, addPersonBinding.getRoot(), dialogWidth, dialogHeight);
        }
        dialogAdpeter = new AddPersonAdapter(mActivity, allUsers);
        dialogAdpeter.setOnItemClickListener(this);
        addPersonBinding.lvContainer.setAdapter(dialogAdpeter);
        mAddPersonDialog.show();


        addPersonBinding.btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(addPersonBinding.etName.getText().toString())) {
                    ToastUtils.showMessage("请输入增加人员姓名");
                } else {
                    DbModel dbModel = new DbModel();
                    dbModel.add("account", "-1");
                    dbModel.add("username", addPersonBinding.etName.getText().toString());
                    dbModel.add("name", "");
                    dbModel.add("dept_id", "-1");
                    dbModel.add("delete", "true");
                    allUsers.add(dbModel);
                    addPersonBinding.etName.setText("");
                    addPersonBinding.lvContainer.smoothScrollToPosition(allUsers.size() - 1);
                    dialogAdpeter.notifyDataSetChanged();

                }
            }
        });
    }

    @Override
    public void onItemClick(View v, Object o, int position) {
        DbModel model = (DbModel) o;
        int i = v.getId();
        if (i == R.id.ll_container) {
            DbModel userModel = (DbModel) o;
            boolean isAreadyExist = false;
            for (DbModel seletModel : selecModels) {
                if (TextUtils.equals(seletModel.getString("account"), userModel.getString("account"))) {
                    ToastUtils.showMessage("已经存在该人员，请勿重复添加");
                    isAreadyExist = true;
                    break;
                }
            }
            if (!isAreadyExist) {
                userModel.add("delete", "true");
                selecModels.add(userModel);
                membersModels.add(userModel);
                mPersonAdapter.notifyDataSetChanged();
                mAddPersonDialog.dismiss();
            }


        } else if (i == R.id.iv_delete) {
            selecModels.remove(position);
            membersModels.remove(model);
            mPersonAdapter.notifyDataSetChanged();

        } else {
        }

    }

    public List<DbModel> getAllSelectUser() {
        return selecModels;
    }
//
//    private HashMap<String, String> nameMap = new HashMap<>();
//
//    public HashMap<String, String> getAllNameMap() {
//
//        return nameMap;
//    }

}
