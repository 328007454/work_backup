package com.cnksi.common.daoservice;

import com.cnksi.common.model.Bdz;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyndon
 * @version 1.0
 * @date 2016/7/7
 */
public class BdzService extends BaseService<Bdz> {
    private final static BdzService instance = new BdzService();

    private BdzService() {
        super(Bdz.class);
    }

    public static BdzService getInstance() {
        return instance;
    }

    public Bdz findByPmsId(String pms_id) throws DbException {
        return selector().and("pms_id", "=", pms_id).findFirst();
    }

    public List<Bdz> findAllBdzByDp(String dept_id) {
        try {
            return selector().and(Bdz.DEPTID, "=", dept_id).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DbModel> findAllBdzByDpDbModel(String deptId) {
        List<DbModel> models = new ArrayList<>();
        String sql = "select * from bdz where dept_id = ? and dlt = 0";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", deptId));
        try {
            models = getDbManager().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
            return models;
        }
        return models;
    }
}
