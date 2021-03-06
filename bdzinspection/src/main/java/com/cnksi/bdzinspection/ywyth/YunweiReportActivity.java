package com.cnksi.bdzinspection.ywyth;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.bdzinspection.databinding.XsActivityYunweiReportBinding;
import com.cnksi.common.utils.AnimationUtils;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DepartmentService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.model.Department;
import com.cnksi.common.model.Report;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;

import org.xutils.ex.DbException;

import java.util.Date;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 运维一体化报告界面
 *
 * @author Oliver
 *
 */
public class YunweiReportActivity extends BaseActivity {
	public static final int ANIMATION = 0X100;

	public static final int VIBRATOR = 0x101;
	private Report report = null;
	/**
	 * 当前部门
	 */
	private Department mCurrentDepartment;
	/**
	 * 巡检完成状态
	 */
	private String status = "";

	private String currentDepartmentId = "";
	
	private XsActivityYunweiReportBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(mActivity,R.layout.xs_activity_yunwei_report);
		initialUI();
		initialData();
		initOnClick();

	}


	private void initialUI() {

		currentDepartmentId = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, "");
		getIntentValue();

		binding.includeTitle.tvTitle.setText(getString(R.string.xs_report_title_format_str, currentBdzName + currentInspectionTypeName));
		binding.tvDashLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		PlaySound.getIntance(mActivity).play(R.raw.printing);
		mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
		mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);

//		binding.includeTitle.ibtnExit.setImageResource(R.drawable.return_main_menu_selector);
		binding.includeTitle.ibtnExit.setVisibility(View.GONE);
	}

	private void initialData() {

		ExecutorManager.executeTask(() -> {
            try {
                // 查询状态
                mCurrentDepartment = DepartmentService.getInstance().findDepartmentById(currentDepartmentId);
                status = TaskService.getInstance().getTaskStatus(currentTaskId);
                report= ReportService.getInstance().findById(currentReportId);

            } catch (DbException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
	}
	private void initOnClick() {
		
		binding.tvContinueInspection.setOnClickListener(view -> YunweiReportActivity.this.startActivity(new Intent(mActivity, YWWorkflowActivity.class)));
		
		binding.includeTitle.ibtnCancel.setOnClickListener(view -> YunweiReportActivity.this.ExitThisAndGoLauncher());
		binding.includeTitle.ibtnExit.setOnClickListener(view -> YunweiReportActivity.this.ExitThisAndGoLauncher());
	}


	@Override
	protected void onRefresh(Message msg) {
		switch (msg.what) {
		case ANIMATION:

			PlaySound.getIntance(mActivity).play(R.raw.print_out);
			AnimationUtils.translateAnimRun(binding.llReportContentContainer, 0.0f,
					binding.llReportContentContainer.getHeight() * 92 / 100);

			break;
		case VIBRATOR:
			mVibrator.vibrate(735);
			break;
		case LOAD_DATA:

			String result = getString(R.string.xs_inspection_status_format_str, status);
			binding.tvInspectionResult.setText(StringUtils.changePartTextColor(mActivity, result, R.color.xs_red_color,
					result.length() - status.length(), result.length()));

			if (report != null) {
				binding.tvInspectionStartTime.setText(DateUtils.getFormatterTime(report.starttime, DateUtils.yyyy_MM_dd_HH_mm));
				binding.tvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime)
						? DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm)
						: DateUtils.getFormatterTime(report.endtime, DateUtils.yyyy_MM_dd_HH_mm));
				binding.tvInspectionPerson.setText(report.persons);
				binding.tvRemainProblems.setText(TextUtils.isEmpty(report.remain_problems) ? "无" : report.remain_problems);
			}
			if (mCurrentDepartment != null) {
				binding.tvWorkGroup.setText(TextUtils.isEmpty(mCurrentDepartment.name) ? "运维一班" : mCurrentDepartment.name);
			} else {
				binding.tvWorkGroup.setText("运维一班");
			}
			if ("已完成".equals(status)) {
				binding.tvContinueInspection.setText(R.string.xs_see_inspection_details_str);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		PlaySound.getIntance(mActivity).stop();
		super.onDestroy();
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
