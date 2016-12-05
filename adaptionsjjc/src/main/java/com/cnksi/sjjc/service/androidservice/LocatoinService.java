package com.cnksi.sjjc.service.androidservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;

import java.util.Timer;

/**
 * Created by han on 2016/11/29.
 */

public class LocatoinService extends BaseService {
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private Context mContext;
    private Timer timer;
    private Handler handler = new Handler();
    private Intent mTimeIntent;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocatoinService(String name) {
        super(name);
    }

    public LocatoinService() {
        super("LocationService");
    }

    @Override
    public void startConmmand(Intent intent, int flags, int startId) {
        this.mContext = getApplicationContext();
        timer = new Timer();
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(4000);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void handleIntent(Intent intent) {
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void start(Intent intent, int startId) {

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取当前系统时间
            String systemTime = DateUtils.getCurrentShortTime();
            //服务器获取时间
            String locationTime = DateUtils.getFormatterTime(location.getTime());
            if (location.getLocType() == BDLocation.TypeGpsLocation)
                CToast.showLong(getApplicationContext(), location.getAddrStr() + ":" + location.getRadius() + ";" + location.getSatelliteNumber() + ":" + location.getAltitude() + ":" + location.getTime()+":"+locationTime);
            else
                CToast.showLong(getApplicationContext(), "GPS定位失败");
            if (TextUtils.isEmpty(locationTime))
                return;
            boolean isSame = DateUtils.compareDate(systemTime, locationTime, "yyyy-MM-dd");
            if (!isSame) {
                CToast.showLong(getApplicationContext(), "必须确保系统当前时间是当前北京时间，请设置！");
                Intent mTimeIntent = new Intent(Settings.ACTION_DATE_SETTINGS);//时间设置
                mTimeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(mTimeIntent);
            } else {
//                handler.removeCallbacks(runnable);
//                mLocationClient.stop();
//                onDestroy();
            }

        }


    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mLocationClient.start();
            handler.postDelayed(this, 4000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        timer.cancel();
    }
}
