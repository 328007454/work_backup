package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.Report;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.indoortempretureactivity.IndoorHumitureRecordActivity;
import com.cnksi.sjjc.adapter.JZLFenJieKaiGuanContentAdapter;
import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.databinding.JzlfenjieLayoutBinding;
import com.cnksi.sjjc.service.ReportCdbhclService;
import com.cnksi.sjjc.service.ReportJzlbyqfjkgService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 交直流分接开关、差动保护、室内温湿度报告
 */
public class JZLFenJieKaiGuanReportActivity extends BaseReportActivity {




    /**
     * 当前报告
     */
    private Report report;
    /**
     * 差动保护差流数集
     */
    private List<ReportCdbhcl> exitCdbhclList;

    /**
     * 差动保护设备数集
     */
    private List<DbModel> listDevice;
    private List<DbModel> listDbModel;
    private List<DbModel> copyTotalDbmodel;
    private JZLFenJieKaiGuanContentAdapter fenJieKaiGuanContentAdapter;
    private int countCopyCdbhcl;
    private List<CdbhclValue> cdbhclValueList = new ArrayList<>();

    private JzlfenjieLayoutBinding mJzlfenjieLayoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
        initOnclick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    @Override
    public View setReportView() {

        mJzlfenjieLayoutBinding = JzlfenjieLayoutBinding.inflate(LayoutInflater.from(getApplicationContext()));

        return mJzlfenjieLayoutBinding.getRoot();
    }

