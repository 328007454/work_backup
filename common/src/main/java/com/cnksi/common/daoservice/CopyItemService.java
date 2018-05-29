package com.cnksi.common.daoservice;


import android.text.TextUtils;

import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.CopyResult;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wastrel
 */
public class CopyItemService extends BaseService<CopyItem> {
    public static CopyItemService mInstance;

    private CopyItemService() {
        super(CopyItem.class);
    }

    public static CopyItemService getInstance() {
        if (mInstance == null) {
            mInstance = new CopyItemService();
        }
        return mInstance;
    }

    /**
     * 查询设备表中在copyItem中的数据
     */
    public List<String> findAllDeviceInCopyItem(String currentInspectionType, String bdzId) throws DbException {
        String sql = "SELECT DISTINCT deviceid FROM	copy_item WHERE kind LIKE '%" + currentInspectionType
                + "%' and bdzid = '" + bdzId + "' AND dlt='0' ";
        List<String> deviceIdList = new ArrayList<String>();
        List<DbModel> modelList = findDbModelAll(new SqlInfo(sql));
        if (null != modelList && !modelList.isEmpty()) {
            for (DbModel dbModel : modelList) {
                deviceIdList.add(dbModel.getString(Device.DEVICEID));
            }
        }

        return deviceIdList;
    }

