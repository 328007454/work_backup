package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;


/**
 * 巡检到位情况、
 *
 * @author luoxy
 */
@Table(name = "placed")
public class Placed extends BaseModel{

    /**
     * id
     */
    public final static String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();

    /**
     * 报告id
     */
    public final static String REPORTID = "report_id";
    @Column(name = REPORTID)
    public String reportId;

    /**
     * 变电站id
     */
    public final static String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzId;

    /**
     * 间隔id
     */
    public final static String SPID = "spid";
    @Column(name = SPID)
    public String spId;

    /**
     * 间隔名称
     */
    public final static String SPACENAME = "space_name";
    @Column(name = SPACENAME)
    public String spaceName;

    /**
     * 是否到位
     */
    public final static String PLACED = "placed";
    @Column(name = PLACED)
    public int placed;

    /**
     * 维度
     */
    public final static String LATITUDE = "latitude";
    @Column(name = LATITUDE)
    public double latitude;

    /**
     * 经度
     */
    public final static String LONGTITUDE = "longtitude";
    @Column(name = LONGTITUDE)
    public double longtitude;


    public Placed() {
    }


    public Placed(String reportId, String spId, String spaceName, int placed, double latitude, double longtitude) {
        this.reportId = reportId;
        this.spId = spId;
        this.spaceName = spaceName;
        this.placed = placed;
        this.latitude = latitude;
        this.longtitude = longtitude;
    }


}
