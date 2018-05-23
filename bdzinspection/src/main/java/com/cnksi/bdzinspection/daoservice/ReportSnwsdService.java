package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.ReportSnwsd;
import com.cnksi.common.daoservice.BaseService;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 16:55
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ReportSnwsdService extends BaseService<ReportSnwsd> {

  final static ReportSnwsdService instance= new ReportSnwsdService();
    protected ReportSnwsdService() {
        super(ReportSnwsd.class);
    }

    public static ReportSnwsdService getInstance() {
        return instance;
    }
}
