package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.model.Lookup;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2017/8/17.
 */

public class SpecialMenuService extends BaseService<SpecialMenu> {
    final static SpecialMenuService menuServiceInstance = new SpecialMenuService();

    protected SpecialMenuService() {
        super(SpecialMenu.class);
    }

    public static SpecialMenuService getInstance() {
        return menuServiceInstance;
    }

    public SpecialMenu findCurrentDeviceType(String currentType) {
        SpecialMenu menu = null;
        try {
            menu = selector().and(SpecialMenu.K, "=", currentType).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            menu = new SpecialMenu();
        }

        return menu == null ? new SpecialMenu() : menu;

    }

    public List<Lookup> findSpecialMenu(String currentTypeStr) {
        List<SpecialMenu> specialMenus = null;
        List<Lookup> lookups = new ArrayList<Lookup>();
        try {
            Selector selector = selector().expr("and k like  '" + currentTypeStr + "%' ")
                    .orderBy(Lookup.SORT, false);

            specialMenus = selector.findAll();
            if (null != specialMenus && !specialMenus.isEmpty()) {
                for (SpecialMenu menu : specialMenus) {
                    lookups.add(SpecialMenu.convertLookup(menu));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return lookups;
    }


}
