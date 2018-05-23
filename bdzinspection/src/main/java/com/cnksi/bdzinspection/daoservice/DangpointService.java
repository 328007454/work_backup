package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.Dangpoint;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 19:00
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DangpointService extends BaseService<Dangpoint> {

    protected DangpointService() {
        super(Dangpoint.class);
    }

    final static DangpointService instance=new DangpointService();

    public static DangpointService getInstance() {
        return instance;
    }


    public List<DbModel> findGroupType() throws DbException {
        return selector().groupBy(Dangpoint.AVOID_TYPE).orderBy(Dangpoint.SORT, false).findAll();
    }
    public List<Dangpoint> findByType(String avoidType) throws DbException {
        return selector().and(Dangpoint.AVOID_TYPE, "=", avoidType).findAll();
    }
}
