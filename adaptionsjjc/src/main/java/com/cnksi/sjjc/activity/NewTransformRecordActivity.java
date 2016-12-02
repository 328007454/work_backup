package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.ReportJzlbyqfjkgService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by han on 2016/9/6.
 * 修改过后的交直流分接开关
 */
public class NewTransformRecordActivity extends BaseActivity {

    @ViewInject(R.id.ll_container)
    private LinearLayout llContainer;
    @ViewInject(R.id.ll_container1)
    private LinearLayout llContainer1;
    @ViewInject(R.id.lv_container)
    private ListView lvContainer;

    private List<DbModel> listDbModel;
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
                    mReport = db.selector(Report.class).where(Report.REPORTID, "=", currentReportId).findFirst();
                    mReport.starttime = DateUtils.getCurrentLongTime();
                } catch (DbException e) {
                    e.printStackTrace();
                }
                listDbModel = DeviceService.getInstance().searchDevicesByNameWays(bdzid, Config.TANSFORMADJUSTMENT_KAIGUAN, Config.TANSFORMADJUSTMENT_DANGWEI);
                if (listDbModel != null && !listDbModel.isEmpty()) {
                    for (DbModel model : listDbModel) {
                        ReportJzlbyqfjkg mReport = ReportJzlbyqfjkgService.getIntance().getFirstReport(model.getString("bdzid"), model.getString("deviceid"),currentReportId);
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
                    String description = model.getString("description");
                    String key = model.getString("key");
                    ArrayList<String> keys = StringUtils.string2List(key);
                    String bcds = listReport.get(i).bcds;
                    String dzcs = listReport.get(i).dzcs;
                    ViewHolder holder = new ViewHolder(this, null, R.layout.activity_jzl_add_titleview, false);
                    holder.setText(R.id.jzl_title, model.getString("name"));
                    EditText txtBcds = holder.getView(R.id.et_put_bcds);
                    listBcds.add(txtBcds);
                    holder.setText(R.id.et_put_dzcs, dzcs == null ? "" : dzcs);
                    EditText txtDzcs = holder.getView(R.id.et_put_dzcs);
                    listDzcs.add(txtDzcs);
                    if (keys.size() == 1) {
                        if (key.equalsIgnoreCase(Config.TANSFORMADJUSTMENT_DANGWEI)) {
                            holder.setVisable(R.id.ll_dongzuo_container, View.GONE);
                            holder.setText(R.id.copy_part_one, description);
                            holder.setText(R.id.et_put_bcds, bcds == null ? "" : bcds);
                        } else {
                            holder.setVisable(R.id.ll_bcds_container, View.GONE);
                            holder.setText(R.id.copy_part_two, description);
                            holder.setText(R.id.et_put_dzcs, TextUtils.isEmpty(dzcs) ? "" : dzcs);
                        }
                    } else {
                        ArrayList<String> descriptionList = StringUtils.string2List(description);
                        holder.setText(R.id.copy_part_one, descriptionList.get(1));
                        holder.setText(R.id.copy_part_two, descriptionList.get(0));
                        holder.setText(R.id.et_put_dzcs, TextUtils.isEmpty(dzcs) ? "" : dzcs);
                        holder.setText(R.id.et_put_bcds, bcds == null ? "" : bcds);
                    }
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
                for (int i = 0; i < listDbModel.size(); i++) {
                    DbModel dbModel = listDbModel.get(i);
                    String bcds = listBcds.get(i).getText().toString();
                    String dzcs = listDzcs.get(i).getText().toString();
                    if(TextUtils.isEmpty(bcds)&&TextUtils.isEmpty(dzcs)){
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
                    try {
                        listReport.get(i).id = mReportJzlby.id;
                        db.saveOrUpdate(mReportJzlby);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    db.update(Task.class, WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
                    setResult(RESULT_OK);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                try {
                    mReport.endtime = DateUtils.getCurrentLongTime();
                    db.saveOrUpdate(mReport);
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