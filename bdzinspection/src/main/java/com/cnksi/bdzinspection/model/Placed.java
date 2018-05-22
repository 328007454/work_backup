package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

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
    @Column(name = ID,isId = true)
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

    public final static String CREATETIME = "create_time";
    @Column(name = CREATETIME)
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
