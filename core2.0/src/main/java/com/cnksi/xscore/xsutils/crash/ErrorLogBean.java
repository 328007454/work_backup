package com.cnksi.xscore.xsutils.crash;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @author Wastrel
 * @date 创建时间：2016年4月25日 下午1:07:28 TODO
 */
@Table(name = "error_log")
public class ErrorLogBean {
	/**
	 * ID
	 */
	public static final String ID = "id";
	@Id(column = ID)
	public String id;
	/**
	 * 时间
	 */
	public static final String DATETIME = "date_time";
	@Column(column = DATETIME)
	public String date_time;
	/**
	 * 应用
	 */
	public static final String APPLICATION = "application";
	@Column(column = APPLICATION)
	public String application;
	/**
	 * 版本号
	 */
	public static final String VERSION_CODE = "version_code";
	@Column(column = VERSION_CODE)
	public String version_code;
	/**
	 * 版本名称
	 */
	public static final String VERSION_NAME = "version_name";
	@Column(column = VERSION_NAME)
	public String version_name;
	public static final String BOARD = "board";
	@Column(column = BOARD)
	public String board;
	public static final String CPU_ABI = "cpu_abi";
	@Column(column = CPU_ABI)
	public String cpu_abi;
	public static final String CPU_ABI2 = "cpu_abi2";
	@Column(column = CPU_ABI2)
	public String cpu_abi2;
	public static final String DEVICE = "device";
	@Column(column = DEVICE)
	public String device;
	public static final String MANUFACTURER = "manufacturer";
	@Column(column = MANUFACTURER)
	public String manufacturer;
	public static final String PRODUCT = "product";
	@Column(column = PRODUCT)
	public String product;
	public static final String USER = "user";
	@Column(column = USER)
	public String user;
	public static final String USER_PHONE = "user_phone";
	@Column(column = USER_PHONE)
	public String user_phone;
	public static final String EXCEPTION = "exception";
	@Column(column = EXCEPTION)
	public String exception;
	public static final String EXCEPTION_DESC = "exception_desc";
	@Column(column = EXCEPTION_DESC)
	public String exception_desc;
	public static final String IS_UPLOAD = "is_upload";
	@Column(column = IS_UPLOAD)
	public String is_upload = "N";

	public ErrorLogBean() {

	}
}
