package com.cnksi.xscore.xsutils.crash;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

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
	@Column(name = ID, isId = true)
	public String id;
	/**
	 * 时间
	 */
	public static final String DATETIME = "date_time";
	@Column(name = DATETIME)
	public String date_time;
	/**
	 * 应用
	 */
	public static final String APPLICATION = "application";
	@Column(name = APPLICATION)
	public String application;
	/**
	 * 版本号
	 */
	public static final String VERSION_CODE = "version_code";
	@Column(name = VERSION_CODE)
	public String version_code;
	/**
	 * 版本名称
	 */
	public static final String VERSION_NAME = "version_name";
	@Column(name = VERSION_NAME)
	public String version_name;
	public static final String BOARD = "board";
	@Column(name = BOARD)
	public String board;
	public static final String CPU_ABI = "cpu_abi";
	@Column(name = CPU_ABI)
	public String cpu_abi;
	public static final String CPU_ABI2 = "cpu_abi2";
	@Column(name = CPU_ABI2)
	public String cpu_abi2;
	public static final String DEVICE = "device";
	@Column(name = DEVICE)
	public String device;
	public static final String MANUFACTURER = "manufacturer";
	@Column(name = MANUFACTURER)
	public String manufacturer;
	public static final String PRODUCT = "product";
	@Column(name = PRODUCT)
	public String product;
	public static final String USER = "user";
	@Column(name = USER)
	public String user;
	public static final String USER_PHONE = "user_phone";
	@Column(name = USER_PHONE)
	public String user_phone;
	public static final String EXCEPTION = "exception";
	@Column(name = EXCEPTION)
	public String exception;
	public static final String EXCEPTION_DESC = "exception_desc";
	@Column(name = EXCEPTION_DESC)
	public String exception_desc;
	public static final String IS_UPLOAD = "is_upload";
	@Column(name = IS_UPLOAD)
	public String is_upload = "N";

	public ErrorLogBean() {

	}
}
