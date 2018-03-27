package com.cnksi.inspe.utils;

import android.os.Environment;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

/**
 * 数据库工具类
 * <br/>
 * 主要初始化数据库
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:14
 */

public final class DBUtils {
    private static DBUtils dbUtils;
    private DbManager dbManager;

    private DBUtils() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("inspection.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbDir(Environment.getExternalStorageDirectory()) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(2)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        // db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });

        dbManager = x.getDb(daoConfig);
    }

    /**
     * 获取数据库管理对象
     *
     * @return
     */
    public static DbManager getDBManager() {

        if (dbUtils == null) {
            synchronized (DBUtils.class) {
                if (dbUtils == null) {
                    dbUtils = new DBUtils();
                }
            }
        }

        if (dbUtils.dbManager != null) {
            return dbUtils.dbManager;
        } else {
            throw new NullPointerException("数据库为null");
        }
    }
}
