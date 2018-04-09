package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseDBEntity;
import com.cnksi.inspe.base.BaseDbService;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 精益化评价表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/27 21:40
 */
@Table(name = "xj_jyhpj_rule")
public class PlusteRuleEntity extends BaseDBEntity {

    public PlusteRuleEntity() {
    }

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "pid")
    public String pid;

    /***/
    @Column(name = "bigid")
    public String bigid;

    /***/
    @Column(name = "bigtype_name")
    public String bigtype_name;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "description")
    public String description;

    /***/
    @Column(name = "row_num")
    public int row_num;

    /***/
    @Column(name = "level")
    public String level;

    /***/
    @Column(name = "rule_num")
    public String rule_num;

    /***/
    @Column(name = "score")
    public double score;

    /***/
    @Column(name = "score_content")
    public String score_content;

    /***/
    @Column(name = "score_charge")
    public String score_charge;

    /***/
    @Column(name = "reason_type")
    public String reason_type;

    /***/
    @Column(name = "check_way")
    public String check_way;

    /***/
    @Column(name = "deduction_checkbox")
    public String deduction_checkbox;

    /***/
    @Column(name = "deduction_copy")
    public String deduction_copy;

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
    @Column(name = "sort")
    public int sort;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "level_name")
    public String level_name;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    public String getId() {
        return this.id;
    }

    public String getPid() {
        return this.pid;
    }

    public String getBigid() {
        return this.bigid;
    }

    public String getBigtype_name() {
        return this.bigtype_name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getRow_num() {
        return this.row_num;
    }

    public String getLevel() {
        return this.level;
    }

    public String getRule_num() {
        return this.rule_num;
    }

    public double getScore() {
        return this.score;
    }

    public String getScore_content() {
        return this.score_content;
    }

    public String getScore_charge() {
        return this.score_charge;
    }

    public String getReason_type() {
        return this.reason_type;
    }

    public String getCheck_way() {
        return this.check_way;
    }

    public String getDeduction_checkbox() {
        return this.deduction_checkbox;
    }

    public String getDeduction_copy() {
        return this.deduction_copy;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public int getSort() {
        return this.sort;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getLevel_name() {
        return this.level_name;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setBigid(String bigid) {
        this.bigid = bigid;
    }

    public void setBigtype_name(String bigtype_name) {
        this.bigtype_name = bigtype_name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRow_num(int row_num) {
        this.row_num = row_num;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setRule_num(String rule_num) {
        this.rule_num = rule_num;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setScore_content(String score_content) {
        this.score_content = score_content;
    }

    public void setScore_charge(String score_charge) {
        this.score_charge = score_charge;
    }

    public void setReason_type(String reason_type) {
        this.reason_type = reason_type;
    }

    public void setCheck_way(String check_way) {
        this.check_way = check_way;
    }

    public void setDeduction_checkbox(String deduction_checkbox) {
        this.deduction_checkbox = deduction_checkbox;
    }

    public void setDeduction_copy(String deduction_copy) {
        this.deduction_copy = deduction_copy;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }


}
