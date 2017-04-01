package com.cnksi.sjjc.util;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.cnksi.sjjc.inter.LocationListener;

/**
 * 定位工具
 * @author lyndon
 */
public class LocationUtil {

    private static LocationUtil instance;

    private LocationClient locationClient;

    private LocationClientOption locationClientOption;

    private LocationListener locationListener;

    private BDLocationListener bdLocationListener;
    private Context context;

    public static LocationUtil getInstance() {
        if (null == instance)
            instance = new LocationUtil();
        return instance;
    }

    /**
     * 设置监听
     */
    public LocationUtil setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
        return this;
    }

    /**
     * 初始化参数
     */
    public LocationUtil init(Context context) {
        this.context = context;
        locationClientOption = new LocationClientOption();
        locationClientOption.setOpenGps(true);// 打开gps
        locationClientOption.setCoorType("bd09ll"); // 设置坐标类型
        locationClientOption.setScanSpan(2000); // 定位间隔时间
        locationClientOption.setTimeOut(30000);// 定位超时时间30s
        locationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
        return this;
    }

    /**
     * 开启定位
     */
    public void requestLocation(final Context context) {
        if (null == locationClientOption) {
            throw new RuntimeException("需要先调用init()方法再进行调用setLocationListener(xxx)");
        }
        if (null == locationListener) {
            throw new RuntimeException("调用init()方法后调用setLocationListener(xxx)");
        }
        locationClient = new LocationClient(context.getApplicationContext());
        locationClient.setLocOption(locationClientOption);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                bdLocationListener = this;
                // 网络定位，离线定位，GPS定位皆可
                if (null != bdLocation && (BDLocation.TypeOffLineLocation == bdLocation.getLocType() || BDLocation.TypeNetWorkLocation == bdLocation.getLocType()
                        || BDLocation.TypeGpsLocation == bdLocation.getLocType())) {
                    locationListener.locationSuccess(bdLocation);
                    stopLocationRequest();
                } else {
                    if (null != bdLocation)
                        locationListener.locationFailure(bdLocation.getLocType(), "定位失败具体原因参考http://wiki.lbsyun.baidu.com/cms/androidloc/doc/v7.0/index.html");
                    else
                        locationListener.locationFailure(-1, "获取定位信息为空");
                }
//                locationClient.unRegisterLocationListener(this);
//                locationClient.stop();
            }
        });
        locationClient.start();
        locationClient.requestLocation();
    }

    public void stopLocationRequest() {
        if (locationClient != null) {
            locationClient.unRegisterLocationListener(bdLocationListener);
            locationClient.stop();
        }
    }
}