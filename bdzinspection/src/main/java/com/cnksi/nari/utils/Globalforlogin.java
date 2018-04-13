package com.cnksi.nari.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;


/**
 * 登录信息 源代码来源于Nari
 */
public class Globalforlogin {
    private static Globalforlogin gInstance_;
    public static String mArkIp;
    public static String mArkOrgan = "default";
    public static String mArkPort;
    public float density;
    public int densityDpi;
    private String deviceMac_;
    public String empVersion = "1.0.0";
    public String imei_ = "";
    public String imsi_ = "";
    public String osVersion_ = "";
    public String phoneModel_;
    public int screenAllHeight_;
    public int screenAllWidth_;

    public static String getDeviceMac2(Context context) {
        if (context == null) {
            return null;
        }
        String macAddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        return macAddress == null ? "" : macAddress;
    }

    public static String getImeiString(Context context) {
        String deviceId = ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
        return (deviceId == null || deviceId.length() == 0) ? DecodeUtils.md5EncryptToString(getDeviceMac2(context)).substring(0, 15) : deviceId;
    }

    public static Globalforlogin getInstance() {
        if (gInstance_ == null) {
            gInstance_ = new Globalforlogin();
        }
        return gInstance_;
    }

    public static String getNetwork(Context context) {
        int networkType = ((TelephonyManager) context.getSystemService("phone")).getNetworkType();
        String str = "UNKNOWN";
        if (networkType == 4) {
            str = "2G";
        }
        if (networkType == 2) {
            str = "2G";
        }
        if (networkType == 5) {
            str = "3G";
        }
        if (networkType == 6) {
            str = "3G";
        }
        if (networkType == 1) {
            str = "2G";
        }
        if (networkType == 8) {
            str = "3G";
        }
        if (networkType == 10) {
            str = "3G";
        }
        if (networkType == 9) {
            str = "3G";
        }
        if (networkType == 3) {
            str = "3G";
        }
        if (networkType == 13) {
            str = "4G";
        }
        return networkType == 0 ? "UNKNOWN" : str;
    }

    public static boolean isNetworkAvailable(Context context, boolean z, int i) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
        NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(0);
        if (z) {
            if (i == 0) {
                if (networkInfo2 != null && networkInfo2.isAvailable() && networkInfo2.isConnected()) {
                    return true;
                }
            } else if (1 == i && networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.isConnected();
            }
        } else if ((networkInfo == null || !networkInfo.isAvailable()) && (networkInfo2 == null || !networkInfo2.isAvailable())) {
            return false;
        } else {
            if (networkInfo.isConnected() || networkInfo2.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isSystemRoot() {

        return false;
    }

    public static void setArkIp(String str) {
        mArkIp = str;
    }

    public static void setArkOrgan(String str) {
        mArkOrgan = str;
    }

    public static void setArkPort(String str) {
        mArkPort = str;
    }

    public String getClientVersion(Context context) {
        String str = "2.7.0";
        return str;
    }

    public String getCurrentNetwork(Context context) {
        return "4G";  }

    public String getDeviceDPI() {
        return "xhdpi";
    }

    public String getDeviceMac(Context context) {
        this.deviceMac_ = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        if (this.deviceMac_ == null) {
            this.deviceMac_ = "";
        }
        return this.deviceMac_;
    }

    public void getDisplayInfo(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.screenAllWidth_ = displayMetrics.widthPixels;
        this.screenAllHeight_ = displayMetrics.heightPixels;
        this.densityDpi = displayMetrics.densityDpi;
        this.density = displayMetrics.density;
    }

    public String getESN(Context context) {
        String str = getInstance().imei_;
        if (this.imei_ != null && this.imei_.length() != 0) {
            return str;
        }
        this.imei_ = getImeiString(context);
        return this.imei_;
    }

    public String getimsi(Context context) {
        this.imsi_ = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
        if (this.imsi_ == null) {
            this.imsi_ = "";
        }
        return this.imsi_;
    }

    public String getosVersion() {
        this.osVersion_ = VERSION.RELEASE;
        return this.osVersion_;
    }

    public String getphoneModel() {
        this.phoneModel_ = Build.MODEL.toLowerCase();
        return this.phoneModel_;
    }
}