package com.cnksi.bdzinspection.fragment.inspectionready;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.databinding.XsFragmentCopyTemperature2Binding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.utils.CommonUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhy.core.utils.AutoUtils;

/**
 * 抄录温湿度Fragment
 *
 * @author Joe
 */
public class CopyTemperatureFragment extends BaseFragment {
    public static final String TAG = "CopyTemperatureFragment";
    /**
     * 当前温湿度
     */
    private String currentTempreature = "";
    private String currentHumidity = "";
    private Report mReport = null;
    private XsFragmentCopyTemperature2Binding binding;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = XsFragmentCopyTemperature2Binding .inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initUI();
        lazyLoad();
        return binding.getRoot();
    }

    private void initUI() {
        binding.etTemperature.addTextChangedListener(new TextWatcherListener());
        getBundleValue();
        isPrepared = true;
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            //Load时加载处理NFC 读取温度湿度
            mFixedThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mReport = CustomApplication.getDbUtils()
                                .findFirst(Selector.from(Report.class).where(Report.TASK_ID, "=", currentTaskId));
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                }
            });
            isFirstLoad = false;
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                if (mReport != null) {
                    binding.etTemperature.setText(currentTempreature = mReport.temperature);
                    binding.weatherView1.setCurrentWeather(mReport.tq);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //设置键盘弹出的类型为数字键盘
        binding.etTemperature.setRawInputType(InputType.TYPE_CLASS_PHONE);
        if (!TextUtils.isEmpty(currentTempreature)) {
            binding.etTemperature.setText(currentTempreature);
        }

    }

    /**
     * 得到当前的温度
     */
    public String getCurrentTemperature() {
        try {
            String temp = binding.etTemperature.getText().toString();
            return temp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "15";
        }
    }

    public void setCurrentTempert(String temperature) {
        this.currentTempreature = temperature;
    }

    public String getCurrentWeater() {
        return binding.weatherView1.getSelectWeather();
    }

    /**
     * 得到当前的湿度
     */
    public String getCurrentHumidity() {
        return this.currentHumidity;
    }

    private String previousInput = "";
    private boolean isRight;

    public class TextWatcherListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousInput = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equalsIgnoreCase("")) {
                if ((s.toString().length() == 1) && (s.toString().startsWith("+") || s.toString().startsWith("-") || s.toString().startsWith("."))) {
                    previousInput = s.toString();
                    isRight = true;
                } else
                    isRight = CommonUtils.judgeTemperature(s.toString());
            } else {
                isRight = true;
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isRight)
                binding.etTemperature.setText(previousInput);
        }
    }
}