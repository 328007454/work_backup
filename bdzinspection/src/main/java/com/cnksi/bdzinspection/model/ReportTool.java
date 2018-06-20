package com.cnksi.bdzinspection.model;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/7/26 10:04
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "xs_gqj_tool")
public class ReportTool extends BaseModel {
    public final static String ID = "id";
    @Column(name = "id",isId = true)
    public String id = UUID.randomUUID().toString();
    public final static String REPORTID = "reportId";
    @Column(name = REPORTID)
    public String reportId;
    public final static String TOOLID = "toolid";
    @Column(name = TOOLID)
    public String toolId;
    public final static String NUM = "num";
    @Column(name = NUM)
    public String num;

    public ReportTool() {
    }

    public ReportTool(String reportId, String toolid, String num) {
        this.reportId = reportId;
        this.toolId = toolid;
        this.num = num;
    }
}
