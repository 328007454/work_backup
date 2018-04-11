package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "standard_special")
public class StandardSpecial extends BaseModel{

	/***/
	public static final String ID = "id";
	@Id(column = ID)
	public String id;

	/** 变电站id */
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	/** 巡视类型 */
	public static final String KIND = "kind";
	@Column(column = KIND)
	public String kind;

	/** 设备大类的Id */
	public static final String BIGID = "bigid";
	@Column(column = BIGID)
	public String bigid;

	/** 设备大类名称 */
	public static final String BIGTYPE_NAME = "bigtype_name";
	@Column(column = BIGTYPE_NAME)
	public String bigtype_name;

	/** 描述 */
	public static final String DESCRIPTION = "description";
	@Column(column = DESCRIPTION)
	public String description;

	/** 来源 */
	public static final String ORIGIN = "origin";
	@Column(column = ORIGIN)
	public String origin;

	/** 同一类型下的不同值的排序 */
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;

	/** 备注 */
	public static final String REMARK = "remark";
	@Column(column = REMARK)
	public String remark;



	/** 插入时间 */
	public static final String INSERT_TIME = "insert_time";
	@Column(column = INSERT_TIME)
	public String insert_time;

	/***/
	public static final String UPDATE_TIME = "update_time";
	@Column(column = UPDATE_TIME)
	public String update_time;

}
