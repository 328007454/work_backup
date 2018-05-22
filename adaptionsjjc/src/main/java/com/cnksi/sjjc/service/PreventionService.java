package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.PreventionRecord;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/14.
 */
public class PreventionService extends BaseService<PreventionRecord> {

    private static PreventionService mPreventionInstance;

    private PreventionService() {
        super(PreventionRecord.class);

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
            listRecord = selector().and(PreventionRecord.REPORT_ID, "=", reportid).and(PreventionRecord.BDZ_ID, "=", bdzid).orderBy(PreventionRecord.LAST_MODIFY_TIME, true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listRecord == null) {
            return new ArrayList<>();
        }
        return listRecord;
    }


    public PreventionRecord findPreventionRecordByReoprtId(String reportId) throws DbException {
        return selector().and(PreventionRecord.REPORT_ID, "=", reportId).findFirst();
    }
}