    public List<DbModel> getCopyDevicebySpidList(String bdzId, String deviceType, String inspection, String spid) {
        String sort = "one".equals(deviceType) ? Spacing.SORT_ONE : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;

        String sql = "select d.deviceid,d.name,d.is_important,d.latitude,d.spid,d.device_type,d.longitude,sp.name as sname from device d "
                + "left join spacing sp on d.spid=sp.spid where d.deviceid in( SELECT DISTINCT(deviceid) from copy_item WHERE bdzid=? and " + "kind like '%" + inspection
                + "%' and dlt = '0') and  d.spid = '" + spid + "' and d.device_type=? order by sp." + sort;
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        sqlInfo.addBindArg(new KeyValue("", deviceType));
        try {
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyItemCount1(String bdzId, String inspection) {
//		String sql = cptSql + "where item.bdzid=? and type_key in ('" + inspection + "')";
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and type_key in ('" + inspection + "') ";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 查询设备下的所有抄录项目（全面、例行等）
     *
     * @param bdzId
     * @param deviceId
     * @return
     */
    public List<CopyItem> getDeviceALLCopyItem(String bdzId, String deviceId) {
        try {
            return selector().and(CopyItem.BDZID, "=", bdzId).and(CopyItem.DEVICEID, "=", deviceId).
                    orderBy(CopyItem.ID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CopyItem> getDeviceCopyItemByKey(String bdzId, String deviceId, String copyType) {
        try {
            Selector<CopyItem> selector = selector().and(CopyItem.BDZID, "=", bdzId)
                    .and(CopyItem.DEVICEID, "=", deviceId).expr(" and " + CopyItem.TYPE_KEY + " in('" + copyType + "') ").orderBy(CopyItem.ID);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<CopyItem> getDeviceCopyItemByInspectionType(String bdzId, String deviceId, String inspectionType) {
        try {
            return selector().and(CopyItem.BDZID, "=", bdzId).and(CopyItem.DEVICEID, "=", deviceId).
                    and(CopyItem.KIND, "like", "%" + inspectionType + "%").and(CopyItem.DLT, "=", 0).orderBy(CopyItem.ID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<DbModel> getCopyDeviceList(String bdzId, String deviceType, String inspection, String deviceWay, String reportId) {

        String devices = "";
        String sql = "";
        String sort = "one".equals(deviceType) ? Spacing.SORT_ONE : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;
        sql = "select d.name_short name_short ,d.deviceid,d.is_important,d.name,d.latitude,d.spid,d.device_type,d.longitude,sp.name as sname from device d "
                + "left join spacing sp on d.spid=sp.spid where d.deviceid in( SELECT DISTINCT(deviceid) from copy_item WHERE bdzid=? and " + "kind like '%" + inspection
                + "%' and dlt = '0')  and d.device_type=?";

        if (!TextUtils.isEmpty(deviceWay) && "bigtype_device".equalsIgnoreCase(deviceWay)) {
            sql = sql + " and d.bigid in ( select bigid from standard_special where kind = '" + inspection + "' and dlt = 0 )";
        }
        if (!TextUtils.isEmpty(deviceWay) && "select_device".equalsIgnoreCase(deviceWay)) {
            Report currentReport = null;
            try {
                currentReport = ReportService.getInstance().findById(reportId);
            } catch (DbException e) {
                e.getMessage();
            }
            devices = "'" + (currentReport == null ? "" : currentReport.selected_deviceid) + "'";
            devices = devices.replace(",", "','");
            sql = "select d.name_short name_short ,d.deviceid,d.is_important,d.name,d.latitude,d.spid,d.device_type,d.longitude,sp.name as sname from device d "
                    + "left join spacing sp on d.spid=sp.spid where d.deviceid in( SELECT DISTINCT(deviceid) from copy_item WHERE bdzid=? and " + "kind like '%" + inspection
                    + "%' and dlt = '0' and deviceid in (" + devices + "))  and d.device_type=?";
        }

        sql = sql + " order by sp." + sort;
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        sqlInfo.addBindArg(new KeyValue("", deviceType));
        try {
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CopyItem getItem(String itemId) {
        try {
            return getDbManager().findById(CopyItem.class, itemId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查寻变电站抄录设备
     *
     * @param bdzId
     * @param inspection 巡检类型
     * @return
     */
    public List<DbModel> findCopyDeviceId(String bdzId, String inspection) {
        List<DbModel> dbModelList = new ArrayList<>();
        String sql = "SELECT DISTINCT(d.deviceid),d.spid FROM	copy_item item LEFT JOIN device d ON item.deviceid=d.deviceid WHERE	item.bdzid = ? AND item.kind LIKE '%" + inspection + "%' AND item.dlt = '0' ";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            dbModelList = findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }

    /**
     * 获取日常维护下的避雷器动作次数抄录设备
     */
    public List<DbModel> findAllDeviceHasCopyValue(String currentInspectionType, String bdzId) throws DbException {
        String selector = "";
        selector = "and d.deviceid in (SELECT DISTINCT(deviceid) FROM copy_item WHERE type_key in ('" + getCopyType() + "') and dlt = '0' )";
        return DeviceService.getInstance().findDeviceHasCopyValueBySelector(selector, bdzId);

    }

    public String getCopyType() {
        return "blqdzcs_dzcs','blqdzcs_xldlz";
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyItemCount(String bdzId) {
        String sql = "select count(case when val = 'Y' then 1 else null end)+"
                + "count(case when val_a = 'Y' then 1 else null end)+"
                + "count(case when val_b = 'Y' then 1 else null end)+"
                + "count(case when val_c = 'Y' then 1 else null end)+"
                + "count(case when val_o = 'Y' then 1 else null end)"
                + " as copyCount from copy_item item where item.bdzid=? and item.dlt='0'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyTotalCount(String bdzId, String inspection) {
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and item.kind like '%" + inspection + "%' and item.dlt = '0'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyItemCount(String bdzId, String itemKeyType) {
//        String sql = "select count(case when val = 'Y' then 1 else null end)+"
//                + "count(case when val_a = 'Y' then 1 else null end)+"
//                + "count(case when val_b = 'Y' then 1 else null end)+"
//                + "count(case when val_c = 'Y' then 1 else null end)+"
//                + "count(case when val_o = 'Y' then 1 else null end)"
//                + " as copyCount from copy_item item where item.bdzid=? and item.type_key in('"+itemKeyType+"')";
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and type_key in ('" + itemKeyType + "') and item.dlt=0";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<CopyItem> findAllMaintenanceHasCopyValue(String currentInspectionType, String currentBdzId) throws DbException {

        return selector().and(CopyItem.BDZID, "=", currentBdzId).and(CopyItem.KIND, "=", currentInspectionType)
                .findAll();
    }

    public List<CopyItem> findAllBySpace(String spid) {
        try {
            return selector().and(CopyItem.SPID, "=", spid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUpdate(final List<CopyItem> objects, final String currentReportId) {
        ExecutorManager.executeTask(() -> {
            if (!objects.isEmpty()) {
                try {
                    for (CopyItem object : objects) {
                        CopyItemService.this.saveOrUpdate(object);
                        CopyItemService.this.logicDelete(CopyResult.class, WhereBuilder.b(CopyResult.ITEM_ID, "=", (object).id).and(CopyResult.REPORTID, "=", currentReportId));

                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
