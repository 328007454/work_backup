package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.bean.Bdz;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.bean.Users;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;


/**
 * @author pantao 部门service
 */
public class DepartmentService extends BaseService<Department> {
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
            mDepartment = findById(Department.class, id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return mDepartment;
    }


    public void deleteOtherDataByDept(String id) {
        if (!TextUtils.isEmpty(id) && !"-1".equals(id))
            try {
                getDbManager().delete(Users.class, WhereBuilder.b().expr(" dept_id <>'" + id + "' and dept_id is null"));
                getDbManager().delete(Bdz.class, WhereBuilder.b().expr(" dept_id <>'" + id + "' "));
            } catch (DbException e) {
                e.printStackTrace();
            }
    }

}
