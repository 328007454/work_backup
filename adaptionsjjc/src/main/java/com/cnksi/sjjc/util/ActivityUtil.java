package com.cnksi.sjjc.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.bdzinspection.activity.maintenance.SafetyBdzListActivity;
import com.cnksi.bdzinspection.activity.maintenance.SafetyToolsRemindActivity;
import com.cnksi.bdzinspection.czp.OperateTaskListActivity;
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
import com.cnksi.workticket.activity.TicketDateWorkActivity;

/**
 * Created by lyndon on 2016/9/12.
 */
public class ActivityUtil {

    public static void startDeviceTourActivity(Activity activity, int id) {
        Intent intent = new Intent(activity, NewLauncherActivity.class);
        intent.putExtra("position", id == R.id.device_xunshi ? 0 : 1);
        activity.startActivity(intent);
    }

    public static void startOperateActivity(Activity activity) {
//        ComponentName componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.czp.OperateTaskListActivity");
        Intent intent2 = new Intent();
        intent2.setClass(activity, OperateTaskListActivity.class);
//        intent2.setComponent(componentName);
        intent2.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get( Config.CURRENT_LOGIN_USER, ""));
        intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get( Config.CURRENT_LOGIN_ACCOUNT, ""));
        activity.startActivity(intent2);
    }

    public static void startUnifyActivity(Activity activity) {
        Intent intent1 = new Intent();
//        ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindFragment");
        intent1.setClass(activity, TaskRemindActivity.class);
        String typeName = InspectionType.operation.name();
        intent1.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, typeName);
        intent1.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get( Config.CURRENT_LOGIN_USER, ""));
        intent1.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent1.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get( Config.CURRENT_DEPARTMENT_ID, ""));
//        intent1.setComponent(componentName1);
        activity.startActivity(intent1);
    }

    public static void startSafetyToolActivity(Activity activity) {
        Intent intent = new Intent();
//        ComponentName componentName1 = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.maintenance.SafetyBdzListActivity");
        intent.setClass(activity, SafetyBdzListActivity.class);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get( Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
//        intent.setComponent(componentName1);
        activity.startActivity(intent);
    }

    public static void startSafetyRemindActivity(Activity activity) {
        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.maintenance.SafetyToolsRemindActivity"));
        intent.setClass(activity, SafetyToolsRemindActivity.class);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get( Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get( Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get( Config.CURRENT_DEPARTMENT_ID, ""));
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
        if (PreferencesUtils.get("SYNC_WAY",BuildConfig.USE_NETWORK_SYNC)) {
            KSyncConfig.getInstance().startNetWorkSync(activity);
        } else {
            KSyncConfig.getInstance().startUsbWorkSync(activity);
        }

    }

    public static void startDefectControlActivity(BaseActivity activity) {
        Intent intent = new Intent(activity, DefectControlActivity.class);
        activity.startActivity(intent);
    }

    public static void startTicketDateModel(Activity activity) {
        Intent intent = new Intent(activity, TicketDateWorkActivity.class);
        activity.startActivity(intent);
    }
}
