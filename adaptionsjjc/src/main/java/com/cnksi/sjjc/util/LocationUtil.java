package com.cnksi.sjjc.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.cnksi.core.utils.CLog;
import com.cnksi.sjjc.inter.LocationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * 定位工具
 *
 * @author lyndon
 */
public class LocationUtil {

    private static LocationUtil instance;

    private LocationClient locationClient;

    private LocationClientOption locationClientOption;

    Handler handler = new Handler(Looper.getMainLooper());

    public static LocationUtil getInstance() {
        if (null == instance)
            instance = new LocationUtil();
        return instance;
    }

    Set<LocationHelper> helpers = new HashSet<>();


    /**
     * 初始化参数
     */
    public static void init(Context context) {
        if (null == instance.locationClientOption) {
            instance.locationClientOption = new LocationClientOption();
        }
        instance.locationClientOption.setOpenGps(true);// 打开gps
        instance.locationClientOption.setCoorType("bd09ll"); // 设置坐标类型
        instance.locationClientOption.setScanSpan(2000); // 定位间隔时间
        instance.locationClientOption.setTimeOut(30000);// 定位超时时间30s
        instance.locationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
        instance.locationClient = new LocationClient(context.getApplicationContext(), instance.locationClientOption);
    }

    /**
     * 开启定位
     */
    public LocationHelper requestLocation(LocationListener listener, int timeout, int interval) {
        if (null == locationClient) {
            throw new RuntimeException("需要先调用init()方法");
        }
        LocationHelper helper = interval > 2000 ? new LocationHelper(listener, true, interval) : new LocationHelper(listener, timeout);
        addListener(helper);
        if (!locationClient.isStarted()) locationClient.start();
        locationClient.requestLocation();
        return helper;
    }

    public class LocationHelper implements BDLocationListener {
        LocationListener listener;
        boolean isRemove;
        boolean isKeep = false;

        public LocationHelper(LocationListener listener, final int timeout) {
            this.listener = listener;
            if (timeout > 2000)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        remove();
                    }
                }, timeout);
        }

        public LocationHelper(LocationListener listener, final boolean isKeep, final int interval) {
            this.listener = listener;
            this.isKeep = true;
            if (interval > 2000)
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRemove) {
                            handler.postDelayed(this, interval);
                        }
                        addListener(LocationHelper.this);
                        locationClient.requestLocation();

                    }
                }, interval);
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            CLog.e("locType:" + bdLocation.getLocType() + ";" + bdLocation.getLatitude() + ";" + bdLocation.getLongitude());
            // 网络定位，离线定位，GPS定位皆可
            if (null != bdLocation && (BDLocation.TypeOffLineLocation == bdLocation.getLocType() || BDLocation.TypeNetWorkLocation == bdLocation.getLocType()
                    || BDLocation.TypeGpsLocation == bdLocation.getLocType())) {
                listener.locationSuccess(bdLocation);
                if (!isKeep)
                    remove();
            } else {
                if (null != bdLocation)
                    listener.locationFailure(bdLocation.getLocType(), "定位失败具体原因参考http://wiki.lbsyun.baidu.com/cms/androidloc/doc/v7.0/index.html");
                else
                    listener.locationFailure(-1, "获取定位信息为空");
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void remove() {
            if (isRemove) return;
            isRemove = true;
            LocationUtil.this.remove(this);
        }
    }

    private void addListener(LocationHelper helper) {
        locationClient.registerLocationListener(helper);
        helpers.add(helper);
    }

    private synchronized void remove(LocationHelper helper) {
        locationClient.unRegisterLocationListener(helper);
        helpers.remove(helper);
    }


    public void cancelAllRequest() {
        if (locationClient != null) {
            if (helpers.size() > 0) {
                for (LocationHelper helper : helpers) {
                    locationClient.unRegisterLocationListener(helper);
                }
            }
            locationClient.stop();
        }
    }
}