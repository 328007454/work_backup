package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.AdapterClickListener;
import com.cnksi.bdzinspection.adapter.AddPersonAdapter;
import com.cnksi.bdzinspection.adapter.SignNameAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.ReportService;
import com.cnksi.bdzinspection.daoservice.TaskService;
import com.cnksi.bdzinspection.databinding.XsActivityGenerateReportBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogAddPersonBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSignViewBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.bdzinspection.model.ReportSignname;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.Role;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.PushNewTaskUtil;
import com.cnksi.bdzinspection.utils.TimePickerUtils;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.nari.NariActivity;
import com.cnksi.nari.type.PackageStatus;
import com.cnksi.nari.utils.NariDataManager;
import com.cnksi.xscore.xscommon.ScreenManager;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.StringUtils;

import org.xutils.common.util.KeyValue;
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
public class GenerateReportActivity extends TitleActivity implements AdapterClickListener, ItemClickListener, TimePickerUtils.TimePickerMissListener {
    public final int MASKGzr = 0x0;
    public final int MASKFzr = 0xFF00;
    public final int SIGNCODEGzr = 0x300;
    public final int SIGNCODEFzr = 0x301;

    List<ReportSignname> mDataCzr = new ArrayList<ReportSignname>();
    List<ReportSignname> mDataFzr = new ArrayList<ReportSignname>();
    SignNameAdapter adapterGzr;
    SignNameAdapter adapterFzr;
    String currentSignNamePath;
    String currentHeadPath;
    ReportSignname currentSign;
    Report currentReport;
    Dialog mAddPersonDialog;
    //    DialogViewHolder dialogViewHolder;
    AddPersonAdapter dialogAdpeter;
    List<DbModel> persons;
    Dialog mSignDetailDialog;
    String inspectionMark;
    String inspectionResult;
    String inspectionContent;
    boolean xudianchi;
    XsDialogAddPersonBinding mPersonBinding;
    List<ReportSignname> list;
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
            if (currentInspectionType.equalsIgnoreCase(Config.InspectionType.routine.name()) || currentInspectionType.equalsIgnoreCase(Config.InspectionType.full.name())) {
                copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                totalCount = CopyItemService.getInstance().getCopyItemCount(currentBdzId, currentInspectionType);
                binding.tvCopyResult.setText(String.valueOf(copyCount) + "/" + String.valueOf(totalCount));
            }
        } else {
            isFirstEnter = false;
        }

    }

    @Override
    protected void initialData() {
        getIntentValue();
        if (!TextUtils.isDigitsOnly(currentInspectionType))
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
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
        xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
        if (xudianchi) {
            binding.layoutConclusion.setVisibility(View.VISIBLE);
        }

        mFixedThreadPoolExecutor.execute(() -> {
            try {
                currentReport = XunshiApplication.getDbUtils().selector(Report.class).where(Report.REPORTID, "=", currentReportId).findFirst();
                inspectionMark = currentReport.inspectionRemark;
                inspectionResult = currentReport.inspectionResult;
                if ((!isParticularInspection()) && (!isRoutineNotCopy())) {
                    copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                    totalCount = CopyItemService.getInstance().getCopyItemCount(currentBdzId, currentInspectionType);
                    if (xudianchi) {
                        List<DbModel> batteryDbmodelList = ReportService.getInstance().findBatteryGroup(currentBdzId);
                        DbModel batteryCopyTotal = ReportService.getInstance().findAllBatteryCodeCount(currentBdzId, currentReportId);
                        int sumBatteryCode = 0;
                        for (DbModel dbModel : batteryDbmodelList) {
                            sumBatteryCode = sumBatteryCode + Integer.valueOf(dbModel.getString("amount"));
                        }
                        copyCount = Integer.valueOf(TextUtils.isEmpty(batteryCopyTotal.getString("count")) ? "0" : batteryCopyTotal.getString("count"));
                        totalCount = sumBatteryCode;
                        mBatteryGroupList = ReportService.getInstance().findAllBatteryGroup(currentBdzId, currentReportId);
                        if (mBatteryGroupList.isEmpty() && !batteryDbmodelList.isEmpty()) {
                            BatteryGroup batteryGroup = new BatteryGroup(currentReportId, currentBdzId, currentBdzName, batteryDbmodelList.get(0).getString("bid"));
                            mBatteryGroupList.add(batteryGroup);
                        }
                    } else if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                        if (currentInspectionType.equalsIgnoreCase("maintenance_blqdzcs")) {
                            copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                            totalCount = CopyItemService.getInstance().getCopyItemCount1(currentBdzId, CopyItemService.getInstance().getCopyType());
                        } else {
                            copyCount = TaskService.getInstance().queryCopyData(currentReportId);
                            totalCount = TaskService.getInstance().getSwitchOverCopyTotal(currentInspectionType, currentBdzId);
                        }

                    }
                } else {
                    if (currentInspectionType.startsWith("special_manual") && TextUtils.isEmpty(inspectionMark)) {

                        String selectDevices = "'" + (currentReport.selected_deviceid == null ? "" : currentReport.selected_deviceid) + "'";
                        selectDevices = selectDevices.replace(",", "','");
                        DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo("SELECT group_concat(name,',') as rs  FROM	device WHERE deviceid IN (" + selectDevices + ")"));
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
//                    DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo("SELECT inspection_content FROM 'lookup' where k=?;", currentInspectionType));
                String sql = "SELECT xjnr,remark,xsjg FROM 'lookup_local' where k=?;";
                SqlInfo sqlInfo = new SqlInfo(sql);
                sqlInfo.addBindArg(new KeyValue("",currentInspectionType));
                DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
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
                returnDateTime(currentReport.starttime, true);
                returnDateTime(currentReport.endtime, false);
            });

            try {
                List<ReportSignname> mList = ReportService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.worker.name());
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
                String userAccount = (String) PreferencesUtils.get(currentActivity, Config.CURRENT_LOGIN_ACCOUNT, "");
                List<DbModel> defaultUesrs = DepartmentService.getInstance().findUserForCurrentUser(userAccount);
                totalCountUser = defaultUesrs.size();
                mList = ReportService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.leader.name());
                if (mList != null) {
                    mDataFzr.addAll(mList);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                adapterGzr = new SignNameAdapter(currentActivity, mDataCzr, GenerateReportActivity.this, MASKGzr);
                adapterGzr.setUserCount(totalCountUser);
                adapterFzr = new SignNameAdapter(currentActivity, mDataFzr, GenerateReportActivity.this, MASKFzr);
                binding.signList.setAdapter(adapterGzr);
                binding.signList1.setAdapter(adapterFzr);
            });
        });
        mFixedThreadPoolExecutor.execute(() -> persons = DepartmentService.getInstance().findAllUserForCurrentUser(currentAcounts));
    }

    /**
     * 显示记录缺陷的列表
     *
     * @param mDefectList
     * @param dialogTitle
     */
    private void showDefectDialog(List<DefectRecord> mDefectList, int dialogTitle) {
        if (mDefectList != null && !mDefectList.isEmpty()) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = mDefectList.size() > 5 ? ScreenUtils.getScreenHeight(currentActivity) / 2
                    : LinearLayout.LayoutParams.WRAP_CONTENT;
            if (mHistoryDefectAdapter == null) {
                mHistoryDefectAdapter = new HistoryDefectAdapter(currentActivity, mDefectList);
                mHistoryDefectAdapter.setTrackHistory(true);
            } else {
                mHistoryDefectAdapter.setList(mDefectList);
            }
            XsContentListDialogBinding listDialogBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mDefectDialog = DialogUtils.createDialog(currentActivity, listDialogBinding.getRoot(), dialogWidth, dialogHeight);
            listDialogBinding.lvContainer.setAdapter(mHistoryDefectAdapter);
            listDialogBinding.tvDialogTitle.setText(dialogTitle);
            mDefectDialog.show();
        }
    }

    private void initOnClick() {
        binding.btnAddPerson.setOnClickListener(view -> {
            clickCzr = true;
            showAddPersonDialog(mDataCzr);
        });

        binding.btnAddPerson1.setOnClickListener(view -> {
            clickCzr = false;
            showAddPersonDialog(mDataFzr);
        });

        binding.llEliminateDefectCount.setOnClickListener(view -> showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));
        binding.tvEliminateDefectCount.setOnClickListener(view -> showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));

        binding.tvTrackDefectCount.setOnClickListener(view -> showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));
        binding.llTrackDefectCount.setOnClickListener(view -> showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));

        binding.tvNewDefectCount.setOnClickListener(view -> {
            showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str);
        });
        binding.llNewDefectCount.setOnClickListener(view -> {
            showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str);
        });

        binding.btnComplete.setOnClickListener(view -> {
            finishReport();
        });
        binding.llCopyResult.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            Intent intent = new Intent(currentActivity, CopyAllValueActivity2.class);
            startActivityForResult(intent, LOAD_DATA);
        });
        binding.llNewDefectCount.setOnClickListener(view -> {
            showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str);
        });

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
                ReportService.getInstance().saveBatteryGroup(mBatteryGroupList);
            }

            // 保存签名 完成任务 保存报告
            ReportService.getInstance().saveSignName(mDataCzr);
            ReportService.getInstance().saveSignName(mDataFzr);
            currentReport.inspectionContent = binding.inspectionContent.getText().toString();
            currentReport.inspectionRemark = binding.etRemark.getText().toString();
            currentReport.inspectionResult = binding.etResult.getText().toString();
            if (TextUtils.isEmpty(currentReport.endtime))
                currentReport.endtime = DateUtils.getCurrentLongTime();
            XunshiApplication.getDbUtils().saveOrUpdate(currentReport);
            Task mTask = new Task(currentTaskId);
            XunshiApplication.getDbUtils().update(mTask, Task.STATUS);
            if (!TextUtils.isEmpty(currentReport.pmsJhid)) {
                NariDataManager.markBdPackageStatus(currentReport.pmsJhid, PackageStatus.done);
                NariActivity.isNeedUpdateStatus = true;
            }
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                intent = new Intent(currentActivity, NewRegularReportActivity.class);
            } else {
                intent = new Intent(currentActivity, InspectionReportActivity.class);
            }
            PushNewTaskUtil.getTaskUtilInstance().createNewTaskByPeriod(currentTaskId, currentInspectionType);
            ScreenManager.getInstance().popAllActivityExceptOne(null);
            startActivity(intent);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void initOnLongClick() {

        binding.txtStartTime.setOnLongClickListener(view -> {
            TimePickerUtils.getPickerUtils().showDialog(currentActivity, currentReport.starttime, currentReport.endtime, true);
            return true;
        });
        binding.txtEndTime.setOnLongClickListener(view -> {
            TimePickerUtils.getPickerUtils().showDialog(currentActivity, currentReport.endtime, currentReport.starttime, false);
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
            currentReport.starttime = dateString;
            String formatString = "开始时间：" + dateString;
            SpannableString spannableString = StringUtils.formStringSpannable(formatString, 5, formatString.length(), "#FFE3E9E8", Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            binding.txtStartTime.setText(spannableString);
        } else {
            if (DateUtils.compareDate(binding.txtStartTime.getText().toString().replace("开始时间：", ""), dateString, CoreConfig.dateFormat2)) {
                CToast.showShort(currentActivity, "开始时间不能大于结束时间");
                return;
            }
            currentReport.endtime = dateString;
            String formatString = "结束时间：" + dateString;
            SpannableString spannableString = StringUtils.formStringSpannable(formatString, 5, formatString.length(), "#FFE3E9E8", Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            binding.txtEndTime.setText(spannableString);
        }
        if (null != TimePickerUtils.getPickerUtils().pvCustomTime)
            TimePickerUtils.getPickerUtils().pvCustomTime.dismiss();
    }

    private XsDialogSignViewBinding signViewBinding;

    public void showSignDetails(int position, boolean isShowDelete, List<ReportSignname> list) {
        final ReportSignname bean = list.get(position);
        if (mSignDetailDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = LayoutParams.WRAP_CONTENT;
            signViewBinding = XsDialogSignViewBinding.inflate(getLayoutInflater());
            mSignDetailDialog = DialogUtils.createDialog(currentActivity, signViewBinding.getRoot(), dialogWidth, dialogHeight);
        }
        if (isShowDelete) {
            signViewBinding.btnResign.setVisibility(View.VISIBLE);
            signViewBinding.btnDelete.setText(getString(R.string.xs_gp_delete_this_person));
        } else {
            signViewBinding.btnResign.setVisibility(View.GONE);
            signViewBinding.btnDelete.setText("确认");
        }
        signViewBinding.tvName.setText(bean.getName());
        signViewBinding.imgHead.setImageBitmap(BitmapUtils.getImageThumbnailByWidth( Config.CUSTOMER_PICTURES_FOLDER + bean.getImg(),600));
        signViewBinding.imgSign.setImageBitmap(BitmapUtils.getImageThumbnailByWidth( Config.CUSTOMER_PICTURES_FOLDER + bean.getSignName(),600));

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
                takeSignName(currentActivity, currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(currentActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(currentActivity, currentSign.getName()), SIGNCODEGzr);
            } else {
                takeSignName(currentActivity, currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(currentActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(currentActivity, currentSign.getName()), SIGNCODEFzr);
            }
            mSignDetailDialog.dismiss();
        });
    }

    public void showAddPersonDialog(final List<ReportSignname> list) {
        if (mAddPersonDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 6 / 10;
            mPersonBinding = XsDialogAddPersonBinding.inflate(getLayoutInflater());
            mAddPersonDialog = DialogUtils.createDialog(currentActivity, mPersonBinding.getRoot(), dialogWidth, dialogHeight);
        }
        dialogAdpeter = new AddPersonAdapter(currentActivity, persons);
        mPersonBinding.lvContainer.setAdapter(dialogAdpeter);
        dialogAdpeter.setOnItemClickListener(this);
        mAddPersonDialog.show();
        this.list = list;
        mPersonBinding.btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mPersonBinding.etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    CToast.showShort(currentActivity, "名字不能为空！");
                    return;
                }
                DbModel model = new DbModel();
                model.add("username", mPersonBinding.etName.getText().toString());
                model.add(ReportSignname.DEPTID, "-1");
                model.add(ReportSignname.DEPTNAME, "");
                model.add(ReportSignname.ACCOUNT, FunctionUtil.getPrimarykey());
                persons.add(model);
                dialogAdpeter.notifyDataSetChanged();
                mPersonBinding.lvContainer.smoothScrollToPosition(mPersonBinding.lvContainer.getCount());
                mPersonBinding.etName.setText("");
            }
        });
    }

    @Override
    public void onItemClick(View v, Object o, int position) {
        DbModel model = persons.get(position);
        boolean hasUser = false;
        if (clickCzr) {
            if (czrModels.contains(model.getString("account"))) {
                CToast.showShort(getApplicationContext(), "已经添加了该工作人员");
                return;
            }
            if (!hasUser) {
                czrModels.add(model.getString("account"));
            }
            czrModelsOrigin.clear();
            czrModelsOrigin.addAll(czrModels);
        } else if (!clickCzr) {
            if (fzrModels.contains(model.getString("account"))) {
                CToast.showShort(getApplicationContext(), "已经添加了该负责人员");
                return;
            }
            fzrModels.add(model.getString("account"));
        }

        list.add(new ReportSignname(currentReportId, list == mDataCzr ? Role.worker.name() : Role.leader.name(), model));
        mAddPersonDialog.dismiss();
        if (list == mDataCzr) {
            adapterGzr.notifyDataSetChanged();
        } else {
            adapterFzr.notifyDataSetChanged();
        }
    }

    public void removeSign(final List<ReportSignname> list, final int position) {
        if (list.size() > 1) {
            DialogUtils.showSureTipsDialog(currentActivity, null, "提示", "您确认要删除该人员吗？", "确认", "取消", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    ReportSignname reportSignname = list.remove(position);
                    reportSignname.setDlt("1");
                    try {
                        XunshiApplication.getDbUtils().saveOrUpdate(reportSignname);
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
                }
            });

        } else {
            CToast.showLong(currentActivity, "至少有一个工作人员和负责人员");
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
                takeSignName(currentActivity, currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(currentActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(currentActivity, currentSign.getName()), SIGNCODEGzr);
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
                takeSignName(currentActivity, currentSign.getName(), currentSignNamePath = FunctionUtil.getSignImageName(currentActivity, currentSign.getName()), currentHeadPath = FunctionUtil.getSignImageHead(currentActivity, currentSign.getName()), SIGNCODEFzr);
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
        if (resultCode != RESULT_OK) return;
        if (currentSign == null) return;
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
                totalCount = CopyItemService.getInstance().getCopyItemCount(currentBdzId, currentInspectionType);
                binding.tvCopyResult.setText(String.valueOf(copyCount) + "/" + String.valueOf(totalCount));
                break;
            default:
                break;
        }
    }

    public void takeSignName(Activity context, String signer, String signPath, String headPath, int requestCode) {
        Intent intent = new Intent(context, LandSignNameActivity.class);
        intent.putExtra(LandSignNameActivity.SIGNER, signer);
        intent.putExtra(LandSignNameActivity.SIGNNAME_PATH, Config.CUSTOMER_PICTURES_FOLDER + signPath);
        intent.putExtra(LandSignNameActivity.HEAD_PATH, Config.CUSTOMER_PICTURES_FOLDER + headPath);
        startActivityForResult(intent, requestCode);
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
