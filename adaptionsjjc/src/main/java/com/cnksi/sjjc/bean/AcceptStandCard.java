package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * 验收标准卡
 */
@Table(name = "accept_stander_card")
public class AcceptStandCard {
    /**
     * 标准卡ID
     */
    @Column(name = "accept_stander_card_id", isId = true)
    public String acceptStanderCardId = UUID.randomUUID().toString();

    /**
     * 标准卡类型
     */
    public static final String ACCEPT_TYPE_ID="accept_type_id";
    @Column(name =ACCEPT_TYPE_ID)
    public String acceptTypeId;

    /**
     * 标准卡名称
     */
    @Column(name = "accept_stander_card_name")
    public String acceptStanderCardName;

    /**
     * 标准卡基本信息（根据标准卡不同而不同）采用json存储
     * {
     * "工程名称": "String",
     * "设计单位": "String",
     * "验收单位": "String",
     * "验收日期": "Date",
     * }
     */
    @Column(name = "accept_stander_card_base_info")
    public String acceptStanderCardBaseInfo;

    public AcceptStandCard() {
    }

    public AcceptStandCard(String acceptTypeId, String acceptStanderCardName, String acceptStanderCardBaseInfo) {
        this.acceptTypeId = acceptTypeId;
        this.acceptStanderCardName = acceptStanderCardName;
        this.acceptStanderCardBaseInfo = acceptStanderCardBaseInfo;
    }
}
