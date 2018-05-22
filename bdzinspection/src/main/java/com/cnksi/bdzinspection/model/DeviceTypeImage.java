package com.cnksi.bdzinspection.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

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
	@Column(name=ID,isId = true)
	public String id;
	
	/**
	 * 设备类型id
	 */
	public final static String TYPE_ID="dtid";
	@Column(name=TYPE_ID)
	public String deviceTypeId;
	
	/**
	 * 设备类型名
	 */
	public final static String TYPE_NAME="dtname";
	@Column(name=TYPE_NAME)
	public String deviceTypeName;
	
	/**
	 * 设备类型模块
	 */
	public final static String MODE="dtmodel";
	@Column(name=MODE)
	public String deviceTypeModel;
	
	/**
	 * 图片
	 */
	public final static String IMAGE="pic";
	@Column(name=IMAGE)
	public String image;
	
	/**
	 * 备注
	 */
	public final static String REMARK="remark";
	@Column(name=REMARK)
	public String remark;
	
	
	/**
	 *日期 
	 */
	public final static String DATE="create_time";
	@Column(name=DATE)
	public String date;
	

}
