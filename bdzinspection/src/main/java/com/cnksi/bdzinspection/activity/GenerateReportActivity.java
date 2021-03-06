package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.AdapterClickListener;
import com.cnksi.bdzinspection.adapter.SignNameAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.daoservice.BatteryGroupService;
import com.cnksi.bdzinspection.daoservice.StandardSwitchOverService;
import com.cnksi.bdzinspection.databinding.XsActivityGenerateReportBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSignViewBinding;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.bdzinspection.utils.PushNewTaskUtil;
import com.cnksi.bdzinspection.utils.TimePickerUtils;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.activity.LandSignNameActivity;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.ReportSignnameService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.Role;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.ReportSignname;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.view.PeopleDialog;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.nari.NariActivity;
import com.cnksi.nari.type.PackageStatus;
import com.cnksi.nari.utils.NariDataManager;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月9日 下午3:05:56 TODO
 */
public class GenerateReportActivity extends TitleActivity implements AdapterClickListener, TimePickerUtils.TimePickerMissListener {
    public final int MASKGzr = 0x0;
    public final int MASKFzr = 0xFF00;
    public final int SIGNCODEGzr = 0x300;
    public final int SIGNCODEFzr = 0x301;

    List<ReportSignname> mDataCzr = new ArrayList<>();
    List<ReportSignname> mDataFzr = new ArrayList<ReportSignname>();
    SignNameAdapter adapterGzr;
    SignNameAdapter adapterFzr;
    String currentSignNamePath;
    String currentHeadPath;
    ReportSignname currentSign;
    Report currentReport;
    List<DbModel> persons;
    Dialog mSignDetailDialog;
    String inspectionMark;
    String inspectionResult;
    String inspectionContent;
    boolean xudianchi;
    List<ReportSignname> listSign = new ArrayList<>();
    /**
     * 本次巡视发现缺陷总数
     */
    private List<DefectRecord> mNewDefectList = null;
    // 本次跟踪的缺陷
    private List<DefectRecord> mTrackDefectList = null;
    // 本次消除的缺陷
    private List<DefectRecord> mEliminateDefectList = null;
    /**
     * 抄录数据条数
     */
    private long copyCount = 0;
    /**
     * 抄录数据总条数
     */
    private long totalCount = 0;
    private int totalCountUser;
    private List<BatteryGroup> mBatteryGroupList = new ArrayList<>();
    private Dialog mDefectDialog;
    private HistoryDefectAdapter mHistoryDefectAdapter;
    private boolean clickCzr;
    private List<String> czrModels = new ArrayList<>();
    private List<String> czrModelsOrigin = new ArrayList<>();
    private List<String> fzrModels = new ArrayList<>();

    private XsActivityGenerateReportBinding binding;

    private boolean isFirstEnter = true;

    @Override
    protected int setLayout() {
        return R.layout.xs_activity_generate_report;
    }

