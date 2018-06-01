package com.cnksi.sjjc.inter;

/**
 * 权限赋予监听
 *
 * @version 1.0
 * @author lyndon
 * @date 2016/11/10 18:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

public interface GrantPermissionListener {
    /**
     * 所有权限都赋予后操作
     */
    void allPermissionsGranted();
}
