package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.SwitchMenu;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.model.Lookup;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2017/8/17.
 */

public class SwitchMenuService extends BaseService<SwitchMenu> {
    final static SwitchMenuService menuServiceInstance=new SwitchMenuService();

    protected SwitchMenuService() {
        super(SwitchMenu.class);
    }

    public static SwitchMenuService getInstance() {
        return menuServiceInstance;
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
        try {
            Selector selector = selector().and(SwitchMenu.BDZID, "=", bdzId).expr("and k like  '" + type + "%' ")
                    .orderBy(Lookup.SORT, false);
            menus = selector.findAll();
            if (null != menus && !menus.isEmpty()) {
                for (SwitchMenu menu : menus) {
                    lookups.add(SwitchMenu.convertLookup(menu));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }

    public SwitchMenu findByBdz(String bdzid,String type) throws DbException {
        return selector().and(SwitchMenu.BDZID, "=", bdzid).and(SwitchMenu.DLT, "=", 0).and(SwitchMenu.K, "=", type).findFirst();
    }
}
