package com.cnksi.sjjc.bean.gztz;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = PmsXianlu._TABLE_NAME_)
public class PmsXianlu{
	public static final String _TABLE_NAME_="pms_xianlu";

	public static final String _PK_NAME_ = "id";

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id  ;
	
	/***/
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name  ;
	
	/***/
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid  ;
	
	/***/
	public static final String PMS_BDZID = "pms_bdzid";
	@Column(name = PMS_BDZID)
	public String pmsBdzid  ;
	
	/***/
	public static final String PMS_ID = "pms_id";
	@Column(name = PMS_ID)
	public String pmsId  ;
	
	/***/
	public static final String PMS_NAME = "pms_name";
	@Column(name = PMS_NAME)
	public String pmsName  ;
	
	/***/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt  =0 ;
	
	/**是否已删除*/
	public static final String ENABLED = "enabled";
	@Column(name = ENABLED)
	public int enabled  ;
	
	
}
