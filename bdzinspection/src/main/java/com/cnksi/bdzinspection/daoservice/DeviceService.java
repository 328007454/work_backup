package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Device;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.utils.Config;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 设备数据查询
 *
 * @author terry
 */
public class DeviceService {

    public static DeviceService mInstance;

    private DeviceService() {
    }

    public static DeviceService getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceService();
        }
        return mInstance;
    }


    /**
     * 获取变电站下面的所有缺陷表 为了加速设备列表展示
     *
     * @param bdzid
     * @return
     */
    public HashMap<String, DefectInfo> findDeviceDefect(String bdzid) {
        SqlInfo sql = new SqlInfo("SELECT" + "	*,count(1) as defect_count_key  FROM(SELECT "
                + "deviceid,defectlevel	FROM	defect_record		WHERE 		bdzid = ?"
                + "		AND has_track = 'N'"
                + "		AND has_remove = 'N'	AND (val = '' OR val IS NULL)  AND (dlt = '0' OR dlt is NULL)	ORDER BY"
                + "			deviceid,	defectlevel ASC) t GROUP BY 	t.deviceid", bdzid);
        HashMap<String, DefectInfo> map = new HashMap<String, DefectInfo>();
        try {
            List<DbModel> mDeviceList = XunshiApplication.getDbUtils().findDbModelAll(sql);
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

    /**
     * 查询某个间隔下的所有设备
     *
     * @param mSpacing
     * @return
     * @throws DbException
     */
    public List<Device> findDeviceBySpacing(Spacing mSpacing, String deviceType) throws DbException {
        Selector selector = BaseService.from(Device.class).and(Device.SPID, "=", mSpacing.spid).and(Device.DEVICE_TYPE, "=", deviceType);
        List<Device> mDeviceList = XunshiApplication.getDbUtils().findAll(selector);
        return mDeviceList;
    }

    /**
     * 滑动跳转到下一个设备
     *
     * @param spid
     * @param sort
     * @param direction
     * @return
     */
    public Device findDeviceBySpacingIdAndSort(String spid, Integer sort, String direction) {
        Device device = null;
        try {
            if ("left".equals(direction)) {
                device = XunshiApplication.getDbUtils()
                        .findFirst(Selector.from(Device.class).expr(Device.SPID, "=", spid).and(Device.SORT, ">", sort)
                                .and(Device.DLT, "<>", Config.DELETED).orderBy(Device.SORT, false));
            } else {
                device = XunshiApplication.getDbUtils()
                        .findFirst(Selector.from(Device.class).expr(Device.SPID, "=", spid).and(Device.SORT, "<", sort)
                                .and(Device.DLT, "<>", Config.DELETED).orderBy(Device.SORT, true));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return device;
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
            XunshiApplication.getDbUtils().update(mDevice, Device.CHANGE_PIC);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新定位信息
     *
     * @param mDevice
     */
    public boolean updateDeviceLocationInfo(Device mDevice) {
        boolean isSuccess = false;
        try {
            XunshiApplication.getDbUtils().update(mDevice, Device.LATITUDE, Device.LONGITUDE);
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 更新定位信息
     */
    public boolean updateDeviceLocationInfo(String spid, String lat, String lng) {
        boolean isSuccess = false;
        try {
            XunshiApplication.getDbUtils().update(Device.class, WhereBuilder.b(Device.SPID, "=", spid),
                    new String[]{Device.LATITUDE, Device.LONGITUDE},
                    new String[]{lat, lng});
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public DbModel findDeviceById(String deviceId) {
        try {
            return XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo("select * from device where deviceid=?", deviceId));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DbModel> findSpaceDeviceByBigType(String bdzId, String deviceType, String... bigtypes) {
        StringBuffer sb = new StringBuffer("(");
        for (String bigId : bigtypes) {
            sb.append("'").append(bigId).append("',");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        String spacingSort = (deviceType.equals("one")) ? Spacing.SORT_ONE
                : (deviceType.equals("second")) ? Spacing.SORT_SECOND : Spacing.SORT;

        String sql = "select d.deviceid as deviceId,d.name as deviceName,s.spid,s.name as spacingName from device d left join spacing s on d.spid=s.spid where d.bdzid=? and d.dlt=0 and d.device_type=? and d.bigid in "
                .concat(sb.toString()).concat(" order by s." + spacingSort + ",d.sort;");
        SqlInfo sqlInfo = new SqlInfo(sql, bdzId, deviceType);
        try {
            return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
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
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        String sql = "select * from (select s.name_pinyin||' '||d." + pinyin + " as search_key,d.deviceid as deviceId,d.name as deviceName,s.type as spaceType,s.group_id,s.spid,s.name as spacingName  from device d LEFT JOIN spacing s on d.spid=s.spid where d.bdzid=? and d.dlt=0 and d.bigid in "
                .concat(sb.toString())
                .concat(" order by s.sort,d.sort ) as t where t.search_key like '%" + keyWord + "%'");
        SqlInfo sqlInfo = new SqlInfo(sql, bdzId);
        try {
            return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DbModel> findAllDevice(String bdzId, String keyWord, String deviceType) {

        String spacingSort = (deviceType.equals("one")) ? Spacing.SORT_ONE
                : (deviceType.equals("second")) ? Spacing.SORT_SECOND : Spacing.SORT;
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        String sql = "select s.name_pinyin||' '||d." + pinyin + " as search_key,d.is_important,d.deviceid as deviceId,d.name_short as deviceName," +
                "s.spid,s.name as spacingName,s.type as spaceType,s.group_id," +
                "s.name_pinyin as spacePY, d." + pinyin + " as devicePY from device d "
                + "left join spacing s on d.spid=s.spid where d.bdzid=? and d.device_type=? and d.dlt!=1 order by s." + spacingSort + ",d.sort";
        if (!TextUtils.isEmpty(keyWord)) {
            sql = "select * from (" + sql + ") as t where t.search_key like '%" + keyWord + "%'";
        }
        SqlInfo sqlInfo = new SqlInfo(sql, bdzId, deviceType);
        try {
            return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 查询设备表中在copyItem中的数据
     */
    public List<String> findAllDeviceInCopyItem(String currentInspectionType, String bdzId) throws DbException {
        String sql = "SELECT DISTINCT deviceid FROM	copy_item WHERE kind LIKE '%" + currentInspectionType
                + "%' and bdzid = '" + bdzId + "' AND dlt='0' ";
        List<String> deviceIdList = new ArrayList<String>();
        List<DbModel> modelList = XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
        if (null != modelList && !modelList.isEmpty())
            for (DbModel dbModel : modelList) {
                deviceIdList.add(dbModel.getString(Device.DEVICEID));
            }

        return deviceIdList;
    }

    public List<DbModel> findDeviceHasCopyValueBySelector(String selector, String bdzId) throws DbException {
        SqlInfo sqlInfo = new SqlInfo(" SELECT * from device d left join spacing s on d.spid = s.spid  WHERE  d.bdzid=? " + selector + " order by s.sort_one", bdzId);
        List<DbModel> dbModelList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        return dbModelList;
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
            sql += "and d.bigid in (select bigid from standard_special where kind = '" + inspectionType + "')";
        }
        sql += " and s.dlt='0' AND d.dlt != 1 ORDER BY s." + spaceSort + " , s.assist_sort, d.sort";

        SqlInfo sqlInfo;
        if (!TextUtils.isEmpty(keyWord)) {
            sql = "select * from (" + sql + ") as t where t.search_key like '%" + keyWord + "%'";
        }
        sqlInfo = new SqlInfo(sql, bdzId, deviceType);
        try {
            return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }

    }

    public List<DbModel> findAllParticularDevice(String bdzId, String keyWord, String deviceType, String currentReportId, String deviceWay) {
        String devices = "";
        String sql = "";
        String spaceSort = "one".equals(deviceType) ? Spacing.SORT_ONE
                : "second".equals(deviceType) ? Spacing.SORT_SECOND : Spacing.SORT;
        String pinyin = "second".equals(deviceType) ? "name_pinyin" : "name_short_pinyin";
        if (deviceWay.equalsIgnoreCase("select_device")) {
            Report currentReport = null;
            try {
                currentReport = XunshiApplication.getDbUtils().findById(Report.class, currentReportId);
            } catch (DbException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        sqlInfo = new SqlInfo(sql, bdzId, deviceType);
        try {
            return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Device> findDeviceByType(String currentBdzId, String deviceType) {
        Selector selector = BaseService.from(Device.class).and(Device.BDZID, "=", currentBdzId).and(Device.DEVICE_TYPE, "=", deviceType);
        try {
            return XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String groupImportantDevicePhoto(String bdzId, String reportId) {
        SqlInfo sqlInfo = new SqlInfo("SELECT COUNT(*) as ic,COUNT(pd.id) c FROM device d " +
                "LEFT JOIN (SELECT * FROM placed_device where reportid=? and dlt=0 and placed_way='photo') pd on pd.deviceid =d.deviceid " +
                " where is_important='Y' and d.bdzid=? ;");
        sqlInfo.addBindArgs(reportId, bdzId);
        try {
            DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            return model.getString("c") + "/" + model.getString("ic");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "0/0";
    }

    public DbModel findCopySF6DefectDevice(String deviceID, String bdzID) {
        String sql = "select distinct ci.deviceid deviceid from copy_item  ci left join defect_record d on d.deviceid = ci.deviceid where ci.deviceid = '" + deviceID + "' and ci.bdzid = '" + bdzID + "' and ci.dlt = '0'" +
                " and d.has_remove ='N' and ci.type_key = 'sf6yl_sf6yl' ";
        DbModel dbModel = null;
        try {
            dbModel = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo(sql));
            return dbModel;
        } catch (DbException e) {
            e.printStackTrace();
            return dbModel;
        }
    }

    public static class DefectInfo {
        public String deviceId;
        public String defectLevel;
        public String defectCount;

        /**
         * @param deviceId
         * @param defectLevel
         * @param defectCount
         */
        public DefectInfo(String deviceId, String defectLevel, String defectCount) {
            super();
            this.deviceId = deviceId;
            this.defectLevel = defectLevel;
            this.defectCount = defectCount;
        }

    }

}
