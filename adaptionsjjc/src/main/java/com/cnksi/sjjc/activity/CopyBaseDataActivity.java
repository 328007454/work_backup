package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.WeatherView1;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.service.ReportService;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 抄录温湿度界面
 */
public class CopyBaseDataActivity extends BaseActivity {
    @ViewInject(R.id.btn_complete_record)
    private Button btDone;
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
    //报告表
    private Report mReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View containerView = getLayoutInflater().inflate(R.layout.activity_copybasedata, null, false);
        RelayoutUtil.reLayoutViewHierarchy(containerView);
        getIntentValue();
        setChildView(containerView);
        initUI();
        initData();

    }

    private void initUI() {
        tvTitle.setText(currentInspectionName);
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = ReportService.getInstance().findById(currentReportId);
                    if (mReport != null) {
                        mReport.starttime = TextUtils.isEmpty(mReport.starttime) ? DateUtils.getCurrentLongTime() : mReport.starttime;
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                weatherView.setCurrentWeather(mReport.tq);
                etCurInstrument.setText(mReport.temperature);
                etCurHumidiyt.setText(mReport.humidity);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mReport) {
            etCurHumidiyt.setText(mReport.humidity);
            etCurInstrument.setText(mReport.temperature);
        }
    }

    @Event({R.id.btn_complete_record, R.id.btn_back})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete_record:
                String weather = weatherView.getSelectWeather();
                String tempreture = etCurInstrument.getText().toString();
                String sd = etCurHumidiyt.getText().toString();
                if (TextUtils.isEmpty(tempreture) || TextUtils.isEmpty(sd) || weather.isEmpty()) {
                    CToast.showLong(_this, "请输入完整信息");
                    return;
                } else if (TextUtils.isEmpty(StringUtils.getTransformTep(tempreture))) {
                    CToast.showShort(_this, "温度在-99.9℃到99.9℃");
                    return;
                } else if ((-99.9f > new Float(tempreture) || new Float(tempreture) > 99.99)) {
                    CToast.showShort(_this, "温度在-99.9℃到99.9℃");
                    return;
                }
                if (TextUtils.isEmpty(StringUtils.getTransformTep(sd))) {
                    CToast.showShort(_this, "湿度在0到100");
                    return;
                } else if (0 > new Float(sd) || new Float(sd) > 100) {
                    CToast.showShort(_this, "湿度在0到100");
                    return;
                }


                mReport.tq = weather;
                mReport.temperature = com.cnksi.sjjc.util.StringUtils.getTransformTep(tempreture);
                mReport.humidity = com.cnksi.sjjc.util.StringUtils.getTransformTep(sd);
                mReport.endtime = DateUtils.getCurrentLongTime();
                try {
                    ReportService.getInstance().saveOrUpdate(mReport);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(_this, CopyAllValueActivity3.class));
                break;
            case R.id.btn_back:
                this.finish();
            default:
                break;
        }
    }
}
