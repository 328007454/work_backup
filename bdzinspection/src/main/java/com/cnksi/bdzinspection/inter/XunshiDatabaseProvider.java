package com.cnksi.bdzinspection.inter;

import org.xutils.DbManager;

/**
 * Created by Mr.K on 2018/4/11.
 */

public interface XunshiDatabaseProvider
{
     Object getDatabase();

    String getDbName();
    DbManager getDbManager();
}
