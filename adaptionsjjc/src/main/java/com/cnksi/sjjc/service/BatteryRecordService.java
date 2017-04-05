package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.BatteryRecord;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by han on 2016/6/19.
 */
public class BatteryRecordService extends BaseService<BatteryRecord> {
    private static BatteryRecordService instance;

    private BatteryRecordService() {
        super(BatteryRecord.class);
    }

    public static BatteryRecordService getInstance() {
        if (null == instance) {
            instance = new BatteryRecordService();
        }
        return instance;
    }

    /**
     * 查询当前报告所有蓄电池记录
     */
    public List<BatteryRecord> getAllBatteryRecord(String reportId) throws DbException {
        return selector().and(BatteryRecord.REPORTID, "=", reportId).findAll();
    }

    /**
     * 查询当前报告所有蓄电池记录
     */
    public List<BatteryRecord> getBatteryRecord(String reportId, String batteryGroupId) throws DbException {
        return selector().and(BatteryRecord.REPORTID, "=", reportId).and(BatteryRecord.BATTARY_GROUP, "=", batteryGroupId).orderBy(BatteryRecord.BATTARY_CODE, false).findAll();
    }

    /**
     * 查询蓄电池记录
     */
    public BatteryRecord getBatteryRecord(String reportId, String batteryGroupId, String batteryCode) throws DbException {
        return selector().and(BatteryRecord.REPORTID, "=", reportId).and(BatteryRecord.BATTARY_GROUP, "=", batteryGroupId).and(BatteryRecord.BATTARY_CODE, "=", batteryCode).findFirst();
    }

    /**
     * 查询蓄电池各项最新记录最新值
     */
    @SuppressWarnings("unchecked")
    public List<BatteryRecord> getBatteryRecordLatest(String reportId, String batteryGroupId, boolean inReport) throws DbException {
        String operate = (inReport) ? "=" : "!=";
        Selector selector = selector().and(BatteryRecord.REPORTID, operate, reportId)
                .and(BatteryRecord.BATTARY_GROUP, "=", batteryGroupId).expr("group by " + BatteryRecord.BATTARY_CODE + " order by  " + BatteryRecord.LAST_MODIFY_TIME + " DESC");
        return selector.findAll();
    }


}
