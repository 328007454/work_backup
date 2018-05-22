package com.cnksi.sjjc.bean;

import com.cnksi.common.model.BaseModel;
import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "report_jzlbyqfjkg")
public class ReportJzlbyqfjkg extends BaseModel {
	
	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id = UUID.randomUUID().toString();
	
	/**报告id*/
	public static final String REPORT_ID = "report_id";
	@Column(name = REPORT_ID)
	public String report_id;
	
	/**变电站id*/
	public static final String BDZ_ID = "bdz_id";
	@Column(name = BDZ_ID)
	public String bdz_id;
	
	/**变电站名称*/
	public static final String BDZ_NAME = "bdz_name";
	@Column(name = BDZ_NAME)
	public String bdz_name;
	
	/**设备id*/
	public static final String DEVICE_ID = "device_id";
	@Column(name = DEVICE_ID)
	public String device_id;
	
	/**设备名称*/
	public static final String DEVICE_NAME = "device_name";
	@Column(name = DEVICE_NAME)
	public String device_name;
	
	/**相别名称*/
	public static final String XBMC = "xbmc";
	@Column(name = XBMC)
	public String xbmc;
	
	/**上次读数*/
	public static final String SCDS = "scds";
	@Column(name = SCDS)
	public String scds;
	
	/**本次读数*/
	public static final String BCDS = "bcds";
	@Column(name = BCDS)
	public String bcds;
	
	/**动作次数*/
	public static final String DZCS = "dzcs";
	@Column(name = DZCS)
	public String dzcs;
	
	/**年度动作次数*/
	public static final String NDDZCS = "nddzcs";
	@Column(name = NDDZCS)
	public String nddzcs;
	
	/**班次*/
	public static final String BC = "bc";
	@Column(name = BC)
	public String bc;
	
	/**插入时间*/
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;
	
	/**最后更新时间*/
	public static final String LAST_MODIFY_TIME = "last_modify_time";
	@Column(name = LAST_MODIFY_TIME)
	public String last_modify_time;
	


	public ReportJzlbyqfjkg(){
		this.insert_time = DateUtils.getCurrentLongTime();
		this.last_modify_time = DateUtils.getCurrentLongTime();
		this.dlt = "0";
	}
}
