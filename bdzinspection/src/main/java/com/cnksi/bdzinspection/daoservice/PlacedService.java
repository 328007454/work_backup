package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Placed;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

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
        Selector selector = BaseService.from(Placed.class).where(Placed.REPORTID, "=", reportId).and(Placed.PLACED, "=",
                "1");
        try {
            placed = XunshiApplication.getDbUtils().findAll(selector);
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

        Selector selector = BaseService.from(PlacedDevice.class).where(PlacedDevice.REPORTID, "=", reportId);
        try {
            return XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashMap<String, String> findPmsPlaced(String reportId) {
        HashMap<String, String> map = new HashMap<>();
        try {
            List<DbModel> dbModels = XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo("SELECT  s.pms_id,p.create_time FROM `placed` p LEFT JOIN spacing s on p.spid=s.spid  where report_id=? and p.dlt=0 GROUP BY pms_id; ", reportId));
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
        return super.saveOrUpdate(mObject);
    }

    public boolean saveOrUpdate(PlacedDevice mObject) {
        mObject.update_time = DateUtils.getCurrentLongTime();
        return super.saveOrUpdate(mObject);
    }

    @Override
    public boolean saveOrUpdateAll(List<? extends Object> entities) {
        for (Object entity : entities) {
            if (entities instanceof Placed)
                ((Placed) entity).createTime = DateUtils.getCurrentLongTime();
        }
        return super.saveOrUpdateAll(entities);
    }

    /**
     * 查詢到位間隔
     *
     * @param reportId
     * @param spid
     * @return
     */
    public Placed findPlaced(String reportId, String spid) {
        Selector selector = BaseService.from(Placed.class).and(Placed.REPORTID, "=", reportId)
                .and(Placed.PLACED, "=", "1").and(Placed.SPID, "=", spid);
        try {
            return XunshiApplication.getDbUtils().findFirst(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public PlacedDevice findDevicePlaced(String reportId, String deviceId) {
        Selector selector = Selector.from(PlacedDevice.class).where(PlacedDevice.REPORTID, "=", reportId)
                .and(PlacedDevice.DEVICEID, "=", deviceId);
        try {
            return XunshiApplication.getDbUtils().findFirst(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
