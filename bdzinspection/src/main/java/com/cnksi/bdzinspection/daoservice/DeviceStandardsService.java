package com.cnksi.bdzinspection.daoservice;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.model.DeviceStandards;
import com.cnksi.core.utils.FileUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备部件查询
 *
 * @author terry
 */
public class DeviceStandardsService extends BaseService<DeviceStandards>{

    public static DeviceStandardsService mInstance;

    private DeviceStandardsService() {
        super(DeviceStandards.class);
    }

    public static DeviceStandardsService getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceStandardsService();
        }
        return mInstance;
    }


    /**
     * 设备详细信息界面 根据DevicePartId 查询对应的巡检标准
     *
     * @param devicePartId 设备部件id
     * @return
     * @throws DbException
     */
    public List<DbModel> findStandardListByDevicePartId(String devicePartId, String deviceId, String inspectionType) {
        try {
            String sql = "SELECT 	ds.staid staid,ds.duid duid,ds.description description,ds.resulttype resulttype,ds.dlt dlt,ds.report_type report_type,ds.kind kind,ds.origin origin FROM device_standards ds " +
                    " WHERE ds.kind LIKE '%" + inspectionType + "%' AND ( ds.report_type = '0' OR ds.report_type IS NULL) AND ds.dlt <> '1' and ds.duid = ? and ds.device_id=? ORDER BY ds.sort,ds.staid DESC";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", devicePartId));
            sqlInfo.addBindArg(new KeyValue("", deviceId));

            return findDbModelAll(sqlInfo);

        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    /**
     * 从库中查询巡检标准
     *
     * @param duid
     * @param inspectionType
     * @return
     */
    public List<DbModel> findStandardListFromDB(String duid, String inspectionType) {
        List<DbModel> dataList = new ArrayList<DbModel>();
        try {
            String sql = "SELECT 1 as islib, s.staid staid,s.duid duid,s.description description,s.resulttype resulttype,s.dlt dlt,s.report_type report_type,s.kind kind,s.origin origin FROM "
                    + " standards s  WHERE " + "s.kind LIKE " + "'%" + inspectionType + "%'" + " AND ( s.report_type = '0' OR s.report_type IS NULL)" + " AND s.dlt <> '1' and s.duid = '" + duid + "' ORDER BY s.sort,s.staid DESC";
            dataList = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(dataList);
    }




    public List<DbModel> findStandardsListFromSpecial(String deviceBigId, String inspectionType) {
        List<DbModel> rs = new ArrayList<DbModel>();
        try {
            String sql = "SELECT * from standard_special ss WHERE ss.kind = '" + inspectionType + "' AND ss.dlt <> '1' AND ss.bigid = '" + deviceBigId + "' ORDER BY ss.sort DESC";
            rs = findDbModelAll(new SqlInfo(sql));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(rs);
    }

    /**
     * 更换巡检标准的图片
     *
     * @param mStandard
     * @param changePicName
     */
    public void updateStandardPic(DeviceStandards mStandard, String changePicName) {
        try {
            // 先删除原来替换的照片
            FileUtils.deleteFile(Config.CUSTOMER_PICTURES_FOLDER + mStandard.change_pic);
            FileUtils.deleteFile(Config.PICTURES_FOLDER + mStandard.change_pic);

            mStandard.change_pic = changePicName;
            update(mStandard, DeviceStandards.CHANGE_PIC);
            // saveOrUpdate(mStandard);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更换巡检标准的图片
     */
    public boolean deleteStandardById(String standardId) {
        try {
            update(DeviceStandards.class, WhereBuilder.b(DeviceStandards.STAID, "=", standardId), new KeyValue(DeviceStandards.DLT, Config.DELETED));
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param standardId
     * @return
     */
    public DeviceStandards findDeviceStandardById(String standardId) {
        try {
            return findById( standardId);
        } catch (DbException e) {
            return null;
        }
    }

    /**
     * 保存巡检标准
     *
     * @param mStandards
     * @return staid
     */
    public String saveDeviceStandards(DeviceStandards mStandards, boolean isAdd) {
        try {
            saveOrUpdate(mStandards);
            return String.valueOf(mStandards.staid);
        } catch (DbException e) {
            return "";
        }
    }
}
