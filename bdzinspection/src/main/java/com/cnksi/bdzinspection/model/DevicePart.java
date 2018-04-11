package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.io.File;

@Table(name = "device_part")
public class DevicePart extends BaseModel{

	// 设备部件编号
	public static final String DUID = "duid";
	@Id(column = DUID)
	public String duid;

	// 设备编号
	public static final String DEVICEID = "deviceid";
	@Column(column = DEVICEID)
	public String deviceid;

	// 设备部件名称
	public static final String NAME = "duname";
	@Column(column = NAME)
	public String name;

	// 部件默认图片
	public static final String PIC = "pic";
	@Column(column = PIC)
	public String pic;

	// 更换的照片
	public static final String CHANGE_PIC = "change_pic";
	@Column(column = CHANGE_PIC)
	public String change_pic;

	// 巡检顺序
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;



	// 更新时间
	public static final String UPDATE_TIME = "update_time";
	@Column(column = UPDATE_TIME)
	public String update_time;

	// 部件自定义图片
	public static final String DIY_PICTURE = "diy_picture";
	@Column(column = DIY_PICTURE)
	public String diy_picture;
	
	// (定期切换试验、定期维护试验项目存储在devicepart表中,kind字段标明当前试验项属于那个试验类型)
	public static final String KIND = "kind";
	@Column(column = KIND)
	public String kind;

	
	public String dtid;
	
	// 判断设备部件的图片是否存在，不存在采用默认的图片
	public static String getPic(String pic) {
		if(!TextUtils.isEmpty(pic)&&new File(Config.BDZ_INSPECTION_FOLDER+pic).exists()){
			pic=Config.BDZ_INSPECTION_FOLDER+pic;
		}else{
			pic = Config.DEFALUTFOLDER+"device_pic.png";
		}
		return pic;
	}

	public DevicePart() {
		
	}
	
	public DevicePart(DeviceUnit unit)
	{
		this.duid=unit.duid;
		this.name=unit.name;
		this.pic=unit.pic;
		this.sort=unit.sort;
		this.dtid=unit.dtid;
		this.update_time=unit.createTime;
		this.dlt=unit.dlt;
	}
	
}
