package com.cnksi.common.utils;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.R;
import com.cnksi.common.adapter.SelectionPersonAdapter;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.view.PeopleDialog;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @decrption 任务添加分组
 * Created by kkk on 2017/12/29.
 */

public class SelectPersonUtil implements ItemClickListener {
    public static SelectPersonUtil selectPersonUtil;
    private SelectionPersonAdapter mPersonAdapter;
    private Activity mActivity;
    private List<DbModel> selecModels = new ArrayList<>();
    private RecyclerView selectRecy;
    private List<DbModel> membersModels = new ArrayList<>();
    private String currentDeptId;
    private FragmentManager fragmentManager = null;

    public static SelectPersonUtil getInstance() {
        if (selectPersonUtil == null) {
            selectPersonUtil = new SelectPersonUtil();
        }
        return selectPersonUtil;
    }

    public SelectPersonUtil() {
    }

    public SelectPersonUtil setRecyWidget(Activity activity, FragmentManager fragmentManager, RecyclerView view, List<DbModel> data, RecyclerView.LayoutManager layoutManager, String deptId) {
        selecModels.clear();
        selecModels.addAll(data);
        for (DbModel dbModel : selecModels) {
            dbModel.add("delete", "false");
        }
        if (mPersonAdapter == null) {
            mPersonAdapter = new SelectionPersonAdapter(R.layout.common_item_task_user, selecModels);
            mPersonAdapter.setOnItemClickListener(this);
        }
        mActivity = activity;
        view.setLayoutManager(layoutManager);
        view.setAdapter(mPersonAdapter);
        this.selectRecy = view;
        this.currentDeptId = deptId;
        this.fragmentManager = fragmentManager;
        return this;
    }


    public void disPlayAllPerson(View showAddPersonWidget) {
        showAddPersonWidget.setOnClickListener(view -> showAddPersonDialog());
    }


    PeopleDialog peopleDialog;

    public void showAddPersonDialog() {
        peopleDialog = new PeopleDialog.Builder().setDeptId(currentDeptId).setItemClickListener((v, data, position) -> {
            peopleDialog.dismiss();
            getClickPerson(v, data, position);
        }).loadData().getPeopleDialog();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        peopleDialog.show(fragmentTransaction, "peopleDialog");
    }

    private void getClickPerson(View v, DbModel data, int position) {
        boolean isAlreadyExist = false;
        for (DbModel selectModel : selecModels) {
            if (TextUtils.equals(selectModel.getString("account"), data.getString("account"))) {
                ToastUtils.showMessage("已经存在该人员，请勿重复添加");
                isAlreadyExist = true;
                break;
            }
        }
        if (!isAlreadyExist) {
            data.add("delete", "true");
            selecModels.add(data);
            membersModels.add(data);
            mPersonAdapter.notifyDataSetChanged();
        }
    }


    public List<DbModel> getAllSelectUser() {
        return selecModels;
    }

    @Override
    public void onClick(View v, Object data, int position) {
        DbModel model = (DbModel) data;
        int i = v.getId();
        if (i == R.id.iv_delete) {
            selecModels.remove(position);
            membersModels.remove(model);
            mPersonAdapter.notifyDataSetChanged();
        }
    }
}
