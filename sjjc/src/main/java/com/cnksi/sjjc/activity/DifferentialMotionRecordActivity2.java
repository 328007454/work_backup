package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DifferentialMotionRecordAdapter4;
import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.databinding.ActivityDifferentialMotionRecordBinding;
import com.cnksi.sjjc.service.ReportCdbhclService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * creat by han 2016/05/30
 * 差动保护差流界面
 */
@Route(path = "/different/activity")
public class DifferentialMotionRecordActivity2 extends BaseSjjcActivity {
    private Map<String, ReportCdbhcl> reportCdbhclsMap = new HashMap<String, ReportCdbhcl>();
    private String reportId;
    private String bdzId;
    private DifferentialMotionRecordAdapter4 mDifferentRecordAdapter4;
    private List<CopyItem> listDevices = new ArrayList<CopyItem>();
    private Report mReport;
    private List<CdbhclValue> cdbhclValueList = new ArrayList<>();

    private ActivityDifferentialMotionRecordBinding mRecordBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordBinding = ActivityDifferentialMotionRecordBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setChildView(mRecordBinding.getRoot());
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
        mTitleBinding.tvTitle.setText(R.string.chadong_baohu_jilu);
    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                mReport = ReportService.getInstance().findById(currentReportId);
                bdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
                reportId = PreferencesUtils.get(Config.CURRENT_REPORT_ID, "");
                listDevices = DeviceService.getInstance().getDevicesByNameWays1(bdzId, Config.DIFFERENTIAL_RECORD_KEY);
                for (CopyItem item : listDevices) {
                    CdbhclValue.addObject(item, cdbhclValueList);
                }
                List<ReportCdbhcl> exitCdbhclList = ReportCdbhclService.getInstance().getReportCdbhclList(bdzId, reportId);
                if (null != exitCdbhclList && !exitCdbhclList.isEmpty()) {
                    for (ReportCdbhcl report : exitCdbhclList) {
                        for (CdbhclValue value : cdbhclValueList) {
                            if (value.getId().equalsIgnoreCase(report.device_id)) {
                                value.reportValue(report, value);
                                reportCdbhclsMap.put(value.getId(), report);
                            }

                        }
                    }
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                mDifferentRecordAdapter4 = new DifferentialMotionRecordAdapter4(this, cdbhclValueList, R.layout.infraed_thermometer_item, mRecordBinding.llContainer);
                break;
            default:
                break;
        }
    }

    private void initOnclick() {
        mRecordBinding.btnConfirmSave.setOnClickListener(v -> {
            if (saveData()) {
                Intent intent = new Intent(mActivity, JZLFenJieKaiGuanReportActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            } else {
                ToastUtils.showMessage("输入数据有误，请核对");
            }
        });
        mTitleBinding.btnBack.setOnClickListener(v -> finish());
    }


    private boolean saveData() {
        ReportCdbhcl mCdReport;
        String bdzName = PreferencesUtils.get(Config.CURRENT_BDZ_NAME, "");
        List<ReportCdbhcl> saveList = new ArrayList<>();
        for (CdbhclValue value : cdbhclValueList) {
            if (!TextUtils.isEmpty(value.getValue()) && (99999999 < new Float(value.getValue()) || 0 > new Float(value.getValue()))) {
                return false;
            }
            if (reportCdbhclsMap.containsKey(value.getId())) {
                mCdReport = reportCdbhclsMap.get(value.getId());
                mCdReport.addValue(value);
            } else {
                mCdReport = new ReportCdbhcl(value, reportId, bdzName, bdzId);
                reportCdbhclsMap.put(value.getId(), mCdReport);
            }
            mCdReport.last_modify_time = DateUtils.getCurrentLongTime();
            saveList.add(mCdReport);
        }

        mReport.endtime = DateUtils.getCurrentLongTime();
        try {
            ReportCdbhclService.getInstance().saveOrUpdate(saveList);
            ReportService.getInstance().saveOrUpdate(mReport);
            TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

}
