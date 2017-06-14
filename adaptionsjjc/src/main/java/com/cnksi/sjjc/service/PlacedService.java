package com.cnksi.sjjc.service;


import com.cnksi.sjjc.bean.Placed;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 * 到位service
 *
 * @author luoxy
 */
public class PlacedService extends BaseService<Placed> {

    private static PlacedService instance;

    protected PlacedService() {
        super(Placed.class);
    }

    public static PlacedService getInstance() {
        if (null == instance)
            instance = new PlacedService();
        return instance;
    }

    /**
     * 查詢已到間隔
     *
     * @param reportId
     * @param bdzId
     * @return
     */
    public String findPlacedSpace(String reportId, String bdzId) {
        try {
            DbModel result = selector().select(" count(DISTINCT spid) as c").and(Placed.REPORTID, "=", reportId).and(Placed.PLACED, "=",
                    "1").findFirst();
            long arrived = result.getLong("c");
            long total = SpacingService.getInstance().selector().
                    expr("and spid in (select distinct(spid) spid from device where device_type like '%one%' and dlt='0' and bdzid = '" + bdzId + "')").count();
            return arrived + "/" + total;
        } catch (DbException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


}
