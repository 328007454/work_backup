package com.cnksi.sjjc.service;

import com.cnksi.sjjc.bean.Transceiver;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 设备检测-收发信机
 *
 * @author luoxy
 * @date 2016/6/15
 * @copyRight
 */
public class TransceiverService extends BaseService<Transceiver> {

    private static TransceiverService instance;

    private TransceiverService() {
        super(Transceiver.class);
    }

    public static TransceiverService getInstance() {
        if (null == instance)
            instance = new TransceiverService();
        return instance;
    }

    /**
     * 查询当前报告某个设备的收发信机记录
     *
     * @param reportId
     * @param deviceId
     * @return
     * @throws DbException
     */
    public Transceiver findExitTransceiver(String reportId, String deviceId) throws DbException {
        return selector().and(Transceiver.REPORDID, "=", reportId).and(Transceiver.DEVICEID, "=", deviceId).findFirst();
    }

    /**
     * 查询当前报告收发信机记录
     *
     * @param reportId
     * @return
     * @throws DbException
     */
    public List<Transceiver> findExitTransceiver(String reportId) throws DbException {
        return selector().and(Transceiver.REPORDID, "=", reportId).findAll();
    }

    /**
     * 查询当前报告异常收发信机记录
     * @param reportId
     * @return
     * @throws DbException
     */
    public List<Transceiver> findExceptionTransceiver(String reportId) throws DbException {
        return selector().and(Transceiver.REPORDID, "=", reportId)
                .and(Transceiver.CHANNELSTATUS, "=", "1").findAll();
    }
}
