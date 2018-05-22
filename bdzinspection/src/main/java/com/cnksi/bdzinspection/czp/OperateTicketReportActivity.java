package com.cnksi.bdzinspection.czp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.bdzinspection.daoservice.OperateItemService;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateTicketReportBinding;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.bdzinspection.utils.AnimationUtils;
import com.cnksi.common.Config;
import com.cnksi.bdzinspection.utils.Config.OperateType;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.xscore.xscommon.ScreenManager;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 操作票报告界面
 *
 * @author Oliver
 */
public class OperateTicketReportActivity extends BaseActivity {
    public static final int ANIMATION = 0X100;

    public static final int VIBRATOR = 0x101;
    private OperateTick mCurrentOperateTick = null;
    private String currentOperateId;
    private String operateItemCount = "0";
    private boolean isFromWorkPage = true;
    private XsActivityOperateTicketReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_operate_ticket_report);
        initialUI();
        initialData();
        initOnClick();
    }


    private void initialUI() {
        currentOperateId = getIntent().getStringExtra(Config.CURRENT_TASK_ID);
        isFromWorkPage = getIntent().getBooleanExtra(Config.IS_FROM_BATTERY, true);
          binding.includeTitle.tvTitle.setText(getString(R.string.xs_operate_report_str));
        binding.includeTitle.ibtnCancel.setVisibility(View.GONE);
        binding.tvDashLine.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        PlaySound.getIntance(currentActivity).play(R.raw.printing);
        mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
        mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);

    }

    private void initialData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                // 查询状态
                mCurrentOperateTick = OperateTicketService.getInstance().findById(currentOperateId);
                operateItemCount = OperateItemService.getInstance().getCountByOperateTick(currentOperateId);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    private void initOnClick() {
        binding.btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, OperateTaskListActivity.class);
            intent.putExtra(Config.IS_FROM_BATTERY, isFromWorkPage);
            startActivity(intent);
            ScreenManager.getInstance().popAllActivityExceptOne(TaskRemindActivity.class);
            this.finish();
        });
    }
	
    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case ANIMATION:

                PlaySound.getIntance(currentActivity).play(R.raw.print_out);
                AnimationUtils.translateAnimRun(binding.llReportContentContainer, 0.0f,
                        binding.llReportContentContainer.getHeight() * 92 / 100);

                break;
            case VIBRATOR:
                mVibrator.vibrate(735);
                break;
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
            binding.tvFlr.setText(mCurrentOperateTick.person_flr);
            binding.tvSlr.setText(mCurrentOperateTick.person_slr);
            binding.tvFltime.setText(mCurrentOperateTick.time_fl);
            binding.tvOperateStart.setText(mCurrentOperateTick.time_operate_start);
            binding.tvOperateEnd.setText(mCurrentOperateTick.time_operate_end);
            if (OperateType.dr.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                binding.tvOperationType.setText(OperateType.dr.toString());
            } else if (OperateType.jh.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                binding.tvOperationType.setText(OperateType.jh.toString());
            } else if (OperateType.jx.name().equalsIgnoreCase(mCurrentOperateTick.operate_type)) {
                binding.tvOperationType.setText(OperateType.jx.toString());
            }
            binding.tvInspectionResult.setText(getString(R.string.xs_complete_operate_item_count_format_str, operateItemCount));
        }
    }

    @Override
    protected void onDestroy() {
        PlaySound.getIntance(currentActivity).stop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ExitThisAndGoLauncher();
                break;
            default:
                break;
        }

        return true;
    }
}
