package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.Battery;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by han on 2016/6/17.
 */
public class BatteryService extends BaseService<Battery> {
    private static BatteryService mBatteryInstance;

    public static BatteryService getInstance() {
        if (mBatteryInstance == null) {
            mBatteryInstance = new BatteryService();
        }
        return mBatteryInstance;
    }


    /**
     * 获取当前变电站下的电池组
     */

    public List<Battery> getAllBattery(String bdzId) throws DbException {
        return CustomApplication.getDbManager().selector(Battery.class).where(Battery.BDZID, "=", bdzId).orderBy(Battery.BID, false).findAll();
    }
}
