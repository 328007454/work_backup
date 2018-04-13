package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.table.DbModel;

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
    @Id(column = DEVICEID)
    public String deviceid;
    @Column(column = SPID)
    public String spid;
    @Column(column = NAME)
    public String name;
    @Column(column = SORT)
    public Integer sort;
    @Column(column = TYPE)
    public String type;
    @Column(column = KIND)
    public String kind;
    @Column(column = MODEL)
    public String model;
    @Column(column = MANUFACTURER)
    public String manufacturer;
    @Column(column = IS_IMPORTANT)
    public String isImportant;
    @Column(column = PIC)
    public String pic;
    @Column(column = CHANGE_PIC)
    public String change_pic;
    @Column(column = VOLTAGE)
    public String voltage;
    @Column(column = COMMISSIONING_DATE)
    public String commissioning_date;
    @Column(column = HAS_YB)
    public String has_yb;
    @Column(column = CHK)
    public String chk;
    @Column(column = Y)
    public String y;
    @Column(column = LONGITUDE)
    public String longitude;
    @Column(column = LATITUDE)
    public String latitude;
    @Column(column = DTID)
    public String dtid;
    @Column(column = BDZID)
    public String bdzid;
    @Column(column = INSTID)
    public String instid;
    @Column(column = HAS_COPY)
    public String has_copy;
    @Column(column = NAME_PINYIN)
    public String name_pinyin;
    @Column(column = NAME_SHORT_PINYIN)
    public String name_short_pinyin;
    @Column(column = NAME_SHORT)
    public String name_short;
    @Column(column = CREATETIME)
    public String createtime;
    @Column(column = CREATER)
    public String creater;
    @Column(column = INSTER_TIME)
    public String insert_time;
    @Column(column = UPDATE_TIME)
    public String update_time;
    @Column(column = DEVICE_TYPE)
    public String device_type;
    @Column(column = BIGID)
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
