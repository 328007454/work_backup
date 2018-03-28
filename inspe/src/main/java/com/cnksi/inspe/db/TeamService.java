package com.cnksi.inspe.db;

import android.util.Log;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DbLogEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.RecordType;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/22 11:23
 */

public class TeamService extends BaseDbService {

    /**
     * 获取建设项目(班组建设检查页面目录项使用)
     *
     * @return
     */
    public List<TeamRuleEntity> getRoleList() {
        try {
            List<TeamRuleEntity> list = dbManager.selector(TeamRuleEntity.class).where("level", "=", "1").findAll();

            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取建设项目(班组建设检查页面小项使用)
     *
     * @param id {@link #getRoleList()}查询出的对象id
     * @return
     */
    public List<TeamRuleEntity> getRoleList(String id) {
        try {
            List<TeamRuleEntity> list = dbManager.selector(TeamRuleEntity.class).where("pid", "=", id).findAll();

            for (TeamRuleEntity trn : list) {
                trn.setScoreCount(getRoleScore(trn.getId()));
                getRoleRecord(trn);
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
     * @param id {@link #getRoleList(String)}查询出的对象id
     * @return
     */
    public List<TeamRuleEntity> getRoleDoc(String id) {
        try {
            List<TeamRuleEntity> list = dbManager.selector(TeamRuleEntity.class).where("pid", "=", id).findAll();


            for (TeamRuleEntity entity : list) {
                entity.setScoreCount(getRoleDocScore(entity.getId()));
            }


            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取资料规范(在检查标准页面使用)
     *
     * @param ids {{@link #getRoleDoc(String)}}集合中的id
     * @return
     */
    public List<TeamRuleEntity> getRoleStandard(String ids) {
        List<TeamRuleEntity> list = new ArrayList<>();
        //为了方便阅读，故拆分多条sql
        //根据首页ID，查询文档ID
        String sql_doc = "SELECT id FROM xj_group_con_rule WHERE pid ='" + ids + "'";
        //查询结果
        String sql = "SELECT xj_group_con_rule.*,xj_group_con_rule_result.record_type,xj_group_con_rule_result.deduct_score FROM xj_group_con_rule LEFT JOIN xj_group_con_rule_result ON xj_group_con_rule.id=xj_group_con_rule_result.rule_id WHERE pid IN(" + sql_doc + ");";

        try {
            List<DbModel> listTemp = dbManager.findDbModelAll(new SqlInfo(sql));
            if (listTemp != null) {
                for (int i = 0; i < listTemp.size(); i++) {
                    DbModel model = listTemp.get(i);
                    TeamRuleEntity entity = parseObject(model, TeamRuleEntity.class);
                    if (entity != null) {
                        list.add(entity);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return list;
    }


    private <T> T parseObject(DbModel model, Class<T> cls) {

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

    /**
     * 获取资料规范(模块)总分
     *
     * @return
     */
    private float getRoleDocScore(String pid) {
        //为了方便阅读，故拆分多条sql
        //查询当前项的Score分组IDS
        String sql_score_ids = "SELECT score_group_id FROM xj_group_con_rule WHERE pid =('" + pid + "') GROUP BY score_group_id";

        //查询出当前检查项总分
        String sql = "SELECT SUM(score) AS score FROM xj_group_con_rule_score_group WHERE id IN(" + sql_score_ids + ");";


        try {
            DbModel moble = dbManager.findDbModelFirst(new SqlInfo(sql));
            if (moble != null && !moble.isEmpty("score")) {
                return moble.getFloat("score");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取资料规范(模块)总分
     *
     * @return
     */
    private void getRoleRecord(TeamRuleEntity entity) {

        //为了方便阅读，故拆分多条sql
        //根据首页ID，查询文档ID
        String sql_doc = "SELECT id FROM xj_group_con_rule WHERE pid ='" + entity.getId() + "'";
        //统计查询结果
        String sql = "SELECT COUNT(xj_group_con_rule.id)AS total_list,COUNT(record_type)AS total_record,SUM(deduct_score)AS sum_score FROM xj_group_con_rule LEFT JOIN xj_group_con_rule_result ON xj_group_con_rule.id=xj_group_con_rule_result.rule_id WHERE pid IN(" + sql_doc + ");";

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
            int sum_score_int = (int) sum_score * 10;
            int count_score_int = (int) entity.getScoreCount() * 10;

            if (total_list != 0) {//标准数有效
                if (total_record == 0) {//未开始
                    entity.setRecord_type(null);
                } else if (total_record == total_record) {//已完成
                    //是否被扣分
                    if (count_score_int == 0) {
                        entity.setRecord_type(RecordType.normal.name());
                    } else {
                        entity.setRecord_type(RecordType.answer.name());
                    }
                } else {
                    entity.setRecord_type(RecordType.ing.name());//进行中
                }

                entity.setDeduct_score(sum_score_int / 10f);

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
            dbManager.save(data);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取-巡检任务
     *
     * @return
     */
    public List<InspecteTaskEntity> getTaskList() {

        //
        List<InspecteTaskEntity> list = null;
        try {
            list = dbManager.selector(InspecteTaskEntity.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取问题记录
     *
     * @return
     */
    public List<TeamRuleResultEntity> getIssueList() {
        List<TeamRuleResultEntity> list = null;
        try {
            list = dbManager.selector(TeamRuleResultEntity.class).where("record_type", "=", "answer").findAll();
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

}
