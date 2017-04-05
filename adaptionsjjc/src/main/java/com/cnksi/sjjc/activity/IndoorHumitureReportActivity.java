package com.cnksi.sjjc.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.databinding.ReportIndoorBinding;
import com.cnksi.sjjc.databinding.ReportIndoorIncludeBinding;
import com.cnksi.sjjc.service.BaseService;
import com.cnksi.sjjc.service.ReportService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 室内温湿度报告界面
 */
public class IndoorHumitureReportActivity extends BaseReportActivity {

    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = ANIMATION + 1;
    private ReportIndoorBinding reportBinding;
    private List<ReportSnwsd> mReportList;
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    @Override
    public View setReportView() {
        reportBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_indoor_report, null, false);
        return reportBinding.getRoot();
    }

    private void initUI() {
        tvTitle.setText(currentBdzName + "室内温湿度记录报告");

    }

    private void initData() {
        mExcutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            report = ReportService.getInstance().findById(currentReportId);
                                            mReportList = BaseService.getInstance(ReportSnwsd.class).selector().and(ReportSnwsd.REPORT_ID, "=", currentReportId).findAll();
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                        mHandler.sendEmptyMessage(LOAD_DATA);
                                    }
                                }

        );
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                reportBinding.tvInspectionStartTime.setText(report.starttime);
                reportBinding.tvInspectionEndTime.setText(report.endtime);
                reportBinding.tvInspectionPerson.setText(report.persons);
                reportBinding.tvInspectionWeather.setText(report.tq);
                if (null != mReportList && !mReportList.isEmpty()) {
                    for (ReportSnwsd indoorReport : mReportList) {
                        ReportIndoorIncludeBinding includeBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.include_indoor_report_layout, null, false);
                        reportBinding.addLocationLayout.addView(includeBinding.getRoot());
                        includeBinding.tvLocation.setText(indoorReport.location);
                        String wd = indoorReport.wd.contains(getString(R.string.temperature_unit_str)) ? indoorReport.wd : indoorReport.wd + getString(R.string.temperature_unit_str);
                        includeBinding.tvInspectionTemperature.setText(wd);
                        String sd = indoorReport.sd.contains(getString(R.string.humidity_unit_str)) ? indoorReport.sd : indoorReport.sd + getString(R.string.humidity_unit_str);
                        includeBinding.tvInspectionHumidity.setText(sd);
                    }
                }
                break;
            default:
                break;
        }
    }
}
