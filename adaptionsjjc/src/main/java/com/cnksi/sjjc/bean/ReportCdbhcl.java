package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * 差动保护
 */
@Table(name = "report_cdbhcl")
public class ReportCdbhcl extends BaseModel{

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
     * 大差流值O
     */
    public static final String DCLZO = "dclz_o";
    @Column(name = DCLZO)
    public String dclzO;
    /**
     * 大差流值A
     */
    public static final String DCLZA = "dclz_a";
    @Column(name = DCLZA)
    public String dclzA;
    /**
     * 大差流值B
     */
    public static final String DCLZB = "dclz_b";
    @Column(name = DCLZB)
    public String dclzB;
    /**
     * 大差流值C
     */
    public static final String DCLZC = "dclz_c";
    @Column(name = DCLZC)
    public String dclzC;


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

    public ReportCdbhcl(CdbhclValue value, String reportId, String bdzName, String bdzId) {
        this.report_id = reportId;
        this.bdz_id = bdzId;
        this.bdz_name = bdzName;
        this.device_name = value.getDeviceName().substring(0, value.getDeviceName().length() - 1);
        this.device_id = value.getId();
        this.insert_time = DateUtils.getCurrentLongTime();
        addValue(value);

    }

    public void addValue(CdbhclValue value) {
        if (value.getName().equals("val")) {
            this.dclz = value.getValue();
        }
        if (value.getName().equals("val_a")) {
            this.dclzA = value.getValue();
        }
        if (value.getName().equals("val_b")) {
            this.dclzB = value.getValue();
        }
        if (value.getName().equals("val_c")) {
            this.dclzC = value.getValue();
        }
        if (value.getName().equals("val_o")) {
            this.dclzO = value.getValue();
        }
    }
}
