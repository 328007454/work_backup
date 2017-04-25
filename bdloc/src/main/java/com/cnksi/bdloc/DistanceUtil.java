package com.cnksi.bdloc;

import com.baidu.location.BDLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static boolean isInCirle(LatLng center, LatLng dst, double radius) {
        return getDistance(center, dst) < radius;
    }

    public static LatLng getHighest(BDLocation... locations) {
        if (locations.length == 0) return null;
        List<LatLng> lngs = new ArrayList<>();
        for (BDLocation location : locations) {
            lngs.add(new LatLng(location));
        }
        return getHighest(lngs);
    }


    public static LatLng getHighest(LatLng... latlngs) {
        if (latlngs.length == 0) return null;
        return getHighest(Arrays.asList(latlngs));
    }

    public static LatLng getHighest(List<LatLng> latlngs) {
        if (latlngs == null || latlngs.size() == 0) return null;
        LatLng highest = latlngs.get(0);
        for (int i = 0; i < latlngs.size(); i++) {
            if (latlngs.get(i).precision < highest.precision)
                highest = latlngs.get(i);
        }
        return highest;
    }

    public static boolean isEquals(BDLocation src,BDLocation dst)
    {
        if (src!=null&&dst!=null&&src.getRadius()>0&&src.getRadius()>0)
        {
           return src.getLongitude()==dst.getLongitude()&&
                    src.getLatitude()==dst.getLatitude()
                   &&src.getRadius()==dst.getRadius();

        }
        return false;
    }


}
