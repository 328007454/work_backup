package com.cnksi.sjjc.util;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/4/20 10:17
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DistanceUtil {
    /**
     * 获取两个经纬度之间的距离 单位米
     *
     * @param start
     * @param end
     * @return
     */
    public static double getDistance(LatLng start, LatLng end) {

        double lat1 = (Math.PI / 180) * start.lat;
        double lat2 = (Math.PI / 180) * end.lat;

        double lon1 = (Math.PI / 180) * start.lng;
        double lon2 = (Math.PI / 180) * end.lng;

        double R = 6370996.81;

        return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

    }

}
