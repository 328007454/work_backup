package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

import java.io.Serializable;

/**
 * 工艺流程
 *
 * @author Oliver
 *
 */
@Table(name = "process")
public class Process extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// id
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	public static final String PRO_ID = "pro_id";
	@Column(name = PRO_ID)
	public String pro_id;

	public static final String CONTENT = "content";
	@Column(name = CONTENT)
	public String content;

	public static final String MAYOCCUR = "mayoccur";
	@Column(name = MAYOCCUR)
	public String mayoccur;

	public static final String IS_CONFIRM = "is_confirm";
	@Column(name = IS_CONFIRM)
	public String is_confirm;

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
