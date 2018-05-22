package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

@Table(name = "standard_special")
public class StandardSpecial extends BaseModel{

	/***/
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	/** 变电站id */
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	/** 巡视类型 */
	public static final String KIND = "kind";
	@Column(name = KIND)
	public String kind;

	/** 设备大类的Id */
	public static final String BIGID = "bigid";
	@Column(name = BIGID)
	public String bigid;

	/** 设备大类名称 */
	public static final String BIGTYPE_NAME = "bigtype_name";
	@Column(name = BIGTYPE_NAME)
	public String bigtype_name;

	/** 描述 */
	public static final String DESCRIPTION = "description";
	@Column(name = DESCRIPTION)
	public String description;

	/** 来源 */
	public static final String ORIGIN = "origin";
	@Column(name = ORIGIN)
	public String origin;

	/** 同一类型下的不同值的排序 */
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;

	/** 备注 */
	public static final String REMARK = "remark";
	@Column(name = REMARK)
	public String remark;



	/** 插入时间 */
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;

	/***/
	public static final String UPDATE_TIME = "update_time";
	@Column(name = UPDATE_TIME)
	public String update_time;

}
