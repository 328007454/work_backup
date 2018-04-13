package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "defect_remove")
public class DefectRemove  extends BaseModel{

	// 缺陷消除ID
	public static final String DRID = "drid";
	@Id(column = DRID)
	public String drid;

	// 缺陷记录主键
	public static final String DEFECTID = "defectid";
	@Column(column = DEFECTID)
	public String defectid;

	// 实际消缺日期(PMS)
	public static final String REMOVE_DATE = "remove_date";
	@Column(column = REMOVE_DATE)
	public String remove_date;

	// 工作负责人(PMS)
	public static final String PRINCIPAL = "principal";
	@Column(column = PRINCIPAL)
	public String principal;

	// 消缺人(PMS)
	public static final String REMOVE_PEOPLE = "remove_people";
	@Column(column = REMOVE_PEOPLE)
	public String remove_people;

	// 消缺单位(PMS)
	public static final String REMOVE_DEPARTMENT = "remove_department";
	@Column(column = REMOVE_DEPARTMENT)
	public String remove_department;

	// 消缺班组(PMS)
	public static final String REMOVE_TEAM = "remove_team";
	@Column(column = REMOVE_TEAM)
	public String remove_team;

	// 责任原因(PMS)
	public static final String RESPONSIBLE_CAUSE = "responsible_cause";
	@Column(column = RESPONSIBLE_CAUSE)
	public String responsible_cause;

	// 技术原因(PMS)
	public static final String TECHNICAL_REASON = "technical_reason";
	@Column(column = TECHNICAL_REASON)
	public String technical_reason;

	// 处理详情(PMS)
	public static final String DEALINFO = "dealinfo";
	@Column(column = DEALINFO)
	public String dealinfo;

	// 遗留问题(PMS)
	public static final String LEGACY_ISSUE = "legacy_issue";
	@Column(column = LEGACY_ISSUE)
	public String legacy_issue;

	// 验收是否合格(PMS)
	public static final String CHECK_IS_OK = "check_is_ok";
	@Column(column = CHECK_IS_OK)
	public String check_is_ok;

	// 验收人(PMS)
	public static final String CHECKER = "checker";
	@Column(column = CHECKER)
	public String checker;

	// 验收时间(PMS)
	public static final String CHECK_TIME = "check_time";
	@Column(column = CHECK_TIME)
	public String check_time;

	// 验收意见(PMS)
	public static final String CHECK_COMMENT = "check_comment";
	@Column(column = CHECK_COMMENT)
	public String check_comment;

	// 登记时间(PMS)
	public static final String CREATE_TIME = "create_time";
	@Column(column = CREATE_TIME)
	public String create_time;

	// 消缺照片
	public static final String REMOVE_PIC = "remove_pic";
	@Column(column = REMOVE_PIC)
	public String remove_pic;

	// 消缺工作票
	public static final String REMOVE_TICKER = "remove_ticker";
	@Column(column = REMOVE_TICKER)
	public String remove_ticker;

	
	public DefectRemove(){}

}
