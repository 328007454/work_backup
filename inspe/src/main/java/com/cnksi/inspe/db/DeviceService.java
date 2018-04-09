package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;
import com.cnksi.inspe.db.entity.UserGroupEntity;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 13:12
 */
public class DeviceService extends BaseDbService {

    /**
     * 获取精益化评价设备类型,共计28类
     *
     * @return
     */
    public List<DeviceTypeEntity> getDeviceTypes() {
        try {
            return dbManager.selector(DeviceTypeEntity.class)
                    .where("dlt", "=", "0")
                    .and("iswt", "=", "Y")
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据班组ID查询变电站
     *
     * @param groupId
     * @return
     */
    public List<SubStationEntity> getSubStations(String groupId) {
        try {
            return dbManager.selector(SubStationEntity.class)
                    .where("dlt", "=", "0")
                    .and("dept_id", "=", groupId)
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据设备大类id获取当前变电站的设备，不分一二次设备等
     */

    public List<DbModel> getAllDeviceByBigID(String bdzId, String bigId) throws DbException {
        List<DbModel> deviceModels = new ArrayList<>();
        String deviceSql = "SELECT s.`name` sname ,s.bdzid,s.spid,s.name_pinyin snamepy , d.deviceid , d.`name` dname,d.name_short dnameshort,d.bigid,d.name_pinyin dnamepy " +
                "FROM device d LEFT JOIN spacing  s on d.spid = s.spid  WHERE d.bdzid = '" + bdzId + "' and d.bigid in " + bigId + "and d.dlt =0";

        deviceModels = dbManager.findDbModelAll(new SqlInfo(deviceSql));
        return deviceModels;
    }

    /**
     * 查询设备大类
     *
     * @param bigId
     * @return
     * @throws DbException
     */
    public List<DbModel> getBigTypeModels(String bigId) throws DbException {
        List<DbModel> deviceModels = new ArrayList<>();
        String bigTypesSql = "select * from device_bigtype where dlt = 0 and bigid in " + bigId + "";
        deviceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return deviceModels;
    }

    /**
     * 根根设备大类查询设备当前精益化检查的大类
     * @param bigId
     * @return
     * @throws DbException
     */
    public List<DbModel> getAllDeviceStandardTypeByBigId(String bigId) throws DbException {

        List<DbModel> deviceModels = new ArrayList<>();
        String bigTypesSql = "select * from xj_jyhpj_rule where bigid = '"+bigId+"' and level = '1' and dlt = '0' ";
        deviceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return deviceModels;
    }

    public DbModel getDeviceById(String deviceId) throws DbException {
        DbModel dbModel = null;
        String sql = "select * from device where deviceid = '"+deviceId+"' and dlt = '0'";
        dbModel = dbManager.findDbModelFirst(new SqlInfo(sql));
        return  dbModel;
    }
}
