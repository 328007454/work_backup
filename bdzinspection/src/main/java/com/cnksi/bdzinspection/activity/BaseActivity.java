package com.cnksi.bdzinspection.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.Users;
import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import com.cnksi.core.common.ScreenManager;


import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static android.os.Build.VERSION.SDK_INT;
import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;

@SuppressLint("HandlerLeak")
public class BaseActivity extends BaseCoreActivity {
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";
    public static final int INIT_SPEECH = -0x101001;
    public static final int ACTION_RECORDVIDEO = 0x500;
    public static final int PERMISSION_WINDOW = ACTION_RECORDVIDEO + 1;
    /**
     * 线程池
     */
    protected ExecutorService mFixedThreadPoolExecutor = XunshiApplication.getFixedThreadPoolExecutor();
    /**
     * 利用静态变量多个实例共享的特性 控制TaskRemind更新任务逻辑
     **/
    public static boolean isNeedUpdateTaskStatus = false;

    /**
     * 退出时间
     */
    protected static long currentBackPressedTime = 0;
    /**
     * 退出间隔
     */
    protected static final int BACK_PRESSED_INTERVAL = 2000;

    /**
     * 是否是特殊巡检
     */
    protected boolean isParticularInspection = false;
    /**
     * 当前巡检类型
     */
    protected String currentInspectionType = InspectionType.full.name();
    /**
     * 当前巡检类型Name
     */
    protected String currentInspectionTypeName = InspectionType.full.toString();

    /**
     * 当前的Title
     */
    protected String currentTitle = "";
    /**
     * 当前设备的id
     */
    protected String currentDeviceId = "";
    /**
     * 当前设备的Name
     */
    protected String currentDeviceName = "";
    /**
     * 当前间隔id
     */
    protected String currentSpacingId = "";
    /**
     * 当前间隔名称
     */
    protected String currentSpacingName = "";
    /**
     * 当前设备部件id
     */
    protected String currentDevicePartId = "";
    /**
     * 当前设备部件名称
     */
    protected String currentDevicePartName = "";

    /**
     * 当前变电站编号
     */
    protected String currentBdzId = "";
    /**
     * 当前变电站名称
     */
    protected String currentBdzName = "";

    /**
     * 当前巡视标准
     */
    protected String currentStandardId = "";
    /**
     * 当前的任务id
     */
    protected String currentTaskId = "";
    /**
     * 当前的报告id
     */
    protected String currentReportId = "";
    /**
     * 是否是从巡检任务提醒界面跳转过去的
     */
    protected boolean isFromTaskRemind = false;

    /**
     * 所属部门名字
     */
    protected String currentDepartmentName;
    /**
     * 自定义键盘 输入法
     */
    protected KeyBoardUtil mKeyBoardUtil = null;

    protected Dialog tipsDialog = null;
    /**
     * 振动器
     */
    protected Vibrator mVibrator;
    /**
     * 键盘布局
     */
    protected View mKeyBoardContainerView = null;
    private int seekBarHeight = 0;
    /**
     * 键盘View
     */
    protected KeyboardView mKeyBoardView = null;
    protected WindowManager mWindowManager = null;
    protected WindowManager.LayoutParams mWindowLayoutParams = null;

    public String currentAcounts;

