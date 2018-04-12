package com.cnksi.bdzinspection.daoservice;

import java.util.Locale;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Users;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 * 用户登陆Service
 *
 * @author terry
 *
 */
public class UserService {

	public static UserService mInstance;

	private UserService() {
	}

	public static UserService getInstance() {
		if (mInstance == null) {
			mInstance = new UserService();
		}
		return mInstance;
	}



	/**
	 * 根据用户名找用户
	 *
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Users findUserByAccount(String username) {
		try {
			return XunshiApplication.getDbUtils().findFirst(Selector.from(Users.class)
					.where("lower(" + Users.ACCOUNT + ")", "=", username.toLowerCase(Locale.CHINA)));
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}


}
