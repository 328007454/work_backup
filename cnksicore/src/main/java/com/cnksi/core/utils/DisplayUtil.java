package com.cnksi.core.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
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
    private float standWidth = 720f;
    /**
     * 标准高度
     */
    private float standHeight = 1080f;
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
        if (null == instance)
            instance = new DisplayUtil();
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
     * 获取宽度缩放比例
     * return
     */
    public float getWidthScale() {
        return widthScale;
    }

    /**
     * 获取高度缩放比例
     * return
     */
    public float getHeightScale() {
        return heightScale;
    }

    /**
     * 获取文字缩放比例
     * return
     */
    public float getTextScale() {
        return textScale;
    }

    /**
     * 获取当前宽度
     * return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取当前高度
     * return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 获取当前密度
     * return
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

    public float spToPx(Context context, float sp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = sp * metrics.scaledDensity + 0.5f;
        return px;
    }
}
