package com.cnksi.api.scanner;

import android.support.annotation.IntDef;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 扫描仪工作模式
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 13:48
 */
@Target({PARAMETER, TYPE})
@IntDef({Model.RFID, Model.QR})
public @interface Model {
    int RFID = 0;
    int QR = 1;
}
