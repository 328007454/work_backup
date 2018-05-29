package com.cnksi.sjjc.activity.batteryactivity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cnksi.bdzinspection.daoservice.BatteryGroupService;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BatteryService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.listener.OnViewClickListener;
import com.cnksi.common.model.Battery;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseSjjcActivity;
import com.cnksi.sjjc.adapter.AddPeopleAdapter;
import com.cnksi.sjjc.adapter.ShowManagerAdapter;
import com.cnksi.sjjc.adapter.ShowPeopleAdapter;
import com.cnksi.sjjc.databinding.ActivityBatteryFinishBinding;
import com.cnksi.sjjc.databinding.DialogPeople;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * Created by han on 2016/8/10.
 * 蓄电池选择人员界面
 */
public class BatteryFinishActivity extends BaseSjjcActivity implements ItemClickListener {
    public static String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";
    public static String BATTERY_FINISH_ACTIVITY = "battery_finish_activity";


    private ArrayList<String> peopleList = new ArrayList<>();

    private ArrayList<String> showManagerList = new ArrayList<>();
    private ShowManagerAdapter showManagerAdapter;

    private ArrayList<String> showPeopleList = new ArrayList<>();
    private ShowPeopleAdapter showPeopleAdapter;
    //蓄电池基础信息
    private List<BatteryGroup> batteryGroupList;
    //后台配置的蓄电池组
    private List<Battery> batteryList;
    private BatteryGroup batteryGroup;
    private int totalCountUser;
    private List<String> czrModels = new ArrayList<>();
    private List<String> fzrModels = new ArrayList<>();

    private ActivityBatteryFinishBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBatteryFinishBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        initView();
        loadData();
        initOnClick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void initView() {
        mTitleBinding.tvTitle.setText("蓄电池检测完成");
        String[] users = PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "").split(",");
        for (String str : users) {
            showPeopleList.add(str);
            czrModels.add(str);
        }
        if (showManagerAdapter == null) {
            showManagerAdapter = new ShowManagerAdapter(mActivity, showManagerList, R.layout.name_show_layout);
        }

        binding.gvTestManager.setAdapter(showManagerAdapter);
        showManagerAdapter.setClickWidget(this);
        binding.gvTestManager.setOnItemClickListener((adapterView, view, i, l) -> {
            ImageView iv = view.findViewById(R.id.delete_imag);
            iv.setVisibility(View.VISIBLE);
        });
        if (showPeopleAdapter == null) {
            showPeopleAdapter = new ShowPeopleAdapter(mActivity, showPeopleList, R.layout.name_show_layout);
        }

        binding.gvTestPeople.setAdapter(showPeopleAdapter);
        showPeopleAdapter.setClickWidget(this);

