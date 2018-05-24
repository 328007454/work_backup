package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.ReportTool;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 19:05
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ReportToolService extends BaseService<ReportTool> {
    protected ReportToolService() {
        super(ReportTool.class);
    }
    final  static ReportToolService instance=new ReportToolService();

    public static ReportToolService getInstance() {
        return instance;
    }

    public List<ReportTool> findByReportId(String reportId) throws DbException {
        return selector().and(ReportTool.REPORTID, "=", reportId).findAll();
    }
}
