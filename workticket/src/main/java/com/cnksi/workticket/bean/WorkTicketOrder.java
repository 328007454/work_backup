package com.cnksi.workticket.bean;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.db.WorkTicketDbManager;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by Mr.K on 2018/5/7.
 */

@Table(name = "workticket_order")
public class WorkTicketOrder {

    /**
     * ID key
     */
    @Column(name = "id", isId = true)
    public String id = UUID.randomUUID().toString();

    /**
     * 部门id；
     */
    public static final String DEPT_ID = "dept_id";
    @Column(name = DEPT_ID)
    public String deptId;

    /**
     * 变电站id
     */
    public static final String BDZ_ID = "bdz_id";
    @Column(name = BDZ_ID)
    public String bdzId;

    /**
     * 变电站名称
     */
    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bdzName;


    /**
     * 工作类型
     */
    public static final String WORK_TYPE = "work_type";
    @Column(name = WORK_TYPE)
    public String workType;


    /**
     * 工作单位
     */
    public static final String WORK_UNIT = "work_unit";
    @Column(name = WORK_UNIT)
    public String workUnit;

    /**
     * 是否时外来班组
     */
    public static final String IS_OTHER_UNIT = "is_other_unit";
    @Column(name = IS_OTHER_UNIT)
    public String isOtherUnit;


    /**
     * 负责人
     */
    public static final String CHARGE_PERSON = "charge_person";
    @Column(name = CHARGE_PERSON)
    public String chargePerson;


    /**
     * 联系方式
     */
    public static final String PHONE_NUMBER = "phonenumber";
    @Column(name = PHONE_NUMBER)
    public String phoneNum;


    /**
     * 工作内容
     */
    public static final String CONTENT = "content";
    @Column(name = CONTENT)
    public String content;


    /**
     * ticket_type
     */
    public static final String TICKET_TYPE = "ticket_type";
    @Column(name = TICKET_TYPE)
    public String ticketType;


    /**
     * 工作时间
     */
    public static final String WORK_DATE = "work_date";
    @Column(name = WORK_DATE)
    public String workDate;


    /**
     * 时间段
     */
    public static final String WORK_REGION_KEY = "work_region_key";
    @Column(name = WORK_REGION_KEY)
    public String workKey;


    /**
     * 时间段
     */
    public static final String WORK_REGION_VAL = "work_region_val";
    @Column(name = WORK_REGION_VAL)
    public String workVal;

    /**
     * 工作票状态  normal:正常，conflict冲突
     */
    public static final String TICKET_STATUS = "ticket_statu";
    @Column(name = TICKET_STATUS)
    public String ticketStatus;


    /**
     * 创建人账号
     */
    public static final String CREATE_PERSON_ACCOUNT = "create_person_account";
    @Column(name = CREATE_PERSON_ACCOUNT)
    public String createAccount;


    /**
     * 创建人名字
     */
    public static final String CREATE_PERSON = "create_person";
    @Column(name = CREATE_PERSON)
    public String createPerson;


    /**
     * 是否时外来班组
     */
    public static final String UPDATE_TIME = "update_time";
    @Column(name = UPDATE_TIME)
    public String updateTime;


    /**
     * 是否时外来班组
     */
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt;


    /**
     * 是否时外来班组
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String lastModifyTime;

    public WorkTicketOrder(){}

    public WorkTicketOrder(String deptID, String bdzId, String bdzName, String selectType, String deptName, String s, String s1, String s2,
                           String ticketType, String selectDate, String selectTimeZoneKey, String seletTimeZone, String userAccount, String userName) {
        this.deptId = deptID;
        this.bdzId = bdzId;
        this.bdzName = bdzName;
        this.workType = selectType;
        this.workUnit = deptName;
        this.chargePerson = s;
        this.phoneNum = s1;
        this.content = s2;
        this.ticketType = ticketType;
        this.workDate = selectDate +" "+seletTimeZone.substring(seletTimeZone.length()-5,seletTimeZone.length())+":00" ;
        this.workKey = selectTimeZoneKey;
        this.workVal = seletTimeZone;
        this.createAccount = userAccount;
        this.createPerson = userName;
        this.updateTime = DateUtils.getCurrentLongTime();
        this.dlt = "0";
        this.lastModifyTime = DateUtils.getCurrentLongTime();
    }
}
