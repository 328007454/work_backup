package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.hwcw.HwcwBaseInfo;

import org.xutils.ex.DbException;

/**
 * @author kkk
 * @date 2017/12/13
 */

public class HwcwBaseInfoService extends BaseService<HwcwBaseInfo> {
    public final static HwcwBaseInfoService service = new HwcwBaseInfoService();

    protected HwcwBaseInfoService() {
        super(HwcwBaseInfo.class);
    }

    public static HwcwBaseInfoService getInstance() {
        return service;
    }

    public HwcwBaseInfo getBaseInfo(String reportID) {
        HwcwBaseInfo baseInfo = null;
        try {
            baseInfo = selector().and(HwcwBaseInfo.REPORT_ID, "=", reportID).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (baseInfo == null) {
            baseInfo = new HwcwBaseInfo();
        } else {
            if (TextUtils.isEmpty(baseInfo.testRange)) {
                baseInfo.testRange = "全站";
            } else {
                if (TextUtils.isEmpty(baseInfo.testRange) && TextUtils.equals("是", baseInfo.isAllBdz)) {
                    baseInfo.testRange = "全站";
                } else if (TextUtils.isEmpty(baseInfo.testRange) && TextUtils.equals("否", baseInfo.isAllBdz)) {
                    baseInfo.testRange = "区域";
                }
            }
            if (TextUtils.isEmpty(baseInfo.remark)) {
                baseInfo.remark = "无";
            }
        }
        return baseInfo;
    }


}
