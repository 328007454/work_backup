package com.cnksi.sjjc.service.gztz;

import com.cnksi.sjjc.bean.gztz.SbjcGztzjlBhdzjl;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/3/9 12:00
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class GZTZBhdzjlService extends BaseService<SbjcGztzjlBhdzjl> {
    static final GZTZBhdzjlService instance = new GZTZBhdzjlService();

    private GZTZBhdzjlService() {
        super(SbjcGztzjlBhdzjl.class);
    }

    public static GZTZBhdzjlService getInstance() {
        return instance;
    }

    public List<SbjcGztzjlBhdzjl> findByGzjl(String gzjlId) {
        try {
            return selector().and(SbjcGztzjlBhdzjl.GZTZJL_ID, "=", gzjlId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
