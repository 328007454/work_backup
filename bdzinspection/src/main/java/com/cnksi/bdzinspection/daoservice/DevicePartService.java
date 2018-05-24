package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.common.model.DevicePart;
import com.cnksi.common.Config;


import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 设备部件查询
 *
 * @author terry
 */
public class DevicePartService {

    public static DevicePartService mInstance;

    private DevicePartService() {
    }

    public static DevicePartService getInstance() {
        if (mInstance == null) {
            mInstance = new DevicePartService();
        }
        return mInstance;
    }

    /**
     * 根据设备ID查询设备部件
     *
     * @return
     */
    public List<DevicePart> getDevicePartList(String deviceid) {
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(DevicePart.class).where(DevicePart.DEVICEID, "=", deviceid)
                    .and(DevicePart.DLT, "!=", "1");

            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据设备ID查询设备部件,和各个部件的缺陷数量和最高缺陷等级
     *
     * @return
     */
    public List<DbModel> getDevicePartModel(String deviceid, String bdzId) {
        List<DbModel> datas = null;
        String sql = "select d.*,d.name as duname,count(1) as defect_count_key from (select d.*,dr.defectlevel from device_part d left join (select * from defect_record where has_track='N' and has_remove='N' AND (val='' OR val IS NULL) and bdzid='"
                + bdzId + "') dr on d.duid = dr.duid where d.deviceid=? and d.dlt<>'" + Config.DELETED
                + "' order by dr.duid,dr.defectlevel asc) d  group by d.duid";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", deviceid));
        try {
            datas = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 查询设备部件（lib+admin）
     *
     * @return
     */
    public List<DbModel> getDevicePart(String dtid, String inspectionType, String bdzId, String deviceid) {
        // 查询lib的部件
        List<DbModel> devicePartList = DevicePartService.getInstance().getDevicePartFromDB(dtid, bdzId, deviceid);
        if (null == devicePartList) {
            devicePartList = new ArrayList<>();
        }

        // 查询admin中的部件
        List<DbModel> partList = DevicePartService.getInstance().getDevicePartModel(deviceid, bdzId);
        if (null != partList) {
            for (DbModel dbModel : partList) {
                DbModel modelInLib = convertAdminToLib(dbModel);
                devicePartList.add(modelInLib);
            }
        }
        //将没有标准的部件过滤掉
        SqlInfo info = new SqlInfo("SELECT DISTINCT duid FROM standards s left join (select  oper,staid from device_standards_oper where dlt=0 and deviceid=?) opt on opt.staid=s.staid " +
                "WHERE s.duid IN (SELECT duid FROM device_unit WHERE dtid = ? and dlt=0)" +
                " and s.dlt=0 and " +
                "s.kind like '%" + inspectionType + "%' AND ( s.report_type = '0' OR s.report_type IS NULL) and ifnull(oper,'') != 'deleted' GROUP BY s.duid");
        info.addBindArg(new KeyValue("", deviceid));
        info.addBindArg(new KeyValue("", dtid));
        HashSet<String> hashSet = new HashSet<>();
        try {
            List<DbModel> standard = XunshiApplication.getDbUtils().findDbModelAll(info);
            if (standard != null) {
                for (DbModel model : standard) {
                    hashSet.add(model.getString("duid"));
                }
            }
            info = new SqlInfo("Select DISTINCT duid from device_standards where device_id=? and dlt=0 and kind like '%" + inspectionType + "%'");
            info.addBindArg(new KeyValue("",deviceid));
            standard = XunshiApplication.getDbUtils().findDbModelAll(info);
            if (standard != null) {
                for (DbModel model : standard) {
                    hashSet.add(model.getString("duid"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        Iterator<DbModel> iterator = devicePartList.iterator();
        while (iterator.hasNext()) {
            if (!hashSet.contains(iterator.next().getString("duid"))) {
                iterator.remove();
            }
        }
        return devicePartList;
    }

    /**
     * admin devicePart convert to lib devicePart
     *
     * @param dbModel
     * @return
     */
    private DbModel convertAdminToLib(final DbModel dbModel) {
        dbModel.add(DevicePart.NAME, dbModel.getString("name"));
        return dbModel;
    }

    /**
     * 根据设备类型查询设备部件
     *
     * @param dtid
     * @return
     */
    public List<DbModel> getDevicePartFromDB(String dtid, String bdzId, String deviceid) {
        List<DbModel> datas = null;
        // String sql = "select * from device_unit where dtid=? order by sort";
        String sql = "select du.*,t.defect_count_key,t.defectlevel from device_unit du " +
                "LEFT JOIN (SELECT *,count(1) as defect_count_key FROM (" +
                "SELECT dr.duid,dr.defectlevel from defect_record dr where dr.bdzid=?  and dr.deviceid=? AND has_track='N' and has_remove='N' AND (val='' OR val IS NULL) AND (dlt='0' OR dlt IS NULL) ORDER BY duid,dr.defectlevel ASC )GROUP BY duid) t on t.duid=du.duid" +
                " where du.dtid=? and du.dlt<>'1' order by sort";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("",bdzId));
        sqlInfo.addBindArg(new KeyValue("",deviceid));
        sqlInfo.addBindArg(new KeyValue("",dtid));
        try {
            datas = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return datas;
    }


    /**
     * 查询当前设备所属的设备类型
     */

    public DbModel getDeviceType(String dtid) {
        DbModel dbModel;
        SqlInfo sqlInfo = new SqlInfo("select name from device_type as dp where dp.dtid = '" + dtid + "' ");
        try {
            dbModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            if (dbModel == null) {
                dbModel = new DbModel();
            }
        } catch (DbException e) {
            e.printStackTrace();
            dbModel = new DbModel();
        }
        return dbModel;
    }

}
