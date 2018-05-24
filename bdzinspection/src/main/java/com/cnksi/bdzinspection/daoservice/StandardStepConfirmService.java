package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.StandardStepConfirm;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 18:32
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class StandardStepConfirmService extends BaseService<StandardStepConfirm> {
    protected StandardStepConfirmService() {
        super(StandardStepConfirm.class);
    }
    final static StandardStepConfirmService instance=new StandardStepConfirmService();

    public static StandardStepConfirmService getInstance() {
        return instance;
    }

    public StandardStepConfirm findByBdzAndReportAndStand(String bdzid, String reportId, String standid) throws DbException {
       return selector().and(StandardStepConfirm.BDZID, "=", bdzid).and(StandardStepConfirm.REPORTID, "=", reportId).and(StandardStepConfirm.STANDID, "=", standid).findFirst();
    }

}
