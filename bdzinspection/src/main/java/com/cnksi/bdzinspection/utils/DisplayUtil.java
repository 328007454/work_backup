package com.cnksi.bdzinspection.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 设备显示属性<br/>
 * 调用方式 {@link DisplayUtil}.{@link #getInstance()}.{@link #setStandWidth(float)}.{@link #setStandHeight(float)}.{@link #init(Context)}
 *
 * @version 1.0
 * @auth lyndon
 * @date 2016/2/16
 */
public class DisplayUtil {
    /**
     * 标准宽度
     */
    private float standWidth = 1080f;
    /**
     * 标准高度
     */
    private float standHeight = 1920f;
    /**
     * 宽度缩放比例
     */
    private float widthScale;
    /**
     * 高度缩放比例
     */
    private float heightScale;
    /**
     * 文字缩放比例
     */
    private float textScale;
    /**
     * 当前设备宽度
     */
    private int width;
    /**
     * 当前设备高度
     */
    private int height;
    /**
     * 设备终端密度
     */
    private float density;

    public static DisplayUtil instance;

    public static DisplayUtil getInstance() {
        if (null == instance) {
            instance = new DisplayUtil();
        }
        return instance;
    }

    /**
     * 初始化,获取当前手机的一些属性
     *
     * @param context
     * @return
     */
    public DisplayUtil init(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        density = metrics.density;

        widthScale = width / standWidth;
        heightScale = height / standHeight;
        textScale = widthScale;
        return this;
    }

    /**
     * 设置标准宽度
     *
     * @param standWidth
     * @return
     */
    public DisplayUtil setStandWidth(float standWidth) {
        this.standWidth = standWidth;
        return this;
    }

    /**
     * 设置标准高度
     *
     * @param standHeight
     * @return
     */
    public DisplayUtil setStandHeight(float standHeight) {
        this.standHeight = standHeight;
        return this;
    }

    /**
     * 获取宽度缩放比例 return
     */
    public float getWidthScale() {
        return widthScale;
    }

    /**
     * 获取高度缩放比例 return
     */
    public float getHeightScale() {
        return heightScale;
    }

    /**
     * 获取文字缩放比例 return
     */
    public float getTextScale() {
        return textScale;
    }

    /**
     * 获取当前宽度 return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取当前高度 return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取当前密度 return
     */
    public float getDensity() {
        return density;
    }

    /**
     * 获取缩放比列
     *
     * @return
     */
    public float getScale() {
        return Math.min(widthScale, heightScale);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public  int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = ScreenUtils.getScreenHeight(anchorView.getContext());
        final int screenWidth = ScreenUtils.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
//        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (false) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }
}
