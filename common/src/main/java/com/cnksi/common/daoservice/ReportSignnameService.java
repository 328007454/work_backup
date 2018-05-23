package com.cnksi.common.daoservice;

import com.cnksi.common.model.ReportSignname;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 17:57
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ReportSignnameService extends BaseService<ReportSignname> {
    protected ReportSignnameService() {
        super(ReportSignname.class);
    }

    final static ReportSignnameService instance=new ReportSignnameService();

    public static ReportSignnameService getInstance() {
        return instance;
    }

    public List<ReportSignname> getSignNamesForReportAndRole(String reportId, String rolo) throws DbException {
        Selector selector =selector()
                .and(ReportSignname.REPORTID, "=", reportId).and(ReportSignname.SIGNERROLE, "=", rolo).expr("and account is not null");
        return selector.findAll();
    }

    public ReportSignname getSignNamesForReportFirst(String reportId) throws DbException {
       return selector()
                .and(ReportSignname.REPORTID, "=", reportId).expr("and account is not null").findFirst();
    }
    public List<ReportSignname> getSignNamesForReportAll(String reportId) throws DbException {
       return selector()
                .and(ReportSignname.REPORTID, "=", reportId).expr("and account is not null").findAll();
    }

}
