package com.cnksi.common.model;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "spacing")
public class Spacing extends BaseModel implements Serializable {

    // 间隔ID
    public static final String SPID = "spid";
    @Column(name = SPID,isId = true)
    public String spid;

    // 父间隔ID
    public static final String PID = "pid";
    @Column(name = PID)
    public String pid;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;
    // 小室编号
    public static final String GROUPID = "group_id";
    @Column(name = GROUPID)
    public String groupId;
    // 小室名称
    public static final String GROUPNAME = "group_name";
    @Column(name = GROUPNAME)
    public String groupName;

    // 间隔名称
    public static final String NAME = "name";
    public static final String SNAME = "sname";
    @Column(name = NAME)
    public String name;

    public static final String NAME_PINYIN = "name_pinyin";
    @Column(name = NAME_PINYIN)
    public String name_pinyin;

    // 设备类型
    public static final String DEVICE_TYPE = "device_type";
    @Column(name = DEVICE_TYPE)
    public String deviceType;


    // 默认排序
    public static final String SORT = "sort";
    @Column(name = SORT)
    public int sort;
    // 一次设备排序
    public static final String SORT_ONE = "sort_one";
    @Column(name = SORT_ONE)
    public int sort_one;
    // 二次设备排序
    public static final String SORT_SECOND = "sort_second";
    @Column(name = SORT_SECOND)
    public int sort_second;

    // 经度
    public static final String LONGITUDE = "longitude";
    @Column(name = LONGITUDE)
    public String longitude;

    // 纬度
    public static final String LATITUDE = "latitude";
    @Column(name = LATITUDE)
    public String latitude;

    public static final String Y = "y";
    @Column(name = Y)
    public String y;

    public static final String X = "x";
    @Column(name = X)
    public String x;


    // 电压等级从lookup表中查询type=voltage
    public static final String VOLTAGE = "voltage";
    @Column(name = VOLTAGE)
    public String voltage;

    // 定位专用
    public double distance;

    public boolean isSubPlace() {
        return !TextUtils.isEmpty(pid);
    }
}