package com.cnksi.nari.model;


import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.nari.type.PackageStatus;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

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

    @Column(column = "appurl")
    public String appurl;
    @Column(column = "createTime")
    public String createTime;
    @Column(column = "packageType")
    public String packageType;
    @Column(column = "packageName")
    public String packageName;

    @Column(column = "databaseProvider")
    public String databaseProvider;
    @Id(column = "packageID")
    public String packageID;
    @Column(column = "regulationID")
    public String regulationID;
    @Column(column = "database")
    public String database;
    @Column(column = "downloadTime")
    public String downloadTime;

    public static final  String PACKAGESTATUS="status";
    @Column(column = PACKAGESTATUS)
    public String status = PackageStatus.nodown.name();

    @Column(column = "taskId")
    public String taskId;
    @Column(column = "user")
    public String user;
    @Column(column = "inspection_type")
    public String inspectionType;
    @Column(column = "pms_jhid")
    public String pmsJhid;

    @Column(column = "start_time")
    public String startTime;
    // public static final String DLT = "dlt";
    // @Id(column = DLT)
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
