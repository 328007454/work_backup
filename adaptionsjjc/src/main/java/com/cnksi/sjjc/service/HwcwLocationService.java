package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.hwcw.HwcwLocation;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk
 * @date 2017/12/13
 */

public class HwcwLocationService extends BaseService<HwcwLocation> {
    public final static HwcwLocationService service = new HwcwLocationService();

    protected HwcwLocationService() {
        super(HwcwLocation.class);
    }

    public static HwcwLocationService getInstance() {
        return service;
    }



    public List<HwcwLocation> getAllLocation(String baseInfoID) {
        List<HwcwLocation> locations = null;
        try {
            locations = selector().and(HwcwLocation.BASEINFO_ID, "=", baseInfoID).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (null == locations) {
            locations = new ArrayList<>();
        }
        return locations;
    }

}
