package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;

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

    public List<PlusteRuleEntity> getPlusteRule() {
        try {
            return dbManager.selector(PlusteRuleEntity.class).where("dlt", "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
