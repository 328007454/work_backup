package com.cnksi.bdloc;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 定位工具
 *
 * @author lyndon
 */
public class LocationUtil {

    private static LocationUtil instance = new LocationUtil();

    private LocationClient locationClient;

    private LocationClientOption locationClientOption;

    Handler handler = new Handler(Looper.getMainLooper());

    public static LocationUtil getInstance() {
        return instance;
    }

    private boolean isPrepare = false;
    private final Set<LocationHelper> helpers = new HashSet<>();
    private long lastLocationTime;

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
        instance.locationClientOption.setTimeOut(300000);// 定位超时时间30s
        instance.locationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
        instance.locationClient = new LocationClient(context.getApplicationContext(), instance.locationClientOption);
    }


    public LocationHelper getLocalHelper(LocationListener listener) {
        return new LocationHelper(listener);
    }


    public class LocationHelper implements BDLocationListener {
        LocationListener listener;
        boolean isRunning = false;
        boolean isKeep = false;
        long timeout = 30000;
        long period = 0;
        private boolean isNeedDispatch = true;

        public LocationHelper(LocationListener listener) {
            this.listener = listener;
        }

        /**
         * 设置超时时间 超时将会调用failure方法，code=-16;
         *
         * @param timeout
         * @return
         */
        public LocationHelper setTimeout(int timeout, TimeUnit timeUnit) {
            this.timeout = timeUnit.toMillis(timeout);
            return this;
        }

        public LocationHelper setTimeout(int timeout) {
            return setTimeout(timeout, TimeUnit.SECONDS);
        }

        /**
         * 设置定位周期 必须大于2000才有效。
         *
         * @param period
         * @return
         */
        public LocationHelper setPeriod(int period, TimeUnit timeUnit) {
            this.period = timeUnit.toMillis(period);
            this.isKeep = true;
            return this;
        }

        public LocationHelper setPeriod(int period) {
            return setPeriod(period, TimeUnit.SECONDS);
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LLog.e("locType:" + bdLocation.getLocType() + ";" + bdLocation.getLatitude() + ";" + bdLocation.getLongitude());
            if (listener.isTrustLocation(bdLocation) && isNeedDispatch) {
                lastLocationTime = System.currentTimeMillis();
                listener.locationSuccess(bdLocation);
                isNeedDispatch = false;
                if (!isKeep)
                    stop();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void stop() {
            isStopByPause = false;
            if (isRunning) {
                isRunning = false;
                LocationUtil.this.removeListener(this);
                handler.removeCallbacks(TIMEOUT_TASK);
                handler.removeCallbacks(PERIOD_TASK);
            }
        }

        Runnable TIMEOUT_TASK = new Runnable() {
            @Override
            public void run() {
                if (isRunning) listener.locationFailure(LocationListener.ERROR_TIMEOUT, "请求超时！");
                stop();
            }
        };
        Runnable PERIOD_TASK = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    handler.postDelayed(this, period);
                    isNeedDispatch = true;
                    //如果60s内没有获得有效的位置。则重新请求定位
                    if (System.currentTimeMillis() - lastLocationTime > TimeUnit.SECONDS.toMillis(60))
                        locationClient.requestLocation();
                }
            }
        };

        public void start() {
            if (isRunning) return;
            isRunning = true;
            addListener(this);

            if (period >= 2000)
                handler.postDelayed(PERIOD_TASK, period);
            else if (timeout >= 2000)
                handler.postDelayed(TIMEOUT_TASK, timeout);
        }

        boolean isStopByPause = false;

        public void pause() {
            LLog.e("暂停定位请求！");
            stop();
            isStopByPause = true;
        }

        /**
         * 恢复暂停的定位请求。
         */
        public void resume() {
            if (isStopByPause) {
                LLog.e("恢复定位请求！");
                start();
            }
        }
    }

    private void addListener(LocationHelper helper) {
        if (helpers.add(helper))
            locationClient.registerLocationListener(helper);
        if (!locationClient.isStarted()) locationClient.start();
        else
            locationClient.requestLocation();
    }

    private synchronized void removeListener(LocationHelper helper) {
        locationClient.unRegisterLocationListener(helper);
        helpers.remove(helper);
        if (helpers.isEmpty()) locationClient.stop();
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

    /**
     * 请求一次GPS定位。
     */
    public void prepare() {
        if (isPrepare) return;
        getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                isPrepare = true;
            }

            @Override
            public boolean isTrustLocation(BDLocation bdLocation) {
                return bdLocation != null && bdLocation.getLocType() == BDLocation.TypeGpsLocation;
            }
        }).setTimeout(300 * 1000).start();
    }
}