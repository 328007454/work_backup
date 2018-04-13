package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "users")
public class Users extends BaseModel{

	// 用户账户
	public static final String ACCOUNT = "account";
	@Id(column = ACCOUNT)
	public String account;

	// 用户名称
	public static final String USERNAME = "username";
	@Column(column = USERNAME)
	public String username;

	// 用户密码
	public static final String PWD = "pwd";
	@Column(column = PWD)
	public String pwd;

	/**
	 * 用户密码
	 */
	public static final String OLD_PWD = "old_pwd";
	@Column(column = OLD_PWD)
	public String oldPwd;

	// 用户性别
	public static final String SEX = "sex";
	@Column(column = SEX)
	public String sex;

	// 是否添加
	public static final String ISADD = "isadd";
	@Column(column = ISADD)
	public String isadd;

	// 用户类型(super 超级管理员, tracker巡检人员)
	public static final String TYPE = "type";
	@Column(column = TYPE)
	public String type;
	
	/**
	 * 部门
	 */
	public static final String DEPT_ID = "dept_id";
	@Column(column = DEPT_ID)
	public String dept_id;

	//
	public static final String HEADPIC = "headpic";
	@Column(column = HEADPIC)
	public String headpic;

	public static final String CCP_KEY_SID = "ccp_key_sid";
	@Column(column = CCP_KEY_SID)
	public String ccp_key_sid;

	public static final String CCP_KEY_PWD = "ccp_key_pwd";
	@Column(column = CCP_KEY_PWD)
	public String ccp_key_pwd;

	public static final String CCP_KEY_SUBID = "ccp_key_subid";
	@Column(column = CCP_KEY_SUBID)
	public String ccp_key_subid;

	public static final String CCP_KEY_SUBPWD = "ccp_key_subpwd";
	@Column(column = CCP_KEY_SUBPWD)
	public String ccp_key_subpwd;

}
