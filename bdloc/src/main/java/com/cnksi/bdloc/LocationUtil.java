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

    private static LocationUtil instance;

    private LocationClient locationClient;

    private static int MIN_INTERVAL_TIME = 2000;

    Handler handler = new Handler(Looper.getMainLooper());

    public static LocationUtil getInstance() {
        if (instance == null) throw new RuntimeException("你必须调用init方法初始化工具类");
        return instance;
    }

    private boolean isPrepare = false;
    private final Set<LocationHelper> helpers = new HashSet<>();
    private final Set<LocationHelper> sleepHelpers = new HashSet<>();


    private LocationUtil(Context context) {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setCoorType("bd09ll"); // 设置坐标类型
        locationClientOption.setScanSpan(MIN_INTERVAL_TIME); // 定位间隔时间
        locationClientOption.setTimeOut(300000);// 定位超时时间30s
        locationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
        locationClientOption.setLocationNotify(true);
        locationClient = new LocationClient(context.getApplicationContext(), locationClientOption);
    }

    /**
     * 初始化参数
     */
    public static void init(Context context) {
        if (instance == null)
            instance = new LocationUtil(context);
    }


    public LocationHelper getLocalHelper(LocationListener listener) {
        return new LocationHelper(listener);
    }

    //记录最后一次受信任的位置
    BDLocation lastLocation;
    long lastLocationTime;

    public class LocationHelper implements BDLocationListener {
        LocationListener listener;
        Status status = Status.stop;
        boolean isKeep = false;
        long timeout = 30000;
        long period = 0;
        boolean periodSuccess = false;


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

        public LocationHelper setKeep(boolean isKeep) {
            this.isKeep = isKeep;
            return this;
        }


        /**
         * 设置定位周期 必须大于MIN_INTERVAL_TIME才有效。
         *
         * @param period
         * @return
         */
        public LocationHelper setPeriod(int period, TimeUnit timeUnit) {
            this.period = timeUnit.toMillis(period);
            //定位周期小于MIN_INTERVAL_TIME 则认为是keep状态
            if (this.period <= MIN_INTERVAL_TIME) {
                isKeep = true;
                this.period = 0;
            }
            return this;
        }

        public LocationHelper setPeriod(int period) {
            return setPeriod(period, TimeUnit.SECONDS);
        }

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            LLog.i("locType:" + bdLocation.getLocTypeDescription() + ";lat=" + bdLocation.getLatitude() + ";lng=" + bdLocation.getLongitude());
            if (listener.isTrustLocation(bdLocation)) {
                //如果是周期模式  暂停定位  等待下一周期恢复
                if (period > MIN_INTERVAL_TIME) {
                    //标记这一个周期已经完成定位。
                    periodSuccess = true;
                    removeListener(this);
                } else if (isKeep) {
                    if (DistanceUtil.isEquals(lastLocation, bdLocation)) {
                        return;//如果是持续模式则当两次返回位置不一样时再调用回调请求。
                    }
                } else {
                    stop();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.locationSuccess(bdLocation);
                    }
                });
                lastLocation = bdLocation;
                lastLocationTime = System.currentTimeMillis();
            }
        }


        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        public void stop() {
            LLog.e("停止定位请求");
            if (Status.stop != status) {
                status = Status.stop;
                removeListener(this);
                handler.removeCallbacks(TIMEOUT_TASK);
                handler.removeCallbacks(PERIOD_TASK);
            }
        }

        Runnable TIMEOUT_TASK = new Runnable() {
            @Override
            public void run() {
                if (Status.start == status) {
                    //如果定位超时 并且上次定位间隔不超过1分钟 则认为上次定位为有效定位。
                    if (System.currentTimeMillis() - lastLocationTime > TimeUnit.SECONDS.toMillis(30))
                        listener.locationFailure(LocationListener.ERROR_TIMEOUT, "请求定位超时！");
                    else listener.locationSuccess(lastLocation);
                }
                stop();
            }
        };
        Runnable PERIOD_TASK = new Runnable() {
            @Override
            public void run() {
                if (Status.start == status) {
                    if (!periodSuccess)
                        listener.locationFailure(LocationListener.ERROR_TIMEOUT, "本次定位失败！");
                    else addListener(LocationHelper.this);
                    handler.postDelayed(this, period);
                }
            }
        };

        public void start() {
            LLog.e("开始定位请求");
            if (Status.start == status) {
                locationClient.requestLocation();
                return;
            }
            status = Status.start;
            addListener(this);
            //如果该helper为持续模式 则超时和周期任务均无效
            if (isKeep) return;
            //周期大于2s才认为有效，并创立周期任务。
            if (period >= MIN_INTERVAL_TIME) {
                handler.postDelayed(PERIOD_TASK, period);
            } else if (timeout >= MIN_INTERVAL_TIME)
                handler.postDelayed(TIMEOUT_TASK, timeout);
        }


        public void pause() {
            if (status == Status.start) {
                status = Status.pause;
                LLog.e("暂停定位请求！");
                removeListener(this);
                if (period > MIN_INTERVAL_TIME) handler.removeCallbacks(PERIOD_TASK);
                sleepHelpers.add(this);
            }
        }

        /**
         * 恢复暂停的定位请求。
         */
        public void resume() {

            if (status == Status.pause) {
                status = Status.start;
                addListener(this);
                LLog.e("恢复定位请求！");
                sleepHelpers.remove(this);
                if (period > MIN_INTERVAL_TIME) handler.postDelayed(PERIOD_TASK, period);
            }

        }
    }

    enum Status {
        start,
        pause,
        stop
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
        if (helpers.size() > 0) {
            for (LocationHelper helper : helpers) {
                helper.stop();
            }
        }
        if (sleepHelpers.size() > 0)
            for (LocationHelper helper : sleepHelpers) {
                helper.stop();
            }
        locationClient.stop();
    }

    /**
     * 请求一次高精度GPS定位。用于提前启动GPS，避免冷启动时间过长的问题。5分钟内没有搜索到GPS则放弃。
     */
    public void preSearchGps(Context context) {
        if (isPrepare) return;
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setCoorType("bd09ll"); // 设置坐标类型
        locationClientOption.setTimeOut(300000);// 定位超时时间30s
        locationClientOption.setLocationMode(LocationMode.Device_Sensors);
        final LocationClient locationClient = new LocationClient(context.getApplicationContext(), locationClientOption);
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null && bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    isPrepare = true;
                    locationClient.unRegisterLocationListener(this);
                    locationClient.stop();
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
        locationClient.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationClient.stop();
            }
        }, 300 * 1000);


    }
}