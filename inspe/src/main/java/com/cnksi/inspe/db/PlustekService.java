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
     * 根据问题ID，获取标准1,2级name
     *
     * @param levle4Id
     * @return
     */
    public String[] getLeve1_2Name(String levle4Id) {
        String level4 = "SELECT pid FROM xj_jyhpj_rule WHERE id='" + levle4Id + "'";//677747476032086022
        String level3 = "SELECT pid FROM xj_jyhpj_rule WHERE id=(" + level4 + ")";
        String level2 = "SELECT * FROM xj_jyhpj_rule WHERE id=(" + level3 + ")";
        String level1 = "SELECT level1.name AS name1,level2.name AS name2 FROM xj_jyhpj_rule AS level1";
        String sql = level1 + " JOIN (" + level2 + ")AS level2 ON level1.id=level2.pid;";

        try {
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (dbModel.isEmpty("name1") || dbModel.isEmpty("name2")) {
                return null;
            } else {
                String[] names = new String[2];
                names[0] = dbModel.getString("name1").replaceAll(" ", "");
                names[1] = dbModel.getString("name2").replaceAll(" ", "");
                return names;
            }

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

    /**
     * 根据标准ID（level=4）获取大项总分
     *
     * @param level4Id
     * @return
     */
    public DbModel getStandardGroupScore(String level4Id) {
        //level 4->3->2-1
        String level4 = "SELECT pid FROM xj_jyhpj_rule WHERE id='" + level4Id + "'";
        String level3 = "SELECT pid FROM xj_jyhpj_rule WHERE id=(" + level4 + ")";
        String level2 = "SELECT pid FROM xj_jyhpj_rule WHERE id=(" + level3 + ")";
        String sql = "SELECT score,id FROM xj_jyhpj_rule WHERE id=(" + level2 + ") AND dlt='0';";
        try {
            return dbManager.findDbModelFirst(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getStandaredMaxDult(String level4Id, String deviceId) {
        //先将leve1下所有标准查询出来，在与结果中的rule_id做一对一管理
        DbModel dbModelTotal = getStandardGroupScore(level4Id);
        if (dbModelTotal == null || dbModelTotal.isEmpty("score") || dbModelTotal.isEmpty("id")) {
            return 0;
        }
        String level1Id = dbModelTotal.getString("id");//level1ID
        float totalScore = dbModelTotal.getFloat("score");//大项总分

//        String level1="";
        String level2 = "SELECT id FROM xj_jyhpj_rule WHERE pid IN('" + level1Id + "')";
        String level3 = "SELECT id FROM xj_jyhpj_rule WHERE pid IN(" + level2 + ")";
        String level4 = "SELECT level,* FROM xj_jyhpj_rule WHERE pid IN(" + level3 + ")";
        String sql = "SELECT SUM(result.deduct_score)AS total_score FROM xj_group_con_rule_result AS result JOIN (" + level4 + ")AS ruel ON ruel.id=result.rule_id WHERE result.check_type='jyhjc' AND dlt='0' AND device_id='" + deviceId + "';";
        try {
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            float deductScore = 0;
            if (dbModel == null || dbModel.isEmpty("total_score")) {
                deductScore = dbModel.getFloat("total_score");
            }
            return Math.max(0, (totalScore - deductScore));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return totalScore;
    }
}