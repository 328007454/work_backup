package com.cnksi.inspe.type;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:56
 */

public enum RoleType {
    /**
     * 部长
     */
    director("主任", 4),
    /**
     * 专责
     */
    specialty("专责", 3),
    /**
     * 班长
     */
    team_leader("班组长", 2),
    /**
     * 组员
     */
    tracker("维护员", 1),
    /**
     * 来宾
     */
    @Deprecated
    guest("来宾", 0),;

    /**
     * 描述
     */
    String desc;
    /**
     * 等级
     */
    int level;

    private RoleType(String desc, int level) {
        this.desc = desc;
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public int getLevel() {
        return level;
    }

    /**
     * 获取用户最大权限
     *
     * @param userType
     * @return
     */
    public static RoleType getMaxRole(String userType) {

        RoleType roleType = RoleType.guest;
        if (userType != null) {
            String[] rolesStr = userType.split(",");

            for (String roleKey : rolesStr) {
                try {
                    RoleType role = RoleType.valueOf(roleKey);
                    if (roleType.getLevel() < role.getLevel()) {
                        roleType = role;
                    }
                } catch (Exception e) {
                    Log.e("RoleType", "未找到角色:" + roleKey);
                }

            }

        }

        return roleType;
    }

    /**
     * 比较2角色权限大小
     *
     * @param roleType1
     * @param roleType2
     * @return
     */
    public static RoleType getMaxRole(RoleType roleType1, RoleType roleType2) {

        if (roleType2.getLevel() > roleType1.getLevel()) {
            return roleType2;
        }

        return roleType1;
    }

}
