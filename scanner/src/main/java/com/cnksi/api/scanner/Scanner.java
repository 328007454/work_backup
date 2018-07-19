package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.IntDef;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 扫描仪封装接口<br/>
 * 主要针对【远望谷】和【斑马】扫描仪。
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 13:24
 */
public interface Scanner {

    /**
     * 绑定扫描仪<br/>连接时如果没有绑定，{@link SM}则会调用该方法进行配对，配对完成后需要实现连接扫描仪
     * @param device 设备包含了绑定状态，未绑定(取消了绑定)，绑定中(自动配对处理)，已绑定(连接扫描仪)
     * @return true中断广播，false广播继续传递
     */
    boolean bond(BluetoothDevice device);

    /**
     * 练级扫描仪
     * @param device 如果需要进行转换请自行通过设备Address作为唯一标识
     * @return <li>true正在建立连接</li>
     * 执行结果回调{@link ScannerCallBack#onConnecteChange(BluetoothDevice, int)},
     * 执行成功state={@link State#SUCCESS},失败state={@link State#ERROR}）
     * <li>false没有建立连接</li>
     */
    boolean startConnecte(BluetoothDevice device);

    /**
     * 关闭已连接的扫描仪<br/>
     * 关闭连接前请先关闭扫描仪当前执行任务如：RFID扫描，QR扫描任务等。
     * @return <li>true当前有连接的扫描仪需要关闭</li>
     * 执行结果回调{@link ScannerCallBack#onConnecteChange(BluetoothDevice, int)}
     * 关闭完成state={@link State#ERROR}
     * <li>false当前没有需要关闭的扫描仪</li>
     */
    boolean stopConnected();

    /** 对{@link #command(int, Object)}的一个封装 */
    boolean startReadCommand();

    boolean stopReadCommand();

    boolean startQRCommand();

    boolean stopQRCommand();

    /**
     * 向扫描仪发送命令</br>
     * 该方法需要实现@{link {@link Command}}命令集，如果不实现的命令请返回false。<br/>
     * <b>注意</b>由于蓝牙传输需要耗时，建议采用队列或者handler去执行相关命令，且有必要考虑重复命令
     * 执行过滤等。
     * @param command 命令代码,另见{@link Command}
     * @param param 命令参数，如果需要执行定义
     * @return true命令正在执行中，false命令不具备执行条件或未实现；
     */
    boolean command(@Command int command, Object param);

    /**
     * 设置相关回调监听器，执行完成后请回调。另见@{link {@link ScannerCallBack}}
     * @param callback
     */
    void register(ScannerCallBack callback);

    /**
     * 保留方法，可以不用实现
     * @param callback
     */
    void unregister(ScannerCallBack callback);

    /**
     * 设备连接状态
     * @return <li>{@link android.bluetooth.BluetoothAdapter#STATE_DISCONNECTED}未连接/已断开连接</li>
     * <li>{@link android.bluetooth.BluetoothAdapter#STATE_DISCONNECTING}正在断开连接</li>
     * <li>{@link android.bluetooth.BluetoothAdapter#STATE_CONNECTING}正在建立连接</li>
     * <li>{@link android.bluetooth.BluetoothAdapter#STATE_CONNECTED}已连接</li>
     */

    @Target({METHOD, PARAMETER})
    @IntDef({BluetoothAdapter.STATE_DISCONNECTED,
            BluetoothAdapter.STATE_DISCONNECTING,
            BluetoothAdapter.STATE_CONNECTING,
            BluetoothAdapter.STATE_CONNECTED
    })
    @interface ConnecteState {
    }

    @ConnecteState
    int connecteState();

    @Target({METHOD, PARAMETER})
    @IntDef({State.ON,
            State.OFF,
    })
    @interface SetState {
    }

    /**
     * RFID状态
     * @return <li>{@link State#ON}扫描进行中</li>
     * <li>{@link State#OFF}停止扫描</li>
     */
    @SetState
    int rfidState();

    /**
     * QR状态
     * @return <li>{@link State#ON}扫描进行中</li>
     * <li>{@link State#OFF}停止扫描</li>
     */
    @SetState
    int qrState();
}
