package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.BatteryGroup;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 17:50
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BatteryGroupService extends BaseService<BatteryGroup> {
    protected BatteryGroupService() {
        super(BatteryGroup.class);
    }

    final static BatteryGroupService instance = new BatteryGroupService();

    public static BatteryGroupService getInstance() {
        return instance;
    }

    public List<BatteryGroup> findAllBatteryGroup(String currentBdzID, String currentReportId) {
        List<BatteryGroup> batteryGroups = new ArrayList<>();
        try {
            batteryGroups = selector().and(BatteryGroup.REPORTID, "=", currentReportId).and(BatteryGroup.BDZID, "=", currentBdzID).and(BatteryGroup.DLT, "<>", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return batteryGroups;
    }

    public List<DbModel> findBatteryGroup(String currentBdzId) throws DbException {
        List<DbModel> dbModelList = null;
        String sql = "select bid ,amount  from battery where bdzid = '" + currentBdzId + "' and dlt = 0 ";
        dbModelList = findDbModelAll(new SqlInfo(sql));
        return dbModelList;
    }


    public DbModel findAllBatteryCodeCount(String currentBdzId, String currentReportId) {
        DbModel dbModels = null;
        String sql = "select count(*) count from sbjc_battery where reportid = '" + currentReportId + "' and bdzid = '" + currentBdzId + "'  and dlt =0 ";
        try {
            dbModels = findDbModelFirst(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
            return new DbModel();
        }
        return dbModels;

    }


    public List<BatteryGroup> getAllGroup(String currentReportId) {
        List<BatteryGroup> batteryGroupList = null;
        try {
            batteryGroupList = selector().and(BatteryGroup.REPORTID, "=", currentReportId).findAll();
            if (batteryGroupList == null) {
                batteryGroupList = new ArrayList<>();
            }
        } catch (DbException e) {
            e.printStackTrace();
            batteryGroupList = new ArrayList<>();
        }

        return batteryGroupList;
    }

    public BatteryGroup getBatteryGroup(String currentReportId, String batteryGroupId) {
        BatteryGroup group = null;
        try {
            group = selector().and(BatteryGroup.REPORTID, "=", currentReportId).and(BatteryGroup.BATTARY_GROUP, "=", batteryGroupId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return group;


    }
}
