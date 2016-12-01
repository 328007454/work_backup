package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.BatteryGroup;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/8/10.
 */
public class BatteryGroupService extends  BaseService<BatteryGroup> {

    private static BatteryGroupService batteryGroupService;

    public static BatteryGroupService getInstance(){
        if(batteryGroupService==null){
            batteryGroupService = new BatteryGroupService();
        }
        return  batteryGroupService;
    }



    public List<BatteryGroup> getAllGroup(String currentReportId){
        List<BatteryGroup> batteryGroupList=null;
        try {
            batteryGroupList = CustomApplication.getDbManager().selector(BatteryGroup.class).where(BatteryGroup.REPORTID,"=",currentReportId).findAll();
            if (batteryGroupList==null) {
                batteryGroupList = new ArrayList<BatteryGroup>();
            }
        } catch (DbException e) {
            e.printStackTrace();
            batteryGroupList = new ArrayList<BatteryGroup>();
        }

        return batteryGroupList;
    }

    public BatteryGroup getBatteryGroup(String currentReportId,String batteryGroupId){
        BatteryGroup group=null;
        try {
            group = CustomApplication.getDbManager().selector(BatteryGroup.class).where(BatteryGroup.REPORTID,"=",currentReportId).and(BatteryGroup.BATTARY_GROUP,"=",batteryGroupId).findFirst();

        } catch (DbException e) {
            e.printStackTrace();
        }
        return group;


    }
}
