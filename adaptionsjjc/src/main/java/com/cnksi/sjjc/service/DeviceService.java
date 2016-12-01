package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.Spacing;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 2016/4/27.
 */
public class DeviceService {

    private static DeviceService mDevices;

    public DeviceService() {
    }

    public static DeviceService getInstance() {
        if (mDevices == null) {
            mDevices = new DeviceService();
            return mDevices;
        } else {
            return mDevices;
        }
    }

    /**
     * 通过“保护设备关键词查询deviece表中的数据”
     */

    public List<Device> getDevicesProtect(String bdzId) {
        List<Device> deviceList = new ArrayList<Device>();
        try {
            deviceList = CustomApplication.getDbManager().selector(Device.class).where(Device.NAME, "like", "%保护%").and(Device.BDZID, "=", bdzId).and(Device.DLT, "<>", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return deviceList;
    }

    /**
     * 通过“保护设备关键词查询deviece表中的数据”
     */

    public List<DbModel> getDevicesByName(String bdzId, String name) {
        List<DbModel> dbModelList = null;
        try {
            SqlInfo sqlInfo = new SqlInfo("SELECT d.deviceid,d.name FROM device d WHERE d.bdzid = '" + bdzId + "' AND dtid IN ( SELECT dtid FROM device_unit WHERE duid IN ( SELECT s.duid FROM standards s WHERE s.description LIKE " +
                    "'%" + name + "%')) order by d.deviceid ");
            dbModelList = CustomApplication.getDbManager().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }


    /**
     * 根据设备型号来查询 所有有抄录数据的设备
     *
     * @param deviceType 设备型号
     * @param bdzId      变电站ID
     * @param nameFilter 设备名字过滤器,如果为空就是筛选全部设备
     * @return
     * @throws DbException
     */
    public List<DbModel> findAllDeviceHasCopyValue(String deviceType, String bdzId, String nameFilter) throws DbException {
        String sort = "one".equals(deviceType) ? Spacing.SORT_ONE
                : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;
        String filter = TextUtils.isEmpty(nameFilter) ? "" : " and d.name LIKE '%" + nameFilter + "%'";
        SqlInfo sqlInfo = new SqlInfo(
                "SELECT d.* FROM device d LEFT JOIN spacing s on s.spid=d.spid WHERE dtid IN " +
                        "( SELECT DISTINCT dt.dtid FROM ( SELECT duid FROM standards WHERE resulttype = 1 AND dlt = 0 ) AS ds" +
                        " LEFT JOIN device_unit dp ON ds.duid = dp.duid LEFT JOIN device_type dt ON dt.dtid = dp.dtid " +
                        " where dp.dlt<>'1' and dt.dlt<>'1') AND d.bdzid = ? AND d.device_type = ?" + filter +
                        " ORDER BY s." + sort + ", d.sort");
        sqlInfo.addBindArg(new KeyValue("bdzid", bdzId));
        sqlInfo.addBindArg(new KeyValue("deviceType", deviceType));
        List<DbModel> mDeviceList = CustomApplication.getDbManager().findDbModelAll(sqlInfo);
        return mDeviceList;
    }

    /**
     * 根据条件查询设备
     *
     * @param deviceType
     * @param bdzId
     * @param selector
     * @return
     * @throws DbException
     */
    public List<DbModel> findDeviceHasCopyValueBySelector(String deviceType, String selector, String bdzId) throws DbException {
        SqlInfo sqlInfo = new SqlInfo(" SELECT * from device WHERE  bdzid=? and device_type=? and has_copy='Y' " + selector);
        sqlInfo.addBindArg(new KeyValue("bdzId", bdzId));
        sqlInfo.addBindArg(new KeyValue("deviceType", deviceType));
        return CustomApplication.getDbManager().findDbModelAll(sqlInfo);
    }

    /**
     * 根据条件查询设备
     *
     * @param bdzId
     * @param selector
     * @return
     * @throws DbException
     */
    public List<DbModel> findDeviceHasCopyValueBySelector( String selector, String bdzId) throws DbException {
        SqlInfo sqlInfo = new SqlInfo(" SELECT * from device WHERE  bdzid=? " + selector);
        sqlInfo.addBindArg(new KeyValue("bdzId", bdzId));
        return CustomApplication.getDbManager().findDbModelAll(sqlInfo);
    }
    /**
     * 根据条件查询当前设备需要抄录的总项数目是否与当前record表中该设备已经抄录的总数据数目是否相等。
     *
     * @param reportId
     * @return
     */
    public Map<String, List<String>> getCopyDevice(String reportId, String bdzid, String deviceType) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        // String sql = "SELECT d.spid,d.deviceid FROM device d WHERE d.device_type=? and d.deviceid IN ( SELECT DISTINCT (dr.deviceid) FROM defect_record dr WHERE dr.reportid = ? AND dr.val IS NOT NULL )";
        // SqlInfo sqlInfo = new SqlInfo(sql, deviceType,reportId);
        try {
            List<DbModel> dataList = getCopyDeviceDbModels(reportId, bdzid, deviceType, null);
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

    public List<DbModel> getCopyDeviceDbModels(String reportId, String bdzid, String deviceType, String filter) throws DbException {
        SqlInfo sqlInfo = null;
        String sql = "";
        sql = sql + "SELECT deviceid,spid from (";
        sql = sql + " SELECT";
        sql = sql + "	d.deviceid,d.spid,";
        sql = sql + "	count(1) AS cpt,";
        sql = sql + "	d.name_short,dr.cpc";
        sql = sql + " FROM";
        sql = sql + "	standards s";
        sql = sql + " LEFT JOIN device_unit du ON s.duid = du.duid ";
        sql = sql + " LEFT JOIN device_type dt ON dt.dtid = du.dtid";
        sql = sql + " LEFT JOIN device d ON d.dtid = dt.dtid";
        sql = sql + " LEFT JOIN ";
        sql = sql
                + " (SELECT count(1) as cpc,deviceid FROM  defect_record where reportid=? and val is not null and val <> '' GROUP BY deviceid )";
        sql = sql + " dr on dr.deviceid=d.deviceid";
        sql = sql + " WHERE";
        sql = sql + "	s.resulttype = '1'";
        sql = sql + " AND s.dlt <> '1'";
        sql = sql + " and du.dlt<>'1'";
        sql = sql + " and d.dlt<>'1'";
        sql = sql + " and cpc is not NULL";
        sql = sql + " AND d.bdzid = ?";
        if (!TextUtils.isEmpty(filter)) {
            sql = sql + " and s.description like '%" + filter + "%' ";
        }
        if (!"search_device_key".equals(deviceType)) {
            sql = sql + " and d.device_type=?";
        }
        sql = sql + " GROUP BY";
        sql = sql + "	d.deviceid )  where cpc=cpt";
        /**
         * 当前为筛选状态时查询条件不需要设备类型
         */
        sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("reportId", reportId));
        sqlInfo.addBindArg(new KeyValue("bdzid", bdzid));
        if (!"search_device_key".equals(deviceType)) {
            sqlInfo.addBindArg(new KeyValue("deviceType", deviceType));
        }
        return CustomApplication.getDbManager().findDbModelAll(sqlInfo);
    }

    /**
     * 查询收发信机设备
     *
     * @param bdzId
     * @return
     * @throws DbException
     */
    public List<Device> findTransceiverDevice(String bdzId) throws DbException {
        return CustomApplication.getDbManager().selector(Device.class).where(Device.BDZID, "=", bdzId).and(Device.NAME, "like", "%收发信机%").findAll();

    }

    /**
     * 恢复抄录数据 到内存HashMap
     *
     * @param bdzid
     */
    public void recoverCopyData(String bdzid) {
        String sql = "select deviceid,standid,val from defect_record where val <> '' and val is not null and bdzid = '"
                + bdzid + "' GROUP BY deviceid,standid order by discovered_date desc";
        SqlInfo sqlInfo = new SqlInfo(sql);
        try {
            // if (!PreferencesUtils.getBoolean(CustomApplication.getInstance(), Config.IS_RECOVER_COPY_DATA + bdzid,
            // false)) {
            List<DbModel> dataList = CustomApplication.getDbManager().findDbModelAll(sqlInfo);
            if (dataList != null) {
                HashMap<String, String> copyMap = CustomApplication.getInstance().getCopyedMap();
                for (DbModel model : dataList) {
                    // 标准+设备确保抄录唯一性
                    String key = model.getString("standid") + "_" + model.getString("deviceid");
                    String value = model.getString("val");
                    copyMap.put(key, value);
                }
            }
            // }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改后的查询抄录方式
     *
     * @param
     */
    public List<DbModel> getDevicesByNameWays(String bdzId, String key) {
        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid,d.device_name name from copy_item d where d.bdzid = '"+bdzId +"' and d.type_key = '"+key +"' order by d.deviceid ASC";
        try {
            dbModelList = CustomApplication.getDbManager().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }

    /**
     * 修改后的查询抄录方式
     *
     * @param
     */
    public List<CopyItem> getDevicesByNameWays1(String bdzId, String key) {
        List<CopyItem> items = null;
        try {
           items = CustomApplication.getDbManager().selector(CopyItem.class).where(CopyItem.BDZID,"=",bdzId).and(CopyItem.TYPE_KEY,"=",key).orderBy(CopyItem.DEVICEID,false).findAll();
            for (CopyItem item :items){
                item.focus = false;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 交直流分接开关查询方式
     *
     * @param bdzid,
     * */
    public List<DbModel> getDevicesByNameWays(String bdzid, String kaiGuanKey, String dangWeiKey) {

        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid deviceid,d.device_name name,d.bdzid bdzid from copy_item d where d.bdzid = '"+bdzid +"' and (d.type_key = '"+kaiGuanKey +"' or d.type_key = '"+dangWeiKey +"')";
        try {
            dbModelList = CustomApplication.getDbManager().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }

    /**
     * 交直流分接开关查询方式2
     *
     * @param bdzid,
     * */
    public List<DbModel> searchDevicesByNameWays(String bdzid, String kaiGuanKey, String dangWeiKey) {

        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid deviceid,d.device_name name,d.bdzid bdzid,group_concat(d.description) description,group_concat(d.type_key) key from copy_item d where  (d.type_key = '"
                +kaiGuanKey +"' or d.type_key = '"+dangWeiKey +"') and d.bdzid = '"+bdzid +"' group by d.deviceid ";
        try {
            dbModelList = CustomApplication.getDbManager().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }

    /**
     * 得到当前变电站以及当前deviceId下的设备
     * */
    public List<CopyItem> getAllCopyItem(String bdzId, String deviceId, String kaiGaunKey, String dangWeiKey) {
        List<CopyItem> copyItemList = null;
        try {
            copyItemList = CustomApplication.getDbManager().selector(CopyItem.class).where(CopyItem.BDZID,"=",bdzId).and(CopyItem.DEVICEID,"=",deviceId).and(CopyItem.TYPE_KEY,"=",kaiGaunKey).or(CopyItem.TYPE_KEY,"=",dangWeiKey).findAll();
            if(null==copyItemList) copyItemList = new ArrayList<>();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return copyItemList;

    }

    /**
     * 交直流分接开关查询方式
     *
     * @param bdzid,
     * */
    public List<DbModel> getDevicesById(String bdzid, String deviceId,String kaiGuanKey, String dangWeiKey) {
        List<DbModel> dbModelList = null;
        String sql = "select d.description description,d.type_key key from copy_item d where d.bdzid = '"+bdzid +"' and d.deviceid = '"+deviceId+"' and ( d.type_key = '"+kaiGuanKey +"' or d.type_key = '"+dangWeiKey +"' )";
        try {
            dbModelList = CustomApplication.getDbManager().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }


    public boolean refreshDeviceHasCopy() {
        boolean isSuccess = false;
        try {
            String sql = "update device set has_copy ='N'";
            CustomApplication.getDbManager().execNonQuery(sql);
            if (Config.NEW_COPY) {
                sql = "update device set has_copy = 'Y' where deviceid in (select distinct deviceid from copy_item);";
            } else {
                sql = "UPDATE device set has_copy='Y' WHERE deviceid IN"
                        + "(select DISTINCT(d.deviceid) deviceid from standards s "
                        + "left JOIN device_unit du on s.duid = du.duid LEFT JOIN device_type dt on dt.dtid = du.dtid"
                        + "	LEFT JOIN device d on d.dtid = dt.dtid "
                        + "where s.resulttype = '1' AND d.dlt <> '1' AND du.dlt <> '1' AND s.dlt <> '1' AND dt.dlt <> '1')";
            }
            CustomApplication.getDbManager().execNonQuery(sql);
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

}
