package com.cnksi.workticket.enum_ticket;

import android.text.TextUtils;

/**
 * @decrption  工作票的相关状态
 * @author Mr.K  on 2018/5/7.
 */

public enum TicketStatusEnum {
    normal("正常"), conflict("冲突"), kp("开票"), jp("结票"), other("其他工作");

    public final String value;

    TicketStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static  String getValue(String string){
        if (TextUtils.equals(string,TicketStatusEnum.kp.name())){
            return TicketStatusEnum.kp.value;
        }else if(TextUtils.equals(string,TicketStatusEnum.jp.name())){
            return TicketStatusEnum.jp.value;
        }else if(TextUtils.equals(string,TicketStatusEnum.other.name())){
            return TicketStatusEnum.other.value;
        }else {
            return "";
        }
    }
}
