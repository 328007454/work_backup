package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 标准卡选项
 */
@Table(name = "accept_stander_item")
public class AcceptCardItem implements Serializable {
    /**
     * 选项ID
     */
    @Column(name = "item_id", isId = true)
    public String itemId = UUID.randomUUID().toString();

    /**
     * 标准卡Id
     */
    public static final String ACCEPT_STANDER_CARD_ID="accept_stander_card_id";
    @Column(name = ACCEPT_STANDER_CARD_ID)
    public String cardId;

    /**
     * 父选项Id
     */
    public static final String PARENT_ITEM_ID="parent_item_id";
    @Column(name = PARENT_ITEM_ID)
    public String parentItemId;


    /**
     * 选项卡排序
     */
    @Column(name = "item_order")
    public int itemOrder;
    /**
     * 选项卡内容
     */
    @Column(name = "item_content")
    public String itemContent;
    /**
     * 选项卡验收标准
     */
    @Column(name = "item_stander")
    public String itemStander;

    /**
     * 选项卡检查方式
     */
    @Column(name = "item_check_style")
    public String itemCheckStyle;

    /**
     * 验收选项<br/>
     * "是,否"或者"有,没有"
     */
    @Column(name = "item_check_option")
    public String itemCheckOption;
    /**
     * 额外验收内容（采用json）存储<br>
     * [
     * {
     * "name": "压力",
     * "type": "String",
     * "unit": "MPa"
     * },
     * {
     * "name": "时间",
     * "type": "String",
     * "unit": "MPa"
     * },
     * {
     * "name": "泄露率 ",
     * "type": "String",
     * "unit": "Pa.L/s"
     * }
     * ]
     */
    @Column(name = "extra_content")
    public String extraContent;


    public AcceptCardItem() {
    }

    public AcceptCardItem(String cardId, String itemContent, int itemOrder) {
        this.cardId = cardId;
        this.itemContent = itemContent;
        this.itemOrder = itemOrder;
    }

    public AcceptCardItem(String cardId, String parentItemId, int itemOrder, String itemContent, String itemStander, String itemCheckStyle, String itemCheckOption, String extraContent) {
        this.cardId = cardId;
        this.parentItemId = parentItemId;
        this.itemOrder = itemOrder;
        this.itemContent = itemContent;
        this.itemStander = itemStander;
        this.itemCheckStyle = itemCheckStyle;
        this.itemCheckOption = itemCheckOption;
        this.extraContent = extraContent;
    }
}
