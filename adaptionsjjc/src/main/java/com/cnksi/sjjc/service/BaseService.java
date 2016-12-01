package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;

import org.xutils.ex.DbException;

/**
 * Created by luoxy on 16/4/29.
 */
public class BaseService<T> {
    public void saveOrUpdate(T t) throws DbException {
        CustomApplication.getDbManager().saveOrUpdate(t);
    }

    public T findById(Class<T> clazz, Object id) throws DbException {
        return CustomApplication.getDbManager().findById(clazz, id);
    }


}
