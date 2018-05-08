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
 * Created by Mr.K on 2018/5/3.
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


    public WorkTicketDbManager initDbManager(Context context) {
        innerDbFolder = context.getFilesDir().getAbsolutePath() + "/database/";
        return this;
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
                        // 开启WAL, 对写入加速提升巨大
                        //db.getDatabase().enableWriteAheadLogging();
                        //此处不处理数据库版本更新  全权交给同步框架处理。
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

    public String getDbFolder() {
        if (TextUtils.isEmpty(innerDbFolder)) {
            throw new RuntimeException("请先调用 initDbManager()方法");
        }
        return innerDbFolder;
    }

}
