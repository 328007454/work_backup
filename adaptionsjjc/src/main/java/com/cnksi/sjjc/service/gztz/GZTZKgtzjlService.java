package com.cnksi.sjjc.service.gztz;

import com.cnksi.sjjc.bean.gztz.SbjcGztzjlKgtzjl;
import com.cnksi.sjjc.service.BaseService;

import org.xutils.ex.DbException;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 16:45
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZKgtzjlService extends BaseService<SbjcGztzjlKgtzjl> {
    final static GZTZKgtzjlService instance = new GZTZKgtzjlService();

    protected GZTZKgtzjlService() {
        super(SbjcGztzjlKgtzjl.class);
    }

    public static GZTZKgtzjlService getInstance() {
        return instance;
    }

    public SbjcGztzjlKgtzjl findByReportId(String reportId) {
        try {
            return selector().and(SbjcGztzjlKgtzjl.REPORTID, "=", reportId).findFirst();
        } catch (DbException e) {
            RecordException(e);
            e.printStackTrace();
        }
        return null;
    }
}
