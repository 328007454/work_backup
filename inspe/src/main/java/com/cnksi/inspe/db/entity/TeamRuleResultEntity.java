package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

/**
 * 班组检查记录表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 10:18
 */
@Table(name = "xj_group_con_rule_result")
public class TeamRuleResultEntity extends BaseDBEntity {

    public TeamRuleResultEntity() {
    }

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "task_id")
    public String task_id;

    /***/
    @Column(name = "check_type")
    public String check_type;

    /***/
    @Column(name = "rule_result_id")
    public String rule_result_id;

    /***/
    @Column(name = "record_type")
    public String record_type;

    /***/
    @Column(name = "rule_id")
    public String rule_id;

    /***/
    @Column(name = "rule_name")
    public String rule_name;

    /***/
    @Column(name = "bdz_name")
    public String bdz_name;

    /***/
    @Column(name = "bdz_id")
    public String bdz_id;

    /***/
    @Column(name = "device_id")
    public String device_id;

    /***/
    @Column(name = "device_name")
    public String device_name;

    /***/
    @Column(name = "device_bigtype")
    public String device_bigtype;

    /***/
    @Column(name = "dept_id")
    public String dept_id;

    /***/
    @Column(name = "dept_name")
    public String dept_name;

    /***/
    @Column(name = "check_person_id")
    public String check_person_id;

    /***/
    @Column(name = "check_person_name")
    public String check_person_name;

    /***/
    @Column(name = "deduct_score")
    public float deduct_score;

    /***/
    @Column(name = "description")
    public String description;

    /***/
    @Column(name = "reason")
    public String reason;

    /***/
    @Column(name = "img")
    public String img;

    /***/
    @Column(name = "problem_nature")
    public String problem_nature;

    /***/
    @Column(name = "charge_unit")
    public String charge_unit;

    /***/
    @Column(name = "charge_group")
    public String charge_group;

    /***/
    @Column(name = "produce_reason")
    public String produce_reason;

    /***/
    @Column(name = "suggest_deal_way")
    public String suggest_deal_way;

    /***/
    @Column(name = "progress")
    public String progress;

    /***/
    @Column(name = "improve_person_id")
    public String improve_person_id;

    /***/
    @Column(name = "improve_person_name")
    public String improve_person_name;

    /***/
    @Column(name = "plan_improve_time")
    public String plan_improve_time;

    /***/
    @Column(name = "improve_description")
    public String improve_description;

    /***/
    @Column(name = "improve_img")
    public String improve_img;

    /***/
    @Column(name = "improve_time")
    public String improve_time;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "create_time")
    public String create_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    public String getId() {
        return this.id;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public String getCheck_type() {
        return this.check_type;
    }

    public String getRule_result_id() {
        return this.rule_result_id;
    }

    public String getRecord_type() {
        return this.record_type;
    }

    public String getRule_id() {
        return this.rule_id;
    }

    public String getRule_name() {
        return this.rule_name;
    }

    public String getBdz_name() {
        return this.bdz_name;
    }

    public String getBdz_id() {
        return this.bdz_id;
    }

    public String getDevice_id() {
        return this.device_id;
    }

    public String getDevice_name() {
        return this.device_name;
    }

    public String getDevice_bigtype() {
        return this.device_bigtype;
    }

    public String getDept_id() {
        return this.dept_id;
    }

    public String getDept_name() {
        return this.dept_name;
    }

    public String getCheck_person_id() {
        return this.check_person_id;
    }

    public String getCheck_person_name() {
        return this.check_person_name;
    }

    public float getDeduct_score() {
        return this.deduct_score;
    }

    public String getDescription() {
        return this.description;
    }

    public String getReason() {
        return this.reason;
    }

    public String getImg() {
        return this.img;
    }

    public String getProblem_nature() {
        return this.problem_nature;
    }

    public String getCharge_unit() {
        return this.charge_unit;
    }

    public String getCharge_group() {
        return this.charge_group;
    }

    public String getProduce_reason() {
        return this.produce_reason;
    }

    public String getSuggest_deal_way() {
        return this.suggest_deal_way;
    }

    public String getProgress() {
        return this.progress;
    }

    public String getImprove_person_id() {
        return this.improve_person_id;
    }

    public String getImprove_person_name() {
        return this.improve_person_name;
    }

    public String getPlan_improve_time() {
        return this.plan_improve_time;
    }

    public String getImprove_description() {
        return this.improve_description;
    }

    public String getImprove_img() {
        return this.improve_img;
    }

    public String getImprove_time() {
        return this.improve_time;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public void setCheck_type(String check_type) {
        this.check_type = check_type;
    }

    public void setRule_result_id(String rule_result_id) {
        this.rule_result_id = rule_result_id;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public void setBdz_name(String bdz_name) {
        this.bdz_name = bdz_name;
    }

    public void setBdz_id(String bdz_id) {
        this.bdz_id = bdz_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public void setDevice_bigtype(String device_bigtype) {
        this.device_bigtype = device_bigtype;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public void setCheck_person_id(String check_person_id) {
        this.check_person_id = check_person_id;
    }

    public void setCheck_person_name(String check_person_name) {
        this.check_person_name = check_person_name;
    }

    public void setDeduct_score(float deduct_score) {
        this.deduct_score = deduct_score;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setProblem_nature(String problem_nature) {
        this.problem_nature = problem_nature;
    }

    public void setCharge_unit(String charge_unit) {
        this.charge_unit = charge_unit;
    }

    public void setCharge_group(String charge_group) {
        this.charge_group = charge_group;
    }

    public void setProduce_reason(String produce_reason) {
        this.produce_reason = produce_reason;
    }

    public void setSuggest_deal_way(String suggest_deal_way) {
        this.suggest_deal_way = suggest_deal_way;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public void setImprove_person_id(String improve_person_id) {
        this.improve_person_id = improve_person_id;
    }

    public void setImprove_person_name(String improve_person_name) {
        this.improve_person_name = improve_person_name;
    }

    public void setPlan_improve_time(String plan_improve_time) {
        this.plan_improve_time = plan_improve_time;
    }

    public void setImprove_description(String improve_description) {
        this.improve_description = improve_description;
    }

    public void setImprove_img(String improve_img) {
        this.improve_img = improve_img;
    }

    public void setImprove_time(String improve_time) {
        this.improve_time = improve_time;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }


}
