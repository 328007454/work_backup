package com.cnksi.bdzinspection.utils;

import java.util.ArrayList;

/**
 * 操作票
 *
 */
public class OperateTick {

	public final static String pageSplit = "第   页";
	public final static String startSplit = "（√）";
	public final static String endSplit = "备注：";
	public final static String unitStartSplits="单位：";
	public final static String unitEndStartSplits="编号：";
	public final static String codeStartSplit="编号：";
	public final static String codeEndSplit="发令人            受令人           发令时间          年  月      时";
	public final static String taskStartSplit="操作类型：      （ ）监护下操作     （ ）单人操作     （ ）检修人员操作";
	public final static String taskEndSplit="操作确认";
	public final static String taskSplit="操作任务：";
	

	//单位
	private String unit;
	//编码
	private String code;
	//任务名称
	private String taskName;
	
	private ArrayList<String> opears;

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public ArrayList<String> getOpears() {
		return opears;
	}

	public void setOpears(ArrayList<String> opears) {
		this.opears = opears;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append("\n任务："+getTaskName());
		result.append("\n单位："+getUnit());
		result.append("\n编码："+getCode());
		result.append("\n操作：");

		int i = 1 ;
		for (String opear : opears) {
			result.append("\n\t"+i+"、"+opear);
			i++;
		}
		
		return result.toString();
	}

}
