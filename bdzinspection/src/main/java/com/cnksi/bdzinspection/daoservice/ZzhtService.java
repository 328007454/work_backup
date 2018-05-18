package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.zzht.Zzht;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkk on 2018/3/8.
 */

public class ZzhtService extends BaseService {

    private static ZzhtService service;

    public static ZzhtService getService() {
        if (service == null) {
            service = new ZzhtService();
        }
        return service;
    }


    public Zzht bdzInZzht(String currentBdzId) {
        Zzht zzht = null;
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(Zzht.class).where(Zzht.DLT, "=", "0").and(Zzht.BDZID, "=", currentBdzId);
            zzht = (Zzht) selector.findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return zzht;
    }

    public List<DbModel> getAllZzhtData(String bdzId, String reportId) {
        List<DbModel> dbModels = null;
        String sql = "select rzr.id rid,rz.level level,rz.sort sort ,rz.unit unit,rz.id id,rz.pid pid,rz.xsbw xsbw,rz.description description,rz.bdzid bdzid,rz.bdz_name bdz_name,rz.deptid deptid,rzr.copy_value copy_value,rz.is_copy is_copy,rzr.confirm_time confirm_time from " +
                " report_zzht rz LEFT JOIN ( SELECT * FROM report_zzht_result WHERE reportid = '" + reportId + "') rzr ON rz.id = rzr.zzht_id" +
                " WHERE rz.bdzid = '" + bdzId + "' AND rz.dlt = '0' and (rzr.reportid = '" + reportId + "' or rzr.reportid IS NULL) ORDER BY rz.LEVEL ASC, rz.sort ASC ";
        try {
            dbModels = XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (dbModels == null)
            dbModels = new ArrayList<>();
        return dbModels;
    }

}