    @Override
    protected String initialUI() {
        binding = (XsActivityGenerateReportBinding) getDataBinding();
        initOnClick();
        TimePickerUtils.getPickerUtils().setTimePickListener(this);
        getIntentValue();
        if (isParticularInspection() || isRoutineNotCopy()) {
            binding.llCopyResult.setVisibility(View.GONE);
            binding.lineCopyResult.setVisibility(View.GONE);
        }
        binding.timeContainer.setVisibility(SystemConfig.isDefineTimeBySelf() ? View.VISIBLE : View.GONE);
        return getString(R.string.xs_gp_generate_report);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstEnter) {
            if (currentInspectionType.equalsIgnoreCase(InspectionType.routine.name()) || currentInspectionType.equalsIgnoreCase(InspectionType.full.name())) {
                copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                totalCount = CopyItemService.getInstance().getCopyTotalCount(currentBdzId, currentInspectionType);
                binding.tvCopyResult.setText(String.valueOf(copyCount) + "/" + String.valueOf(totalCount));
            }
        } else {
            isFirstEnter = false;
        }

    }

    @Override
    protected void initialData() {
        getIntentValue();
        if (!TextUtils.isDigitsOnly(currentInspectionType)) {
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                binding.etSwitchoverResult.setVisibility(View.VISIBLE);
                runOnUiThread(() -> {
                    binding.layoutResult.setVisibility(View.GONE);
                    binding.layoutMessageInput.setVisibility(View.GONE);
                    binding.layoutMessageResultinput.setVisibility(View.GONE);
                    if (currentInspectionType.contains("switchover")) {
                        title.setText("生成试验报告");
                        binding.txtResult.setText("试验结果");
                        binding.btnComplete.setText("生成试验报告");
                    } else {
                        title.setText("生成维护报告");
                        binding.txtResult.setText("维护结果");
                        binding.btnComplete.setText("生成维护报告");
                    }
                });
            }
        }
        xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
        if (xudianchi) {
            binding.layoutConclusion.setVisibility(View.VISIBLE);
        }

        ExecutorManager.executeTask(() -> {
            try {
                currentReport = ReportService.getInstance().getReportById(currentReportId);
                inspectionMark = currentReport.inspectionRemark;
                inspectionResult = currentReport.inspectionResult;
                if ((!isParticularInspection()) && (!isRoutineNotCopy())) {
                    copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                    totalCount = CopyItemService.getInstance().getCopyTotalCount(currentBdzId, currentInspectionType);
                    if (xudianchi) {
                        List<DbModel> batteryDbmodelList = BatteryGroupService.getInstance().findBatteryGroup(currentBdzId);
                        DbModel batteryCopyTotal = BatteryGroupService.getInstance().findAllBatteryCodeCount(currentBdzId, currentReportId);
                        int sumBatteryCode = 0;
                        for (DbModel dbModel : batteryDbmodelList) {
                            sumBatteryCode = sumBatteryCode + Integer.valueOf(dbModel.getString("amount"));
                        }
                        copyCount = Integer.valueOf(TextUtils.isEmpty(batteryCopyTotal.getString("count")) ? "0" : batteryCopyTotal.getString("count"));
                        totalCount = sumBatteryCode;
                        mBatteryGroupList = BatteryGroupService.getInstance().findAllBatteryGroup(currentBdzId, currentReportId);
                        if (mBatteryGroupList.isEmpty() && !batteryDbmodelList.isEmpty()) {
                            BatteryGroup batteryGroup = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, batteryDbmodelList.get(0).getString("bid"));
                            mBatteryGroupList.add(batteryGroup);
                        }
                    } else if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                        if ("maintenance_blqdzcs".equalsIgnoreCase(currentInspectionType)) {
                            copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                            totalCount = CopyItemService.getInstance().getCopyItemCount1(currentBdzId, CopyItemService.getInstance().getCopyType());
                        } else {
                            copyCount = TaskService.getInstance().queryCopyData(currentReportId);
                            totalCount = StandardSwitchOverService.getInstance().getSwitchOverCopyTotal(currentInspectionType, currentBdzId);
                        }

                    }
                } else {
                    if (currentInspectionType.startsWith("special_manual") && TextUtils.isEmpty(inspectionMark)) {

                        String selectDevices = "'" + (currentReport.selected_deviceid == null ? "" : currentReport.selected_deviceid) + "'";
                        selectDevices = selectDevices.replace(",", "','");
                        DbModel model = DeviceService.getInstance().findDeviceNamesByDeviceIds(selectDevices);
                        String mark = model.getString("rs");
                        inspectionMark = mark == null ? "" : "本次巡视的设备：\n" + mark.replace(",", "\n");
                    }
                }

                // 查询本次发现的缺陷
                mNewDefectList = DefectRecordService.getInstance().findCurrentTaskNewDefectList(currentBdzId, currentReportId);
                // 查询本次跟踪的缺陷
                mTrackDefectList = DefectRecordService.getInstance().findCurrentTaskTrackDefectList(currentBdzId, currentReportId);
                // 查询本次消除的缺陷
                mEliminateDefectList = DefectRecordService.getInstance().findCurrentTaskEliminateDefectList(currentBdzId, currentReportId);

            } catch (DbException e1) {
                e1.printStackTrace();
            }

            try {
                String sql = "SELECT xjnr,remark,xsjg FROM lookup_local where bdzid='" + currentBdzId + "' and k= '" + currentInspectionType + "' and dlt =0 ;";
                SqlInfo sqlInfo = new SqlInfo(sql);
                DbModel model = LookupService.getInstance().findDbModelFirst(sqlInfo);
                inspectionContent = TextUtils.isEmpty(currentReport.inspectionContent) ? model == null ? "" : model.getString("xjnr") : currentReport.inspectionContent;
                if (model != null) {
                    inspectionMark = TextUtils.isEmpty(inspectionMark) ? model.getString("remark") : inspectionMark;
                    inspectionResult = TextUtils.isEmpty(inspectionResult) ? model.getString("xsjg") : inspectionResult;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                inspectionMark = "";
            }
            // 显示 发现的缺陷 跟踪的缺陷 以及 消除的缺陷
            runOnUiThread(() -> {
                binding.tvNewDefectCount.setText(String.valueOf(mNewDefectList == null ? 0 : mNewDefectList.size()));
                binding.tvTrackDefectCount.setText(String.valueOf(mTrackDefectList == null ? 0 : mTrackDefectList.size()));
                binding.tvEliminateDefectCount.setText(String.valueOf(mEliminateDefectList == null ? 0 : mEliminateDefectList.size()));
                binding.tvCopyResult.setText(String.valueOf(copyCount) + "/" + String.valueOf(totalCount));
                binding.inspectionContent.setText(inspectionContent);
                binding.etRemark.setText(TextUtils.isEmpty(inspectionMark) ? "" : inspectionMark);
                binding.etResult.setText(TextUtils.isEmpty(inspectionResult) ? "" : inspectionResult);
                binding.etSwitchoverResult.setText(TextUtils.isEmpty(inspectionResult) ? "" : inspectionResult);
                returnDateTime(currentReport.starttime, true);
                returnDateTime(currentReport.endtime, false);
            });

            try {
                List<ReportSignname> mList = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.worker.name());
                if (mList != null) {
                    StringBuilder nameBuilder = new StringBuilder();
                    for (int i = 0; i < mList.size(); i++) {
                        if (!TextUtils.isEmpty(mList.get(i).getAccount()) && !nameBuilder.toString().contains(mList.get(i).getAccount())) {
                            czrModels.add(mList.get(i).getAccount());
                            nameBuilder.append(mList.get(i).getAccount()).append(",");
                            mDataCzr.add(mList.get(i));
                        }
                    }
                    czrModelsOrigin.addAll(czrModels);
                }
                String userAccount = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
                List<DbModel> defaultUesrs = DepartmentService.getInstance().findUserForCurrentUser(userAccount);
                totalCountUser = defaultUesrs.size();
                mList = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.leader.name());
                if (mList != null) {
                    mDataFzr.addAll(mList);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                adapterGzr = new SignNameAdapter(mActivity, mDataCzr, GenerateReportActivity.this, MASKGzr);
                adapterGzr.setUserCount(totalCountUser);
                adapterFzr = new SignNameAdapter(mActivity, mDataFzr, GenerateReportActivity.this, MASKFzr);
                binding.signList.setAdapter(adapterGzr);
                binding.signList1.setAdapter(adapterFzr);
            });
        });
        ExecutorManager.executeTask(() -> persons = DepartmentService.getInstance().findAllUserForCurrentUser(currentAcounts));
    }

    /**
     * 显示记录缺陷的列表
     *
     * @param mDefectList
     * @param dialogTitle
     */
    private void showDefectDialog(List<DefectRecord> mDefectList, int dialogTitle) {
        if (mDefectList != null && !mDefectList.isEmpty()) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = mDefectList.size() > 5 ? ScreenUtils.getScreenHeight(mActivity) / 2
                    : LinearLayout.LayoutParams.WRAP_CONTENT;
            if (mHistoryDefectAdapter == null) {
                mHistoryDefectAdapter = new HistoryDefectAdapter(mActivity, mDefectList);
                mHistoryDefectAdapter.setTrackHistory(true);
            } else {
                mHistoryDefectAdapter.setList(mDefectList);
            }
            XsContentListDialogBinding listDialogBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mDefectDialog = DialogUtils.createDialog(mActivity, listDialogBinding.getRoot(), dialogWidth, dialogHeight);
            listDialogBinding.lvContainer.setAdapter(mHistoryDefectAdapter);
            listDialogBinding.tvDialogTitle.setText(dialogTitle);
            mDefectDialog.show();
        }
    }

    private void initOnClick() {
        binding.btnAddPerson.setOnClickListener(view -> {
            clickCzr = true;
            showAddPersonDialog();
        });

        binding.btnAddPerson1.setOnClickListener(view -> {
            clickCzr = false;
            showAddPersonDialog();
        });

        binding.llEliminateDefectCount.setOnClickListener(view -> showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));
        binding.tvEliminateDefectCount.setOnClickListener(view -> showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));

        binding.tvTrackDefectCount.setOnClickListener(view -> showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));
        binding.llTrackDefectCount.setOnClickListener(view -> showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));

        binding.tvNewDefectCount.setOnClickListener(view -> showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));
        binding.llNewDefectCount.setOnClickListener(view -> showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));

        binding.btnComplete.setOnClickListener(view -> finishReport());
        binding.llCopyResult.setOnClickListener(view -> {
            if (currentInspectionType.contains(InspectionType.switchover.name()) || currentInspectionType.contains(InspectionType.maintenance.name())) {
                return;
            }
            PlaySound.getIntance(mActivity).play(R.raw.input);
            Intent intent = new Intent(mActivity, CopyAllValueActivity2.class);
            startActivityForResult(intent, LOAD_DATA);
        });
        binding.llNewDefectCount.setOnClickListener(view -> showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));

        initOnLongClick();
    }


    private void finishReport() {
        Intent intent = null;
        try {
            if (xudianchi) {
                String testPersons = "";
                String testManagers = "";
                int cicrleCzr = 0;
                int cicrleFzr = 0;
                for (ReportSignname signname : mDataCzr) {
                    testPersons += signname.getName();
                    if (cicrleCzr + 1 < mDataCzr.size()) {
                        testPersons += ",";
                    }
                }
                for (ReportSignname signname : mDataFzr) {
                    testManagers += signname.getName();
                    if (cicrleFzr + 1 < mDataFzr.size()) {
                        testManagers += ",";
                    }
                }
                for (BatteryGroup group : mBatteryGroupList) {
                    group.testManager = testManagers;
                    group.testPersons = testPersons;
                    group.analysisResult = binding.etConlusion.getText().toString();
                }
                BatteryGroupService.getInstance().saveOrUpdate(mBatteryGroupList);
            }

            // 保存签名 完成任务 保存报告
            ReportSignnameService.getInstance().saveOrUpdate(mDataCzr);
            ReportSignnameService.getInstance().saveOrUpdate(mDataFzr);
            currentReport.inspectionContent = binding.inspectionContent.getText().toString();
            currentReport.inspectionRemark = binding.etRemark.getText().toString();
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                currentReport.inspectionResult = binding.etSwitchoverResult.getText().toString();
            } else {
                currentReport.inspectionResult = binding.etResult.getText().toString();
            }
            if (TextUtils.isEmpty(currentReport.endtime)) {
                currentReport.endtime = DateUtils.getCurrentLongTime();
            }
            com.cnksi.common.daoservice.ReportService.getInstance().saveOrUpdate(currentReport);
            Task mTask = new Task(currentTaskId);
            TaskService.getInstance().update(mTask, Task.STATUS);
            if (!TextUtils.isEmpty(currentReport.pmsJhid)) {
                NariDataManager.markBdPackageStatus(currentReport.pmsJhid, PackageStatus.done);
                NariActivity.isNeedUpdateStatus = true;
            }
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                intent = new Intent(mActivity, NewRegularReportActivity.class);
            } else {
                intent = new Intent(mActivity, InspectionReportActivity.class);
            }
            PushNewTaskUtil.getTaskUtilInstance().createNewTaskByPeriod(currentTaskId, currentInspectionType, currentInspectionTypeName);
            startActivity(intent);
            finish();
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void initOnLongClick() {

        binding.txtStartTime.setOnLongClickListener(view -> {
            TimePickerUtils.getPickerUtils().showDialog(mActivity, currentReport.starttime, currentReport.endtime, true);
            return true;
        });
        binding.txtEndTime.setOnLongClickListener(view -> {
            TimePickerUtils.getPickerUtils().showDialog(mActivity, currentReport.endtime, currentReport.starttime, false);
            return true;
        });

    }


    @Override
    public void returnDateTime(String dateString, boolean isStartTime) {
        if (TextUtils.isEmpty(dateString) && !isStartTime) {
            binding.txtEndTime.setText("结束时间：");
            return;
        }
        if (isStartTime) {
            if (!TextUtils.isEmpty(dateString)) {
                currentReport.starttime = dateString;
                binding.txtStartTime.setText(StringUtils.formatPartTextColor("开始时间：%s", Color.parseColor("#03B9A0"), dateString));
            }
        } else {
            if (DateUtils.compareDate(binding.txtStartTime.getText().toString().replace("开始时间：", ""), dateString, DateUtils.yyyy_MM_dd_HH_mm_ss)) {
                ToastUtils.showMessage("开始时间不能大于结束时间");
                return;
            }
            currentReport.endtime = dateString;
            binding.txtEndTime.setText(StringUtils.formatPartTextColor("结束时间：%s", Color.parseColor("#03B9A0"), dateString));
        }
        if (null != TimePickerUtils.getPickerUtils().pvCustomTime) {
            TimePickerUtils.getPickerUtils().pvCustomTime.dismiss();
        }
    }

    private XsDialogSignViewBinding signViewBinding;

    public void showSignDetails(int position, boolean isShowDelete, List<ReportSignname> list) {
        final ReportSignname bean = list.get(position);
        if (mSignDetailDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = LayoutParams.WRAP_CONTENT;
            signViewBinding = XsDialogSignViewBinding.inflate(getLayoutInflater());
            mSignDetailDialog = DialogUtils.createDialog(mActivity, signViewBinding.getRoot(), dialogWidth, dialogHeight);
        }
        if (isShowDelete) {
            signViewBinding.btnResign.setVisibility(View.VISIBLE);
            signViewBinding.btnDelete.setText(getString(R.string.xs_gp_delete_this_person));
        } else {
            signViewBinding.btnResign.setVisibility(View.GONE);
            signViewBinding.btnDelete.setText("确认");
        }
        signViewBinding.tvName.setText(bean.getName());
        signViewBinding.imgHead.setImageBitmap(BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + bean.getImg(), 600));
        signViewBinding.imgSign.setImageBitmap(BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + bean.getSignName(), 600));

        mSignDetailDialog.show();

        signViewBinding.btnDelete.setOnClickListener(view -> {
            if (isShowDelete) {
                removeSign(list, position);
            }
            mSignDetailDialog.dismiss();
        });
        signViewBinding.btnResign.setOnClickListener(view -> {
            currentSign = list.get(position);
            if (list == mDataCzr) {
                takeSignName(currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(mActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(mActivity, currentSign.getName()), SIGNCODEGzr);
            } else {
                takeSignName(currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(mActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(mActivity, currentSign.getName()), SIGNCODEFzr);
            }
            mSignDetailDialog.dismiss();
        });
    }

    PeopleDialog peopleDialog;
    PeopleDialog.Builder builder = new PeopleDialog.Builder();

    public void showAddPersonDialog() {
        peopleDialog = builder.setDeptId(currentDepartmentId).setItemClickListener((v, data, position) -> {
            peopleDialog.dismiss();
            getClickPerson(v, data, position);
        }).loadData().getPeopleDialog();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        peopleDialog.show(transaction, "peopleDialog");
    }

    private void getClickPerson(View v, DbModel model, int position) {
        boolean hasUser = false;
        if (clickCzr) {
            if (czrModels.contains(model.getString("account"))) {
                ToastUtils.showMessage("已经添加了该工作人员");
                return;
            }
            if (!hasUser) {
                czrModels.add(model.getString("account"));
            }
            czrModelsOrigin.clear();
            czrModelsOrigin.addAll(czrModels);
            listSign = mDataCzr;
        } else if (!clickCzr) {
            listSign = mDataFzr;
            if (fzrModels.contains(model.getString("account"))) {
                ToastUtils.showMessage("已经添加了该负责人员");
                return;
            }
            fzrModels.add(model.getString("account"));
        }

        listSign.add(new ReportSignname(currentReportId, listSign == mDataCzr ? Role.worker.name() : Role.leader.name(), model));
        if (listSign == mDataCzr) {
            adapterGzr.notifyDataSetChanged();
        } else {
            adapterFzr.notifyDataSetChanged();
        }
    }

    public void removeSign(final List<ReportSignname> list, final int position) {
        if (list.size() > 1) {
            DialogUtils.showSureTipsDialog(mActivity, null, "提示", "您确认要删除该人员吗？", "确认", "取消", v -> {
                ReportSignname reportSignname = list.remove(position);
                reportSignname.setDlt("1");
                try {
                    ReportSignnameService.getInstance().saveOrUpdate(reportSignname);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                adapterGzr.notifyDataSetChanged();
                adapterFzr.notifyDataSetChanged();
                if (clickCzr) {
                    czrModels.remove(position);
                    czrModelsOrigin.remove(position);
                } else {
                    fzrModels.remove(position);
                }
            });

        } else {
            ToastUtils.showMessageLong("至少有一个工作人员和负责人员");
        }

    }

    @Override
    public void OnAdapterClick(View v, final int position, int tag) {
        switch (tag) {
            case MASKGzr | 0:
                clickCzr = true;
                removeSign(mDataCzr, position);
                break;
            case MASKGzr | 1:
                currentSign = mDataCzr.get(position);
                takeSignName(currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(mActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(mActivity, currentSign.getName()), SIGNCODEGzr);
                break;
            case MASKGzr | 2:
                showSignDetails(position, true, mDataCzr);
                break;
            case MASKFzr | 0:
                clickCzr = false;
                removeSign(mDataFzr, position);

                break;
            case MASKFzr | 1:
                currentSign = mDataFzr.get(position);
                takeSignName(currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(mActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(mActivity, currentSign.getName()), SIGNCODEFzr);
                break;
            case MASKFzr | 2:
                showSignDetails(position, true, mDataFzr);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (currentSign == null) {
            return;
        }
        switch (requestCode) {
            case 0x300:
                currentSign.setImg(currentHeadPath);
                currentSign.setSignName(currentSignNamePath);
                adapterGzr.notifyDataSetChanged();
                showSignDetails(mDataCzr.indexOf(currentSign), false, mDataCzr);
                break;
            case 0x301:
                currentSign.setImg(currentHeadPath);
                currentSign.setSignName(currentSignNamePath);
                adapterFzr.notifyDataSetChanged();
                showSignDetails(mDataFzr.indexOf(currentSign), false, mDataFzr);
                break;
            case LOAD_DATA:
                copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                totalCount = CopyItemService.getInstance().getCopyTotalCount(currentBdzId, currentInspectionType);
                binding.tvCopyResult.setText(String.valueOf(copyCount) + "/" + String.valueOf(totalCount));
                break;
            default:
                break;
        }
    }

    public void takeSignName(String signer, String signPath, String headPath, int requestCode) {
        LandSignNameActivity.with(mActivity).setHeadPath(Config.CUSTOMER_PICTURES_FOLDER + headPath)
                .setRequestCode(requestCode)
                .setSigner(signer)
                .setSignPath(Config.CUSTOMER_PICTURES_FOLDER + signPath)
                .start();
    }

    @Override
    protected void releaseResAndSaveData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (TimePickerUtils.pickerUtils != null) {
            TimePickerUtils.pickerUtils.mActivity = null;
            TimePickerUtils.pickerUtils = null;
        }
    }
}
