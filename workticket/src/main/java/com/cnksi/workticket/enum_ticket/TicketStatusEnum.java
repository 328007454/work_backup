package com.cnksi.workticket.enum_ticket;

/**
 * Created by Mr.K on 2018/5/7.
 */

public enum TicketStatusEnum {
    normal("正常"), conflict("冲突");

    public final String value;

    TicketStatusEnum(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