    protected BaseActivity currentActivity;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mWindowLayoutParams = getWindowManagerParams();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = getIntent();
        if (null != intent) {
            // 获取从数据检测传过来的登陆用户
            String userName = intent.getStringExtra(Config.CURRENT_LOGIN_USER);
            String userAccount = intent.getStringExtra(Config.CURRENT_LOGIN_ACCOUNT);
            String bdzId = intent.getStringExtra(Config.LASTTIEM_CHOOSE_BDZNAME);
            PreferencesUtils.put( Config.LASTTIEM_CHOOSE_BDZNAME, bdzId);
            PreferencesUtils.put( Config.CURRENT_LOGIN_USER, userName);
            PreferencesUtils.put( Config.CURRENT_LOGIN_ACCOUNT,
                    userAccount);
        }
    }

    @Override
    public void getRootDataBinding() {

    }

    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    public WindowManager.LayoutParams getWindowManagerParams() {
        return new WindowManager.LayoutParams();
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
     * 得到Intent传过来的值
     */
    protected void getIntentValue() {
        isParticularInspection = getIntent().getBooleanExtra(Config.IS_PARTICULAR_INSPECTION, false);
        currentTitle = getIntent().getStringExtra(Config.TITLE_NAME);
        currentDeviceId = getIntent().getStringExtra(Config.CURRENT_DEVICE_ID);
        currentDeviceName = getIntent().getStringExtra(Config.CURRENT_DEVICE_NAME);
        currentDevicePartId = getIntent().getStringExtra(Config.CURRENT_DEVICE_PART_ID);
        currentDevicePartName = getIntent().getStringExtra(Config.CURRENT_DEVICE_PART_NAME);
        currentSpacingId = getIntent().getStringExtra(Config.CURRENT_SPACING_ID);
        currentSpacingName = getIntent().getStringExtra(Config.CURRENT_SPACING_NAME);
        currentStandardId = getIntent().getStringExtra(Config.CURRENT_STANDARD_ID); // 巡视标准ID
        // 是否是从巡检任务提醒界面跳转过去的
        isFromTaskRemind = getIntent().getBooleanExtra(Config.IS_FROM_TASK_REMIND, false);

        currentBdzId = PreferencesUtils.get( Config.CURRENT_BDZ_ID, "");
        currentBdzName = PreferencesUtils.get( Config.CURRENT_BDZ_NAME, "");
        currentInspectionType = PreferencesUtils.get( Config.CURRENT_INSPECTION_TYPE, "");
        currentInspectionTypeName = PreferencesUtils.get( Config.CURRENT_INSPECTION_TYPE_NAME,
                "");
        currentTaskId = PreferencesUtils.get( Config.CURRENT_TASK_ID, "");
        currentReportId = PreferencesUtils.get( Config.CURRENT_REPORT_ID, "");
        currentAcounts = PreferencesUtils.get( Config.CURRENT_LOGIN_ACCOUNT, "");
    }

    /**
     * 刷新数据
     */
    @Override
    protected void onRefresh(android.os.Message msg) {
    }

    /**
     * 显示大图片
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete) {
        Intent intent = new Intent(currentActivity, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.startActivityForResult(intent, CANCEL_RESULT_LOAD_IMAGE);
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

    /**
     * 显示大图片
     */
    public void showImageDetails(Activity context, String mImageUrl, boolean isShowDelete) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        mImageUrlList.add(mImageUrl);
        showImageDetails(context, 0, mImageUrlList, isShowDelete);
    }

    public void showImageDetails(Activity context, String fileFolder, List<String> filesName, boolean isShowDelete) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        for (String name : filesName) {
            mImageUrlList.add(fileFolder + name);
        }
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

    // 设置应用程序的字体大小不会随 系统字体大小而变化
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        // config.setToDefaults();
        config.fontScale = 0.85f;
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    protected void takeVideo(String videopath) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);// action is
        // capture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(videopath)));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, ACTION_RECORDVIDEO);
    }

    /**
     * 裁剪图片
     */
    protected void cropImageUri(Uri uri, int outputX, int outputY, int requestCode, String currentImageName) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 10);
        intent.putExtra("aspectY", 9);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        CustomerDialog.dismissProgress();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(0);
        }
        DialogUtils.setDialogNull();
        PlaySound.setPlayNull();
        super.onDestroy();
    }

    /**
     * 退出应用
     */
    @Override
    protected void exitSystem() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(currentActivity, R.string.xs_one_more_click_exit_str, Toast.LENGTH_SHORT).show();
        } else {
            compeletlyExitSystem();
        }
    }

    /**
     * 完全退出应用
     */
    @Override
    protected void compeletlyExitSystem() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(null);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    protected void showTipsDialog(ViewGroup mRootContainer, Intent intent) {

        if (currentInspectionType.contains("special") || currentActivity.equals(InspectionType.routine.name()))
            showTipsDialog(mRootContainer, intent, -1, R.string.xs_dialog_tips_content_special, false);
        else {
            showTipsDialog(mRootContainer, intent, -1, R.string.xs_dialog_tips_content_str, false);
        }
    }

    protected void showTipsDialog(ViewGroup mRootContainer, Intent intent, int requestCode, int dialogContentResId,
                                  boolean isFinishInspection) {
        showTipsDialog(mRootContainer, intent, requestCode, getText(dialogContentResId), isFinishInspection);
    }

    /**
     * 完成巡检提示框
     */
    XsDialogTipsBinding tipsBinding;

    protected void showTipsDialog(ViewGroup mRootContainer, Intent intent, int requestCode, CharSequence text,
                                  boolean isFinishInspection) {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        tipsBinding.tvDialogContent.setText(text);
        tipsDialog.show();
        tipsBinding.btnSure.setOnClickListener(view -> {
            if (isFinishInspection) {
                updateReportStatus();
            }
            startActivityForResult(intent, requestCode);
            tipsDialog.dismiss();
        });
        tipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });
    }

    /**
     * 完成报告
     */
    protected void updateReportStatus() {
        try {
            Report report = new Report(currentReportId);
            XunshiApplication.getDbUtils().update(report, Report.ENDTIME);
            Task mTask = new Task(currentTaskId);
            XunshiApplication.getDbUtils().update(mTask, Task.STATUS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示光标
     */
    protected void showCursor(EditText mEditText) {
        if (SDK_INT <= 10) {
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(false);
                setShowSoftInputOnFocus.invoke(mEditText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建keyBoardView
     */
    protected void createKeyBoardView(ViewGroup root) {

        mKeyBoardContainerView = LayoutInflater.from(currentActivity).inflate(R.layout.xs_keyboard_layout, root, false);
        mKeyBoardView = (KeyboardView) mKeyBoardContainerView.findViewById(R.id.keyboard_view);
        final LinearLayout mSeekBarContainer = (LinearLayout) mKeyBoardContainerView
                .findViewById(R.id.rl_seekbar_container);
        // 动态获取seekBar的高度
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mSeekBarContainer.measure(w, h);
        seekBarHeight = mSeekBarContainer.getMeasuredHeight();
        seekBarHeight = 0;
        mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // wmParams.format=1;
        mWindowLayoutParams.flags |= 8;
        // 调整悬浮窗口至底部
        mWindowLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 以屏幕左上角为原点，设置x、y初始值
        mWindowLayoutParams.x = 0;
        mWindowLayoutParams.y = 0;
        // 设置悬浮窗口长宽数据
        mWindowLayoutParams.width = ScreenUtils.getScreenWidth(currentActivity);
        // 动态获取键盘的高度 键盘的高度加上seekBar的高度
        int height = getResources().getDimensionPixelSize(R.dimen.xs_key_height) * 4
                + getResources().getDimensionPixelSize(R.dimen.xs_key_vertical_gap) * 5 + seekBarHeight;
        mWindowLayoutParams.height = height;
//        mWindowManager.addView(mKeyBoardContainerView, mWindowLayoutParams);

        mKeyBoardUtil = new KeyBoardUtil(mKeyBoardContainerView, currentActivity, null, mWindowManager, mWindowLayoutParams);
    }

    public void ExitThisAndGoLauncher() {
        this.finish();
    }

    public boolean isParticularInspection() {
        if (isParticularInspection) {
            return true;
        }
        if (currentInspectionType == null) {
            return false;
        } else if (currentInspectionType.contains("special")) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isParticularInspection(String deviceWay) {

        if (TextUtils.isEmpty(deviceWay)) {
            return false;
        } else if ("by_device_bigtype".equalsIgnoreCase(deviceWay)) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isRoutineNotCopy() {
        return false;
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
        int textSize = AutoUtils.getPercentHeightSizeBigger((int) currentActivity.getResources().getDimension(R.dimen.xs_tab_strip_text_size_px));
        mPagerTabStrip.setTextSize(textSize);
//        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, currentActivity.getResources().getDimensionPixelOffset(R.dimen.tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色
        mPagerTabStrip.setIndicatorColor(currentActivity.getResources().getColor(com.cnksi.xscore.R.color.xs_tab_strip_text_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mPagerTabStrip.setSelectedTextColor(currentActivity.getResources().getColor(com.cnksi.xscore.R.color.xs_tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }

    protected void changedStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    /**
     * 检查应用是否设置悬浮窗权限
     */
    @TargetApi(23)
    public void setWindowOverLayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                ToastUtils.showMessage( "没有悬浮窗权限");
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, PERMISSION_WINDOW);
            }
        }
    }


    private Users one, two;

    public Users getUsers() {
        if (one != null) {
            return one;
        }
        if (two != null) {
            return two;
        } else {
            String[] account = PreferencesUtils.get(Users.ACCOUNT, "").split(Config.COMMA_SEPARATOR);
            for (int i = 0; i < account.length; i++) {
                Users _t = null;
                if (!TextUtils.isEmpty(account[i])) {
                    {
                        _t = UserService.getInstance().findUserByAccount(account[i]);
                    }
                }
                if (one != null) {
                    two = _t;
                } else {
                    one = _t;
                }
            }
        }
        return one;
    }

    /**
     * 工器具零时状态保存
     */
    private Map<String, Map<Integer, Boolean>> gqjcheck = null;


    /**
     * @return the gqjcheck
     */
    public Map<String, Map<Integer, Boolean>> getGqjcheck() {
        if (gqjcheck == null) {
            gqjcheck = new HashMap<>();
        }
        return gqjcheck;
    }


}