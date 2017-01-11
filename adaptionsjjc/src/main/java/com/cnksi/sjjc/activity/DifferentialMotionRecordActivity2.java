package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DifferentialMotionRecordAdapter3;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.CopyResult;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.ReportCdbhclService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * creat by han 2016/05/30
 * 差动保护差流界面
 */
public class DifferentialMotionRecordActivity2 extends BaseActivity {
    @ViewInject(R.id.lv_container)
    private ListView lvContainer;

    @ViewInject(R.id.ll_container)
    private LinearLayout llContainer;

    @ViewInject(R.id.ll_container1)
    private LinearLayout llContainer1;
    //根据变电站id，报告id查询出差动保护相应的数据
    private Map<String, ReportCdbhcl> reportCdbhclsMap;
    private Map<String, CopyResult> copyResultMap;
    //当前报告id
    private String reportId;
    //当前变电站id
    private String bdzId;
    //差动保护差流Adapter
    private DifferentialMotionRecordAdapter3 mDifferentRecordAdapter;
    //查询有关保护设备的所有集合
    private List<DbModel> listDevice = new ArrayList<DbModel>();
    private List<CopyItem> listDevices = new ArrayList<CopyItem>();
    //当前的Report表
    private Report mReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_differential_motion_record);
        getIntentValue();
        initUI();
        initData();
    }

    private void initUI() {
        tvTitle.setText(R.string.chadong_baohu_jilu);
//        llContainer1.setVisibility(View.);
//        lvContainer.setVisibility(View.VISIBLE);
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = db.selector(Report.class).where(Report.REPORTID, "=", currentReportId).findFirst();
                    bdzId = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_ID, "");
                    reportId = PreferencesUtils.getString(_this, Config.CURRENT_REPORT_ID, "");
                    if (Config.NEW_COPY) {
                        if (true)
                            listDevices = DeviceService.getInstance().getDevicesByNameWays1(bdzId, Config.DIFFERENTIAL_RECORD_KEY);
                        else
                            listDevice = DeviceService.getInstance().getDevicesByNameWays(bdzId, Config.DIFFERENTIAL_RECORD_KEY);
                    } else
                        listDevice = DeviceService.getInstance().getDevicesByName(bdzId, "差流");
                    List<ReportCdbhcl> exitCdbhclList = ReportCdbhclService.getIntance().getReportCdbhclList(bdzId, reportId);
                    if (null != exitCdbhclList && !exitCdbhclList.isEmpty()) {
                        reportCdbhclsMap = new HashMap<String, ReportCdbhcl>();
                        for (ReportCdbhcl report : exitCdbhclList) {
                            reportCdbhclsMap.put(report.device_id, report);
                        }
                    }
                    mHandler.sendEmptyMessage(LOAD_DATA);
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
//                mDifferentRecordAdapter = new DifferentialMotionRecordAdapter2(this, listDevice, R.layout.infraed_thermometer_item);
                mDifferentRecordAdapter = new DifferentialMotionRecordAdapter3(this, listDevices,R.layout.infraed_thermometer_item,llContainer);
//                lvContainer.setAdapter(mDifferentRecordAdapter);
                mDifferentRecordAdapter.setRecordList(reportCdbhclsMap);
                break;
            default:
                break;
        }
    }

    @Event({R.id.btn_back, R.id.btn_confirm_save})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_confirm_save:
                saveData();
                Intent intent = new Intent(_this, JZLFenJieKaiGuanReportActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }
    }
    @SuppressWarnings("unchecked")
    private void saveData() {
        ReportCdbhcl mCdReport;
        String bdzName = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_NAME, "");
        HashMap<String, ReportCdbhcl> map = (HashMap<String, ReportCdbhcl>) mDifferentRecordAdapter.getMap();
        for (Map.Entry<String, ReportCdbhcl> entry : map.entrySet()) {
            mCdReport = entry.getValue();
            try {
                mCdReport.report_id = reportId;
                mCdReport.bdz_name = bdzName;
                mCdReport.bdz_id = bdzId;
                mCdReport.device_id = entry.getKey();
                for (CopyItem item : listDevices) {
                    if (item.deviceid.equals(entry.getKey())) {
                        mCdReport.device_name = item.device_name;
                    }
                }
                mCdReport.last_modify_time = DateUtils.getCurrentLongTime();
                ReportCdbhclService.getIntance().saveOrUpdate(mCdReport);
                db.update(Task.class, WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));

            } catch (DbException e) {
                e.printStackTrace();
            }

        }
        mReport.endtime = DateUtils.getCurrentLongTime();
        try {
            db.saveOrUpdate(mReport);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
