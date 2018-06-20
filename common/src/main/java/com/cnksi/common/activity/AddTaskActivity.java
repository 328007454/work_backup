package com.cnksi.common.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.databinding.CommonAddTaskActivityBinding;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.ReportSignname;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.TaskExtend;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.ListUtils;
import com.cnksi.common.utils.SelectPersonUtil;
import com.cnksi.common.view.BdzDialog;
import com.cnksi.common.view.InspectionTypeDialog;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.activity.DeviceSelectActivity.RESULT_SELECT_KEY;

/**
 * @author Mr.K  on 2018/6/14.
 * @decrption 增加任务界面
 */

public class AddTaskActivity extends BaseTitleActivity {
    public static final int MUMAL_SELECT_DEVICE = 0x001;
    public static final int ADD_YUNWEI_TASK_CODE = MUMAL_SELECT_DEVICE + 1;
    private CommonAddTaskActivityBinding binding;
    private boolean isSelectInspectionType;
    private boolean isSelectPersons;
    private BdzDialog bdzDialog;
    private Bdz bdz;
    private InspectionTypeDialog typeDialog;
    private List<DbModel> selectTypeModels = new ArrayList<>();
    private List<DbModel> selectPersons;
    private SelectPersonUtil selectPersonUtil;
    private boolean isXuDianChi;
    private String xuDianChiTest;
    private boolean isSelectDevice;
    private String selectInspectionType;
    private String selectDeviceId;
    private String yunWeiYiTiHua;
    private TaskExtend taskExpand;

    @Override
    protected View getChildContentView() {
        binding = CommonAddTaskActivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void initUI() {
        getIntentValue();
        currentInspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE);
        if (TextUtils.isEmpty(currentInspectionType)) {
            return;
        }
        mTitleBinding.tvTitle.setText(R.string.xs_add_inspection_task_str);
        isSelectInspectionType = currentInspectionType.contains(InspectionType.maintenance.name()) || currentInspectionType.contains(InspectionType.switchover.name()) ||
                (currentInspectionType.contains(InspectionType.special.name()) && !TextUtils.equals(currentInspectionType, InspectionType.special_xideng.name()) || currentInspectionType.contains(InspectionType.SBJC_06.name()));
        isSelectPersons = TextUtils.equals(currentInspectionType, InspectionType.full.name())
                || currentInspectionType.contains(InspectionType.special.name()) || TextUtils.equals(currentInspectionType, InspectionType.professional.name());

        String inspectionValue = InspectionType.get(currentInspectionType).value;
        isXuDianChi = inspectionValue.contains("蓄电池") || inspectionValue.contains("电压") || inspectionValue.contains("内阻");
        if (isXuDianChi) {
            binding.batteryGroup.setVisibility(View.VISIBLE);
            binding.radioCeshiType.setOnCheckedChangeListener(onCheckedChangeListener);
        }
        if (!isSelectInspectionType) {
            binding.tvSelectInspectionType.setCompoundDrawables(null, null, null, null);
            binding.tvSelectInspectionType.setText(InspectionType.get(currentInspectionType).value);
        }

        if (isSelectPersons) {
            binding.llPersons.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(currentBdzName)) {
            binding.tvSelectPowerStation.setText(currentBdzName);
        }

        if (InspectionType.operation.name().equalsIgnoreCase(currentInspectionType)) {
            binding.btnConfirm.setText("下一步");
        }

        binding.tvInspectionDate.setText(DateUtils.getCurrentShortTime());

        initOnClick();
    }

