package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.type.TaskType;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 精益化评价服务
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 20:27
 */
public class PlustekService extends BaseDbService {

    /**
     * 获取精益化评价规则
     *
     * @param bigid       设备大类
     * @param pid         null获取根目录(标准类型)
     * @param plustekType 检查类型
     * @return
     */
    public List<PlusteRuleEntity> getPlusteRule(String bigid, PlustekType plustekType, String... pid) {
        try {
            Selector selector = dbManager.selector(PlusteRuleEntity.class)
                    .where("dlt", "=", "0");
            if (bigid != null) {
                selector.and("bigid", "=", bigid);
            }
            if (plustekType != null) {
                selector.and("check_way", "LIKE", "%" + plustekType.name() + "%");
            }
            if (pid != null && pid.length > 0) {
                selector.and("pid", "IN", pid);
            } else {
                selector.and("level", "=", "1");
            }

            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据标准ID查询标准
     *
     * @param id
     * @return
     */
    public PlusteRuleEntity getIssue(String id) {
        try {
            return dbManager.selector(PlusteRuleEntity.class)
                    .where("dlt", "=", "0")
                    .and("id", "=", id)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取精益化问题
     *
     * @param taskId   任务ID
     * @param deviceId 设备ID,如果不存在则查询该任务所有问题
     * @return
     */
    public List<TeamRuleResultEntity> getIssues(String taskId, String deviceId) {
        try {
            Selector selector = dbManager.selector(TeamRuleResultEntity.class)
                    .where("dlt", "=", "0")
                    .and("check_type", "=", TaskType.jyhjc.name());
            if (taskId != null) {
                selector.and("task_id", "=", taskId);
            }
            if (deviceId != null) {
                selector.and("device_id", "=", deviceId);
            }
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 错误统计
     * <p/>统计该任务下，变电站错误数
     *
     * @param taskId
     * @param deviceId
     * @return
     */
    public int getIssueTotal(String taskId, String deviceId) {
        String sql = "SELECT count(*)AS issue_total FROM xj_group_con_rule_result WHERE dlt='0' AND task_id='" + taskId + "' AND check_type='" + TaskType.jyhjc.name() + "' AND device_id='" + deviceId + "';";
        try {
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (dbModel != null && !dbModel.isEmpty("issue_total")) {
                return dbModel.getInt("issue_total");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
