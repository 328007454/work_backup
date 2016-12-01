package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 评价报告
 */
@Table(name = "evaluation_report")
public class EvaluationReport implements Serializable {
    /**
     * 评价报告id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID, isId = true)
    public String reportId = UUID.randomUUID().toString();
    /**
     * 评价类型Id
     */
    public static final String TYPE_ID = "type_id";
    @Column(name = TYPE_ID)
    public String typeId;
    /**
     * 评价类型名称
     */
    public static final String TYPE_NAME = "type_name";
    @Column(name = TYPE_NAME)
    public String typeName;

    /**
     * 评价总得分
     */
    public static final String FINAL_SCORE = "evaluation_score";
    @Column(name = FINAL_SCORE)
    public float finalStore;

    /**
     * 评价报告填写内容
     */
    public static final String REPORT_BASE_INFO = "base_info";
    @Column(name = REPORT_BASE_INFO)
    public String baseInfo;

    /**
     * 评价报告签名
     */
    public static final String REPORT_SIGN = "evaluation_sign";
    @Column(name = REPORT_SIGN)
    public String evaluationSign;
    /**
     * 评价报告日期
     */
    public static final String REPORT_DATE = "evaluation_date";
    @Column(name = REPORT_DATE)
    public String evaluationDate;


    public class TypeBaseInfo implements Serializable {
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
