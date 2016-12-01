package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.Department;

import org.xutils.ex.DbException;


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
			mDepartment = CustomApplication.getDbManager().findById(Department.class, id);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return mDepartment;
	}


}
