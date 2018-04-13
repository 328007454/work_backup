package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.BatteryDetails;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Device;
import com.cnksi.bdzinspection.model.DevicePart;
import com.cnksi.bdzinspection.model.DeviceStandards;
import com.cnksi.bdzinspection.model.SwitchPic;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.lidroid.xutils.db.sqlite.DbModelSelector;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缺陷查询统计
 *
 * @author
 */
public class DefectRecordService {

    public static DefectRecordService mInstance;

    private DefectRecordService() {
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
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.DEVICEID, "=", deviceid)
                .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                .and(DefectRecord.BDZID, "=", bdzid).and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                .orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
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
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.DEVICEID, "=", deviceid).and(DefectRecord.REPORTID, "=", reportId)
                .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                .and(DefectRecord.BDZID, "=", bdzid).and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)  AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                .orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 根据缺陷Code查询某个缺陷的历史跟踪缺陷
     *
     * @param deviceid
     * @return
     */
    public List<DefectRecord> queryDefectHistoryByDefectCode(String defectCode) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.DEFECTCODE, "=", defectCode)
                .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                .orderBy(DefectRecord.UPDATE_TIME, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
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
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.SPID, "=", batteryId)
                .and(DefectRecord.DEVICEID, "=", batteryNum).and(DefectRecord.HAS_TRACK, "=", "N")
                .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.BDZID, "=", bdzId)
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                .orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询电池记录的最高缺陷等级
     *
     * @param batteryId  电池组 id
     * @param batteryNum 电池编号
     * @param bdzId      变电站id
     * @return
     */
    public DbModel queryMaxDefectLevelByBatteryId(String batteryId, String batteryNum, String bdzId, String reportid) {
        DbModel batteryDbModel = null;
        try {
            String sql = "select *,count(1) as defect_count_key from (select bd.*,dr.defectlevel from battery_details bd left join (select dr.* from defect_record dr where dr.has_track='N' and dr.has_remove='N' AND (dr.val='' OR dr.val IS NULL) and bdzid='"
                    + bdzId
                    + "' order by dr.deviceid,dr.defectlevel asc) dr on dr.deviceid=bd.battery_number and dr.reportid=bd.reportid and dr.spid=bd.batteryid where bd.battery_number='"
                    + batteryNum + "' and bd.reportid='" + reportid + "' and bd.batteryid='" + batteryId
                    + "') group by battery_number";
            SqlInfo sqlInfo = new SqlInfo(sql);
            batteryDbModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batteryDbModel;
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
            batteryDbModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batteryDbModel;
    }

    /**
     * 查询运维一体化电池记录
     *
     * @param reportid 报告ID
     * @return
     */
    public List<BatteryDetails> queryAllRecordBattery(String reportid) {
        List<BatteryDetails> batterys = null;
        try {
            batterys = XunshiApplication.getDbUtils().findAll(Selector.from(BatteryDetails.class)
                    .where(BatteryDetails.REPORTID, "=", reportid).orderBy(BatteryDetails.BATTERY_NUMBER, false));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batterys;
    }

    /**
     * 查询红外测温的所有测温记录
     *
     * @param isTemperatureRecord 是否是测温记录
     * @return
     */
    public List<DefectRecord> queryDefectByInfrared(String bdzId, String reportId, boolean isTemperatureRecord) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", reportId)
                .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.HAS_REMOVE, "=", "N")
                .and(DefectRecord.BDZID, "=", bdzId);
        if (isTemperatureRecord) {
            selector = selector.expr("AND " + DefectRecord.VAL + "<>'' AND " + DefectRecord.VAL + " IS NOT NULL");
        } else {
            selector = selector.expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND "
                    + DefectRecord.DESCRIPTION + "<>'' AND " + DefectRecord.DESCRIPTION + " IS NOT NULL");
        }
        selector = selector.orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
            if (isTemperatureRecord && defects != null) {
                for (DefectRecord mDefectRecord : defects) {
                    selector = Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", reportId)
                            .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.HAS_REMOVE, "=", "N")
                            .and(DefectRecord.BDZID, "=", bdzId).and(DefectRecord.DEVICEID, "=", mDefectRecord.deviceid)
                            .and(DefectRecord.SPID, "=", mDefectRecord.spid)
                            .and(DefectRecord.DUID, "=", mDefectRecord.duid)
                            .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND "
                                    + DefectRecord.DESCRIPTION + "<>'' AND " + DefectRecord.DESCRIPTION
                                    + " IS NOT NULL");
                    long count = XunshiApplication.getDbUtils().count(selector);
                    if (count > 0) {
                        mDefectRecord.has_infrade_temperature_question = true;
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站现存的所有缺陷
     *
     * @param deviceid
     * @return
     */
    public List<DefectRecord> queryCurrentBdzExistDefectList(String bdzId) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.BDZID, "=", bdzId)
                .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.HAS_REMOVE, "=", "N")
                .and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                .orderBy(DefectRecord.DISCOVERED_DATE, false);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视发现的缺陷
     *
     * @param level
     * @return
     */
    public List<DefectRecord> findCurrentTaskNewDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.BDZID, "=", bdzId)
                .and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                .and(DefectRecord.REPORTID, "=", reportId).and(DefectRecord.HAS_TRACK, "=", "N")
                .and(DefectRecord.DLT, "=", "0")
                .orderBy(DefectRecord.DISCOVERED_DATE, false);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视跟踪的缺陷
     *
     * @param level
     * @return
     */
    public List<DefectRecord> findCurrentTaskTrackDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.BDZID, "=", bdzId)
                .and(DefectRecord.HAS_TRACK, "=", "Y").and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                .and(DefectRecord.DLT, "=", "0")
                .and(DefectRecord.REPORTID, "=", reportId).orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视消除的缺陷
     *
     * @param level
     * @return
     */
    public List<DefectRecord> findCurrentTaskEliminateDefectList(String bdzId, String reportId) {
        List<DefectRecord> defects = null;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.BDZID, "=", bdzId)
                .and(DefectRecord.HAS_REMOVE, "=", "Y").and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)")
                .and(DefectRecord.DLT, "=", "0")
                .and(DefectRecord.REPORTID, "=", reportId).orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defects = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询变电站本次巡视发现的缺陷
     *
     * @param level
     * @return
     */
    public List<DbModel> queryCurrentBdzCurrentTaskDefectList(String bdzId, String taskId) {
        List<DbModel> defects = null;
        SqlInfo sqlInfo = new SqlInfo("SELECT * FROM defect_record WHERE bdzid = '" + bdzId
                + "' AND has_track = 'N' and is_copy<>'Y' AND has_remove = 'N' AND (val='' OR val IS NULL) AND reportid = (select reportid from report where report.taskid = '"
                + taskId + "') ORDER BY discovered_date DESC");
        try {
            defects = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }

    /**
     * 查询定期维护过程中拍摄的图片
     *
     * @param reportId
     * @param bdzId
     * @return
     * @throws DbException
     */
    public List<DbModel> getCurrentTaskSwitchPic(String reportId, String bdzId) throws DbException {
        DbModelSelector selector = DbModelSelector.from(SwitchPic.class).where(SwitchPic.REPORTID, "=", reportId)
                .and(SwitchPic.BDZID, "=", bdzId);
        List<DbModel> mSwitchPicList = XunshiApplication.getDbUtils().findDbModelAll(selector);
        return mSwitchPicList;
    }

    /**
     * 根据变电站id和巡检类型、专项巡检标准id 并查询记录的缺陷
     *
     * @param inspectionType
     * @param bdzId
     * @param stadId
     * @return
     */
    public List<DefectRecord> findStandardAndDefectRecordByStandardId(String inspectionType, String bdzId,
                                                                      String stadId) {
        // TODO: 是查询本次发现的缺陷 还是 查询所有的现存的缺陷
        List<DefectRecord> defectRecordList = null;
        try {
            defectRecordList = XunshiApplication.getDbUtils()
                    .findAll(Selector.from(DefectRecord.class).where(DefectRecord.BDZID, "=", bdzId)
                            .and(DefectRecord.STADID, "=", stadId).and(DefectRecord.HAS_TRACK, "=", "N")
                            .and(DefectRecord.IS_COPY, "<>", "Y").and(DefectRecord.HAS_REMOVE, "=", "N")
                            .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL)"));
        } catch (DbException e) {
            e.printStackTrace();
        }

        return defectRecordList;
    }

    /**
     * 根据巡视标准Id查询最高等级的缺陷
     *
     * @param standardId
     * @return
     */
    public DbModel getMaxLevelByStandard(String bdzId, DbModel mStandardModel) {
        DbModel mDefectRecordModel = null;
        try {
            SqlInfo sqlInfo = new SqlInfo(
                    "select d.defectlevel,count(1) as defect_count_key from (select d.*,dr.defectlevel from device_standards d left join (select * from defect_record where has_track='N' and is_copy<>'Y' and has_remove='N' AND (val='' OR val IS NULL) and bdzid='"
                            + bdzId
                            + "') dr on d.staid=dr.standid where dr.standid=? order by dr.standid,dr.defectlevel asc) d group by d.staid",
                    mStandardModel.getString(DeviceStandards.STAID));
            mDefectRecordModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            if (mDefectRecordModel != null) {
                mStandardModel.add(Config.DEFECT_COUNT_KEY, mDefectRecordModel.getString(Config.DEFECT_COUNT_KEY));
                mStandardModel.add(DefectRecord.DEFECTLEVEL, mDefectRecordModel.getString(DefectRecord.DEFECTLEVEL));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDefectRecordModel;
    }

    /**
     * 根据设备id查询最高等级的缺陷
     *
     * @param standardId
     * @return
     */
    public DbModel getMaxLevelByDevice(DbModel mDeviceModel) {
        DbModel mDefectRecordModel = null;
        try {
            SqlInfo sqlInfo = new SqlInfo(
                    "select d.defectlevel,count(1) as defect_count_key from (select d.*,dr.defectlevel from device d left join (select * from defect_record where has_track='N' and is_copy<>'Y' and has_remove='N' AND (val='' OR val IS NULL) AND (dlt = '0' OR dlt IS NULL)) dr on d.deviceid = dr.deviceid where d.deviceid=? order by dr.deviceid,dr.defectlevel asc) d  group by d.deviceid",
                    mDeviceModel.getString(Device.DEVICEID));
            mDefectRecordModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            if (mDefectRecordModel != null) {
                mDeviceModel.add(Config.DEFECT_COUNT_KEY, mDefectRecordModel.getString(Config.DEFECT_COUNT_KEY));
                mDeviceModel.add(DefectRecord.DEFECTLEVEL, mDefectRecordModel.getString(DefectRecord.DEFECTLEVEL));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDefectRecordModel;
    }

    /**
     * 根据设备id查询最高等级的缺陷
     *
     * @param standardId
     * @return
     */
    public DbModel getMaxLevelByDevicePart(DbModel mDevicePartModel) {
        DbModel mDefectRecordModel = null;
        try {
            SqlInfo sqlInfo = new SqlInfo(
                    "select d.defectlevel,d.name as duname,count(1) as defect_count_key from (select d.*,dr.defectlevel from device_part d left join (select * from defect_record where has_track='N' and is_copy<>'Y' and has_remove='N' AND (val='' OR val IS NULL)) dr on d.duid = dr.duid where dr.duid=? order by dr.duid,dr.defectlevel asc) d  group by d.duid",
                    mDevicePartModel.getString(DevicePart.DUID));
            mDefectRecordModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            if (mDefectRecordModel != null) {
                mDevicePartModel.add(Config.DEFECT_COUNT_KEY, mDefectRecordModel.getString(Config.DEFECT_COUNT_KEY));
                mDevicePartModel.add(DefectRecord.DEFECTLEVEL, mDefectRecordModel.getString(DefectRecord.DEFECTLEVEL));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDefectRecordModel;
    }

    /**
     * 得到所有的抄录数据
     *
     * @return
     */
    public List<DbModel> getHistoryLineDataModelList() {

        List<DbModel> dataList = null;
        SqlInfo sqlInfo = new SqlInfo(
                "select strftime('%m-%d',discovered_date) discovered_date,val from defect_record where val is not null and val <>''");

        try {
            dataList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    /**
     * 恢复抄录数据
     *
     * @param bdzid
     */
    public void recoverCopyData(String bdzid) {
        String sql = "select deviceid,standid,val from defect_record where val <> '' and val is not null and bdzid = '"
                + bdzid + "' GROUP BY deviceid,standid order by discovered_date desc";
        SqlInfo sqlInfo = new SqlInfo(sql);
        try {
            // if (!PreferencesUtils.getBoolean(XunshiApplication.getInstance(), Config.IS_RECOVER_COPY_DATA + bdzid,
            // false)) {
            List<DbModel> dataList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (dataList != null) {
                for (DbModel model : dataList) {
                    // 标准+设备确保抄录唯一性
                    String key = model.getString("standid") + "_" + model.getString("deviceid");
                    String value = model.getString("val");
                    PreferencesUtils.put(XunshiApplication.getInstance(), key, value);
                }
                PreferencesUtils.put(XunshiApplication.getInstance(), Config.IS_RECOVER_COPY_DATA + bdzid, true);
            }
            // }
        } catch (DbException e) {
            e.printStackTrace();
        }
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
            sqlInfo = new SqlInfo(sql, bdzid, reportId, bdzid);
        } else {
            sqlInfo = new SqlInfo(sql, bdzid, reportId, bdzid, deviceType);
        }
        try {
            List<DbModel> dataList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
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

    /**
     * 根据条件查询当前设备需要抄录的总项数目是否与当前record表中该设备已经抄录的总数据数目是否相等。
     *
     * @param reportId
     * @return
     */

    public DefectRecord getHighestRecord(String kindId, String reportId) {
        DefectRecord defectRecord = new DefectRecord();
        int level = -1;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", reportId)
                .and(DefectRecord.STADIDSWICHERID, "=", kindId).and(DefectRecord.HAS_REMOVE, "=", "N")
                .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.IS_COPY, "=", "N");
        try {
            List<DefectRecord> defectRecords = XunshiApplication.getDbUtils().findAll(selector);
            if (null != defectRecords && defectRecords.size() > 0)
                for (DefectRecord defect : defectRecords) {
                    if (level < Integer.parseInt(defect.defectlevel)) {
                        defectRecord = defect;
                        level = Integer.parseInt(defect.defectlevel);
                    }
                }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return defectRecord;
    }

    /**
     * @param reportId
     * @return
     */
    public DefectRecord getCopyRecord(String kindId, String reportId) {
        DefectRecord defectRecord = new DefectRecord();
        int level = -1;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", reportId)
                .and(DefectRecord.STADIDSWICHERID, "=", kindId).and(DefectRecord.HAS_REMOVE, "=", "N")
                .and(DefectRecord.HAS_TRACK, "=", "N").and(DefectRecord.IS_COPY, "=", "Y");
        try {
            defectRecord = XunshiApplication.getDbUtils().findFirst(selector);
            if (defectRecord == null)
                defectRecord = new DefectRecord();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defectRecord;
    }

    public List<DefectRecord> getReportDefectRecords(String reportId, String bdzId) {
        List<DefectRecord> defectRecords;
        Selector selector = Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", reportId)
                .and(DefectRecord.HAS_REMOVE, "=", "N").and(DefectRecord.HAS_TRACK, "=", "N")
                .and(DefectRecord.BDZID, "=", bdzId).and(DefectRecord.IS_COPY, "<>", "Y")
                .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                .orderBy(DefectRecord.DISCOVERED_DATE, true);
        try {
            defectRecords = XunshiApplication.getDbUtils().findAll(selector);
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
            deRecord = XunshiApplication.getDbUtils().findFirst(Selector.from(DefectRecord.class).where(DefectRecord.REPORTID, "=", currentReportId).and(DefectRecord.STADIDSWICHERID, "=", standId).and(DefectRecord.IS_COPY, "=", "Y"));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return deRecord;
    }
}
