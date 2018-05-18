package com.cnksi.bdzinspection.daoservice;

import java.util.Locale;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Users;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

/**
 * 用户登陆Service
 *
 * @author terry
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
     * @return
     */
    public Users findUserByAccount(String username) {
        try {
            return XunshiApplication.getDbUtils().selector(Users.class)
                    .where("lower(" + Users.ACCOUNT + ")", "=", username.toLowerCase(Locale.CHINA)).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }


}
