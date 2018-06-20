package com.cnksi.bdzinspection.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
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
import com.cnksi.bdzinspection.adapter.RoadMapAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.daoservice.PlacedService;
import com.cnksi.bdzinspection.databinding.XsActivityInspectionReportBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsHistoryDataDialogBinding;
import com.cnksi.bdzinspection.model.Placed;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.ReportSignnameService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.Role;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.ReportSignname;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.nari.NariActivity;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 全面 日常 特殊巡检（大风 大雾 下雪 高温 夜间 雷雨）巡检报告
 *
 * @author terry
 */
public class InspectionReportActivity extends BaseActivity {

    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = 0x101;
    public static final int UPDATE_DEVICE_DEFECT_REQUEST_CODE = VIBRATOR + 1;
    private static final boolean ISREPORT = true;
    private boolean isDefectChanged = false;

    /**
     * 轨迹回放的dialog
     */
    private Dialog mPlayBackDialog = null;

    private HistoryDefectAdapter mHistoryDefectAdapter = null;
    private RoadMapAdapter roadMapAdapter = null;
    /**
     * 历史缺陷的dialog
     */
    private Dialog mDefectDialog = null;
    /**
     * 本次巡视发现缺陷总数
     */
    private List<DefectRecord> mNewDefectList = null;
    // 本次跟踪的缺陷
    private List<DefectRecord> mTrackDefectList = null;
    // 本次消除的缺陷
    private List<DefectRecord> mEliminateDefectList = null;

    private List<ReportSignname> mReportSignnameListCzr = null;
    private List<ReportSignname> mReportSignnameListFzr = null;
    /**
     * 间隔集合
     */

    private List<Spacing> spacingList = new ArrayList<Spacing>();

    private ArrayList<String> placedSpacing;

    private Report report = null;
    /**
     * 抄录数据条数
     */
    private long copyCount = 0;
    /**
     * 抄录数据总条数
     */
    private long totalCount = 0;

