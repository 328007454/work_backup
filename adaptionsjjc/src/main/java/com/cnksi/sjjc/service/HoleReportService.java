package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.HoleRecord;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/14.
 */
public class HoleReportService extends BaseService<HoleRecord> {

    private static HoleReportService mHoleInstance;

    private HoleReportService() {
        super(HoleRecord.class);

    }

    public static HoleReportService getInstance() {
        if (mHoleInstance == null) {
            mHoleInstance = new HoleReportService();
        }
        return mHoleInstance;
    }

    /**
     * 查询现存孔洞的记录（照片与位置信息）
     */

    public List<HoleRecord> getAllHoleRecord(String reportid, String bdzid) {
        List<HoleRecord> listHole = null;
        try {
            listHole = selector().and(HoleRecord.BDZ_ID, "=", bdzid).
                    and(HoleRecord.STATUS, "=", 0).orderBy(HoleRecord.LAST_MODIFY_TIME, true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listHole == null) {
            return new ArrayList<>();
        }
        return listHole;
    }

    /**
     * 查询现存孔洞的清除记录（照片与位置信息）
     */
    public List<HoleRecord> getXianCunHoleRecord(String reportid, String bdzid) {
        List<HoleRecord> listHole = null;
        try {
            listHole = selector().and(HoleRecord.BDZ_ID, "=", bdzid)
                    .and(HoleRecord.REPORT_ID, "=", reportid).and(HoleRecord.CLEAR_REPORTID, "=", reportid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listHole == null) {
            return new ArrayList<>();
        }
        return listHole;
    }

    /**
     * 查询现存孔洞的清除记录（照片与位置信息）
     */
    public List<HoleRecord> getReportRecord(String reportid, String bdzid) {
        List<HoleRecord> listHole = null;
        try {
            listHole = selector().and(HoleRecord.BDZ_ID, "=", bdzid)
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listHole == null) {
            return new ArrayList<>();
        }
        return listHole;
    }

    /**
     * 查询现存孔洞的清除记录（照片与位置信息）
     */
    public List<HoleRecord> getCurrentClearRecord(String reportid, String bdzid) {
        List<HoleRecord> listHole = null;
        try {
            listHole = selector().and(HoleRecord.BDZ_ID, "=", bdzid)
                    .and(HoleRecord.REPORT_ID, "=", reportid).or(HoleRecord.CLEAR_REPORTID, "=", reportid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listHole == null) {
            return new ArrayList<>();
        }
        return listHole;
    }

    /**
     * 查询当前报告清除或者存在的记录
     */
    public List<HoleRecord> findClearPostion(String reportId, String bdzId) {
        List<HoleRecord> listRecord = null;
        try {
            listRecord = selector().and(HoleRecord.BDZ_ID, "=", bdzId)
                    .and(HoleRecord.STATUS, "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return listRecord;
    }
}
