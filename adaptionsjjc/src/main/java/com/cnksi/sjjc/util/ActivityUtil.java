package com.cnksi.sjjc.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.activity.DefectControlActivity;
import com.cnksi.sjjc.activity.NewLauncherActivity;
import com.cnksi.sjjc.activity.TypeListActivity;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.sync.KSyncConfig;

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

    public static void startDeviceTourActivity(Activity activity, int id) {
        Intent intent = new Intent(activity, NewLauncherActivity.class);
        intent.putExtra("position", id == R.id.device_xunshi ? 0 : 1);
        activity.startActivity(intent);
    }

    public static void startOperateActivity(Activity activity) {
        ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.OperateTaskListActivity");
        Intent intent2 = new Intent();
        intent2.setComponent(componentName);
        intent2.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_USER, ""));
        intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        activity.startActivity(intent2);
    }

    public static void startUnifyActivity(Activity activity) {
        Intent intent1 = new Intent();
        ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
        String typeName = InspectionType.operation.name();
        intent1.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName);
        intent1.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_USER, ""));
        intent1.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent1.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(activity, Config.CURRENT_DEPARTMENT_ID, ""));
        intent1.setComponent(componentName1);
        activity.startActivity(intent1);
    }

    public static void startSafetyToolActivity(Activity activity) {
        Intent intent = new Intent();
        ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.maintenance.SafetyBdzListActivity");
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(activity, Config.CURRENT_DEPARTMENT_ID, ""));
        intent.setComponent(componentName1);
        activity.startActivity(intent);
    }

    public static void startSafetyRemindActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.maintenance.SafetyToolsRemindActivity"));
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(activity, Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(activity, Config.CURRENT_DEPARTMENT_ID, ""));
        activity.startActivity(intent);
    }


    public static void startShuJuJianCe(Activity activity) {
        Intent intent = new Intent(activity, TypeListActivity.class);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC.name());
        activity.startActivity(intent);
    }

    public static void startWTYCActiviy(Activity activity) {
        Intent intent = new Intent(activity, TypeListActivity.class);
        intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.TJWT.name());
        activity.startActivity(intent);
    }


    public static void startSync(Activity activity) {
        if (PreferencesUtils.getBoolean(activity,"SYNC_WAY",BuildConfig.USE_NETWORK_SYNC)) {
            KSyncConfig.getInstance().startNetWorkSync(activity);
        } else {
            KSyncConfig.getInstance().startUsbWorkSync(activity);
        }

    }

    public static void startDefectControlActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, DefectControlActivity.class);
        activity.startActivity(intent);
    }
}
