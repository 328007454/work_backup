package com.cnksi.workticket.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @decrption  时间区间 ""11:00-12:00,14:00-15:00
 * @author Mr.K on 2018/5/9.
 */

public class TicketTimeZone extends AbstractExpandableItem<WorkTicketOrder> implements MultiItemEntity {
    public String timeZone;

    public TicketTimeZone(String title) {
        this.timeZone = title;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
