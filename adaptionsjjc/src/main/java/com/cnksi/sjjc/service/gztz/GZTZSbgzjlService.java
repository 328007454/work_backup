package com.cnksi.sjjc.service.gztz;

import com.cnksi.sjjc.bean.gztz.SbjcGztzjlSbgzjl;
import com.cnksi.sjjc.service.BaseService;

import org.xutils.ex.DbException;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 16:45
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZSbgzjlService extends BaseService<SbjcGztzjlSbgzjl> {
    final static GZTZSbgzjlService instance = new GZTZSbgzjlService();

    protected GZTZSbgzjlService() {
        super(SbjcGztzjlSbgzjl.class);
    }

    public static GZTZSbgzjlService getInstance() {
        return instance;
    }

    public SbjcGztzjlSbgzjl findByReportId(String reportId) {
        try {
            return selector().and(SbjcGztzjlSbgzjl.REPORTID, "=", reportId).findFirst();
        } catch (DbException e) {
            RecordException(e);
            e.printStackTrace();
        }
        return null;
    }
}
