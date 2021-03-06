package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;

import com.cnksi.bdzinspection.emnu.ToolStatus;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 2017/6/29.
 */

public class SafeToolsInfoService extends BaseService<SafeToolsInfor> {
    private static SafeToolsInfoService safeToolsInfoService;

    public SafeToolsInfoService() {
        super(SafeToolsInfor.class);
    }

    public static SafeToolsInfoService getInstance() {
        if (safeToolsInfoService == null) {
            safeToolsInfoService = new SafeToolsInfoService();
        }
        return safeToolsInfoService;
    }

    public List<DbModel> findAllTools(String dept, String keyWord, String bdzId) {
        List<DbModel> toolsInfors = null;
        List<DbModel> inTestList;
        String sql = "SELECT gi.id,gi.name,gi.bdz_name,gi.dept_name,gi.bdz_id,gi.dept_id,gi.num,gi.lastly_check_time,gi.period,gi.next_check_time,gi.isnormal,gi.status,gi.remark FROM gqj_info gi  WHERE gi.dlt = '0' and dept_id = '" + dept + "' and bdz_id = '" + bdzId + "' and (gi.status <> '" + ToolStatus.stop.name() + "' or gi.status is null ) ";
        if (!TextUtils.isEmpty(keyWord)) {
            sql = sql + " and (gi.name_pinyin like '%" + keyWord + "%' or gi.num like '%" + keyWord + "%') ";
        }
        sql = sql + " ORDER BY  next_check_time is null asc,next_check_time asc";
        try {
            toolsInfors = findDbModelAll(new SqlInfo(sql));
            inTestList = new ArrayList<>();
            for (DbModel dbModel : toolsInfors) {
                if (ToolStatus.inTest.name().equalsIgnoreCase(dbModel.getString("status"))) {
                    inTestList.add(dbModel);
                    continue;
                }
            }
            toolsInfors.removeAll(inTestList);
            toolsInfors.addAll(inTestList);
            return toolsInfors;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<DbModel>();
        }
    }

