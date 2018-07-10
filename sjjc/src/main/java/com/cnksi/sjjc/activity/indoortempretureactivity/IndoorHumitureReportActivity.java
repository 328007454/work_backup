package com.cnksi.sjjc.activity.indoortempretureactivity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Report;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseReportActivity;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.databinding.ReportIndoorBinding;
import com.cnksi.sjjc.databinding.ReportIndoorIncludeBinding;

import org.xutils.ex.DbException;

import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 室内温湿度报告界面
 */
@Route(path = "/indoorHumitureReport/activity")
public class IndoorHumitureReportActivity extends BaseReportActivity {

    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = ANIMATION + 1;
    private ReportIndoorBinding reportBinding;
    private List<ReportSnwsd> mReportList;
    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    @Override
    public View setReportView() {
        reportBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_indoor_report, null, false);
        return reportBinding.getRoot();
    }


    public void initView() {
       mTvTitle.setText(currentBdzName + "室内温湿度记录报告");
    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                report = ReportService.getInstance().findById(currentReportId);
                mReportList = BaseService.getInstance(ReportSnwsd.class).selector().and(ReportSnwsd.REPORT_ID, "=", currentReportId).findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
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
