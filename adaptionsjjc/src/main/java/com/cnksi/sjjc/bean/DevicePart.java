package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.FileUtils;
import com.cnksi.sjjc.Config;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "device_part")
public class DevicePart {

	// 设备部件编号
	public static final String DUID = "duid";
	@Column(name = DUID,isId = true)
	public String duid;

	// 设备编号
	public static final String DEVICEID = "deviceid";
	@Column(name = DEVICEID)
	public String deviceid;

	// 设备部件名称
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name;

	// 部件默认图片
	public static final String PIC = "pic";
	@Column(name = PIC)
	public String pic;

	// 更换的照片
	public static final String CHANGE_PIC = "change_pic";
	@Column(name = CHANGE_PIC)
	public String change_pic;

	// 巡检顺序
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;

	// 逻辑删除
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public String dlt;

	// 更新时间
	public static final String UPDATE_TIME = "update_time";
	@Column(name = UPDATE_TIME)
	public String update_time;

	// 部件自定义图片
	public static final String DIY_PICTURE = "diy_picture";
	@Column(name = DIY_PICTURE)
	public String diy_picture;
	
	// (定期切换试验、定期维护试验项目存储在devicepart表中,kind字段标明当前试验项属于那个试验类型)
	public static final String KIND = "kind";
	@Column(name = KIND)
	public String kind;

	// 判断设备部件的图片是否存在，不存在采用默认的图片
	public static String getPic(String pic) {
		if (!FileUtils.isFileExists(Config.PICTURES_FOLDER + pic)) {
			pic = "part_pic.png";
		}
		return pic;
	}
    public String dtid;

	public DevicePart()
	{

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
