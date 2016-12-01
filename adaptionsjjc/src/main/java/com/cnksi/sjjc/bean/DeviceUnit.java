package com.cnksi.sjjc.bean;


import com.cnksi.core.utils.FileUtils;
import com.cnksi.sjjc.Config;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


@Table(name = "device_unit")
public class DeviceUnit {

	// 设备部件编号
	public static final String DUID = "duid";
	@Column(name = DUID,isId = true)
	public String duid;

	// 设备类型编号
	public static final String DTID = "dtid";
	@Column(name = DTID)
	public String dtid;

	// 设备部件名称
	public static final String NAME = "duname";
	@Column(name = NAME)
	public String name;

	// 部件默认图片
	public static final String PIC = "pics";
	@Column(name = PIC)
	public String pic;

	// 巡检顺序
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;

	// 逻辑删除
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public String dlt;

	// 更新时间
	public static final String CREATE_TIME = "createtime";
	@Column(name = CREATE_TIME)
	public String createTime;



	// 判断设备部件的图片是否存在，不存在采用默认的图片
	public static String getPic(String pic) {
		if (!FileUtils.isFileExists(Config.PICTURES_FOLDER + pic)) {
			pic = "part_pic.png";
		}
		return pic;
	}
}
