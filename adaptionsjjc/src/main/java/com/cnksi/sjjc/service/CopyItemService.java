package com.cnksi.sjjc.service;


import com.cnksi.sjjc.bean.CopyItem;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

public class CopyItemService extends BaseService<CopyItem> {
    public static CopyItemService mInstance;

    private CopyItemService() {
        super(CopyItem.class);
    }

    public static CopyItemService getInstance() {
        if (mInstance == null) {
            mInstance = new CopyItemService();
        }
        return mInstance;
    }

    /**
     * 查询设备下的抄录项目
     *
     * @param bdzId
     * @param deviceId
     * @return
     */
    public List<CopyItem> getDeviceCopyItem(String bdzId, String deviceId) {
        try {
            Selector<CopyItem> selector = selector().and(CopyItem.BDZID, "=", bdzId)
                    .and(CopyItem.DEVICEID, "=", deviceId).orderBy(CopyItem.ID);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<CopyItem> getDeviceCopyItem(String bdzId, String deviceId, String copyType) {
        try {
            Selector<CopyItem> selector = selector().and(CopyItem.BDZID, "=", bdzId)
                    .and(CopyItem.DEVICEID, "=", deviceId).expr(" and " + CopyItem.TYPE_KEY + " in('" + copyType + "') ").orderBy(CopyItem.ID);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<DbModel> getCopyDeviceList(String bdzId, String deviceType) {
        String sql = "select d.deviceid,d.name,d.latitude,d.longitude from device d where d.deviceid in( SELECT DISTINCT(deviceid) from copy_item WHERE bdzid=? and dlt='0') and device_type=? and d.dlt='0'";
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

    public CopyItem getItem(String itemId) {
        try {
            return getDbManager().findById(CopyItem.class, itemId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyItemCount(String bdzId) {
        String sql = "select count(case when val = 'Y' then 1 else null end)+"
                + "count(case when val_a = 'Y' then 1 else null end)+"
                + "count(case when val_b = 'Y' then 1 else null end)+"
                + "count(case when val_c = 'Y' then 1 else null end)+"
                + "count(case when val_o = 'Y' then 1 else null end)"
                + " as copyCount from copy_item item where item.bdzid=? and item.dlt='0'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询变电站下所有抄录项目
     *
     * @param bdzId
     * @return
     */
    public long getCopyItemCount(String bdzId, String itemKeyType) {
//        String sql = "select count(case when val = 'Y' then 1 else null end)+"
//                + "count(case when val_a = 'Y' then 1 else null end)+"
//                + "count(case when val_b = 'Y' then 1 else null end)+"
//                + "count(case when val_c = 'Y' then 1 else null end)+"
//                + "count(case when val_o = 'Y' then 1 else null end)"
//                + " as copyCount from copy_item item where item.bdzid=? and item.type_key in('"+itemKeyType+"')";
        String sql = "select count(1) AS copyCount from copy_item item where item.bdzid=? and type_key in ('" + itemKeyType + "') and item.dlt=0";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel dbModel = findDbModelFirst(sqlInfo);
            return dbModel.getLong("copyCount");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
