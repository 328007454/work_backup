package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.AddPeopleAdapter;
import com.cnksi.sjjc.adapter.ShowManagerAdapter;
import com.cnksi.sjjc.adapter.ShowPeopleAdapter;
import com.cnksi.sjjc.bean.Battery;
import com.cnksi.sjjc.bean.BatteryGroup;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.DialogPeople;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.BatteryGroupService;
import com.cnksi.sjjc.service.BatteryService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.service.UserService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.OnViewClickListener;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/8/10.
 * 蓄电池选择人员界面
 */
public class BatteryFinishActivity extends BaseActivity implements ItemClickListener {
    public static String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";
    public static String BATTERY_FINISH_ACTIVITY = "battery_finish_activity";

    @ViewInject(R.id.et_conclusion)
    private EditText txtConclusion;

    @ViewInject(R.id.gv_test_manager)
    private GridView gridManager;

    @ViewInject(R.id.gv_test_people)
    private GridView gridPeople;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_battery_finish);
        x.view().inject(_this);
        getIntentValue();
        initUI();
        initData();
    }


    private void initUI() {
        tvTitle.setText("蓄电池检测完成");
        String[] users = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_USER, "").split(",");
        for (String str : users) {
            showPeopleList.add(str);
        }
        if (showManagerAdapter == null) {
            showManagerAdapter = new ShowManagerAdapter(mCurrentActivity, showManagerList, R.layout.name_show_layout);
        }
        gridManager.setAdapter(showManagerAdapter);
        showManagerAdapter.setClickWidget(this);
        gridManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView iv = (ImageView) view.findViewById(R.id.delete_imag);
                iv.setVisibility(View.VISIBLE);
            }
        });
        if (showPeopleAdapter == null) {
            showPeopleAdapter = new ShowPeopleAdapter(mCurrentActivity, showPeopleList, R.layout.name_show_layout);
        }
        gridPeople.setAdapter(showPeopleAdapter);
        showPeopleAdapter.setClickWidget(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheBatteryInfor();
                BatteryFinishActivity.this.finish();
            }
        });
    }


    private void initData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {
            List<DbModel> dbModelList = null;

            @Override
            public void run() {
                String account = PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_ACCOUNT, "");
                try {
                    batteryGroupList = BatteryGroupService.getInstance().getAllGroup(currentReportId);
                    if (batteryGroupList.size() > 0)
                        batteryGroup = batteryGroupList.get(0);
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
                if (!TextUtils.isEmpty(batteryGroup.testPersons)) {
                    showPeopleList = com.cnksi.core.utils.StringUtils.string2List(batteryGroup.testPersons);
                    showPeopleAdapter.setList(showPeopleList);
                }
                if (!TextUtils.isEmpty(batteryGroup.testManager)) {
                    showManagerList = com.cnksi.core.utils.StringUtils.string2List(batteryGroup.testManager);
                    showManagerAdapter.setList(showManagerList);
                }
                txtConclusion.setText(batteryGroup.analysisResult == null ? "" : batteryGroup.analysisResult);
                break;
            default:
                break;
        }
    }

    @Event({R.id.tv_manager, R.id.tv_person, R.id.btn_next})
    private void viewEvent(View view) {
        switch (view.getId()) {
            case R.id.tv_manager:

                showPeopleDialog(MANAGER_FLAG);
                break;
            case R.id.tv_person:
                showPeopleDialog(PEOPLE_FLAG);
                break;
            case R.id.btn_next:
                if (showPeopleList.size() < 1 || showManagerList.size() < 1) {
                    CToast.showShort(mCurrentActivity, "至少要有一个测试人和测试负责人!");
                    return;
                }
                cacheBatteryInfor();
                try {
                    TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(_this, BatteryTestReportActivity.class);
                startActivity(intent);
                ScreenManager.getScreenManager().popActivity(BatteryTestActivity.class);
                isNeedUpdateTaskState = true;
                this.finish();
            default:
                break;
        }
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
            String managerStr = com.cnksi.core.utils.StringUtils.ArrayListToString(showManagerList);
            batteryGroup.testManager = managerStr;
        }
        if (showPeopleList.size() > 0) {
            String peopleStr = com.cnksi.core.utils.StringUtils.ArrayListToString(showPeopleList);
            batteryGroup.testPersons = peopleStr;
        }
        try {
            batteryGroup.analysisResult = txtConclusion.getText().toString();
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
                if (flag.equalsIgnoreCase(MANAGER_FLAG)) {
                    showManagerList.add(name);
                    showManagerAdapter.setList(showManagerList);
                } else {
                    showPeopleList.add(name);
                    showPeopleAdapter.setList(showPeopleList);
                }

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

    @Override
    public void itemClick(final View view, final Object o, int position) {
        if (view.getTag().equals(MANAGER_FLAG)) {
            if (showManagerList.size() == 1) {
                CToast.showShort(mCurrentActivity, "至少要有一个测试负责人!");
                return;
            }
        } else {
            if (showPeopleList.size() == 1) {
                CToast.showShort(mCurrentActivity, "至少要有一个负责人!");
                return;
            }
        }
        DialogUtils.showSureTipsDialog(mCurrentActivity, null, "是否确认删除 " + (String) o + "?", "确认", "取消", new OnViewClickListener() {
            @Override
            public void onClick(View v) {
                if (view.getTag().equals(MANAGER_FLAG)) {
                    showManagerList.remove(o);
                    showManagerAdapter.notifyDataSetChanged();
                } else {
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
