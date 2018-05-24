package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by kkk on 2018/1/4.
 */
@Table(name = "spacing_lastly")
public class SpacingLastly {
    //
    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id = UUID.randomUUID().toString();

    //
    public static final String REPORTID = "reportid";
    @Column(name = REPORTID)
    public String reportID;

    public static final String SPID = "spid";
    @Column(name = SPID)
    public String spid;

    //
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt;

    //
    public static final String ACCOUNT = "account";
    @Column(name = ACCOUNT)
    public String account;

    //
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String lastModifyTime;

    //
    public static final String DEVICE_TYPE = "devicetype";
    @Column(name = DEVICE_TYPE)
    public String deviceType;

    public SpacingLastly() {
    }

    public SpacingLastly(String id, String accounts, String spid, String model) {
        this.reportID = id;
        this.account = accounts;
        this.spid = spid;
        this.deviceType = model;
    }
}
