package com.cnksi.sjjc.util;

import com.baidu.location.BDLocation;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/4/20 13:43
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

public class LatLng {
    public double lat;
    public double lng;

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng(BDLocation location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    public static LatLng valueOf(String latStr, String lngStr) {
        try {
            double lat = Double.valueOf(latStr);
            double lng = Double.valueOf(lngStr);
            return new LatLng(lat, lng);
        } catch (Exception e) {
            return null;
        }
    }

}

