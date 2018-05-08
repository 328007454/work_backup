package com.cnksi.workticket.db;

import com.cnksi.workticket.bean.Department;
import com.cnksi.workticket.bean.LookUpLocal;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.K on 2018/5/4.
 */

public class BdzService {
    private static BdzService bdzService;

    public static BdzService getInstance() {
        if (bdzService == null) {
            bdzService = new BdzService();
        }

        return bdzService;
    }


    public List<DbModel> getBdzData(String deptid) throws DbException {
        List<DbModel> bdzModels = new ArrayList<>();

        String sql = "select * from bdz where dept_id = '" + deptid + "' and dlt = 0 ";
        bdzModels = WorkTicketDbManager.getInstance().getTicketManager().findDbModelAll(new SqlInfo(sql));
        return bdzModels;
    }

    /**
     * 获取当前班组的信息
     */
    public Department getCurrentDept(String deptid) {
        Department department = null;
        try {
            department = WorkTicketDbManager.getInstance().getTicketManager().selector(Department.class).where(Department.ID, "=", deptid).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return department;
    }

    public LookUpLocal getLookUpLocal() {
       LookUpLocal lookUpLocal =null;

        try {
            lookUpLocal = WorkTicketDbManager.getInstance().getTicketManager().selector(LookUpLocal.class).where(LookUpLocal.TYPE, "=", "workticket_worktype_notice").and("dlt", "=", "0").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return lookUpLocal;


    }
}
