package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.StandardSwitchover;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class StandardSwitchOverService extends BaseService<StandardSwitchover> {

    private static StandardSwitchOverService switchOverServiceInstance;

    protected StandardSwitchOverService() {
        super(StandardSwitchover.class);
    }

    public static StandardSwitchOverService getInstance() {
        if (switchOverServiceInstance == null) {
            switchOverServiceInstance = new StandardSwitchOverService();
        }
        return switchOverServiceInstance;
    }

    /**
     * 查詢所有的步驟以及步驟對應的圖片和錄音缺陷
     *
     * @throws DbException
     */
    public List<DbModel> getAllType(String currentInspectionType, String currentBdzId, String currentReportId, String reportSwitchId) throws DbException {

        String sql = " SELECT ss.id,ss.level,ss.code,ss.sort,ss.pid,ss.description,ss.unit,ss.is_copy,spic.pic,spic.voice,dr.val,dr.stand_switch_id,dr.defectlevel,dr.oldval,dr.deviceid,dr.confirm_date"
                + " FROM standard_switchover ss"
                + " LEFT JOIN (SELECT * FROM switch_pic sp WHERE sp.reportid = '" + currentReportId + "') spic ON ss.id = spic.stand_switch_id"
                + " LEFT JOIN (SELECT stand_switch_id,group_concat(val) val,max(defectlevel) defectlevel,group_concat(oldval) oldval,deviceid,group_concat(confirm_date) confirm_date FROM defect_record WHERE reportid = '" + currentReportId + "' AND DLT = '0' AND has_remove = 'N' AND has_track = 'N' GROUP BY stand_switch_id)"
                + " dr ON ss.id = dr.stand_switch_id"
                + " WHERE ss.kind = '" + currentInspectionType + "'AND ss.bdzid = '" + currentBdzId + "' and ss.rep_swithover_id = '" + reportSwitchId + "' and ss.dlt=0 order by ss.sort,ss.code";
        List<DbModel> dbModleList = findDbModelAll(new SqlInfo(sql));

        return repeatSort(dbModleList);
    }

    private ArrayList<DbModel> repeatSort(List<DbModel> dbModleList) {
        ArrayList<DbModel> newDbmodelList = new ArrayList<>();
        ArrayList<DbModel> dbModelLevel1 = new ArrayList<>();
        ArrayList<DbModel> dbModelLevel2 = new ArrayList<>();
        ArrayList<DbModel> dbModelLevel3 = new ArrayList<>();
        for (DbModel dbModel : dbModleList) {
            if ("1".equalsIgnoreCase(dbModel.getString(StandardSwitchover.LEVEL))) {
                dbModelLevel1.add(dbModel);
            } else if ("2".equalsIgnoreCase(dbModel.getString(StandardSwitchover.LEVEL))) {
                dbModelLevel2.add(dbModel);
            } else if ("3".equalsIgnoreCase(dbModel.getString(StandardSwitchover.LEVEL))) {
                dbModelLevel3.add(dbModel);
            }
        }
        for (DbModel dbModel1 : dbModelLevel1) {
            newDbmodelList.add(dbModel1);
            for (DbModel dbModel2 : dbModelLevel2) {
                if (dbModel1.getString(StandardSwitchover.ID).equalsIgnoreCase(dbModel2.getString(StandardSwitchover.PID))) {
                    newDbmodelList.add(dbModel2);
                    for (DbModel dbModel3 : dbModelLevel3) {
                        if (dbModel2.getString(StandardSwitchover.ID).equalsIgnoreCase(dbModel3.getString(StandardSwitchover.PID))) {
                            newDbmodelList.add(dbModel3);
                        }
                    }
                }

            }

        }

        return newDbmodelList;
    }


    /**
     * 查詢所有的步驟以及步驟對應的圖片和錄音缺陷
     * 新的方式，从{@link com.cnksi.bdzinspection.model.StandardStepConfirm}表里面读取状态
     *
     * @throws DbException
     */
    public List<DbModel> getAllTypeNew(String currentInspectionType, String currentBdzId, String currentReportId, String reportSwitchId) throws DbException {
        String sql = " SELECT ss.id,ss.level,ss.code,ss.sort,ss.description,ss.unit,ss.pid,ss.is_copy,spic.pic,spic.voice,dr.val,dr.stand_switch_id,dr.defectlevel,dr.oldval,dr.deviceid,stc.confirm_date"
                + " FROM standard_switchover ss"
                + " LEFT JOIN (SELECT * FROM switch_pic sp WHERE sp.reportid = ? ) spic ON ss.id = spic.stand_switch_id"
                + " LEFT JOIN (SELECT stand_switch_id,group_concat(val) val,max(defectlevel) defectlevel,group_concat(oldval) oldval,deviceid FROM defect_record WHERE reportid = ? AND has_remove = 'N' AND has_track = 'N' GROUP BY stand_switch_id)"
                + " dr ON ss.id = dr.stand_switch_id "
                + " LEFT JOIN (select standid,confirm_date from standard_step_confirm where bdzid=?  and reportid=?) stc on stc.standid=ss.id "
                + " WHERE ss.kind =  ? AND ss.bdzid = ?  and ss.rep_swithover_id = '" + reportSwitchId + "' and ss.dlt=0 order by ss.sort ,ss.code";

        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", currentReportId));
        sqlInfo.addBindArg(new KeyValue("", currentReportId));
        sqlInfo.addBindArg(new KeyValue("", currentBdzId));
        sqlInfo.addBindArg(new KeyValue("", currentReportId));
        sqlInfo.addBindArg(new KeyValue("", currentInspectionType));
        sqlInfo.addBindArg(new KeyValue("", currentBdzId));
        List<DbModel> dbModleList = findDbModelAll(sqlInfo);
        return repeatSort(dbModleList);
    }



    public String getStandardMaxLevel(String standardId, String reportId) throws DbException {
        String sql = " SELECT  max(defectlevel) maxdefect FROM defect_record dr where dr.reportid =? AND has_remove = 'N' " +
                "AND has_track = 'N' and  dr.stand_switch_id =? and dr.dlt = '0'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", reportId));
        sqlInfo.addBindArg(new KeyValue("", standardId));
        DbModel model = findDbModelFirst(sqlInfo);
        return model.getString("maxdefect");
    }


    /**
     * 完成任务
     */
    public long getSwitchOverCopyTotal(String type, String bdzId) {
        long total = 0;
        List<StandardSwitchover> standardSwitchOvers = null;
        try {
            standardSwitchOvers =selector().and(StandardSwitchover.BDZID, "=", bdzId).and(StandardSwitchover.KIND, "=", type).and(StandardSwitchover.ISCOPY, "=", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (null == standardSwitchOvers) {
            total = 0;
        } else {
            total = standardSwitchOvers.size();
        }
        return total;
    }

}
