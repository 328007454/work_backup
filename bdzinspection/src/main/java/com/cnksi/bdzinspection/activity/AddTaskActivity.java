package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.XunJianTypeAdapter;
import com.cnksi.bdzinspection.adapter.addtask.BdzDialogAdapter;
import com.cnksi.bdzinspection.adapter.addtask.InspectionTypeAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.LookupService;
import com.cnksi.bdzinspection.daoservice.SpecialMenuService;
import com.cnksi.bdzinspection.daoservice.SwitchMenuService;
import com.cnksi.bdzinspection.databinding.XsActivityAddInspectionTaskBinding;
import com.cnksi.bdzinspection.databinding.XsActivityAddinpsectionTypeDialogBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.model.TaskExtend;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.MyUUID;
import com.cnksi.bdzinspection.utils.SelectPersonUtil;
import com.cnksi.bdzinspection.ywyth.YWDeviceListActivity;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 追加巡检任务
 *
 * @author terry
 */
public class AddTaskActivity extends BaseActivity {

    public static final int LOAD_BDZ_DATA = 0x111;
    public static final int LOAD_INSPECTION_TYPE_DATA = LOAD_BDZ_DATA + 1;
    public static final int SAVE_TASK_REQUEST_CODE = LOAD_INSPECTION_TYPE_DATA + 1;
    public static final int INIT_INSPECTION_TYPE_CODE = SAVE_TASK_REQUEST_CODE + 1;
    public static final int ADD_YUNWEI_TASK_CODE = INIT_INSPECTION_TYPE_CODE + 1;
    public static final int MUMAL_SELECT_DEVICE = ADD_YUNWEI_TASK_CODE + 1;
    public static final int INIT_PERSONS = MUMAL_SELECT_DEVICE + 1;
    public static final int TASK_ALREADY = INIT_PERSONS + 1;
    /**
     * 巡检类型dialog
     */
    private Dialog mInspectionTypeDialog = null;
    private InspectionTypeAdapter mInspectionTypeAdapter = null;
    private XunJianTypeAdapter mXunJianTypeAdapter;
    /**
     * 变电站
     */
    private Dialog mPowerStationDialog = null;

    /**
     * 变电站集合
     */
    private List<Bdz> mBdzList = null;
    /**
     * 是否数据准备好
     */
    private boolean isBdzDataPrepared = false;
    /**
     * 当前选择的变电站
     */
    private Bdz mCurrentBdz = null;

    /**
     * 巡检类型的集合
     */
    private LinkedList<Lookup> groupList = null;
    private HashMap<Lookup, ArrayList<Lookup>> groupHashMap = null;
    private boolean isInspectionTypeDataPrepared = false;
    /**
     * 当前选中的巡检类型
     */
    private Lookup mInspectionType = null;
    /**
     * 当前生成的Task
     */
    private Task mCurrentTask = null;

    private Report mReport = null;

