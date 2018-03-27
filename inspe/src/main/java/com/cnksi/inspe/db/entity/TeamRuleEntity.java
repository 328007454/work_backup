package com.cnksi.inspe.db.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.Map;

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
    private String id;

    /***/
    @Column(name = "name")
    private String name;

    /***/
    @Column(name = "level")
    private String level;

    /***/
    @Column(name = "type")
    private String type;

    /***/
    @Column(name = "score_group_id")
    private String score_group_id;

    /***/
    @Column(name = "pid")
    private String pid;

    /***/
    @Column(name = "dependence")
    private String dependence;

    /***/
    @Column(name = "dept_name")
    private String dept_name;

    /***/
    @Column(name = "workshop")
    private String workshop;

    /***/
    @Column(name = "team")
    private String team;

    /***/
    @Column(name = "stand_score")
    private float stand_score;

    /***/
    @Column(name = "stand_content")
    private String stand_content;

    /***/
    @Column(name = "create_time")
    private long create_time;

    /***/
    @Column(name = "update_time")
    private long update_time;

    /***/
    @Column(name = "last_modify_time")
    private long last_modify_time;

    /***/
    @Column(name = "gzrwid")
    private String gzrwid;

    /***/
    @Column(name = "sort")
    private String sort;

    /***/
    @Column(name = "level_namel")
    private String level_namel;

    /***/
    @Column(name = "city_id")
    private String city_id;

    /***/
    @Column(name = "insert_time")
    private long insert_time;

    /**
     * 总分
     */
    private float scoreCount;

    public TeamRuleEntity() {
    }

    public TeamRuleEntity(String id, String name, String level, String type, String score_group_id, String pid, String dependence, String dept_name, String workshop, String team, float stand_score, String stand_content, long create_time, long update_time, long last_modify_time, String gzrwid, String sort, String level_namel, String city_id, long insert_time) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.type = type;
        this.score_group_id = score_group_id;
        this.pid = pid;
        this.dependence = dependence;
        this.dept_name = dept_name;
        this.workshop = workshop;
        this.team = team;
        this.stand_score = stand_score;
        this.stand_content = stand_content;
        this.create_time = create_time;
        this.update_time = update_time;
        this.last_modify_time = last_modify_time;
        this.gzrwid = gzrwid;
        this.sort = sort;
        this.level_namel = level_namel;
        this.city_id = city_id;
        this.insert_time = insert_time;
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

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public long getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(long last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public String getGzrwid() {
        return gzrwid;
    }

    public void setGzrwid(String gzrwid) {
        this.gzrwid = gzrwid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getLevel_namel() {
        return level_namel;
    }

    public void setLevel_namel(String level_namel) {
        this.level_namel = level_namel;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public long getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(long insert_time) {
        this.insert_time = insert_time;
    }

    public float getScoreCount() {
        return scoreCount;
    }

    public void setScoreCount(float scoreCount) {
        this.scoreCount = scoreCount;
    }

    public TeamRuleEntity getParent(DbManager db) throws DbException {
        return db.findById(TeamRuleEntity.class, id);
    }

}
