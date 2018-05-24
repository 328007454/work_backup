package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Report;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ReportCopyvalueBinding;
import com.cnksi.sjjc.processor.CopyDataInterface;
import com.cnksi.sjjc.processor.ProcessorFactory;
import com.cnksi.sjjc.util.CoreConfig;

import org.xutils.ex.DbException;

import java.util.Date;

/**
 * 抄录报告
 *
 * @author Oliver
 */
public class CopyValueReportActivity extends BaseReportActivity {
    private Report report = null;
    /**
     * 巡检完成状态
     */
    private String status = "";
    private CopyDataInterface processor;
    private ReportCopyvalueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processor = ProcessorFactory.getProcessor(currentInspectionType, currentReportId);
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

    }

    @Override
    public View setReportView() {
        binding = ReportCopyvalueBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                // 查询状态
                //	mCurrentDepartment = DepartmentService.getInstance().findDepartmentById(currentDepartmentId);
                status = processor.getCopyResult(currentBdzId);
                report = ReportService.getInstance().findById(currentReportId);

            } catch (DbException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    private void initOnClick() {
        binding.tvContinueInspection.setOnClickListener(view -> processor.gotoInspection(_this));
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        
        switch (msg.what) {
            case LOAD_DATA:
                
                String result = getString(R.string.work_status_format_str, status);
                binding.tvInspectionResult.setText(StringUtils.changePartTextColor(_this, result, R.color.red_color, result.length() - status.length(), result.length()));
                if (currentInspectionType.contains(InspectionType.SBJC_06.name())) {
                    binding.llInspectionType.setVisibility(View.VISIBLE);
                    binding.tvInspectionType.setText(currentInspectionName);
                }
                
                if (report != null) {
                    binding.tvInspectionStartTime.setText(DateUtils.getFormatterTime(report.starttime, CoreConfig.dateFormat8));
                    binding.tvInspectionEndTime.setText(TextUtils.isEmpty(report.endtime) ? DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) : DateUtils.getFormatterTime(report.endtime, CoreConfig.dateFormat8));
                    binding.tvInspectionPerson.setText(report.persons);
                    if (processor.isHasCheckResult()) {
                        binding.tvCheck.setVisibility(View.VISIBLE);
                        binding.tvCheck.append(report.jcqk);
                    }
                    
                    if (processor.isHasWeather()) {
                        binding.lltq.setVisibility(View.VISIBLE);
                        binding.tvInspectionTemperature.setText(report.temperature.contains(getString(R.string.temperature_unit_str)) ? report.temperature : report.temperature + getString(R.string.temperature_unit_str));
                        binding.tvInspectionHumidity.setText(report.humidity.contains(getString(R.string.humidity_unit_str)) ? report.humidity : report.humidity + getString(R.string.humidity_unit_str));
                        binding.tvInspectionWeather.setText(report.tq);
                    } else {
                        binding.lltq.setVisibility(View.GONE);
                    }
                }
                
                String reg[] = status.split("/");
                if (reg.length >= 2 && reg[0].equals(reg[1])) {
                    binding.tvContinueInspection.setText(R.string.see_inspection_details_str);
                }
                break;
            default:
                break;
        }
    }


}
