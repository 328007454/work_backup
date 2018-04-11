package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;

import java.util.UUID;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/26 10:04
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ReportTool extends BaseModel {
    public final static String ID = "id";
    @Id(column = "id")
    public String id = UUID.randomUUID().toString();
    ;
    public final static String REPORTID = "reportId";
    @Column(column = REPORTID)
    public String reportId;
    public final static String TOOLID = "toolid";
    @Column(column = TOOLID)
    public String toolId;
    public final static String NUM = "num";
    @Column(column = NUM)
    public String num;

    public ReportTool() {
    }

    public ReportTool(String reportId, String toolid, String num) {
        this.reportId = reportId;
        this.toolId = toolid;
        this.num = num;
    }
}
