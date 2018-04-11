package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.table.DbModel;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/17 20:25
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "placed_device")
public class PlacedDevice {

    public final static String ID = "id";
    public final static String REPORTID = "reportid";
    public final static String BDZID = "bdzid";
    public final static String DEVICEID = "deviceid";
    public final static String DEVICE_NAME = "device_name";
    public final static String PERSON = "person";
    public final static String PLACED_WAY = "placed_way";
    public final static String PIC = "pic";
    public final static String LATITUDE = "latitude";
    public final static String LONGTITUDE = "longtitude";
    public final static String CREATE_TIME = "create_time";
    public final static String DLT = "dlt";
    public final static String LAST_MODIFY_TIME = "last_modify_time";
    public final static String INSERT_TIME = "insert_time";
    public final static String UPDATE_TIME = "update_time";
    @Id(column = ID)
    public String id;
    @Column(column = REPORTID)
    public String reportid;
    @Column(column = BDZID)
    public String bdzid;
    @Column(column = DEVICEID)
    public String deviceid;
    @Column(column = DEVICE_NAME)
    public String device_name;
    @Column(column = PERSON)
    public String person;
    @Column(column = PLACED_WAY)
    public String placed_way;
    @Column(column = PIC)
    public String pic;
    @Column(column = LATITUDE)
    public String latitude;
    @Column(column = LONGTITUDE)
    public String longtitude;
    @Column(column = CREATE_TIME)
    public String create_time;
    @Column(column = DLT)
    public String dlt = "0";
    @Column(column = LAST_MODIFY_TIME)
    public String last_modify_time;
    @Column(column = INSERT_TIME)
    public String insert_time;
    @Column(column = UPDATE_TIME)
    public String update_time;

    public static PlacedDevice create(Device device, String reportid) {
        PlacedDevice placedDevice = new PlacedDevice();
        placedDevice.bdzid = device.bdzid;
        placedDevice.create_time = DateUtils.getCurrentLongTime();
        placedDevice.id = FunctionUtil.getPrimarykey();
        placedDevice.deviceid = device.deviceid;
        placedDevice.device_name = device.name;
        placedDevice.insert_time = placedDevice.create_time;
        placedDevice.update_time = placedDevice.create_time;
        placedDevice.reportid = reportid;
        placedDevice.person = PreferencesUtils.getString(CustomApplication.getAppContext(), Config.CURRENT_LOGIN_USER, "");
        return placedDevice;
    }

    public static PlacedDevice create(DbModel device, String reportid) {
        PlacedDevice placedDevice = new PlacedDevice();
        placedDevice.bdzid = device.getString(BDZID);
        placedDevice.create_time = DateUtils.getCurrentLongTime();
        placedDevice.id = FunctionUtil.getPrimarykey();
        placedDevice.deviceid = device.getString(DEVICEID);
        placedDevice.device_name = device.getString(DEVICE_NAME);
        placedDevice.insert_time = placedDevice.create_time;
        placedDevice.update_time = placedDevice.create_time;
        placedDevice.reportid = reportid;
        placedDevice.person = PreferencesUtils.getString(CustomApplication.getAppContext(), Config.CURRENT_LOGIN_USER, "");
        return placedDevice;
    }

    public void setPlacedWayHighest(String placedWay) {
        if (TextUtils.isEmpty(placed_way)) {
            placed_way = placedWay;
            return;
        }
        switch (placed_way) {
            case "gps":
                if (placedWay.equals("photo") || placedWay.equals("copy")) {
                    placed_way = placedWay;
                }
                break;
            case "copy":
                if (placedWay.equals("photo")) {
                    placed_way = placedWay;
                }
                break;
            default:
                break;
        }
    }

    public boolean isHasPhoto() {
        return !TextUtils.isEmpty(pic);
    }

    public boolean isGPS() {
        return "gps".equals(placed_way);
    }
}
