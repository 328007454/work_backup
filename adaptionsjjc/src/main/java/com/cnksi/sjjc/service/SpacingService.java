package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.Spacing;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by ironGe on 2016/6/23.
 */
public class SpacingService extends BaseService<Spacing> {

    public static SpacingService instance;

    private SpacingService() {
        super(Spacing.class);
    }

    public static SpacingService getInstance() {
        if (null == instance)
            instance = new SpacingService();
        return instance;
    }

    public String findSpacingByDeviceId(String deviceId) {
        SqlInfo sqlInfo = new SqlInfo("SELECT s.name from device d LEFT JOIN spacing s on d.spid=s.spid where d.dlt='0' and  d.deviceid=?");
        sqlInfo.addBindArg(new KeyValue("deviceId", deviceId));
        try {
            DbModel model = findDbModelFirst(sqlInfo);
            if (model != null) {
                return model.getString("name");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过间隔查询当前变电站
     */
    public List<DbModel> findBdzBySpacing() {
        List<DbModel> spacingList;
        SqlInfo sqlInfo = new SqlInfo("select * from spacing where dlt <> 1 and device_type like '%one%' and (latitude <> '' or latitude <> null) and (longitude <> '' or longitude <> null)");
        try {
            spacingList = findDbModelAll(sqlInfo);
            return spacingList;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Spacing> findSpacingByModel(String currentBdzId, String model) {
        try {
            return selector().and(Spacing.BDZID, "=", currentBdzId).
                    expr("and spid in (select distinct(spid) spid from device where device_type = '" + model + "' and bdzid = '" + currentBdzId + "')").
                    orderBy(Spacing.SORT, false).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


}
