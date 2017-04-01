package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;

/**
 * Created by luoxy on 16/4/29.
 */
public class BaseService<T> {
    public void saveOrUpdate(T t) throws DbException {
        getDbManager().saveOrUpdate(t);
    }

    public T findById(Class<T> clazz, Object id) throws DbException {
        return getDbManager().findById(clazz, id);
    }

    public <D> Selector<D> from(Class<D> clz) throws DbException {
        //暂留1=1条件 合并同步之后采用dlt
        return getDbManager().selector(clz).where("1", "=", "1");
    }

    protected DbManager getDbManager() {
        return CustomApplication.getDbManager();
    }
}
