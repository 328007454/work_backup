package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by han on 2016/4/28.
 */
public class ReportJzlbyqfjkgService extends BaseService<ReportJzlbyqfjkg> {
    private static ReportJzlbyqfjkgService mReportJzlby;

    private ReportJzlbyqfjkgService() {
        super(ReportJzlbyqfjkg.class);
    }

    public static ReportJzlbyqfjkgService getInstance() {
        if (mReportJzlby == null) {
            mReportJzlby = new ReportJzlbyqfjkgService();
            return mReportJzlby;
        } else {
            return mReportJzlby;
        }
    }

    /**
     * 通过 "分接开关关键词，以及设备id等于 部件id 查询"
     */

    public List<DbModel> getPartDevice(String bdzid, String partDevice) {
        List<DbModel> dbModel = null;
//        String sql = "select * from ( select device.bdzid as bdzid,device.deviceid as deviceid,device.name as name,t.name as partname from (select * from device_part where name ='" + partDevice + "')t left join device on t.deviceid = device.deviceid )b where b.bdzid = '" + bdzid + "'";
        String sql = "SELECT d.deviceid,d.name,d.bdzid FROM device d WHERE d.dlt='0' and d.bdzid = '" + bdzid + "' AND dtid IN ( SELECT dtid FROM device_unit WHERE dlt='0' and duid IN ( SELECT s.duid FROM standards s WHERE s.dlt='0' and s.description LIKE '%" + partDevice + "%'))";

        try {
            dbModel = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModel;
    }

    /**
     * 根据报告id,变电站id，设备id
     */
    public ReportJzlbyqfjkg getFirstReport(String bdzid, String deviceid, String reportId) {
        ReportJzlbyqfjkg mReportJzlbyq = null;

        try {
            mReportJzlbyq = selector().and(ReportJzlbyqfjkg.BDZ_ID, "=", bdzid).and(ReportJzlbyqfjkg.DEVICE_ID, "=", deviceid).and(ReportJzlbyqfjkg.REPORT_ID, "=", reportId).findFirst();
            if (mReportJzlbyq == null) {
                mReportJzlbyq = new ReportJzlbyqfjkg();
            }
            return mReportJzlbyq;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mReportJzlbyq;
    }

    public List<DbModel> getPartDeviceAndReport(String bdzid, String partDevice, String reportid) {
        List<DbModel> dbModel = null;
//        String sql = "select * from ( select device.bdzid as bdzid,device.deviceid as deviceid,device.name as name,t.name as partname from (select * from device_part where name ='" + partDevice + "')t left join device on t.deviceid = device.deviceid )b where b.bdzid = '" + bdzid + "'";
//        String sql = "SELECT d.deviceid,d.name,d.bdzid FROM device d WHERE d.bdzid = '"+bdzid+"' AND dtid IN ( SELECT dtid FROM device_unit WHERE duid IN ( SELECT s.duid FROM standards s WHERE s.description LIKE '%"+partDevice+"%'))";
        String sql = "select * from report_jzlbyqfjkg  rj left join (SELECT d.deviceid as deviceId,d.name,d.bdzid as bdzId FROM device d WHERE d.dlt='0' and d.bdzid = '" + bdzid + "' AND dtid IN ( SELECT dtid FROM device_unit WHERE dlt='0' and  duid IN ( SELECT s.duid FROM standards s WHERE s.dlt='0' and s.description LIKE '%" + partDevice + "%'))) bb on rj.bdz_id = bb.bdzId where rj.dlt='0' and rj.device_id = bb.deviceId and rj.report_id = '" + reportid + "'";
        try {
            dbModel = findDbModelAll(new SqlInfo(sql));
            return dbModel;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModel;
    }

    public List<DbModel> getJzlfjkgCopyRecord(String bdzId, String reportId) {

        List<DbModel> dbModel = null;
        try {
            dbModel = selector().select("*").and(ReportJzlbyqfjkg.REPORT_ID, "=", reportId).and(ReportJzlbyqfjkg.BDZ_ID, "=", bdzId).findAll();
            return dbModel;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModel;
    }
}
