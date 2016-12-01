package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 验收报告
 */
@Table(name = "accept_report")
public class AcceptReport {
    /**
     * 报告Id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID, isId = true)
    public String reportId = UUID.randomUUID().toString();
    /**
     * 设备Id
     */
    public static final String DEVICE_ID = "device_id";
    @Column(name = DEVICE_ID)
    public String deviceId;

    /**
     * 验收类型ID
     */
    public static final String ACCEPT_TYPE_ID = "accept_type_id";
    @Column(name = ACCEPT_TYPE_ID)
    public String acceptTypeId;

    /**
     * 验收类型名称
     */
    public static final String ACCEPT_TYPE_NAME = "accept_type_name";
    @Column(name = ACCEPT_TYPE_NAME)
    public String acceptTypeName;

    /**
     * 验收标准卡ID
     */
    public static final String ACCEPT_STANDER_CARD_ID = "accept_stander_card_id";
    @Column(name = ACCEPT_STANDER_CARD_ID)
    public String acceptStanderCardId;

    /**
     * 验收标准卡名称
     */
    public static final String ACCEPT_STANDER_CARD_NAME = "accept_stander_card_name";
    @Column(name = ACCEPT_STANDER_CARD_NAME)
    public String acceptStanderCardName;

    /**
     * 验收标准卡填写的基本内容
     */
    public static final String ACCEPT_STANDER_CARD_BASE = "stander_card_base_info";
    @Column(name = ACCEPT_STANDER_CARD_BASE)
    public String standerCardBaseInfo;


    public class StanderBaseInfo implements Serializable {
        /**
         * 基本内容key
         */
        public String name;
        /**
         * 基本内容类型
         */
        public String type;
        /**
         * 基本内容value
         */
        public String value;
    }


}
