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
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.View.WeatherView;
import com.cnksi.sjjc.bean.Report;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 抄录温湿度界面
 *
 * */
public class CopyBaseDataActivity extends BaseActivity {
    @ViewInject(R.id.btn_complete_record)
    private Button btDone;
    //标题
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.weatherView1)
    private WeatherView weatherView;

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
        View containerView=getLayoutInflater().inflate(R.layout.activity_copybasedata,null,false);
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
                    mReport = db.selector(Report.class).where(Report.REPORTID, "=", currentReportId).findFirst();
                    if (mReport!=null) {
                        mReport.starttime =TextUtils.isEmpty(mReport.starttime)? DateUtils.getCurrentLongTime():mReport.starttime;
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
        switch (msg.what)
        {
            case LOAD_DATA:
                weatherView.setCurrentWeather(mReport.tq);
                etCurInstrument.setText(mReport.temperature);
                etCurHumidiyt.setText(mReport.humidity);
                break;
        }
    }

    @Event({R.id.btn_complete_record, R.id.btn_back})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_complete_record:
                String weather = weatherView.getSelectWeather();
                if(TextUtils.isEmpty(etCurInstrument.getText().toString())||TextUtils.isEmpty(etCurHumidiyt.getText().toString())||weather.isEmpty()){
                    CToast.showLong(_this,"请输入完整信息");
                    return;
                }


                mReport.tq =weather;
                mReport.temperature =etCurInstrument.getText().toString();
                mReport.humidity =etCurHumidiyt.getText().toString();
                mReport.endtime = DateUtils.getCurrentLongTime();
                try {
                    db.saveOrUpdate(mReport);
                    } catch (DbException e) {
                    e.printStackTrace();
                }
                if(!Config.NEW_COPY) {
                    startActivity(new Intent(_this, CopyAllValueActivity.class));
                }else{
                    startActivity(new Intent(_this, CopyAllValueActivity3.class));
                }
                break;
            case R.id.btn_back:
                this.finish();
            default:
                break;
        }
    }
}
