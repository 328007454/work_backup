package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.processor.CopyDataInterface;
import com.cnksi.sjjc.processor.ProcessorFactory;
import com.cnksi.sjjc.service.ReportService;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;

/**
 * 抄录报告
 *
 * @author Oliver
 */
public class CopyValueReportActivity extends BaseReportActivity {
	/**
	 * 巡检结果
	 */
	@ViewInject(R.id.tv_inspection_result)
	private TextView mTvInspectionResult;
	/**
	 * 检查结果
	 */
	@ViewInject(R.id.tv_check)
	private TextView tvCheck;
	/**
	 * 巡检开始时间
	 */
	@ViewInject(R.id.tv_inspection_start_time)
	private TextView mTvInspectionStartTime;

	/**
	 * 巡检结束时间
	 */
	@ViewInject(R.id.tv_inspection_end_time)
	private TextView mTvInspectionEndTime;
	/**
	 * 巡检人员
	 */
	@ViewInject(R.id.tv_inspection_person)
	private TextView mTvInspectionPerson;
    /**
 * 巡检类型布局
 */
	@ViewInject(R.id.ll_inspection_type)
	LinearLayout llInspectionType;
	/**
	 * 巡检类别
	 */
	@ViewInject(R.id.tv_inspection_type)
	TextView mTvInspectionTyoe;
    /**
     * 温度
     */
    @ViewInject(R.id.tv_inspection_temperature)
    private TextView mTvInspectionTemperature;
    /**
     * 湿度
     */
    @ViewInject(R.id.tv_inspection_humidity)
    private TextView mTvInspectionHumidity;
    /**
     * 天气
     */
    @ViewInject(R.id.tv_inspection_weather)
    private TextView mTvInspectionWeather;
	/**
	 * 天气布局
	 */
	@ViewInject(R.id.lltq)
	LinearLayout lltq;
	@ViewInject(R.id.tv_continue_inspection)
	private TextView continue_inspection;
	private Report report = null;
	/**
	 * 巡检完成状态
	 */
	private String status = "";
	private CopyDataInterface processor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        processor = ProcessorFactory.getProcessor(currentInspectionType, currentReportId);
		initData();
	}

	@Override
	public View setReportView() {

        return getLayoutInflater().inflate(R.layout.report_copyvalue, null);
	}


	private void initData() {

		mFixedThreadPoolExecutor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					// 查询状态
				//	mCurrentDepartment = DepartmentService.getInstance().findDepartmentById(currentDepartmentId);
					status = processor.getCopyResult(currentBdzId);
					report = ReportService.getInstance().findById(currentReportId);

				} catch (DbException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(LOAD_DATA);
			}
		});
	}

    @Event({R.id.tv_continue_inspection, R.id.ibtn_cancel, R.id.ibtn_exit})
	private void OnViewClick(View view) {
		switch (view.getId()) {
		case R.id.tv_continue_inspection: // 继续巡检
				processor.gotoInspcetion(_this);
                this.finish();
			break;
		}
	}

	@Override
	protected void onRefresh(Message msg) {
		super.onRefresh(msg);
		switch (msg.what) {
		case LOAD_DATA:
			String result = getString(R.string.work_status_format_str, status);
                mTvInspectionResult.setText(StringUtils.changePartTextColor(_this, result, R.color.red_color, result.length() - status.length(), result.length()));
                if (currentInspectionType.contains(InspectionType.SBJC_06.name())) {
				llInspectionType.setVisibility(View.VISIBLE);
				mTvInspectionTyoe.setText(currentInspectionName);
			}
			if (report != null) {
				mTvInspectionStartTime.setText(DateUtils.getFormatterTime(report.starttime, CoreConfig.dateFormat8));
                    mTvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime) ? DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) : DateUtils.getFormatterTime(report.endtime, CoreConfig.dateFormat8));
				mTvInspectionPerson.setText(report.persons);
				if (processor.isHasCheckResult()) {
					tvCheck.setVisibility(View.VISIBLE);
					tvCheck.append(report.jcqk);
				}
                    if (processor.isHasWeather()) {
					lltq.setVisibility(View.VISIBLE);
                	mTvInspectionTemperature.setText(report.temperature.contains(getString(R.string.temperature_unit_str)) ? report.temperature : report.temperature + getString(R.string.temperature_unit_str));
                	mTvInspectionHumidity.setText(report.humidity.contains(getString(R.string.humidity_unit_str)) ? report.humidity : report.humidity + getString(R.string.humidity_unit_str));
               	 	mTvInspectionWeather.setText(report.tq);
                    } else {
					lltq.setVisibility(View.GONE);
				}
			}
                String reg[] = status.split("/");
                if (reg.length >= 2 && reg[0].equals(reg[1])) {
				continue_inspection.setText(R.string.see_inspection_details_str);
			}
			break;
		default:
			break;
		}
	}


}
