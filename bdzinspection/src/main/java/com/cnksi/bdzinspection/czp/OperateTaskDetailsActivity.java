package com.cnksi.bdzinspection.czp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateTaskDetailsBinding;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.OperateType;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsview.CustomerDialog;

/**
 * 操作票任务详细界面
 *
 * @author Oliver
 */
public class OperateTaskDetailsActivity extends BaseActivity {
    /**
     * 操作任务Id
     */
    private String currentOperateId = "1";
    private OperateTick mCurrentOperateTick;
    private boolean isFromCheckedActivity = false;
    private XsActivityOperateTaskDetailsBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_operate_task_details);
       
        initUI();
        initData();

        initOnClick();
    }


    private void initUI() {
        currentOperateId = getIntent().getStringExtra(Config.CURRENT_TASK_ID);
        isFromCheckedActivity = getIntent().getBooleanExtra(Config.IS_FROM_BATTERY, false);
         binding.includeTitle.tvTitle.setText(R.string.xs_operate_task_str);
    }

    private void initData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                mCurrentOperateTick = OperateTicketService.getInstance().findById(currentOperateId);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:

                fillContent();

                break;
            default:
                break;
        }
    }

    /**
     * 填充数据
     */
    private void fillContent() {
        if (mCurrentOperateTick != null) {
             binding.tvOperateContent.setText(mCurrentOperateTick.task);
             binding.tvDepartment.setText(mCurrentOperateTick.unit);
             binding.tvCode.setText(mCurrentOperateTick.code);
             binding.tvFlTime.setText(TextUtils.isEmpty(mCurrentOperateTick.time_fl) ? DateUtils.getCurrentLongTime() : mCurrentOperateTick.time_fl);
             binding.etFlr.setText(TextUtils.isEmpty(mCurrentOperateTick.person_flr) ? "" : mCurrentOperateTick.person_flr);
             binding.etSlr.setText(TextUtils.isEmpty(mCurrentOperateTick.person_slr) ? "" : mCurrentOperateTick.person_slr);
            if (OperateType.jh.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                 binding.rbJh.setChecked(true);
            } else if (OperateType.dr.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                 binding.rbDr.setChecked(true);
            } else if (OperateType.jx.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                 binding.rbJxr.setChecked(true);
            }
        }
    }
    
    private void initOnClick() {
        binding.tvFlTime.setOnClickListener(view -> showTime());
        binding.tvFlTimeContainer.setOnClickListener(view -> showTime());
        binding.ibtnSelectInspectionDate.setOnClickListener(view -> showTime());
        binding.btnConfirm.setOnClickListener(view -> updateOperateTask());
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void showTime() {
        CustomerDialog.showDatePickerDialog(currentActivity, true, (result, position) ->  binding.tvFlTime.setText(result));
    }

    /**
     * 更新操作票任务
     */
    private void updateOperateTask() {

        String flr =  binding.etFlr.getText().toString().trim();
        String slr =  binding.etSlr.getText().toString().trim();
        String flTime =  binding.tvFlTime.getText().toString().trim();
        String code =  binding.tvCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            CToast.showShort(currentActivity, R.string.xs_please_input_code_str);
            return;
        }
        if (TextUtils.isEmpty(flr)) {
            CToast.showShort(currentActivity, R.string.xs_please_input_flr_str);
            return;
        }
        if (TextUtils.isEmpty(slr)) {
            CToast.showShort(currentActivity, R.string.xs_please_input_slr_str);
            return;
        }
        if (TextUtils.isEmpty(flTime)) {
            CToast.showShort(currentActivity, R.string.xs_please_select_fl_time_str);
            return;
        }
        // 选择的操作类型
        String operateType = OperateType.jh.name();
        if ( binding.rbDr.isChecked()) {
            operateType = OperateType.dr.name();
        } else if ( binding.rbJxr.isChecked()) {
            operateType = OperateType.jx.name();
        }
        if (mCurrentOperateTick == null) {
            return;
        }
        mCurrentOperateTick.operate_type = operateType;
        mCurrentOperateTick.person_flr = flr;
        mCurrentOperateTick.person_slr = slr;
        mCurrentOperateTick.time_fl = flTime;
        mCurrentOperateTick.code = code;

        boolean isSuccess = OperateTicketService.getInstance().saveOrUpdate(mCurrentOperateTick);
        if (isSuccess) {
            // TODO:跳转到操作票工作界面
            Intent intent = new Intent(currentActivity, OperateWorkActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, currentOperateId);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromCheckedActivity) {
            Intent intent = new Intent(currentActivity, OperateTaskListActivity.class);
            intent.putExtra(Config.IS_FROM_BATTERY, true);
            startActivity(intent);
        }
        this.finish();
    }
}