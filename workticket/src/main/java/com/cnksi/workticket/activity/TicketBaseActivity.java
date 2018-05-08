package com.cnksi.workticket.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;

/**
 * @author Mr.K  on 2018/4/28.
 */

public abstract class TicketBaseActivity extends BaseCoreActivity {

    /**
     * 重写父类
     *
     * @return
     */
    @Override
    public abstract int getLayoutResId();

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void getIntentVaule() {
        Intent intent = getIntent();
        Config.userName = intent.getStringExtra(Config.CURRENT_LOGIN_USER);
        Config.userAccount = intent.getStringExtra(Config.CURRENT_LOGIN_ACCOUNT);
        Config.deptID = intent.getStringExtra(Config.CURRENT_DEPARTMENT_ID);
        Config.deptName = intent.getStringExtra(Config.CURRENT_DEPARTMENT_NAME);
        Config.SYNC_APP_ID_VALUE = intent.getStringExtra(Config.KEY_SYNC_APP_ID);
        Config.SYNC_URL_VALUE = intent.getStringExtra(Config.KEY_SYNC_URL);
    }

    /**
     * 对PagerSlidingmPagerTabStriptrip的各项属性进行赋值。
     */

    protected void setTabStripStyle(PagerSlidingTabStrip mPagerTabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = this.getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        mPagerTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        mPagerTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色  _this.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setIndicatorColor(ContextCompat.getColor(this, R.color.tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)_this.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setSelectedTextColor(ContextCompat.getColor(this, R.color.tab_strip_text_color));
        mPagerTabStrip.setTextColor(ContextCompat.getColor(this, R.color.ticket_color_A8ABAA));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }

}
