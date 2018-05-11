package com.cnksi.workticket.db;

import android.text.TextUtils;

import com.cnksi.workticket.Config;
import com.cnksi.workticket.bean.Department;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K  on 2018/5/4.
 * @decrption 变电站service
 */

public class BdzService {
    private static BdzService bdzService;

    public static BdzService getInstance() {
        if (bdzService == null) {
            bdzService = new BdzService();
        }

        return bdzService;
    }

    /**
     * 获取变电站的数据
     *
     * @param deptid    部门id
     * @param otherDept 外来班组
     * @return 变电站数据集合
     * @throws DbException 数据库异常
     */

    public List<DbModel> getBdzData(String deptid, String otherDept) throws DbException {
        List<DbModel> bdzModels ;
        String sql;
        if (!TextUtils.isEmpty(otherDept) && otherDept.equalsIgnoreCase(Config.OTHER_DEPT_USER)) {
            sql = "select * from bdz where dlt = 0 ";
        } else {
            sql = "select * from bdz where dept_id = '" + deptid + "' and dlt = 0 ";
        }
        bdzModels = WorkTicketDbManager.getInstance().getTicketManager().findDbModelAll(new SqlInfo(sql));
        if (bdzModels == null) {
            bdzModels = new ArrayList<>();
        }
        return bdzModels;
    }

    /**
     * @param deptid 班组id
     *               获取当前班组的信息
     */
    public Department getCurrentDept(String deptid) {
        Department department = null;
        try {
            department = WorkTicketDbManager.getInstance().getTicketManager().selector(Department.class).where(Department.ID, "=", deptid).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (department == null) {
            department = new Department();
        }
        return department;
    }

}
