package com.cnksi.sjjc.service;

import android.os.Handler;
import android.os.Looper;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.crash.AbstractCrashReportHandler;
import com.cnksi.core.utils.crash.CrashLogUtil;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.BaseModel;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.DbManager;


import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luoxy on 16/4/29.
 */
public class BaseService<T> {

    public static <K> BaseService<K> getInstance(Class<K> clz) {
        return new BaseService<>();
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


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
    public int logicDelete(Class<?> clz, Object id) throws DbException {
        try {
            TableEntity entity = getDbManager().getTable(clz);
            String idName = entity.getId().getName();
            return getDbManager().update(clz, WhereBuilder.b(idName, "=", id), new KeyValue(BaseModel.DLT, "1"));
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public <D> Selector<D> from(Class<D> clz) throws DbException {
        //暂留1=1条件 合并同步之后采用dlt
        return getDbManager().selector(clz).where("1", "=", "1");
    }


    public List<DbModel> findDbModelAll(SqlInfo sqlInfo) throws DbException {
        try {
            return getDbManager().findDbModelAll(sqlInfo);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public DbModel findDbModelFirst(SqlInfo sqlInfo) throws DbException {
        try {
            return getDbManager().findDbModelFirst(sqlInfo);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            getDbManager().update(entity, updateColumnNames);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public void update(Class<?> entityType, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        try {
            getDbManager().update(entityType, whereBuilder, nameValuePairs);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public void execSql(String sql) throws DbException {
        try {
            getDbManager().execNonQuery(sql);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public void execSql(SqlInfo sqlInfo) throws DbException {
        try {
            getDbManager().execNonQuery(sqlInfo);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    protected void RecordException(final DbException e) {
        if (BuildConfig.DEBUG)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    CToast.showShort(CustomApplication.getAppContext(), "操作数据库出错，请确保数据库的完整性。");
                }
            });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CrashLogUtil.writeLog(new File(Config.LOGFOLDER, "DbException-" + FunctionUtil.getPrimarykey() + ".txt"), "DbException",
                        AbstractCrashReportHandler.buildBody(CustomApplication.getInstance()), e.getMessage(), e);
            }
        });
    }
}
