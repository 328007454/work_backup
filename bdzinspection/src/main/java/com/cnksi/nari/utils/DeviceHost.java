package com.cnksi.nari.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.security.MessageDigest;


/**
 * 获取设备信息 源代码由NARI软件破译获得
 * 主要用于生成登录信息。
 */
public class DeviceHost {
    private String _deviceID;
    private String imei;
    Context context;

    public DeviceHost(Context c) {
        this._deviceID = null;
        this.context = c;
    }

    private char Hex2Chr(byte b) {
        return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}[b & 15];
    }


    private String getDeviceMac() {
        String macAddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        return macAddress == null ? "" : macAddress;
    }

    private String md5(String str) {
        String str2 = new String();
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes());
            for (byte b : digest) {
                str2 = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(str2)).append(Hex2Chr((byte) (b >>> 4))).toString())).append(Hex2Chr(b)).toString();
            }
        } catch (Exception e) {
        }
        return str2;
    }

    public String getDeviceID() {
        if (this._deviceID != null && !this._deviceID.isEmpty()) {
            return this._deviceID;
        }
        String deviceId = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }

        String str = deviceId == null ? "" : deviceId;
        try {
            Class cls = Class.forName("android.os.SystemProperties");
            deviceId = (String) cls.getMethod("get", String.class, String.class).invoke(cls, new Object[]{"ro.serialno", null});
        } catch (Exception e) {
            deviceId = null;
        }
        if (deviceId == null) {
            deviceId = "";
        }
        if (TextUtils.isEmpty(str) && TextUtils.isEmpty(deviceId)) {
            this._deviceID = "";
        } else {
            this._deviceID = DecodeUtils.md5EncryptToString(new StringBuilder(String.valueOf(str)).append(deviceId).toString()).toUpperCase();
        }
        return this._deviceID;
    }

    public String getESN() {
        return getIMEI();
    }

    public String getIMEI() {

        if (this.imei != null && !this.imei.isEmpty()) {
            return this.imei;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            this.imei = ((TelephonyManager) context.getSystemService(Context.POWER_SERVICE)).getDeviceId();
            if (this.imei == null || this.imei.length() == 0) {
                this.imei = md5(getDeviceMac()).substring(0, 15);
            }
        }
        return this.imei;
    }


}