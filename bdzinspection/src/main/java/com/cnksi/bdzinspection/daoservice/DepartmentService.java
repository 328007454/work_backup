package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.common.model.Department;
import com.cnksi.bdzinspection.model.Users;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pantao 部门service
 */
public class DepartmentService {
    private static DepartmentService mInstance;

    public DepartmentService() {
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
            mDepartment = XunshiApplication.getDbUtils().findById(Department.class, id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDepartment;
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
        String sql = "SELECT u.account,u.type, u.username,dp.name,u.dept_id FROM users u LEFT JOIN department dp on u.dept_id=dp.id where u.account IN("
                + u + ")";
        return XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
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
            return XunshiApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Users> getAllUsers(String departID) throws DbException {
        List<Users> userses = new ArrayList<>();
        userses = XunshiApplication.getDbUtils().selector(Users.class).where(Users.DEPT_ID, "=", departID).and(Users.DLT, "=", "0").findAll();
        return userses;
    }
}
