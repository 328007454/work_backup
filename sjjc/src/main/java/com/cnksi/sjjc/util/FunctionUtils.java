package com.cnksi.sjjc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.cnksi.common.Config;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.ToastUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FunctionUtils {




    /**
     * 以系统时间返回照相图片文件名称
     *
     * @return yyyyMMddHHmmssSSSadfdfdsfwei.jpg
     */
    public static String getCurrentImageName() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.yyyyMMddHHmmssSSS, Locale.CHINA);
        String uuidStr = UUID.randomUUID().toString().replace(Config.DASH_SEPARATOR, "");
        if (uuidStr.length() > 8) {
            uuidStr = uuidStr.substring(0, 8);
        }
        return String.valueOf(formatter.format(new Date()) + uuidStr + Config.IMAGE_JPG_POSTFIX);
    }



    /**
     * 启动系统照相功能，按指定的文件名称存储到系统图片路径下
     *
     * @param activity         启动activity
     * @param currentImageName 当前照片文件名
     * @param imageFolder      文件夹
     */
    public static void takePicture(Activity activity, String currentImageName, String imageFolder) {
        takePicture(activity, currentImageName, imageFolder, Config.ACTION_IMAGE);
    }

    /**
     * 启动系统照相功能，按指定的文件名称存储到系统图片路径下
     *
     * @param activity         启动activity
     * @param currentImageName 当前照片文件名
     * @param imageFolder      文件夹
     */
    public static void takePicture(Activity activity, String currentImageName, String imageFolder, int requestCode) {
        if (!FileUtils.isFolderExists(imageFolder)) {
            FileUtils.makeDirectory(imageFolder);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentImageName = (null == currentImageName) ? getCurrentImageName() : currentImageName;
        File imageFile = new File(imageFolder, currentImageName);
        Uri photoUri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(intent, requestCode);
    }






    /**
     * 获取ApiKey
     *
     * @param context
     * @param metaKey
     * @return
     */
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.out);
        }
        return apiKey;
    }

    /**
     * 打开应用导航
     *
     * @param context
     * @param latitude  纬度
     * @param longitude 经度
     * @param address   地址
     */
    public static void startNavigation(Context context, String latitude, String longitude, String address) {
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            String uriString = "geo:" + latitude + "," + longitude + "?q=" + address;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                ToastUtils.showMessage("本机还未安装地图应用,请安装后使用.");
            }
        }
    }

    /**
     * 开启百度导航
     *
     * @param mContext
     * @param endLatitude
     * @param endLongitude
     * @param address
     */
    public static void startBaiduNavi(Context mContext, String endLatitude, String endLongitude, String address) {
        try {
            // 先检测是否安装百度地图App
            if (AppUtils.isAppInstalled(mContext, "com.baidu.BaiduMap")) {
                PackageInfo packageInfor = AppUtils.getLocalPackageInfo(mContext);
                String packageName = packageInfor.packageName;
                String appName = mContext.getResources().getString(packageInfor.applicationInfo.labelRes);
                String intentStr = "intent://map/marker?location=" + endLatitude + "," + endLongitude + "&title=" + address + "&content=" + address + "&src=" + packageName + "|" + appName + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                mContext.startActivity(Intent.parseUri(intentStr, 0));
            } else {
                ToastUtils.showMessage( "未安装百度地图，请您先安装百度地图");
            }
        } catch (URISyntaxException e) {
            ToastUtils.showMessage( "未安装百度地图，请您先安装百度地图");
        }
    }

    /**
     * 开启百度导航
     *
     * @param mContext
     * @param lon       百度地图经度
     * @param lat       百度地图纬度
     * @param describle 目的地
     */
    public static void startBaiduNavi(Context mContext, double lat, double lon, String describle) {
        try {
            if (AppUtils.isAppInstalled(mContext, "com.baidu.BaiduMap")) {
                StringBuilder loc = new StringBuilder();
                loc.append("intent://map/direction?origin=latlng:");
                loc.append(lat);
                loc.append(",");
                loc.append(lon);
                loc.append("|name:");
                loc.append("我的位置");
                loc.append("&destination=latlng:");
                loc.append(lat);
                loc.append(",");
                loc.append(lon);
                loc.append("|name:");
                loc.append(describle);
                loc.append("&mode=driving");
                loc.append("&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                Intent intent = Intent.parseUri(loc.toString(), 0);
                mContext.startActivity(intent);
            } else {
                ToastUtils.showMessage("未安装百度地图，请您先安装百度地图");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启高德导航
     *
     * @param mContext
     * @param lon       百度地图经度
     * @param lat       百度地图纬度
     * @param describle 目的地
     */
    public static void startGaodeNavi(Context mContext, double lat, double lon, String describle) {
        try {
            if (AppUtils.isAppInstalled(mContext, "com.autonavi.minimap")) {
                double[] gd_lat_lon = bdToGaoDe(lon, lat);
                StringBuilder loc = new StringBuilder();
                loc.append("androidamap://viewMap?sourceApplication=XX");
                loc.append("&poiname=");
                loc.append(describle);
                loc.append("&lat=");
                loc.append(gd_lat_lon[0]);
                loc.append("&lon=");
                loc.append(gd_lat_lon[1]);
                loc.append("&dev=0");
                Intent intent = Intent.parseUri(loc.toString(), 0);
                mContext.startActivity(intent);
            } else {
                ToastUtils.showMessage("未安装高德地图，请您先安装高德地图");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 百度坐标转高德坐标
     *
     * @param bd_lat 百度纬度
     * @param bd_lon 百度经度
     * @return
     */
    public static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    /**
     * 高德坐标转百度坐标
     *
     * @param gd_lon 高德经度
     * @param gd_lat 高德纬度
     * @return
     */
    public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

}
