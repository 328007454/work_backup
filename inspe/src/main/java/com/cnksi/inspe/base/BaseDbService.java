package com.cnksi.inspe.base;

import com.cnksi.inspe.utils.DBUtils;

import org.xutils.DbManager;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:23
 */

public class BaseDbService {

    protected DbManager dbManager = DBUtils.getDBManager();

    public boolean select() {

        return true;
    }

    public boolean insert(Object object) {

        return true;
    }

    public boolean update(Object object) {

        return true;
    }

    public boolean delete(Object object) {

        return true;
    }
}
