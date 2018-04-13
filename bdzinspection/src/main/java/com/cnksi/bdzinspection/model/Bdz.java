package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

@Table(name = "bdz")
public class Bdz extends BaseModel {

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Id(column = BDZID)
    public String bdzid;

    // 名称
    public static final String NAME = "name";
    @Column(column = NAME)
    public String name;

    // 级别
    public static final String LEVEL = "level";
    @Column(column = LEVEL)
    public String level;

    // 全面巡检路线图
    public static final String ROADMAP = "roadmap";
    @Column(column = ROADMAP)
    public String roadmap;
    // 班组ID
    public static final String DEPTID = "dept_id";
    @Column(column = DEPTID)
    public String deptId;

    // 地址
    public static final String ADDR = "addr";
    @Column(column = ADDR)
    public String addr;


    public static final String FOLDER = "folder_name";
    @Column(column = FOLDER)
    public String folder;

    public static final String FINISH_WAY = "maintenance_finish_way";
    @Column(column = FINISH_WAY)
    public String finishWay;

    @Transient
    public boolean isFinish = false;

    public Bdz() {
    }

    public Bdz(String bdzid, String bdzName) {
        this.bdzid = bdzid;
        this.name = bdzName;
    }
}
