package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Placed;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.xscore.xsutils.DateUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 到位service
 *
 * @author luoxy
 */
public class PlacedService extends BaseService {

    private static PlacedService instance;

    public static PlacedService getInstance() {
        if (null == instance)
            instance = new PlacedService();
        return instance;
    }

    /**
     * 查詢已到間隔
     *
     * @param reportId
     * @return
     */
    public List<Placed> findPlacedSpace(String reportId) {
        List<Placed> placed = new ArrayList<Placed>();
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(Placed.class).where(Placed.REPORTID, "=", reportId).and(Placed.PLACED, "=",
                    "1");

            placed = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return placed;
    }

    /**
     * 查詢已到間隔
     *
     * @param reportId
     * @return
     */
    public List<PlacedDevice> findPlacedDevice(String reportId) {
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(PlacedService.class).where(PlacedDevice.REPORTID, "=", reportId);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashMap<String, String> findPmsPlaced(String reportId) {
        HashMap<String, String> map = new HashMap<>();
        try {
            String sql = "SELECT  s.pms_id,p.create_time FROM `placed` p LEFT JOIN spacing s on p.spid=s.spid  where report_id=? and p.dlt=0 GROUP BY pms_id";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", reportId));
            List<DbModel> dbModels = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (dbModels != null) {
                for (DbModel model : dbModels) {
                    map.put(model.getString("pms_id"), model.getString("create_time"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return map;
    }


    public boolean saveOrUpdate(Placed mObject) {
        mObject.createTime = DateUtils.getCurrentLongTime();
        try {
            super.saveOrUpdate(mObject);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveOrUpdate(PlacedDevice mObject) {
        mObject.update_time = DateUtils.getCurrentLongTime();
        try {
            XunshiApplication.getDbUtils().saveOrUpdate(mObject);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveOrUpdateAll(List<? extends Object> entities) {
        for (Object entity : entities) {
            if (entities instanceof Placed)
                ((Placed) entity).createTime = DateUtils.getCurrentLongTime();
        }
        try {
            XunshiApplication.getDbUtils().saveOrUpdate(entities);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 查詢到位間隔
     *
     * @param reportId
     * @param spid
     * @return
     */
    public Placed findPlaced(String reportId, String spid) {
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(Placed.class).where(Placed.DLT, "=", "0").and(Placed.REPORTID, "=", reportId)
                    .and(Placed.PLACED, "=", "1").and(Placed.SPID, "=", spid);

            return (Placed) selector.findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public PlacedDevice findDevicePlaced(String reportId, String deviceId) {
        try {

            Selector selector = XunshiApplication.getDbUtils().selector(PlacedDevice.class).where(PlacedDevice.REPORTID, "=", reportId)
                    .and(PlacedDevice.DEVICEID, "=", deviceId);

            return (PlacedDevice) selector.findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
