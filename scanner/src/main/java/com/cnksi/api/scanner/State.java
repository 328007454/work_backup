package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.IntDef;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 命令执行状态
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 13:16
 */
@Target({PARAMETER})
@IntDef({State.OFF, State.ON, State.SUCCESS, State.ERROR, State.ERROR_UNCONN, State.ERROR_EXCEP})
public @interface State {
    /** 关闭状态 */
    int OFF = 0;
    /** 打开状态 */
    int ON = 1;
    /** 命令执行成功，并不意味命令生效 */
    int SUCCESS = 10;
    /** 命令执行错误(未归类错误) */
    int ERROR = 11;
    /** 执行命令，设备未连接错误 */
    int ERROR_UNCONN = 12;
    /** 执行命令，异常错误 */
    int ERROR_EXCEP = 13;
}
