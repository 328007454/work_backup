package com.cnksi.inspe.db;

import android.text.TextUtils;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.db.entity.UserGroupEntity;
import com.cnksi.inspe.type.RoleType;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public UserEntity[] getUsers() {
        if (userEntity1 == null && userEntity2 == null) {
            return new UserEntity[]{};
        }

        if (userEntity1 != null && userEntity2 != null) {
            return new UserEntity[]{userEntity1, userEntity2};
        }

        if (userEntity1 != null) {
            return new UserEntity[]{userEntity1};
        } else {
            return new UserEntity[]{userEntity2};
        }

    }

    public String[] getUserIds() {
        UserEntity[] array = getUsers();
        String[] values = new String[array.length];
        for (int i = 0, length = array.length; i < length; i++) {
            values[i] = array[i].getId();
        }

        return values;
    }

    /**
     * 根据权限排序，权限大的拍第一位
     *
     * @param user1
     * @param user2
     */
    private void sort(UserEntity user1, UserEntity user2) {
        List<RoleType> roleTypes1 = null;
        List<RoleType> roleTypes2 = null;
        Map<String, String> groupMap = new HashMap<>();
        if (user1 != null) {
            roleTypes1 = RoleType.getRoles(user1.getType());
            user1.setRoleTypes(roleTypes1);
            if (!TextUtils.isEmpty(user1.getDept_id())) {
                String[] userGroups = user1.getDept_id().split(",");
                for (String g : userGroups) {
                    groupMap.put(g, g);
                }
            }
        }

        if (user2 != null) {
            roleTypes2 = RoleType.getRoles(user2.getType());
            user2.setRoleTypes(roleTypes2);

            if (!TextUtils.isEmpty(user2.getDept_id())) {
                String[] userGroups = user2.getDept_id().split(",");
                for (String g : userGroups) {
                    groupMap.put(g, g);
                }
            }
        }

        userGroup = new String[groupMap.size()];
        int i = 0;
        for (String key : groupMap.keySet()) {
            userGroup[i] = key;
        }

        //比较
        if (roleTypes1 != null && roleTypes2 != null) {
            RoleType roleType1 = user1.getRoleType();
            RoleType roleType2 = user2.getRoleType();
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

    /**
     * 根据account获取用户
     *
     * @param name
     * @return
     */
    public UserEntity getUserOnName(String name) {
        try {
            return dbManager.selector(UserEntity.class).where("account", "=", name).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String[] userGroup;

    /**
     * 获取用组ID
     *
     * @return
     */
    public String[] getUserGroup0() {
        return userGroup;
    }

    /**
     * 获取用户分组
     *
     * @return
     */
    public List<UserGroupEntity> getUserGroup() {
        try {
            return dbManager.selector(UserGroupEntity.class).where("dlt", "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return new ArrayList<UserGroupEntity>();
    }

    /**
     * 获取指定角色用户组<p/>
     *
     * @param groupId  组ID
     * @param userId   用户ID
     * @param roleType 角色
     * @return
     */
    public List<UserEntity> getUsers(String groupId, String userId, RoleType roleType) {
        try {
            Selector selector = dbManager.selector(UserEntity.class)
                    .where("dlt", "=", "0");
            if (groupId != null) {
                selector.and("dept_id", "=", groupId);
            }

            if (roleType != null) {
                selector.and("type", "LIKE", "%" + roleType.name() + "%");
            }

            if (userId != null) {
                selector.or("id", "=", userId);
            }

            return selector.findAll();

        } catch (DbException e) {
            e.printStackTrace();
        }

        return new ArrayList<UserEntity>();
    }

    /**
     * 获取专家用户
     *
     * @param roleType
     * @return 没有专家则返回null
     */
    public UserEntity getUserExpert(RoleType roleType) {
        if (userEntity1 != null && userEntity1.getRoleTypes().contains(roleType)) {
            return userEntity1;
        }

        if (userEntity2 != null && userEntity2.getRoleTypes().contains(roleType)) {
            return userEntity2;
        }

        return null;
    }

    /**
     * 当前用户是否包含了指定账户
     *
     * @param userId
     * @return
     */
    public boolean isUserContains(String userId) {
        if (userEntity1 != null && userEntity1.getId().equals(userId)) {
            return true;
        }
        if (userEntity2 != null && userEntity2.getId().equals(userId)) {
            return true;
        }
        return false;
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

    @Override
    public boolean update(Object object) {
        //return super.update(object);
        try {
            dbManager.update(object, "type");
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
