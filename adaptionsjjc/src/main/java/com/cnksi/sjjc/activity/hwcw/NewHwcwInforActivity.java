package com.cnksi.sjjc.activity.hwcw;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.AddPeopleAdapter;
import com.cnksi.sjjc.adapter.ShowPeopleAdapter;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ActivityNewhwcwInforBinding;
import com.cnksi.sjjc.databinding.DialogPeople;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.NewHwcwService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.GsonUtil;
import com.cnksi.sjjc.util.OnViewClickListener;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/13.
 */

public class NewHwcwInforActivity extends BaseActivity implements ItemClickListener {
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
        iniData();
    }

    private void iniData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            List<DbModel> dbModelList = null;

            @Override
            public void run() {
                mHwcwBaseInfo = NewHwcwService.getInstance().getBaseInfo(currentReportId);
                if (!TextUtils.isEmpty(mHwcwBaseInfo.id)) {
                    hotLocations = NewHwcwService.getInstance().getAllLocation(mHwcwBaseInfo.id);
                }
                String account = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_ACCOUNT, "");
                try {
                    dbModelList = UserService.getInstance().getAllUser(account);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                for (DbModel model : dbModelList) {
                    peopleList.add(model.getString("username"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initUI();
                    }
                });
            }
        });
    }

    private void initUI() {
        tvTitle.setText(currentBdzName + currentInspectionName + "记录");
        mInforBinding.setInfo(mHwcwBaseInfo);
        resolveHotPart();
        String[] users = new String[]{""};
        if (TextUtils.isEmpty(mHwcwBaseInfo.testPerson)) {
            users = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_USER, "").split(",");
        } else {
            users = mHwcwBaseInfo.testPerson.split(",");
        }
        for (String str : users) {
            showPeopleList.add(str);
            czrModels.add(str);
        }
        if (showPeopleAdapter == null) {
            showPeopleAdapter = new ShowPeopleAdapter(mCurrentActivity, showPeopleList, R.layout.name_show_layout);
        }
        mInforBinding.gvTestManager.setAdapter(showPeopleAdapter);
        showPeopleAdapter.setUserCount(1);
        showPeopleAdapter.setClickWidget(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                _this.finish();
            }
        });
    }

    private void resolveHotPart() {
        int i = 0;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        if (!hotLocations.isEmpty()) {
            for (HwcwLocation location : hotLocations) {
                String deviceName = location.deviceName;
                stringBuilder.append("\n发热设备:").append(deviceName).append("\n");
                HwcwHotPart hotParts = (HwcwHotPart) GsonUtil.resolveJson(location.hotPart);
                for (HwcwHotPart.Result result : hotParts.result) {
                    stringBuilder.append("发热部位名称：").append(result.bw_name).append("\n温度：").append(result.wd).append("\n");
                }
            }
            mInforBinding.etTempRecord.setText(stringBuilder.toString());
        }

    }

    boolean isUpdateReport = false;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_produce_record:
                isUpdateReport = true;
                saveData();
                isNeedUpdateTaskState = true;
                Intent intent = new Intent(_this, NewHwcwReportActivity.class);
                startActivity(intent);
                _this.finish();
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
        String testPeople = com.cnksi.core.utils.StringUtils.ArrayListToString(showPeopleList);
        mHwcwBaseInfo.setRecordData(testRange, testLocation, testProblem, testRemark, testPeople);
        try {
            CustomApplication.getDbManager().saveOrUpdate(mHwcwBaseInfo);
            if (isUpdateReport) {
                CustomApplication.getDbManager().update(Report.class, WhereBuilder.b(Report.REPORTID, "=", currentReportId), new KeyValue(Report.ENDTIME, DateUtils.getCurrentLongTime()));
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void itemClick(View v, final Object o, final int position) {
        if (showPeopleList.size() == 1) {
            CToast.showShort(mCurrentActivity, "至少要有一个负责人!");
            return;
        }
        DialogUtils.showSureTipsDialog(mCurrentActivity, null, "是否确认删除 " + (String) o + "?", "确认", "取消", new OnViewClickListener() {
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

        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 7 / 9;
        int dialogheight = ScreenUtils.getScreenHeight(mCurrentActivity) * 6 / 10;
        if (null == peopleDialog) {
            peopleBinding = (DialogPeople) DataBindingUtil.inflate(LayoutInflater.from(_this), R.layout.dialog_add_person, null, false);
        }
        if (peopleDialog == null) {
            peopleDialog = DialogUtils.creatDialog(mCurrentActivity, peopleBinding.getRoot(), dialogWidth, dialogheight);
        }
        if (peopleAdapter == null) {
            peopleAdapter = new AddPeopleAdapter(_this, peopleList, R.layout.text_view_layout);
        }
        peopleBinding.lvPeople.setAdapter(peopleAdapter);

        peopleBinding.lvPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = (String) adapterView.getItemAtPosition(i);
                peopleDialog.cancel();
                if (czrModels.contains(name)) {
                    CToast.showShort(_this, "测试人中已经有该成员了");
                    return;
                } else {
                    czrModels.add(name);
                }
                showPeopleList.add(name);
                showPeopleAdapter.setList(showPeopleList);
            }
        });

        peopleBinding.tvNewPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(peopleBinding.etName.getText().toString())) {
                    peopleList.add(peopleBinding.etName.getText().toString());
                    peopleAdapter.setList(peopleList);
                    peopleBinding.lvPeople.setSelection(peopleAdapter.getCount());
                    peopleBinding.etName.setText("");
                }
            }
        });

        peopleDialog.show();
    }
}
