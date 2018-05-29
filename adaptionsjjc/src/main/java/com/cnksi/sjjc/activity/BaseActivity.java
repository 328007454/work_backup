package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.databinding.IncludeTitleBinding;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.NetWorkUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.AppVersion;
import com.cnksi.sjjc.databinding.DialogCopyTipsBinding;
import com.cnksi.sjjc.util.AppUtils;
import com.cnksi.sjjc.util.CoreConfig;
import com.cnksi.sjjc.util.FunctionUtils;
import com.cnksi.sjjc.util.util.UpdateUtils;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cnksi.sjjc.activity.LoginActivity.SHOW_UPDATE_LOG_DIALOG;

/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/23
 */
public abstract class BaseActivity extends BaseCoreActivity {
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
    public static boolean isNeedUpdateTaskState = false;
    public boolean isDefaultTitle = true;
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
     * 保存数据
     */
    public static final int SAVE_DATA = 0x3;

    /**
     * 当前变电站编号
     */
    protected String currentBdzId = "50";
    /**
     * 当前变电站名称
     */
    protected String currentBdzName = "杨桥变电站";
    /**
     * 当前的任务id
     */
    protected String currentTaskId = "";

    /**
     * 当前巡检类型
     */
    protected String currentInspectionType = "";
    /**
     * 当前巡检类型名称
     */
    protected String currentInspectionName = "";
    /**
     * 当前的报告id
     */
    protected String currentReportId = "";

    /**
     * 是否显示文件名
     */

    protected boolean isShowPicName;


    protected static HashMap<String, Object> dataMap = new HashMap<>();
    public Vibrator mVibrator;

    /**
     * 开启svg格式图片兼容
     * */
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected IncludeTitleBinding mTitleBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (!BuildConfig.DEBUG) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        mVibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public void getRootDataBinding() {
        if (isDefaultTitle) {
            mTitleBinding = DataBindingUtil.setContentView(mActivity, R.layout.include_title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }


    /**
     * 设置container内容
     *
     * @param layoutResID
     */
    public void setChildView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, mTitleBinding.rootContainer, false);
        setChildView(view);
    }

    /**
     * 设置container内容
     *
     * @param
     */
    public void setChildView(View view) {
        mTitleBinding.rootContainer.addView(view);
        mTitleBinding.btnBack.setOnClickListener(v -> {
            KeyBoardUtils.closeKeybord(mActivity);
            BaseActivity.this.onBackPressed();
        });

    }

    /**
     * 设置container内容
     *
     * @param view
     * @param params
     */
    public void setChildView(View view, ViewGroup.LayoutParams params) {
        mTitleBinding.rootContainer.addView(view, params);
    }


