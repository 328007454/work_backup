package com.cnksi.sjjc.activity.hwcw;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.listener.OnViewClickListener;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseTitleActivity;
import com.cnksi.sjjc.adapter.AddPeopleAdapter;
import com.cnksi.sjjc.adapter.ShowPeopleAdapter;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ActivityNewhwcwInforBinding;
import com.cnksi.sjjc.databinding.DialogPeople;
import com.cnksi.sjjc.service.NewHwcwService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/13.
 */

public class NewHwcwInforActivity extends BaseTitleActivity implements ItemClickListener {
    ActivityNewhwcwInforBinding mInforBinding;
    private List<HwcwLocation> hotLocations = new ArrayList<>();
    private HwcwBaseInfo mHwcwBaseInfo;
    private ArrayList<String> showPeopleList = new ArrayList<>();
    private List<String> czrModels = new ArrayList<>();
    private ShowPeopleAdapter showPeopleAdapter;
    private ArrayList<String> peopleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInforBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_newhwcw_infor, null, false);
        setChildView(mInforBinding.getRoot());
        getIntentValue();
        loadData();
    }

    @Override
    public void initUI() {

    }

    private void loadData() {
        ExecutorManager.executeTaskSerially(new Runnable() {
            List<DbModel> dbModelList = null;

            @Override
            public void run() {
                mHwcwBaseInfo = NewHwcwService.getInstance().getBaseInfo(currentReportId);
                if (!TextUtils.isEmpty(mHwcwBaseInfo.id)) {
                    hotLocations = NewHwcwService.getInstance().getAllLocation(mHwcwBaseInfo.id);
                }
                String account = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
                try {
                    dbModelList = UserService.getInstance().getAllUser(account);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                for (DbModel model : dbModelList) {
                    peopleList.add(model.getString("username"));
                }
                runOnUiThread(() -> initView());
            }
        });
    }

    public void initView() {
        mTitleBinding.tvTitle.setText(currentBdzName + currentInspectionTypeName + "记录");
        resolveHotPart();
        String[] users;
        if (TextUtils.isEmpty(mHwcwBaseInfo.testPerson)) {
            users = PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "").split(",");
        } else {
            users = mHwcwBaseInfo.testPerson.split(",");
        }
        for (String str : users) {
            showPeopleList.add(str);
            czrModels.add(str);
        }
        if (showPeopleAdapter == null) {
            showPeopleAdapter = new ShowPeopleAdapter(mActivity, showPeopleList, R.layout.name_show_layout);
        }
        mInforBinding.gvTestManager.setAdapter(showPeopleAdapter);
        showPeopleAdapter.setUserCount(1);
        showPeopleAdapter.setClickWidget(this);
        mTitleBinding.btnBack.setOnClickListener(view -> {
            saveData();
            setResult(RESULT_OK);
            mActivity.finish();
        });
    }

    @Override
    public void initData() {

    }

    private void resolveHotPart() {
        int i = 0;
        List<String> spaces = new ArrayList<>();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        if (!hotLocations.isEmpty()) {
            for (HwcwLocation location : hotLocations) {
                if (!TextUtils.isEmpty(location.spacingName) && !spaces.contains(location.spacingName)) {
                    spaces.add(location.spacingName);
                }
                String deviceName = location.deviceName;
                stringBuilder.append("\n发热设备:").append(deviceName).append("\n");
                HwcwHotPart hotParts = JSONObject.parseObject(location.hotPart, HwcwHotPart.class);
                if (hotParts != null && hotParts.result != null && !hotParts.result.isEmpty()) {
                    for (HwcwHotPart.Result result : hotParts.result) {
                        stringBuilder.append("发热部位名称：").append(result.bw_name).append("\n温度：").append(result.wd).append("(℃)\n");
                    }
                }
            }
            mInforBinding.etTempRecord.setText(stringBuilder.toString());
            if (TextUtils.isEmpty(mHwcwBaseInfo.problem)) {
                mInforBinding.etProblem.setText(stringBuilder.toString());
            }
            mHwcwBaseInfo.problem = stringBuilder.toString();
            if (!spaces.isEmpty()) {
                mHwcwBaseInfo.testLocation = StringUtils.arrayListToString(spaces);
            }
        } else {
            mInforBinding.etProblem.setText("无");
            mInforBinding.etLocation.setText("全站间隔设备");
        }
        mInforBinding.setInfo(mHwcwBaseInfo);
    }

    boolean isUpdateReport = false;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_produce_record:
                isUpdateReport = true;
                saveData();
                isNeedUpdateTaskStatus = true;
                Intent intent = new Intent(mActivity, NewHwcwReportActivity.class);
                startActivity(intent);
                mActivity.finish();
                break;
            case R.id.txt_new_person:
                showPeopleDialog();
                break;
            default:
                break;
        }

    }

    private void saveData() {
        String testRange = mInforBinding.etTestRange.getText().toString();
        String testLocation = mInforBinding.etLocation.getText().toString();
        String testProblem = mInforBinding.etProblem.getText().toString();
        String testRemark = mInforBinding.etMark.getText().toString();
        String testPeople = StringUtils.arrayListToString(showPeopleList);
        mHwcwBaseInfo.setRecordData(testRange, testLocation, testProblem, testRemark, testPeople);
        try {
            CustomApplication.getInstance().getDbManager().saveOrUpdate(mHwcwBaseInfo);
            if (isUpdateReport) {
                CustomApplication.getInstance().getDbManager().update(Report.class, WhereBuilder.b(Report.REPORTID, "=", currentReportId), new KeyValue(Report.ENDTIME, DateUtils.getCurrentLongTime()));
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClick(View v, final Object o, final int position) {
        if (showPeopleList.size() == 1) {
            ToastUtils.showMessage("至少要有一个负责人!");
            return;
        }
        DialogUtils.showSureTipsDialog(mActivity, null, "是否确认删除 " + o + "?", "确认", "取消", new OnViewClickListener() {
            @Override
            public void onClick(View v) {
                czrModels.remove(position);
                showPeopleList.remove(o);
                showPeopleAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }

    private Dialog peopleDialog;
    private DialogPeople peopleBinding;
    private AddPeopleAdapter peopleAdapter;

    private void showPeopleDialog() {

        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 7 / 9;
        int dialogheight = ScreenUtils.getScreenHeight(mActivity) * 6 / 10;
        if (null == peopleDialog) {
            peopleBinding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_add_person, null, false);
        }
        if (peopleDialog == null) {
            peopleDialog = DialogUtils.creatDialog(mActivity, peopleBinding.getRoot(), dialogWidth, dialogheight);
        }
        if (peopleAdapter == null) {
            peopleAdapter = new AddPeopleAdapter(mActivity, peopleList, R.layout.text_view_layout);
        }
        peopleBinding.lvPeople.setAdapter(peopleAdapter);

        peopleBinding.lvPeople.setOnItemClickListener((adapterView, view, i, l) -> {
            String name = (String) adapterView.getItemAtPosition(i);
            peopleDialog.cancel();
            if (czrModels.contains(name)) {
                ToastUtils.showMessage("测试人中已经有该成员了");
                return;
            } else {
                czrModels.add(name);
            }
            showPeopleList.add(name);
            showPeopleAdapter.setList(showPeopleList);
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