    @Override
    public void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                if (!TextUtils.isEmpty(currentBdzId)) {
                    bdz = BdzService.getInstance().findById(currentBdzId);
                }
                currentAcounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
                selectPersons = DepartmentService.getInstance().findUserForCurrentUser(currentAcounts);
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                binding.tvSelectPowerStation.setText(bdz.name);
                selectPersonUtil = SelectPersonUtil.getInstance().setRecyWidget(mActivity, getSupportFragmentManager(), binding.recPersonContainer, selectPersons, new GridLayoutManager(getApplicationContext(), 4), currentDepartmentId);

            });
        });
    }

    private void initOnClick() {
        if (isSelectInspectionType) {
            binding.tvSelectInspectionType.setOnClickListener(v -> {
                selectType();
            });
        }

        binding.tvSelectPowerStation.setOnClickListener(v -> {
            selectBdz();
        });

        binding.tvInspectionDate.setOnClickListener(v -> {
            CustomerDialog.showDatePickerDialog(mActivity, (result, position) -> binding.tvInspectionDate.setText(result));
        });

        binding.ibSelectPerson.setOnClickListener(v -> {
            selectPersonUtil.disPlayAllPerson(binding.ibSelectPerson);
        });

        binding.btnCancel.setOnClickListener(v -> onBackPressed());
        binding.btnConfirm.setOnClickListener(v -> {
            if (isSelectDevice) {
                com.cnksi.common.activity.DeviceSelectActivity.with(mActivity)
                        .setBdzId(bdz.bdzid)
                        .setInspectionType(selectInspectionType)
                        .setMultiSelect(true)
                        .setRequestCode(MUMAL_SELECT_DEVICE)
                        .start();
            } else if (InspectionType.operation.name().equalsIgnoreCase(currentInspectionType)) {
                // 运维一体化
                ComponentName componentName = new ComponentName(getApplicationContext().getPackageName(), "com.cnksi.bdzinspection.ywyth.YWDeviceListActivity");
                Intent intent = new Intent();
                intent.setComponent(componentName);
                startActivityForResult(intent, ADD_YUNWEI_TASK_CODE);

            } else {
                saveReady();
            }
        });
    }

    public void selectBdz() {
        if (isSelectInspectionType && bdz != null && !TextUtils.equals(bdz.name, binding.tvSelectPowerStation.getText().toString())) {
            binding.tvSelectInspectionType.setText("");
        }
        bdzDialog = new BdzDialog.Builder(mActivity).setDeptId(currentDepartmentId).setItemClickListener((view, data, position) -> {
            bdzDialog.dismiss();
            bdz = data;
            binding.tvSelectPowerStation.setText(bdz.name);
        }).loadData().getBdzDialog();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        bdzDialog.show(fragmentTransaction, "bdzDialog");
    }

    public void selectType() {
        if (bdz == null) {
            ToastUtils.showMessage("请先选择变电站");
            return;
        }
        if (currentInspectionType.contains(InspectionType.special.name()) || currentInspectionType.contains(InspectionType.SBJC_06.name())) {
            typeDialog = new InspectionTypeDialog.Builder(mActivity, false).setBdzId(bdz.bdzid).setInspection(currentInspectionType).loadData().setItemClickListener((v1, data, position) -> {
                selectTypeModels.clear();
                selectTypeModels.add(data);
                binding.tvSelectInspectionType.setText(data.getString("v"));
                String deviceWay = data.getString("device_way");
                if (!TextUtils.isEmpty(deviceWay) && TextUtils.equals(deviceWay, "select_device")) {
                    binding.btnConfirm.setText("下一步");
                    selectInspectionType = data.getString("k");
                    isSelectDevice = true;
                }
                typeDialog.dismiss();
            }).getTypeDialog();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            typeDialog.show(fragmentTransaction, "typeDialog");
        } else {
            typeDialog = new InspectionTypeDialog.Builder(mActivity, true).setBdzId(bdz.bdzid).setInspection(currentInspectionType).loadData().getTypeDialog();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            typeDialog.show(fragmentTransaction, "typeDialog");
            typeDialog.setDialogDissMissListener(selectTypeModels -> {
                boolean hasXuDianChi;
                if (selectTypeModels != null && !selectTypeModels.isEmpty()) {
                    this.selectTypeModels = selectTypeModels;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int position = 0; position < selectTypeModels.size(); position++) {
                        DbModel dbModel = selectTypeModels.get(position);
                        String value = dbModel.getString("v");
                        if (position == 0) {
                            stringBuilder.append(value);
                        } else {
                            stringBuilder.append("\n").append(value);
                        }
                        hasXuDianChi = value.contains("蓄电池") || value.contains("电压") || value.contains("内阻");
                        if (hasXuDianChi) {
                            isXuDianChi = true;
                        }
                    }
                    binding.tvSelectInspectionType.setText(stringBuilder.toString());
                }
                if (isXuDianChi) {
                    binding.batteryGroup.setVisibility(View.VISIBLE);
                } else {
                    binding.batteryGroup.setVisibility(View.GONE);
                }
            });
        }

    }

    private List<Task> saveTasks = new ArrayList<>();
    private List<Report> saveReports = new ArrayList<>();
    private List<ReportSignname> saveReportSignNames = new ArrayList<>();

    public void saveReady() {
        if (bdz == null) {
            ToastUtils.showMessage("请选择变电站");
            return;
        }
        if (isSelectInspectionType && selectTypeModels.isEmpty()) {
            ToastUtils.showMessage("请选择任务类型");
            return;
        }
        if (isSelectInspectionType) {
            StringBuilder builder = new StringBuilder();
            for (DbModel model : selectTypeModels) {
                String type = model.getString("k");
                Task task = TaskService.getInstance().findTaskByTypeAndTodayTime(type, DateUtils.getCurrentShortTime());
                if (task != null) {
                    builder.append(model.getString("v")).append("\n");
                }
                createTaskAndReportAndSignName(model.getString("k"), model.getString("v"));
            }
            if (!judgeTodayTaskExisted(builder.toString())) {
                saveData();
            }
        } else {
            StringBuilder builder = new StringBuilder();
            Task task = TaskService.getInstance().findTaskByTypeAndTodayTime(currentInspectionType, DateUtils.getCurrentShortTime());
            if (task != null) {
                builder.append(InspectionType.get(currentInspectionType).value).append("\n");
            }
            if (!judgeTodayTaskExisted(builder.toString())) {
                if (InspectionType.operation.name().equalsIgnoreCase(currentInspectionType)) {
                    createTaskAndReportAndSignName(selectInspectionType, currentInspectionTypeName);
                } else {
                    createTaskAndReportAndSignName(currentInspectionType, InspectionType.get(currentInspectionType).value);
                }
                saveData();
            }
        }
    }

    public void saveData() {
        try {
            CommonApplication.getInstance().getDbManager().saveOrUpdate(saveTasks);
            CommonApplication.getInstance().getDbManager().saveOrUpdate(saveReports);
            if (!currentInspectionType.contains(InspectionType.SBJC.name()) || isXuDianChi) {
                CommonApplication.getInstance().getDbManager().saveOrUpdate(saveReportSignNames);
            }
            if (taskExpand != null) {
                CommonApplication.getInstance().getDbManager().saveOrUpdate(taskExpand);
            }
            setResult(RESULT_OK);
            this.finish();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void createTaskAndReportAndSignName(String inspectionType, String inspectionValue) {
        Task saveTask = new Task(bdz, inspectionType, currentAcounts, inspectionValue, selectPersonUtil.getAllSelectUser(), selectDeviceId, yunWeiYiTiHua);
        if (currentInspectionType.equals(InspectionType.SBJC_11.name()) || currentInspectionType.equals(InspectionType.SBJC_10.name())||inspectionValue.contains("电压")||inspectionValue.contains("蓄电池")||inspectionValue.contains("内阻")) {
            taskExpand = new TaskExtend(saveTask.taskid);
            if (binding.radioChouce.isChecked()) {
                taskExpand.sbjcIsAllCheck = 1;
            } else if (binding.radioPuce.isChecked()) {
                taskExpand.sbjcIsAllCheck = 0;
            }
        }
        Report saveReport = new Report(saveTask, bdz);
        if (isXuDianChi) {
            xuDianChiTest = TextUtils.isEmpty(xuDianChiTest) ? "0" : "1";
            saveReport.checkType = xuDianChiTest;
        }
        ReportSignname saveReportSignName = new ReportSignname(currentAcounts, selectPersonUtil.getAllSelectUser(), saveReport);
        saveTasks.add(saveTask);
        saveReports.add(saveReport);
        saveReportSignNames.add(saveReportSignName);
    }


    private boolean judgeTodayTaskExisted(String value) {
        if (!TextUtils.isEmpty(value)) {
            DialogUtils.showSureTipsDialog(mActivity, null, "已下任务在今天已经存\n" + value, v -> {
                DialogUtils.dialog.dismiss();
            });
            return true;
        } else {
            return false;
        }
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
        if (checkedId == R.id.radio_chouce) {
            xuDianChiTest = "1";
        } else {
            xuDianChiTest = "0";
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_YUNWEI_TASK_CODE:
                    // TODO:保存选择的运维一体化类型
                    if (data != null) {
                        String inspectionTypeId = data.getStringExtra(Config.CURRENT_INSPECTION_TYPE);
                        String inspectionTypeName = data.getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
                        if (!TextUtils.isEmpty(inspectionTypeId) && !TextUtils.isEmpty(inspectionTypeName)) {
                            yunWeiYiTiHua = InspectionType.operation.name();
                            selectInspectionType = inspectionTypeId;
                            currentInspectionTypeName = inspectionTypeName;
                            saveReady();
                        }
                    }
                    break;
                case MUMAL_SELECT_DEVICE:
                    ArrayList<DbModel> selectDeviceList = (ArrayList<DbModel>) data.getSerializableExtra(RESULT_SELECT_KEY);
                    String selectDevice = ListUtils.toString(selectDeviceList, (model, aBoolean) -> model.getString(DeviceService.DEVICE_ID_KEY) + (aBoolean ? "" : Config.COMMA_SEPARATOR));
                    selectDeviceId = selectDevice;
                    isSelectDevice = false;
                    saveReady();
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
