package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.ReportHwcw;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by ksi-android on 2016/4/28.
 */
public class HwcwService {
    private static HwcwService mInstance;
    private HwcwService()
    {}
    public static HwcwService getInstance()
    {
        if (mInstance==null)
        {
            mInstance=new HwcwService();
        }
        return mInstance;
    }

    public  List<ReportHwcw> findAll(String reportid,String type)
    {
        try{
            return CustomApplication.getDbManager().selector(ReportHwcw.class).where(ReportHwcw.REPORT_ID,"=",reportid).and(ReportHwcw.DLT,"<>","1").and(ReportHwcw.TYPE,"=",type).orderBy(ReportHwcw.LAST_MODIFY_TIME).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  Boolean saveOrUpdateOne(ReportHwcw bean)
    {
        try{
            CustomApplication.getDbManager().saveOrUpdate(bean);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
             return false;
        }finally {
            ModifyRecordService.getInstance().saveOrUpdateModifyRecord(bean.id, ReportHwcw.ID,"report_hwcw_frd","update");
        }
    }


}
