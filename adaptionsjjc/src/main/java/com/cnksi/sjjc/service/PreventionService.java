package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.PreventionRecord;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/14.
 */
public class PreventionService extends BaseService<PreventionRecord> {

    private static PreventionService mPreventionInstance;

    public PreventionService() {

    }

    public static PreventionService getInstance() {
        if (mPreventionInstance == null) {
            mPreventionInstance = new PreventionService();
        }
        return mPreventionInstance;
    }

    public List<PreventionRecord> getAllRecord(String reportid, String bdzid) {
        List<PreventionRecord> listRecord = null;
        try {
            listRecord = CustomApplication.getDbManager().selector(PreventionRecord.class).where(PreventionRecord.REPORT_ID, "=", reportid).and(PreventionRecord.BDZ_ID, "=", bdzid).orderBy(PreventionRecord.LAST_MODIFY_TIME, true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listRecord == null) {
            return new ArrayList<PreventionRecord>();
        }
        return listRecord;
    }


    public PreventionRecord findPreventionRecordByReoprtId(String reportId) throws DbException {
        return CustomApplication.getDbManager().selector(PreventionRecord.class).where(PreventionRecord.REPORT_ID, "=", reportId).findFirst();
    }
}
