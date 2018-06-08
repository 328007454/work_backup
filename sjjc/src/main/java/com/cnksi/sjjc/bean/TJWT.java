package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 图解五通实体
 */
@Table(name = "wt_tujie")
public class TJWT {
    /**
     * 五通id
     */
    public static final String WT_ID = "wtid";
    @Column(name = WT_ID, isId = true)
    public String wtid;

    /**
     * 名称
     */
    public static final String NAME = "name";
    @Column(name = NAME)
    public String name;

    /**
     * 图片路径
     */
    public static final String PIC = "pic";
    @Column(name = PIC)
    public String pic;

    /**
     * 排序
     */
    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;

    /**
     * 章节名称
     */
    public static final String CHAPTER_NAME = "chapter_name";
    @Column(name = CHAPTER_NAME)
    public String chapterName;


    /**
     * 删除标志
     */
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt;
}