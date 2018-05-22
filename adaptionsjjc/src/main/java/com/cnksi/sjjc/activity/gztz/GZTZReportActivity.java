package com.cnksi.sjjc.activity.gztz;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Report;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.activity.BaseReportActivity;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.databinding.GztzReportBinding;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;

import org.xutils.ex.DbException;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/9 20:10
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZReportActivity extends BaseReportActivity {
    GztzReportBinding binding;
    Report report;
    SbjcGztzjl sbjcGztzjl;

    @Override
    public View setReportView() {
        binding = GztzReportBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    public void loadData() {
        try {
            report = ReportService.getInstance().findById(currentReportId);
            if (report == null) {
                ToastUtils.showMessage("数据异常，没有查询到报告！！！");
                return;
            }
            if (Cache.GZTZJL != null) {
                if (TextUtils.equals(report.reportid, Cache.GZTZJL.reportid)) {
                    sbjcGztzjl = Cache.GZTZJL;
                }
            }
            if (sbjcGztzjl == null) {
                sbjcGztzjl = GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        binding.tvInspectionStartTime.setText(report.starttime);
        binding.tvInspectionEndTime.setText(report.endtime);
        binding.tvInspectionPerson.setText(report.persons);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("保护动作情况");
        builder.setSpan(new RelativeSizeSpan(1.2f), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\n    ").append(sbjcGztzjl.bhdzqk).append("\n");
        SpannableStringBuilder temp = new SpannableStringBuilder("故障简题");
        temp.setSpan(new RelativeSizeSpan(1.2f), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("\n").append(temp);
        builder.append("\n    ").append(sbjcGztzjl.gzjt);
        binding.xsJg.setText(builder);
    }
}