    /**
     * 主菜单界面点击的巡检类型
     */
    private int currentSelectInspectionType = -1;
    private String currentTypeStr = "";
    private String currentTypeBatteryStr = "";
    private XsActivityAddInspectionTaskBinding binding;
    private List<DbModel> allPersons;
    private List<DbModel> selectPersons;
    private String currentAcounts;
    private ArrayList<Lookup> childList;
    private List<Lookup> switchMaintenances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_add_inspection_task);
        initialUI();
        initialData();
        initOnClick();
    }

    private void initialUI() {

        binding.includeTitle.tvTitle.setText(R.string.xs_add_inspection_task_str);
        binding.tvInspectionDate.setText(DateUtils.getCurrentShortTime());

        // 筛选当前选中的是哪一个巡检类型
        currentSelectInspectionType = getIntent().getIntExtra(Config.CURRENT_INSPECTION_TYPE, -1);
        currentTypeStr = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        if (TextUtils.isEmpty(currentTypeStr)) {
            currentSelectInspectionType = -1;
        } else {
            currentSelectInspectionType = 1;
        }
        boolean seletedPersons = TextUtils.equals(currentTypeStr, InspectionType.full.name()) || TextUtils.equals(currentTypeStr, InspectionType.routine.name()) || (currentTypeStr.contains(InspectionType.special.name())) || TextUtils.equals(currentTypeStr, InspectionType.professional.name());
        if (seletedPersons) {
            binding.llPersons.setVisibility(View.VISIBLE);
        }


    }

    private void initialData() {
        mHandler.postDelayed(() -> ExecutorManager.executeTask(new Runnable() {
            @Override
            public void run() {
                initBdzData();
                initInspectionTypeData();
                initInspectionTypeChildData();

            }
        }), 0);

    }

    /**
     * 首先加载变电站数据
     */
    private void initBdzData() {
        try {
            String bdzId = PreferencesUtils.get(Config.LASTTIEM_CHOOSE_BDZNAME, "");
            if (!TextUtils.isEmpty(bdzId)) {
                mCurrentBdz = XunshiApplication.getDbUtils().selector(Bdz.class).where(Bdz.BDZID, "=", bdzId).and(Bdz.DLT, "=", 0).findFirst();
            }
            mBdzList = XunshiApplication.getDbUtils().selector(Bdz.class).where(Bdz.DLT, "=", "0").findAll();
            mHandler.sendEmptyMessage(LOAD_BDZ_DATA);
            currentAcounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
            allPersons = DepartmentService.getInstance().findAllUserForCurrentUser(currentAcounts);
            selectPersons = DepartmentService.getInstance().findUserForCurrentUser(currentAcounts);
            mHandler.sendEmptyMessage(INIT_PERSONS);

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载巡检类型
     */
    public void initInspectionTypeData() {
        try {
            // 如果没指定类型，则查全部
            if (currentSelectInspectionType == -1) {
                groupList = new LinkedList<Lookup>(
                        LookupService.getInstance().findLookupByTypeWithOutLooId(LookUpType.inspection.name()));
            } else {
                // 如果当前类型为“定期切换实验”，则查定期切换以及蓄电池实验
                if (InspectionType.switchover.equals(currentTypeStr)) {
                    groupList = new LinkedList<Lookup>(LookupService.getInstance()
                            .findLookupByTypeWithOutLooId(LookUpType.inspection.name(), currentTypeStr));
                } else if (InspectionType.special.equals(currentTypeStr)) {
                    groupList = new LinkedList<Lookup>(LookupService.getInstance()
                            .findLookupByTypeWithOutLooId(LookUpType.inspection.name(), currentTypeStr));
                } else {
                    // 只查当前选择的巡检类型
                    groupList = new LinkedList<Lookup>(LookupService.getInstance()
                            .findLookupByTypeWithOutLooId(LookUpType.inspection.name(), currentTypeStr));
                }
            }
            if (groupList != null && groupList.size() == 1) {
                Lookup mGroupLookUp = groupList.get(0);
                if ((InspectionType.switchover.name().equals(currentTypeStr) || InspectionType.maintenance.name().equals(currentTypeStr)) && null != mCurrentBdz) {
                    childList = (ArrayList<Lookup>) SwitchMenuService.getInstance()
                            .findSwitchMenu(currentTypeStr, mCurrentBdz.bdzid);
                } else if (InspectionType.special.name().equals(currentTypeStr)) {
                    childList = (ArrayList<Lookup>) SpecialMenuService.getInstance()
                            .findSpecialMenu(currentTypeStr);
                } else {
                    childList = new ArrayList<Lookup>(LookupService.getInstance()
                            .findLookupByLooId(LookUpType.inspection.name(), mGroupLookUp.id));
                }
                if (childList == null || childList.isEmpty()) {
                    mHandler.sendEmptyMessage(INIT_INSPECTION_TYPE_CODE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载巡检类型大类下是否有子项
     */
    public void initInspectionTypeChildData() {

        if (mCurrentBdz != null && groupList != null && !groupList.isEmpty()) {
            groupHashMap = new HashMap<>();
            for (Lookup mGroupLookUp : groupList) {
                if ((InspectionType.switchover.name().equals(currentTypeStr) || InspectionType.maintenance.name().equals(currentTypeStr)) && null != mCurrentBdz) {
                    childList = (ArrayList<Lookup>) SwitchMenuService.getInstance()
                            .findSwitchMenu(currentTypeStr, mCurrentBdz.bdzid);
                    groupHashMap.put(mGroupLookUp, childList);
                } else if (InspectionType.special.name().equals(currentTypeStr)) {
                    childList = (ArrayList<Lookup>) SpecialMenuService.getInstance()
                            .findSpecialMenu(currentTypeStr);
                    groupHashMap.put(mGroupLookUp, childList);
                } else {
                    childList = new ArrayList<Lookup>(LookupService.getInstance()
                            .findLookupByLooId(LookUpType.inspection.name(), mGroupLookUp.id));
                    groupHashMap.put(mGroupLookUp, childList);
                }
            }
        }
        mHandler.sendEmptyMessage(LOAD_INSPECTION_TYPE_DATA);
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> this.finish());
        binding.btnCancel.setOnClickListener(view -> finish());
        binding.btnConfirm.setOnClickListener(view -> saveTask());
        binding.ibtnSelectInspectionDate.setOnClickListener(view -> {
            CustomerDialog.showDatePickerDialog(currentActivity, (result, position) -> binding.tvInspectionDate.setText(result));
        });
        binding.bdzContanier.setOnClickListener(view -> {
            binding.bdzContanier.setPressed(true);
            if (isBdzDataPrepared) {
                showPowerStationDialog(mBdzList);
            }
        });
        binding.tvSelectInspectionType.setOnClickListener(view -> selectType());
        binding.typeXunjianIbt.setOnClickListener(view -> selectType());
        binding.typeLlContainer.setOnClickListener(view -> selectType());
    }

    private void selectType() {
        binding.typeLlContainer.setPressed(true);
        if (isInspectionTypeDataPrepared) {
            if (childList == null || childList.size() == 0) {
                if (TextUtils.isEmpty(binding.tvSelectPowerStation.getText().toString())) {
                    ToastUtils.showMessage( "请先选择变电站");
                    return;
                }
                return;
            }
            if (mCurrentBdz != null) {
                showInspectionTypeDialog();
            } else {
                ToastUtils.showMessage( "请先选择变电站");
            }
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_BDZ_DATA:
                if (null != mCurrentBdz) {
                    binding.tvSelectPowerStation.setText(mCurrentBdz.name);
                }
                isBdzDataPrepared = true;
                break;
            case LOAD_INSPECTION_TYPE_DATA:
                isInspectionTypeDataPrepared = true;
                if (groupList != null && groupList.size() != 0) {
                    mInspectionType = groupList.get(0);
                }
                if (childList != null && childList.size() > 0) {
                    binding.tvSelectInspectionType.setText("");
                }
                break;
            case INIT_INSPECTION_TYPE_CODE:
                binding.tvSelectInspectionType.setCompoundDrawables(null, null, null, null);
                mInspectionType = groupList.get(0);
                switchMaintenances.add(mInspectionType);
                binding.typeXunjianIbt.setVisibility(View.GONE);
                setNewProductDisplay();
                binding.tvSelectInspectionType.setText(mInspectionType.v);
                break;
            case INIT_PERSONS:
                SelectPersonUtil.getInstance().setRecyWidget(currentActivity, binding.recPersonContainer, selectPersons, R.layout.xs_item_task_user, new GridLayoutManager(getApplicationContext(), 4))
                        .disPlayAllPerson(allPersons, binding.ibSelectPerson);
                break;
            default:
                break;
        }
    }

    /**
     * 显示巡检类型dialog
     */
    XsActivityAddinpsectionTypeDialogBinding typeDialogBinding;

    private void showInspectionTypeDialog() {
        if (mXunJianTypeAdapter == null) {
            mXunJianTypeAdapter = new XunJianTypeAdapter(currentActivity, childList);
        } else {
            mXunJianTypeAdapter.setList(childList);
        }
        mXunJianTypeAdapter.setType(currentTypeStr);
        if (mInspectionTypeDialog == null) {
            int screenWidth = ScreenUtils.getScreenWidth(currentActivity);
            int dialogWidth = screenWidth * 7 / 9;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            typeDialogBinding = XsActivityAddinpsectionTypeDialogBinding.inflate(getLayoutInflater());
            mInspectionTypeDialog = DialogUtils.createDialog(currentActivity, typeDialogBinding.getRoot(), dialogWidth, dialogHeight);
        }
        if (currentTypeStr.contains(InspectionType.switchover.name()) || currentTypeStr.contains(InspectionType.maintenance.name())) {
            typeDialogBinding.btnConfirm.setVisibility(View.VISIBLE);
        } else {
            typeDialogBinding.btnConfirm.setVisibility(View.GONE);
        }
        typeDialogBinding.lvContainer.setAdapter(mXunJianTypeAdapter);
        mInspectionTypeDialog.show();
        typeDialogBinding.lvContainer.setOnItemClickListener((adapterView, view, i, l) -> {
            Lookup mLookup = (Lookup) adapterView.getItemAtPosition(i);
            mInspectionType = mLookup;
            binding.batteryGroup.setVisibility(View.GONE);
            boolean xudianchi = mInspectionType.v.contains(Config.XUDIANCHI) && (mInspectionType.v.contains(Config.DIANYA) || mInspectionType.v.contains(Config.NEIZU));
            if ("select_device".equals(mInspectionType.deviceWay)) {
                binding.btnConfirm.setText(R.string.xs_next_step_str);
            } else if (xudianchi) {
                // 电池
                binding.batteryGroup.setVisibility(View.VISIBLE);
            } else {
                binding.btnConfirm.setText(R.string.xs_confirm_str);
            }

            PreferencesUtils.put( mLookup.k + "_" + mCurrentBdz.bdzid, mLookup.repSwitchOverId);
            if (currentTypeStr.contains(InspectionType.switchover.name()) || currentTypeStr.contains(InspectionType.maintenance.name())) {
                StringBuilder builder = new StringBuilder();
                if (switchMaintenances.contains(mLookup)) {
                    switchMaintenances.remove(mLookup);
                } else {
                    switchMaintenances.add(mLookup);
                }
                mXunJianTypeAdapter.setSelectTypes(switchMaintenances);
                for (Lookup lookup : switchMaintenances) {
                    builder.append(lookup.v).append(" ");
                }
                binding.tvSelectInspectionType.setText(builder.toString());
            } else {
                switchMaintenances.clear();
                switchMaintenances.add(mLookup);
                binding.tvSelectInspectionType.setText(mLookup.v);
                mInspectionTypeDialog.dismiss();
            }
        });
        typeDialogBinding.btnConfirm.setOnClickListener(view -> {
            mInspectionTypeDialog.dismiss();
        });
    }

    /**
     * 设置设备列表界面
     */
    private void setNewProductDisplay() {
        if (InspectionType.operation.name().equalsIgnoreCase(mInspectionType.k)) {
            binding.btnConfirm.setText(R.string.xs_next_step_str);
        } else {
            binding.btnConfirm.setText(R.string.xs_confirm_str);
        }
    }

    /**
     * 显示变电站的dialog
     *
     * @param powerStationList
     */
    private XsContentListDialogBinding dialogBinding;

    private void showPowerStationDialog(List<Bdz> powerStationList) {
        BdzDialogAdapter mPowerStationAdapter = new BdzDialogAdapter(currentActivity, powerStationList);
        if (mPowerStationDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = powerStationList.size() > 8 ? ScreenUtils.getScreenHeight(currentActivity) * 3 / 5
                    : LinearLayout.LayoutParams.WRAP_CONTENT;
            dialogBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mPowerStationDialog = DialogUtils.createDialog(currentActivity, dialogBinding.getRoot(), dialogWidth, dialogHeight);
        }
        dialogBinding.lvContainer.setAdapter(mPowerStationAdapter);
        dialogBinding.tvDialogTitle.setText(R.string.xs_please_select_power_station_str);
        mPowerStationDialog.show();
        dialogBinding.lvContainer.setOnItemClickListener((adapterView, view, i, l) -> {
            mCurrentBdz = (Bdz) adapterView.getItemAtPosition(i);
            binding.tvSelectPowerStation.setText(mCurrentBdz.name);
            if (mInspectionType != null) {
                if ("full,day".contains(mInspectionType.k)) {
                } else {
                    binding.tvTips.setVisibility(View.GONE);
                }
            }
            mPowerStationDialog.dismiss();
            initInspectionTypeChildData();
        });
    }

    /**
     * 保存任务
     */
    private void saveTask() {
        if (mCurrentBdz == null) {
            ToastUtils.showMessage( R.string.xs_please_select_power_station_str);
            return;
        }
        if (mInspectionType == null) {
            ToastUtils.showMessage( R.string.xs_please_select_inspection_type_str);
            return;
        }
        if (TextUtils.isEmpty(binding.tvSelectInspectionType.getText().toString())) {
            ToastUtils.showMessage( R.string.xs_please_select_inspection_type_str);
            return;
        }
        String inspectionDate = binding.tvInspectionDate.getText().toString().trim();
        if (TextUtils.isEmpty(inspectionDate)) {
            ToastUtils.showMessage( R.string.xs_please_select_inspection_date_str);
            return;
        }

        if (!DateUtils.compareDate(inspectionDate, DateUtils.getCurrentShortTime(), DateUtils.yyyy_MM_dd)) {
            ToastUtils.showMessage( R.string.xs_please_select_after_today_date_str);
            binding.tvInspectionDate.setText("");
            return;
        }
        selectPersons = SelectPersonUtil.getInstance().getAllSelectUser();
        StringBuilder creatUser = new StringBuilder();
        StringBuilder membersUser = new StringBuilder();
        ArrayList<String> nameList = new ArrayList<>();
        String selectNames = "";
        for (DbModel model : selectPersons) {
            if (currentAcounts.contains(model.getString("account"))) {
                if (!TextUtils.isEmpty(creatUser.toString())) {
                    creatUser.append(",");
                }
                creatUser.append(model.getString("account"));
            } else {
                if (!TextUtils.isEmpty(membersUser.toString())) {
                    membersUser.append(",");
                }
                membersUser.append(model.getString("account"));
            }
            nameList.add(model.getString("username"));
            selectNames = StringUtils.arrayListToString(nameList);
            PreferencesUtils.put( Config.TASK_SELECT_PERSONS, selectNames);
        }
        PreferencesUtils.put( Config.CURRENT_BDZ_NAME, mCurrentBdz.name);
        PreferencesUtils.put( Config.CURRENT_BDZ_ID, mCurrentBdz.bdzid);
        for (Lookup lookup : switchMaintenances) {
            mInspectionType = lookup;
            List<Task> taskList = TaskService.getInstance().getCountSameTask(mInspectionType.k, mCurrentBdz.bdzid, inspectionDate, currentAcounts);
            int todaySameTaskCount = taskList.size();
            if (todaySameTaskCount > 0) {
                ToastUtils.showMessage( "计划列表中已经有" + mInspectionType.v + "任务了，请返回查看哟！");
                return;
            }
            mCurrentTask = new Task(MyUUID.id(4), mCurrentBdz.bdzid, mCurrentBdz.name, mInspectionType.k, mInspectionType.v,
                    DateUtils.getFormatterTime(inspectionDate + " 00:00:00", DateUtils.yyyy_MM_dd_HH_mm_ss),
                    TaskStatus.undo.name(), creatUser.toString(), membersUser.toString());
            mReport = new Report();
            mReport.persons = selectNames;
            mReport.bdzid = mCurrentBdz.bdzid;
            mReport.bdz = mCurrentBdz.name;
            mReport.reportid = FunctionUtil.getPrimarykey();
            mReport.taskid = mCurrentTask.taskid;
            PreferencesUtils.put( Config.CURRENT_REPORT_ID, mReport.reportid);
            PreferencesUtils.put( Config.LOO_ID, mInspectionType.loo_id);
            PreferencesUtils.put( Config.CURRENT_TASK_ID, mCurrentTask.taskid);
            PreferencesUtils.put( Config.CURRENT_INSPECTION_TYPE, mInspectionType.k);
            PreferencesUtils.put( Config.CURRENT_INSPECTION_TYPE_NAME, mInspectionType.v);
            boolean xudianchi = mInspectionType.v.contains(Config.XUDIANCHI) && (mInspectionType.v.contains(Config.DIANYA) || mInspectionType.v.contains(Config.NEIZU));
            try {
                XunshiApplication.getDbUtils().saveOrUpdate(mReport);
                if ("select_device".equalsIgnoreCase(mInspectionType.deviceWay)) {
                    // 手动选择设备
                    Intent intent = new Intent(currentActivity, DeviceSelectActivity.class);
                    intent.putExtra(Config.CURRENT_INSPECTION_TYPE, mInspectionType.k);

                    intent.putExtra(DeviceSelectActivity.SELECT_TYPE, DeviceSelectActivity.SELECT_TYPE_MULT);
                    startActivityForResult(intent, MUMAL_SELECT_DEVICE);
                } else if (xudianchi) {
                    // 蓄电池电压检测
                    RadioGroup checkType = (RadioGroup) findViewById(R.id.radio_ceshi_type);
                    TaskExtend taskExpand = new TaskExtend(mCurrentTask.taskid);
                    if (checkType.getCheckedRadioButtonId() == R.id.radio_puce) {
                        taskExpand.sbjcIsAllCheck = 0;
                    } else {
                        taskExpand.sbjcIsAllCheck = 1;
                    }
                    XunshiApplication.getDbUtils().save(taskExpand);
                    XunshiApplication.getDbUtils().save(mCurrentTask);
                    setResult(RESULT_OK);
                    this.finish();
                } else if (InspectionType.operation.name().equalsIgnoreCase(mInspectionType.k)) {
                    // 运维一体化
                    Intent intent = new Intent(currentActivity, YWDeviceListActivity.class);
                    startActivityForResult(intent, ADD_YUNWEI_TASK_CODE);
                } else {
                    XunshiApplication.getDbUtils().save(mCurrentTask);
                    setResult(RESULT_OK);
                    this.finish();
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        PreferencesUtils.put( Config.LASTTIEM_CHOOSE_BDZNAME, mCurrentBdz.bdzid);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SAVE_TASK_REQUEST_CODE:
                    try {
                        XunshiApplication.getDbUtils().save(mCurrentTask);
                        setResult(RESULT_OK);
                        this.finish();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADD_YUNWEI_TASK_CODE:
                    // TODO:保存选择的运维一体化类型
                    if (data != null) {
                        String inspectionTypeId = data.getStringExtra(Config.CURRENT_INSPECTION_TYPE);
                        String inspectionTypeName = data.getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
                        if (!TextUtils.isEmpty(inspectionTypeId) && !TextUtils.isEmpty(inspectionTypeName)) {
                            mCurrentTask.inspection = inspectionTypeId;
                            mCurrentTask.inspection_name = inspectionTypeName;
                            mCurrentTask.type = InspectionType.operation.name();
                            PreferencesUtils.put( Config.CURRENT_TASK_ID, mCurrentTask.taskid);
                            PreferencesUtils.put( Config.CURRENT_BDZ_NAME, mCurrentBdz.name);
                            PreferencesUtils.put( Config.CURRENT_BDZ_ID, mCurrentBdz.bdzid);
                            PreferencesUtils.put( Config.CURRENT_INSPECTION_TYPE, mInspectionType.k);
                            PreferencesUtils.put( Config.CURRENT_INSPECTION_TYPE_NAME, mInspectionType.v);
                            try {
                                XunshiApplication.getDbUtils().save(mCurrentTask);
                                setResult(RESULT_OK);
                                this.finish();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case MUMAL_SELECT_DEVICE:
                    String selectDevice = data.getStringExtra(DeviceSelectActivity.RESULT_SELECT_DEVICE);
                    mCurrentTask.selected_deviceid = selectDevice;
                    mReport.selected_deviceid = selectDevice;
                    try {
                        XunshiApplication.getDbUtils().saveOrUpdate(mReport);
                        XunshiApplication.getDbUtils().save(mCurrentTask);
                        setResult(RESULT_OK);
                        this.finish();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SelectPersonUtil.selectPersonUtil = null;
    }
}
