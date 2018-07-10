package com.cnksi.login.util;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.bdzinspection.activity.maintenance.SafetyBdzListActivity;
import com.cnksi.bdzinspection.activity.maintenance.SafetyToolsRemindActivity;
import com.cnksi.bdzinspection.czp.OperateTaskListActivity;
import com.cnksi.common.BuildConfig;
import com.cnksi.common.Config;
import com.cnksi.common.KSyncConfig;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.defect.activity.DefectControlActivity;
import com.cnksi.login.R;
import com.cnksi.sjjc.activity.TypeListActivity;
import com.cnksi.workticket.activity.TicketDateHomeActivity;

/**
 * Created by lyndon on 2016/9/12.
 */
public class ActivityUtil {

    public static void startDeviceTourActivity(Activity activity, int id) {
        ARouter.getInstance().build("/login/NewLauncherActivity")
                .withInt("position", id == R.id.device_xunshi ? 0 : 1)
                .navigation();
    }

    public static void startOperateActivity(Activity activity) {
        Intent intent2 = new Intent();
        intent2.setClass(activity, OperateTaskListActivity.class);
        intent2.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        activity.startActivity(intent2);
    }

    public static void startUnifyActivity(Activity activity) {
        Intent intent1 = new Intent();
        intent1.setClass(activity, TaskRemindActivity.class);
        String typeName = InspectionType.operation.name();
        intent1.putExtra(Config.CURRENT_INSPECTION_TYPE, typeName);
        intent1.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent1.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent1.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        activity.startActivity(intent1);
    }

    public static void startSafetyToolActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SafetyBdzListActivity.class);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        activity.startActivity(intent);
    }

    public static void startSafetyRemindActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SafetyToolsRemindActivity.class);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
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
        if (PreferencesUtils.get("SYNC_WAY", BuildConfig.USE_NETWORK_SYNC)) {
            KSyncConfig.getInstance().startNetWorkSync(activity);
        } else {
            KSyncConfig.getInstance().startUsbWorkSync(activity);
        }
    }

    public static void startDefectControlActivity(BaseCoreActivity activity) {
        Intent intent = new Intent(activity, DefectControlActivity.class);
        activity.startActivity(intent);
    }

    public static void startTicketDateModel(Activity activity) {
        Intent intent = new Intent(activity, TicketDateHomeActivity.class);
        intent.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
        intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
        intent.putExtra(Config.LAST_CHOOSE_BDZ_NAME_KEY, PreferencesUtils.get(Config.LAST_CHOOSE_BDZ_NAME_KEY, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_ID, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        intent.putExtra(Config.CURRENT_DEPARTMENT_NAME, PreferencesUtils.get(Config.CURRENT_DEPARTMENT_NAME, ""));
        intent.putExtra(Config.OTHER_DEPT_USER, PreferencesUtils.get(Config.OTHER_DEPT_USER, ""));
        intent.putExtra(Config.KEY_SYNC_URL, Config.SYNC_URL);
        intent.putExtra(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
        activity.startActivity(intent);
    }

    public static void startTourActivity(BaseCoreActivity mActivity,String type) {
        ARouter.getInstance().build("/xian/tourActivity")
                .withString(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""))
                .withString(Config.INSPECTION_TYPE,type)
                .withString(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "")).navigation();

//        Intent intent2 = new Intent();
//        intent2.setClass(mActivity, TourInspectionHomeActivity.class);
//        intent2.putExtra(Config.CURRENT_LOGIN_USER, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
//        intent2.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, ""));
//        mActivity.startActivity(intent2);
    }
}
