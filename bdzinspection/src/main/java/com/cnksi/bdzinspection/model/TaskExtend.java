package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "task_extend")
public class TaskExtend extends BaseModel {

    // ID
    public static final String ID = "id";
    @Id(column = ID)
    public String id = UUID.randomUUID().toString();

    // 任务编号
    public static final String TASK_ID = "taskid";
    @Column(column = TASK_ID)
    public String taskId;

    // 是否普测
    public static final String SBJC_IS_ALL_CHECK = "sbjc_is_all_check";
    @Column(column = SBJC_IS_ALL_CHECK)
    public int sbjcIsAllCheck;

    // 内阻测试
    public static final String SBJC_RESISTANCE_CHECK = "sbjc_resistance_check";
    @Column(column = SBJC_RESISTANCE_CHECK)
    public int sbjcResistanceCheck;

    // 压力测试类
    public static final String SBJC_STRESS_TYPE = "sbjc_stress_type";
    @Column(column = SBJC_STRESS_TYPE)
    public String sbjcStressType;

    public TaskExtend() {

    }

    public TaskExtend(String taskId) {
        this.taskId = taskId;
    }
}
