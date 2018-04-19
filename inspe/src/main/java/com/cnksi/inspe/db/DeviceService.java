package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceEntity;
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

    public DeviceTypeEntity getDeviceTypes(String bigId) {
        try {
            return dbManager.selector(DeviceTypeEntity.class)
                    .where("dlt", "=", "0")
                    .and("bigid", "=", bigId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ID查询变电站
     * @param bzdId
     * @return
     */
    public SubStationEntity getSubStation(String bzdId) {
        try {
            return dbManager.selector(SubStationEntity.class)
                    .where("dlt", "=", "0")
                    .and("bdzid", "=", bzdId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据班组ID查询变电站集合
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
        String deviceSql = "SELECT d.type type,s.`name` sname ,s.bdzid,s.spid,s.name_pinyin snamepy , d.deviceid , d.`name` dname,d.name_short dnameshort, d.name_short_pinyin dshortpinyin ,d.bigid,d.name_pinyin dnamepy " +
                "FROM device d LEFT JOIN spacing  s on d.spid = s.spid  WHERE d.bdzid = '" + bdzId + "' and d.bigid in " + bigId + "and d.dlt =0 AND (d.zhsblx is null OR d.zhsblx='null' OR d.zhsblx='01' OR d.zhsblx='');";
        //过滤组合设备AND (zhsblx is null or zhsblx='null' or zhsblx='01' or zhsblx='')
        deviceModels = dbManager.findDbModelAll(new SqlInfo(deviceSql));
        return deviceModels;
    }

    /**
     * 查询设备大类
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
     * 查询所有的设备大类
     * @return 所有的数据
     * @throws DbException
     */
    public List<DbModel> getBigTypeAll() throws DbException {
        List<DbModel> deviceModels = new ArrayList<>();
        String bigTypesSql = "select * from device_bigtype where iswt='Y' and  dlt = 0";
        deviceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return deviceModels;
    }

    /**
     * 根据设备ID查询设备
     * @param deviceId
     * @return
     */
    public DeviceEntity getDeviceById(String deviceId) {
        try {
            return dbManager.selector(DeviceEntity.class)
//                    .where("dlt", "=", "0")//自定义添加设备dlt=1;
                    .where("deviceid", "=", deviceId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return
     */
    public List<DbModel> getAllOneSpace(String bdzId) throws DbException {
        List<DbModel> spaceModels = new ArrayList<>();
        String bigTypesSql = "select * from spacing where bdzid = '" + bdzId + "' and dlt = 0 and device_type like '%one%'";
        spaceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return spaceModels;
    }


    /**
     * 保存缺失的设备台账到设备表中
     * @param entities
     */
    public void saveExtraDevice(List<DeviceEntity> entities) {
        try {
            dbManager.saveOrUpdate(entities);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机端添加的缺设备台账
     * @param bdzId
     * @return
     */
    public List<DbModel> getAddDevice(String bdzId, String taskId) throws DbException {

        List<DbModel> spaceModels = new ArrayList<>();
        String bigTypesSql = "select * from device where bdzid = '" + bdzId + "' and dlt = 1 and type ='" + taskId + "' ";
        spaceModels = dbManager.findDbModelAll(new SqlInfo(bigTypesSql));
        return spaceModels;
    }

    public List<String> getCheckDevices(String taskId, String plustekType) throws DbException {
        List<DbModel> checkDeviceModels = new ArrayList<>();
        List<String> checkDeviceIds = new ArrayList<>();

        String sql = "SELECT * FROM device_check_temp WHERE task_id='" + taskId + "' AND plustek_type ='"+plustekType+"' and  dlt =0 ;";
        checkDeviceModels = dbManager.findDbModelAll(new SqlInfo(sql));
        if (checkDeviceModels!=null&&!checkDeviceModels.isEmpty()){
            for (DbModel model: checkDeviceModels){
                checkDeviceIds.add(model.getString("device_id"));
            }
        }
        return checkDeviceIds ;
    }
}
