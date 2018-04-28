package com.cnksi.xscore.xsactivity;

import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Toast;

import com.cnksi.xscore.R;
import com.cnksi.xscore.xsapplication.CoreApplication;
import com.cnksi.xscore.xscommon.ScreenManager;
import com.cnksi.xscore.xsutils.BitmapHelp;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsview.CustomerDialog;
import com.cnksi.xscore.xsview.PagerSlidingTabStrip;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public abstract class BaseCoreActivity extends AppCompatActivity {
    /**
     * 退出时间
     */
    protected static long currentBackPressedTime = 0;
    /**
     * 退出间隔
     */
    protected static final int BACK_PRESSED_INTERVAL = 2000;
    /**
     * 第一次加载数据
     */
    public static final int LOAD_DATA = 0x1;
    /**
     * 刷新数据
     */
    public static final int REFRESH_DATA = 0x2;
    /**
     * 保存数据
     */
    public static final int SAVE_DATA = 0x3;
    /**
     * 请求失败
     */
    public static final int ERROR_DATA = 0x4;
    /**
     * Session过期
     */
    public static final int OVER_TIME_DATA = 0x5;
    /**
     * 启动照相请求
     */
    public static final int ACTION_IMAGE = 0x6;
    /**
     * 网络不可用
     */
    public static final int NETWORK_UNVISIBLE = 0x7;
    /**
     * 加载更多
     */
    public static final int LOAD_MORE_DATA = 0x8;
    /**
     * 上传进度条
     */
    public static final int UPDATE_PROGRESS_BAR = 0x9;
    /**
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = 0x10;
    /**
     * 裁剪图片
     */
    public static final int CROP_PICTURE = 0x11;
    public static final int UPLOAD_DEVICE_INFOR_CODE = CROP_PICTURE + 1;
    public static final int UPDATE_APP_CODE = UPLOAD_DEVICE_INFOR_CODE + 1;
    public static final int INSTALL_APP_CODE = UPDATE_APP_CODE + 1;
    /**
     * VPN成功返回
     */
    public static final int ICS_OPENVPN_PERMISSION = INSTALL_APP_CODE + 1;
    public static final int ICS_OPENVPN_MESSAGE_CODE = ICS_OPENVPN_PERMISSION + 1;
    public static final int START_PROFILE_BYUUID = ICS_OPENVPN_MESSAGE_CODE + 1;
    /**
     * 成功代码
     */
    public static final String OK_CODE = "200";
    /**
     * 重新登录界面
     */
    public static final String OVER_TIME_CODE = "301";
    /**
     * 成功代码
     */
    public static final String MESSAGE = "message";
    /**
     * 状态
     */
    public static final String STATUS = "status";
    /**
     * 数据
     */
    public static final String DATA = "data";
    /**
     * 数据List
     */
    public static final String LIST_DATA = "list";
    protected BaseCoreActivity currentActivity = null;
    /**
     * fragment管理器
     */
    protected FragmentManager fManager = getSupportFragmentManager();
    /**
     * 线程池
     */
    protected ExecutorService mFixedThreadPoolExecutor = CoreApplication.getFixedThreadPoolExecutor();
    /**
     * 升级的apk文件
     */
    protected File mUpdateFile = null;
    /**
     * 网络请求Service
     */
    protected CustomHandler mHandler = null;
    protected boolean isCheckPermission = false;

    protected static class CustomHandler extends Handler {
        private WeakReference<BaseCoreActivity> mActivityReference;

        public CustomHandler(BaseCoreActivity mActivity) {
            mActivityReference = new WeakReference<BaseCoreActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseCoreActivity mActivity = mActivityReference.get();
            if (mActivity != null) {
                mActivity.onRefresh(msg);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new CustomHandler(currentActivity = this);
        ScreenManager.getInstance().pushActivity(currentActivity);
    }

    /**
     * 刷新数据
     *
     * @param msg
     */
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case NETWORK_UNVISIBLE:
                CToast.showLong(currentActivity, R.string.xs_network_unvisible_str);
                break;
        }
    }

    /**
     * 对PagerSlidingmPagerTabStriptrip的各项属性进行赋值。
     */
    protected void setPagerTabStripValue(PagerSlidingTabStrip mPagerTabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = currentActivity.getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        mPagerTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        mPagerTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, currentActivity.getResources().getDimensionPixelOffset(R.dimen.xs_tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色
        mPagerTabStrip.setIndicatorColor(currentActivity.getResources().getColor(R.color.xs_tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mPagerTabStrip.setSelectedTextColor(currentActivity.getResources().getColor(R.color.xs_tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        CustomerDialog.dismissProgress();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        ScreenManager.getInstance().popActivity(currentActivity);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /**
     * 退出应用
     */
    protected void exitSystem() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(currentActivity, R.string.xs_one_more_click_exit_str, Toast.LENGTH_SHORT).show();
        } else {
            compeletlyExitSystem();
        }
    }

    /**
     * 退出应用 true 完全退出 false 结束当前页面
     *
     * @param isExitSystem
     */
    protected void exitSystem(boolean isExitSystem) {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(currentActivity, R.string.xs_one_more_click_exit_str, Toast.LENGTH_SHORT).show();
        } else {
            if (isExitSystem) {
                compeletlyExitSystem();
            } else {
                this.finish();
            }
        }
    }

    /**
     * 完全退出应用
     */
    protected void compeletlyExitSystem() {
        // 退出
        BitmapHelp.clearDiskCache();
        ScreenManager.getInstance().popAllActivityExceptOne(null);
        android.os.Process.killProcess(android.os.Process.myPid());
        CoreApplication.getFixedThreadPoolExecutor().shutdownNow();
        System.exit(0);
    }

    protected ServiceConnection mVPNConnection = null;
    protected boolean isStartedVPN = false;
    protected String mStartUUID = "";


    /**
     * 创建文件夹
     *
     * @param filePathArray
     */
    protected void initFileSystem(final String[] filePathArray) {
        FileUtils.initFile(filePathArray);
    }


    /**
     * 获得权限后的操作
     */
    protected void afterGrantedPermissions() {

    }
}
