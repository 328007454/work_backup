package com.cnksi.sjjc.activity.indoortempretureactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.StringUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.IndoorWeathearAdapter;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.databinding.IndoorBinding;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

import static com.cnksi.sjjc.R.id.add_indoor_weather;

/**
 * 修改后的室内温湿度界面
 */
public class NewIndoorHumitureRecordActivity extends BaseActivity implements ItemClickListener {

    //报告表
    private List<ReportSnwsd> mReportList;
    private IndoorBinding binding;
    private IndoorWeathearAdapter indoorWeatherAdapter;
    //报告表
    private Report mReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(_this), R.layout.activity_new_indoor_humiture, null, false);
        setChildView(binding.getRoot());
        getIntentValue();
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


    public void initView() {
        mTitleBinding.tvTitle.setText(R.string.indoor_tempreture_recoder);
        mTitleBinding.btnBack.setOnClickListener(v -> NewIndoorHumitureRecordActivity.this.finish());
    }

    public void loadData() {

        ExecutorManager.executeTaskSerially(() -> {
            try {
                mReport = ReportService.getInstance().findById(currentReportId);
                if (null != mReport) {
                    mReport.starttime = DateUtils.getCurrentLongTime();
                }
                mReportList = BaseService.getInstance(ReportSnwsd.class).selector().and(ReportSnwsd.REPORT_ID, "=", currentReportId).findAll();
                if (null == mReportList || mReportList.isEmpty()) {
                    mReportList.add(new ReportSnwsd(currentReportId, currentBdzId, currentBdzName));
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
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


    public void initOnclick() {
        binding.btnConfirmSave.setOnClickListener((v) -> {
            saveData();
        });
    }


    private void saveData() {
        if (TextUtils.isEmpty(binding.weatherView1.getSelectWeather())) {
            ToastUtils.showMessage("请选择天气");
            return;
        }
        for (ReportSnwsd reportSnwsd : mReportList) {
            if (TextUtils.isEmpty(reportSnwsd.location) || TextUtils.isEmpty(reportSnwsd.wd) || TextUtils.isEmpty(reportSnwsd.sd)) {
                ToastUtils.showMessage("请填写完相应的数据");
                return;
            } else if ((TextUtils.isEmpty(StringUtils.getTransformTep(reportSnwsd.wd))) || (-99.9f > Float.valueOf(reportSnwsd.wd) || Float.valueOf(reportSnwsd.wd) > 99.99)) {
                ToastUtils.showMessage("温度在-99.9℃到99.9℃");
                return;
            }
            if ((TextUtils.isEmpty(StringUtils.getTransformTep(reportSnwsd.sd))) || (0 > Float.valueOf(reportSnwsd.sd) || Float.valueOf(reportSnwsd.sd) > 100)) {
                ToastUtils.showMessage("湿度在0到100");
                return;
            }
            reportSnwsd.last_modify_time = DateUtils.getCurrentLongTime();

        }
        try {
            BaseService.getInstance(ReportSnwsd.class).saveOrUpdate(mReportList);
        } catch (DbException e) {
            Log.d("Tag", e.getMessage());
        }
        mReport.tq = binding.weatherView1.getSelectWeather().isEmpty() ? "" : binding.weatherView1.getSelectWeather();
        mReport.temperature = mReportList.get(0).wd;
        mReport.humidity = mReportList.get(0).sd;
        mReport.endtime = DateUtils.getCurrentLongTime();

        try {
            ReportService.getInstance().saveOrUpdate(mReport);
            TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
        } catch (DbException e) {
            Log.d("Tag", e.getMessage());
        }
        Intent intent = new Intent(_this, IndoorHumitureReportActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public void itemClick(View v, Object o, int position) {
        switch (v.getId()) {
            case add_indoor_weather:
                mReportList.add(new ReportSnwsd(currentReportId, currentBdzId, currentBdzName));
                break;
            case R.id.delete_indoor_weather:
                mReportList.remove(position);
                break;
            default:
                break;
        }
        indoorWeatherAdapter.setList(mReportList);

    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }
}
