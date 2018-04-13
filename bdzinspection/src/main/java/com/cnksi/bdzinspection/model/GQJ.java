package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 工器具
 * 
 * @author Oliver
 *
 */
@Table(name = "gqj")
public class GQJ extends BaseModel{

	// 工器具id
	public static final String ID = "id";
	@Id(column = ID)
	public String id;

	public static final String PRO_ID = "pro_id";
	@Column(column = PRO_ID)
	public String pro_id;

	// 名称
	public static final String NAME = "name";
	@Column(column = NAME)
	public String name;

	// 数量
	public static final String AMOUNT = "amount";
	@Column(column = AMOUNT)
	public String amount;

	public static final String IS_CONFIRM = "is_confirm";
	@Column(column = IS_CONFIRM)
	public String is_confirm;

	public static final String BZ = "bz";
	@Column(column = BZ)
	public String bz;

	// 插入时间
	public static final String INSERT_TIME = "insert_time";
	@Column(column = INSERT_TIME)
	public String insert_time;

	public final static String LAST_MODIFY_TIME = "last_modify_time";
	@Column(column = LAST_MODIFY_TIME)
	public String last_modify_time;

	// 逻辑删除
	public final static String ENABLED = "enabled";
	@Column(column = ENABLED)
	public String enabled;

}