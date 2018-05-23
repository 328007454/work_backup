package com.cnksi.sjjc.processor;

import android.app.Activity;
import android.content.Intent;

import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.model.Device;
import com.cnksi.sjjc.activity.CopyAllValueActivity3;
import com.cnksi.sjjc.bean.Standards;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wastrel on 2016/6/14.
 */
public class ArresterCheckOLProcessor extends CopyDataInterface {

    private static final String filter = "电流";

    public ArresterCheckOLProcessor(String reportId) {
        super(reportId);
    }

    @Override
    public String getCopyType() {
        return "blqdzcs_dzcs','blqdzcs_xldlz";
    }

    @Override
    public List<DbModel> findAllDeviceHasCopyValue(String currentFunctionModel, String bdzId) throws DbException {
        String selector = "and deviceid in (SELECT DISTINCT(deviceid) FROM copy_item WHERE type_key in('" + getCopyType() + "') and dlt = '0' )";
        return DeviceService.getInstance().findDeviceHasCopyValueBySelector(selector, bdzId);
    }


    @Override
    public void gotoInspection(Activity activity) {
        activity.startActivity(new Intent(activity, CopyAllValueActivity3.class));
    }

    @Override
    public String getCopyResult(String bdzId) {
        long copyCount = CopyResultService.getInstance().getReportCopyCount(reportId);
        long totalCount = CopyItemService.getInstance().getCopyItemCount(bdzId, getCopyType());
        return copyCount + "/" + totalCount;
    }


    @Override
    public HashMap<String, Boolean> getCopyMap(String bdzId, String type) {
        HashMap<String, Boolean> copyMap = new HashMap<String, Boolean>();
        try {
            List<DbModel> dataList = DeviceService.getInstance().getCopyDeviceDbModels(reportId, bdzId, type, filter);
            if (dataList != null) {
                for (DbModel m : dataList) {
                    copyMap.put(m.getString(Device.DEVICEID), true);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return copyMap;
    }


    @Override
    public List<DbModel> findCopyStandardListByDevicePartId(String deviceId, String duid) {
        List<DbModel> modelList = super.findCopyStandardListByDevicePartId(deviceId, duid);
        if (modelList != null) {
            Iterator<DbModel> iterator = modelList.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next().getString(Standards.DESCRIPTION);
                if (str == null || !str.contains(filter)) {
                    iterator.remove();
                }
            }
        }
        return modelList;
    }

    @Override
    public String getFinishString() {
        return "完成检测";
    }
}
