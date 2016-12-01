package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.service.PreventionService;
import com.cnksi.sjjc.util.DialogUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by ironGe on 2016/6/7.
 * <p/>
 * 防小动物措施检查界面
 */
public class PreventAnimalSecondActivity extends BaseActivity {

    /**
     * 发现问题点
     */
    @ViewInject(R.id.tv_discover_position)
    private TextView tvDiscover;
    /**
     * 清楚情况
     */
    @ViewInject(R.id.tv_clear_position)
    private TextView tvClear;

    @ViewInject(R.id.radio_switch)
    private RadioGroup radioGroupSwitch;

    @ViewInject(R.id.radio_indoor)
    private RadioGroup radioGroupIndoor;

    @ViewInject(R.id.radio_outdoor)
    private RadioGroup radioGroupOutdoor;

    @ViewInject(R.id.radio_window)
    private RadioGroup radioGroupWindow;

    @ViewInject(R.id.radio_ratsbane)
    private RadioGroup radioGroupRatsbane;

    @ViewInject(R.id.radio_mousetrap)
    private RadioGroup radioGroupMousetrap;

    private PreventionRecord preventionRecord;
    //
    private List<HoleRecord> mHoleList;

    private List<HoleRecord> clearHoleList;

    private Report report;


    private RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (radioGroup.getId()) {
                case R.id.radio_switch:
                    preventionRecord.switchStatus = (i == R.id.switch_yes) ? 0 : 1;
                    break;
                case R.id.radio_indoor:
                    preventionRecord.inroomStatus = (i == R.id.indoor_yes) ? 0 : 1;
                    break;
                case R.id.radio_outdoor:
                    preventionRecord.outroomStatus = (i == R.id.outdoor_yes) ? 0 : 1;
                    break;
                case R.id.radio_window:
                    preventionRecord.doorWindowStatus = (i == R.id.window_yes) ? 0 : 1;
                    break;
                case R.id.radio_ratsbane:
                    preventionRecord.ratsbaneStatus = (i == R.id.ratsbane_yes) ? 0 : 1;
                    break;
                case R.id.radio_mousetrap:
                    preventionRecord.mousetrapStatus = (i == R.id.mousetrap_yes) ? 0 : 1;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_prevent_second_animal);
        x.view().inject(_this);
        getIntentValue();
        initUI();
        initDatata();
    }

    private void initUI() {
        tvTitle.setText("防小动物措施检查");
        radioGroupSwitch.setOnCheckedChangeListener(checkedChangeListener);
        radioGroupIndoor.setOnCheckedChangeListener(checkedChangeListener);
        radioGroupMousetrap.setOnCheckedChangeListener(checkedChangeListener);
        radioGroupOutdoor.setOnCheckedChangeListener(checkedChangeListener);
        radioGroupWindow.setOnCheckedChangeListener(checkedChangeListener);
        radioGroupRatsbane.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void initDatata() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    preventionRecord = PreventionService.getInstance().findPreventionRecordByReoprtId(currentReportId);
                    report = db.findById(Report.class, currentReportId);
//                    preventionRecord = (PreventionRecord) getIntent().getSerializableExtra("PreventionRecord");
                    mHoleList = HoleReportService.getInstance().getCurrentClearRecord(currentReportId, currentBdzId);
                    if (null == preventionRecord) {
                        preventionRecord = new PreventionRecord(currentReportId, currentBdzId, currentBdzName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    @Event({R.id.btn_next})
    private void clickEvent(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                showSureDialog();
                break;

        }
    }

    private SureHolder mSureHolder;
    private Dialog mSureDialog;

    private void showSureDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 7 / 9;
        if (mSureHolder == null) {
            mSureHolder = new SureHolder();
        }
        if (mSureDialog == null) {
            mSureDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_tips, mSureHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
        mSureHolder.tvTitle.setText("提示");
        mSureHolder.tvContent.setText("是否完成本次检查");
        mSureDialog.show();
        mSureHolder.btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSureDialog.dismiss();
            }
        });

        mSureHolder.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    preventionRecord.last_modify_time = DateUtils.getCurrentLongTime();
                    db.saveOrUpdate(preventionRecord);
                    report.endtime = DateUtils.getCurrentLongTime();
                    db.saveOrUpdate(report);
                    db.update(Task.class, WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS,Task.TaskStatus.done.name()));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(_this, AnimalReportActivity.class);
                isNeedUpdateTaskState=true;
                startActivity(intent);
                _this.finish();
                mSureDialog.dismiss();
                ScreenManager.getScreenManager().popActivity(PreventAnimalActivity.class);

            }
        });

    }

    class SureHolder {
        @ViewInject(R.id.tv_dialog_title)
        private TextView tvTitle;
        @ViewInject(R.id.tv_dialog_content)
        private TextView tvContent;

        @ViewInject(R.id.btn_cancel)
        private Button btCancle;
        @ViewInject(R.id.btn_sure)
        private Button btFinish;

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (preventionRecord != null) {
                    checkStatus();
                }
                CheckResult();
                break;
            default:
                break;
        }
    }

    private void checkStatus() {
        if (0 == preventionRecord.switchStatus) radioGroupSwitch.check(R.id.switch_yes);
        else radioGroupSwitch.check(R.id.switch_no);

        if (0 == preventionRecord.inroomStatus) radioGroupIndoor.check(R.id.indoor_yes);
        else radioGroupIndoor.check(R.id.indoor_no);

        if (0 == preventionRecord.outroomStatus) radioGroupOutdoor.check(R.id.outdoor_yes);
        else radioGroupOutdoor.check(R.id.outdoor_no);

        if (0 == preventionRecord.doorWindowStatus) radioGroupWindow.check(R.id.window_yes);
        else radioGroupWindow.check(R.id.window_no);

        if (0 == preventionRecord.ratsbaneStatus) radioGroupRatsbane.check(R.id.ratsbane_yes);
        else radioGroupRatsbane.check(R.id.ratsbane_no);

        if (0 == preventionRecord.mousetrapStatus) radioGroupMousetrap.check(R.id.mousetrap_yes);
        else radioGroupMousetrap.check(R.id.mousetrap_no);

    }

    private void CheckResult() {
        String problemPosition = "";
        String morePostion = "";
        if (mHoleList != null && mHoleList.size() > 0) {

            for (HoleRecord record : mHoleList) {
                if(currentReportId.equals(record.reportId)){
                    problemPosition += record.location + ",";
                }
                if(currentReportId.equals(record.clear_reportid)&&"1".equals(record.status)){
                    morePostion += record.location + "_" + record.hole_detail + ",";
                }
            }
            if (problemPosition.endsWith(",")) {
                problemPosition = problemPosition.substring(0, problemPosition.length()-1);
            }
            if (morePostion.endsWith(",")) {
                morePostion = morePostion.substring(0, morePostion.length()-1);
            }
        }
        if (!TextUtils.isEmpty(problemPosition)) {
            tvDiscover.setVisibility(View.VISIBLE);
            tvDiscover.setText(problemPosition);
        }
        if (!TextUtils.isEmpty(morePostion)) {
            tvClear.setVisibility(View.VISIBLE);
            tvClear.setText(morePostion);
        }


    }
}
