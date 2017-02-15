package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.IndoorWeathearAdapter;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.IndoorBinding;
import com.cnksi.sjjc.inter.ItemClickListener;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;

import java.util.ArrayList;

/**
 * 修改后的室内温湿度界面
 */
public class NewIndoorHumitureRecordActivity extends BaseActivity implements ItemClickListener {

    //报告表
    private ArrayList<ReportSnwsd> mReportList;
    private IndoorBinding binding;
    private IndoorWeathearAdapter indoorWeatherAdapter;
    //报告表
    private Report mReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(_this), R.layout.activity_new_indoor_humiture, null, false);
        RelayoutUtil.reLayoutViewHierarchy(binding.getRoot());
        setChildView(binding.getRoot());
        initUI();
        initData();
        getIntentValue();
    }

    private void initUI() {
        tvTitle.setText(R.string.indoor_tempreture_recoder);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewIndoorHumitureRecordActivity.this.finish();
            }
        });
    }

    private void initData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = db.selector(Report.class).where(Report.REPORTID, "=", currentReportId).findFirst();
                    if (null != mReport)
                        mReport.starttime = DateUtils.getCurrentLongTime();
                    mReportList = (ArrayList<ReportSnwsd>) db.selector(ReportSnwsd.class).where(ReportSnwsd.REPORT_ID, "=", currentReportId).findAll();
                    if (null == mReportList || mReportList.isEmpty())
                        mReportList.add(new ReportSnwsd(currentReportId, currentBdzId, currentBdzName));
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                indoorWeatherAdapter = new IndoorWeathearAdapter(_this, mReportList, binding.llContainer, R.layout.adapter_indoor_item);
                indoorWeatherAdapter.setItemClickListener(this);
                indoorWeatherAdapter.setLocation("全站");
                break;
            default:
                break;
        }
    }

    @Event({R.id.btn_confirm_save, R.id.btn_back})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm_save:
                saveData();
                break;
            default:
                break;
        }

    }

    private void saveData() {
        for (ReportSnwsd reportSnwsd : mReportList) {
            if (TextUtils.isEmpty(reportSnwsd.location) || TextUtils.isEmpty(reportSnwsd.wd) || TextUtils.isEmpty(reportSnwsd.sd)) {
                CToast.showLong(_this, "请填写完相应的数据");
                return;
            }
            reportSnwsd.last_modify_time = DateUtils.getCurrentLongTime();
            try {
                db.saveOrUpdate(reportSnwsd);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        mReport.tq = binding.weatherView1.getSelectWeather().isEmpty() ? "" : binding.weatherView1.getSelectWeather();
        mReport.temperature = mReportList.get(0).wd;
        mReport.humidity = mReportList.get(0).sd;
        mReport.endtime = DateUtils.getCurrentLongTime();

        try {
            db.saveOrUpdate(mReport);
            db.update(Task.class, WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(_this, IndoorHumitureReportActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void itemClick(View v, Object o, int position) {
        mReportList.add(new ReportSnwsd(currentReportId, currentBdzId, currentBdzName));
        indoorWeatherAdapter.notifyDataSetChanged();
    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }
}
