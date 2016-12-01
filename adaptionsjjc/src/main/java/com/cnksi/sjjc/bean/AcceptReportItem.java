package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * item验收报告
 */
@Table(name = "accept_report_item")
public class AcceptReportItem {
    /**
     * item报告id
     */
    public static final String REPORT_ITEM_ID = "report_item_id";
    @Column(name = REPORT_ITEM_ID, isId = true)
    public String reportItemId;

    /**
     * 报告Id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID)
    public String reportId;

    /**
     * 标准卡选项Id
     */
    public static final String STANDER_ITEM_ID = "stander_item_id";
    @Column(name = STANDER_ITEM_ID)
    public String standerItemId;

    /**
     * 标准卡选项名称
     */
    public static final String STANDER_ITEM_NAME = "stander_item_name";
    @Column(name = STANDER_ITEM_NAME)
    public String standerItemName;

    /**
     * 是否是验收大项（0-不是，1-是）
     */
    public static final String IS_PARENT = "is_parent";
    @Column(name = IS_PARENT)
    public int isParent;

    /**
     * 验收是否合格（0合格，-1不合格）
     */
    public static final String IS_NORMAL = "is_normal";
    @Column(name = IS_NORMAL)
    public int isNormal;

    /**
     * 验收签名
     */
    public static final String ITEM_SIGN = "item_sign";
    @Column(name = ITEM_SIGN)
    public String itemSign;

    /**
     * 验收视频
     */
    public static final String ITEM_VIDEO = "item_video";
    @Column(name = ITEM_VIDEO)
    public String itemVideo;

    /**
     * 验收音频
     */
    public static final String ITEM_AUDIO = "item_audio";
    @Column(name = ITEM_AUDIO)
    public String itemAudio;


    /**
     * 验收图片集 ","分割
     */
    public static final String ITEM_IMAGES = "item_images";
    @Column(name = ITEM_IMAGES)
    public String itemImages;

    /**
     * 验收问题备注
     */
    public static final String ITEM_REMARK = "item_question_remark";
    @Column(name = ITEM_SIGN)
    public String itemQuestionRemark;

    /**
     * 验收额外内容
     */
    public static final String ITEM_EXTRA_CONTENT = "item_extra_content";
    @Column(name = ITEM_EXTRA_CONTENT)
    public String itemExtraContent;

    /**
     * 验收项额外内容（JSON）
     * [
     * {
     * "name": "压力",
     * "type": "String",
     * "unit": "MPa",
     * "value": "9"
     * },
     * {
     * "name": "时间",
     * "type": "String",
     * "unit": "h"h,
     * "value": "0.5"
     * },
     * {
     * "name": "泄露率 ",
     * "type": "String",
     * "unit": "Pa.L/s",
     * "value": "0.98"
     * }
     * ]
     */
    public class ItemExtraContent {
        public String name;

        public String type;

        public String unit;

        public String value;
    }


}
