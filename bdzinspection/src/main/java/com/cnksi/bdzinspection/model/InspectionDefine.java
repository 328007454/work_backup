package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by kkk on 2018/2/22.
 */

@Table(name = "lookup_local")
public class InspectionDefine {

    public static final String ID = "id";
    @Id(column = "id")
    public String id;

    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bzdid;

    public static final String  K = "k";
    @Column(column = K)
    public String k ;

    public static final String V="v";
    @Column(column = V)
    public String v;

    public static final String SORT = "sort";
    @Column(column = SORT)
    public String sort;

    public static final String  XJNR = "xjnr";
    @Column(column = XJNR)
    public String xjnr ;

    public static final String TYPE="type";
    @Column(column = TYPE)
    public String type;

    public static final String REMARK = "remark";
    @Column(column = REMARK)
    public String remark;

    public static final String  INSERTTIME = "insert_time";
    @Column(column = INSERTTIME)
    public String insertTime ;

    public static final String UPDATETIME="update_time";
    @Column(column = UPDATETIME)
    public String updateTime;

    public static final String  LASTMODIFYTIME = "last_modify_time";
    @Column(column = LASTMODIFYTIME)
    public String lastModifyTime ;

    public static final String DLT="dlt";
    @Column(column = DLT)
    public String dlt;
}
