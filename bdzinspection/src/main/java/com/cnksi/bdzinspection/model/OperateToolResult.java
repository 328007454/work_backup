package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * 工器具操作结果表
 * Created by han on 2017/6/29.
 */
@Table(name = "gqj_operation_info")
public class OperateToolResult {
    // 报告ID
    public static final String ID = "id";
    @Id(column = ID)
    public String id;

    public static final String GQJ_ID = "gqj_id";
    @Column(column = GQJ_ID)
    public String gqjId;

    public static final String BDZID = "bdz_id";
    @Column(column = BDZID)
    public String bdzId;

    public static final String BDZNAME = "bdz_name";
    @Column(column = BDZNAME)
    public String bdzName;

    public static final String REPORTID = "report_id";
    @Column(column = REPORTID)
    public String reportId;


    public static final String GQJ_NAME = "gqj_name";
    @Column(column = GQJ_NAME)
    public String gqjName;


    public static final String OPERATION = "operation";
    @Column(column = OPERATION)
    public String operation;

    public static final String OPER_TIME = "oper_time";
    @Column(column = OPER_TIME)
    public String operTime;

    public static final String RESULT = "result";
    @Column(column = RESULT)
    public String result;

    public static final String DESCRIPTION = "description";
    @Column(column = DESCRIPTION)
    public String description;

    public static final String PERSONS = "persons";
    @Column(column = PERSONS)
    public String persons;

    public static final String PIC = "pic";
    @Column(column = PIC)
    public String pic;

    public static final String DLT = "dlt";
    @Column(column = DLT)
    public int dlt;

    public static final String LASTMODIFYTIME = "last_modify_time";
    @Column(column = LASTMODIFYTIME)
    public String lastModifyTime;

    public static final String INSERTTIME = "insert_time";
    @Column(column = INSERTTIME)
    public String insertTime;

    public static final String UPDATETIME = "update_time";
    @Column(column = UPDATETIME)
    public String updateTime;

    public static final String CREATETIME = "create_time";
    @Column(column = CREATETIME)
    public String creatTime;


    public OperateToolResult() {
    }

    public OperateToolResult(String reportId, String bdzName, String bdzId, String id, String name, String time, String result, String pics, String person, String operation) {
        this.id = UUID.randomUUID().toString();
        this.gqjName = name;
        this.gqjId = id;
        this.operation = operation;
        this.operTime = time;
        this.result = result;
        this.pic = pics;
        this.persons = person;
        this.creatTime = DateUtils.getCurrentLongTime();
        this.dlt = 0;
        this.reportId = reportId;
        this.bdzId = bdzId;
        this.bdzName = bdzName;
    }

    public OperateToolResult(String reportId, String bdzName, String bdzId, String toolId, String name, String operation,String result,String reason,String stopPerson) {
        this.id = UUID.randomUUID().toString();
        this.gqjName = name;
        this.operation = operation;
        this.gqjId = toolId;
        this.dlt = 0;
        this.creatTime = DateUtils.getCurrentLongTime();
        this.result = result;
        this.reportId = reportId;
        this.bdzId = bdzId;
        this.bdzName = bdzName;
        this.description = reason;
        this.persons = stopPerson;
    }
}
