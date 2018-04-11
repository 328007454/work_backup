package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by han on 2017/8/17.
 */

public class SpecialMenuService extends BaseService {
   static SpecialMenuService menuServiceInstance;

    public static SpecialMenuService getInstance(){
        if(menuServiceInstance==null){
            menuServiceInstance = new SpecialMenuService();
        }

        return menuServiceInstance;
    }

    public SpecialMenu findCurrentDeviceType(String currentType){
        SpecialMenu menu=null;
        try {
            menu = CustomApplication.getDbUtils().findFirst(from(SpecialMenu.class).and(SpecialMenu.K,"=",currentType));
        } catch (DbException e) {
            e.printStackTrace();
            menu = new SpecialMenu();
        }

        return  menu==null? new SpecialMenu():menu;

    }


}
