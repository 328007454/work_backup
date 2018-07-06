package com.cnksi.bdzinspection.model;

import com.flyco.tablayout.listener.CustomTabEntity;

/**
 * Created by Mr.K on 2018/7/6.
 */

public class TableEntity implements CustomTabEntity {

    public String tabTitle;
    public int selectedIcon;
    public int unSelectedIcon;

    public TableEntity(String tabTitle, int selectedIcon, int unSelectedIcon) {
        this.tabTitle = tabTitle;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return tabTitle;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
