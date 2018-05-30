package com.cnksi.common.daoservice;

import com.cnksi.common.model.SpacingGroup;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 19:46
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpacingGroupService extends BaseService<SpacingGroup> {
    protected SpacingGroupService() {
        super(SpacingGroup.class);
    }

    final static SpacingGroupService instance=new SpacingGroupService();

    public static SpacingGroupService getInstance() {
        return instance;
    }
    public List<SpacingGroup> findSpacingGroup(String bdzId) {

        try {
            Selector selector = selector().and(SpacingGroup.BDZID, "=", bdzId).orderBy(SpacingGroup.SORT);

            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
