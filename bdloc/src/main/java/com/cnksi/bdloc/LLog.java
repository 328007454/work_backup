package com.cnksi.bdloc;

import android.util.Log;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/4/21 14:31
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class LLog {

    public static String TAG = "location";
    public static boolean isLog = BuildConfig.DEBUG;

    public static void e(String msg) {
        if (isLog) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isLog) {
            Log.i(TAG, msg);
        }
    }
}
