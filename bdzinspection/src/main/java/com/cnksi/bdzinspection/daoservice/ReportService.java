package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.bdzinspection.model.ReportSignname;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 巡检任务的数据查询
 *
 * @author terry
 */
public class ReportService {

    public static ReportService mInstance;

    private ReportService() {
    }

    public static ReportService getInstance() {
        if (mInstance == null) {
            mInstance = new ReportService();
        }
        return mInstance;
    }

    public void saveSignName(List<ReportSignname> list) throws DbException {
        XunshiApplication.getDbUtils().saveOrUpdate(list);
    }

    public void saveBatteryGroup(List<BatteryGroup> list) throws DbException {
        XunshiApplication.getDbUtils().saveOrUpdate(list);
    }

    public List<ReportSignname> getSignNamesForReportAndRole(String reportId, String rolo) throws DbException {
        Selector selector = XunshiApplication.getDbUtils().selector(ReportSignname.class).where(ReportSignname.DLT, "=", "0")
                .and(ReportSignname.REPORTID, "=", reportId).and(ReportSignname.SIGNERROLE, "=", rolo).expr("and account is not null");
        return selector.findAll();
    }

    public List<DbModel> findBatteryGroup(String currentBdzId) throws DbException {
        List<DbModel> dbModelList = null;
        String sql = "select bid ,amount  from battery where bdzid = '" + currentBdzId + "' and dlt = 0 ";
        dbModelList = XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
        return dbModelList;
    }


    public DbModel findAllBatteryCodeCount(String currentBdzId, String currentReportId) {
        DbModel dbModels = null;
        String sql = "select count(*) count from sbjc_battery where reportid = '" + currentReportId + "' and bdzid = '" + currentBdzId + "'  and dlt =0 ";
        try {
            dbModels = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
            return new DbModel();
        }
        return dbModels;

    }

    public List<BatteryGroup> findAllBatteryGroup(String currentBdzID, String currentReportId) {
        List<BatteryGroup> batteryGroups = new ArrayList<>();
        try {
            batteryGroups = XunshiApplication.getDbUtils().selector(BatteryGroup.class).where(BatteryGroup.REPORTID, "=", currentReportId).and(BatteryGroup.BDZID, "=", currentBdzID).and(BatteryGroup.DLT, "<>", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batteryGroups;
    }
}
