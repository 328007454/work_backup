package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.common.utils.AnimationUtils;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.sjjc.R;
import com.cnksi.common.utils.PlaySound;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * @author wastrel
 */
public abstract class BaseReportActivity extends BaseSjjcActivity {
    public static final int ANIMATION = 0x2000;
    public static final int VIBRATOR = 0x2001;
    protected RelativeLayout rlContainer;
    /**
     * 报告布局
     */
    protected View reportView;
    /**
     * 标题
     */
    protected TextView mTvTitle;
    /**
     * 返回按钮
     */
    protected ImageButton mBtnBack;
    /**
     * 返回主菜单按钮
     */
    protected ImageButton mBtnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDefaultTitle = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_report);
        rlContainer = findViewById(R.id.llbase);
        reportView = setReportView();
        rlContainer.addView(reportView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        AutoUtils.autoSize(reportView);
        reportView.setVisibility(View.INVISIBLE);
        getIntentValue();
        initTitleBar();
        mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
        mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);
    }

    private void initTitleBar() {
        mTvTitle = findViewById(R.id.tv_title);
        mBtnRight = findViewById(R.id.btn_right);
        mBtnRight.setVisibility(View.GONE);
        mBtnBack = findViewById(R.id.btn_back);
        mTvTitle.setText(getString(R.string.report_title_format_str, currentBdzName + currentInspectionTypeName));
        mBtnBack.setOnClickListener(v -> {
            ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);
            BaseReportActivity.this.onBackPressed();
        });
        mBtnRight.setOnClickListener(v -> {
            ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);
            BaseReportActivity.this.onBackPressed();
        });
    }

    public abstract View setReportView();


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case ANIMATION:
                PlaySound.getIntance(mActivity).play(R.raw.print_out);
                reportView.setVisibility(View.VISIBLE);
                AnimationUtils.translateAnimRun(reportView, -reportView.getHeight() * 92 / 100, 0.0f);
                break;
            case VIBRATOR:
                mVibrator.vibrate(735);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        PlaySound.getIntance(mActivity).stop();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ScreenManager.getScreenManager().popAllActivityExceptOne(HomeActivity.class);
                break;
            default:
                break;
        }
        return true;
    }
}
