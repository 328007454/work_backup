package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.ReportCdbhcl;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by han on 2016/4/28.
 */
public class ReportCdbhclService extends BaseService {

    private static ReportCdbhclService mReportCdbhclService;

    public ReportCdbhclService() {
    }

    public static ReportCdbhclService getIntance() {
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
            mReportCdbhcl = CustomApplication.getDbManager().selector(ReportCdbhcl.class).where(ReportCdbhcl.DEVICE_ID, "=", deviceid).and(ReportCdbhcl.BDZ_ID, "=", bdzid).findFirst();
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
        return CustomApplication.getDbManager().selector(ReportCdbhcl.class).where(ReportCdbhcl.REPORT_ID,"=",reportId)
        .and(ReportCdbhcl.BDZ_ID,"=",bdzid).orderBy(ReportCdbhcl.DEVICE_ID,false).findAll();
    }
}