    public void initView() {

        if (currentInspectionType.equals(InspectionType.SBJC_03.name())) {
            mTvTitle.setText(currentBdzName + "室内温湿度记录报告");
            mJzlfenjieLayoutBinding.llJianceResult.setVisibility(View.GONE);
            mJzlfenjieLayoutBinding.reViewpagerContainer.setVisibility(View.GONE);
            mJzlfenjieLayoutBinding.lineAboveResult.setVisibility(View.GONE);
        } else if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            mTvTitle.setText(currentBdzName + "分接开关记录报告");
            setViewGone();
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
            mTvTitle.setText(currentBdzName + "差动保护记录报告");
            mJzlfenjieLayoutBinding.tvResult.setText("记录结果");
            setViewGone();
        }
    }

    /**
     * 控制差动差流与分接开关 温度、湿度、天气控件报告中不显示
     */

    private void setViewGone() {

        mJzlfenjieLayoutBinding.tvResult.setText("记录结果");
        mJzlfenjieLayoutBinding.tvResult.setVisibility(View.VISIBLE);
        mJzlfenjieLayoutBinding.lvReportContent.setVisibility(View.VISIBLE);
        mJzlfenjieLayoutBinding.tvInspectionTemperature.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.tvInspectionHumidity.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.tvInspectionWeather.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.tvInspectionTemperatureTitle.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.tvInspectionTemperatureTitle.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.tvInspectionWeatherTitle.setVisibility(View.GONE);
        mJzlfenjieLayoutBinding.reViewpagerContainer.setVisibility(View.GONE);

    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
                    try {
                        report = ReportService.getInstance().findById(currentReportId);
                    } catch (DbException e) {
                        e.printStackTrace(System.out);
                    }
                    if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
                        listDbModel = ReportJzlbyqfjkgService.getInstance().getJzlfjkgCopyRecord(currentBdzId, currentReportId);
                        copyTotalDbmodel = DeviceService.getInstance().getDevicesByNameWays(currentBdzId, Config.TANSFORMADJUSTMENT_KAIGUAN, Config.TANSFORMADJUSTMENT_DANGWEI);
                    }

                    if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                        try {
                            listDevice = DeviceService.getInstance().getDevicesByNameWays(currentBdzId, Config.DIFFERENTIAL_RECORD_KEY);
                            exitCdbhclList = ReportCdbhclService.getInstance().getReportCdbhclList(currentBdzId, currentReportId);
                            for (ReportCdbhcl reportCdbhcl : exitCdbhclList) {
                                CdbhclValue.reportChangeValue(reportCdbhcl, cdbhclValueList);
                            }
                            for (DbModel modle : listDevice) {
                                if (modle.getString(CopyItem.VAL).equalsIgnoreCase("Y")) {
                                    countCopyCdbhcl += 1;
                                }
                                if (modle.getString(CopyItem.VAL_A).equalsIgnoreCase("Y")) {
                                    countCopyCdbhcl += 1;
                                }
                                if (modle.getString(CopyItem.VAL_B).equalsIgnoreCase("Y")) {
                                    countCopyCdbhcl += 1;
                                }
                                if (modle.getString(CopyItem.VAL_C).equalsIgnoreCase("Y")) {
                                    countCopyCdbhcl += 1;
                                }
                                if (modle.getString(CopyItem.VAL_O).equalsIgnoreCase("Y")) {
                                    countCopyCdbhcl += 1;
                                }
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
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
                mJzlfenjieLayoutBinding.tvInspectionStartTime.setText(report.starttime);

                mJzlfenjieLayoutBinding.tvInspectionEndTime.setText(report.endtime);

                mJzlfenjieLayoutBinding.tvInspectionPerson.setText(report.persons);
                mJzlfenjieLayoutBinding.tvInspectionTemperature.setText(report.temperature.contains(getString(R.string.temperature_unit_str)) ? report.temperature : report.temperature + getString(R.string.temperature_unit_str));
                mJzlfenjieLayoutBinding.tvInspectionHumidity.setText(report.humidity.contains(getString(R.string.humidity_unit_str)) ? report.humidity : report.humidity + getString(R.string.humidity_unit_str));
                mJzlfenjieLayoutBinding.tvInspectionWeather.setText(report.tq);
                if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
                    int i = 0;
                    for (DbModel model : listDbModel) {
                        if (!TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.BCDS))) {
                            i += 1;
                        }
                        if (!TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.DZCS))) {
                            i += 1;
                        }
                    }
                    mJzlfenjieLayoutBinding.tvInspectionResult.setText("记录完成情况:\t\t" + i + "/" + copyTotalDbmodel.size());
                    if (i == copyTotalDbmodel.size()) {
                        mJzlfenjieLayoutBinding.tvContinueInspection.setText("查看详情");
                    } else {
                        mJzlfenjieLayoutBinding.tvContinueInspection.setText("继续记录");
                    }
                    if (fenJieKaiGuanContentAdapter == null) {
                        fenJieKaiGuanContentAdapter = new JZLFenJieKaiGuanContentAdapter(_this, listDbModel, currentInspectionType, R.layout.jzlfenjiekaiguan_adapter);
                        mJzlfenjieLayoutBinding.lvReportContent.setAdapter(fenJieKaiGuanContentAdapter);
                    } else {
                        fenJieKaiGuanContentAdapter.setListModel(listDbModel);
                    }
                }
                if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                    int i = 0;
                    for (ReportCdbhcl cdbhcl : exitCdbhclList) {
                        if (!TextUtils.isEmpty(cdbhcl.dclz)) {
                            i += 1;
                        }
                        if (!TextUtils.isEmpty(cdbhcl.dclzA)) {
                            i += 1;
                        }
                        if (!TextUtils.isEmpty(cdbhcl.dclzB)) {
                            i += 1;
                        }
                        if (!TextUtils.isEmpty(cdbhcl.dclzC)) {
                            i += 1;
                        }
                        if (!TextUtils.isEmpty(cdbhcl.dclzO)) {
                            i += 1;
                        }

                    }
                    mJzlfenjieLayoutBinding.tvInspectionResult.setText("记录完成情况:\t\t" + i + "/" + countCopyCdbhcl);
                    if (i == listDevice.size()) {
                        mJzlfenjieLayoutBinding.tvContinueInspection.setText("查看详情");
                    } else {
                        mJzlfenjieLayoutBinding.tvContinueInspection.setText("继续记录");
                    }
                    if (fenJieKaiGuanContentAdapter == null) {
                        fenJieKaiGuanContentAdapter = new JZLFenJieKaiGuanContentAdapter(_this, cdbhclValueList, currentInspectionType, R.layout.jzlfenjiekaiguan_adapter);
                        mJzlfenjieLayoutBinding.lvReportContent.setAdapter(fenJieKaiGuanContentAdapter);
                    } else {
                        fenJieKaiGuanContentAdapter.setListBean(cdbhclValueList);
                    }
                }
                break;
            default:
                break;
        }
    }

    private Intent intentDiff;


    private void initOnclick() {
        mJzlfenjieLayoutBinding.tvContinueInspection.setOnClickListener((v) -> {
            if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                intentDiff = new Intent(_this, DifferentialMotionRecordActivity2.class);
            } else if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
                intentDiff = new Intent(_this, NewTransformRecordActivity.class);
            } else {
                intentDiff = new Intent(_this, IndoorHumitureRecordActivity.class);
            }
            startActivity(intentDiff);
            this.finish();
        });

    }
}
