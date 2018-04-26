package com.cnksi.inspe.type;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    director("主任", 5),
    /**
     * 专责
     */
    specialty("专责", 4),
    /**
     * 专家
     */
    //专家不存在特殊要求，仅在任务创建，精益化评价有效
    expert("专家", -1),
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

    RoleType(String desc, int level) {
        this.desc = desc;
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public int getLevel() {
        return level;
    }

    public static List<RoleType> getRoles(String userType) {
        ArrayList<RoleType> list = new ArrayList<>(3);
        if (userType != null) {
            list.add(guest);
            String[] rolesStr = userType.split(",");

            for (String roleKey : rolesStr) {
                try {
                    RoleType role = RoleType.valueOf(roleKey);
                    list.add(role);
                } catch (Exception e) {
                    Log.e("RoleType", "未找到角色:" + roleKey);
                }

            }

            //排序
            Collections.sort(list, new Comparator<RoleType>() {
                @Override
                public int compare(RoleType t0, RoleType t1) {
                    if (t1.getLevel() > t0.getLevel()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
        }

        return list;
    }

    /**
     * 获取用户最大权限
     *
     * @param list
     * @return
     */
    public static RoleType getMaxRole(List<RoleType> list) {
        RoleType maxRole = guest;
        if (list != null) {
            if (list.size() > 1) {
                for (RoleType roleType : list) {
                    if (roleType.getLevel() > maxRole.level) {
                        maxRole = roleType;
                    }
                }
            }
        }
        return maxRole;
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
