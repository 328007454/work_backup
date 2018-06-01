package com.cnksi.bdloc;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/4/20 13:43
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

public class LatLng implements Parcelable {
    public double lat;
    public double lng;
    public double precision = Double.MAX_VALUE;

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng(BDLocation location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        precision = location.getRadius() > 0 ? location.getRadius() : Double.MAX_VALUE;
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

    protected LatLng(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        precision = in.readDouble();
    }

    public static final Creator<LatLng> CREATOR = new Creator<LatLng>() {
        @Override
        public LatLng createFromParcel(Parcel in) {
            return new LatLng(in);
        }

        @Override
        public LatLng[] newArray(int size) {
            return new LatLng[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(precision);
    }
}

