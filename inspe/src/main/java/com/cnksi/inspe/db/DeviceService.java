package com.cnksi.inspe.db;

import android.widget.LinearLayout;

import com.cnksi.inspe.base.BaseDbService;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 设备相关信息
 * Created by Mr.K on 2018/4/9.
 */

public class DeviceService extends BaseDbService {


    /**
     * 根据设备大类id获取当前变电站的设备，不分一二次设备等
     */

    public List<DbModel> getAllDeviceByBigID(String bdzId, String bigId) throws DbException {
        List<DbModel> deviceModels = new ArrayList<>();
        String deviceSql = "SELECT s.`name` sname ,s.bdzid,s.spid,s.name_pinyin snamepy,d.`name` dname,d.name_short dnameshort,d.bigid,d.name_pinyin dnamepy " +
                "FROM device d LEFT JOIN spacing  s on d.spid = s.spid  WHERE d.bdzid = '" + bdzId + "' and d.bigid in " + bigId + "and d.dlt =0";

        deviceModels = dbManager.findDbModelAll(new SqlInfo(deviceSql));
        return deviceModels;
    }

    public List<DbModel> getBigTypeModels(String bigId) throws DbException {
        List<DbModel> deviceModels = new ArrayList<>();
        String bigTypesSql = "select * from device_bigtype where dlt = 0 and bigid in "+bigId+"";
        deviceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return deviceModels;
    }

}
