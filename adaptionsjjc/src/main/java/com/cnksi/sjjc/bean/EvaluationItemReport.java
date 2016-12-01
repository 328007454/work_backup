package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 评价报告
 */
@Table(name = "evaluation_item_report")
public class EvaluationItemReport implements Serializable {
    /**
     * id
     */
    public static final String UID = "uid";
    @Column(name =UID,isId = true)
    public String uid=UUID.randomUUID().toString();
    /**
     * 评价报告id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID)
    public String reportId;
    /**
     * 评价项id
     */
    public static final String ITEM_ID = "evaluation_item_id";
    @Column(name = ITEM_ID)
    public String itemId;
    /**
     * 评价项内容
     */
    public static final String ITEM_NAME = "evaluation_item_content";
    @Column(name = ITEM_NAME)
    public String itemName;

    /**
     * 评价结果 0/-1 合格/不合格
     */
    public static final String IS_NORMAL = "is_normal";
    @Column(name = IS_NORMAL)
    public int isNormal;

    /**
     * 评价项扣分
     */
    public static final String DISCOUNT_SCORE = "discount_score";
    @Column(name = DISCOUNT_SCORE)
    public float discountScore;

    /**
     * 评价项额外输入内容
     */
    public static final String ITEM_EXTRA = "item_extra";
    @Column(name = ITEM_EXTRA)
    public String itemExtra;
    /**
     * 评价视频
     */
    public static final String ITEM_VIDEO = "item_video";
    @Column(name = ITEM_VIDEO)
    public String itemVideo;

    /**
     * 评价音频
     */
    public static final String ITEM_AUDIO = "item_audio";
    @Column(name = ITEM_AUDIO)
    public String itemAudio;


    /**
     * 评价图片集 ","分割
     */
    public static final String ITEM_IMAGES = "item_images";
    @Column(name = ITEM_IMAGES)
    public String itemImages;

    /**
     * 评价备注
     */
    public static final String ITEM_REMARK = "item_remark";
    @Column(name = ITEM_REMARK)
    public String itemRemark;

}
