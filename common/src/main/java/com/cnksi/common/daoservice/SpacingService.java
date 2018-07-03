package com.cnksi.common.daoservice;

import android.text.TextUtils;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.utils.DateUtils;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ironGe on 2016/6/23.
 */
public class SpacingService extends BaseService<Spacing> {

    public static SpacingService instance;

    private SpacingService() {
        super(Spacing.class);
    }

    public static SpacingService getInstance() {
        if (null == instance) {
            instance = new SpacingService();
        }
        return instance;
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
        try {
            Selector selector = selector().and(Spacing.BDZID, "=", bdzid)
                    .expr("and spid in (select distinct(spid) spid from device where device_type = '" + type
                            + "' and bdzid = '" + bdzid + "')")
                    .orderBy(sort, false);

            return selector.findAll();
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
                Report currentReport = ReportService.getInstance().findById(reportId);
                String devices = "'" + (currentReport.selected_deviceid == null ? "" : currentReport.selected_deviceid)
                        + "'";
                devices = devices.replace(",", "','");
                selector = selector().and(Spacing.BDZID, "=", bdzId)
                        .expr(" and spid in(SELECT DISTINCT(spid) from device d where d.deviceid in(" + devices
                                + ")and d.device_type='" + type + "' )")
                        .orderBy(sort, false);
            } else {
                // 根据类型自动查设备
                selector = selector().and(Spacing.BDZID, "=", bdzId)
                        .expr("and spid in (SELECT DISTINCT(spid) from device d where d.bigid in (SELECT DISTINCT(ss.bigid) from standard_special ss where ss.kind='"
                                + inspectionType + "') and d.device_type='" + type + "' )")
                        .orderBy(sort, false);
            }
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 更新间隔的定位信息
     *
     * @return
     */
    public boolean updateSpacingLocationInfo(String spid, String lat, String lng) {
        boolean isSuccess = false;
        try {
            update(WhereBuilder.b(Spacing.SPID, "=", spid), new KeyValue(Spacing.LATITUDE, lat), new KeyValue(Spacing.LONGITUDE, lng));
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public String findSpacingByDeviceId(String deviceId) {
        SqlInfo sqlInfo = new SqlInfo("SELECT s.name from device d LEFT JOIN spacing s on d.spid=s.spid where d.dlt='0' and  d.deviceid=?");
        sqlInfo.addBindArg(new KeyValue("deviceId", deviceId));
        try {
            DbModel model = findDbModelFirst(sqlInfo);
            if (model != null) {
                return model.getString("name");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
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
        try {
            Selector selector = selector().and(Spacing.BDZID, "=", bdzId)
                    .expr((hasCopy ? " and spid in(" : "and spid not in(") + copySpaceId + ")");

            return selector.findAll();
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
        try {
            Selector selector = selector().and(Spacing.BDZID, "=", bdzId)
                    .expr(" and spid in(select distinct(spid) from device where dlt='0' and device_type='" + deviceType + "' and bdzid='" + bdzId + "')");

            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 通过间隔查询当前变电站
     */
    public List<DbModel> findBdzBySpacing() {
        List<DbModel> spacingList;
        SqlInfo sqlInfo = new SqlInfo("select bdzid,latitude,longitude from spacing where dlt <> 1 and device_type like '%one%' and (latitude  is not null and latitude<>'0' and latitude<>'') and (latitude!='0' and  longitude <> '' and longitude is not null) group by bdzid ");
        try {
            spacingList = findDbModelAll(sqlInfo);
            return spacingList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashMap<String, Integer> groupBySpacingCount(String bdzId, String deviceType) {
        SqlInfo sqlInfo = new SqlInfo("SELECT spid,count(*) as c FROM device WHERE bdzid=? and dlt=0 and device_type=? GROUP BY spid;");
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        sqlInfo.addBindArg(new KeyValue("", deviceType));
        HashMap<String, Integer> rs = new HashMap<>();
        try {
            List<DbModel> models = findDbModelAll(sqlInfo);
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

    public List<Spacing> findSpacingByModel(String currentBdzId, String model) {
        try {
            return selector().and(Spacing.BDZID, "=", currentBdzId).
                    expr("and spid in (select distinct(spid) spid from device where device_type = '" + model + "' and bdzid = '" + currentBdzId + "')").
                    orderBy(Spacing.SORT, false).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Spacing> findByFunctionModel(String reportID, String bdzId, String functionModel, String sort, String inspectionType) throws DbException {
        if (!TextUtils.isEmpty(inspectionType) && inspectionType.contains(InspectionType.special.name())) {
            String sql = "select * from special_menu where dlt =0 and k = ?";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", inspectionType));
            DbModel dbModel = CommonApplication.getInstance().getDbManager().findDbModelFirst(sqlInfo);
            if (null!=dbModel&&TextUtils.equals("select_device", dbModel.getString("device_way"))) {
                Report report = ReportService.getInstance().getReportById(reportID);
                String devices = "'" + (report == null ? "" : report.selected_deviceid) + "'";
                devices = devices.replace(",", "','");
                return selector().and(Spacing.BDZID, "=", bdzId)
                        .expr("and spid in (select distinct(spid) spid from device where device_type = '" + functionModel
                                + "' and bdzid = '" + bdzId + "' and deviceid in (" + devices + ") and dlt<>1)")
                        .orderBy(sort, false).findAll();
            }
            return findNormalSpacing(bdzId, functionModel, sort);
        } else {
            return findNormalSpacing(bdzId, functionModel, sort);
        }
    }

    public List<Spacing> findNormalSpacing(String bdzId, String functionModel, String sort) {
        List<Spacing> spacings = null;
        try {
            spacings = selector().and(Spacing.BDZID, "=", bdzId)
                    .expr("and spid in (select distinct(spid) spid from device where device_type = '" + functionModel
                            + "' and bdzid = '" + bdzId + "' and dlt<>1)")
                    .orderBy(sort, false).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return spacings;
    }

    public boolean restore(Spacing spacing) {
        String sqlDevice = "update device set spid='" + spacing.pid + "' where spid='" + spacing.spid + "'";
        String sqlCopyItem = "update copy_item set spid='" + spacing.pid + "' where spid='" + spacing.spid + "'";
        DbManager manager = getDbManager();
        try {
            manager.beginTransaction();
            execSql(sqlDevice);
            execSql(sqlCopyItem);
            execSql("update spacing set dlt=1 where spid='" + spacing.spid + "'");
            manager.setTransactionSuccessful();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            manager.endTransaction();
        }
        return false;

    }

    public boolean copySpacing(Spacing spacing, String newSpid, StringBuilder copyItemIds, StringBuilder deviceIds) {
        DbManager manager = getDbManager();
        try {
            manager.beginTransaction();
            //复制一个新间隔
            String sql = "select * from spacing where spid=?";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", spacing.pid));
            DbModel model = findDbModelFirst(sqlInfo);
            StringBuilder insertSql = new StringBuilder("INSERT INTO spacing( ");
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, String> entry : model.getDataMap().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                insertSql.append(k).append(",");
                if ("spid".equals(k)) {
                    v = spacing.spid;
                }
                if ("pid".equals(k)) {
                    v = spacing.pid;
                }
                if ("name".equals(k)) {
                    v = spacing.name;
                }
                if ("device_type".equals(k)) {
                    v = spacing.deviceType;
                }
                if ("type".equals(k)) {
                    v = "spacing_self";
                }
                if ("update_time".equals(k) || "create_time".equals(k)) {
                    v = DateUtils.getCurrentLongTime();
                }
                if (v == null) {
                    values.append("null").append(",");
                } else {
                    values.append("'").append(v).append("',");
                }
            }
            insertSql.deleteCharAt(insertSql.length() - 1).append(") VALUES(").append(values.deleteCharAt(values.length() - 1).toString()).append(");");
            //保存一个新间隔
            execSql(insertSql.toString());

            //更新设备表的SPID
            sql = "update device set spid='" + newSpid + "' where deviceid in(" + deviceIds.toString() + ")";
            execSql(sql);
            //更新抄录表的SPID
            if (copyItemIds.length() > 0) {
                copyItemIds.deleteCharAt(copyItemIds.length() - 1);
                sql = "update copy_item set spid='" + newSpid + "' where id in(" + copyItemIds.toString() + ")";
                execSql(sql);
            }
            manager.setTransactionSuccessful();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            manager.endTransaction();
        }
        return false;
    }
}
