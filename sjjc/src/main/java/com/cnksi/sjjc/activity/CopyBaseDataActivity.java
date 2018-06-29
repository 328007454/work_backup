package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Report;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.databinding.ActivityCopybasedataBinding;

import org.xutils.ex.DbException;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 抄录温湿度界面
 */
public class CopyBaseDataActivity extends BaseSjjcActivity {
    //报告表
    private Report mReport;
    private ActivityCopybasedataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCopybasedataBinding.inflate(getLayoutInflater());
        getIntentValue();
        setChildView(binding.getRoot());
        initView();
        loadData();
        initOnClick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void initView() {
        mTitleBinding.tvTitle.setText(currentInspectionTypeName);
    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                mReport = ReportService.getInstance().findById(currentReportId);
                if (mReport != null) {
                    mReport.starttime = TextUtils.isEmpty(mReport.starttime) ? DateUtils.getCurrentLongTime() : mReport.starttime;
                    mHandler.sendEmptyMessage(LOAD_DATA);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                binding.weatherView1.setCurrentWeather(mReport.tq);
                binding.etTestInstrument.setText(mReport.temperature);
                binding.etCurrentHumidity.setText(mReport.humidity);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mReport) {
            binding.etCurrentHumidity.setText(mReport.humidity);
            binding.etTestInstrument.setText(mReport.temperature);
        }
    }

    private void initOnClick() {
        binding.btnCompleteRecord.setOnClickListener(view -> {
            String weather = binding.weatherView1.getSelectWeather();
            String tempreture = binding.etTestInstrument.getText().toString();
            String sd = binding.etCurrentHumidity.getText().toString();
            if (TextUtils.isEmpty(tempreture) || TextUtils.isEmpty(sd) || weather.isEmpty()) {
                ToastUtils.showMessageLong("请输入完整信息");
                return;
            } else if ((TextUtils.isEmpty(StringUtilsExt.getDecimalPoint(tempreture))) || (-99.9f > Float.valueOf(tempreture) || Float.valueOf(tempreture) > 99.99)) {
                ToastUtils.showMessage("温度在-99.9℃到99.9℃");
                return;
            }
            if ((TextUtils.isEmpty(StringUtilsExt.getDecimalPoint(sd))) || (0 > Float.valueOf(sd) || Float.valueOf(sd) > 100)) {
                ToastUtils.showMessage("湿度在0到100");
                return;
            }

            mReport.tq = weather;
            mReport.temperature = StringUtilsExt.getDecimalPoint(tempreture);
            mReport.humidity = StringUtilsExt.getDecimalPoint(sd);
            mReport.endtime = DateUtils.getCurrentLongTime();
            try {
                ReportService.getInstance().saveOrUpdate(mReport);
            } catch (DbException e) {
                Log.i("Tag", e.getMessage());
            }
            startActivity(new Intent(mActivity, CopyAllValueActivity3.class));
        });
        mTitleBinding.btnBack.setOnClickListener(view -> finish());
    }
}
