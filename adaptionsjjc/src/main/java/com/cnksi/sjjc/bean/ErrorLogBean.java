package com.cnksi.sjjc.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author Wastrel
 * @date 创建时间：2016年4月25日 下午1:07:28 TODO
 */
@Table(name = "error_log")
public class ErrorLogBean extends BaseModel{
	@Column(isId = true,name = "id")
	public String id;

	@Column(name = "datatime")
	public String datatime;

	@Column(name = "application")
	public String application;

	@Column(name = "version_code")
	public String version_code;

	@Column(name = "version_name")
	public String version_name;

	@Column(name = "board")
	public String board;

	@Column(name = "cpu_abi")
	public String cpu_abi;

	@Column(name = "cpu_abi2")
	public String cpu_abi2;

	@Column(name = "device")
	public String device;

	@Column(name = "manufacturer")
	public String manufacturer;

	@Column(name = "product")
	public String product;

	@Column(name = "user")
	public String user;

	@Column(name = "user_phone")
	public String user_phone;

	@Column(name = "exception")
	public String exception;

	@Column(name = "exception_desc")
	public String exception_desc;

	public ErrorLogBean() {

	}
}
