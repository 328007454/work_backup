package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.model.SpacingGroup;
import com.cnksi.bdzinspection.model.SpacingLastly;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 巡检任务的数据查询
 *
 * @author terry
 */
public class SpacingService {

    public static SpacingService mInstance;

    private SpacingService() {
    }

    public static SpacingService getInstance() {
        if (mInstance == null) {
            mInstance = new SpacingService();
        }
        return mInstance;
    }

    /**
     * 更新间隔的定位信息
     *
     * @return
     */
    public boolean updateSpacingLocationInfo(String spid, String lat, String lng) {
        boolean isSuccess = false;
        try {
            CustomApplication.getDbUtils().update(Spacing.class, WhereBuilder.b(Spacing.SPID, "=", spid),
                    new String[]{Spacing.LATITUDE, Spacing.LONGITUDE},
                    new String[]{lat, lng});
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * add by luoxy 查询包含抄录设备的间隔
     *
     * @param bdzId      间隔所在变电站id
     * @param deviceType 设备类型
     * @param hasCopy    是否包含抄录
     * @return
     */
    public List<Spacing> findSpacing(String bdzId, String deviceType, boolean hasCopy, String inspection) {
        String copyDeviceId = "select distinct(deviceid) from copy_item where dlt='0' and bdzid='" + bdzId + "' and kind like '%" + inspection + "%'";
        String copySpaceId = "select distinct(spid) from device where deviceid in(" + copyDeviceId + ") and dlt='0' and device_type='" + deviceType + "'";
        Selector selector = Selector.from(Spacing.class).where(Spacing.DLT, "=", "0").and(Spacing.BDZID, "=", bdzId)
                .expr((hasCopy ? " and spid in(" : "and spid not in(") + copySpaceId + ")");
        try {
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 查詢變電站下的間隔
     *
     * @param bdzId
     * @return
     */
    public List<Spacing> findSpacing(String bdzId, String deviceType) {
        Selector selector = Selector.from(Spacing.class).where(Spacing.DLT, "=", "0").and(Spacing.BDZID, "=", bdzId)
                .expr(" and spid in(select distinct(spid) from device where dlt='0' and device_type='" + deviceType + "' and bdzid='" + bdzId + "')");
        try {
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    /**
     * @param bdzid
     * @param type
     * @param sort
     * @return
     * @author wastrel
     * @date 2016年8月29日
     * @desc 根据设备类型查找间隔
     */
    public List<Spacing> findSpacingByDevicesType(String bdzid, String type, String sort) {
        Selector selector = BaseService.from(Spacing.class).where(Spacing.BDZID, "=", bdzid)
                .expr("and spid in (select distinct(spid) spid from device where device_type = '" + type
                        + "' and bdzid = '" + bdzid + "')")
                .orderBy(sort, false);
        try {
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * @param type
     * @param sort
     * @param inspectionType
     * @param reportId
     * @return
     * @author wastrel
     * @date 2016年8月29日
     * @desc 根据巡检类型和自选设备查找间隔 （用于特巡）
     */
    public List<Spacing> findSpacingByDeviceTypeAndInspection(String bdzId, String type, String sort,
                                                              String inspectionType, String reportId) {
        Selector selector;
        try {
            if (inspectionType.startsWith("special_manual")) {
                Report currentReport = CustomApplication.getDbUtils().findById(Report.class, reportId);
                String devices = "'" + (currentReport.selected_deviceid == null ? "" : currentReport.selected_deviceid)
                        + "'";
                devices = devices.replace(",", "','");
                selector = BaseService.from(Spacing.class).where(Spacing.BDZID, "=", bdzId)
                        .expr(" and spid in(SELECT DISTINCT(spid) from device d where d.deviceid in(" + devices
                                + ")and d.device_type='" + type + "' )")
                        .orderBy(sort, false);
            } else {
                // 根据类型自动查设备
                selector = BaseService.from(Spacing.class).where(Spacing.BDZID, "=", bdzId)
                        .expr("and spid in (SELECT DISTINCT(spid) from device d where d.bigid in (SELECT DISTINCT(ss.bigid) from standard_special ss where ss.kind='"
                                + inspectionType + "') and d.device_type='" + type + "' )")
                        .orderBy(sort, false);
            }
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public HashMap<String, Integer> groupBySpacingCount(String bdzId, String deviceType) {
        SqlInfo sqlInfo = new SqlInfo("SELECT spid,count(*) as c FROM device WHERE bdzid=? and dlt=0 and device_type=? GROUP BY spid;");
        sqlInfo.addBindArgs(bdzId, deviceType);
        HashMap<String, Integer> rs = new HashMap<>();
        try {
            List<DbModel> models = CustomApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (models != null) {
                for (DbModel model : models) {
                    rs.put(model.getString("spid"), model.getInt("c"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public List<SpacingGroup> findSpacingGroup(String bdzId) {
        Selector selector = BaseService.from(SpacingGroup.class).and(SpacingGroup.BDZID, "=", bdzId).orderBy(SpacingGroup.SORT);
        try {
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public SpacingLastly findSpacingLastly(String accounts, String reportId, String mode) {
        String[] accountArray = accounts.split(",");
        try {
            if (accountArray.length > 1) {
                return CustomApplication.getDbUtils().findFirst(Selector.from(SpacingLastly.class).where(SpacingLastly.REPORTID, "=", reportId).and(SpacingLastly.DEVICE_TYPE, "=", mode).expr("and account like '%" + accountArray[0] + "%' and account like '%" + accountArray[1] + "%'"));
            } else {
                return CustomApplication.getDbUtils().findFirst(Selector.from(SpacingLastly.class).where(SpacingLastly.REPORTID, "=", reportId).and(SpacingLastly.DEVICE_TYPE, "=", mode).expr("and account like '%" + accountArray[0] + "%'"));
            }
        } catch (DbException x) {
        }
        return null;
    }

}
