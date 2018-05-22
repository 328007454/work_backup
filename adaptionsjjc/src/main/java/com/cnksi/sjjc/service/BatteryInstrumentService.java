package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.BatteryInstrument;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/4.
 */

public class BatteryInstrumentService extends BaseService<BatteryInstrument> {
    private static BatteryInstrumentService instance;

    private BatteryInstrumentService() {
        super(BatteryInstrument.class);
    }

    public static BatteryInstrumentService getInstance() {
        if (null == instance) {
            instance = new BatteryInstrumentService();
        }
        return instance;
    }


    public List<BatteryInstrument> findAllInstrument(int pagestart, int pageEnd) {
        List<BatteryInstrument> instruments = new ArrayList<>();

        try {
            instruments = selector().expr("order by select_num  desc , last_modify_time  desc limit " + pagestart + "," + pageEnd).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return instruments;
    }

    public List<BatteryInstrument> findAllLikeName(String name) {
        List<BatteryInstrument> instruments = new ArrayList<>();


        try {
            instruments = selector().expr("and (csyqmc like '%" + name + "%' or  num like '%" + name + "%') order by select_num  desc , last_modify_time desc").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return instruments;
    }

    public void saveOrUpdateObject(String id, String selectDeviceNum) {

        try {
            String sql = "update test_instrument set select_num=" + selectDeviceNum + " , last_modify_time='" + DateUtils.getCurrentLongTime() + "' where id = '" + id + "'";
            CustomApplication.getInstance().getDbManager().executeUpdateDelete(sql);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }
}
