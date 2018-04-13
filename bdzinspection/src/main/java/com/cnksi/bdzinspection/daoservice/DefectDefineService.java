package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.Defect;
import com.cnksi.bdzinspection.model.DefectDefine;
import com.cnksi.bdzinspection.utils.Config;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 缺陷定义
 *
 * @author terry
 */
public class DefectDefineService {

    public static DefectDefineService mInstance;

    private DefectDefineService() {
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
    public HashMap<String, ArrayList<DefectDefine>> findDefectDefineByStandardId(String staid) {

        LinkedHashMap<String, ArrayList<DefectDefine>> groupHashMap = new LinkedHashMap<String, ArrayList<DefectDefine>>();
        try {
            Selector selector = Selector.from(Defect.class).where(Defect.STAID, "=", staid).expr("and dlt <> '1'");
            List<Defect> defects = XunshiApplication.getDbUtils().findAll(selector);
            List<DefectDefine> ddlist = new ArrayList<DefectDefine>();
            if (null != defects && !defects.isEmpty()) {
                for (Defect df : defects)
                    ddlist.add(new DefectDefine(df));
            }
            selector = Selector.from(DefectDefine.class).where(DefectDefine.STAID, "=", staid)
                    .expr("and (" + DefectDefine.DLT + " is null or " + DefectDefine.DLT + "<>'1')")
                    .orderBy(DefectDefine.LEVEL);
            List<DefectDefine> defectList = XunshiApplication.getDbUtils().findAll(selector);
            if (null != defectList)
                ddlist.addAll(defectList);
            String _tmpLevel = "";
            ArrayList<DefectDefine> descriptionList = null;
            if (ddlist != null && ddlist.size() > 0) {
                for (DefectDefine dd : ddlist) {
                    if (TextUtils.isEmpty(_tmpLevel)) {
                        _tmpLevel = dd.level;
                        descriptionList = new ArrayList<DefectDefine>();
                    }
                    if (!_tmpLevel.equals(dd.level)) {
                        groupHashMap.put(_tmpLevel, descriptionList);
                        _tmpLevel = dd.level;
                        descriptionList = new ArrayList<DefectDefine>();
                    }
                    descriptionList.add(dd);
                }
                if (!TextUtils.isEmpty(_tmpLevel)) {
                    groupHashMap.put(_tmpLevel, descriptionList);
                }
                _tmpLevel = "";
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
        SqlInfo sqlInfo = new SqlInfo(
                "select d.*,dp.duid,dp.name as duname from defect_define d left join device_standards s on d.staid=s.staid left join device_part dp on s.duid=dp.duid left join device de on dp.deviceid=de.deviceid where de.deviceid =? and d.description like '%"
                        + content + "%'",
                deviceId);

        List<DbModel> defectModel = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
        if (defectList == null)
            defectList = new ArrayList<DbModel>();
        if (defectModel != null)
            defectList.addAll(defectModel);

        return defectList;
    }

    public List<DbModel> findDefectDefineByDeviceIdAndContentFromDB(String deviceId, String content)
            throws DbException {
        SqlInfo sqlInfo = new SqlInfo(
                "SELECT dn.duid, dn.duname, d.* FROM defect d LEFT JOIN standards s ON d.staid = s.staid LEFT JOIN device_unit dn ON dn.duid = s.duid LEFT JOIN device dv ON dv.dtid = dn.dtid WHERE dv.deviceid = ? AND d.description LIKE ?",
                deviceId, "%" + content + "%");

        return XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
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
    public boolean saveOrUpdateDefectDefine(DefectDefine mDefectDefine, boolean isAdd) {
        try {
            XunshiApplication.getDbUtils().saveOrUpdate(mDefectDefine);
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
    public boolean deleteDefectDefine(DefectDefine mDefectDefine) {
        try {
            mDefectDefine.dlt = Config.DELETED;
            XunshiApplication.getDbUtils().saveOrUpdate(mDefectDefine);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

}