package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.RoleType;

import org.xutils.ex.DbException;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/29 09:27
 */

public final class UserService extends BaseDbService {

    private static UserService userService;

    public static UserService getInstance() {
        if (userService == null) {
            synchronized (UserService.class) {
                if (userService == null) {
                    userService = new UserService();
                }
            }
        }

        return userService;
    }

    private UserService() {

    }

    //用户,多用户登录主任->专责->组长->维护人员
    private UserEntity userEntity1;
    //用户2
    private UserEntity userEntity2;


    /**
     * 初始化登录用户
     *
     * @param userIds
     */
    public void initIds(String... userIds) {
        clear();//防止用户组重叠

        switch (userIds.length - 1) {
            case 1:
                userEntity2 = getUserOnId(userIds[1]);
            case 0:
                userEntity1 = getUserOnId(userIds[0]);
            default:
                break;
        }

        sort(userEntity1, userEntity2);
    }

    public void initNames(String... userNames) {
        clear();//防止用户组重叠

        switch (userNames.length - 1) {
            case 1:
                userEntity2 = getUserOnName(userNames[1]);
            case 0:
                userEntity1 = getUserOnName(userNames[0]);
            default:
                break;
        }
        sort(userEntity1, userEntity2);
    }

    /**
     * 根据权限排序，权限大的拍第一位
     *
     * @param user1
     * @param user2
     */
    private void sort(UserEntity user1, UserEntity user2) {
        if (user1 != null && user2 != null) {
            RoleType roleType1 = RoleType.getMaxRole(user1.getType());
            user1.setRoleType(roleType1);
            RoleType roleType2 = RoleType.getMaxRole(user2.getType());
            user2.setRoleType(roleType2);

            //比较
            RoleType maxRole = RoleType.getMaxRole(roleType1, roleType2);
            if (maxRole != roleType1) {
                UserEntity maxUserEntity = userEntity1;
                userEntity1 = userEntity2;
                userEntity2 = maxUserEntity;
            }

        }
    }

    /**
     * 清除用户
     */
    public void clear() {
        userEntity1 = null;
        userEntity2 = null;
    }

    /**
     * 根据Id获取用户
     *
     * @param id
     * @return
     */
    public UserEntity getUserOnId(String id) {
        try {
            return dbManager.selector(UserEntity.class).where("id", "=", id).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    public UserEntity getUserOnName(String name) {
        try {
            return dbManager.selector(UserEntity.class).where("account", "=", name).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取用户1
     *
     * @return
     */
    public UserEntity getUser1() {
        return userEntity1;
    }

    /**
     * 获取用户2
     *
     * @return
     */
    public UserEntity getUser2() {
        return userEntity2;
    }
}
