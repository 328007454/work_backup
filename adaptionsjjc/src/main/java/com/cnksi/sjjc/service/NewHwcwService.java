package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkk on 2017/12/13.
 */

public class NewHwcwService {
    public static NewHwcwService service;

    public static NewHwcwService getInstance() {
        if (service == null) {
            service = new NewHwcwService();
            return service;
        } else {
            return service;
        }
    }

    public HwcwBaseInfo getBaseInfo(String reportID) {
        HwcwBaseInfo baseInfo = null;
        try {
            baseInfo = CustomApplication.getDbManager().selector(HwcwBaseInfo.class).where(HwcwBaseInfo.REPORT_ID, "=", reportID).and(HwcwBaseInfo.DLT, "=", "0").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (baseInfo == null) {
            baseInfo = new HwcwBaseInfo();
        } else {
            if (TextUtils.isEmpty(baseInfo.testRange)) {
                baseInfo.testRange = "全站";
            } else {
                if (TextUtils.equals("是", baseInfo.isAllBdz)) {
                    baseInfo.testRange = "全站";
                } else if (TextUtils.equals("否", baseInfo.isAllBdz)) {
                    baseInfo.testRange = "区域";
                }
            }
            if (TextUtils.isEmpty(baseInfo.testLocation)) {
                baseInfo.testLocation = "全站间隔设备";
            }

            if (TextUtils.isEmpty(baseInfo.problem)) {
                baseInfo.problem = "无";
            }
            if (TextUtils.isEmpty(baseInfo.remark)) {
                baseInfo.remark = "无";
            }
        }
        return baseInfo;
    }

    public List<HwcwLocation> getAllLocation(String baseInfoID) {
        List<HwcwLocation> locations = null;
        try {
            locations = CustomApplication.getDbManager().selector(HwcwLocation.class).where(HwcwLocation.BASEINFO_ID, "=", baseInfoID).and(HwcwLocation.DLT, "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (null == locations) {
            locations = new ArrayList<>();
        }
        return locations;
    }

}
