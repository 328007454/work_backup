package com.cnksi.workticket.enum_ticket;

/**
 * Created by Mr.K on 2018/5/7.
 */

public enum TicketTimeEnum {
    region_10to11("10:00-11:00"), region_11to12("11:00-12:00"), region_14to15("14:00-15:00"),region_15to16("15:00-16:00"),region_16to17("16:00-17:00");

    public final String value;

    TicketTimeEnum(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
