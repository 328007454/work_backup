package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.view.InnerListView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.JZLFenJieKaiGuanContentAdapter;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.ReportCdbhclService;
import com.cnksi.sjjc.service.ReportJzlbyqfjkgService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 交直流分接开关、差动保护、室内温湿度报告
 */
public class JZLFenJieKaiGuanReportActivity extends BaseReportActivity {
    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = ANIMATION + 1;
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
     * 温度
     */
    @ViewInject(R.id.tv_inspection_temperature)
    private TextView mTvInspectionTemperature;
    /**
     * 温度标题
     */
    @ViewInject(R.id.tv_inspection_temperature_title)
    private TextView mTvInspectionTemperatureTitle;
    /**
     * 湿度
     */
    @ViewInject(R.id.tv_inspection_humidity)
    private TextView mTvInspectionHumidity;
    /**
     * 湿度标题
     */
    @ViewInject(R.id.tv_inspection_humidity_title)
    private TextView mTvInspectionHumidityTitle;
    /**
     * 天气
     */
    @ViewInject(R.id.tv_inspection_weather)
    private TextView mTvInspectionWeather;
    /**
     * 天气标题
     */
    @ViewInject(R.id.tv_inspection_weather_title)
    private TextView mTvInspectionWeatherTitle;

    /**
     * 巡检结果
     */
    @ViewInject(R.id.tv_inspection_result)
    private TextView mTvInspectionResult;

