package com.cnksi.sjjc.bean;

import android.annotation.SuppressLint;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Table(name = "task")
public class Task extends BaseModel{

    // 任务编号
    public static final String TASKID = "taskid";
    @Column(name = TASKID, isId = true)
    public String taskid;

    // 巡检类型
    public static final String INSPECTION = "inspection";
    @Column(name = INSPECTION)
    public String inspection;

    // 变电站ID
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    // 变电站名称
    public static final String BDZNAME = "bdzname";
    @Column(name = BDZNAME)
    public String bdzname;

    // 计划巡视日期
    public static final String SCHEDULE_TIME = "schedule_time";
    @Column(name = SCHEDULE_TIME)
    public String schedule_time;

    // 计划完成日期
    public static final String SCHEDULE_ENDTIME = "schedule_endtime";
    @Column(name = SCHEDULE_ENDTIME)
    public String schedule_endtime;

    // 计划状态 undo=未开始，doing=巡视中，done=已完成
    public static final String STATUS = "STATUS";
    @Column(name = STATUS)
    public String status;

    // 巡检类型名称
    public static final String INSPECTION_NAME = "inspection_name";
    @Column(name = INSPECTION_NAME)
    public String inspection_name;

    // 是否是运维一体化
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    public boolean hasNewDefect = false;
    public String isUpload = "Y";
    public String remark;

    public Task() {
    }

    public Task(String taskid) {
        this.taskid = taskid;
        this.status = TaskStatus.done.name();
    }

    public Task(String taskid, String bdzid, String bdzname, String inspection, String inspection_name, String schedule_time, String status) {
        this.taskid = taskid;
        this.inspection = inspection;
        this.bdzid = bdzid;
        this.bdzname = bdzname;
        this.schedule_time = schedule_time;
        this.status = status;
        this.inspection_name = inspection_name;
    }

    /**
     * 该计划是否完成，true =完成
     *
     * @return
     */
    public boolean isFinish() {
        return status != null && status.equals("done");
    }

    /**
     * 获取计划时间
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public Date getScheduleTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(schedule_time);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    /**
     * 任务状态
     */
    public static enum TaskStatus {
        undo("未巡视"), doing("巡视中"), done("已完成");
        private final String value;

        TaskStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
