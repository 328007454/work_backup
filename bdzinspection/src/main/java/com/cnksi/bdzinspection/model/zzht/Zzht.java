package com.cnksi.bdzinspection.model.zzht;


import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by kkk on 2018/3/8.
 */
@Table(name = "report_zzht")
public class Zzht {

    public static final String ID = "id";
    @Id(column = ID)
    public String id;

    public static final String PID = "pid";
    @Column(column = PID)
    public String pid;


    public static final String DEPTID = "deptid";
    @Column(column = DEPTID)
    public String deptID;


    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzID;


    public static final String BDZ_NAME = "bdz_name";
    @Column(column = BDZ_NAME)
    public String bdzName;

    public static final String LEVEL = "level";
    @Column(column = LEVEL)
    public String level;


    public static final String XSBW = "xsbw";
    @Column(column = XSBW)
    public String xsbw;


    public static final String DESCRIPTION = "description";
    @Column(column = DESCRIPTION)
    public String description;


    public static final String UNIT = "unit";
    @Column(column = UNIT)
    public String unit;

    public static final String IS_COPY = "is_copy";
    @Column(column = IS_COPY)
    public String isCopy;


    public static final String SORT = "sort";
    @Column(column = SORT)
    public String sort;


    public static final String REMARK = "remark";
    @Column(column = REMARK)
    public String remark;

    public static final String DLT = "dlt";
    @Column(column = DLT)
    public String dlt;


    public static final String INSERT_TIME = "insert_time";
    @Column(column = INSERT_TIME)
    public String insertTime;


    public static final String UPDATE_TIME = "update_time";
    @Column(column = UPDATE_TIME)
    public String updateTime;


    //更新时间
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(column = LAST_MODIFY_TIME)
    public String lastModifyTime;
}
