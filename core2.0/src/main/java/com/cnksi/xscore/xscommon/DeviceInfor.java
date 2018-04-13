package com.cnksi.xscore.xscommon;

import com.cnksi.xscore.xsutils.DateUtils;

public class DeviceInfor {

	/**
	 * 项目id
	 */
	public static final String PID = "client.pcode";
	// "34794BDC-5B5C-4569-BA6D-87DC7E1462BC";
	public String pcode = "A0AE26FD-7B59-495B-99FC-FF274D413C7E";

	/**
	 * 终端设备ID
	 */
	public static final String DEVICE_ID = "client.deviceid";
	public String deviceid;

	/**
	 * 终端设备品牌
	 */
	public static final String BRAND = "client.brand";
	public String brand;

	/**
	 * 终端运行内存
	 */
	public static final String MEMORY = "client.memory";
	public String memory;

	/**
	 * 终端设备型号
	 */
	public static final String MODEL = "client.model";
	public String model;

	/**
	 * 终端程序版本
	 */
	public static final String VER = "client.ver";
	public String ver;

	/**
	 * 最后访问时间
	 */
	public static final String LASTVIST = "client.lastvist";
	public String lastvist = DateUtils.getCurrentLongTime();

	/**
	 * android系统版本
	 */
	public static final String ANDROID = "client.android";
	public String android;

	/**
	 * 手机厂商
	 */
	public static final String FACTORY = "client.factory";
	public String factory;

	/**
	 * 电话号码
	 */
	public static final String PHONE_NUM = "client.phone";
	public String phone;

	/**
	 * 像素密度
	 */
	public static final String DENSITY = "client.density";
	public String density;

	/**
	 * 屏幕分辨率
	 */
	public static final String RESOLUTION = "client.resolution";
	public String resolution;

	/**
	 * 经度
	 */
	public static final String LONGITUDE = "client.longitude";
	public String longitude;

	/**
	 * 纬度
	 */
	public static final String LATITUDE = "client.latitude";
	public String latitude;

	/**
	 * 位置
	 */
	public static final String PLACE = "client.place";
	public String place;
}
