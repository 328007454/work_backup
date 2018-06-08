package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.databinding.ActivityDifferentialMotionRecordBinding;
import com.cnksi.sjjc.service.ReportJzlbyqfjkgService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * Created by han on 2016/9/6.
 * 修改过后的交直流分接开关
 */
public class NewTransformRecordActivity extends BaseSjjcActivity {
    private List<DbModel> listDbModel;
    //本次读数填写的EditText
    List<EditText> listBcds = new ArrayList<EditText>();
    //动作次数填写的EditText
    List<EditText> listDzcs = new ArrayList<EditText>();
    private List<ReportJzlbyqfjkg> listReport = new ArrayList<ReportJzlbyqfjkg>();
    private String bdzid;
    private ReportJzlbyqfjkg mReportJzlby;
    private Report mReport;

    private ActivityDifferentialMotionRecordBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDifferentialMotionRecordBinding.inflate(getLayoutInflater());
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
        mTitleBinding.tvTitle.setText(R.string.jiaozhiliu_fenjie_kaiguan);
        binding.llContainer1.setVisibility(View.VISIBLE);
        binding.lvContainer.setVisibility(View.GONE);

    }

    public void loadData() {
        bdzid = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        ExecutorManager.executeTaskSerially(() -> {
            try {
                mReport = ReportService.getInstance().findById(currentReportId);
                mReport.starttime = DateUtils.getCurrentLongTime();
            } catch (DbException e) {
                e.printStackTrace();
            }
            listDbModel = DeviceService.getInstance().searchDevicesByNameWays(bdzid, Config.TANSFORMADJUSTMENT_KAIGUAN, Config.TANSFORMADJUSTMENT_DANGWEI);
            if (listDbModel != null && !listDbModel.isEmpty()) {
                for (DbModel model : listDbModel) {
                    ReportJzlbyqfjkg mReport = ReportJzlbyqfjkgService.getInstance().getFirstReport(model.getString("bdzid"), model.getString("deviceid"), currentReportId);
                    listReport.add(mReport);
                }
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
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
                    ArrayList<String> keys = com.cnksi.core.utils.StringUtils.stringToList(key);
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
                            holder.setVisibility(R.id.ll_dongzuo_container, View.GONE);
                            holder.setText(R.id.copy_part_one, description);
                            holder.setText(R.id.et_put_bcds, bcds == null ? "" : bcds);
                        } else {
                            holder.setVisibility(R.id.ll_bcds_container, View.GONE);
                            holder.setText(R.id.copy_part_two, description);
                            holder.setText(R.id.et_put_dzcs, TextUtils.isEmpty(dzcs) ? "" : dzcs);
                        }
                    } else {
                        ArrayList<String> descriptionList = com.cnksi.core.utils.StringUtils.stringToList(description);
                        holder.setText(R.id.copy_part_one, descriptionList.get(1));
                        holder.setText(R.id.copy_part_two, descriptionList.get(0));
                        holder.setText(R.id.et_put_dzcs, TextUtils.isEmpty(dzcs) ? "" : dzcs);
                        holder.setText(R.id.et_put_bcds, bcds == null ? "" : bcds);
                    }
                    binding.llContainer.addView(holder.getRootView());
                }
                break;
            default:
                break;
        }
    }

    private void initOnClick() {

        mTitleBinding.btnBack.setOnClickListener(view -> finish());

        binding.btnConfirmSave.setOnClickListener(view -> {
            List<ReportJzlbyqfjkg> saveList = new ArrayList<>();
            if (listDbModel != null && listDbModel.size() > 0) {
                for (int i = 0; i < listDbModel.size(); i++) {
                    DbModel dbModel = listDbModel.get(i);
                    String bcds = StringUtilsExt.getDecimalPoint(listBcds.get(i).getText().toString());
                    String dzcs = StringUtilsExt.getDecimalPoint(listDzcs.get(i).getText().toString());
                    if (TextUtils.isEmpty(bcds) && TextUtils.isEmpty(dzcs)) {
                        continue;
                    }
                    mReportJzlby = listReport.get(i);
                    mReportJzlby.bdz_id = dbModel.getString("bdzid");
                    mReportJzlby.bdz_name = PreferencesUtils.get(Config.CURRENT_BDZ_NAME, "");
                    mReportJzlby.device_id = dbModel.getString("deviceid");
                    mReportJzlby.device_name = dbModel.getString("name");
                    mReportJzlby.report_id = currentReportId;
                    mReportJzlby.bcds = bcds;
                    mReportJzlby.dzcs = dzcs;
                    mReportJzlby.last_modify_time = DateUtils.getCurrentLongTime();
                    saveList.add(mReportJzlby);
                    try {

                        ReportJzlbyqfjkgService.getInstance().saveOrUpdate(mReportJzlby);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, TaskStatus.done.name()));
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
            Intent intent = new Intent(mActivity, JZLFenJieKaiGuanReportActivity.class);
            startActivity(intent);
            finish();
        });
    }


}