package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 评价选项
 */
@Table(name = "evaluation_item")
public class EvaluationItem implements Serializable {

    public class ExtraContent {
        public String name;
        public String type;
        public String unit;


        public ExtraContent(String name, String type, String unit) {
            this.name = name;
            this.type = type;
            this.unit = unit;
        }
    }

    /**
     * 评价选项Id
     */
    public static final String ITEM_ID = "item_id";
    @Column(name = ITEM_ID, isId = true)
    public String itemId = UUID.randomUUID().toString();

    /**
     * 评价类型Id
     */
    public static final String TYPE_ID = "type_id";
    @Column(name = TYPE_ID)
    public String typeId;

    /**
     * 父评价选项Id
     */
    public static final String PARENT_ID = "parent_id";
    @Column(name = PARENT_ID)
    public String parentId;
    /**
     * item等级
     */
    public static final  String ITEM_LEVEL="item_level";
    @Column(name = ITEM_LEVEL)
    public String itemLevel;
    /**
     * 评价选项内容
     */
    public static final String ITEM_CONTENT = "item_content";
    @Column(name = ITEM_CONTENT)
    public String itemContent;

    /**
     * 评价选项总分数
     */
    public static final String ITEM_SCORE = "item_score";
    @Column(name = ITEM_SCORE)
    public double itemScore;

    /**
     * 评价选项序号
     */
    public static final String ITEM_ORDER = "item_order";
    @Column(name = ITEM_ORDER)
    public int itemOrder;

    /**
     * 评价选项检查方式
     */
    public static final String ITEM_CHECK_STYLE = "item_check_style";
    @Column(name = ITEM_CHECK_STYLE)
    public String itemCheckStyle;

    /**
     * 评价选项检查选项
     */
    public static final String ITEM_CHECK_OPTION = "item_check_option";
    @Column(name = ITEM_CHECK_OPTION)
    public String itemCheckOption;

    /**
     * 评价选项检查标准
     */
    public static final String ITEM_CHECK_STANDER = "item_check_stander";
    @Column(name = ITEM_CHECK_STANDER)
    public String itemCheckStander;

    /**
     * 评价选项额外输入内容
     */
    public static final String ITEM_CHECK_EXTRA = "item_check_extra";
    @Column(name = ITEM_CHECK_EXTRA)
    public String itemCheckExtra;


    public float disScore=0;

    public EvaluationItem(String typeId, String itemContent, double itemScore, int itemOrder) {
        this.typeId = typeId;
        this.itemContent = itemContent;
        this.itemScore = itemScore;
        this.itemOrder = itemOrder;
    }

    public EvaluationItem(String typeId, String parentId, String itemContent) {
        this.typeId = typeId;
        this.parentId = parentId;
        this.itemContent = itemContent;
    }

    public EvaluationItem(String typeId, String parentId, String itemContent, int itemOrder, String itemCheckStyle, String itemCheckOption, String itemCheckStander, String itemCheckExtra) {
        this.typeId = typeId;
        this.parentId = parentId;
        this.itemContent = itemContent;
        this.itemOrder = itemOrder;
        this.itemCheckStyle = itemCheckStyle;
        this.itemCheckOption = itemCheckOption;
        this.itemCheckStander = itemCheckStander;
        this.itemCheckExtra = itemCheckExtra;
    }

    public EvaluationItem() {
    }

}
