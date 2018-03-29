package com.cnksi.inspe.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 数据库日志实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/27 21:40
 */
@Table(name = "xj_group_con_rule_oper_log")
public class DbLogEntity {
   /***/
    @Column(name = "id",isId = true)
    public String id;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "user_id")
    public String user_id;

    /***/
    @Column(name = "user_name")
    public String user_name;

    /***/
    @Column(name = "rule_result_id")
    public String rule_result_id;

    /***/
    @Column(name = "content")
    public String content;

    /***/
    @Column(name = "create_time")
    public String create_time;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "remark")
    public String remark;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    public DbLogEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getRule_result_id() {
        return rule_result_id;
    }

    public void setRule_result_id(String rule_result_id) {
        this.rule_result_id = rule_result_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
