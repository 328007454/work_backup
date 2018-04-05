package com.lidroid.xutils.db;

import android.database.Cursor;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;


/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/19 9:55
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class CipherDatabase implements KDatabase {
    SQLiteDatabase database;

    static {
        SQLiteDatabase.loadLibs(null, (File) null);
    }


    public CipherDatabase(DbUtils.DaoConfig config) {
        File dbFile = null;
        String dbDir = config.getDbDir();
        if (!TextUtils.isEmpty(dbDir)) {
            File dir = new File(dbDir);
            if (dir.exists() || dir.mkdirs()) {
                dbFile = new File(dbDir, config.getDbName());
            }
        }
        if (dbFile == null) {
            dbFile = config.getContext().getDatabasePath(config.getDbName());
        }
        database = SQLiteDatabase.openOrCreateDatabase(dbFile, config.getPassword(), null);
    }

    @Override
    public void setVersion(int version) {
        database.setVersion(version);
    }

    @Override
    public int getVersion() {
        return database.getVersion();
    }

    @Override
    public void execSQL(String sql, Object... args) {
        database.execSQL(sql, args);
    }

    @Override
    public Cursor rawQuery(String sql, String[] args) {
        return database.rawQuery(sql, args);
    }

    @Override
    public void beginTransaction() {
        database.beginTransaction();
    }

    @Override
    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    @Override
    public void endTransaction() {
        database.endTransaction();
    }

    @Override
    public Object getDatabase() {
        return database;
    }

    public void close() {
        database.close();
    }

    @Override
    public boolean isOpen() {
        return database.isOpen();
    }
}
