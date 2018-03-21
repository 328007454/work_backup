package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.ActivityPreventSecondAnimalBinding;
import com.cnksi.sjjc.databinding.DialogTipsBinding;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.service.PreventionService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.DialogUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by ironGe on 2016/6/7.
 * <p/>
 * 防小动物措施检查界面
 */
public class PreventAnimalSecondActivity extends BaseActivity {


    private PreventionRecord preventionRecord;
    //
    private List<HoleRecord> mHoleList;

    private List<HoleRecord> clearHoleList;

    private Report report;


    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (radioGroup.getId()) {
                case R.id.radio_switch:
                    preventionRecord.switchStatus = (i == R.id.switch_yes) ? 0 : 1;
                    break;
                case R.id.radio_indoor:
                    preventionRecord.inroomStatus = (i == R.id.indoor_yes) ? 0 : 1;
                    break;
                case R.id.radio_outdoor:
                    preventionRecord.outroomStatus = (i == R.id.outdoor_yes) ? 0 : 1;
                    break;
                case R.id.radio_window:
                    preventionRecord.doorWindowStatus = (i == R.id.window_yes) ? 0 : 1;
                    break;
                case R.id.radio_ratsbane:
                    preventionRecord.ratsbaneStatus = (i == R.id.ratsbane_yes) ? 0 : 1;
                    break;
                case R.id.radio_mousetrap:
                    preventionRecord.mousetrapStatus = (i == R.id.mousetrap_yes) ? 0 : 1;
                    break;
            }
        }
    };

    ActivityPreventSecondAnimalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreventSecondAnimalBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        initOnClick();
    }


    @Override
    public void initUI() {
        mTitleBinding.tvTitle.setText("防小动物措施检查");
        binding.radioSwitch.setOnCheckedChangeListener(checkedChangeListener);
        binding.radioIndoor.setOnCheckedChangeListener(checkedChangeListener);
        binding.radioMousetrap.setOnCheckedChangeListener(checkedChangeListener);
        binding.radioOutdoor.setOnCheckedChangeListener(checkedChangeListener);
        binding.radioWindow.setOnCheckedChangeListener(checkedChangeListener);
        binding.radioRatsbane.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                preventionRecord = PreventionService.getInstance().findPreventionRecordByReoprtId(currentReportId);
                report = ReportService.getInstance().findById(currentReportId);
                mHoleList = HoleReportService.getInstance().getCurrentClearRecord(currentReportId, currentBdzId);
                if (null == preventionRecord) {
                    preventionRecord = new PreventionRecord(currentReportId, currentBdzId, currentBdzName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    private void initOnClick() {
        binding.btnNext.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.etMouseInfo.getText().toString())) {
                ToastUtils.showMessage("请输入鼠药及捕鼠器具放置情况");
                return;
            }
            showSureDialog();
        });

    }

    private Dialog mSureDialog;
    private DialogTipsBinding mTipsBinding;

    private void showSureDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 7 / 9;
        if (mSureDialog == null) {
            mTipsBinding = DialogTipsBinding.inflate(LayoutInflater.from(getApplicationContext()));
            mSureDialog = DialogUtils.creatDialog(mActivity, mTipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mTipsBinding.tvDialogTitle.setText("提示");
        mTipsBinding.tvDialogContent.setText("是否完成本次检查");
        mSureDialog.show();
        mTipsBinding.btnCancel.setOnClickListener(view -> mSureDialog.dismiss());

        mTipsBinding.btnSure.setOnClickListener(view -> {
            try {
                preventionRecord.mousetrapInfo = binding.etMouseInfo.getText().toString();
                preventionRecord.clearInfo = binding.tvClearPosition.getText().toString().trim();
                preventionRecord.last_modify_time = DateUtils.getCurrentLongTime();
                PreventionService.getInstance().saveOrUpdate(preventionRecord);
                report.endtime = DateUtils.getCurrentLongTime();
                ReportService.getInstance().saveOrUpdate(report);
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
            } catch (DbException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(_this, AnimalReportActivity.class);
            isNeedUpdateTaskState = true;
            startActivity(intent);
            _this.finish();
            mSureDialog.dismiss();
            ScreenManager.getScreenManager().popActivity(PreventAnimalActivity.class);

        });

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (preventionRecord != null) {
                    checkStatus();
                }
                CheckResult();
                break;
            default:
                break;
        }
    }

    private void checkStatus() {

        if (0 == preventionRecord.switchStatus) binding.radioSwitch.check(R.id.switch_yes);
        else binding.radioSwitch.check(R.id.switch_no);

        if (0 == preventionRecord.inroomStatus) binding.radioIndoor.check(R.id.indoor_yes);
        else binding.radioIndoor.check(R.id.indoor_no);

        if (0 == preventionRecord.outroomStatus) binding.radioOutdoor.check(R.id.outdoor_yes);
        else binding.radioOutdoor.check(R.id.outdoor_no);

        if (0 == preventionRecord.doorWindowStatus) binding.radioWindow.check(R.id.window_yes);
        else binding.radioWindow.check(R.id.window_no);

        if (0 == preventionRecord.ratsbaneStatus) binding.radioRatsbane.check(R.id.ratsbane_yes);
        else binding.radioRatsbane.check(R.id.ratsbane_no);

        if (0 == preventionRecord.mousetrapStatus) {
            binding.radioMousetrap.check(R.id.mousetrap_yes);
        } else {
            binding.radioMousetrap.check(R.id.mousetrap_no);
        }

    }

    private void CheckResult() {
        String problemPosition = "";
        String morePostion = "";
        if (mHoleList != null && mHoleList.size() > 0) {
            int i = 0;
            int countCurrentReportId = 0;
            for (HoleRecord record : mHoleList) {
                if (currentReportId.equals(record.reportId)) {
                    if (countCurrentReportId >= 1)
                        problemPosition = problemPosition + "\n";
                    problemPosition += ++countCurrentReportId + "、" + record.location + "_" + record.hole_detail + "_" + record.problem;
                }
                if (currentReportId.equals(record.clear_reportid) && "1".equals(record.status)) {
                    if (i >= 1)
                        morePostion = morePostion + "\n";
                    morePostion += ++i + "、" + record.location + "_" + record.hole_detail + "_" + record.problem + "_" + "已清除";
                }
                if (null != preventionRecord && !TextUtils.isEmpty(preventionRecord.clearInfo))
                    morePostion = preventionRecord.clearInfo;
            }
            if (problemPosition.endsWith(",")) {
                problemPosition = problemPosition.substring(0, problemPosition.length() - 1);
            }
            if (morePostion.endsWith(",")) {
                morePostion = morePostion.substring(0, morePostion.length() - 1);
            }
        }

        if (!TextUtils.isEmpty(problemPosition)) {
            binding.tvDiscoverPosition.setVisibility(View.VISIBLE);
            binding.tvDiscoverPosition.setText(problemPosition);
        }

        if (!TextUtils.isEmpty(morePostion)) {
            binding.tvClearPosition.setVisibility(View.VISIBLE);
            binding.tvClearPosition.setText(morePostion);
        }


    }
}
