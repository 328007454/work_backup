package com.cnksi.bdzinspection.model.zzht;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by kkk on 2018/3/8.
 */
@Table(name = "report_zzht")
public class Zzht {

    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id;

    public static final String PID = "pid";
    @Column(name = PID)
    public String pid;


    public static final String DEPTID = "deptid";
    @Column(name = DEPTID)
    public String deptID;


    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzID;


    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bdzName;

    public static final String LEVEL = "level";
    @Column(name = LEVEL)
    public String level;


    public static final String XSBW = "xsbw";
    @Column(name = XSBW)
    public String xsbw;


    public static final String DESCRIPTION = "description";
    @Column(name = DESCRIPTION)
    public String description;


    public static final String UNIT = "unit";
    @Column(name = UNIT)
    public String unit;

    public static final String IS_COPY = "is_copy";
    @Column(name = IS_COPY)
    public String isCopy;


    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;


    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt;


    public static final String INSERT_TIME = "insert_time";
    @Column(name = INSERT_TIME)
    public String insertTime;


    public static final String UPDATE_TIME = "update_time";
    @Column(name = UPDATE_TIME)
    public String updateTime;


    //更新时间
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String lastModifyTime;
}
