package com.cnksi.common.daoservice;

import android.text.TextUtils;

import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Department;
import com.cnksi.common.model.Users;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author pantao 部门service
 */
public class DepartmentService extends BaseService<Department> {
    private static DepartmentService mInstance;

    public DepartmentService() {
        super(Department.class);
    }

    public static DepartmentService getInstance() {
        if (mInstance == null) {
            mInstance = new DepartmentService();
        }
        return mInstance;
    }

    /**
     * 通过id查询部门
     *
     * @param id
     * @return
     */
    public Department findDepartmentById(String id) {
        Department mDepartment = null;
        try {
            mDepartment = findById(id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDepartment;
    }


    public void deleteOtherDataByDept(String id) {
        if (!TextUtils.isEmpty(id) && !"-1".equals(id)) {
            try {
                getDbManager().delete(Users.class, WhereBuilder.b().expr(" dept_id <>'" + id + "'  or dept_id is null"));
                getDbManager().delete(Bdz.class, WhereBuilder.b().expr(" dept_id <>'" + id + "' "));
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }


    public List<DbModel> findUserForCurrentUser(String users) throws DbException {
        String strs[] = users.split(",");
        String u = "";
        for (int i = 0; i < strs.length; i++) {
            u += "'" + strs[i] + "',";
        }
        if (u.length() > 0) {
            u = u.substring(0, u.length() - 1);
        }
        String sql = "SELECT u.account,u.type, u.username,dp.name dept_name,u.dept_id FROM users u LEFT JOIN department dp on u.dept_id=dp.id where u.account IN("
                + u + ")";
        return findDbModelAll(new SqlInfo(sql));
    }


    public List<DbModel> findAllUserForCurrentUser(String users) {
        String strs[] = users.split(",");
        String u = "";
        for (int i = 0; i < strs.length; i++) {
            u += "'" + strs[i] + "',";
        }
        if (u.length() > 0) {
            u = u.substring(0, u.length() - 1);
        }
        String sql = "SELECT u.account,u.username,dp.name,u.dept_id FROM users u LEFT JOIN department dp on u.dept_id=dp.id where u.dlt=0 and u.dept_id in(SELECT dept_id FROM users where account IN("
                + u + "))";
        try {
            return findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Users> getAllUsers(String departID) throws DbException {

        return UserService.getInstance().selector().and(Users.DEPT_ID, "=", departID).and(Users.DLT, "=", "0").findAll();
    }

}
