package com.cnksi.common.daoservice;

import android.text.TextUtils;

import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缺陷查询统计
 *
 * @author
 */
public class DefectRecordService extends BaseService<DefectRecord> {

    public static DefectRecordService mInstance;

    private DefectRecordService() {
        super(DefectRecord.class);
    }

    public static DefectRecordService getInstance() {
        if (mInstance == null) {
            mInstance = new DefectRecordService();
        }
        return mInstance;
    }

    /**
     * 根据设备ID查询缺陷信息
     *
     * @param deviceid
     * @return
     */
    public List<DefectRecord> queryDefectByDeviceid(String deviceid, String bdzid) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.DEVICEID, "=", deviceid)
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.BDZID, "=", bdzid).and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 根据设备ID查询缺陷信息
     *
     * @param deviceid
     * @return
     */
    public List<DefectRecord> queryDefectByDeviceid(String deviceid, String bdzid, String reportId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.DEVICEID, "=", deviceid).and(DefectRecord.REPORTID, "=", reportId)
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.BDZID, "=", bdzid).and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 根据缺陷Code查询某个缺陷的历史跟踪缺陷
     *
     * @return
     */
    public List<DefectRecord> queryDefectHistoryByDefectCode(String defectCode) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.DEFECTCODE, "=", defectCode)
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                    .orderBy(DefectRecord.UPDATE_TIME, true);
            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询电池记录的缺陷
     *
     * @param batteryId  电池组 id
     * @param batteryNum 电池编号
     * @param bdzId      变电站id
     * @return
     */
    public List<DefectRecord> queryDefectByBatteryId(String batteryId, String batteryNum, String bdzId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.SPID, "=", batteryId)
                    .and(DefectRecord.DEVICEID, "=", batteryNum).and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.BDZID, "=", bdzId)
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }


    /**
     * 查询运维一体化电池记录的最高缺陷等级
     *
     * @param batteryNum 电池编号
     * @param bdzId      变电站id
     * @return
     */
    public DbModel queryMaxDefectLevelByBatteryId(String batteryNum, String bdzId, String reportid) {
        DbModel batteryDbModel = null;
        try {
            String sql = "select *,count(1) as defect_count_key from (select bd.*,dr.defectlevel from battery_details bd left join (select dr.* from defect_record dr where dr.has_track='N' and dr.has_remove='N' AND (dr.val='' OR dr.val IS NULL) and bdzid='"
                    + bdzId
                    + "' order by dr.deviceid,dr.defectlevel asc) dr on dr.deviceid=bd.battery_number and dr.reportid=bd.reportid and dr.spid=bd.batteryid where bd.battery_number='"
                    + batteryNum + "' and bd.reportid='" + reportid + "') group by battery_number";
            SqlInfo sqlInfo = new SqlInfo(sql);
            batteryDbModel = findDbModelFirst(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batteryDbModel;
    }


    /**
     * 查询变电站现存的所有缺陷
     *
     * @return
     */
    public List<DefectRecord> queryCurrentBdzExistDefectList(String bdzId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.BDZID, "=", bdzId)
                    .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.HAS_REMOVE, "=", "N")
                    .and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, false);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视发现的缺陷
     *
     * @return
     */
    public List<DefectRecord> findCurrentTaskNewDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.BDZID, "=", bdzId)
                    .and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                    .and(DefectRecord.REPORTID, "=", reportId).and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.DLT, "=", "0")
                    .orderBy(DefectRecord.DISCOVERED_DATE, false);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视跟踪的缺陷
     *
     * @return
     */
    public List<DefectRecord> findCurrentTaskTrackDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.BDZID, "=", bdzId)
                    .and(DefectRecord.HAS_TRACK, "=", "Y").and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                    .and(DefectRecord.DLT, "=", "0")
                    .and(DefectRecord.REPORTID, "=", reportId).orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视消除的缺陷
     *
     * @return
     */
    public List<DefectRecord> findCurrentTaskEliminateDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.BDZID, "=", bdzId)
                    .and(DefectRecord.HAS_REMOVE, "=", "Y").and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                    .and(DefectRecord.DLT, "=", "0")
                    .and(DefectRecord.REPORTID, "=", reportId).orderBy(DefectRecord.DISCOVERED_DATE, true);
            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }


    /**
     * 根据条件查询当前设备需要抄录的总项数目是否与当前record表中该设备已经抄录的总数据数目是否相等。
     *
     * @param reportId
     * @return
     */
    public Map<String, List<String>> getCopyDevice(String reportId, String bdzid, String deviceType, String inspection) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        String sql = "SELECT tb_cpt.deviceid,device.spid FROM "
                + "(SELECT deviceid,count(CASE WHEN val = 'Y' THEN 1 ELSE NULL END ) +"
                + " count(CASE WHEN val_a = 'Y' THEN 1 ELSE NULL END) + count(CASE   WHEN val_b = 'Y' THEN 1 ELSE NULL END ) + "
                + "count(CASE WHEN val_c = 'Y' THEN 1 ELSE NULL END) + count(CASE WHEN val_o = 'Y' THEN 1 ELSE NULL END) AS cpt FROM  copy_item where bdzid=? and dlt = '0' and kind like '%" + inspection + "%' "
                + "GROUP BY  deviceid) as tb_cpt LEFT JOIN (select deviceid,count(CASE WHEN val = '' THEN NULL ELSE val END)+"
                + "count(CASE WHEN val_a = '' THEN NULL ELSE val_a END)+"
                + "count(CASE WHEN val_b = '' THEN NULL ELSE val_b END)+"
                + "count(CASE WHEN val_c = '' THEN NULL ELSE val_c END)+"
                + "count(CASE WHEN val_special = '' THEN NULL ELSE val_special END)+"
                + "count(CASE WHEN val_o = '' THEN NULL ELSE val_o END) as cpc from copy_result result where"
                + " result.reportId=? and result.bdzid=? and result.dlt='0' GROUP BY deviceid) as tb_cpc on tb_cpc.deviceid=tb_cpt.deviceid "
                + "LEFT JOIN device on tb_cpt.deviceid=device.deviceid where cpc=cpt "
                + ("search_device_key".equals(deviceType) ? "" : "and device.device_type=?") + " ORDER BY spid";
        SqlInfo sqlInfo;
        if ("search_device_key".equals(deviceType)) {
            sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", bdzid));
            sqlInfo.addBindArg(new KeyValue("", reportId));
            sqlInfo.addBindArg(new KeyValue("", bdzid));
        } else {
            sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", bdzid));
            sqlInfo.addBindArg(new KeyValue("", reportId));
            sqlInfo.addBindArg(new KeyValue("", bdzid));
            sqlInfo.addBindArg(new KeyValue("", deviceType));
        }
        try {
            List<DbModel> dataList = findDbModelAll(sqlInfo);
            if (null != dataList && !dataList.isEmpty()) {
                for (DbModel dbModel : dataList) {
                    String spid = dbModel.getString(Device.SPID);
                    String deviceId = dbModel.getString(Device.DEVICEID);
                    if (map.keySet().contains(spid)) {
                        map.get(spid).add(deviceId);
                    } else {
                        List<String> list = new ArrayList<String>();
                        list.add(deviceId);
                        map.put(spid, list);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return map;
    }


    public List<DefectRecord> getReportDefectRecords(String reportId, String bdzId) {
        List<DefectRecord> defectRecords;
        try {
            Selector selector = selector().and(DefectRecord.REPORTID, "=", reportId)
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.BDZID, "=", bdzId).and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);
            defectRecords = selector.findAll();
            if (null == defectRecords) {
                defectRecords = new ArrayList<>();
            }
        } catch (DbException e) {
            defectRecords = new ArrayList<>();
            e.printStackTrace();
        }
        return defectRecords;
    }

    /**
     * 定期切换和维护找到标准抄录的第一条
     *
     * @throws DbException
     */
    public DefectRecord findFirstCopyRecord(String currentReportId, String standId) {
        DefectRecord deRecord = null;
        try {
            deRecord = selector().and(DefectRecord.REPORTID, "=", currentReportId).and(DefectRecord.STADIDSWICHERID, "=", standId).and(DefectRecord.IS_COPY, "=", "Y").findFirst();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return deRecord;
    }

    /**
     * 查询变电站现存的所有缺陷
     *
     * @return
     */
    public List<DefectRecord> queryCurrentBdzExistDefectList(String bdzId, String userName, int level, String deptName) {
        List<DefectRecord> defects = new ArrayList<>();
        try {
            Selector<DefectRecord> selector = selector().and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.HAS_REMOVE, "=", "N")
                    .and(DefectRecord.IS_COPY, "<>", "Y");
            if (!TextUtils.isEmpty(bdzId) && !TextUtils.equals("-1", bdzId)) {
                selector.and(DefectRecord.BDZID, "=", bdzId);
            } else {
                selector.and(DefectRecord.DISCOVERER_UNIT, "=", deptName);
            }

            if (!TextUtils.equals(userName, "全部")) {
                selector.expr(" and discoverer  like '%" + userName + "%'");
            }

            if (level > 0) {
                selector.and(DefectRecord.DEFECTLEVEL, "=", level);
            }
            selector.expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站现存的所有缺陷
     *
     * @return
     */
    public List<DefectRecord> queryCurrentBdzExistDefectList() {
        List<DefectRecord> defects = null;
        try {
            defects = selector().and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.HAS_REMOVE, "=", "N")
                    .and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    public List<DefectRecord> queryDefectByDefectId(String defectId, String bdzId) {
        List<DefectRecord> defects = null;
        try {
            Selector selector = selector().and(DefectRecord.DEFECTID, "=", defectId)
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.BDZID, "=", bdzId).and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;

    }

    public List<DefectRecord> queryDefectSwitchOverId(String currentReportId, String standardId) {

        List<DefectRecord> defects = null;
        try {
            Selector selector = selector()
                    .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.REPORTID, "=", currentReportId).and(DefectRecord.IS_COPY, "<>", "Y")
                    .and(DefectRecord.STADIDSWICHERID, "=", standardId)
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true);

            defects = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    public List<DbModel> queryDefectStidByDtid(String deviceDtid, String standardId) {
        List<DbModel> models = new ArrayList<>();
        SqlInfo sqlInfo = new SqlInfo();
        String sql = "";
        if (TextUtils.isEmpty(standardId)) {
            sql = "select * from defect where staid in (select  s.staid from standards s left join device_unit  u on s.duid = u.duid where u.dtid = ? and u.dlt = 0 and s.dlt = 0) and dlt =0";
            sqlInfo.setSql(sql);
            sqlInfo.addBindArg(new KeyValue("", deviceDtid));
        } else {
            sql = "select * from defect where staid = ? and dlt = 0";
            sqlInfo.setSql(sql);
            sqlInfo.addBindArg(new KeyValue("", standardId));
        }

        try {
            models = getDbManager().findDbModelAll(sqlInfo);

        } catch (DbException e) {
            e.printStackTrace();
        }
        return models;
    }

    public List<DefectRecord> findAllDefectRecords() {
        List<DefectRecord> defectRecords = new ArrayList<>();
        try {
            defectRecords = selector().expr("AND has_track = 'N'\n" +
                    "        AND has_remove = 'N'\n" +
                    "        and is_copy <> 'Y'\n" +
                    "        and (val = '' or val is NULL)\n" +
                    "        and discovered_date > (select datetime('now','localtime','start of day','-6 days','weekday 1'))\n" +
                    "        and discovered_date < (select datetime('now','localtime','start of day','23 hours','59 minutes','59 seconds','0 day','weekday 0') \n" +
                    ")\n" +
                    "        ORDER BY\n" +
                    "        discovered_date DESC").findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return defectRecords;
        }
        return defectRecords;


    }
}
