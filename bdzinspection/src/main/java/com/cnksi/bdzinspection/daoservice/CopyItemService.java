package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;
import android.view.TextureView;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.model.CopyType;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.model.Spacing;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.cnksi.bdzinspection.application.XunshiApplication.getDbUtils;

public class CopyItemService {
    private final static String cptSql = "select count(case when val = 'Y' then 1 else null end)+" + "count(case when val_a = 'Y' then 1 else null end)+"
            + "count(case when val_b = 'Y' then 1 else null end)+" + "count(case when val_c = 'Y' then 1 else null end)+" + "count(case when val_o = 'Y' then 1 else null end)"
            + " as copyCount from copy_item item ";
    public static CopyItemService mInstance;

    private CopyItemService() {
    }

    public static CopyItemService getInstance() {
        if (mInstance == null) {
            mInstance = new CopyItemService();
        }
        return mInstance;
    }

    /**
     * 查询设备下的抄录项目
     *
     * @param bdzId
     * @param deviceId
     * @return
     */
    public List<CopyItem> getDeviceCopyItem(String bdzId, String deviceId, String inspection) {
        try {
            return getDbUtils().selector(CopyItem.class).where(CopyItem.BDZID, "=", bdzId).and(CopyItem.DEVICEID, "=", deviceId).
                    and(CopyItem.KIND, "like", "%" + inspection + "%").and(CopyItem.DLT, "=", 0).orderBy(CopyItem.ID).findAll();
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

        if (!TextUtils.isEmpty(deviceWay) && deviceWay.equalsIgnoreCase("bigtype_device")) {
            sql = sql + " and d.bigid in ( select bigid from standard_special where kind = '" + inspection + "' and dlt = 0 )";
        }
        if (!TextUtils.isEmpty(deviceWay) && deviceWay.equalsIgnoreCase("select_device")) {
            Report currentReport = null;
            try {
                currentReport = XunshiApplication.getDbUtils().findById(Report.class, reportId);
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
            return getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
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
            return getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CopyItem getItem(String itemId) {
        try {
            return getDbUtils().findById(CopyItem.class, itemId);
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
    public long getCopyItemCount(String bdzId, String inspection) {
//		String sql = cptSql + "where item.bdzid=? and kind like '%" + inspection + "%'";
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and kind like '%" + inspection + "%' and dlt = '0'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = getDbUtils().findDbModelFirst(sqlInfo);
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
    public long getCopyItemCount1(String bdzId, String inspection) {
//		String sql = cptSql + "where item.bdzid=? and type_key in ('" + inspection + "')";
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and type_key in ('" + inspection + "') ";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = getDbUtils().findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
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
     * 查询设备下的避雷器抄录项目
     *
     * @param bdzId
     * @param deviceId
     * @return
     */
    public List<CopyItem> getDeviceCopyItem1(String bdzId, String deviceId, String inspection) {
        try {
            return getDbUtils().selector(CopyItem.class).where(CopyItem.BDZID, "=", bdzId).and(CopyItem.DEVICEID, "=", deviceId).expr("and " + CopyItem.TYPE_KEY + " in ('" + inspection + "')")
                    .and(CopyItem.DLT, "=", 0).orderBy(CopyItem.ID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查寻变动站抄录设备
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
            dbModelList = getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
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
            return getDbUtils().selector(CopyItem.class).where(CopyItem.BDZID, "=", bdzId).and(CopyItem.DEVICEID, "=", deviceId).
                    and(CopyItem.DLT, "=", 0).orderBy(CopyItem.ID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<CopyType> findAllCopyType() {
        try {
            List<CopyType> types = XunshiApplication.getDbUtils().selector(CopyType.class).where(CopyType.SELECTED_ABLE, "=", "Y").findAll();
            return types;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUpdate(final List<? extends Object> objects, ExecutorService mFixedThreadPoolExecutor) {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!objects.isEmpty()) {
                    try {
                        getDbUtils().saveOrUpdate(objects);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void saveUpdate(final List<? extends Object> objects, final String currentReportId, ExecutorService mFixedThreadPoolExecutor) {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!objects.isEmpty()) {
                    try {
                        for (Object object : objects) {
                            if (object instanceof CopyItem) {
                                getDbUtils().saveOrUpdate(object);
                                getDbUtils().delete(CopyResult.class, WhereBuilder.b(CopyResult.ITEM_ID, "=", ((CopyItem) object).id).and(CopyResult.REPORTID, "=", currentReportId));
                            }
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public List<CopyItem> findAllMaintenanceHasCopyValue(String currentInspectionType, String currentBdzId) throws DbException {

        return getDbUtils().selector(CopyItem.class).where(CopyItem.BDZID, "=", currentBdzId).and(CopyItem.KIND, "=", currentInspectionType)
                .and(CopyItem.DLT, "=", "0").findAll();
    }

    public List<CopyItem> findAllBySpace(String spid) {
        try {
            return XunshiApplication.getDbUtils().selector(CopyItem.class).where(CopyItem.DLT, "=", "0").and(CopyItem.SPID, "=", spid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
