package com.cnksi.common.daoservice;

import android.text.TextUtils;

import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.model.vo.DefectInfo;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by han on 2016/4/27.
 */
public class DeviceService extends BaseService<Device> {

    private static DeviceService mDevices;

    private DeviceService() {
        super(Device.class);
    }

    public static DeviceService getInstance() {
        if (mDevices == null) {
            mDevices = new DeviceService();
        }
        return mDevices;
    }

    /**
     * 查询某个间隔下的所有设备
     *
     * @param mSpacing
     * @return
     * @throws DbException
     */
    public List<Device> findDeviceBySpacing(Spacing mSpacing, String deviceType) throws DbException {
        return selector().and(Device.SPID, "=", mSpacing.spid).and(Device.DEVICE_TYPE, "=", deviceType).findAll();
    }

    public List<DbModel> findAllDevice(String bdzId, String keyWord, String deviceType, String inspectionType, String deviceWay) {
        String spaceSort = "one".equals(deviceType) ? Spacing.SORT_ONE
                : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";

        String sql = "SELECT s.name_pinyin || ' ' || d." + pinyin + " AS search_key,s.latitude as slat,s.longitude as slot,s.name_pinyin as spacePY," +
                " d." + pinyin + " as devicePY,d.deviceid AS deviceId,"
                + "d.name_short AS shortName,d.name AS deviceName,d.latitude,d.longitude,d.is_important,"
                + "	s.spid,	s.name AS spacingName,s.type as spaceType,s.group_id FROM	device d " +
                " LEFT JOIN (select ROWID as assist_sort ,* from spacing) s ON d.spid = s.spid"
                + " WHERE d.bdzid = ?";
        sql += "AND d.device_type = ?";
        if ("bigtype_device".equalsIgnoreCase(deviceWay)) {
            sql += "and d.bigid in (select bigid from standard_special where kind = '" + inspectionType + "' and dlt = 0 )";
        }
        sql += " and s.dlt='0' AND d.dlt != 1 ORDER BY s." + spaceSort + " , s.assist_sort, d.sort";

        SqlInfo sqlInfo;
        if (!TextUtils.isEmpty(keyWord)) {
            sql = "select * from (" + sql + ") as t where t.search_key like '%" + keyWord + "%'";
        }
        sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        sqlInfo.addBindArg(new KeyValue("", deviceType));
        try {
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }

    }

