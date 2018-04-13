package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by kkk on 2018/1/4.
 */

@Table(name = "inspection_prepared")
public class InspectionPrepared {

    //
    public static final String ID = "id";
    //
    public static final String REPORTID = "reportid";
    public static final String TASKID = "taskid";
    //
    public static final String DLT = "dlt";
    //
    public static final String ACCOUNT = "account";
    //
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Id(column = ID)
    public String id = UUID.randomUUID().toString();
    @Column(column = REPORTID)
    public String reportID;
    @Column(column = TASKID)
    public String taskID;
    @Column(column = DLT)
    public int dlt;
    @Column(column = ACCOUNT)
    public String account;
    @Column(column = LAST_MODIFY_TIME)
    public String lastModifyTime;

    public InspectionPrepared() {
    }

    public InspectionPrepared(String reportID, String taskID, String account) {
        this.reportID = reportID;
        this.taskID = taskID;
        dlt = 0;
        lastModifyTime = DateUtils.getCurrentLongTime();
        this.account = account;
    }

    public boolean contains(String[] us) {
        if (TextUtils.isEmpty(account)) return false;
        for (String s : us) {
            if (account.contains(s)) return true;
        }
        return false;
    }
}
