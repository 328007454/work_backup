package com.cnksi.inspe.type;

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
    director,
    /**
     * 专责
     */
    specialty,
    /**
     * 班长
     */
    team_leader,
    /**
     * 组员
     */
    tracker,
    /**
     * 来宾
     */
    @Deprecated
    guest,;

    /**
     * 获取用户最大权限
     *
     * @param userType
     * @return
     */
    public static RoleType getMaxRole(String userType) {
        if (userType != null) {
            String[] rolesStr = userType.split(",");
            List<RoleType> roles = new ArrayList<RoleType>();
            for (String role : rolesStr) {
                roles.add(RoleType.valueOf(role));
            }

            if (roles.remove(RoleType.director)) {

                return director;//主任
            } else if (roles.remove(RoleType.specialty)) {

                return specialty;//专责
            } else if (roles.remove(team_leader)) {

                return team_leader;//组长
            } else if (roles.remove(tracker)) {

                return tracker;//维护人员
            } else {

                return guest;//来宾
            }

        }

        return guest;
    }

    /**
     * 比较2角色权限大小
     *
     * @param roleType1
     * @param roleType2
     * @return
     */
    public static RoleType getMaxRole(RoleType roleType1, RoleType roleType2) {

        Map<RoleType, Integer> roles = new HashMap<>();
        roles.put(director, 4);
        roles.put(specialty, 3);
        roles.put(team_leader, 2);
        roles.put(tracker, 1);
        roles.put(guest, 0);

        if (roles.get(roleType2) > roles.get(roleType1)) {
            return roleType2;
        }

        return roleType1;
    }

}
