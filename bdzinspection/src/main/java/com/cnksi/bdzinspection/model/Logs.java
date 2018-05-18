package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by han on 2017/5/15.
 */
@Table(name = "logs")
public class Logs {

    @Column(name = "id",isId = true)
    public String id;

    public static final String DEVICEID = "device_id";
    @Column(name = DEVICEID)
    public String deviceId;

    public static final String EDIT_FROM = "edit_from";
    @Column(name = EDIT_FROM)
    public String editForm;

    /**
     * add update delte
     */
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    public static final String USER_ID = "user_id";
    @Column(name = USER_ID)
    public String userId;

    public static final String USER_NAME = "user_name";
    @Column(name = USER_NAME)
    public String userName;

    public static final String DEVICE_NAME = "device_name";
    @Column(name = DEVICE_NAME)
    public String deviceName;

    public static final String CONTENT = "content";
    @Column(name = CONTENT)
    public String content;

    public static final String CREATE_TIME = "create_time";
    @Column(name = CREATE_TIME)
    public String createTime;

    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String lastModifyTime;

    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt;

    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    public void setCurrentMessage(String currentDeviceId, String currentDeviceName, String currentPersonId, String currentUserName, String modify, String copyItem) {
        this.createTime = DateUtils.getCurrentLongTime();
        this.dlt = 0;
        this.id = UUID.randomUUID().toString();
        this.deviceId = currentDeviceId;
        this.deviceName = currentDeviceName;
        this.userId = currentPersonId;
        this.userName = currentUserName;
        this.editForm = copyItem;
        this.type = modify;
        this.lastModifyTime = DateUtils.getCurrentLongTime();
    }
}
