package com.cnksi.common.daoservice;

import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.model.Lookup;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lookup 数据查询
 *
 * @author terry
 */
public class LookupService extends BaseService<Lookup> {

    public static LookupService mInstance;

    private LookupService() {
        super(Lookup.class);
    }

    public static LookupService getInstance() {
        if (mInstance == null) {
            mInstance = new LookupService();
        }
        return mInstance;
    }


    /**
     * 根据LookupType查询
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupByType(String type) {
        return findLookupByType(type, false);
    }


    /**
     * 根据LookupType查询
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupByType(String type, boolean hasSearch) {
        List<Lookup> lookups = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                    .orderBy(Lookup.SORT, false);

            lookups = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (hasSearch) {
            if (lookups == null) {
                lookups = new ArrayList<>();
            }
        }
        return lookups;
    }

    /**
     * 按照Type来查询LookUp表中第一级的数据
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupByTypeWithOutLooId(String type) {
        List<Lookup> lookups = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                    .expr("AND (" + Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
                    .expr("AND (" + Lookup.K + " NOT LIKE 'exclusive' " + ") AND (" + Lookup.LOO_ID + " IS NULL OR "
                            + Lookup.LOO_ID + "='')")
                    .orderBy(Lookup.SORT, false);

            lookups = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }


    /**
     * 按照Type,k来查询LookUp表中第一级的数据
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupByTypeWithOutLooId(String type, String k) {
        List<Lookup> lookups = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                    .expr("AND (" + Lookup.K + " = '" + k + "' AND " + Lookup.K + " NOT LIKE 'exclusive' " + ") AND ("
                            + Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
                    .orderBy(Lookup.SORT, false);

            lookups = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }

    /**
     * 按照类型和looid来查询
     *
     * @param type
     * @param looId
     * @return
     */
    public List<Lookup> findLookupByLooId(String type, String looId) {
        List<Lookup> lookups = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                    .and(Lookup.LOO_ID, "=", looId).and(Lookup.K, "<>", "special_guzhang")
                    .and(Lookup.K, "<>", "special_xideng").and(Lookup.K, "<>", "special_nighttime")
                    .orderBy(Lookup.SORT, false);

            lookups = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }


    /**
     * 根据ParentId 查询缺陷原因
     *
     * @param parentId
     * @return
     */
    public List<Lookup> getDefectReasonListByParentId(String parentId) {
        List<Lookup> lookupList = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", LookUpType.defect_reason.name())
                    .and(Lookup.LOO_ID, "=", parentId);
            lookupList = selector.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * 根据ParentId 查询缺陷原因
     */
    public List<Lookup> getDefectReasonType() {
        List<Lookup> lookupList = null;
        try {
            Selector selector = selector().and(Lookup.TYPE, "=", LookUpType.defect_reason.name())
                    .and(WhereBuilder.b(Lookup.LOO_ID, "=", "").expr("OR " + Lookup.LOO_ID + " IS NULL"));
            lookupList = selector.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * 根据变电站和巡视或者维护类型，查询条件下的子项
     *
     * @param type
     * @param bdzId
     * @return
     */
    public List<DbModel> findAllTypeModel(String bdzId, String type) {
        List<DbModel> models = new ArrayList<>();
        String sql = "";
        SqlInfo sqlInfo = null;
        if (type.contains(InspectionType.maintenance.name()) || type.contains(InspectionType.switchover.name())) {
            sql = "select * from switch_menu where bdzid =? and k like '" + type + "%' and dlt = 0 order by sort";
            sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", bdzId));
        } else if (type.contains(InspectionType.special.name())) {
            sql = "select * from special_menu where k like '" + type + "%' and dlt = 0 order by sort";
            sqlInfo = new SqlInfo(sql);
        }
        try {
            if (!InspectionType.SBJC_06.name().equalsIgnoreCase(type)) {
                models = findDbModelAll(sqlInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        if (InspectionType.SBJC_06.name().equalsIgnoreCase(type)) {
            final String[] types = {InspectionType.SBJC_06_sf6.name(), InspectionType.SBJC_06_water.name(), InspectionType.SBJC_06_gas.name()};
            final String[] names = {InspectionType.SBJC_06_sf6.value, InspectionType.SBJC_06_water.value, InspectionType.SBJC_06_gas.value};
            for (int typePosition = 0; typePosition < types.length; typePosition++) {
                String typeModel = types[typePosition];
                String typeName = names[typePosition];
                DbModel dbModel = new DbModel();
                dbModel.add("k", typeModel);
                dbModel.add("v", typeName);
                models.add(dbModel);
            }
        }

        return models;
    }

}
