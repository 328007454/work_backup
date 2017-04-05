package com.cnksi.sjjc.bean.upload;


import com.cnksi.sjjc.bean.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "device")
public class UploadDevice extends BaseModel {

	// 设备编号
	public static final String DEVICEID = "deviceid";
	@Column(name = DEVICEID,isId = true)
	public String deviceid;

	public static final String DTID = "dtid";
	@Column(name = DTID)
	public String dtid;

	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	public static final String INSTID = "instid";
	@Column(name = INSTID)
	public String instid;

	// 间隔ID
	public static final String SPID = "spid";
	@Column(name = SPID)
	public String spid;

	// 设备名称
	public static final String NAME = "name";
	public static final String DNAME = "dname";
	@Column(name = NAME)
	public String name;

	public static final String CHK = "chk";
	@Column(name = CHK)
	public String chk;

	public static final String Y = "y";
	@Column(name = Y)
	public String y;

	// 巡检顺序
	public static final String SORT = "sort";
	@Column(name = SORT)
	public Integer sort;

	// 设备类型,如：变压器
	public static final String TYPE = "type";
	@Column(name = TYPE)
	public String type;

	// 设备种类，如：水冷式变压器
	public static final String KIND = "kind";
	@Column(name = KIND)
	public String kind;

	// 设备型号
	public static final String MODEL = "model";
	@Column(name = MODEL)
	public String model;

	// 生产厂家
	public static final String MANUFACTURER = "manufacturer";
	@Column(name = MANUFACTURER)
	public String manufacturer;

	//
	public static final String PIC = "pic";
	@Column(name = PIC)
	public String pic;

	// 更换的照片
	public static final String CHANGE_PIC = "change_pic";
	@Column(name = CHANGE_PIC)
	public String change_pic;



	// 电压等级从lookup表中查询type=voltage
	public static final String VOLTAGE = "voltage";
	@Column(name = VOLTAGE)
	public String voltage;

	// 投产日期
	public static final String COMMISSIONING_DATE = "commissioning_date";
	@Column(name = COMMISSIONING_DATE)
	public String commissioning_date;

	// 是否有压板
	public static final String HAS_YB = "has_yb";
	@Column(name = HAS_YB)
	public String has_yb;

	// 是否有抄录数据
	public static final String HAS_COPY = "has_copy";
	@Column(name = HAS_COPY)
	public String has_copy;

	// 设备名称拼音
	public static final String NAME_PINYIN = "name_pinyin";
	@Column(name = NAME_PINYIN)
	public String name_pinyin;

	public static final String CREATETIME = "createtime";
	@Column(name = CREATETIME)
	public String createtime;

	public static final String CREATER = "creater";
	@Column(name = CREATER)
	public String creater;

	public static final String INSTER_TIME = "insert_time";
	@Column(name = INSTER_TIME)
	public String insert_time;

	public static final String UPDATE_TIME = "update_time";
	@Column(name = UPDATE_TIME)
	public String update_time;

	// 经度
	public static final String LONGITUDE = "longitude";
	@Column(name = LONGITUDE)
	public String longitude;

	// 纬度
	public static final String LATITUDE = "latitude";
	@Column(name = LATITUDE)
	public String latitude;

}
