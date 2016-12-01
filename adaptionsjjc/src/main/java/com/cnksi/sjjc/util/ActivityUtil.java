package com.cnksi.sjjc.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.cnksi.core.utils.CToast;

/**
 * Created by lyndon on 2016/9/12.
 */
public class ActivityUtil {
    /**
     * 启动变电运维智能移动作业平台
     *
     * @param activity 启动activity
     * @param account  用户名 eg：FanY0010
     * @param password 密码 eg：1
     */
    public static void openInspection(Activity activity, String account, String password) {
        try {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.cnksi.sjjc", "com.cnksi.sjjc.activity.LauncherActivity");
            intent.setComponent(componentName);
            intent.putExtra("u_account", account);
            intent.putExtra("u_password", password);
            activity.startActivity(intent);
        } catch (Exception e) {
            CToast.showShort(activity, "请安装变电运维智能移动作业平台");
        }

    }
}
