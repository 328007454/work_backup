package com.cnksi.sjjc.activity.gztz;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.AddPeopleAdapter;
import com.cnksi.sjjc.adapter.ShowPeopleAdapter;
import com.cnksi.sjjc.bean.ReportSignname;
import com.cnksi.sjjc.databinding.ActivityGztzRecordBinding;
import com.cnksi.sjjc.databinding.DialogPeople;
import com.cnksi.sjjc.databinding.HomePageIncludeBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.util.DialogUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 故障跳闸记录情况
 *
 * @author kkk on 2018/3/7.
 */

public class GZTZRecordActivity extends BaseActivity {

    private ActivityGztzRecordBinding mRecordBinding;
    private ShowPeopleAdapter showPeopleAdapter;
    private ArrayList<String> showPeopleList = new ArrayList<>();
    private ArrayList<String> peopleList = new ArrayList<>();
    private Dialog peopleDialog;
    private DialogPeople peopleBinding;
    private AddPeopleAdapter peopleAdapter;
    private List<DbModel> selectDbModel = new ArrayList<>();
    private List<DbModel> dbModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordBinding = DataBindingUtil.setContentView(_this, R.layout.activity_gztz_record);
        initUI();
        initData();
    }

    private void initUI() {
        getIntentValue();
        mRecordBinding.includeTitle.tvTitle.setText(currentBdzName+"设备跳闸情况记录");
        if (showPeopleAdapter == null) {
            showPeopleAdapter = new ShowPeopleAdapter(mCurrentActivity, showPeopleList, R.layout.name_show_layout);
        }
        mRecordBinding.gvPeople.setAdapter(showPeopleAdapter);
        mRecordBinding.txtAddPerson.setOnClickListener(view -> showPeopleDialog());
        mRecordBinding.includeTitle.btnBack.setOnClickListener(view -> this.finish());
        mRecordBinding.btnSave.setOnClickListener(view -> {
            saveData();
        });
        showPeopleAdapter.setClickWidget(new ItemClickListener() {
            @Override
            public void itemClick(View v, Object o, int position) {
                showPeopleList.remove(position);
                selectDbModel.remove(position);
                showPeopleAdapter.notifyDataSetChanged();
            }

            @Override
            public void itemLongClick(View v, Object o, int position) {

            }
        });
    }


    private void initData() {
        mFixedThreadPoolExecutor.execute(() -> {
            try {
                String account = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_ACCOUNT, "");
                dbModelList = UserService.getInstance().getAllUser(account);
                if (null != dbModelList && !dbModelList.isEmpty()) {
                    for (DbModel model : dbModelList) {
                        if (account.contains(model.getString("account"))) {
                            showPeopleList.add(model.getString("username"));
                            selectDbModel.add(model);
                        }
                        peopleList.add(model.getString("username"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                showPeopleAdapter.setList(showPeopleList);
            });
        });
    }

    /**
     * 保存数据
     */
    public void saveData() {
        for (DbModel model : selectDbModel) {
            try {
                ReportSignname signname = new ReportSignname(model, currentReportId);
                CustomApplication.getDbManager().saveOrUpdate(signname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 选择检查人员对话框
     */
    private void showPeopleDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 7 / 9;
        int dialogheight = ScreenUtils.getScreenHeight(mCurrentActivity) * 6 / 10;
        if (null == peopleDialog) {
            peopleBinding = DataBindingUtil.inflate(LayoutInflater.from(_this), R.layout.dialog_add_person, null, false);
        }
        if (peopleDialog == null) {
            peopleDialog = DialogUtils.creatDialog(mCurrentActivity, peopleBinding.getRoot(), dialogWidth, dialogheight);
        }
        if (peopleAdapter == null) {
            peopleAdapter = new AddPeopleAdapter(_this, peopleList, R.layout.text_view_layout);
        }
        peopleBinding.lvPeople.setAdapter(peopleAdapter);

        peopleBinding.lvPeople.setOnItemClickListener((adapterView, view, i, l) -> {
            String name = (String) adapterView.getAdapter().getItem(i);
            showPeopleList.add(name);
            showPeopleAdapter.notifyDataSetChanged();
            selectDbModel.add(dbModelList.get(i));
            peopleDialog.dismiss();
        });

        peopleBinding.tvNewPerson.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(peopleBinding.etName.getText().toString())) {
                peopleList.add(peopleBinding.etName.getText().toString());
                peopleAdapter.setList(peopleList);
                peopleBinding.lvPeople.setSelection(peopleAdapter.getCount());
                peopleBinding.etName.setText("");
            }
        });
        peopleDialog.show();
    }
}