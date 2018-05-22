package com.cnksi.nari.model;


import com.cnksi.common.Config;
import com.cnksi.nari.type.PackageStatus;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Nari Package数据实体类
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/21 17:34
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "bd_package")
public class BDPackage {

    @Column(name = "appurl")
    public String appurl;
    @Column(name = "createTime")
    public String createTime;
    @Column(name = "packageType")
    public String packageType;
    @Column(name = "packageName")
    public String packageName;

    @Column(name = "databaseProvider")
    public String databaseProvider;
    @Column(name = "packageID",isId = true)
    public String packageID;
    @Column(name = "regulationID")
    public String regulationID;
    @Column(name = "database")
    public String database;
    @Column(name = "downloadTime")
    public String downloadTime;

    public static final  String PACKAGESTATUS="status";
    @Column(name = PACKAGESTATUS)
    public String status = PackageStatus.nodown.name();

    @Column(name = "taskId")
    public String taskId;
    @Column(name = "user")
    public String user;
    @Column(name = "inspection_type")
    public String inspectionType;
    @Column(name = "pms_jhid")
    public String pmsJhid;

    @Column(name = "start_time")
    public String startTime;
    // public static final String DLT = "dlt";
    // @Id(name = DLT)
    // public String dlt="0";

    public String bulidString() {
        StringBuilder sb = new StringBuilder();
        sb.append("作业名称：").append(packageName).append("\n");
        sb.append("创建时间：").append(createTime).append("\n");
        sb.append("当前状态：").append(status).append("\n");
        return sb.toString();
    }

    public String getDatabasePath() {
        return Config.NARI_BASEFOLDER + packageID + "/data.db3";
    }
}
