package com.cnksi.sjjc.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.KeyBoardUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.core.view.PagerSlidingTabStrip;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.DbManager;
import org.xutils.ImageManager;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

/**
 * @version 1.0
 * @auth luoxy
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
    public static final int INIT_SPEECH = -0x101001;

    /**
     * 图片显示工具类
     */
    protected ImageManager mBitmapUtils = x.image();
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
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = 0x10;
    /**
     * Activity本身
     */
    BaseActivity _this;
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
     * Db验收实例
     */
    DbManager dbYShou;
    /**
     * Db实例
     */
    DbManager db;
    /**
     * 是否显示文件名
     */

    protected boolean isShowPicName;
    /**
     * 默认发音人
     */
    protected String voicer = "xiaoyan";

    protected static HashMap<String, Object> dataMap = new HashMap<String, Object>();
    protected ExecutorService mFixedThreadPoolExecutor = mExcutorService;

    @ViewInject(R.id.btn_back)
    protected ImageView btnBack;

    @ViewInject(R.id.btn_right)
    protected ImageView btnRight;

    @ViewInject(R.id.tv_right)
    protected TextView tvRight;

    @ViewInject(R.id.tv_title)
    protected TextView tvTitle;

    @ViewInject(R.id.root_container)
    protected LinearLayout rootContainer;

    @ViewInject(R.id.shadom_rela)
    protected RelativeLayout layoutRelat;

    protected SpeechSynthesizer mTts;
    public Vibrator mVibrator;

    /**
     * 开启svg格式图片兼容
     * */
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;
        mVibrator = (Vibrator) _this.getSystemService(Context.VIBRATOR_SERVICE);
        if (isDefaultTitle)
            setContentView(R.layout.include_title);
        if (PreferencesUtils.getBoolean(_this, Config.PERMISSION_STASTUS, false)) {
            dbYShou = CustomApplication.getYanShouDbManager();
            db = CustomApplication.getDbManager();
        }
        x.view().inject(this);
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        x.view().inject(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        x.view().inject(this);
    }


    /**
     * 设置container内容
     *
     * @param layoutResID
     */
    public void setChildView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, rootContainer, false);
        setChildView(view);
    }

    /**
     * 设置container内容
     *
     * @param view
     */
    public void setChildView(View view) {
        rootContainer.addView(view);
        x.view().inject(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtils.closeKeybord(_this);
                onBackPressed();
            }
        });
    }

    /**
     * 设置container内容
     *
     * @param view
     * @param params
     */
    public void setChildView(View view, ViewGroup.LayoutParams params) {
        RelayoutUtil.reLayoutViewHierarchy(view);
        RelayoutUtil.relayoutLayoutParams(params);
        rootContainer.addView(view, params);
        x.view().inject(this);
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

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, ArrayList<String> mImageUrlList,
                                 boolean isShowDelete, boolean isDeleteFile) {
        Intent intent = new Intent(_this, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        intent.putExtra(Config.CANCEL_IMAGEURL_LIST, isShowPicName);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_DELETE_FILE, isDeleteFile);
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.startActivityForResult(intent, CANCEL_RESULT_LOAD_IMAGE);
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

    /**
     * 加强版Toast 避免重复提示
     */

    public void Toast(String msg) {
        CToast.showShort(_this, msg);
    }


    public void getIntentValue() {
        currentBdzId = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_ID, "");
        currentBdzName = PreferencesUtils.getString(_this, Config.CURRENT_BDZ_NAME, "");
        currentReportId = PreferencesUtils.getString(_this, Config.CURRENT_REPORT_ID, "");
        currentTaskId = PreferencesUtils.getString(_this, Config.CURRENT_TASK_ID, "");
        currentInspectionType = PreferencesUtils.getString(_this, Config.CURRENT_INSPECTION_TYPE, "");
        currentInspectionName = PreferencesUtils.getString(_this, Config.CURRENT_INSPECTION_NAME, "");
    }

    public boolean isEmpty(TextView tv) {
        if (tv == null) {
            return true;
        } else {
            return TextUtils.isEmpty(tv.getText().toString().trim());
        }
    }

    public String getText(TextView tv) {
        if (tv == null)
            return "";
        else return tv.getText().toString();

    }

    public void setTitleText(CharSequence str) {
        tvTitle.setText(str);
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
            Toast.makeText(_this, R.string.one_more_click_exit_str, Toast.LENGTH_SHORT).show();
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

    /**
     * 初始化语音引擎
     */
    protected void initSpeech(Context context) {
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(context, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    // 清空参数
                    mTts.setParameter(SpeechConstant.PARAMS, null);
                    // 设置本地合成
                    mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
                    // 设置发音人资源路径
                    mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
                    // 设置发音人 voicer为空默认通过语音+界面指定发音人。
                    mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
                    // 设置语速
                    mTts.setParameter(SpeechConstant.SPEED, "50");
                    // 设置音调
                    mTts.setParameter(SpeechConstant.PITCH, "50");
                    // 设置音量
                    mTts.setParameter(SpeechConstant.VOLUME, "100");
                    // 设置播放器音频流类型
                    mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
                    mHandler.sendEmptyMessage(INIT_SPEECH);
                }
            }
        });
    }

    // 获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicer + ".jet"));
        return tempBuffer.toString();
    }

    @Override
    protected void compeletlyExitSystem() {
        // 退出
        ScreenManager.getScreenManager().popAllActivityExceptOne(null);
        android.os.Process.killProcess(android.os.Process.myPid());
        // PreferencesUtils.clear(_this);
        System.exit(0);
    }

    /**
     * 停止说话
     */
    protected void stopSpeaking() {
        if (mTts != null) {
            mTts.stopSpeaking();
        }
    }

    /**
     * 可以标记图片
     */

    public void drawCircle(String pictureName, String pictureContent) {
        Intent intent = new Intent(_this, DrawCircleImageActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_NAME, pictureName);
        intent.putExtra(Config.PICTURE_CONTENT, pictureContent);
        startActivityForResult(intent, LOAD_DATA);
    }

    /**
     * 对PagerSlidingmPagerTabStriptrip的各项属性进行赋值。
     */
    protected void setPagerTabStripValue(PagerSlidingTabStrip mPagerTabStrip) {

        // 当前屏幕密度
        DisplayMetrics mDisplayMetrics = _this.getResources().getDisplayMetrics();
        // 设置Tab的分割线是透明的
        mPagerTabStrip.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mPagerTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
        // 设置Tab Indicator的高度
        mPagerTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
        // 设置Tab标题文字的大小
        mPagerTabStrip.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, _this.getResources().getDimensionPixelOffset(R.dimen.tab_strip_text_size), mDisplayMetrics));
        // 设置Tab Indicator的颜色  _this.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setIndicatorColor(ContextCompat.getColor(_this, R.color.tab_strip_background_color));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)_this.getResources().getColor(R.color.tab_strip_text_color)
        mPagerTabStrip.setSelectedTextColor(ContextCompat.getColor(_this, R.color.tab_strip_text_color));
        // 取消点击Tab时的背景色
        mPagerTabStrip.setTabBackground(0);
    }


    public void translateAnimRun(final View view, float... values) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translate", values).setDuration(2800);
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                view.setTranslationY(cVal);
            }
        });

    }

    /**
     * 隐藏输入法键盘
     */
    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    }

    /**
     * 显示输入法键盘
     */
    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }
}
