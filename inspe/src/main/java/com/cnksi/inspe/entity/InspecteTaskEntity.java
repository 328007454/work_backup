package com.cnksi.inspe.entity;

import com.cnksi.inspe.base.BaseEntity;

/**
 * 检查任务实体类
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 00:31
 */

public class InspecteTaskEntity extends BaseEntity {
    /**
     * 检查任务ID
     */
    public String id;
    /**
     * 检查班组ID
     */
    public String teamId;
    /**
     * 检查班组名称
     */
    public String teamName;
    /**
     * 任务类型
     */
    public int taskType;
    /**
     * 任务类型名称
     */
    public String taskTypeName;
    /**
     * 完成日期
     */
    public long targetDate;
    /**
     * 执行人
     */
    public String[] executors;

    public InspecteTaskEntity(String id, String teamId, String teamName, int taskType, String taskTypeName, long targetDate, String[] executors) {
        this.id = id;
        this.teamId = teamId;
        this.teamName = teamName;
        this.taskType = taskType;
        this.taskTypeName = taskTypeName;
        this.targetDate = targetDate;
        this.executors = executors;
    }

    public String toExecutorsString() {
        StringBuffer sb = new StringBuffer();
        for (String persion : executors) {
            sb.append(" ").append(persion);
        }

        return sb.toString();
    }
}
