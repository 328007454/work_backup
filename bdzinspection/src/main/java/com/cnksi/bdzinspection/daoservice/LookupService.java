package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Lookup;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.bdzinspection.model.SwitchMenu;
import com.cnksi.bdzinspection.utils.Config.LookUpType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lookup 数据查询
 *
 * @author terry
 */
public class LookupService {

    public static LookupService mInstance;

    private LookupService() {
    }

    public static LookupService getInstance() {
        if (mInstance == null) {
            mInstance = new LookupService();
        }
        return mInstance;
    }

    /**
     * 根据Lookup类型查询所有
     *
     * @param type
     * @return
     */
    public List<String> getAllValueByLookupType(String type) {
        List<String> valueList = new ArrayList<String>();
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                .orderBy(Lookup.SORT, false);
        try {
            List<Lookup> lookups = XunshiApplication.getDbUtils().findAll(selector);
            for (Lookup look : lookups) {
                valueList.add(look.v);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return valueList;

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

    public Lookup findLookupByK(String k) {
        try {
            return XunshiApplication.getDbUtils().findFirst(Selector.from(Lookup.class).where(Lookup.K, "=", k));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public Lookup findLookupByValue(String value) {
        Lookup lup = null;
        Selector selector = Selector.from(Lookup.class).where(Lookup.V, "=", value);

        try {
            lup = XunshiApplication.getDbUtils().findFirst(selector);
        } catch (DbException ex) {
            ex.printStackTrace();
        }
        return lup;
    }

    /**
     * 根据LookupType查询
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupByType(String type, boolean hasSearch) {
        List<Lookup> lookups = null;
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                .orderBy(Lookup.SORT, false);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (hasSearch) {
            if (lookups == null) {
                lookups = new ArrayList<Lookup>();
            }
//            lookups.add(new Lookup(Config.SEARCH_DEVICE_KEY, Config.SEARCH_DEVICE_VALUE));
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
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                .expr("AND (" + Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
                .expr("AND (" + Lookup.K + " NOT LIKE 'exclusive' " + ") AND (" + Lookup.LOO_ID + " IS NULL OR "
                        + Lookup.LOO_ID + "='')")
                .orderBy(Lookup.SORT, false);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }

    /**
     * 零时筛选全面巡检与日常巡检
     *
     * @param type
     * @return
     */
    public List<Lookup> findLookupTemp(String type) {
        List<Lookup> lookups = null;
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type)
                .and(WhereBuilder.b(Lookup.K, "in", new String[]{"full", "day"})).orderBy(Lookup.SORT, false);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
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
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                .expr("AND (" + Lookup.K + " = '" + k + "' AND " + Lookup.K + " NOT LIKE 'exclusive' " + ") AND ("
                        + Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
                .orderBy(Lookup.SORT, false);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
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
        Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
                .and(Lookup.LOO_ID, "=", looId).and(Lookup.K, "<>", "special_guzhang")
                .and(Lookup.K, "<>", "special_xideng").and(Lookup.K, "<>", "special_nighttime")
                .orderBy(Lookup.SORT, false);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }

    /**
     * 按照类型和looid来查询
     *
     * @param type
     * @param bdzId
     * @return
     */
    public List<Lookup> findSwitchMenu(String type, String bdzId) {
        List<SwitchMenu> menus = null;
        List<Lookup> lookups = new ArrayList<Lookup>();
        Selector selector = Selector.from(SwitchMenu.class).where(SwitchMenu.BDZID, "=", bdzId).and(SwitchMenu.DLT,"<>", "1").expr("and k like  '" + type + "%' ")
                .orderBy(Lookup.SORT, false);
        try {
            menus = XunshiApplication.getDbUtils().findAll(selector);
            if (null != menus && !menus.isEmpty())
                for (SwitchMenu menu : menus) {
                    lookups.add(new Lookup(menu));
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }


    /**
     * 根据lookup表中的k来查询 remark
     *
     * @param k
     * @return
     */
    public String getLookUpRemarkByK(String k) {
        String remark = "";
        Selector selector = Selector.from(Lookup.class).where(Lookup.K, "=", k);
        try {
            Lookup mLookUp = XunshiApplication.getDbUtils().findFirst(selector);
            if (mLookUp != null) {
                remark = mLookUp.remark;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return remark;
    }

    public List<String> getDeviceTypeList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("变压器");
        list.add("断路器");
        list.add("间隔刀闸");
        list.add("电压互感器");
        list.add("电流互感器");
        list.add("避雷器");
        list.add("电容器");
        list.add("电抗器");
        list.add("阻波器");
        list.add("开关柜");
        list.add("保护屏");
        list.add("测控屏");
        return list;
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
            Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", LookUpType.defect_reason.name())
                    .and(Lookup.LOO_ID, "=", parentId);
            lookupList = XunshiApplication.getDbUtils().findAll(selector);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * 根据ParentId 查询缺陷原因
     *
     * @param
     * @return
     */
    public List<Lookup> getDefectReasonType() {
        List<Lookup> lookupList = null;
        try {
            Selector selector = Selector.from(Lookup.class).where(Lookup.TYPE, "=", LookUpType.defect_reason.name())
                    .and(WhereBuilder.b(Lookup.LOO_ID, "=", "").expr("OR " + Lookup.LOO_ID + " IS NULL"));
            lookupList = XunshiApplication.getDbUtils().findAll(selector);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lookupList;
    }

    /**
     * 根据ParentId 查找子类
     *
     * @param
     * @return
     */

    public List<Lookup> getAllTypeListFromParentId(String id) {
        List<Lookup> lookups = null;
        Selector selector = Selector.from(Lookup.class).where(Lookup.LOO_ID, "=", id);
        try {
            lookups = XunshiApplication.getDbUtils().findAll(selector);
            if (null == lookups)
                lookups = new ArrayList<Lookup>();
        } catch (DbException e) {
            lookups = new ArrayList<Lookup>();
            e.printStackTrace();
        }
        return lookups;
    }

    public List<Lookup> findSpecialMenu(String currentTypeStr) {
        List<SpecialMenu> specialMenus = null;
        List<Lookup> lookups = new ArrayList<Lookup>();
        Selector selector = Selector.from(SpecialMenu.class).where(SpecialMenu.DLT,"<>", "1").expr("and k like  '" + currentTypeStr + "%' ")
                .orderBy(Lookup.SORT, false);
        try {
            specialMenus = XunshiApplication.getDbUtils().findAll(selector);
            if (null != specialMenus && !specialMenus.isEmpty())
                for (SpecialMenu menu : specialMenus) {
                    lookups.add(new Lookup(menu));
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups ;
    }
}
