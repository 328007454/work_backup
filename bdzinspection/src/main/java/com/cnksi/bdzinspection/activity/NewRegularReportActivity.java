package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.daoservice.BatteryGroupService;
import com.cnksi.bdzinspection.daoservice.StandardSwitchOverService;
import com.cnksi.bdzinspection.databinding.XsActivityInspectionReportBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.utils.AnimationUtils;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.ReportSignnameService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.Role;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.ReportSignname;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.ScreenUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.common.Config.REFRESH_DATA;

public class NewRegularReportActivity extends BaseActivity {

    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = 0x101;
    public static final int UPDATE_DEVICE_DEFECT_REQUEST_CODE = VIBRATOR + 1;
    private static final boolean ISREPORT = true;
    private boolean isDefectChanged = false;

    private Report report = null;
    /**
     *
     */
    private String status = "";
    /**
     * 抄录数据条数
     */
    private long copyCount = 0;
    /**
     * 抄录数据总条数
     */
    private long totalCount = 0;
    /*
     * 本次巡视发现缺陷总数
     */
    private List<DefectRecord> mNewDefectList = null;
    // 本次跟踪的缺陷
    private List<DefectRecord> mTrackDefectList = null;
    // 本次消除的缺陷
    private List<DefectRecord> mEliminateDefectList = null;
    private List<ReportSignname> mReportSignnameListCzr = null;
    private List<ReportSignname> mReportSignnameListFzr = null;
    private HistoryDefectAdapter mHistoryDefectAdapter = null;

    /**
     * 历史缺陷的dialog
     */
    private Dialog mDefectDialog = null;

