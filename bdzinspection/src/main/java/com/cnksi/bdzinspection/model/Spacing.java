package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.io.Serializable;

@Table(name = "spacing")
public class Spacing extends BaseModel implements Serializable {

    // 间隔ID
    public static final String SPID = "spid";
    @Id(column = SPID)
    public String spid;

    // 父间隔ID
    public static final String PID = "pid";
    @Column(column = PID)
    public String pid;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzid;
    // 小室编号
    public static final String GROUPID = "group_id";
    @Column(column = GROUPID)
    public String groupId;
    // 小室名称
    public static final String GROUPNAME = "group_name";
    @Column(column = GROUPNAME)
    public String groupName;

    // 间隔名称
    public static final String NAME = "name";
    public static final String SNAME = "sname";
    @Column(column = NAME)
    public String name;

    public static final String NAME_PINYIN = "name_pinyin";
    @Column(column = NAME_PINYIN)
    public String name_pinyin;

    // 设备类型
    public static final String DEVICE_TYPE = "device_type";
    @Column(column = DEVICE_TYPE)
    public String deviceType;


    // 默认排序
    public static final String SORT = "sort";
    @Column(column = SORT)
    public int sort;
    // 一次设备排序
    public static final String SORT_ONE = "sort_one";
    @Column(column = SORT_ONE)
    public int sort_one;
    // 二次设备排序
    public static final String SORT_SECOND = "sort_second";
    @Column(column = SORT_SECOND)
    public int sort_second;

    // 经度
    public static final String LONGITUDE = "longitude";
    @Column(column = LONGITUDE)
    public String longitude;

    // 纬度
    public static final String LATITUDE = "latitude";
    @Column(column = LATITUDE)
    public String latitude;

    public static final String Y = "y";
    @Column(column = Y)
    public String y;

    public static final String X = "x";
    @Column(column = X)
    public String x;


    // 电压等级从lookup表中查询type=voltage
    public static final String VOLTAGE = "voltage";
    @Column(column = VOLTAGE)
    public String voltage;

    // 定位专用
    @Transient
    public double distance;

    public boolean isSubPlace() {
        return !TextUtils.isEmpty(pid);
    }
}
