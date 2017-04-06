package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;


@Table(name = "task_extend")

public class TaskExtend extends BaseModel {

    // ID
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();

    // 任务编号
    public static final String TASK_ID = "taskid";
    @Column(name = TASK_ID)
    public String taskId;

    // 是否普测
    public static final String SBJC_IS_ALL_CHECK = "sbjc_is_all_check";
    @Column(name = SBJC_IS_ALL_CHECK)
    public int sbjcIsAllCheck;

    // 内阻测试
    public static final String SBJC_RESISTANCE_CHECK = "sbjc_resistance_check";
    @Column(name = SBJC_RESISTANCE_CHECK)
    public int sbjcResistanceCheck;

    // 压力测试类
    public static final String SBJC_STRESS_TYPE = "sbjc_stress_type";
    @Column(name = SBJC_STRESS_TYPE)
    public String sbjcStressType;

    public TaskExtend() {

    }

    public TaskExtend(String taskId) {
        this.taskId = taskId;
    }
}


