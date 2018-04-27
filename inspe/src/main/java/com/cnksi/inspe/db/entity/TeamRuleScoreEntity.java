package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 班组检查规则计分分组表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 10:18
 */
@Table(name = "xj_group_con_rule_score_group")
public class TeamRuleScoreEntity extends BaseDBEntity {

     /***/
    @Column(name = "id",isId = true)
    public String id;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "score")
    public double score;

    /***/
    @Column(name = "sort")
    public int sort;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    public TeamRuleScoreEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
