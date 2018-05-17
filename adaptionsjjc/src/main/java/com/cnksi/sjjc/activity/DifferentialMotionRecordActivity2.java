package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.common.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DifferentialMotionRecordAdapter4;
import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.ActivityDifferentialMotionRecordBinding;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.ReportCdbhclService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * creat by han 2016/05/30
 * 差动保护差流界面
 */
public class DifferentialMotionRecordActivity2 extends BaseActivity {
    //根据变电站id，报告id查询出差动保护相应的数据
    private Map<String, ReportCdbhcl> reportCdbhclsMap = new HashMap<String, ReportCdbhcl>();
    //当前报告id
    private String reportId;
    //当前变电站id
    private String bdzId;
    //差动保护差流Adapter
    private DifferentialMotionRecordAdapter4 mDifferentRecordAdapter4;
    //查询有关保护设备的所有集合
    private List<DbModel> listDevice = new ArrayList<DbModel>();
    private List<CopyItem> listDevices = new ArrayList<CopyItem>();
    //当前的Report表
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
        mRecordBinding.btnConfirmSave.setOnClickListener((v) -> {
            if (saveData()) {
                Intent intent = new Intent(_this, JZLFenJieKaiGuanReportActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                this.finish();
            } else {
                ToastUtils.showMessage("输入数据有误，请核对");
            }
        });
        mTitleBinding.btnBack.setOnClickListener((v) -> {
            finish();
        });
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
            TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

}
