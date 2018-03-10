package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.bean.Report;

import org.xutils.ex.DbException;

/**
 * 报告
 */
public class ReportService extends BaseService<Report> {
    private static ReportService instance;

    private ReportService() {
        super(Report.class);
    }

    public static ReportService getInstance() {
        if (null == instance) instance = new ReportService();
        return instance;
    }

    public Report getReportByTask(String taskId) throws DbException {
        if (!TextUtils.isEmpty(taskId)) {
            return selector().and(Report.TASK_ID, "=", taskId).findFirst();
        }
        return null;
    }

    /**
     * 查询report
     *
     * @param id 主键ID
     * @return
     * @throws DbException
     */
    public Report getReportById(String id) {
        if (!TextUtils.isEmpty(id)) {
            try {
                return findById(id);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