    public DbModel findDeviceById(String deviceId) {
        try {
            String sql = "select * from device where deviceid=? and dlt = 0";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", deviceId));
            return findDbModelFirst(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String groupImportantDevicePhoto(String bdzId, String reportId) {
        SqlInfo sqlInfo = new SqlInfo("SELECT COUNT(*) as ic,COUNT(pd.id) c FROM device d " +
                "LEFT JOIN (SELECT * FROM placed_device where reportid=? and dlt=0 and placed_way='photo') pd on pd.deviceid =d.deviceid " +
                " where is_important='Y' and d.bdzid=? ;");
        sqlInfo.addBindArg(new KeyValue("",reportId));
        sqlInfo.addBindArg(new KeyValue("",bdzId));
        try {
            DbModel model = findDbModelFirst(sqlInfo);
            return model.getString("c") + "/" + model.getString("ic");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "0/0";
    }

    public List<DbModel> findAllDevice(String bdzId, String keyWord, String deviceType) {

        String spacingSort = ("one".equals(deviceType)) ? Spacing.SORT_ONE
                : ("second".equals(deviceType)) ? Spacing.SORT_SECOND : Spacing.SORT;
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        String sql = "select s.name_pinyin||' '||d." + pinyin + " as search_key,d.is_important,d.deviceid as deviceId,d.name_short as deviceName," +
                "s.spid,s.name as spacingName,s.type as spaceType,s.group_id," +
                "s.name_pinyin as spacePY, d." + pinyin + " as devicePY from device d "
                + "left join spacing s on d.spid=s.spid where d.bdzid=? and d.device_type=? and d.dlt!=1 order by s." + spacingSort + ",d.sort";
        if (!TextUtils.isEmpty(keyWord)) {
            sql = "select * from (" + sql + ") as t where t.search_key like '%" + keyWord + "%'";
        }
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

    public List<DbModel> findAllParticularDevice(String bdzId, String keyWord, String deviceType, String currentReportId, String deviceWay) {
        String devices = "";
        String sql = "";
        String spaceSort = "one".equals(deviceType) ? Spacing.SORT_ONE
                : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        if ("select_device".equalsIgnoreCase(deviceWay)) {
            Report currentReport = ReportService.getInstance().getReportById(currentReportId);
            devices = "'" + (currentReport == null ? "" : currentReport.selected_deviceid) + "'";
            devices = devices.replace(",", "','");

            sql = "SELECT s.name_pinyin || ' ' || d." + pinyin + " AS search_key,s.name_pinyin as spacePY,s.latitude as slat,s.longitude as slot," +
                    " d." + pinyin + " as devicePY," + "d.deviceid AS deviceId,d.name_short AS shortName,d.is_important,d.name AS deviceName,d.latitude,d.longitude,"
                    + "	s.spid,s.name AS spacingName,s.type as spaceType,s.group_id FROM	device d LEFT JOIN spacing s ON d.spid = s.spid"
                    + " WHERE d.bdzid = ? and d.dlt=0 and d.deviceid in (" + devices + ")";

        }

        sql += "AND d.device_type = ?";
        sql += " AND d.dlt != 1 ORDER BY s." + spaceSort + ", d.sort";

        SqlInfo sqlInfo = null;
        if (!TextUtils.isEmpty(keyWord)) {
            sql = "select * from (" + sql + ") as t where t.search_key like '%" + keyWord + "%'";
        }
        sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        sqlInfo.addBindArg(new KeyValue("", deviceType));
        try {
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DbModel> findSpaceDeviceByKeyWord(String bdzId, String keyWord, String deviceType, String... bigtypes) {
        StringBuffer sb = new StringBuffer("(");
        for (String bigId : bigtypes) {
            sb.append("'").append(bigId).append("',");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        String sql = "select * from (select s.name_pinyin||' '||d." + pinyin + " as search_key,d.deviceid as deviceId,d.name as deviceName,s.type as spaceType,s.group_id,s.spid,s.name as spacingName  from device d LEFT JOIN spacing s on d.spid=s.spid where d.bdzid=? and d.dlt=0 and d.bigid in "
                .concat(sb.toString())
                .concat(" order by s.sort,d.sort ) as t where t.search_key like '%" + keyWord + "%'");
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据条件查询设备
     *
     * @param bdzId
     * @param selector
     * @return
     * @throws DbException
     */
    public List<DbModel> findDeviceHasCopyValueBySelector(String selector, String bdzId) throws DbException {
        SqlInfo sqlInfo = new SqlInfo(" SELECT * from device WHERE dlt='0' and  bdzid=? " + selector);
        sqlInfo.addBindArg(new KeyValue("bdzId", bdzId));
        return findDbModelAll(sqlInfo);
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
        return findDbModelAll(sqlInfo);
    }

    /**
     * 查询收发信机设备
     *
     * @param bdzId
     * @return
     * @throws DbException
     */
    public List<Device> findTransceiverDevice(String bdzId) throws DbException {
        return selector().and(Device.BDZID, "=", bdzId).and(Device.NAME, "like", "%收发信机%").findAll();

    }

    /**
     * 获取变电站下面的所有缺陷表 为了加速设备列表展示
     *
     * @param bdzid
     * @return
     */
    public HashMap<String, DefectInfo> findDeviceDefect(String bdzid) {
        String sql = "SELECT" + "	*,count(1) as defect_count_key  FROM(SELECT "
                + "deviceid,defectlevel	FROM	defect_record		WHERE 		bdzid = ?"
                + "		AND has_track = 'N'"
                + "		AND has_remove = 'N'	AND (val = '' OR val IS NULL)  AND (dlt = '0' OR dlt is NULL)	ORDER BY"
                + "			deviceid,	defectlevel ASC) t GROUP BY 	t.deviceid";

        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzid));
        HashMap<String, DefectInfo> map = new HashMap<>();
        try {
            List<DbModel> mDeviceList = findDbModelAll(sqlInfo);
            if (mDeviceList != null && !mDeviceList.isEmpty()) {
                for (DbModel model : mDeviceList) {
                    map.put(model.getString(DefectRecord.DEVICEID),
                            new DefectInfo(model.getString(DefectRecord.DEVICEID),
                                    model.getString(DefectRecord.DEFECTLEVEL), model.getString("defect_count_key")));
                }
            }
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return map;
    }

    public String findBigId(String alias) {
        SqlInfo sqlInfo = new SqlInfo("SELECT * FROM `device_bigtype` where alias_pinyin like '%" + alias + "%'; ");
        try {
            List<DbModel> models = findDbModelAll(sqlInfo);
            if (models != null && models.size() > 0) {
                StringBuilder bigids = new StringBuilder();
                for (DbModel model : models) {
                    bigids.append(model.getString("bigid")).append(",");
                }
                return bigids.deleteCharAt(bigids.length() - 1).toString();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改后的查询抄录方式
     *
     * @param
     */
    public List<DbModel> getDevicesByNameWays(String bdzId, String key) {
        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid,d.device_name name,d.val,d.val_a,d.val_b,d.val_c,d.val_O  from copy_item d where d.dlt='0' and d.bdzid = '" + bdzId + "' and d.type_key = '" + key + "' order by d.deviceid ASC";
        try {
            dbModelList = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }


    /**
     * 交直流分接开关查询方式
     *
     * @param bdzid,
     */
    public List<DbModel> getDevicesByNameWays(String bdzid, String kaiGuanKey, String dangWeiKey) {

        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid deviceid,d.device_name name,d.bdzid bdzid from copy_item d where d.bdzid = '" + bdzid + "'  and  d.dlt = '0'  and (d.type_key = '" + kaiGuanKey + "' or d.type_key = '" + dangWeiKey + "')";
        try {
            dbModelList = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }

    /**
     * 交直流分接开关查询方式2
     *
     * @param bdzid,
     */
    public List<DbModel> searchDevicesByNameWays(String bdzid, String kaiGuanKey, String dangWeiKey) {

        List<DbModel> dbModelList = null;
        String sql = "select d.deviceid deviceid,d.device_name name,d.bdzid bdzid,group_concat(d.description) description,group_concat(d.type_key) key from copy_item d where  (d.type_key = '"
                + kaiGuanKey + "' or d.type_key = '" + dangWeiKey + "') and d.bdzid = '" + bdzid + "' and d.dlt = '0' group by d.deviceid ";
        try {
            dbModelList = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModelList;
    }


    //建议服务器处理 否则会引起同步问题 这里关掉触发器避免大范围的跟踪
    public boolean refreshDeviceHasCopy() {
        boolean isSuccess = false;
        try {
            dropTrigger("device");
            String sql = "update device set has_copy ='N'";
            getDbManager().execNonQuery(sql);
            sql = "update device set has_copy = 'Y' where deviceid in (select distinct deviceid from copy_item);";
            getDbManager().execNonQuery(sql);
            createTrigger("device", "deviceid");
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


    /**
     * 删除某个表的Trigger
     */
    public void dropTrigger(String tableName) {
        try {

            String triggerSql = String.format("DROP TRIGGER  If Exists %s_insert_trigger", tableName);
            execSql(triggerSql);
            triggerSql = String.format("DROP TRIGGER If Exists %s_update_trigger", tableName);
            execSql(triggerSql);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为表创建Trigger(2个trigger)
     */
    public void createTrigger(String tableName, String pk) {
        try {
            String triggerSql = String.format("CREATE TRIGGER IF NOT EXISTS %s_insert_trigger After insert ON %s BEGIN  INSERT INTO %s(tblname, pk,pkvalue,opera,enabled,create_time) VALUES ('%s','%s',new.%s,'insert','0',(datetime('now', 'localtime'))); END;", tableName, tableName, "ksync_modify_record", tableName, pk, pk);
            execSql(triggerSql);
            triggerSql = String.format("CREATE TRIGGER IF NOT EXISTS  %s_update_trigger After update ON %s BEGIN  INSERT INTO %s(tblname, pk,pkvalue,opera,enabled,create_time) VALUES ('%s','%s',new.%s,'update','0',(datetime('now', 'localtime'))); END;", tableName, tableName, "ksync_modify_record", tableName, pk, pk);
            execSql(triggerSql);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<Device> findDeviceBySpidAndType(String spid, String type) throws DbException {
        return selector().and(Device.SPID, "=", spid).and(Device.DEVICE_TYPE, "=", type).orderBy(Device.SORT).findAll();
    }

    public DbModel findAccidentDeal(String currentDeviceId) {
        String sql = " select db.rules,db.exception_deal_methods from device_bigtype db, (SELECT * from device d left join device_type dt on d.dtid= dt.dtid where  d.deviceid='" + currentDeviceId + "') t where db.bigid = t.bigid ";
        try {
            return findDbModelFirst(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();

        }
        return null;
    }


    public DbModel findDeviceNamesByDeviceIds(String selectDevices) throws DbException {
        return findDbModelFirst(new SqlInfo("SELECT group_concat(name,',') as rs  FROM	device WHERE deviceid IN (" + selectDevices + ")"));

    }

    /**
     * 更新更换的照片
     *
     * @param mDevice
     * @param changePicName
     */
    public void updateDeviceChangePic(Device mDevice, String changePicName) {
        try {
            mDevice.change_pic = changePicName;
            update(mDevice, Device.CHANGE_PIC);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public DbModel findCopySF6DefectDevice(String deviceID, String bdzID) {
        String sql = "select distinct ci.deviceid deviceid from copy_item  ci left join defect_record d on d.deviceid = ci.deviceid where ci.deviceid = '" + deviceID + "' and ci.bdzid = '" + bdzID + "' and ci.dlt = '0'" +
                " and d.has_remove ='N' and ci.type_key = 'sf6yl_sf6yl' ";
        DbModel dbModel = null;
        try {
            dbModel = findDbModelFirst(new SqlInfo(sql));
            return dbModel;
        } catch (DbException e) {
            e.printStackTrace();
            return dbModel;
        }
    }

    public List<Device> findDeviceByType(String currentBdzId, String deviceType) {
        try {
            return selector().and(Device.BDZID, "=", currentBdzId).and(Device.DEVICE_TYPE, "=", deviceType).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean updateDeviceLocationInfo(Device mDevice) {
        boolean isSuccess = false;
        try {
           update(mDevice, Device.LATITUDE, Device.LONGITUDE);
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 根据间隔更新定位信息
     */
    public boolean updateDeviceLocationInfo(String spid, String lat, String lng) {
        boolean isSuccess = false;
        try {
            update( WhereBuilder.b(Device.SPID, "=", spid), new KeyValue(Device.LATITUDE, lat), new KeyValue(Device.LONGITUDE, lng));
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
