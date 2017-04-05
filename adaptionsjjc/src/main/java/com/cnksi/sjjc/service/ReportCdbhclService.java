package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.ReportCdbhcl;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by han on 2016/4/28.
 */
public class ReportCdbhclService extends BaseService<ReportCdbhcl> {

    private static ReportCdbhclService mReportCdbhclService;

    private ReportCdbhclService() {
        super(ReportCdbhcl.class);
    }

    public static ReportCdbhclService getInstance() {
        if (mReportCdbhclService == null) {
            mReportCdbhclService = new ReportCdbhclService();
            return mReportCdbhclService;
        } else {
            return mReportCdbhclService;
        }
    }

    /**
     * 根据计划id 设备id 变电站id查询
     */
    ReportCdbhcl mReportCdbhcl;

    public ReportCdbhcl getReportCdbhcl(String bdzid, String deviceid) {
        try {
            mReportCdbhcl = selector().and(ReportCdbhcl.DEVICE_ID, "=", deviceid).and(ReportCdbhcl.BDZ_ID, "=", bdzid).findFirst();
            if (mReportCdbhcl == null) {
                mReportCdbhcl = new ReportCdbhcl();
                return mReportCdbhcl;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mReportCdbhcl;
    }


    public List<ReportCdbhcl> getReportCdbhclList(String bdzid, String reportId) throws DbException {
        return selector().and(ReportCdbhcl.REPORT_ID, "=", reportId)
                .and(ReportCdbhcl.BDZ_ID, "=", bdzid).orderBy(ReportCdbhcl.DEVICE_ID, false).findAll();
    }
}
