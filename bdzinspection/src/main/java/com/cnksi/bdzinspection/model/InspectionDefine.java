package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by kkk on 2018/2/22.
 */

@Table(name = "lookup_local")
public class InspectionDefine {

    public static final String ID = "id";
    @Column(name = "id",isId = true)
    public String id;

    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bzdid;

    public static final String  K = "k";
    @Column(name = K)
    public String k ;

    public static final String V="v";
    @Column(name = V)
    public String v;

    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;

    public static final String  XJNR = "xjnr";
    @Column(name = XJNR)
    public String xjnr ;

    public static final String TYPE="type";
    @Column(name = TYPE)
    public String type;

    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    public static final String  INSERTTIME = "insert_time";
    @Column(name = INSERTTIME)
    public String insertTime ;

    public static final String UPDATETIME="update_time";
    @Column(name = UPDATETIME)
    public String updateTime;

    public static final String  LASTMODIFYTIME = "last_modify_time";
    @Column(name = LASTMODIFYTIME)
    public String lastModifyTime ;

    public static final String DLT="dlt";
    @Column(name = DLT)
    public String dlt;
}
