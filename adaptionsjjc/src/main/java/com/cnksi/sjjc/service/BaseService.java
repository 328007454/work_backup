package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.BaseModel;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

/**
 * Created by luoxy on 16/4/29.
 */
public class BaseService<T> {

    public static <K> BaseService<K> getInstance(Class<K> clz) {
        return new BaseService<>();
    }


    public void saveOrUpdate(T t) throws DbException {
        getDbManager().saveOrUpdate(t);
    }

    public T findById(Class<T> clazz, Object id) throws DbException {
        return getDbManager().findById(clazz, id);
    }

    protected Selector<T> selector(Class<T> clz) throws DbException {
        return getDbManager().selector(clz).where(BaseModel.DLT, "=", "0");
    }

    protected DbManager getDbManager() {
        return CustomApplication.getDbManager();
    }

    /**
     * 逻辑删除
     *
     * @param clz
     * @param id
     * @return
     */
    public int logicDelete(Class<?> clz, Object id) {
        try {
            TableEntity entity = getDbManager().getTable(clz);
            String idName = entity.getId().getName();
            return getDbManager().update(clz, WhereBuilder.b(idName, "=", id), new KeyValue(BaseModel.DLT, "1"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
