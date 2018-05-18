package com.cnksi.bdzinspection.daoservice;

import android.os.Handler;
import android.os.Looper;

import com.cnksi.bdzinspection.BuildConfig;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.BaseModel;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.utils.crash.AbstractCrashReportHandler;
import com.cnksi.core.utils.crash.CrashLogUtil;

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
        return new BaseService<>(clz);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    protected Class<T> clz;

    public BaseService(){}

    protected BaseService(Class<T> clz) {
        this.clz = clz;
    }

    public void saveOrUpdate(T t) throws DbException {
        try {
            getDbManager().saveOrUpdate(t);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public void saveOrUpdate(List<T> t) throws DbException {
        try {
            getDbManager().saveOrUpdate(t);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }

    }

    public T findById(Object id) throws DbException {
        try {
            return getDbManager().findById(clz, id);
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }

    public Selector<T> selector() throws DbException {
        try {
            return getDbManager().selector(clz).where(BaseModel.DLT, "=", "0");
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
    }


    protected DbManager getDbManager() {
        return XunshiApplication.getDbUtils();
    }

    public TableEntity<T> getTable() throws DbException {
        return getDbManager().getTable(clz);
    }

    /**
     * 逻辑删除
     *
     * @param id
     * @return
     */
    public int logicDeleteById(Object id) throws DbException {
        TableEntity entity = getDbManager().getTable(clz);
        String idName = entity.getId().getName();
        return logicDelete(WhereBuilder.b(idName, "=", id));
    }

    public int logicDelete(WhereBuilder whereBuilder) throws DbException {
        return logicDelete(clz, whereBuilder);
    }

    public int logicDelete(Class clz, WhereBuilder whereBuilder) throws DbException {
        try {
            return update(clz, whereBuilder, new KeyValue(BaseModel.DLT, "1"));
        } catch (DbException e) {
            RecordException(e);
            throw e;
        }
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

    public int update(WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        return update(clz, whereBuilder, nameValuePairs);
    }

    public int update(Class clz, WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        try {
            return getDbManager().update(clz, whereBuilder, nameValuePairs);
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


    public List<T> findAll() throws DbException {
        try {
            return selector().findAll();
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
                    ToastUtils.showMessage("操作数据库出错，请确保数据库的完整性。");
                }
            });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                CrashLogUtil.writeLog(new File(Config.LOGFOLDER, "DbException-" + FunctionUtil.getPrimarykey() + ".txt"), "DbException",
                        AbstractCrashReportHandler.buildBody(XunshiApplication.getInstance()), e.getMessage(), e);
            }
        });
    }
}
