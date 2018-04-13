package com.cnksi.bdzinspection.model;

import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.FileUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "device_unit")
public class DeviceUnit extends BaseModel {

	// 设备部件编号
	public static final String DUID = "duid";
	@Id(column = DUID)
	public String duid;

	// 设备类型编号
	public static final String DTID = "dtid";
	@Column(column = DTID)
	public String dtid;

	// 设备部件名称
	public static final String NAME = "duname";
	@Column(column = NAME)
	public String name;

	// 部件默认图片
	public static final String PIC = "pics";
	@Column(column = PIC)
	public String pic;

	// 巡检顺序
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;



	// 更新时间
	public static final String CREATE_TIME = "createtime";
	@Column(column = CREATE_TIME)
	public String createTime;



	// 判断设备部件的图片是否存在，不存在采用默认的图片
	public static String getPic(String pic) {
		if (!FileUtils.isFileExists(Config.PICTURES_FOLDER + pic)) {
			pic = "part_pic.png";
		}
		return pic;
	}
}
