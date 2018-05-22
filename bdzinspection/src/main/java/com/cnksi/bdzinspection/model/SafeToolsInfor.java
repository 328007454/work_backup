package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

/**
 * 工器具台账信息
 * Created by han on 2017/6/29.
 */
@Table(name = "gqj_info")
public class SafeToolsInfor {
    // 报告ID
    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id;
    //工器具编号
    public static final String NUM = "num";
    @Column(name = NUM)
    public String num;

    /**
     * 类型
     */
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    //工器具名字
    public static final String NAME = "name";
    @Column(name = NAME)
    public String name;

    /**
     * 变电站ID
     */
    public static final String BDZID = "bdz_id";
    @Column(name = BDZID)
    public String bdzId;

    /**
     * 变电站名称
     */
    public static final String BDZNAME = "bdz_name";
    @Column(name = BDZNAME)
    public String bdzName;

    //简称
    public static final String SHORTNAME = "short_name";
    @Column(name = SHORTNAME)
    public String shorName;

    //拼音搜索
    public static final String PINYIN = "name_pinyin";
    @Column(name = PINYIN)
    public String pinYin;

    public static final String MODEL = "model";
    @Column(name = MODEL)
    public String model;

    public static final String DEPTID = "dept_id";
    @Column(name = DEPTID)
    public String deptID;

    public static final String DEPTNAME = "dept_name";
    @Column(name = DEPTNAME)
    public String deptName;


    public static final String PRODUCETIME = "produce_time";
    @Column(name = PRODUCETIME)
    public String produceTime;


    public static final String PRODUCEFACTORY = "produce_factory";
    @Column(name = PRODUCEFACTORY)
    public String produceFactory;


    public static final String CREATTIME = "create_time";
    @Column(name = CREATTIME)
    public String createTime;


    public static final String ISNORMAL = "isnormal";
    @Column(name = ISNORMAL)
    public String isNormal;

    public static final String PERIOD = "period";
    @Column(name = PERIOD)
    public String period;

    public static final String LAST_CHECK_TIME = "lastly_check_time";
    @Column(name = LAST_CHECK_TIME)
    public String LastCheckTime;

    public static final String REARK = "remark";
    @Column(name = REARK)
    public String remark;

    public static final String NEXTCHECKTIME = "next_check_time";
    @Column(name = NEXTCHECKTIME)
    public String nextCheckTime;

    public static final String STATUS = "status";
    @Column(name = STATUS)
    public String status;

    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt;

    public static final String LASTMODIFYTIME = "last_modify_time";
    @Column(name = LASTMODIFYTIME)
    public String lastModifyTime;

    public static final String INSERTTIME = "insert_time";
    @Column(name = INSERTTIME)
    public String insertTime;

    public static final String UPDATETIME = "update_time";
    @Column(name = UPDATETIME)
    public String updateTime;


    public static final String LOCATION = "location";
    @Column(name = LOCATION)
    public String location;

    public static final String UNUSEINFO = "unuse_info";
    @Column(name = UNUSEINFO)
    public String unUseInfo;


}
