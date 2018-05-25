package com.cnksi.bdzinspection.czp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateTaskDetailsBinding;
import com.cnksi.bdzinspection.emnu.OperateType;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.common.Config;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.ex.DbException;

import static com.cnksi.common.Config.LOAD_DATA;

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

        initialUI();
        initialData();

        initOnClick();
    }


    private void initialUI() {
        currentOperateId = getIntent().getStringExtra(Config.CURRENT_TASK_ID);
        isFromCheckedActivity = getIntent().getBooleanExtra(Config.IS_FROM_BATTERY, false);
        binding.includeTitle.tvTitle.setText(R.string.xs_operate_task_str);
    }

    private void initialData() {

        ExecutorManager.executeTask(() -> {
            try {
                mCurrentOperateTick = OperateTicketService.getInstance().findById(currentOperateId);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (mCurrentOperateTick == null) {
                OperateTaskDetailsActivity.this.runOnUiThread(() -> ToastUtils.showMessage("没有找到操作票!"));
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
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
        binding.tvFlTime.setOnClickListener(view -> OperateTaskDetailsActivity.this.showTime());
        binding.tvFlTimeContainer.setOnClickListener(view -> OperateTaskDetailsActivity.this.showTime());
        binding.ibtnSelectInspectionDate.setOnClickListener(view -> OperateTaskDetailsActivity.this.showTime());
        binding.btnConfirm.setOnClickListener(view -> OperateTaskDetailsActivity.this.updateOperateTask());
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> OperateTaskDetailsActivity.this.onBackPressed());

    }

    private void showTime() {
        CustomerDialog.showDatePickerDialog(currentActivity, true, (result, position) -> binding.tvFlTime.setText(result));
    }

    /**
     * 更新操作票任务
     */
    private void updateOperateTask() {

        String flr = binding.etFlr.getText().toString().trim();
        String slr = binding.etSlr.getText().toString().trim();
        String flTime = binding.tvFlTime.getText().toString().trim();
        String code = binding.tvCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showMessage(R.string.xs_please_input_code_str);
            return;
        }
        if (TextUtils.isEmpty(flr)) {
            ToastUtils.showMessage(R.string.xs_please_input_flr_str);
            return;
        }
        if (TextUtils.isEmpty(slr)) {
            ToastUtils.showMessage(R.string.xs_please_input_slr_str);
            return;
        }
        if (TextUtils.isEmpty(flTime)) {
            ToastUtils.showMessage(R.string.xs_please_select_fl_time_str);
            return;
        }
        // 选择的操作类型
        String operateType = OperateType.jh.name();
        if (binding.rbDr.isChecked()) {
            operateType = OperateType.dr.name();
        } else if (binding.rbJxr.isChecked()) {
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

        boolean isSuccess = false;
        try {
            OperateTicketService.getInstance().saveOrUpdate(mCurrentOperateTick);
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
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
