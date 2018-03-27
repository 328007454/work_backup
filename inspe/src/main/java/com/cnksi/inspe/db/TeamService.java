package com.cnksi.inspe.db;

import android.database.Cursor;
import android.util.Log;

import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.TeamRuleScoreEntity;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<TeamRuleEntity> getRoleStandard(String ids[]) {
        try {
            List<TeamRuleEntity> list = dbManager.selector(TeamRuleEntity.class).where("pid", "in", ids).findAll();

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
            if (moble != null) {
                return moble.getFloat("score");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return 0;
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


}