    public List<DbModel> findAllTools(String dept) {
        String sql = "SELECT id,num,name,short_name,name_pinyin,status,bdz_id,bdz_name,next_check_time FROM gqj_info where dept_id=? and dlt=0 and status not in('" + ToolStatus.inTest
                + "','" + ToolStatus.stop + "') and  datetime(next_check_time, '-1 month') <= datetime('now', 'localtime', 'start of day')  ORDER BY next_check_time;";
        try {
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", dept));
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public DbModel findToolInfo(String deptId, String toolId, String bdzId) {
        DbModel dbModel = null;
        if (!TextUtils.isEmpty(toolId)) {
            String sql = "select gi.id,gi.location,gi.name,gi.num,gi.bdz_id,gi.bdz_name,gi.dept_name,gi.dept_id,gi.model,gi.period,gi.produce_factory,gi.produce_time,gi.lastly_check_time,gi.next_check_time,gi.isnormal,gi.status,gi.remark FROM gqj_info gi  WHERE gi.id = '" + toolId + "' and gi.dept_id= '" + deptId + "' and gi.dlt = '0'  and bdz_id = '" + bdzId + "' ORDER BY period = 0 asc ,next_check_time is null asc,next_check_time asc";
            try {
                dbModel = findDbModelFirst(new SqlInfo(sql));
                return dbModel;
            } catch (DbException e) {
                e.printStackTrace();

            }
        }
        return dbModel;
    }

    public List<DbModel> findInMonthTools(String deptId, String currentTime, String bdzId) {
        List<DbModel> toolsInfors = null;
        String sql = "select gi.id, gi.name,gi.num,gi.bdz_id,gi.dept_id,gi.bdz_name,gi.dept_name,gi.model,gi.period,gi.produce_factory,gi.produce_time,gi.lastly_check_time,gi.next_check_time,gi.isnormal,gi.status,gi.remark FROM gqj_info gi  WHERE datetime(next_check_time) >= datetime('" + currentTime + "','start of day')\n" +
                "AND datetime(next_check_time) <= datetime('" + currentTime + "','localtime','start of day','+1 month') and gi.dept_id= '" + deptId + "' and gi.dlt = '0'  and bdz_id = '" + bdzId + "' and (gi.status <> '" + ToolStatus.stop.name() + "' and gi.status <> 'inTest' or gi.status is null ) ORDER BY   period = 0 asc ,next_check_time is null asc,next_check_time asc";
        try {
            toolsInfors = findDbModelAll(new SqlInfo(sql));
            return toolsInfors;
        } catch (DbException e) {
            e.printStackTrace();

        }
        return toolsInfors;
    }


    public List<DbModel> findInPeriodTools(String deptId, String bdzId) {
        List<DbModel> toolsInfors = null;
        String sql = "select gi.id, gi.name,gi.num,gi.bdz_id,gi.dept_id,gi.bdz_name,gi.dept_name,gi.model,gi.period,gi.produce_factory,gi.produce_time,gi.lastly_check_time,gi.next_check_time,gi.isnormal,gi.status,gi.remark FROM gqj_info gi  WHERE status = '" + ToolStatus.inTest.name() + "' and  bdz_id = '" + bdzId + "' and (gi.status <> '" + ToolStatus.stop.name() + "' or gi.status is null )  and gi.dept_id= '" + deptId + "' and gi.dlt = '0' ORDER BY   period = 0 asc ,next_check_time is null asc,next_check_time asc";
        try {
            toolsInfors = findDbModelAll(new SqlInfo(sql));
            return toolsInfors;
        } catch (DbException e) {
            e.printStackTrace();

        }
        return toolsInfors;
    }

    public List<DbModel> findOverDateTools(String deptId, String currentLongTime, String bdzId) {
        List<DbModel> toolsInfors = null;
        String sql = "select gi.id, gi.name,gi.num,gi.bdz_id,gi.dept_id,gi.bdz_name,gi.dept_name,gi.model,gi.period,gi.produce_factory,gi.produce_time,gi.lastly_check_time,gi.next_check_time,gi.isnormal,gi.status,gi.remark FROM gqj_info gi  WHERE '" + currentLongTime + "' > datetime(next_check_time,'+1 day') and gi.dept_id= '" + deptId + "' and gi.dlt = '0'  and bdz_id = '" + bdzId + "' and (gi.status <> '" + ToolStatus.stop.name() + "' and gi.status <>  '" + ToolStatus.inTest.name() + "' or gi.status is null )   ORDER BY   period = 0 asc ,next_check_time is null asc,next_check_time asc";
        try {
            toolsInfors = findDbModelAll(new SqlInfo(sql));
            return toolsInfors;
        } catch (DbException e) {
            e.printStackTrace();

        }
        return toolsInfors;
    }


    public Map<String, String> countRemindGroupByBdz(String deptId) {
        Map<String, String> rs = new HashMap<>();
        try {

            String sql = "SELECT bdz_id,bdz_name,count(*) AS c  FROM gqj_info where dept_id=? and dlt=0 and status not in('"
                    + ToolStatus.stop + "','" + ToolStatus.inTest + "')  and  datetime(next_check_time, '-1 month') <= datetime('now', 'localtime', 'start of day') GROUP BY bdz_id";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", deptId));
            List<DbModel> models = findDbModelAll(sqlInfo);
            if (models != null) {
                for (DbModel model : models) {
                    rs.put(model.getString("bdz_id"), model.getString("c"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public DbModel findToolCity() {
        DbModel dbModel = null;
        String sql = "select * from city where dlt = 0";
        try {
            dbModel = findDbModelFirst(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return dbModel;
    }

    public void updateStatus(String toolId,ToolStatus status) throws DbException {
        update(WhereBuilder.b("id", "=", toolId), new KeyValue(SafeToolsInfor.STATUS, status.name()));

    }
}
