package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * 差动保护
 */
@Table(name = "report_cdbhcl")
public class ReportCdbhcl {

    /***/
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();


    /**
     * 报告id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID)
    public String report_id;

    /**
     * 变电站id
     */
    public static final String BDZ_ID = "bdz_id";
    @Column(name = BDZ_ID)
    public String bdz_id;

    /**
     * 变电站名称
     */
    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bdz_name;

    /**
     * 设备id
     */
    public static final String DEVICE_ID = "device_id";
    @Column(name = DEVICE_ID)
    public String device_id;

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "device_name";
    @Column(name = DEVICE_NAME)
    public String device_name;

    /**
     * 大差流值
     */
    public static final String DCLZ = "dclz";
    @Column(name = DCLZ)
    public String dclz;

    /**
     * 插入时间
     */
    public static final String INSERT_TIME = "insert_time";
    @Column(name = INSERT_TIME)
    public String insert_time;

    /**
     * 最后更新时间
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;

    /**
     * 是否删除（0-可用，1-不可用）
     */
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt = "0";


    public ReportCdbhcl() {
        this.insert_time = DateUtils.getCurrentLongTime();
    }

    public ReportCdbhcl(String report_id, String bdz_id, String bdz_name, String device_id, String device_name, String insert_time) {
        this.report_id = report_id;
        this.bdz_id = bdz_id;
        this.bdz_name = bdz_name;
        this.device_id = device_id;
        this.device_name = device_name;
        this.insert_time = insert_time;
    }

    ;
//	public ReportCdbhcl(String ){
//
//	}

}