    /**
     * 适配布局控件
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) {
            return view;
        }

        return super.onCreateView(name, context, attrs);
    }

    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete, boolean isDeleteFile) {
        ImageDetailsActivity.with(context).setPosition(position).setImageUrlList(mImageUrlList).setDeleteFile(isDeleteFile).setShowDelete(isShowDelete).start();

    }

    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete) {
        showImageDetails(context, position, mImageUrlList, isShowDelete, true);
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Activity context, ArrayList<String> mImageUrlList) {
        showImageDetails(context, 0, mImageUrlList, false);
    }

    public void showImageDetails(Activity context, ArrayList<String> mImageUrlList, boolean isShowDelete) {
        showImageDetails(context, 0, mImageUrlList, isShowDelete);
    }

    public void showImageDetails(Activity context, ArrayList<String> mImageUrlList, boolean isShowDelete, boolean isDeleteFile) {
        showImageDetails(context, 0, mImageUrlList, isShowDelete, isDeleteFile);
    }

    /**
     * 显示大图片,不显示文件名
     */
    public void showImageDetails(Activity context, String mImageUrl, boolean isShowDelete, boolean isShowName) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        this.isShowPicName = isShowName;
        mImageUrlList.add(mImageUrl);
        showImageDetails(context, 0, mImageUrlList, isShowDelete);
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Activity context, String mImageUrl) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        mImageUrlList.add(mImageUrl);
        showImageDetails(context, 0, mImageUrlList, false);
    }

    public void getIntentValue() {
        currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        currentBdzName = PreferencesUtils.get(Config.CURRENT_BDZ_NAME, "");
        currentReportId = PreferencesUtils.get(Config.CURRENT_REPORT_ID, "");
        currentTaskId = PreferencesUtils.get(Config.CURRENT_TASK_ID, "");
        currentInspectionType = PreferencesUtils.get(Config.CURRENT_INSPECTION_TYPE, "");
        currentInspectionName = PreferencesUtils.get(Config.CURRENT_INSPECTION_NAME, "");
    }

    public boolean isEmpty(TextView tv) {
        if (tv == null) {
            return true;
        } else {
            return TextUtils.isEmpty(tv.getText().toString().trim());
        }
    }

    public String getText(TextView tv) {
        if (tv == null) {
            return "";
        } else {
            return tv.getText().toString();
        }

    }

    public void setTitleText(CharSequence str) {
        mTitleBinding.tvTitle.setText(str);
    }

    public void setViewVisible(View v, int Mode) {
        if (v != null) {
            if (v.getVisibility() != Mode) {
                v.setVisibility(Mode);
            }
        }
    }

    /**
     * 退出应用
     */
    @Override
    protected void exitSystem() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(mActivity, R.string.one_more_click_exit_str, Toast.LENGTH_SHORT).show();
        } else {
            compeletlyExitSystem();
        }
    }


    // 设置应用程序的字体大小不会随 系统字体大小而变化
    @Override
    @SuppressWarnings("deprecation")
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        // config.setToDefaults();
        config.fontScale = 0.85f;

        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    @Override
    protected void compeletlyExitSystem() {
        // 退出
        ScreenManager.getScreenManager().popAllActivityExceptOne(null);
        android.os.Process.killProcess(android.os.Process.myPid());
        // PreferencesUtils.clear(mActivity);
        System.exit(0);
    }

    /**
     * 可以标记图片
     */

    public void drawCircle(String pictureName, String pictureContent) {
        DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(pictureName).start();
    }

    /**
     * 对PagerSlidingmPagerTabStriptrip的各项属性进行赋值。
     */
    protected void setPagerTabStripValue(PagerSlidingTabStrip mPagerTabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = mActivity.getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        mPagerTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        mPagerTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelOffset(R.dimen.tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色  mActivity.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setIndicatorColor(ContextCompat.getColor(mActivity, R.color.tab_strip_background_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)mActivity.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setSelectedTextColor(ContextCompat.getColor(mActivity, R.color.tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }


    protected void changedStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private boolean isPms = false;

    /**
     * 更新日志
     */
    protected String updateContent;

    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, boolean isPms, String updateContent) {
        this.updateContent = updateContent;
        this.isPms = isPms;
        checkUpdateVersion(downloadFolder, downloadFileName, FunctionUtils.getMetaValue(mActivity, CoreConfig.PROGRAM_APP_CODE));
    }

    /**
     * 检测更新
     */
    File localUpdateFile;

    @Override
    protected void checkUpdateVersion(final String downloadFolder, String downloadFileName, final String appCode) {
        ExecutorManager.executeTaskSerially(() -> {
            if (localUpdateFile == null) {
                localUpdateFile = UpdateUtils.hasUpdateApk(mActivity, downloadFolder, isPms);
                if (localUpdateFile != null) {
                    com.cnksi.sjjc.view.CustomerDialog.dismissProgress();
                    mHandler.sendEmptyMessage(CoreConfig.INSTALL_APP_CODE);
                } else if (null == localUpdateFile) {
                    com.cnksi.sjjc.view.CustomerDialog.dismissProgress();
                    return;
                } else {
                    if (NetWorkUtils.isNetworkConnected(mActivity)) {
                        // 上传用户信息 检查升级
                        Map<String, String> params = UpdateUtils.getDeviceInforMapParams(mActivity, appCode);
                    }
                }
            } else {
                com.cnksi.sjjc.view.CustomerDialog.dismissProgress();
                mHandler.sendEmptyMessage(CoreConfig.INSTALL_APP_CODE);
            }
        });
    }


    /**
     * 刷新数据
     *
     * @param msg
     */
    @Override
    protected void onRefresh(android.os.Message msg) {
        switch (msg.what) {
            case CoreConfig.INSTALL_APP_CODE:
                // TODO:显示安装对话框
                UpdateUtils.showInstallNewApkDialog(mActivity, localUpdateFile, isPms, updateContent);
                break;
            case SHOW_UPDATE_LOG_DIALOG:
                if (null != updateLogDialog && null != remoteSjjcAppVersion) {
                    layout.tvDialogTitle.setText("本次更新内容");
                    layout.clickLinearlayout.setVisibility(View.GONE);
                    layout.tvCopy.setVisibility(View.GONE);
                    layout.tvTips.setText(Html.fromHtml(TextUtils.isEmpty(remoteSjjcAppVersion.description) ? "欢迎使用！" : remoteSjjcAppVersion.description));
                    updateLogDialog.show();
                }
                break;
            default:
        }
    }

    private Dialog updateLogDialog;
    private AppVersion remoteSjjcAppVersion;
    private DialogCopyTipsBinding layout;

    /**
     * 检测升级
     */
    public void checkUpdate() {
        layout = DialogCopyTipsBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        updateLogDialog = DialogUtils.creatDialog(mActivity, layout.getRoot(), dialogWidth, dialogHeight);
        PackageInfo info = AppUtils.getLocalPackageInfo(getApplicationContext());
        int version = info.versionCode;
        try {
            remoteSjjcAppVersion = CustomApplication.getInstance().getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (remoteSjjcAppVersion != null) {
            com.cnksi.sjjc.view.CustomerDialog.showProgress(mActivity, "检测到需要升级，请等待");
            ExecutorManager.executeTaskSerially(() -> {
                try {
                    remoteSjjcAppVersion = CustomApplication.getInstance().getDbManager().selector(AppVersion.class).where(AppVersion.DLT, "!=", "1").expr(" and version_code > '" + version + "'").expr("and file_name like '%sjjc%'").orderBy(AppVersion.VERSIONCODE, true).findFirst();
                    String apkPath = "";
                    //下载APK文件夹
                    SqlInfo info1 = new SqlInfo("select short_name_pinyin from city");
                    try {
                        DbModel model = CustomApplication.getInstance().getDbManager().findDbModelFirst(info1);
                        if (model != null) {
                            apkPath = "admin/" + model.getString("short_name_pinyin") + "/apk";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != remoteSjjcAppVersion) {
                        checkUpdateVersion(Config.BDZ_INSPECTION_FOLDER + apkPath,
                                Config.PCODE, false, TextUtils.isEmpty(remoteSjjcAppVersion.description) ? "修复bug,优化流畅度" : remoteSjjcAppVersion.description);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtils.setDialogNull();
    }
}
