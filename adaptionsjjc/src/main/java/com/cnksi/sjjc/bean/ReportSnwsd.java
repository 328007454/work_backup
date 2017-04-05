package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "report_snwsd")
public class ReportSnwsd extends BaseModel{
	
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
	
	/**温度*/
	public static final String WD = "wd";
	@Column(name = WD)
	public String wd;
	
	/**湿度*/
	public static final String SD = "sd";
	@Column(name = SD)
	public String sd;

	/**
	 * 地域
	 */
	public static final String LOCATION = "location";
	@Column(name = LOCATION)
	public String location;
	
	/**插入时间*/
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;
	
	/**最后更新时间*/
	public static final String LAST_MODIFY_TIME = "last_modify_time";
	@Column(name = LAST_MODIFY_TIME)
	public String last_modify_time;
	



	public ReportSnwsd(){
		this.insert_time = DateUtils.getCurrentLongTime();
		this.dlt = "0";
	}

	public ReportSnwsd(String currentReportId, String currentBdzId, String currentBdzName) {
		this.report_id = currentReportId;
		this.bdz_id = currentBdzId;
		this.bdz_name=currentBdzName;
		this.insert_time = DateUtils.getCurrentLongTime();
		this.dlt = "0";
	}
}
