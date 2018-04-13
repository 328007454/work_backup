package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 工器具台账信息
 * Created by han on 2017/6/29.
 */
@Table(name = "gqj_info")
public class SafeToolsInfor {
    // 报告ID
    public static final String ID = "id";
    @Id(column = ID)
    public String id;
    //工器具编号
    public static final String NUM = "num";
    @Column(column = NUM)
    public String num;

    /**
     * 类型
     */
    public static final String TYPE = "type";
    @Column(column = TYPE)
    public String type;

    //工器具名字
    public static final String NAME = "name";
    @Column(column = NAME)
    public String name;

    /**
     * 变电站ID
     */
    public static final String BDZID = "bdz_id";
    @Column(column = BDZID)
    public String bdzId;

    /**
     * 变电站名称
     */
    public static final String BDZNAME = "bdz_name";
    @Column(column = BDZNAME)
    public String bdzName;

    //简称
    public static final String SHORTNAME = "short_name";
    @Column(column = SHORTNAME)
    public String shorName;

    //拼音搜索
    public static final String PINYIN = "name_pinyin";
    @Column(column = PINYIN)
    public String pinYin;

    public static final String MODEL = "model";
    @Column(column = MODEL)
    public String model;

    public static final String DEPTID = "dept_id";
    @Column(column = DEPTID)
    public String deptID;

    public static final String DEPTNAME = "dept_name";
    @Column(column = DEPTNAME)
    public String deptName;


    public static final String PRODUCETIME = "produce_time";
    @Column(column = PRODUCETIME)
    public String produceTime;


    public static final String PRODUCEFACTORY = "produce_factory";
    @Column(column = PRODUCEFACTORY)
    public String produceFactory;


    public static final String CREATTIME = "create_time";
    @Column(column = CREATTIME)
    public String createTime;


    public static final String ISNORMAL = "isnormal";
    @Column(column = ISNORMAL)
    public String isNormal;

    public static final String PERIOD = "period";
    @Column(column = PERIOD)
    public String period;

    public static final String LAST_CHECK_TIME = "lastly_check_time";
    @Column(column = LAST_CHECK_TIME)
    public String LastCheckTime;

    public static final String REARK = "remark";
    @Column(column = REARK)
    public String remark;

    public static final String NEXTCHECKTIME = "next_check_time";
    @Column(column = NEXTCHECKTIME)
    public String nextCheckTime;

    public static final String STATUS = "status";
    @Column(column = STATUS)
    public String status;

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


    public static final String LOCATION = "location";
    @Column(column = LOCATION)
    public String location;

    public static final String UNUSEINFO = "unuse_info";
    @Column(column = UNUSEINFO)
    public String unUseInfo;


}
