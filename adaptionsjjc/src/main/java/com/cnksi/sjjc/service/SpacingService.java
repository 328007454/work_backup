package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.Spacing;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 * Created by ironGe on 2016/6/23.
 */
public class SpacingService extends BaseService<Spacing> {

    public static SpacingService instance;

    public static SpacingService getInstance() {
        if (null == instance)
            instance = new SpacingService();
        return instance;
    }

    public String findSpacingByDeviceId(String deviceId)
    {
        SqlInfo sqlInfo=new SqlInfo("SELECT s.name from device d LEFT JOIN spacing s on d.spid=s.spid where d.deviceid=?");
        sqlInfo.addBindArg(new KeyValue("deviceId",deviceId));
        try {
            DbModel model= CustomApplication.getDbManager().findDbModelFirst(sqlInfo);
            if (model!=null)
            {
                return model.getString("name");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return "";
    }


}
