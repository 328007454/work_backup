package com.cnksi.sjjc.service.gztz;

import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/3/7 16:45
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZSbgzjlService extends BaseService<SbjcGztzjl> {
    final static GZTZSbgzjlService instance = new GZTZSbgzjlService();

    protected GZTZSbgzjlService() {
        super(SbjcGztzjl.class);
    }

    public static GZTZSbgzjlService getInstance() {
        return instance;
    }

    public SbjcGztzjl findByReportId(String reportId) {
        try {
            return selector().and(SbjcGztzjl.REPORTID, "=", reportId).findFirst();
        } catch (DbException e) {
            RecordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public SbjcGztzjl findLastByDeviceId(String deviceId, String reportId) {
        try {
            return selector().and(SbjcGztzjl.REPORTID, "!=", reportId).and(SbjcGztzjl.DLQBH, "=", deviceId).orderBy(SbjcGztzjl.CREATE_TIME, true).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
