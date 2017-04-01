package com.cnksi.sjjc.service;


import com.cnksi.sjjc.bean.Placed;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Spacing;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 * 到位service
 *
 * @author luoxy
 */
public class PlacedService extends BaseService<Placed> {

    private static PlacedService instance;

    public static PlacedService getInstance() {
        if (null == instance)
            instance = new PlacedService();
        return instance;
    }

    /**
     * 查詢已到間隔
     *
     * @param report
     * @return
     */
    public String findPlacedSpace(Report report) {
        try {
            DbModel result = from(Placed.class).select(" count(DISTINCT spid) as c").and(Placed.REPORTID, "=", report.reportid).and(Placed.PLACED, "=",
                    "1").findFirst();
            long arrived = result.getLong("c");
            long total = SpacingService.getInstance().from(Spacing.class).
                    expr("and spid in (select distinct(spid) spid from device where device_type = 'one' and bdzid = '" + report.bdzid + "')").count();
            return arrived + "/" + total;
        } catch (DbException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


}
