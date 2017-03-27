package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.DefectRecord;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 缺陷查询统计
 *
 * @author
 */
public class DefectRecordService {

    public static DefectRecordService mInstance;

    private DefectRecordService() {
    }

    public static DefectRecordService getInstance() {
        if (mInstance == null) {
            mInstance = new DefectRecordService();
        }
        return mInstance;
    }


    /**
     * 查询变电站现存的所有缺陷
     *
     * @return
     */
    public List<DefectRecord> queryCurrentBdzExistDefectList() {
        List<DefectRecord> defects = null;
        try {
            defects = CustomApplication.getDbManager().selector(DefectRecord.class).where(DefectRecord.HAS_TRACK, "=", "N")
                    .and(DefectRecord.HAS_REMOVE, "=", "N")
                    .and(DefectRecord.IS_COPY, "<>", "Y")
                    .expr("AND (" + DefectRecord.VAL + "='' OR " + DefectRecord.VAL + " IS NULL) AND (" + DefectRecord.DLT + "='0' OR " + DefectRecord.DLT + " IS NULL) ")
                    .orderBy(DefectRecord.DISCOVERED_DATE, true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return defects;
    }


}