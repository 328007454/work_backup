package com.cnksi.xscore.xsutils;

import android.content.Context;
import android.widget.Toast;

import com.cnksi.xscore.xsapplication.CoreApplication;

/**
 * Toast统一管理类
 */
public class CToast {

    public static boolean isShow = true;
    public static Context context;
    private CToast() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public  static  void init(Context context){
        CToast.context = context;
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(CharSequence message) {
        showShort(context, message);
    }
    public static void showLong(CharSequence message) {
        showLong(context, message);
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}