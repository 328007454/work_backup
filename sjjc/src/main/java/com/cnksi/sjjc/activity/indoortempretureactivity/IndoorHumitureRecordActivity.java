package com.cnksi.sjjc.activity.indoortempretureactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseSjjcActivity;
import com.cnksi.sjjc.activity.JZLFenJieKaiGuanReportActivity;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.databinding.ActivityIndoorHumitureBinding;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

/**
 * 室内温湿度界面
 */
public class IndoorHumitureRecordActivity extends BaseSjjcActivity {

    //报告id
    private String reportId;
    //变电站Id
    private String bdzId;
    //变电站名字
    private String bdzName;
    //报告表
    private Report mReport;

    private ActivityIndoorHumitureBinding mHumitureBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHumitureBinding = DataBindingUtil.inflate(LayoutInflater.from(getApplicationContext()), R.layout.activity_indoor_humiture, null, false);
        AutoUtils.auto(mHumitureBinding.getRoot());
        setChildView(mHumitureBinding.getRoot());
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
    }


    public void loadData() {
        bdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        reportId = PreferencesUtils.get(Config.CURRENT_REPORT_ID, "");
        bdzName = PreferencesUtils.get(Config.CURRENT_BDZ_NAME, "");
        ExecutorManager.executeTaskSerially(() -> {
            try {
                mReport = ReportService.getInstance().findById(reportId);
                mReport.starttime = DateUtils.getCurrentLongTime();
            } catch (DbException e) {
                e.printStackTrace();
            }
        });

    }


    private void initOnclick() {
        mHumitureBinding.btnCompleteRecord.setOnClickListener(v -> saveData());
        mTitleBinding.btnBack.setOnClickListener(v -> finish());
    }

    private void saveData() {
        
        if (TextUtils.isEmpty(mHumitureBinding.etTestInstrument.getText().toString()) || TextUtils.isEmpty( mHumitureBinding.etCurrentHumidity.getText().toString())) {
            ToastUtils.showMessageLong("请输入完整信息");
            return;
        }
        ReportSnwsd snwsd = new ReportSnwsd();
        String weather = mHumitureBinding.weatherView1.getSelectWeather();
        snwsd.report_id = reportId;
        snwsd.bdz_id = bdzId;
        snwsd.bdz_name = bdzName;
        snwsd.wd = mHumitureBinding.etTestInstrument.getText().toString();
        snwsd.sd =  mHumitureBinding.etCurrentHumidity.getText().toString();
        mReport.tq = weather.isEmpty() ? "" : weather;
        mReport.temperature = snwsd.wd;
        mReport.humidity = snwsd.sd;
        mReport.endtime = DateUtils.getCurrentLongTime();
        try {
            BaseService.getInstance(ReportSnwsd.class).saveOrUpdate(snwsd);
            ReportService.getInstance().saveOrUpdate(mReport);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(snwsd.sd) && !TextUtils.isEmpty(snwsd.wd)) {
            try {
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(mActivity, JZLFenJieKaiGuanReportActivity.class);
        startActivity(intent);
        setResult(RESULT_OK);
        this.finish();
    }
}
