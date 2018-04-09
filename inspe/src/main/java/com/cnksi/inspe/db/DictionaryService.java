package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.DictionaryEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 字典服务
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 13:12
 */
public class DictionaryService extends BaseDbService {

    /**
     * 根据ID获取字典
     *
     * @param type
     * @return
     */
    public List<DictionaryEntity> getDictonary(String type) {
        try {
            return dbManager.selector(DictionaryEntity.class)
                    .where("dlt", "=", "0")
                    .and("type", "=", type)
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取精益化检查-精益化评价问题产生原因
     *
     * @return
     */
    public List<DictionaryEntity> getDictonaryIssueReason() {
        return getDictonary("jyhpj_produce_reason");
    }

    /**
     * 获取精益化检查-问题性质
     *
     * @return
     */
    public List<DictionaryEntity> getDictonaryIssueNature() {
        return getDictonary("jyhpj_problem_nature");
    }

}
