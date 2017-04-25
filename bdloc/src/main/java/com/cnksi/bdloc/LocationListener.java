package com.cnksi.bdloc;

import com.baidu.location.BDLocation;

/**
 * 定位回调接口
 *
 * @author lyndon
 */
public abstract class LocationListener {

    public static final int ERROR_TIMEOUT = -16;

    /**
     * 定位成功
     *
     * @param location
     */
    public abstract void locationSuccess(BDLocation location);

    /**
     * 定位失败
     *
     * @param code
     * @param message
     */
    public void locationFailure(int code, String message) {
        LLog.e("定位失败：code" + code + ",message:" + message);
    }


    /**
     * 默认离线定位网络定位等都有效。
     *
     * @param bdLocation
     * @return
     */
    public boolean isTrustLocation(BDLocation bdLocation) {
        return null != bdLocation && (BDLocation.TypeOffLineLocation == bdLocation.getLocType() || BDLocation.TypeNetWorkLocation == bdLocation.getLocType()
                || BDLocation.TypeGpsLocation == bdLocation.getLocType());
    }
}
