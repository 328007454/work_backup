package com.cnksi.inspe.db;

import android.os.DropBoxManager;
import android.text.TextUtils;
import android.util.Log;

import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DbLogEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.entity.InspectePlustekEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.ui.InspePlustekActivity;
import com.cnksi.inspe.utils.DBUtils;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:23
 */

public class TeamService extends BaseDbService {

    /**
     * 获取Levenl=1数据
     *
     * @return
     */
    public List<TeamRuleEntity> getRoleList() {
        String sql = "SELECT * FROM xj_group_con_rule WHERE level='1' AND dlt='0';";
        try {
            return parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), TeamRuleEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取建设项目(获取Level=2数据)
     *
     * @param levle1Id {@link #getRoleList()}查询出的对象id
     * @return
     */
    public List<TeamRuleEntity> getRoleList(String levle1Id, String taskId) {
        String sql = "SELECT * FROM xj_group_con_rule WHERE pid='" + levle1Id + "' AND dlt='0';";
        try {
            List<TeamRuleEntity> list = parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), TeamRuleEntity.class);

            for (TeamRuleEntity trn : list) {
                //获取总分
                trn.setScoreCount(getRoleScore(trn.getId()));
                //获取已扣分
                getRoleRecord(trn, taskId);
            }

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取资料规范(在查看标准页面使用)
     *
     * @param level2Id {@link #getRoleList(String, String)}查询出的对象id
     * @return
     */
    public List<TeamRuleEntity> getRoleDoc(String level2Id) {
        String sql = "SELECT * FROM xj_group_con_rule WHERE pid='" + level2Id + "' AND dlt='0';";
        try {
            List<TeamRuleEntity> list = parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), TeamRuleEntity.class);

            //统计总分
//            for (TeamRuleEntity entity : list) {
//                entity.setScoreCount(getRoleDocScore(entity.getId()));
//            }

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取规范标准文件名
     *
     * @param level2Id
     * @return
     */
    public String getRoleDocName(String level2Id) {
        String sql_leve3 = "SELECT id FROM xj_group_con_rule WHERE pid='" + level2Id + "' AND dlt='0'";
        String sql = "SELECT dependence FROM xj_group_con_rule WHERE pid in(" + sql_leve3 + ") AND dependence IS NOT '' AND dlt='0'";
        try {
            //注意，由于表原因，一个level2可能对应多个标准文档，在此仅显示了第一个标准文档
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (dbModel != null && !dbModel.isEmpty("dependence")) {
                return dbModel.getString("dependence");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }


        return null;
    }

    public String getRoleDoc1(String level4Id) {
        String sql_level4 = "SELECT pid FROM xj_group_con_rule WHERE id='" + level4Id + "' AND dlt='0'";
        String sql = "SELECT * FROM xj_group_con_rule WHERE id=(" + sql_level4 + ") AND dlt='0';";
        try {
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (dbModel != null && !dbModel.isEmpty("name")) {
                return dbModel.getString("name");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param level4Id
     * @return
     */
    public String getRoleDocName4(String level4Id) {
        try {
            //根据ID查询到具体标准（level=4）
            TeamRuleEntity entity4 = dbManager.selector(TeamRuleEntity.class)
                    .where("id", "=", level4Id)
                    .and("dlt", "=", "0")
                    .findFirst();
            if (entity4 == null) {
                return null;
            }

            //如果有标准文件名，则返回
            if (!TextUtils.isEmpty(entity4.getDependence())) {
                return entity4.getDependence();
            }

            //根据pid查询所有兄弟标准，看是否存在标准名(level =3)
            TeamRuleEntity entity3 = dbManager.selector(TeamRuleEntity.class)
                    .where("pid", "=", entity4.getPid())
                    .and("dlt", "=", "0")
                    .and("dependence", "IS NOT", "''")
                    .findFirst();
            if (entity3 != null && !TextUtils.isEmpty(entity3.getDependence())) {
                return entity3.getDependence();
            }

            //根据次根节点查询文件名(level=2)
            TeamRuleEntity entity2 = dbManager.selector(TeamRuleEntity.class)
                    .where("id", "=", entity4.getPid())
                    .and("dlt", "=", "0")
                    .findFirst();
            if (entity4 == null) {
                return null;
            }
            String level2Id = entity2.getPid();
            String sql_level3Id = "SELECT id FROM xj_group_con_rule WHERE pid ='" + level2Id + "' AND dlt='0'";
            String sql = "SELECT * FROM xj_group_con_rule WHERE pid IN(" + sql_level3Id + ") AND dependence !='' AND dlt='0';";
            DbModel dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (dbModel != null && !dbModel.isEmpty("dependence")) {
                return dbModel.getString("dependence");
            }

            return null;

        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取资料规范(在检查标准页面使用)
     *
     * @param levle2Id {{@link #getRoleDoc(String)}}集合中的id
     * @return
     */
    public List<TeamRuleEntity> getRoleStandard(String levle2Id, String taskId) {
        //为了方便阅读，故拆分多条sql
        //根据首页ID，查询文档ID
        String sql_doc = "SELECT id FROM xj_group_con_rule WHERE pid ='" + levle2Id + "' AND dlt='0'";
        //中间表
        String sql_result_tab = "(SELECT * FROM xj_group_con_rule_result WHERE task_id='" + taskId + "' AND xj_group_con_rule_result.dlt='0')AS result_tab";
        //查询
        String sql = "SELECT xj_group_con_rule.*,result_tab.record_type,result_tab.deduct_score FROM xj_group_con_rule LEFT JOIN " + sql_result_tab + " ON xj_group_con_rule.id=result_tab.rule_id WHERE xj_group_con_rule.pid IN(" + sql_doc + ") AND xj_group_con_rule.dlt='0'";

        try {
            List<TeamRuleEntity> list = parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), TeamRuleEntity.class);

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取资料规范(模块)总分
     *
     * @return
     */
    public TeamRuleEntity getRoleScore(TeamRuleEntity entityLevel4, String taskId) {
        //为了方便阅读，故拆分多条sql
        //查询出GROUP总分
        String sql_total = "SELECT score FROM xj_group_con_rule_score_group WHERE id='" + entityLevel4.getScore_group_id() + "' AND dlt='0'";

        //查询已被扣了多少分
        String sql_groupid = "SELECT id FROM xj_group_con_rule WHERE score_group_id = '" + entityLevel4.getScore_group_id() + "' AND dlt='0'";
        String sql_deduction = "SELECT SUM(deduct_score)AS score_dede FROM xj_group_con_rule_result WHERE rule_id IN (" + sql_groupid + ") AND task_id='" + taskId + "' AND dlt='0'";

        try {
            //总分
            DbModel moble1 = dbManager.findDbModelFirst(new SqlInfo(sql_total));
            if (moble1 != null && !moble1.isEmpty("score")) {
                entityLevel4.setScoreCount(moble1.getFloat("score"));
            }

            //被扣分
            DbModel moble2 = dbManager.findDbModelFirst(new SqlInfo(sql_deduction));
            if (moble2 != null && !moble2.isEmpty("score_dede")) {
                entityLevel4.setDeduct_score(moble2.getFloat("score_dede"));
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        return entityLevel4;
    }

    /**
     * 获取资料规范(模块)总分
     *
     * @return
     */
    private void getRoleRecord(TeamRuleEntity entity, String taskId) {

        //为了方便阅读，故拆分多条sql
        //根据首页ID，查询文档ID
        String sql_doc = "SELECT id FROM xj_group_con_rule WHERE pid ='" + entity.getId() + "' AND dlt='0'";
        String sql_result_tab = "(SELECT * FROM xj_group_con_rule_result WHERE task_id='" + taskId + "' AND xj_group_con_rule_result.dlt='0')AS xj_group_con_rule_result ";
        //统计查询结果
        String sql = "SELECT COUNT(xj_group_con_rule.id)AS total_list,COUNT(record_type)AS total_record,SUM(deduct_score)AS sum_score FROM xj_group_con_rule LEFT JOIN " + sql_result_tab + " ON xj_group_con_rule.id=xj_group_con_rule_result.rule_id WHERE pid IN(" + sql_doc + ") AND xj_group_con_rule.dlt='0';";

        try {
            DbModel moble = dbManager.findDbModelFirst(new SqlInfo(sql));
            getRoleRecord(entity, moble);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解析统计
     *
     * @param entity
     * @param moble
     */
    private void getRoleRecord(TeamRuleEntity entity, DbModel moble) {
        if (moble != null) {
            int total_list = moble.getInt("total_list");//总标准数
            int total_record = moble.getInt("total_record");//已检查标准数

            float sum_score = 0;//扣分数
            if (!moble.isEmpty("sum_score")) {
                sum_score = moble.getFloat("sum_score");//扣分数
            }

            //防止经度
            int sum_dept_score = (int) (sum_score * 10);
            //int sumt_score_int = (int) entity.getScoreCount() * 10;

            if (total_list != 0) {//标准数有效
                if (total_record == 0) {//未开始
                    entity.setRecord_type(null);
                } else if (total_record == total_list) {//已完成
                    //是否被扣分
                    if (sum_dept_score == 0) {
                        entity.setRecord_type(RecordType.normal.name());
                    } else {
                        entity.setRecord_type(RecordType.answer.name());
                    }
                } else {
                    entity.setRecord_type(RecordType.ing.name());//进行中
                }

                entity.setDeduct_score(sum_dept_score / 10f);

            } else {
                entity.setRecord_type(RecordType.normal.name());//已完成
            }
        }
    }

    /**
     * 保存班组建设记录
     *
     * @return
     */
    public boolean saveRuleResult(TeamRuleResultEntity data) {
        try {
            data.setLast_modify_time(DateFormat.dateToDbString(System.currentTimeMillis()));
//            data.setUpdate_time(DateFormat.dateToDbString(System.currentTimeMillis()));
            dbManager.saveOrUpdate(data);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存班组建设记录
     *
     * @return
     */
    public TeamRuleResultEntity getRuleResult(String id) {
        try {
            String sql = "SELECT * FROM xj_group_con_rule_result WHERE id='" + id + "' AND dlt='0';";
            TeamRuleResultEntity entity = parseObject(dbManager.findDbModelFirst(new SqlInfo(sql)), TeamRuleResultEntity.class);
            return entity;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 保存班组建设记录
     *
     * @return
     */
    public TeamRuleResultEntity getRuleResult(String ruleId, String taskId) {
        try {
            String sql = "SELECT * FROM xj_group_con_rule_result WHERE rule_id='" + ruleId + "' AND task_id='" + taskId + "' AND dlt='0';";
            TeamRuleResultEntity entity = parseObject(dbManager.findDbModelFirst(new SqlInfo(sql)), TeamRuleResultEntity.class);
            return entity;
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取问题记录
     *
     * @return
     */
    public List<TeamRuleResultEntity> getIssueList() {
        List<TeamRuleResultEntity> list = null;
        try {
            list = dbManager.selector(TeamRuleResultEntity.class)
                    .where("record_type", "=", RecordType.answer.name())
                    .and("dlt", "=", "0")
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据用户ID和状态查看问题
     *
     * @param userIds  用户IDS
     * @param progress 问题状态
     * @return
     */
    public List<TeamRuleResultEntity> getIssueListForUserIds(String userIds[], String taskType, String bdzId, String... progress) {

        List<TeamRuleResultEntity> list = null;
        try {
            Selector selector = dbManager.selector(TeamRuleResultEntity.class).
                    where("record_type", "=", RecordType.answer.name());

            if (userIds.length > 0) {
                selector.and("improve_person_id", "IN", userIds);
            }
            if (taskType != null) {
                selector.and("check_type", "=", taskType);
            }
            if (bdzId != null) {
                selector.and("bdz_name", "=", taskType);
            }
            if (progress.length > 0) {
                selector.and("progress", "IN", progress);
            }
            selector.and("dlt", "=", "0");

            list = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 判断该问题是否已经分享
     *
     * @param taskId
     * @param ruleId
     * @return
     */
    public boolean isShared(String depId, String taskId, String ruleId) {
        DbModel entity = null;
        try {
            String sql = "SELECT * FROM xj_group_con_rule_result WHERE record_type='" + RecordType.answer.name() + "' AND dept_id='" + depId + "' AND rule_id='" + ruleId + "' AND dlt='0' AND(rule_result_id='" + taskId + "' OR task_id='" + taskId + "')";
            entity = dbManager.findDbModelFirst(new SqlInfo(sql));
//            entity = dbManager.selector(TeamRuleResultEntity.class)
//                    .where("record_type", "=", RecordType.answer.name())
//                    .and("dept_id", "=", depId)
//                    .and("rule_result_id", "=", taskId)
//                    .and("rule_id", "=", ruleId)
//                    .and("dlt", "=", "0")
//                    .or("task_id", "=", taskId)
//                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return entity == null ? false : true;
    }

    /**
     * 班组长，我处理的问题
     *
     * @param groupIds
     * @param taskType
     * @param bdzId
     * @param userIds
     * @param progress
     * @return
     */
    @Deprecated
    public List<TeamRuleResultEntity> getIssueListForTeamLeader(String groupIds, String taskType, String bdzId, String[] userIds, String... progress) {

        String sql = "SELECT * FROM xj_group_con_rule_result WHERE record_type = 'answer' AND dept_id = '" + groupIds + "' OR (`improve_person_id` IN (" + StringUtils.getArrayToDbIn(progress) + ") AND `progress` = 'wzg') AND `dlt` = '0'";
        try {
            List<DbModel> listTemp = dbManager.findDbModelAll(new SqlInfo(sql));
            return parseObjectList(listTemp, TeamRuleResultEntity.class);

        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据组和状态查询问题
     *
     * @param groupIds
     * @param progress
     * @return
     */
    public List<TeamRuleResultEntity> getIssueListForGroupIds(String[] groupIds, String taskType, String bdzId, String... progress) {
        List<TeamRuleResultEntity> list = null;

        try {
            Selector selector = dbManager.selector(TeamRuleResultEntity.class)
                    .where("record_type", "=", RecordType.answer.name());
            if (groupIds != null) {
                selector.and("dept_id", "IN", groupIds);
            }
            if (taskType != null) {
                selector.and("check_type", "=", taskType);
            }
            if (bdzId != null) {
                selector.and("bdz_name", "=", bdzId);
            }
            if (progress.length > 0) {
                selector.and("progress", "IN", progress);
            }
            selector.and("dlt", "=", "0");
            list = selector.findAll();

        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 保存数据库记录日志
     *
     * @param data
     * @return
     */
    public boolean saveLog(DbLogEntity data) {

        try {
            dbManager.save(data);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 清除数据
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T> boolean clear(Class<T> cls) {
        try {
            dbManager.delete(cls);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * DB Array对象解析
     *
     * @param modles
     * @param cls
     * @param <T>
     * @return
     */
    private <T> List<T> parseObjectList(List<DbModel> modles, Class<T> cls) {
        List<T> list = new ArrayList<>();
        if (modles != null) {
            for (DbModel model : modles) {
                list.add(parseObject(model, cls));
            }
        }

        return list;
    }

    /**
     * DB Object解析
     *
     * @param model
     * @param cls
     * @param <T>
     * @return
     */
    private <T> T parseObject(DbModel model, Class<T> cls) {
        if (model == null) {
            return null;
        }

        Field[] fields = cls.getDeclaredFields();
        try {
            T entity = cls.newInstance();

            //以空间换时间
            int length = fields.length;
            //属性
            Field field;
            //属性
            String name;
            //类型
            String type;
            for (int i = 0; i < length; i++) {
                field = fields[i];
                //属性
                name = field.getName();
                //类型
                type = field.getType().getName();

                field.setAccessible(true);

                if (model.isEmpty(name)) {
                    continue;
                }

                if (type.contains("String")) {
                    field.set(entity, model.getString(name));
                } else if (type.contains("int") || type.contains("Integer")) {
                    field.set(entity, model.getInt(name));
                } else if (type.contains("short") || type.contains("Short")) {
                    field.set(entity, model.getInt(name));
                } else if (type.contains("float") || type.contains("Float")) {
                    field.set(entity, model.getFloat(name));
                } else if (type.contains("Double") || type.contains("Double")) {
                    field.set(entity, model.getDouble(name));
                } else if (type.contains("boolean") || type.contains("Boolean")) {
                    field.set(entity, model.getBoolean(name));
                } else {
                    Log.w("TeamService", "未定义");
                }


            }

            return entity;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取资料规范(模块)总分
     *
     * @return
     */
    private float getRoleScore(String pid) {

        //为了方便阅读，故拆分多条sql
        //根据首页ID，查询文档ID
        String sql_doc = "SELECT id FROM xj_group_con_rule WHERE pid ='" + pid + "'";
        //查询当前项的Score分组IDS
        String sql_score_ids = "SELECT score_group_id FROM xj_group_con_rule WHERE pid IN(" + sql_doc + ") GROUP BY score_group_id";
        //查询出当前检查项总分
        String sql = "SELECT SUM(score) AS score FROM xj_group_con_rule_score_group WHERE id IN(" + sql_score_ids + ");";


        try {
            DbModel moble = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (moble != null) {
                return moble.getFloat("score");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
