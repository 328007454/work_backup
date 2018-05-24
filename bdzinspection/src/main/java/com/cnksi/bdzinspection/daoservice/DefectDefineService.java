package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;

import com.cnksi.bdzinspection.model.Defect;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 缺陷定义
 *
 * @author terry
 */
public class DefectDefineService extends BaseService<Defect> {

    public static DefectDefineService mInstance;

    private DefectDefineService() {
        super(Defect.class);
    }

    public static DefectDefineService getInstance() {
        if (mInstance == null) {
            mInstance = new DefectDefineService();
        }
        return mInstance;
    }

    /**
     * 根据巡视标准查询缺陷定义
     *
     * @param staid 巡视标准ID
     * @return
     */
    public HashMap<String, ArrayList<Defect>> findDefectDefineByStandardId(String staid) {

        LinkedHashMap<String, ArrayList<Defect>> groupHashMap = new LinkedHashMap<String, ArrayList<Defect>>();
        try {
            List<Defect> ddlist = new ArrayList<Defect>();
            SqlInfo sqlInfo = new SqlInfo("select * from defect_define where staid=? and dlt=0 ");
            sqlInfo.addBindArg(new KeyValue("staid", staid));
            try {
                List<DbModel> localDefectDefine = findDbModelAll(sqlInfo);
                if (localDefectDefine != null && localDefectDefine.size() > 0) {
                    for (DbModel model : localDefectDefine) {
                        ddlist.add(new Defect(model));
                    }
                }
            }catch (Exception e){

            }

            List<Defect> defectList = selector().and(Defect.STAID, "=", staid)
                    .expr("and (" + Defect.DLT + " is null or " + Defect.DLT + "<>'1')")
                    .orderBy(Defect.LEVEL).findAll();
            if (null != defectList) {
                ddlist.addAll(defectList);
            }
            String _tmpLevel = "";
            ArrayList<Defect> descriptionList = null;
            if (ddlist != null && ddlist.size() > 0) {
                for (Defect dd : ddlist) {
                    if (TextUtils.isEmpty(_tmpLevel)) {
                        _tmpLevel = dd.level;
                        descriptionList = new ArrayList<Defect>();
                    }
                    if (!_tmpLevel.equals(dd.level)) {
                        groupHashMap.put(_tmpLevel, descriptionList);
                        _tmpLevel = dd.level;
                        descriptionList = new ArrayList<Defect>();
                    }
                    descriptionList.add(dd);
                }
                if (!TextUtils.isEmpty(_tmpLevel)) {
                    groupHashMap.put(_tmpLevel, descriptionList);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return groupHashMap;
    }

    /**
     * 根据设备id查询对应的部件的缺陷定义
     *
     * @param deviceId
     * @param content
     * @return
     * @throws DbException
     */
    public List<DbModel> findDefectDefineByDeviceIdAndContent(String deviceId, String content) throws DbException {

        List<DbModel> defectList = findDefectDefineByDeviceIdAndContentFromDB(deviceId, content);
        // TODO: 2017/4/14 device_part 是否弃用
        String sql = "select d.*,dp.duid,dp.name as duname from defect_define d left join device_standards s on d.staid=s.staid left join device_part dp on s.duid=dp.duid left join device de on dp.deviceid=de.deviceid where de.deviceid =? and d.description like '%"
                + content + "%'";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", deviceId));
        List<DbModel> defectModel = findDbModelAll(sqlInfo);
        if (defectList == null) {
            defectList = new ArrayList<>();
        }
        if (defectModel != null) {
            defectList.addAll(defectModel);
        }

        return defectList;
    }

    public List<DbModel> findDefectDefineByDeviceIdAndContentFromDB(String deviceId, String content)
            throws DbException {
        String sql = "SELECT dn.duid, dn.duname, d.* FROM defect d LEFT JOIN standards s ON d.staid = s.staid LEFT JOIN device_unit dn ON dn.duid = s.duid LEFT JOIN device dv ON dv.dtid = dn.dtid WHERE dv.deviceid = ? AND d.description LIKE ?";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", deviceId));
        sqlInfo.addBindArg(new KeyValue("", "%" + content + "%"));

        return findDbModelAll(sqlInfo);
    }

    /**
     * 获取电池的缺陷内容
     *
     * @return
     */
    public LinkedHashMap<String, ArrayList<String>> getBatteryDefectContent() {
        LinkedHashMap<String, ArrayList<String>> groupHashMap = new LinkedHashMap<String, ArrayList<String>>();

        ArrayList<String> defectContentList = null;

        defectContentList = new ArrayList<String>();
        defectContentList.add("蓄电池内阻大于30mΩ小于50mΩ");
        defectContentList.add("单只蓄电池电压低于2.09V高于1.97V");
        groupHashMap.put("一般缺陷", defectContentList);

        defectContentList = new ArrayList<String>();
        defectContentList.add("蓄电池内阻大于50mΩ小于100mΩ");
        defectContentList.add("单只蓄电池电压低于1.97V高于1.8V");
        groupHashMap.put("严重缺陷", defectContentList);

        defectContentList = new ArrayList<String>();
        defectContentList.add("蓄电池内阻大于100mΩ");
        defectContentList.add("单只蓄电池电压低于1.8V");
        groupHashMap.put("危急缺陷", defectContentList);

        return groupHashMap;
    }

    /**
     * 保存缺陷定义
     *
     * @param mDefectDefine
     * @return
     */
    public boolean saveOrUpdateDefectDefine(Defect mDefectDefine, boolean isAdd) {
        try {
            saveOrUpdate(mDefectDefine);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缺陷定义
     *
     * @param mDefectDefine
     * @return
     */
    public boolean deleteDefectDefine(Defect mDefectDefine) {
        try {
            mDefectDefine.dlt = "1";
            saveOrUpdate(mDefectDefine);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

}
