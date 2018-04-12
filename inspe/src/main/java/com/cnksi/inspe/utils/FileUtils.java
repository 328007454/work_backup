package com.cnksi.inspe.utils;

import android.text.TextUtils;

import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.TaskType;

import java.io.File;

/**
 * 文件工具类<br/>
 * 精益化评价模块图片文件存储说明<br/>
 * 目前所有图片存放在/sdcard/BdzInspection/admin/jyhjc/[任务类型]/[变电站ID|班组ID]/[文件名].jpg
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/12 11:14
 */
public final class FileUtils {
    private FileUtils() {

    }

    //**精益化检查相对路径*/
    private static final String INPSE_PATH = "admin/jyhjc/";//隐藏文件夹

    /**
     * 创建精益化检查图片长文件名称
     *
     * @param task
     * @return
     */
    public static String createInpseImgLongName(InspecteTaskEntity task) {
        String img_prefix = "";
        if (task != null) {
            img_prefix = task.getId().replace("-", "");
        } else {
            img_prefix = "jyhjc_unknow";
        }

        return img_prefix + "_" + FunctionUtil.getCurrentImageName();
    }

    /**
     * 创建精益化检查图片长文件名称
     *
     * @param ruleResult
     * @return
     */
    public static String createInpseImgLongName(TeamRuleResultEntity ruleResult) {
        String img_prefix = "";
        if (ruleResult != null) {
            img_prefix = (TextUtils.isEmpty(ruleResult.getTask_id()) ? ruleResult.getRule_result_id() : ruleResult.getTask_id()).replace("-", "");
        } else {
            img_prefix = "jyhjc_unknow";
        }
        return img_prefix + "_" + FunctionUtil.getCurrentImageName();
    }

    /**
     * 获取精益化检查相对位置
     *
     * @param task 任务类型
     * @return
     */
    public static String getInpseImgPath(InspecteTaskEntity task) {
        //同步框架不支持文件夹递归查询
//        if (task != null) {
//            try {
//                TaskType taskType = TaskType.valueOf(task.getType());
//                switch (taskType) {
//                    case bzjs://班组建设
//                        return INPSE_PATH + taskType.name() + "/" + task.getDept_id() + "/";
//                    case jyhjc:
//                        return INPSE_PATH + taskType.name() + "/" + task.getBdz_id() + "/";
//                    default:
//                        return INPSE_PATH + taskType.name() + "/" + task.getId() + "/";
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }

        return INPSE_PATH;
    }

    /**
     * 根据问题获取图片相对位置
     *
     * @param ruleResult
     * @return
     */
    public static String getInpseImgPath(TeamRuleResultEntity ruleResult) {
        //同步框架不支持文件夹递归查询
//        if (ruleResult != null) {
//            try {
//                TaskType taskType = TaskType.valueOf(ruleResult.getCheck_type());
//                switch (taskType) {
//                    case bzjs://班组建设
//                        return INPSE_PATH + taskType.name() + "/" + ruleResult.getDept_id() + "/";
//                    case jyhjc:
//                        return INPSE_PATH + taskType.name() + "/" + ruleResult.getBdz_id() + "/";
//                    default:
//                        return INPSE_PATH + taskType.name() + "/" + (TextUtils.isEmpty(ruleResult.getTask_id()) ? ruleResult.getRule_result_id() : ruleResult.getTask_id()) + "/";//分享任务，TaskID变更到Rule_result_id
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }

        return INPSE_PATH;
    }

    /**
     * 获取根目录
     *
     * @return
     */
    public static String getInpseRootPath() {
        //../BdzInspection/
        return Config.RESULT_PICTURES_FOLDER;
    }
}
