package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.czp.OperateTaskCheckedActivity;
import com.cnksi.bdzinspection.daoservice.OperateItemService;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityDownloadOptrTickBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.model.OperateItem;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.OpearTickParser;
import com.cnksi.bdzinspection.utils.OperateTick;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.core.utils.ScreenUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * @author ksi-android
 */
public class DownloadOperationTickActivity extends TitleActivity {


    private com.cnksi.bdzinspection.model.OperateTick mOperateTick;
    private boolean isSuccess;

    // 解析路径
    private String path = Config.DATABASE_FOLDER + "print.txt";
    private XsActivityDownloadOptrTickBinding binding;

    @Override
    protected int setLayout() {
        return R.layout.xs_activity_download_optr_tick;
    }

    @Override
    protected String initialUI() {
        binding = (XsActivityDownloadOptrTickBinding) getDataBinding();
        final Animation animation1 = AnimationUtils.loadAnimation(currentActivity, R.anim.xs_slide_in_right2);
        final Animation animation2 = AnimationUtils.loadAnimation(currentActivity, R.anim.xs_slide_in_left2);

        animation1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.img2.startAnimation(animation2);
            }
        });
        animation2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.img2.startAnimation(animation1);
            }
        });
        binding.img2.startAnimation(animation1);

        initOnClick();

        return getResources().getString(R.string.xs_czp_download_title);
    }


    @Override
    protected void initialData() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            path = getIntent().getStringExtra("filePath");
            if (TextUtils.isEmpty(path)) {
                path = Config.DATABASE_FOLDER + "print.txt";
            }
            parseData(0);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        path = intent.getStringExtra("filePath");
        if (TextUtils.isEmpty(path)) {
            path = Config.DATABASE_FOLDER + "print.txt";
        }
        parseData(0);
    }

    /**
     * 解析数据
     */
    private void parseData(long delayTime) {
        mHandler.postDelayed(() -> {
            OperateTick operateTick = OpearTickParser.parseOpearTick(path);
            if (operateTick != null) {
                mOperateTick = new com.cnksi.bdzinspection.model.OperateTick(operateTick.getTaskName(),
                        operateTick.getCode(), operateTick.getUnit());
                try {
                    isSuccess = OperateTicketService.getInstance().saveOrUpdate(mOperateTick);
                } catch (DbException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
                if (isSuccess && operateTick.getOpears() != null && operateTick.getOpears().size() > 0) {
                    ArrayList<OperateItem> operateItemList = new ArrayList<OperateItem>();
                    for (int i = 0, count = operateTick.getOpears().size(); i < count; i++) {
                        operateItemList.add(new OperateItem(mOperateTick.id, String.valueOf(i + 1),
                                operateTick.getOpears().get(i)));
                    }
                    try {
                        isSuccess = OperateItemService.getInstance().saveOrUpdate(operateItemList);
                    } catch (DbException e) {
                        e.printStackTrace();
                        isSuccess = false;
                    }
                }
                if (isSuccess) {
                    mHandler.sendEmptyMessage(LOAD_DATA);
                }
            }
        }, delayTime);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.cnksi.bdzinspection.activity.TitleActivity#releaseData()
     */
    @Override
    protected void releaseResAndSaveData() {

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                TTSUtils.getInstance().startSpeaking("接收到新的操作票");
                showTipsDialog(mOperateTick.task);
                break;
            default:
                break;
        }
    }

    private void initOnClick() {
        titlebaseBinding.ibtnCancel.setOnClickListener(view -> DownloadOperationTickActivity.this.onBackPressed());
        binding.llRootContainer.setOnLongClickListener(view -> {
            mVibrator.vibrate(50);
            DownloadOperationTickActivity.this.parseData(3000);
            return true;
        });
    }


    /**
     * 接收到操作任务提示
     */
    XsDialogTipsBinding tipsBinding;

    protected void showTipsDialog(String content) {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        tipsBinding.tvDialogContent.setText(content);
        tipsDialog.show();
        tipsBinding.btnSure.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, OperateTaskCheckedActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, mOperateTick.id);
            intent.putExtra(Config.IS_FROM_BATTERY, true);
            DownloadOperationTickActivity.this.startActivity(intent);
            currentActivity.finish();
        });
        tipsBinding.btnCancel.setOnClickListener(view -> tipsDialog.dismiss());
    }

    @Override
    public void onBackPressed() {
        if (isSuccess) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}