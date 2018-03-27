package com.cnksi.inspe.entity;

import com.cnksi.inspe.base.BaseEntity;

/**
 * 检查出的问题实体类
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:14
 */

public class InspecteIssueEntity extends BaseEntity {
    /***/
    public String id;
    /***/
    public String title;
    /***/
    public String context;
    /***/
    public int state;
    /***/
    public String stateName;
    /**整改日期*/
    public long date;

    public InspecteIssueEntity(String id, String title, String context, int state, String stateName, long date) {
        this.id = id;
        this.title = title;
        this.context = context;
        this.state = state;
        this.stateName = stateName;
        this.date = date;
    }
}
