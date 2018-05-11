package com.cnksi.workticket.db;

import android.content.Context;
import android.text.TextUtils;

import com.cnksi.workticket.Config;
import com.lzy.okgo.db.DBUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

/**
 * @author Mr.K   on 2018/5/3.
 * @decrption 数据库管理
 */

public class WorkTicketDbManager {

    private static WorkTicketDbManager instance;
    private String innerDbFolder;
    private DbManager.DaoConfig config;
    private DbManager ticketManager;

    public static WorkTicketDbManager getInstance() {
        if (instance == null) {
            instance = new WorkTicketDbManager();
        }
        return instance;
    }


    /**
     * 初始化数据库文件夹
     *
     * @param context 上下文
     * @return 该类对象
     */
    public void initDbManagerFolder(Context context) {
        innerDbFolder = context.getFilesDir().getAbsolutePath() + "/database/";
        Config.DATABASE_FOLDER = innerDbFolder;
    }

    public DbManager getTicketManager() {
        if (ticketManager == null) {
            ticketManager = x.getDb(getDaoConfig());
        }
        return ticketManager;
    }

    private DbManager.DaoConfig getDaoConfig() {
        if (config == null) {
            config = new DbManager.DaoConfig().setDbDir(new File(innerDbFolder)).setDbName(Config.DATABASE_NAME).setDbVersion(1)
                    .setDbOpenListener(db -> {
                    })
                    .setUseEncrypt(true)
                    .setKey("com.cnksi")
                    .setDbUpgradeListener((db, oldVersion, newVersion) -> {
                    }).setAllowTransaction(true);
            return config;
        } else {
            return config;
        }
    }

    /**
     * @return 返回数据库文件夹
     */
    public String getDbFolder() {
        if (TextUtils.isEmpty(innerDbFolder)) {
            throw new RuntimeException("请先调用 initDbManager()方法");
        }
        return innerDbFolder;
    }

}
