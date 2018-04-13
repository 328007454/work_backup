package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * 巡检到位情况、
 *
 * @author luoxy
 */
@Table(name = "placed")
public class Placed extends BaseModel {

    /**
     * id
     */
    public final static String ID = "id";
    @Id(column = ID)
    public String id = UUID.randomUUID().toString();

    /**
     * 报告id
     */
    public final static String REPORTID = "report_id";
    @Column(column = REPORTID)
    public String reportId;

    /**
     * 变电站id
     */
    public final static String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzId;

    /**
     * 间隔id
     */
    public final static String SPID = "spid";
    @Column(column = SPID)
    public String spId;

    /**
     * 间隔名称
     */
    public final static String SPACENAME = "space_name";
    @Column(column = SPACENAME)
    public String spaceName;

    /**
     * 是否到位
     */
    public final static String PLACED = "placed";
    @Column(column = PLACED)
    public int placed;

    /**
     * 维度
     */
    public final static String LATITUDE = "latitude";
    @Column(column = LATITUDE)
    public double latitude;

    /**
     * 经度
     */
    public final static String LONGTITUDE = "longtitude";
    @Column(column = LONGTITUDE)
    public double longtitude;

    public final static String CREATETIME = "create_time";
    @Column(column = CREATETIME)
    public String createTime;

    public Placed() {
    }


    public Placed(String reportId, String bdzId, String spId, String spaceName, int placed, double latitude, double longtitude) {
        this.reportId = reportId;
        this.bdzId = bdzId;
        this.spId = spId;
        this.spaceName = spaceName;
        this.placed = placed;
        this.latitude = latitude;
        this.longtitude = longtitude;
        createTime = DateUtils.getCurrentLongTime();
    }


}
