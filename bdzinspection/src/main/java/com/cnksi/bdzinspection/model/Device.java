package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;

import java.io.File;

@Table(name = "device")
public class Device extends BaseModel {

    // 设备编号
    public static final String DEVICEID = "deviceid";
    // 间隔ID
    public static final String SPID = "spid";
    // 设备名称
    public static final String NAME = "name";
    public static final String DNAME = "dname";
    // 巡检顺序
    public static final String SORT = "sort";
    // 设备类型,如：变压器
    public static final String TYPE = "type";
    // 设备种类，如：水冷式变压器
    public static final String KIND = "kind";
    // 设备型号
    public static final String MODEL = "model";
    // 生产厂家
    public static final String MANUFACTURER = "manufacturer";
    // 是否是重点关注设备 （Y，N）
    public static final String IS_IMPORTANT = "is_important";
    //
    public static final String PIC = "pic";
    // 更换的照片
    public static final String CHANGE_PIC = "change_pic";
    // 电压等级从lookup表中查询type=voltage
    public static final String VOLTAGE = "voltage";
    // 投产日期
    public static final String COMMISSIONING_DATE = "commissioning_date";
    // 是否有压板
    public static final String HAS_YB = "has_yb";
    public static final String CHK = "chk";
    public static final String Y = "y";
    // 经度
    public static final String LONGITUDE = "longitude";
    // 纬度
    public static final String LATITUDE = "latitude";
    public static final String DTID = "dtid";
    public static final String BDZID = "bdzid";
    public static final String INSTID = "instid";
    // 是否有抄录数据
    public static final String HAS_COPY = "has_copy";
    // 设备名称拼音
    public static final String NAME_PINYIN = "name_pinyin";
    // 设备简称名称拼音
    public static final String NAME_SHORT_PINYIN = "name_short_pinyin";
    // 设备简称名称
    public static final String NAME_SHORT = "name_short";
    public static final String CREATETIME = "createtime";
    public static final String CREATER = "creater";
    public static final String INSTER_TIME = "insert_time";
    public static final String UPDATE_TIME = "update_time";
    /**
     * 设备型号
     */
    public static final String DEVICE_TYPE = "device_type";
    /**
     * 设备大类
     */
    public static final String BIGID = "bigid";
    /**
     * 时长
     */
    public static final String DURATION = "duration";
    /**
     * 巡视频率
     */
    public static final String FREQUENCY = "frequency";
    /**
     * 已巡视的次数
     */
    public static final String FREQUENCYED = "frequenced";
    // 总共巡视次数
    public static final String AMOUNT = "amount";
    // 上次巡视时间
    public static final String LAST_TRACK_TIME = "last_track_time";
    // 任务id
    public static final String TASK_ID = "taskid";
    @Column(name = DEVICEID,isId = true)
    public String deviceid;
    @Column(name = SPID)
    public String spid;
    @Column(name = NAME)
    public String name;
    @Column(name = SORT)
    public Integer sort;
    @Column(name = TYPE)
    public String type;
    @Column(name = KIND)
    public String kind;
    @Column(name = MODEL)
    public String model;
    @Column(name = MANUFACTURER)
    public String manufacturer;
    @Column(name = IS_IMPORTANT)
    public String isImportant;
    @Column(name = PIC)
    public String pic;
    @Column(name = CHANGE_PIC)
    public String change_pic;
    @Column(name = VOLTAGE)
    public String voltage;
    @Column(name = COMMISSIONING_DATE)
    public String commissioning_date;
    @Column(name = HAS_YB)
    public String has_yb;
    @Column(name = CHK)
    public String chk;
    @Column(name = Y)
    public String y;
    @Column(name = LONGITUDE)
    public String longitude;
    @Column(name = LATITUDE)
    public String latitude;
    @Column(name = DTID)
    public String dtid;
    @Column(name = BDZID)
    public String bdzid;
    @Column(name = INSTID)
    public String instid;
    @Column(name = HAS_COPY)
    public String has_copy;
    @Column(name = NAME_PINYIN)
    public String name_pinyin;
    @Column(name = NAME_SHORT_PINYIN)
    public String name_short_pinyin;
    @Column(name = NAME_SHORT)
    public String name_short;
    @Column(name = CREATETIME)
    public String createtime;
    @Column(name = CREATER)
    public String creater;
    @Column(name = INSTER_TIME)
    public String insert_time;
    @Column(name = UPDATE_TIME)
    public String update_time;
    @Column(name = DEVICE_TYPE)
    public String device_type;
    @Column(name = BIGID)
    public String bigid;
    // 缺陷数量
    public int defectCount = 0;
    // 最高缺陷级别
    public String defectLevel = "";
    // 是否已巡视
    public boolean hasInspection = false;
    public String duration;
    public String frequency;
    public String frequenced;
    public String amount;
    public String last_track_time;
    public String taskid;
    /**
     * 是否设定了周期
     */
    public boolean isSetuped = false;
    /**
     * 已经巡视的次数
     */
    public String hasInspectionTimes = "0";

    public Device() {
    }

    public Device(String deviceid, String latitude, String longitude) {
        this.deviceid = deviceid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 判断设备的图片是否存在，不存在采用默认的图片
    public static String getPic(String pic) {
        if (!TextUtils.isEmpty(pic) && new File(Config.BDZ_INSPECTION_FOLDER + pic).exists()) {
            pic = Config.BDZ_INSPECTION_FOLDER + pic;
        } else {
            pic = Config.DEFALUTFOLDER + "device_pic.png";
        }
        return pic;
    }

    public static boolean isImportant(DbModel model) {
        return "Y".equals(model.getString(IS_IMPORTANT));
    }

    public static boolean isOnceDevice(DbModel model) {
        return Config.PmsDeviceType.one.name().equals(model.getString(DEVICE_TYPE));
    }

    public static boolean hasGPSInfo(DbModel model) {
        return !(TextUtils.isEmpty(model.getString(LATITUDE)) || TextUtils.isEmpty(model.getString(LONGITUDE)));
    }

    public boolean isOnceDevice() {
        return Config.PmsDeviceType.one.name().equals(device_type);
    }

    public boolean getHasCopy() {
        if (has_copy != null && has_copy.equals("Y")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isImportant() {
        return "Y".equals(isImportant);
    }
}
