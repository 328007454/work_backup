package com.cnksi.sjjc.service;

import android.util.Log;

import com.cnksi.sjjc.bean.Users;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by luoxy on 16/4/28.Ø
 */
public class UserService extends BaseService<Users> {
    private static UserService instance;

    protected UserService() {
        super(Users.class);
    }

    public static UserService getInstance() {
        if (null == instance)
            instance = new UserService();
        return instance;
    }

    /**
     * 根据用户名和密码查找用户
     *
     * @param username
     * @param pwd
     * @return
     */
    public Users findUserByNameAndPwd(String username, String pwd) {
        Users t = null;
        try {
            t = selector().expr(" and lower(" + Users.ACCOUNT + ") = '" + username.toLowerCase(Locale.CHINA) + "'").expr("and (pwd = '" + pwd + "' or old_pwd = '" + pwd + "') ").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 根据用户名找用户
     *
     * @param username
     * @return
     */
    public Users findUserByAccount(String username) {
        Users t = null;
        try {
            t = selector().expr(" and lower(" + Users.ACCOUNT + ") = '" + username.toLowerCase(Locale.CHINA) + "'").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return t;
    }

    public List<DbModel> getAllUser(String users) throws DbException {
        List<DbModel> dbModels = null;
        String strs[] = users.split(",");
        String u = "";
        for (int i = 0; i < strs.length; i++) {
            u += "'" + strs[i] + "',";
        }
        if (u.length() > 0) {
            u = u.substring(0, u.length() - 1);
        }
        String sql = "SELECT u.username,dp.name,u.dept_id FROM users u LEFT JOIN department dp on u.dept_id=dp.dept_id where u.dlt='0' and u.dept_id in(SELECT dept_id FROM users where account IN(" + u + "))";
        dbModels = findDbModelAll(new SqlInfo(sql));
        return dbModels;
    }

    public List<String> searchUsersName(String name) {
        List<String> nameList = new ArrayList<>();
        try {
            List<Users> usersList =selector().and(Users.ACCOUNT, "like", "%" + name + "%").findAll();
            if (usersList != null) {
                for (Users mUser : usersList) {
                    nameList.add(mUser.account);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            Log.i("Test", "查询用户数据出错了");
        }
        return nameList;
    }
}
