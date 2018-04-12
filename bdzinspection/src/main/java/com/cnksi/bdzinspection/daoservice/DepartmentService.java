package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Department;
import com.cnksi.bdzinspection.model.Users;
import com.cnksi.xscore.xsutils.StringUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

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

    /**
     * 根据 部门的名称或部门名称拼音来查询部门
     *
     * @param name
     * @return
     */
    public List<Department> findDepartmentByName(String name) {
        try {
            Selector selector = Selector.from(Department.class).where(Department.DEPT_PINYIN, "like", "%" + name + "%");
            if (StringUtils.containsChinese(name)) {
                selector = Selector.from(Department.class).where(Department.DEPT_NAME, "like", "%" + name + "%");
            }
            return XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据 部门的名称或部门名称拼音来查询单个部门
     *
     * @param name
     * @return
     */
    public Department findSingleDepartmentByName(String name) {
        try {
            Selector selector = Selector.from(Department.class).where(Department.DEPT_PINYIN, "=", name);
            return XunshiApplication.getDbUtils().findFirst(selector);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 查询当前计划所在部门
     *
     * @param planId
     * @return
     */
    public DbModel findCurrentPlanDepartment(String planId) {
        try {
            String sql = "select d.* from department d left join scene_plan_original s on s.dept_id = d.dept_id where s.scene_plan_id=?";
            return XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo(sql, planId));
        } catch (DbException e) {
            e.printStackTrace();
            return null;
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
        userses = XunshiApplication.getDbUtils().findAll(Selector.from(Users.class).where(Users.DEPT_ID, "=", departID).and(Users.DLT, "=", "0"));
        return userses;
    }
}
