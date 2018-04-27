package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 班组检查规则表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 10:18
 */
@Table(name = "xj_group_con_rule")
public class TeamRuleEntity extends BaseDBEntity {
    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "row_num")
    public int row_num;

    /***/
    @Column(name = "level")
    public String level;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "score_group_id")
    public String score_group_id;

    /***/
    @Column(name = "pid")
    public String pid;

    /***/
    @Column(name = "dependence")
    public String dependence;

    /***/
    @Column(name = "dept_name")
    public String dept_name;

    /***/
    @Column(name = "workshop")
    public String workshop;

    /***/
    @Column(name = "team")
    public String team;

    /***/
    @Column(name = "stand_score")
    public float stand_score;

    /***/
    @Column(name = "stand_content")
    public String stand_content;

    /***/
    @Column(name = "create_time")
    public String create_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "gzrwid")
    public String gzrwid;

    /***/
    @Column(name = "sort")
    public int sort;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "level_name")
    public String level_name;

    /***/
    @Column(name = "city_id")
    public String city_id;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    //**当前项总分*/
    public float scoreCount;

    //**当前标准状态(not null为已检查)*/
    public String record_type;

    //**扣分当前项扣分数*/
    public float deduct_score;

    public TeamRuleEntity() {
    }

    public String getRecord_type() {
        return record_type;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public float getDeduct_score() {
        return deduct_score;
    }

    public void setDeduct_score(float deduct_score) {
        this.deduct_score = deduct_score;
    }

    public float getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(float scoreCount) {
        this.scoreCount = scoreCount;
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

    public int getRow_num() {
        return row_num;
    }

    public void setRow_num(int row_num) {
        this.row_num = row_num;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScore_group_id() {
        return score_group_id;
    }

    public void setScore_group_id(String score_group_id) {
        this.score_group_id = score_group_id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDependence() {
        return dependence;
    }

    public void setDependence(String dependence) {
        this.dependence = dependence;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public float getStand_score() {
        return stand_score;
    }

    public void setStand_score(float stand_score) {
        this.stand_score = stand_score;
    }

    public String getStand_content() {
        return stand_content;
    }

    public void setStand_content(String stand_content) {
        this.stand_content = stand_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public String getGzrwid() {
        return gzrwid;
    }

    public void setGzrwid(String gzrwid) {
        this.gzrwid = gzrwid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }
}
