package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.service.ReportJzlbyqfjkgService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 交直流变压器分接开关记录调整界面
 */
public class TransformAdjustmentRecordActivity extends BaseActivity {
    @ViewInject(R.id.ll_container)
    private LinearLayout llContainer;
    @ViewInject(R.id.ll_container1)
    private LinearLayout llContainer1;
    @ViewInject(R.id.lv_container)
    private ListView lvContainer;


    private List<DbModel> listDbModel;
    TextView tv;
    EditText et;
    //本次读数填写的EditText
    List<EditText> listBcds = new ArrayList<EditText>();
    //动作次数填写的EditText
    List<EditText> listDzcs = new ArrayList<EditText>();
    private List<ReportJzlbyqfjkg> listReport = new ArrayList<ReportJzlbyqfjkg>();

    private String bdzid;
    private ReportJzlbyqfjkg mReportJzlby;
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
        tvTitle.setText(R.string.jiaozhiliu_fenjie_kaiguan);
        llContainer1.setVisibility(View.VISIBLE);
        lvContainer.setVisibility(View.GONE);

    }

    private void initData() {
        bdzid = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_ID, "");
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mReport = ReportService.getInstance().findById(currentReportId);
                    mReport.starttime = DateUtils.getCurrentLongTime();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                listDbModel = ReportJzlbyqfjkgService.getInstance().getPartDevice(bdzid, "分接开关动作次数");
                if (listDbModel != null && !listDbModel.isEmpty()) {
                    for (DbModel model : listDbModel) {
                        ReportJzlbyqfjkg mReport = ReportJzlbyqfjkgService.getInstance().getFirstReport(model.getString("bdzid"), model.getString("deviceid"), currentReportId);
                        listReport.add(mReport);
                    }
                }

                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                for (int i = 0; i < listDbModel.size(); i++) {
                    DbModel model = listDbModel.get(i);
                    String bcds = listReport.get(i).bcds;
                    String dzcs = listReport.get(i).dzcs;
                    ViewHolder holder = new ViewHolder(this, null, R.layout.activity_jzl_add_titleview, false);
                    holder.setText(R.id.jzl_title, model.getString("name"));
                    holder.setText(R.id.et_put_bcds, bcds == null ? "" : bcds);
                    EditText txtBcds = holder.getView(R.id.et_put_bcds);
                    listBcds.add(txtBcds);
                    holder.setText(R.id.et_put_dzcs, dzcs == null ? "" : dzcs);
                    EditText txtDzcs = holder.getView(R.id.et_put_dzcs);
                    listDzcs.add(txtDzcs);
                    llContainer.addView(holder.getRootView());
                }
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
                boolean isSaveTask = true;
                List<ReportJzlbyqfjkg> saveList = new ArrayList<>();
                for (int i = 0; i < listDbModel.size(); i++) {
                    DbModel dbModel = listDbModel.get(i);
                    String bcds = listBcds.get(i).getText().toString();
                    String dzcs = listDzcs.get(i).getText().toString();
                    if (TextUtils.isEmpty(bcds) && TextUtils.isEmpty(dzcs)) {
                        continue;
                    }
                    mReportJzlby = listReport.get(i);
                    mReportJzlby.bdz_id = dbModel.getString("bdzid");
                    mReportJzlby.bdz_name = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_NAME, "");
                    mReportJzlby.device_id = dbModel.getString("deviceid");
                    mReportJzlby.device_name = dbModel.getString("name");
                    mReportJzlby.report_id = currentReportId;
                    mReportJzlby.bcds = bcds;
                    mReportJzlby.dzcs = dzcs;
                    mReportJzlby.last_modify_time = DateUtils.getCurrentLongTime();
                    saveList.add(mReportJzlby);

                }
                try {
                    ReportJzlbyqfjkgService.getInstance().saveOrUpdate(saveList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                try {
                    TaskService.getInstance().update( WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
                    setResult(RESULT_OK);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                try {
                    mReport.endtime = DateUtils.getCurrentLongTime();
                    ReportService.getInstance().saveOrUpdate(mReport);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(_this, JZLFenJieKaiGuanReportActivity.class);
                startActivity(intent);
                this.finish();
                break;

            default:
                break;
        }
    }


}
