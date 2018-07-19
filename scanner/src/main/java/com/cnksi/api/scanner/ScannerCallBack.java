package com.cnksi.api.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * 扫描仪执行回调接口
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 13:24
 */
public interface ScannerCallBack {

    /**
     * 扫描设备回调接口
     * @param device 发现的蓝牙设备，如果等于null标识该回调为状态消息
     * @param state <ul>
     * <li>当device==null时，{@link State#ON}开始扫描,{@link State#OFF}停止扫描；</li>
     * <li>当device!=null时，{@link State#ON}有效设备,{@link State#OFF}无效设备。</li>
     * <ul/>
     */
//    void onScan(BluetoothDevice device, @State int state);

    /**
     * 设备连接状态改变回调
     * @param device 状态改变的设备
     * @param state {@link State#ON}连接成功,{@link State#OFF}连接断开(之前是连接的),link {@link State#ERROR}连接失败。
     */
    void onConnecteChange(BluetoothDevice device, @State int state);

    /**
     * 命令执行回调
     * @param command 命令另见{@link Command}
     * @param state <ul>命令执行结果
     * <li>{@link State#SUCCESS}成功</li>
     * <li>{@link State#ERROR}执行失败</li>
     * <li>{@link State#ERROR_UNCONN}设备未连接</li>
     * <li>{@link State#ERROR_EXCEP}命令发送失败</li>
     * </ul>
     */
    void onCommand(@Command int command, @State int state);

    /**
     * 扫描结果回调
     * @param tagId 识别RFID或者QR记录ID
     * @param tag 自定义数据对象，可以根据需要自行定义
     * @param model 另见 {@link Model}，目前仅支持RFID和二维码(QE)
     */
    void onReadData(String tagId, Object tag, @Model int model);
}
