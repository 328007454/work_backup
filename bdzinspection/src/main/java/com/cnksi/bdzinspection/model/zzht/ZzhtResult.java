package com.cnksi.bdzinspection.model.zzht;

import android.text.TextUtils;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;

import java.util.UUID;

/**
 * Created by kkk on 2018/3/8.
 */

@Table(name = "report_zzht_result")
public class ZzhtResult {

    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id = UUID.randomUUID().toString();


    public static final String ZZHT_ID = "zzht_id";
    @Column(name = ZZHT_ID)
    public String zzhtID;


    public static final String PID = "pid";
    @Column(name = PID)
    public String pid;

    public static final String REPORTID = "reportid";
    @Column(name = REPORTID)
    public String reportID;


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

    public static final String COPY_VALUE = "copy_value";
    @Column(name = COPY_VALUE)
    public String copyValue;

    public static final String CONFIRM_TIME = "confirm_time";
    @Column(name = CONFIRM_TIME)
    public String confirmTime;


    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;


    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt="0";

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

    public ZzhtResult(DbModel model, String reportId) {
        if (!TextUtils.isEmpty(model.getString("rid"))) {
            this.id = model.getString("rid");
            updateTime = DateUtils.getCurrentLongTime();
        } else {
            insertTime = DateUtils.getCurrentLongTime();
            model.add("rid", id);
        }
        this.zzhtID = model.getString("id");
        pid = model.getString("pid");
        reportID = reportId;
        bdzID = model.getString("bdzid");
        bdzName = model.getString("bdz_name");
        level = model.getString("level");
        xsbw = model.getString("xsbw");
        description = model.getString("description");
        unit = model.getString("unit");
        isCopy = model.getString("is_copy");
        confirmTime = model.getString("confirm_time");
        copyValue = model.getString("copy_value");
        sort = model.getString("sort");
    }
}