    /**
     * 继续巡检
     */
    @ViewInject(R.id.tv_continue_inspection)
    private TextView mTvInspectionContinue;
    @ViewInject(R.id.lv_container)
    private InnerListView lvContainer;
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;
    @ViewInject(R.id.ll_report_content_container)
    private LinearLayout mLLReportContentContainer;
    @ViewInject(R.id.ll_jiance_result)
    private LinearLayout mLLReportResultContainer;
    @ViewInject(R.id.re_viewpager_container)
    private RelativeLayout mLLReportViewPagerContainer;
    @ViewInject(R.id.line_above_result)
    private View viewLine;
    //记录结果
    @ViewInject(R.id.tv_result)
    private TextView tvResult;
    //记录结果
    @ViewInject(R.id.lv_report_content)
    private ListView lvResultContent;
    /**
     * 当前报告
     */
    private Report report;
    /**
     * 差动保护差流数集
     */
    private List<ReportCdbhcl> exitCdbhclList;
    /**
     * 以保存的差留值被清空的集合
     */
    private List<ReportCdbhcl> changedCdbhcList = new ArrayList<ReportCdbhcl>();
    /**
     * 差动保护设备数集
     */
    private List<DbModel> listDevice;
    private List<DbModel> listDbModel;
    private List<DbModel> copyTotalDbmodel;
    private JZLFenJieKaiGuanContentAdapter fenJieKaiGuanContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        initData();
    }

    @Override
    public View setReportView() {
        return getLayoutInflater().inflate(R.layout.jzlfenjie_layout, null);
    }

    private void initUI() {

        if (currentInspectionType.equals(InspectionType.SBJC_03.name())) {
            tvTitle.setText(currentBdzName + "室内温湿度记录报告");
            mLLReportResultContainer.setVisibility(View.GONE);
            mLLReportViewPagerContainer.setVisibility(View.GONE);
            viewLine.setVisibility(View.GONE);
        } else if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            tvTitle.setText(currentBdzName + "分接开关记录报告");
            setViewGone();
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
            tvTitle.setText(currentBdzName + "差动保护记录报告");
            tvResult.setText("记录结果");
            setViewGone();
        }
    }

    /**
     * 控制差动差流与分接开关 温度、湿度、天气控件报告中不显示
     */

    private void setViewGone() {
        tvResult.setText("记录结果");
        tvResult.setVisibility(View.VISIBLE);
        lvResultContent.setVisibility(View.VISIBLE);
        mTvInspectionTemperature.setVisibility(View.GONE);
        mTvInspectionHumidity.setVisibility(View.GONE);
        mTvInspectionWeather.setVisibility(View.GONE);
        mTvInspectionTemperatureTitle.setVisibility(View.GONE);
        mTvInspectionHumidityTitle.setVisibility(View.GONE);
        mTvInspectionWeatherTitle.setVisibility(View.GONE);
        mLLReportViewPagerContainer.setVisibility(View.GONE);

    }

    private void initData() {
        mExcutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            report = db.findById(Report.class, currentReportId);
                                        } catch (DbException e) {
                                            e.printStackTrace(System.out);
                                        }
                                        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
                                            if (Config.NEW_COPY) {
                                                listDbModel = ReportJzlbyqfjkgService.getIntance().getJzlfjkgCopyRecord(currentBdzId, currentReportId);
                                                copyTotalDbmodel = DeviceService.getInstance().getDevicesByNameWays(currentBdzId, Config.TANSFORMADJUSTMENT_KAIGUAN, Config.TANSFORMADJUSTMENT_DANGWEI);
                                            } else {
                                                listDbModel = ReportJzlbyqfjkgService.getIntance().getPartDeviceAndReport(currentBdzId, "分接开关动作次数", currentReportId);
                                            }
                                        }

                                        if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                                            try {
                                                if (Config.NEW_COPY)
                                                    listDevice = DeviceService.getInstance().getDevicesByNameWays(currentBdzId, Config.DIFFERENTIAL_RECORD_KEY);
                                                else
                                                    listDevice = DeviceService.getInstance().getDevicesByName(currentBdzId, "保护差流");
                                                exitCdbhclList = ReportCdbhclService.getIntance().getReportCdbhclList(currentBdzId, currentReportId);
                                                for (ReportCdbhcl reportCdbhcl : exitCdbhclList) {
                                                    if (TextUtils.isEmpty(reportCdbhcl.dclz)) {
                                                        changedCdbhcList.add(reportCdbhcl);
                                                    }
                                                }
                                                exitCdbhclList.removeAll(changedCdbhcList);
                                            } catch (DbException e) {
                                                e.printStackTrace();
                                            }

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
                mTvInspectionStartTime.setText(report.starttime);
                mTvInspectionEndTime.setText(report.endtime);
                mTvInspectionPerson.setText(report.persons);
                mTvInspectionTemperature.setText(report.temperature.contains(getString(R.string.temperature_unit_str)) ? report.temperature : report.temperature + getString(R.string.temperature_unit_str));
                mTvInspectionHumidity.setText(report.humidity.contains(getString(R.string.humidity_unit_str)) ? report.humidity : report.humidity + getString(R.string.humidity_unit_str));
                mTvInspectionWeather.setText(report.tq);
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
                    if (Config.NEW_COPY) {
                        mTvInspectionResult.setText("记录完成情况:\t\t" + i + "/" + copyTotalDbmodel.size());
                        if (i == copyTotalDbmodel.size()) {
                            mTvInspectionContinue.setText("查看详情");
                        } else {
                            mTvInspectionContinue.setText("继续记录");
                        }
                    } else {
                        mTvInspectionResult.setText("记录完成情况:\t\t" + i + "/" + listDbModel.size());
                        if (i == listDbModel.size()) {
                            mTvInspectionContinue.setText("查看详情");
                        } else {
                            mTvInspectionContinue.setText("继续记录");
                        }
                    }
                    if (fenJieKaiGuanContentAdapter == null) {
                        fenJieKaiGuanContentAdapter = new JZLFenJieKaiGuanContentAdapter(_this, listDbModel, currentInspectionType);
                        lvResultContent.setAdapter(fenJieKaiGuanContentAdapter);
                    } else {
                        fenJieKaiGuanContentAdapter.setList(listDbModel);
                    }
                }
                if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                    int i = 0;
                    for (ReportCdbhcl cdbhcl : exitCdbhclList) {
                        if (!TextUtils.isEmpty(cdbhcl.dclz)) {
                            i += 1;
                        }
                    }
                    mTvInspectionResult.setText("记录完成情况:\t\t" + i + "/" + listDevice.size());
                    if (i == listDevice.size()) {
                        mTvInspectionContinue.setText("查看详情");
                    } else {
                        mTvInspectionContinue.setText("继续记录");
                    }
                    if (fenJieKaiGuanContentAdapter == null) {
                        fenJieKaiGuanContentAdapter = new JZLFenJieKaiGuanContentAdapter(_this, exitCdbhclList, currentInspectionType);
                        lvResultContent.setAdapter(fenJieKaiGuanContentAdapter);
                    } else {
                        fenJieKaiGuanContentAdapter.setList(exitCdbhclList);
                    }
                }
                break;
            default:
                break;
        }
    }

    private Intent intentDiff;

    @Event({R.id.btn_back, R.id.tv_continue_inspection})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                Intent intent = new Intent(_this, TaskRemindActivity.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.tv_continue_inspection:
                if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
                    intentDiff = new Intent(_this, DifferentialMotionRecordActivity2.class);
                } else if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
                    if (Config.NEW_COPY)
                        intentDiff = new Intent(_this, NewTransformRecordActivity.class);
                    else
                        intentDiff = new Intent(_this, TransformAdjustmentRecordActivity.class);
                } else {
                    intentDiff = new Intent(_this, IndoorHumitureRecordActivity.class);
                }
                startActivity(intentDiff);
                this.finish();

                break;
            default:
                break;
        }
    }
}
