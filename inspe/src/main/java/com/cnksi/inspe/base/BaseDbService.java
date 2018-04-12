package com.cnksi.inspe.base;

import com.cnksi.inspe.utils.DBUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:23
 */

public class BaseDbService {

    protected DbManager dbManager = DBUtils.getDBManager();

    public DbManager getDbManager() {
        return dbManager;
    }

    public <T> List<T> findAll(Class<T> cls) {
        try {
            return (List<T>) dbManager.selector(cls).select("dlt", "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T find(Class<T> cls) {
        try {
            return (T) dbManager.selector(cls).select("dlt", "=", "0").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Object object) {
        try {
            dbManager.save(object);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean update(Object object) {
        try {
            dbManager.saveOrUpdate(object);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean delete(Object object) {
        try {
            dbManager.delete(object);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
