package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.SpacingLastly;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 16:26
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpacingLastlyService extends BaseService<SpacingLastly> {
    protected SpacingLastlyService() {
        super(SpacingLastly.class);
    }

    final static SpacingLastlyService instance=new SpacingLastlyService();

    public static SpacingLastlyService getInstance() {
        return instance;
    }

    public SpacingLastly findSpacingLastly(String accounts, String reportId, String mode) {
        String[] accountArray = accounts.split(",");
        try {
            if (accountArray.length > 1) {
                return selector().and(SpacingLastly.REPORTID, "=", reportId).and(SpacingLastly.DEVICE_TYPE, "=", mode).expr("and account like '%" + accountArray[0] + "%' and account like '%" + accountArray[1] + "%'").findFirst();
            } else {
                return selector().and(SpacingLastly.REPORTID, "=", reportId).and(SpacingLastly.DEVICE_TYPE, "=", mode).expr("and account like '%" + accountArray[0] + "%'").findFirst();
            }
        } catch (DbException x) {
        }
        return null;
    }
}
