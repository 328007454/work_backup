package com.cnksi.bdzinspection.model.bluetooth;

import java.io.Serializable;

/**
 * 蓝牙文件传输对应的字段标识
 * Created by han on 2016/12/8.
 */

public class BluetoothFileBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 消息内容
     */
    private String msg = "";
    /**
     * 文件名字
     */
    private String fileName = "";
    /**
     * 文件路径
     */
    private String filePath = "";
    /**
     * 接收百分比
     */
    private String uppercent = "";
    private String tspeed = "";
    private boolean showflag;
    private byte[] file;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getFilename() {
        return fileName;
    }

    public void setFilename(String filename) {
        this.fileName = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFilepath() {
        return filePath;
    }

    public void setFilepath(String filepath) {
        this.filePath = filepath;
    }

    public String getUppercent() {
        return uppercent;
    }

    public void setUppercent(String uppercent) {
        this.uppercent = uppercent;
    }

    public String getTspeed() {
        return tspeed;
    }

    public void setTspeed(String tspeed) {
        this.tspeed = tspeed;
    }

    public boolean isShowflag() {
        return showflag;
    }

    public void setShowflag(boolean showflag) {
        this.showflag = showflag;
    }

}