    private XsActivityInspectionReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_inspection_report);
        initialUI();
        initialData();
        initOnClick();

    }


    private void initialUI() {
        getIntentValue();
        if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
            if (currentInspectionType.contains("switchover")) {
                binding.txtXunshiResult.setText("试验结果");
            } else {
                binding.txtXunshiResult.setText("维护结果");
            }
        }
        binding.ibtnExit.setImageResource(R.drawable.xs_return_main_menu_selector);
        binding.ibtnExit.setVisibility(View.GONE);
        binding.btXunjianLine.setVisibility(View.GONE);
        binding.layoutWeather.setVisibility(View.GONE);
        binding.layoutTemperature.setVisibility(View.GONE);
        binding.layoutInspection.setVisibility(View.VISIBLE);
        binding.layoutMark.setVisibility(View.GONE);
        binding.layoutXunshicontent.setVisibility(View.GONE);
        if (currentInspectionType.contains("maintenance")) {
            binding.tvInspectionType.setText("维护名称：");
        }
        binding.tvTitle.setText(getString(R.string.xs_report_title_format_str, currentBdzName + currentInspectionTypeName));
        binding.tvInspectionType.setText(currentInspectionTypeName);
        binding.tvDashLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        PlaySound.getIntance(mActivity).play(R.raw.printing);
        mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
        mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);
    }

    private void initialData() {
        final boolean xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
        ExecutorManager.executeTask(() -> {
            // 查询report数据
            try {
                report = ReportService.getInstance().getReportById(currentReportId);
                mReportSignnameListCzr = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.worker.name());
                mReportSignnameListFzr = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId, Role.leader.name());
                mHandler.sendEmptyMessage(REFRESH_DATA);
            } catch (DbException e) {
                e.printStackTrace(System.out);
            }
            // 查询状态
            status = TaskService.getInstance().getTaskStatus(currentTaskId);
            if (xudianchi) {
                try {

                    List<DbModel> batteryDbmodelList = BatteryGroupService.getInstance().findBatteryGroup(currentBdzId);
                    DbModel batteryCopyTotal = BatteryGroupService.getInstance().findAllBatteryCodeCount(currentBdzId, currentReportId);
                    int sumBatteryCode = 0;
                    for (DbModel dbModel : batteryDbmodelList) {
                        sumBatteryCode = sumBatteryCode + Integer.valueOf(dbModel.getString("amount"));
                    }
                    copyCount = Integer.valueOf(TextUtils.isEmpty(batteryCopyTotal.getString("count")) ? "0" : batteryCopyTotal.getString("count"));
                    totalCount = sumBatteryCode;
                } catch (DbException e) {
                    e.printStackTrace();
                }
            } else if ("maintenance_blqdzcs".equalsIgnoreCase(currentInspectionType)) {
                copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                totalCount = CopyItemService.getInstance().getCopyItemCount1(currentBdzId, CopyItemService.getInstance().getCopyType());
            } else {
                // 查询数据抄录数量
                copyCount = TaskService.getInstance().queryCopyData(currentReportId);
                totalCount = StandardSwitchOverService.getInstance().getSwitchOverCopyTotal(currentInspectionType, currentBdzId);
            }

            // 查询本次发现的缺陷
            mNewDefectList = DefectRecordService.getInstance().findCurrentTaskNewDefectList(currentBdzId, currentReportId);
            // 查询本次跟踪的缺陷
            mTrackDefectList = DefectRecordService.getInstance().findCurrentTaskTrackDefectList(currentBdzId, currentReportId);
            // 查询本次消除的缺陷
            mEliminateDefectList = DefectRecordService.getInstance().findCurrentTaskEliminateDefectList(currentBdzId, currentReportId);
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case ANIMATION:
                PlaySound.getIntance(mActivity).play(R.raw.print_out);
//			translateAnimRun(binding.llReportContentContainer, 0.0f, binding.llReportContentContainer.getHeight() * 92 / 100);
                LinearLayout.MarginLayoutParams marginLayoutParams = (LinearLayout.MarginLayoutParams) binding.llReportContentContainer.getLayoutParams();
                AnimationUtils.translateAnimRun(binding.llReportContentContainer, 0.0f, Math.abs(marginLayoutParams.topMargin));
                break;
            case VIBRATOR:
                mVibrator.vibrate(735);
                break;
            case LOAD_DATA:
                binding.tvNewDefectCount.setText(String.valueOf(mNewDefectList == null ? 0 : mNewDefectList.size()));
                binding.tvTrackDefectCount.setText(String.valueOf(mTrackDefectList == null ? 0 : mTrackDefectList.size()));
                binding.tvEliminateDefectCount.setText(String.valueOf(mEliminateDefectList == null ? 0 : mEliminateDefectList.size()));
                String copyCountStr = String.valueOf(copyCount) + "/" + String.valueOf(totalCount);
                binding.tvCopyResult.setText(copyCountStr);
                break;
            case REFRESH_DATA:
                // 设置报告的详细信息
                if (report != null) {
                    binding.tvInspectionStartTime.setText(DateUtils.getFormatterTime(report.starttime, DateUtils.yyyy_MM_dd_HH_mm));
                    binding.tvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime) ? DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm) : DateUtils.getFormatterTime(report.endtime, DateUtils.yyyy_MM_dd_HH_mm));
                    binding.tvInspectionWorker.setText(getName(mReportSignnameListCzr));
                    binding.tvInspectionLeader.setText(getName(mReportSignnameListFzr));
                }
                initSignName();
                break;
            default:
                break;
        }
    }

    private String getName(List<ReportSignname> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (ReportSignname bean : list) {
            str.append(bean.getName() + ",");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    private void initSignName() {
        float scale = DisplayUtils.getInstance().getDensity();
        if (mReportSignnameListCzr != null) {
            for (ReportSignname bean : mReportSignnameListCzr) {
                ImageView v = new ImageView(mActivity);
                v.setLayoutParams(new LayoutParams((int) (scale * 90), (int) (scale * 40)));
                v.setPadding((int) (scale * 5), (int) (scale * 5), (int) (scale * 5), (int) (scale * 5));
                v.setScaleType(ScaleType.FIT_CENTER);
                Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + bean.getSignName(), (int) (scale * 90));
                if (bitmap != null) {
                    v.setImageBitmap(bitmap);
                }
            }
        }
        if (mReportSignnameListFzr != null) {
            for (ReportSignname bean : mReportSignnameListFzr) {
                ImageView v = new ImageView(mActivity);
                v.setLayoutParams(new LayoutParams((int) (scale * 90), (int) (scale * 40)));
                v.setPadding((int) (scale * 5), (int) (scale * 5), (int) (scale * 5), (int) (scale * 5));
                v.setScaleType(ScaleType.FIT_CENTER);
                Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + bean.getSignName(), (int) (scale * 90));
                if (bitmap != null) {
                    v.setImageBitmap(bitmap);
                }
                binding.llSignContainer1.addView(v);
            }
        }
    }

    private void initOnClick() {

        binding.ibtnCancel.setOnClickListener(view -> NewRegularReportActivity.this.onBackPressed());

        binding.tvEliminateDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));
        binding.llEliminateDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));

        binding.tvTrackDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));
        binding.llTrackDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));

        binding.tvNewDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));
        binding.llNewDefectCount.setOnClickListener(view -> NewRegularReportActivity.this.showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));

        binding.ibtnExit.setOnClickListener(view -> NewRegularReportActivity.this.ExitThisAndGoLauncher());

    }


    /**
     * 显示记录缺陷的列表
     *
     * @param mDefectList
     * @param dialogTitle
     */
    XsContentListDialogBinding defectBinding;

    private void showDefectDialog(List<DefectRecord> mDefectList, int dialogTitle) {
        if (mDefectList != null && !mDefectList.isEmpty()) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = mDefectList.size() > 5 ? ScreenUtils.getScreenHeight(mActivity) / 2 : LinearLayout.LayoutParams.WRAP_CONTENT;
            if (mHistoryDefectAdapter == null) {
                mHistoryDefectAdapter = new HistoryDefectAdapter(mActivity, mDefectList);
                mHistoryDefectAdapter.setTrackHistory(true);
            } else {
                mHistoryDefectAdapter.setList(mDefectList);
            }
            defectBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mDefectDialog = DialogUtils.createDialog(mActivity, defectBinding.getRoot(), dialogWidth, dialogHeight);
            defectBinding.lvContainer.setAdapter(mHistoryDefectAdapter);
            defectBinding.tvDialogTitle.setText(dialogTitle);
            mDefectDialog.show();

            defectBinding.lvContainer.setOnItemClickListener((parent, v, position, id) -> {
                DefectRecord mDefectRecord = (DefectRecord) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(Config.TRACK_DEFECT_RECORD_ID, mDefectRecord.defectid);
                intent.putExtra(Config.CURRENT_DEVICE_ID, mDefectRecord.deviceid);
                intent.putExtra(Config.CURRENT_DEVICE_NAME, mDefectRecord.devcie);
                intent.putExtra(Config.CURRENT_SPACING_ID, mDefectRecord.spid);
                intent.putExtra(Config.CURRENT_SPACING_NAME, mDefectRecord.spname);
                intent.putExtra(Config.CURRENT_DEVICE_PART_ID, mDefectRecord.duid);
                intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, mDefectRecord.duname);
                intent.putExtra(Config.CURRENT_STANDARD_ID, mDefectRecord.standid);
                intent.putExtra(Config.IS_TRACK_DEFECT, true);
            });
        }
    }

    @Override
    public void onBackPressed() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(TaskRemindActivity.class);
        if (isDefectChanged) {
            setResult(RESULT_OK);
        } else if (!isFromTaskRemind) {
            isNeedUpdateTaskStatus = true;
        }
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ExitThisAndGoLauncher();
                break;
            default:
                break;
        }

        return true;
    }
}
