package com.cnksi.workticket.enum_ticket;

/**
 * Created by Mr.K on 2018/4/28.
 */

public enum TicketEnum {

    GZRZ("工作日志"), GZJL("工作记录"), YUYUE("预约");

    public final String value;

    TicketEnum(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
