package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

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
    @Column(name = "id", isId = true)
    private String id;

    /***/
    @Column(name = "name")
    private String name;

    /***/
    @Column(name = "score")
    private float score;

    /***/
    @Column(name = "sort")
    private int sort;

    /***/
    @Column(name = "timestamp")
    private long last_modify_time;

    /***/
    @Column(name = "dlt")
    private int dlt;

    /***/
    @Column(name = "insert_time")
    private long insert_time;

    /***/
    @Column(name = "update_time")
    private long update_time;

    public TeamRuleScoreEntity() {
    }

    public TeamRuleScoreEntity(String id, float score) {
        this.id = id;
        this.score = score;
    }

    public TeamRuleScoreEntity(String id, String name, float score, int sort, long last_modify_time, int dlt, long insert_time, long update_time) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.sort = sort;
        this.last_modify_time = last_modify_time;
        this.dlt = dlt;
        this.insert_time = insert_time;
        this.update_time = update_time;
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(long last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public long getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(long insert_time) {
        this.insert_time = insert_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public TeamRuleScoreEntity getParent(DbManager db) throws DbException {
        return db.findById(TeamRuleScoreEntity.class, id);
    }

}
