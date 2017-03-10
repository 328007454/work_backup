package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by han on 2016/5/5.
 */
@Table(name = "workpiao")
public class WorkPiao extends BaseModel {

    /***/
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();


    // 变电站名称
    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bzd_name;

    // 编号
    public static final String BIANHAO = "bianhao";
    @Column(name = BIANHAO)
    public String bianhao;

    // 计划开工时间
    public static final String START_WORK_TIME = "start_work_time";
    @Column(name = START_WORK_TIME)
    public String start_work_time;

    // 完工时间
    public static final String END_WORK_TIME = "end_work_time";
    @Column(name = END_WORK_TIME)
    public String end_work_time;

    // 票种
    public static final String PIAOZHONG = "piaozhong";
    @Column(name = PIAOZHONG)
    public String piaozhong;

    //状态
    public static final String STATUS = "status";
    @Column(name = STATUS)
    public String status;

    public boolean isFinish = false;

}
