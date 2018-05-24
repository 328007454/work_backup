package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.BatteryDetails;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 13:17
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BatteryDetailsService extends BaseService<BatteryDetails> {
    protected BatteryDetailsService() {
        super(BatteryDetails.class);
    }

    final static BatteryDetailsService instance = new BatteryDetailsService();

    public static BatteryDetailsService getInstance() {
        return instance;
    }

    /**
     * 查询运维一体化电池记录
     *
     * @param reportid 报告ID
     * @return
     */
    public List<BatteryDetails> queryAllRecordBattery(String reportid) {
        List<BatteryDetails> batterys = null;
        try {
            batterys = selector()
                    .and(BatteryDetails.REPORTID, "=", reportid).orderBy(BatteryDetails.BATTERY_NUMBER, false).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batterys;
    }
}
