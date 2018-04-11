package com.cnksi.bdzinspection.daoservice;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class CopyResultService {
    public static CopyResultService mInstance;

    private CopyResultService() {
    }

    public static CopyResultService getInstance() {
        if (mInstance == null) {
            mInstance = new CopyResultService();
        }
        return mInstance;
    }

    public void saveOrUpdate(CopyResult copyResult) {
        copyResult.update_time = DateUtils.getCurrentTime("yyyy-MM-dd HH:mm:ss");
        try {
            if (TextUtils.isEmpty(copyResult.id)) {
                copyResult.id = UUID.randomUUID().toString();
                copyResult.create_time = copyResult.update_time;
                CustomApplication.getDbUtils().save(copyResult);
            } else {
                CustomApplication.getDbUtils().replace(copyResult);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public CopyResult getResult(String reportId, String itemId) {
        Selector selector = Selector.from(CopyResult.class).where(CopyResult.REPORTID, "=", reportId).and(CopyResult.ITEM_ID, "=", itemId).orderBy(CopyResult.UPDATE_TIME);
        try {
            return CustomApplication.getDbUtils().findFirst(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询设备先最新的抄录项
     */
    public List<CopyResult> getResultList(String bdzid, String reportId, String deviceId, boolean inReport) {
        try {

            Selector selector = Selector.from(CopyResult.class).where(CopyResult.DEVICEID, "=", deviceId).and(CopyResult.BDZID, "=", bdzid);
            if (inReport) {
                selector.and(CopyResult.REPORTID, "=", reportId);
            } else {
                selector.expr(" and (reportId <>'" + reportId + "' or reportId ISNULL)");
            }
            selector.expr(" and create_time in ( select max(create_time) from copy_result cr where cr.deviceid = '" + deviceId + "' and cr.bdzid = '" + bdzid + "' group by item_id) group by " + CopyResult.ITEM_ID + " order by " + CopyResult.UPDATE_TIME + " DESC," + CopyResult.CREATE_TIME + " DESC");
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashSet<String> getCopyDeviceIdList(String reportId, String inspection) {
        HashSet<String> rs = new HashSet<>();
        try {
            String sql = "select c.deviceId from(" + "SELECT deviceid,count(CASE WHEN val = '' THEN NULL ELSE val END) +" + " count(CASE WHEN val_a = '' THEN NULL ELSE val_a END) + " + "count(CASE WHEN val_b = '' THEN NULL ELSE val_b END) + " + "count(CASE WHEN val_c = '' THEN NULL ELSE val_c END) + " + "count(CASE WHEN val_special = '' THEN NULL ELSE val_special END) +" + "count(CASE WHEN val_o = '' THEN NULL ELSE val_o END)  count FROM copy_result" + " WHERE reportid = ? GROUP BY deviceid) c  LEFT JOIN (" + "SELECT item.deviceid,count(CASE WHEN val = 'Y' THEN 1 ELSE NULL END) +" + "count(CASE WHEN val_a = 'Y' THEN 1 ELSE NULL END)+" + "count(CASE WHEN val_b = 'Y' THEN 1 ELSE NULL END)+" + "count(CASE WHEN val_c = 'Y' THEN 1 ELSE NULL END)+" + "count(CASE WHEN val_o = 'Y' THEN 1 ELSE NULL END)" + "AS copyCount FROM copy_item item where kind like '%"
                    + inspection + "%' and item.dlt = '0' GROUP BY item.deviceid" + ") t ON t.deviceid = c.deviceid WHERE t.copyCount = c.count";
            SqlInfo sqlInfo = new SqlInfo(sql, reportId);
            List<DbModel> models = CustomApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (models != null)
                for (DbModel model : models) {
                    rs.add(model.getString("deviceid"));
                }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public HashSet<String> getCopyDeviceIdList1(String reportId, String inspection) {
        HashSet<String> rs = new HashSet<>();
        try {
            String sql = "select c.deviceId from(" + "SELECT deviceid,count(CASE WHEN val = '' THEN NULL ELSE val END) +" + " count(CASE WHEN val_a = '' THEN NULL ELSE val_a END) + " + "count(CASE WHEN val_b = '' THEN NULL ELSE val_b END) + " + "count(CASE WHEN val_c = '' THEN NULL ELSE val_c END) + " + "count(CASE WHEN val_o = '' THEN NULL ELSE val_o END) count FROM copy_result"
                    + " WHERE reportid = ? GROUP BY deviceid) c  LEFT JOIN (" + "SELECT item.deviceid,count(CASE WHEN val = 'Y' THEN 1 ELSE NULL END) +"
                    + "count(CASE WHEN val_a = 'Y' THEN 1 ELSE NULL END)+" + "count(CASE WHEN val_b = 'Y' THEN 1 ELSE NULL END)+"
                    + "count(CASE WHEN val_c = 'Y' THEN 1 ELSE NULL END)+" + "count(CASE WHEN val_o = 'Y' THEN 1 ELSE NULL END)"
                    + "AS copyCount FROM copy_item item where type_key in ('"
                    + inspection + "') and item.dlt = '0' GROUP BY item.deviceid" + ") t ON t.deviceid = c.deviceid WHERE t.copyCount = c.count";
            SqlInfo sqlInfo = new SqlInfo(sql, reportId);

            List<DbModel> models = CustomApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (models != null) {
                for (DbModel model : models) {
                    rs.add(model.getString("deviceid"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public long getReportCopyCount(String reportId) {
        // String sql = "select count(CASE WHEN val = '' THEN NULL ELSE val END)+" + "count(CASE WHEN val_a = '' THEN NULL ELSE val_a END)+" + "count(CASE WHEN val_b = '' THEN NULL ELSE val_b END)+" + "count(CASE WHEN val_c = '' THEN NULL ELSE val_c END)+" + "count(CASE WHEN val_o = '' THEN NULL ELSE val_o END) as count" + " from copy_result result WHERE result.reportid=?";
        String sql = "select count(1) as count" + " from copy_result result WHERE result.reportid=? and result.dlt=0 and ( result.val is not null or result.val_a is not null or result.val_b is not null or result.val_c is not null or result.val_o is not null or result.val_special is not null)";
        SqlInfo sqlInfo = new SqlInfo(sql, reportId);
        try {
            DbModel dbMode = CustomApplication.getDbUtils().findDbModelFirst(sqlInfo);
            return dbMode.getLong("count");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void clearNULLData() {
        String sql = "update copy_result SET  val=(CASE WHEN val='' THEN NULL ELSE val END)," + "val_a=(CASE WHEN val_a='' THEN NULL ELSE val_a END)," + "val_b=(CASE WHEN val_b='' THEN NULL ELSE val_b END)," + "val_c=(CASE WHEN val_c='' THEN NULL ELSE val_c END)," + "val_o=(CASE WHEN val_o='' THEN NULL ELSE val_o END)," + "val_old=(CASE WHEN val_old='' THEN NULL ELSE val_old END)," + "val_a_old=(CASE WHEN val_a_old='' THEN NULL ELSE val_a_old END)," + "val_b_old=(CASE WHEN val_b_old='' THEN NULL ELSE val_b_old END)," + "val_c_old=(CASE WHEN val_c_old='' THEN NULL ELSE val_c_old END)," + "val_o_old=(CASE WHEN val_o_old='' THEN NULL ELSE val_o_old END)," + "dzcs=(CASE WHEN dzcs='' THEN NULL ELSE dzcs END)," + "dzcs_old=(CASE WHEN dzcs_old='' THEN NULL ELSE dzcs_old END),"
                + "dzcs_total=(CASE WHEN dzcs_total='' THEN NULL ELSE dzcs_total END) where val=''  OR val_a=''" + "OR val_b=''  OR val_c=''  OR val_o='' OR val_old='' OR val_a_old=''" + "OR val_b_old='' OR val_c_old='' OR val_o_old='' OR dzcs='' OR dzcs_old=''" + "OR dzcs_total=''";
        try {
            CustomApplication.getDbUtils().execNonQuery(sql);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询设备先最新的抄录项
     */
    public List<CopyResult> getResultList(String bdzid, String reportId, String deviceId, boolean inReport, String type) {
        try {
            String operate = (inReport) ? "=" : "!=";
            Selector selector = Selector.from(CopyResult.class).where(CopyResult.DEVICEID, "=", deviceId).and(CopyResult.REPORTID, operate, reportId).expr(" and " + CopyItem.TYPE_KEY + " in('" + type + "') and create_time in ( select max(create_time) from copy_result cr where cr.bdzid = '" + bdzid + "' and cr.deviceid = '" + deviceId + "' group by item_id)  group by " + CopyResult.ITEM_ID + " order by " + CopyResult.UPDATE_TIME + "," + CopyResult.CREATE_TIME);
            return CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCopyResult(String bdzId, String reportId, String currentType) {
        long copyCount = CopyResultService.getInstance().getReportCopyCount(reportId);
        // long totalCount = CopyItemService.getInstance().getCopyItemCount1(bdzId, currentType);
        long totalCount = CopyItemService.getInstance().getCopyItemCount1(bdzId, currentType);
        return copyCount + "/" + totalCount;
    }

    public List<CopyResult> findAllMaintenanceCopyResult(String reportId, String currentInspectionType, String currentBdzId) throws DbException {
        List<CopyResult> copyResults = null;
        copyResults = CustomApplication.getDbUtils().findAll(Selector.from(CopyResult.class).where(CopyResult.TYPE_KEY, "=", currentInspectionType).and(CopyResult.REPORTID, "=", reportId).and(CopyResult.BDZID, "=", currentBdzId).and(CopyResult.DLT, "=", "0"));

        return copyResults;
    }
}