        mTitleBinding.btnBack.setOnClickListener(v -> {
            BatteryFinishActivity.this.cacheBatteryInfor();
            BatteryFinishActivity.this.finish();
        });
    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(new Runnable() {
            List<DbModel> dbModelList = null;
            @Override
            public void run() {
                String account = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
                String[] accountS = account.split(",");
                totalCountUser = accountS.length;
                if (showPeopleAdapter != null) {
                    showPeopleAdapter.setUserCount(totalCountUser);
                }
                try {
                    batteryGroupList = BatteryGroupService.getInstance().getAllGroup(currentReportId);
                    if (batteryGroupList.size() > 0) {
                        batteryGroup = batteryGroupList.get(0);
                    }
                    batteryList = BatteryService.getInstance().getAllBattery(currentBdzId);

                    dbModelList = UserService.getInstance().getAllUser(account);
                    for (DbModel model : dbModelList) {
                        peopleList.add(model.getString("username"));
                    }
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                    dbModelList = new ArrayList<DbModel>();
//                    Log.i(BATTERY_FINISH_ACTIVITY, "加载数据错误");
                }
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (batteryGroup != null && !TextUtils.isEmpty(batteryGroup.testPersons)) {
                    showPeopleList = com.cnksi.core.utils.StringUtils.stringToList(batteryGroup.testPersons);
                    showPeopleAdapter.setList(showPeopleList);
                }
                if (batteryGroup != null && !TextUtils.isEmpty(batteryGroup.testManager)) {
                    showManagerList = com.cnksi.core.utils.StringUtils.stringToList(batteryGroup.testManager);
                    showManagerAdapter.setList(showManagerList);
                }
                if (batteryGroup != null) {

                    binding.etConclusion.setText(batteryGroup.analysisResult == null ? "" : batteryGroup.analysisResult);
                }

                break;
            default:
                break;
        }
    }

    private void initOnClick() {
        binding.tvManager.setOnClickListener(view -> BatteryFinishActivity.this.showPeopleDialog(MANAGER_FLAG));
        binding.tvPerson.setOnClickListener(view -> BatteryFinishActivity.this.showPeopleDialog(PEOPLE_FLAG));
        binding.btnNext.setOnClickListener(view -> {
            BatteryFinishActivity.this.cacheBatteryInfor();
            try {
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(mActivity, BatteryTestReportActivity.class);
            BatteryFinishActivity.this.startActivity(intent);
            ScreenManager.getScreenManager().popActivity(BatteryTestActivity.class);
            isNeedUpdateTaskStatus = true;
            BatteryFinishActivity.this.finish();
        });
    }


    private void cacheBatteryInfor() {
        if (batteryGroupList.isEmpty()) {
            BatteryGroup batteryGroup = null;
            Battery battery = batteryList.get(0);
            batteryGroup = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, battery.bid);
            saveGroupBaseInfor(batteryGroup);
        } else {
            for (BatteryGroup batteryGroup1 : batteryGroupList) {
                saveGroupBaseInfor(batteryGroup1);
            }
        }

    }

    /**
     * 保存数据
     */
    private void saveGroupBaseInfor(BatteryGroup batteryGroup) {
        if (showManagerList.size() > 0) {
            String managerStr = com.cnksi.core.utils.StringUtils.arrayListToString(showManagerList);
            batteryGroup.testManager = managerStr;
        }
        if (showPeopleList.size() > 0) {
            String peopleStr = com.cnksi.core.utils.StringUtils.arrayListToString(showPeopleList);
            batteryGroup.testPersons = peopleStr;
        }
        try {
            batteryGroup.analysisResult = binding.etConclusion.getText().toString();
            BatteryGroupService.getInstance().saveOrUpdate(batteryGroup);
//            Log.i("Test", "保存测试人员成功");
        } catch (DbException e) {
//            Log.i("Test", "保存测试人员失败");
            e.printStackTrace();
        }
    }

    private Dialog peopleDialog;
    private DialogPeople peopleBinding;
    private AddPeopleAdapter peopleAdapter;

    private void showPeopleDialog(final String flag) {

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
            if (flag.equalsIgnoreCase(MANAGER_FLAG)) {
                if (fzrModels.contains(name)) {
                    ToastUtils.showMessage("负责人中已经有该成员了");
                    return;
                } else {
                    fzrModels.add(name);
                }
                showManagerList.add(name);
                showManagerAdapter.setList(showManagerList);
            } else {
                if (czrModels.contains(name)) {
                    ToastUtils.showMessage("测试人中已经有该成员了");
                    return;
                } else {
                    czrModels.add(name);
                }
                showPeopleList.add(name);
                showPeopleAdapter.setList(showPeopleList);
            }

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

    @Override
    public void itemClick(final View view, final Object o, final int position) {
        if (view.getTag().equals(MANAGER_FLAG)) {
            if (showManagerList.size() == 1) {
                ToastUtils.showMessage( "至少要有一个测试负责人!");
                return;
            }
        } else {
            if (showPeopleList.size() == 1) {
                ToastUtils.showMessage( "至少要有一个负责人!");
                return;
            }
        }
        DialogUtils.showSureTipsDialog(mActivity, null, "是否确认删除 " + o + "?", "确认", "取消", new OnViewClickListener() {
            @Override
            public void onClick(View v) {
                if (view.getTag().equals(MANAGER_FLAG)) {
                    fzrModels.remove(position);
                    showManagerList.remove(o);
                    showManagerAdapter.notifyDataSetChanged();
                } else {
                    czrModels.remove(position);
                    showPeopleList.remove(o);
                    showPeopleAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }
}
