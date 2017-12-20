package com.cnksi.sjjc.activity.hwcw;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseReportActivity;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;
import com.cnksi.sjjc.bean.hwcw.HwcwHotPart;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;
import com.cnksi.sjjc.databinding.ActivityNewHwcwReportBinding;
import com.cnksi.sjjc.service.NewHwcwService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.util.GsonUtil;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/18.
 */

public class NewHwcwReportActivity extends BaseReportActivity {
    ActivityNewHwcwReportBinding mReportBinding;
    Report mReport;
    HwcwBaseInfo mBaseInfo;
    private List<HwcwLocation> hotLocations = new ArrayList<>();
    private List<String> devices = new ArrayList<>();
    private List<String> spaces = new ArrayList<>();
    private SpannableStringBuilder deviceBuilder = new SpannableStringBuilder();
    private SpannableStringBuilder spaceBuilder = new SpannableStringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    @Override
    public View setReportView() {
        mReportBinding = ActivityNewHwcwReportBinding.inflate(getLayoutInflater());
        return mReportBinding.getRoot();
    }

    private void initUI() {
        tvTitle.setText(currentBdzName + currentInspectionName + "报告");
        mBtnRight.setVisibility(View.VISIBLE);
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = ReportService.getInstance().selector().and(Report.REPORTID, "=", currentReportId).findFirst();
                    mBaseInfo = NewHwcwService.getInstance().getBaseInfo(currentReportId);
                    if (!TextUtils.isEmpty(mBaseInfo.id)) {
                        hotLocations = NewHwcwService.getInstance().getAllLocation(mBaseInfo.id);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshData();
                        }
                    });
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshData() {
        int loopTime = 0;
        mReportBinding.setReport(mReport);
        mReportBinding.setHwcwReport(mBaseInfo);
        StringBuilder stringBuilder = new StringBuilder();
        if (!hotLocations.isEmpty()) {
            for (HwcwLocation location : hotLocations) {
                String deviceName = location.deviceName;
                deviceBuilder.append(loopTime == hotLocations.size() ? deviceName : deviceName + "\n");
                spaceBuilder.append(loopTime == hotLocations.size() ? location.spacingName : location.spacingName + "\n");
                stringBuilder.append("发热设备:").append(deviceName).append("\n");
                HwcwHotPart hotParts = (HwcwHotPart) GsonUtil.resolveJson(location.hotPart);
                for (HwcwHotPart.Result result : hotParts.result) {
                    stringBuilder.append("发热部位名称：").append(result.bw_name).append("\n温度：").append(result.wd).append("\n");
                }
                if (++loopTime != hotLocations.size())
                    stringBuilder.append("\n");
            }
            mReportBinding.txtHotInfor.setText(stringBuilder.toString());
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("温度：").append(mBaseInfo.temp).append("℃");
        AbsoluteSizeSpan grayRelativeSizeSpan = new AbsoluteSizeSpan((int) DisplayUtil.getInstance().spToPx(getApplicationContext(), 14));
        AbsoluteSizeSpan greenRelativeSizeSpan = new AbsoluteSizeSpan((int) DisplayUtil.getInstance().spToPx(getApplicationContext(), 18));
        ForegroundColorSpan grayForegroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.global_gray_text_color));
        ForegroundColorSpan greenForegroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.green_color));
        spannableStringBuilder.setSpan(grayRelativeSizeSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(grayForegroundColorSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenRelativeSizeSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenForegroundColorSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mReportBinding.txtTemp.setText(spannableStringBuilder);
        spannableStringBuilder.clear();

        spannableStringBuilder.append("湿度：").append(mBaseInfo.shidu).append("%RH");
        spannableStringBuilder.setSpan(grayRelativeSizeSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(grayForegroundColorSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenRelativeSizeSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenForegroundColorSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mReportBinding.txtShidu.setText(spannableStringBuilder);
        spannableStringBuilder.clear();

        spannableStringBuilder.append("风速：").append(mBaseInfo.fengsu).append("m/s");
        spannableStringBuilder.setSpan(grayRelativeSizeSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(grayForegroundColorSpan, 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenRelativeSizeSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableStringBuilder.setSpan(greenForegroundColorSpan, 3, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        mReportBinding.txtFengsu.setText(spannableStringBuilder);

        mReportBinding.txtDeviceName.setText(deviceBuilder.toString());
        mReportBinding.txtSpaceName.setText(spaceBuilder.toString());
    }

}
