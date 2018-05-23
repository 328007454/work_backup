package com.cnksi.bdzinspection.daoservice;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.model.SwitchPic;

import org.xutils.ex.DbException;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 13:49
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SwitchPicService extends BaseService<SwitchPic> {
    protected SwitchPicService() {
        super(SwitchPic.class);
    }


    public static SwitchPicService getInstance() {
        return instance;
    }

    final  static SwitchPicService instance =new SwitchPicService();
    public SwitchPic findFirstPic(String currentReportId, String standId) throws DbException {
        SwitchPic pic = selector().and(SwitchPic.REPORTID, "=", currentReportId).and(SwitchPic.STADIDSWICHERID, "=", standId).findFirst();
        return pic;
    }

}
