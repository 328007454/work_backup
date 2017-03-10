package com.cnksi.sjjc.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "bdz")
public class Bdz extends BaseModel{

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID, isId = true)
    public String bdzid;

    // 名称
    public static final String NAME = "name";
    @Column(name = NAME)
    public String name;

    // 级别
    public static final String LEVEL = "level";
    @Column(name = LEVEL)
    public String level;

    // 全面巡检路线图
    public static final String ROADMAP = "roadmap";
    @Column(name = ROADMAP)
    public String roadmap;

    // 地址
    public static final String ADDR = "addr";
    @Column(name = ADDR)
    public String addr;



    public static final String FOLDER = "folder_name";
    @Column(name = FOLDER)
    public String folder;
    
    //定期维护定期实验是否是严格标准
    public static final String FINISH_WAY = "maintenance_finish_way";
    @Column(name = FINISH_WAY)
    public String finishWay;

    public boolean isFinish = false;

}
