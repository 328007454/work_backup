package com.cnksi.api.scanner;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 扫描仪命令
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 13:14
 */
@Target({PARAMETER})
@IntDef({Command.RFID_ON, Command.RFID_OFF, Command.QR_ON, Command.QR_OFF, Command.DEVICE_CONFIG})
public @interface Command {
    int RFID_ON = 11;
    int RFID_OFF = 12;
    int QR_ON = 13;
    int QR_OFF = 14;
    int DEVICE_CONFIG = 15;
}
