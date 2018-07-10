package com.cnksi.common.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;

/**
 * Created by Mr.K on 2018/7/10.
 */

public class RouterActivityUtils {

    public static void startCopyModel(Task task, String reportid, String folder) {
        setPreferenceData(task, reportid, folder);
        if (task.status.equals(TaskStatus.undo.name())) {
            switch (InspectionType.get(task.inspection)) {
                //红外测温
                case SBJC_13:
                    ARouter.getInstance().build("/hwcw/activity").navigation();
                    break;
                //保护屏红外成像
                case SBJC_02:
                    break;
                //室内温湿度记录
                case SBJC_03:
                    ARouter.getInstance().build("/indoorHumitrue/activity").navigation();
                    break;
                //差动保护
                case SBJC_04:
                    ARouter.getInstance().build("\"/different/activity\"").navigation();
                    break;
                //交直流分接开关
                case SBJC_05:
                    ARouter.getInstance().build("\"/transformRecord/activity\"").navigation();
                    break;
                case SBJC_06_gas:
                case SBJC_06_sf6:
                case SBJC_06_water:
                    ARouter.getInstance().build("/copyBaseData/activity").navigation();
                    break;
                case SBJC_07:
                    ARouter.getInstance().build("/copyAllValue/activity").navigation();
                    break;
                case SBJC_08:
                    ARouter.getInstance().build("/copyBaseData/activity").navigation();
                    break;
                //防小动物措施检测
                case SBJC_09:
                    ARouter.getInstance().build("/preventAnimal/activity").navigation();
                    break;
                //蓄电池检测
                case SBJC_10:
                    ARouter.getInstance().build("/batteryTest/activity").navigation();
                    break;
                //蓄电池内阻检测
                case SBJC_11:
                    ARouter.getInstance().build("/batteryTest/activity").navigation();
                    break;
                //收发信机测试
                case SBJC_12:
                    ARouter.getInstance().build("/getSendLetter/activity").navigation();
                    break;
                case SBJC_KGGZTZJL:
                    ARouter.getInstance().build("/tzqk/activity").navigation();
                    break;
                default:
                    ToastUtils.showMessageLong("异常的数据检测类型");
                    return;
            }
        } else {
            switch (InspectionType.get(task.inspection)) {
                //设备测温
                case SBJC_13:
                    ARouter.getInstance().build("/hwcwReport/activity").navigation();
                    break;
                //保护屏红外成像
                case SBJC_02:
                    break;
                //室内温湿度记录
                case SBJC_03:
                    ARouter.getInstance().build("/indoorHumitureReport/activity").navigation();
                    break;
                //差动保护
                case SBJC_04:
                    ARouter.getInstance().build("/jzlfjkgReport/activity").navigation();
                    break;
                //交直流分接开关
                case SBJC_05:
                    ARouter.getInstance().build("/jzlfjkgReport/activity").navigation();
                    break;
                //防小动物措施检查
                case SBJC_09:
                    ARouter.getInstance().build("/animalReport/activity").navigation();
                    break;
                //蓄电池检测
                case SBJC_10:
                    ARouter.getInstance().build("/batteryTestReport/Activity").navigation();
                    break;
                //蓄电池内阻检测
                case SBJC_11:
                    ARouter.getInstance().build("/batteryTestReport/Activity").navigation();
                    break;
                case SBJC_12:
                    ARouter.getInstance().build("/getSendLetterReport/activity").navigation();
                    //TODO 跳转收发信机报告
                    break;
                case SBJC_06_gas:
                case SBJC_06_sf6:
                case SBJC_06_water:
                case SBJC_07:
                case SBJC_08:
                    ARouter.getInstance().build("/copyValueReport/activity").navigation();
                    break;
                case SBJC_KGGZTZJL:
                    ARouter.getInstance().build("/gztzReport/activity").navigation();
                    break;
                default:
                    throw new RuntimeException("异常的数据检测类型");
            }
        }

    }

    private static void setPreferenceData(Task task, String reportid, String folder) {
        PreferencesUtils.put(Config.CURRENT_TASK_ID, task.taskid);
        PreferencesUtils.put(Config.CURRENT_REPORT_ID, reportid);
        PreferencesUtils.put(Config.PICTURE_PREFIX_KEY, folder + "/");
        PreferencesUtils.put(Config.CURRENT_BDZ_ID, task.bdzid);
        PreferencesUtils.put(Config.CURRENT_BDZ_NAME, task.bdzname);
        PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE, task.inspection);
        PreferencesUtils.put(Config.CURRENT_INSPECTION_TYPE_NAME, task.inspection_name);
    }

    public static void startSwitchOverModel(Task task, String reportId, String folder) {
        setPreferenceData(task, reportId, folder);
        if (task.status.equals(TaskStatus.undo.name())) {
            if (task.inspection.contains(InspectionType.switchover.name()) || task.inspection.contains(InspectionType.maintenance.name()) || task.inspection.equalsIgnoreCase(InspectionType.battery.name())) {
                if (SystemConfig.NONE.equalsIgnoreCase(SystemConfig.getSwitchMenuConfirmStyle())) {
                    ARouter.getInstance().build("/regularSwitch/activity");
                } else {
                    ARouter.getInstance().build("/regularSwitchStrict/activity");
                }
            }
        }

    }
}
