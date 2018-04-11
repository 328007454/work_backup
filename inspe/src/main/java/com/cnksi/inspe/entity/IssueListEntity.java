package com.cnksi.inspe.entity;

import com.cnksi.inspe.db.entity.TeamRuleResultEntity;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/11 09:36
 */
public class IssueListEntity {
    public TeamRuleResultEntity resultEntity;
    //一二级Name
    public String[] names;

    public IssueListEntity(TeamRuleResultEntity resultEntity, String[] names) {
        this.resultEntity = resultEntity;
        this.names = names;
    }

    public IssueListEntity() {
    }
}
