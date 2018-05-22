package com.cnksi.bdzinspection.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

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
	@Column(name = ID,isId = true)
	public String id;

	public static final String PRO_ID = "pro_id";
	@Column(name = PRO_ID)
	public String pro_id;

	// 名称
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name;

	// 数量
	public static final String AMOUNT = "amount";
	@Column(name = AMOUNT)
	public String amount;

	public static final String IS_CONFIRM = "is_confirm";
	@Column(name = IS_CONFIRM)
	public String is_confirm;

	public static final String BZ = "bz";
	@Column(name = BZ)
	public String bz;

	// 插入时间
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;

	public final static String LAST_MODIFY_TIME = "last_modify_time";
	@Column(name = LAST_MODIFY_TIME)
	public String last_modify_time;

	// 逻辑删除
	public final static String ENABLED = "enabled";
	@Column(name = ENABLED)
	public String enabled;

}