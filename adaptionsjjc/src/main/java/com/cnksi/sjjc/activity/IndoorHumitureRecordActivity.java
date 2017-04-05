package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.View.WeatherView1;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.service.BaseService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 室内温湿度界面
 */
public class IndoorHumitureRecordActivity extends BaseActivity {
    //    @ViewInject(R.id.btn_complete_record)
//    private Button btDone;
    //标题
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.weatherView1)
    private WeatherView1 weatherView;

    //当前温度
    @ViewInject(R.id.et_current_humidity)
    private EditText etCurHumidiyt;
    //当前湿度
    @ViewInject(R.id.et_test_instrument)
    private EditText etCurInstrument;
    //报告id
    private String reportId;
    //变电站Id
    private String bdzId;
    //变电站名字
    private String bdzName;
    //报告表
    private Report mReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View containerView = getLayoutInflater().inflate(R.layout.activity_indoor_humiture, null, false);
        RelayoutUtil.reLayoutViewHierarchy(containerView);
        setChildView(containerView);
        initUI();
        initData();
        getIntentValue();
    }

    private void initUI() {
        tvTitle.setText(R.string.indoor_tempreture_recoder);
    }

    private void initData() {
        bdzId = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_ID, "");
        reportId = PreferencesUtils.getString(_this, Config.CURRENT_REPORT_ID, "");
        bdzName = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_NAME, "");
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = ReportService.getInstance().findById(reportId);
                    mReport.starttime = DateUtils.getCurrentLongTime();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Event({R.id.btn_complete_record, R.id.btn_back})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete_record:
                if (TextUtils.isEmpty(etCurInstrument.getText().toString()) || TextUtils.isEmpty(etCurHumidiyt.getText().toString())) {
                    CToast.showLong(_this, "请输入完整信息");
                    return;
                }
                ReportSnwsd snwsd = new ReportSnwsd();
                String weather = weatherView.getSelectWeather();
                snwsd.report_id = reportId;
                snwsd.bdz_id = bdzId;
                snwsd.bdz_name = bdzName;
                snwsd.wd = etCurInstrument.getText().toString();
                snwsd.sd = etCurHumidiyt.getText().toString();
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
                        TaskService.getInstance().update( WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(_this, JZLFenJieKaiGuanReportActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                this.finish();
                break;
            case R.id.btn_back:
                this.finish();
            default:
                break;
        }
    }
}
