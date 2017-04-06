package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.CopyResult;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;
import java.util.UUID;

public class CopyResultService extends BaseService<CopyResult> {
    public static CopyResultService mInstance;

    private CopyResultService() {
        super(CopyResult.class);
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
            }
            super.saveOrUpdate(copyResult);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public CopyResult getResult(String reportId, String itemId) {
        try {
            Selector<CopyResult> selector = selector().and(CopyResult.REPORTID, "=", reportId)
                    .and(CopyResult.ITEM_ID, "=", itemId).orderBy(CopyResult.UPDATE_TIME);
            return selector.findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询设备先最新的抄录项
     *
     * @param deviceId
     * @return
     */
    public List<CopyResult> getResultList(String reportId, String deviceId, boolean inReport, String type) {
        try {
            String operate = (inReport) ? "=" : "!=";
            Selector<CopyResult> selector = selector().and(CopyResult.DEVICEID, "=", deviceId)
                    .and(CopyResult.REPORTID, operate, reportId).
                            expr(" and " + CopyItem.TYPE_KEY + " in('" + type + "')  and create_time in ( select max(create_time) from copy_result where dlt='0' group by item_id)  group by " + CopyResult.ITEM_ID + " order by " + CopyResult.UPDATE_TIME + "," + CopyResult.CREATE_TIME);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DbModel> getCopyDeviceIdList(String type, String reportId) {
        try {
            String sql = "select c.deviceId from("
                    + "SELECT deviceid,count(CASE WHEN val = '' THEN NULL ELSE val END) + count(CASE WHEN val_a = '' THEN NULL ELSE val_a END) " +
                    "+ count(CASE WHEN val_b = '' THEN NULL ELSE val_b END) + count(CASE WHEN val_c = '' THEN NULL ELSE val_c END) +" +
                    " count(CASE WHEN val_o = '' THEN NULL ELSE val_o END) count FROM copy_result WHERE reportid = ? and dlt='0' GROUP BY deviceid) c "
                    + "LEFT JOIN (SELECT item.deviceid,count(CASE WHEN val = 'Y' THEN 1 ELSE NULL END) +"
                    + "count(CASE WHEN val_a = 'Y' THEN 1 ELSE NULL END)+"
                    + "count(CASE WHEN val_b = 'Y' THEN 1 ELSE NULL END)+"
                    + "count(CASE WHEN val_c = 'Y' THEN 1 ELSE NULL END)+"
                    + "count(CASE WHEN val_o = 'Y' THEN 1 ELSE NULL END)"
                    + "AS copyCount FROM copy_item item where item.dlt='0' and item.type_key in ('" + type + "') GROUP BY item.deviceid"
                    + ") t ON t.deviceid = c.deviceid WHERE t.copyCount = c.count ";
            SqlInfo sqlInfo = new SqlInfo(sql);
            sqlInfo.addBindArg(new KeyValue("", reportId));
            return findDbModelAll(sqlInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getReportCopyCount(String reportId) {
        String sql = "select count(1) as count" + " from copy_result result WHERE result.dlt='0' and result.reportid=? and ( result.val is not null or result.val_a is not null or result.val_b is not null or result.val_c is not null or result.val_o is not null)";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", reportId));
        try {
            DbModel dbMode = findDbModelFirst(sqlInfo);
            return dbMode.getLong("count");
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 查询差动保护差流的记录值
     */
    public List<CopyResult> getDifferentialRecord(String bdzId, String reportId) throws DbException {

        return selector().and(CopyResult.REPORTID, "=", reportId)
                .and(CopyResult.BDZID, "=", bdzId).orderBy(CopyResult.DEVICEID, false).findAll();
    }
}
