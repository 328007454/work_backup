package com.cnksi.inspe.utils;

import android.os.Environment;
import android.util.Log;

import org.xutils.DbManager;
import org.xutils.db.table.DbModel;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库工具类
 * <br/>
 * 主要初始化数据库
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:14
 */

public final class DBUtils {
    private static DBUtils dbUtils;
    private DbManager dbManager;

    private DBUtils() {
        if (new File(Environment.getExternalStorageDirectory(), "/BdzInspection/database/bdzinspection.db").exists()) {
            Log.e("DBUtils", "数据库存在🙂");
        } else {
            Log.e("DBUtils", "数据库不存在！😂");
        }
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("bdzinspection.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbDir(new File(Environment.getExternalStorageDirectory(), "/BdzInspection/database/")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
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
     * @return
     */
    public static synchronized DbManager getDBManager() {

        if (dbUtils == null) {
            dbUtils = new DBUtils();
        }

        if (dbUtils.dbManager != null) {
            return dbUtils.dbManager;
        } else {
            throw new NullPointerException("数据库为null");
        }
    }

    /**
     * DB Array对象解析
     * @param modles
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> parseObjectList(List<DbModel> modles, Class<T> cls) {
        List<T> list = new ArrayList<>();
        if (modles != null) {
            for (DbModel model : modles) {
                list.add(parseObject(model, cls));
            }
        }

        return list;
    }

    /**
     * DB Object解析
     * @param model
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T parseObject(DbModel model, Class<T> cls) {
        if (model == null) {
            return null;
        }

        Field[] fields = cls.getDeclaredFields();
        try {
            T entity = cls.newInstance();

            //以空间换时间
            int length = fields.length;
            //属性
            Field field;
            //属性
            String name;
            //类型
            String type;
            for (int i = 0; i < length; i++) {
                field = fields[i];
                //属性
                name = field.getName();
                //类型
                type = field.getType().getName();

                field.setAccessible(true);

                if (model.isEmpty(name)) {
                    continue;
                }

                if (type.contains("String")) {
                    field.set(entity, model.getString(name));
                } else if (type.contains("int") || type.contains("Integer")) {
                    field.set(entity, model.getInt(name));
                } else if (type.contains("short") || type.contains("Short")) {
                    field.set(entity, model.getInt(name));
                } else if (type.contains("float") || type.contains("Float")) {
                    field.set(entity, model.getFloat(name));
                } else if (type.contains("double") || type.contains("Double")) {
                    field.set(entity, model.getDouble(name));
                } else if (type.contains("boolean") || type.contains("Boolean")) {
                    field.set(entity, model.getBoolean(name));
                } else {
                    Log.w("TeamService", "未定义");
                }


            }

            return entity;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
