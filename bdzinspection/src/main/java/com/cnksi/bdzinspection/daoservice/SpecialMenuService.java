package com.cnksi.bdzinspection.daoservice;

import android.widget.GridLayout;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.SpecialMenu;

import org.xutils.ex.DbException;

import java.nio.channels.Selector;

/**
 * Created by han on 2017/8/17.
 */

public class SpecialMenuService extends BaseService {
    static SpecialMenuService menuServiceInstance;

    public static SpecialMenuService getInstance() {
        if (menuServiceInstance == null) {
            menuServiceInstance = new SpecialMenuService();
        }

        return menuServiceInstance;
    }

    public SpecialMenu findCurrentDeviceType(String currentType) {
        SpecialMenu menu = null;
        try {
            menu = XunshiApplication.getDbUtils().selector(SpecialMenu.class).where(SpecialMenu.DLT, "=", "0").and(SpecialMenu.K, "=", currentType).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
            menu = new SpecialMenu();
        }

        return menu == null ? new SpecialMenu() : menu;

    }


}
