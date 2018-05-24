package com.cnksi.nari.utils;


import com.cnksi.common.CommonApplication;
import com.cnksi.nari.model.BDPackage;
import com.cnksi.nari.model.XSJH;
import com.cnksi.nari.type.PackageStatus;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 南瑞离线作业包下载之后的数据管理 用于连接db3数据库
 *
 * @version 1.0
 * @auth wastrel
 * @date 2017/8/1 9:20
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NariDataManager {
    static HashMap<String, DbManager.DaoConfig> PATHMAP = new HashMap<>();
    private static String BASEFOLDER;

    public static void init(String baseFolder) {
        BASEFOLDER = baseFolder;
    }

    public static DbManager getDbManager(File file) {
        DbManager.DaoConfig daoConfig = PATHMAP.get(file.getAbsolutePath());
        if (daoConfig == null) {
            daoConfig = buildConfig(file.getParent(), file.getName());
            PATHMAP.put(file.getAbsolutePath(), daoConfig);
        }
        return x.getDb(daoConfig);
    }

    public static DbManager getPackageManager() {
        return CommonApplication.getInstance().getDbManager();
    }

    public static HashMap<String, BDPackage> getPackageByUser(String user) {
        HashMap<String, BDPackage> map = new HashMap<>();
        try {
            List<BDPackage> rs = getPackageManager().selector(BDPackage.class).where("user", "=", user).and(BDPackage.PACKAGESTATUS, "<>", PackageStatus.upload.name()).findAll();
            if (rs != null) {
                for (BDPackage r : rs) {
                    map.put(r.packageID, r);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void markBdPackageStatus(String pmsJhId, PackageStatus status) throws DbException {
        getPackageManager().update(BDPackage.class, WhereBuilder.b("pms_jhid", "=", pmsJhId),new KeyValue("status",status.name()));
    }


    public static void unBindingTask(String taskid) throws DbException {
        getPackageManager().update(BDPackage.class, WhereBuilder.b("taskId", "=", taskid),new KeyValue("status",PackageStatus.undo.name()),new KeyValue("taskId",null));
    }

    public static BDPackage getPackageByTaskId(String taskId) throws DbException {
        return getPackageManager().selector(BDPackage.class).expr(" taskId like '" + taskId + "'").findFirst();
    }

    private static DbManager.DaoConfig buildConfig(String parent, String name) {
        DbManager.DaoConfig daoConfig = CommonApplication.getInstance().getDaoConfig();
        daoConfig.setDbDir(new File(parent));
        daoConfig.setDbName(name);
        return daoConfig;
    }

    /**
     * 解析下发的DB3
     *
     * @param dbFile
     * @return
     * @throws DbException
     */
    public static XSJH parseDb3(File dbFile) throws DbException {
        DbManager dma = NariDataManager.getDbManager(dbFile);
        DbModel model = dma.findDbModelFirst(new SqlInfo("SELECT * FROM 'T_YJ_DWYJ_XS_ZNXSJH';"));
        XSJH xsjh = new XSJH();
        xsjh.OBJ_ID = model.getString("OBJ_ID");
        xsjh.BDZ = model.getString("BDZ");
        xsjh.XSLX = model.getString("XSLX");
        xsjh.JHSJ = model.getString("JHXSSJ");
        return xsjh;
    }


}
