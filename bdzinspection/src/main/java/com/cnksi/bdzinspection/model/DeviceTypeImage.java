package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 
 * 设备类型可选图片
 * @author lyndon
 *
 */
@Table(name="device_type_pics")
public class DeviceTypeImage extends BaseModel implements Serializable {
	
	public final static String ID="id";
	@Id(column=ID)
	public String id;
	
	/**
	 * 设备类型id
	 */
	public final static String TYPE_ID="dtid";
	@Column(column=TYPE_ID)
	public String deviceTypeId;
	
	/**
	 * 设备类型名
	 */
	public final static String TYPE_NAME="dtname";
	@Column(column=TYPE_NAME)
	public String deviceTypeName;
	
	/**
	 * 设备类型模块
	 */
	public final static String MODE="dtmodel";
	@Column(column=MODE)
	public String deviceTypeModel;
	
	/**
	 * 图片
	 */
	public final static String IMAGE="pic";
	@Column(column=IMAGE)
	public String image;
	
	/**
	 * 备注
	 */
	public final static String REMARK="remark";
	@Column(column=REMARK)
	public String remark;
	
	
	/**
	 *日期 
	 */
	public final static String DATE="create_time";
	@Column(column=DATE)
	public String date;
	

}