    /**
     *
     */
    private String status = "";
    // 当前设备类型
    private String fucntionModel = "one";
    //
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
        if (isParticularInspection() || isRoutineNotCopy()) {
            binding.llCopyResult.setVisibility(View.GONE);
            binding.lineCopyResult.setVisibility(View.GONE);
        }
        binding.tvTitle.setText(getString(R.string.xs_report_title_format_str, currentBdzName + currentInspectionTypeName));
        binding.tvDashLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        PlaySound.getIntance(mActivity).play(R.raw.printing);
        mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
        mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);
        binding.ibtnExit.setVisibility(View.GONE);
        binding.btXunjianLine.setVisibility(View.VISIBLE);
        if (currentInspectionType.equals(InspectionType.full.name())
                || currentInspectionType.equalsIgnoreCase(InspectionType.professional.name())
                || currentInspectionType.equals(InspectionType.day.name())
                || currentInspectionType.equals(InspectionType.routine.name())) {
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.svContainer.getLayoutParams();
            params.height = params.height + getResources().getDimensionPixelOffset(R.dimen.xs_playback_button_height);
            params.setMargins(0, 0, 0, 36);
            binding.svContainer.setLayoutParams(params);
        }
    }


    private void initOnClick() {
        binding.btXunjianLine.setOnClickListener(view -> InspectionReportActivity.this.showXunJianLineDialog());

        binding.tvNewDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));
        binding.llNewDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mNewDefectList, R.string.xs_new_defect_count_str));

        binding.tvTrackDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));
        binding.llTrackDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mTrackDefectList, R.string.xs_track_count_str));

        binding.tvEliminateDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));
        binding.llEliminateDefectCount.setOnClickListener(view -> InspectionReportActivity.this.showDefectDialog(mEliminateDefectList, R.string.xs_clear_count_str));

        binding.btnPlayback.setOnClickListener(view -> InspectionReportActivity.this.showPlaybackDialog());
        binding.ibtnExit.setOnClickListener(view -> InspectionReportActivity.this.ExitThisAndGoLauncher());
        binding.ibtnCancel.setOnClickListener(view -> InspectionReportActivity.this.ExitThisAndGoLauncher());
    }

    private void initialData() {

        ExecutorManager.executeTask(() -> {
            // 查询report数据
            report = ReportService.getInstance().getReportById(currentReportId);

            // 查询状态
            status = TaskService.getInstance().getTaskStatus(currentTaskId);
            // 查询数据抄录数量

            if (!InspectionReportActivity.this.isParticularInspection()) {
                copyCount = CopyResultService.getInstance().getReportCopyCount(currentReportId);
                totalCount = CopyItemService.getInstance().getCopyItemCount(currentBdzId, currentInspectionType);
            }
            // 查询本次发现的缺陷
            mNewDefectList = DefectRecordService.getInstance().findCurrentTaskNewDefectList(currentBdzId,
                    currentReportId);
            // 查询本次跟踪的缺陷
            mTrackDefectList = DefectRecordService.getInstance().findCurrentTaskTrackDefectList(currentBdzId,
                    currentReportId);
            // 查询本次消除的缺陷
            mEliminateDefectList = DefectRecordService.getInstance()
                    .findCurrentTaskEliminateDefectList(currentBdzId, currentReportId);
            try {
                mReportSignnameListCzr = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId,
                        Role.worker.name());
                mReportSignnameListFzr = ReportSignnameService.getInstance().getSignNamesForReportAndRole(currentReportId,
                        Role.leader.name());
            } catch (DbException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);

            /** 查询间隔展示巡视路线图 **/
            if (currentInspectionType.equals(InspectionType.full.name())
                    || currentInspectionType.equals(InspectionType.day.name())
                    || currentInspectionType.equalsIgnoreCase(InspectionType.professional.name())
                    || currentInspectionType.equals(InspectionType.routine.name())) {
                String sort = "one".equals(fucntionModel) ? Spacing.SORT_ONE
                        : "second".equals(fucntionModel) ? Spacing.SORT_SECOND : Spacing.SORT;
                currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");

                try {

                    spacingList = SpacingService.getInstance().findByFunctionModel(currentBdzId, fucntionModel, sort);
                } catch (DbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            InspectionReportActivity.this.placedSpacing();
        });

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
                binding.llSignContainer.addView(v);
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

    /**
     * 已到间隔
     */
    private void placedSpacing() {
        if (!isParticularInspection()) {
            List<Placed> placedList = PlacedService.getInstance().findPlacedSpace(currentReportId);
            if (null != placedList && !placedList.isEmpty()) {
                placedSpacing = new ArrayList<String>();
                for (Placed p : placedList) {
                    placedSpacing.add(p.spId);
                }
            }
        }

    }

    /**
     * 展示巡检路线对话框
     */
    private XsContentListDialogBinding dialogBinding;

    private void showXunJianLineDialog() {
        if (!spacingList.isEmpty()) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 4 / 5;
            int dialogHeight = ScreenUtils.getScreenHeight(mActivity) * 3 / 5;
            if (roadMapAdapter == null) {
                roadMapAdapter = new RoadMapAdapter(mActivity, spacingList);
            } else {
                roadMapAdapter.setList(spacingList);
            }
            roadMapAdapter.setSpacingRecord(placedSpacing);
            roadMapAdapter.setIsReport(true);
            dialogBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mDefectDialog = DialogUtils.createDialog(mActivity, dialogBinding.getRoot(), dialogWidth, dialogHeight);
            dialogBinding.lvContainer.setAdapter(roadMapAdapter);
            dialogBinding.tvDialogTitle.setText("巡检路线");
            mDefectDialog.show();
        }
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case ANIMATION:
                PlaySound.getIntance(mActivity).play(R.raw.print_out);
//			translateAnimRun(binding.llReportContentContainer, 0.0f, binding.llReportContentContainer.getHeight() * 92 / 100);
                LinearLayout.MarginLayoutParams marginLayoutParams = (LinearLayout.MarginLayoutParams) binding.llReportContentContainer.getLayoutParams();
                translateAnimRun(binding.llReportContentContainer, 0.0f, Math.abs(marginLayoutParams.topMargin));

                break;
            case VIBRATOR:
                mVibrator.vibrate(735);
                break;
            case LOAD_DATA:
                binding.tvNewDefectCount.setText(String.valueOf(mNewDefectList == null ? 0 : mNewDefectList.size()));
                binding.tvTrackDefectCount.setText(String.valueOf(mTrackDefectList == null ? 0 : mTrackDefectList.size()));
                binding.tvEliminateDefectCount.setText(String.valueOf(mEliminateDefectList == null ? 0 : mEliminateDefectList.size()));
                String copyCountStr = String.valueOf(copyCount) + "/" + String.valueOf(totalCount);
                String result = "";
                if (currentInspectionType.equalsIgnoreCase(InspectionType.full.name())
                        || currentInspectionType.equalsIgnoreCase(InspectionType.day.name())
                        || currentInspectionType.equalsIgnoreCase(InspectionType.professional.name())
                        || currentInspectionType.equalsIgnoreCase(InspectionType.routine.name())
                        || currentInspectionType.contains("special")) {
                    binding.tvCopyResult.setText(copyCountStr);
                } else {
                    if (!TextUtils.isEmpty(currentInspectionType)) {
                        result = getString(R.string.xs_inspection_status_format_str, "已完成");
                        binding.tvInspectionResult.setText(StringUtils.changePartTextColor(mActivity, result,
                                R.color.xs_red_color, result.length() - status.length(), result.length()));
//                        mTvContinueXunJian.setText("查看详情");
                    } else {
                        result = getString(R.string.xs_inspection_status_format_str, status);
                        binding.tvInspectionResult.setText(StringUtils.changePartTextColor(mActivity, result,
                                R.color.xs_red_color, result.length() - status.length(), result.length()));
                    }
                }

                // 设置报告的详细信息
                if (report != null) {
                    binding.tvInspectionStartTime.setText(TextUtils.isEmpty(report.starttime) ? "" : DateUtils.getFormatterTime(report.starttime, DateUtils.yyyy_MM_dd_HH_mm));
                    binding.tvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime)
                            ? DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm)
                            : DateUtils.getFormatterTime(report.endtime, DateUtils.yyyy_MM_dd_HH_mm));
                    binding.tvInspectionWorker.setText(getName(mReportSignnameListCzr));
                    binding.tvInspectionLeader.setText(getName(mReportSignnameListFzr));
                    binding.tvInspectionTemperature.setText(TextUtils.isEmpty(report.temperature) ? "" : (report.temperature.contains(getString(R.string.xs_temperature_unit_str))
                            ? report.temperature : report.temperature + getString(R.string.xs_temperature_unit_str)));
                    binding.tvInspectionTq.setText(TextUtils.isEmpty(report.tq) ? "" : report.tq);

                    binding.tvInspectionContent.setText(TextUtils.isEmpty(report.inspectionContent) ? "" : report.inspectionContent);
                    binding.tvInspectionMark.setText(TextUtils.isEmpty(report.inspectionContent) ? "" : report.inspectionRemark);
                    binding.tvInspectionResult.setText(TextUtils.isEmpty(report.inspectionResult) ? "" : report.inspectionResult);

                }
                initSignName();
                break;
            default:
                break;
        }
    }

    private String getName(List<ReportSignname> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        for (ReportSignname bean : list) {
            str.append(bean.getName() + ",");
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    /**
     * 启动打印小票动画
     */
    public void translateAnimRun(final View view, float... values) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translate", values).setDuration(2800);
        anim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        anim.start();
        anim.addUpdateListener(animation -> {
            float cVal = (Float) animation.getAnimatedValue();
            view.setTranslationY(cVal);
        });

    }

    private XsHistoryDataDialogBinding dataDialogBinding;

    private void showPlaybackDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        if (mPlayBackDialog == null) {
            dataDialogBinding = XsHistoryDataDialogBinding.inflate(getLayoutInflater());
            mPlayBackDialog = DialogUtils.createDialog(mActivity, dataDialogBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        dataDialogBinding.tvDialogTitle.setText(R.string.xs_playback_str);
        dataDialogBinding.btnCancel.setText(R.string.xs_back_str);
        mPlayBackDialog.show();
        dataDialogBinding.btnCancel.setOnClickListener(view -> mPlayBackDialog.dismiss());
    }


    /**
     * 显示记录缺陷的列表
     *
     * @param mDefectList
     * @param dialogTitle
     */
    private XsContentListDialogBinding defectBinding;

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
            defectBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mDefectDialog = DialogUtils.createDialog(mActivity, defectBinding.getRoot(), dialogWidth, dialogHeight);
            defectBinding.lvContainer.setAdapter(mHistoryDefectAdapter);
            defectBinding.tvDialogTitle.setText(dialogTitle);
            mDefectDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case UPDATE_DEVICE_DEFECT_REQUEST_CODE:
                    // 缺陷更改了之后需要刷新界面
                    isDefectChanged = true;
                    initialData();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ScreenManager.getScreenManager().hasActivity(NariActivity.class)) {
            ScreenManager.getScreenManager().popAllActivityExceptOne(NariActivity.class);
        } else {
            ScreenManager.getScreenManager().popAllActivityExceptOne(TaskRemindActivity.class);
        }
        if (isDefectChanged) {
            setResult(RESULT_OK);
        } else if (!isFromTaskRemind) {
            isNeedUpdateTaskStatus = true;
        }
        this.finish();
    }

    @Override
    protected void onStop() {
        PlaySound.getIntance(mActivity).stop();
        mHandler.removeCallbacks(null);
        super.onStop();
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
