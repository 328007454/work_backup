package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.util.PlaySound;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.x;

/**
 * @author wastrel
 *
 */
public abstract class BaseReportActivity extends BaseActivity {
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_report);
        rlContainer = FindViewById(R.id.llbase);
        reportView = setReportView();
        rlContainer.addView(reportView,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT));
        AutoUtils.autoSize(reportView);
        reportView.setVisibility(View.INVISIBLE);
        x.view().inject(_this);
        getIntentValue();
        initTitleBar();
        mHandler.sendEmptyMessageDelayed(VIBRATOR, 500);
        mHandler.sendEmptyMessageDelayed(ANIMATION, 2000);
    }

    private void initTitleBar() {
        mTvTitle=FindViewById(R.id.tv_title);
        mBtnRight =FindViewById(R.id.btn_right);
        mBtnBack =FindViewById(R.id.btn_back);
        mTvTitle.setText(getString(R.string.report_title_format_str, currentBdzName + currentInspectionName));
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ScreenManager.getScreenManager().popAllActivityExceptOne(LauncherActivity.class);
                ScreenManager.getInstance().popAllActivityExceptOne(HomeActivity.class);
                onBackPressed();
            }
        });
    }

    public abstract View setReportView();


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case ANIMATION:
                PlaySound.getIntance(mCurrentActivity).play(R.raw.print_out);
                reportView.setVisibility(View.VISIBLE);
                translateAnimRun(reportView, -reportView.getHeight() * 92 / 100, 0.0f);
                break;
            case VIBRATOR:
                mVibrator.vibrate(735);
                break;
            default:
                break;
        }
    }
    @SuppressWarnings("unchecked")
    protected <T extends View> T FindViewById(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        PlaySound.getIntance(_this).stop();
        super.onDestroy();
    }
}
