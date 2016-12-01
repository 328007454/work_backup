package com.cnksi.sjjc.bean;



import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 评价类型
 */
@Table(name = "evaluation_type")
public class EvaluationType implements Serializable {
    /**
     * 评价类型Id
     */
    public static final String TYPE_ID = "type_id";
    @Column(name = TYPE_ID, isId = true)
    public String typeId = UUID.randomUUID().toString();

    /**
     * 评价类型名称
     */
    public static final String TYPE_NAME = "type_name";
    @Column(name = TYPE_NAME)
    public String typeName;

    /**
     * 评价总分
     */
    public static final String TOTAL_SCORE = "total_score";
    @Column(name = TOTAL_SCORE)
    public float totalStore;

    /**
     * 评价类型填写内容LABEL
     */
    public static final String TYPE_BASE_LABEL = "base_info_label";
    @Column(name = TYPE_BASE_LABEL)
    public String baseLabel;
    /**
     * 评价类型填写内容
     */
    public static final String TYPE_BASE_INFO = "base_info";
    @Column(name = TYPE_BASE_INFO)
    public String baseInfo;


    public EvaluationType(String typeName, float totalStore, String baseLabel, String baseInfo) {
        this.typeName = typeName;
        this.totalStore = totalStore;
        this.baseLabel = baseLabel;
        this.baseInfo = baseInfo;
    }

    public EvaluationType() {
    }
}
