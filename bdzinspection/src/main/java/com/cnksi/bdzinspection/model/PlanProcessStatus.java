package com.cnksi.bdzinspection.model;



import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

import java.io.Serializable;

/**
 * 工艺流程表
 *
 * @author Oliver
 *
 */
@Table(name = "plan_process_status")
public class PlanProcessStatus extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 工器具id
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	/**
	 * 计划id
	 */
	public static final String TASK_ID = "task_id";
	@Column(name = TASK_ID)
	public String task_id;

	/**
	 * 工艺流程Id
	 */
	public static final String PROCESS_ID = "process_id";
	@Column(name = PROCESS_ID)
	public String process_id;
	/**
	 * 选中状态
	 */
	public static final String IS_SELETED = "is_seleted";
	@Column(name = IS_SELETED)
	public String is_selected;

	/**
	 * 视频
	 */
	public static final String VIDEO = "video";
	@Column(name = VIDEO)
	public String video;

	/**
	 * 音频
	 */
	public static final String AUDIO = "audio";
	@Column(name = AUDIO)
	public String audio;
	/**
	 * 选中状态
	 */
	public static final String PICTURE = "picture";
	@Column(name = PICTURE)
	public String picture;

	/**
	 * @param task_id
	 * @param process_id
	 * @param is_selected
	 */
	public PlanProcessStatus(String task_id, String process_id, String is_selected) {
		super();
		this.id = task_id + process_id;
		this.task_id = task_id;
		this.process_id = process_id;
		this.is_selected = is_selected;
	}

	/**
	 *
	 */
	public PlanProcessStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

}