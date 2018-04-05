package com.lidroid.xutils.db;

import android.database.Cursor;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/2/28 15:12
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public interface KDatabase {
    void setVersion(int version);

    int getVersion();

    void execSQL(String sql, Object... args);

    Cursor rawQuery(String sql, String[] args);

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();

    void close();

    Object getDatabase();

    boolean isOpen();
}
