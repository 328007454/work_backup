package com.cnksi.defect.fragment;

import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.listener.ItemClickOrLongClickListener;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.core.fragment.BaseCoreFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K on 2018/5/31.
 * @decrption 缺陷fragment基类
 */

public class BaseDefectFragment extends BaseCoreFragment implements ItemClickOrLongClickListener<DefectRecord> {
    protected String deviceId;
    protected String bdzId;
    protected List<DefectRecord> defectRecords = new ArrayList<>();
    protected DefectRecord selectDefect;
    private boolean hasStandardSwitchDefectId;
    /**
     * 查询该设备下的所有缺陷还是单个缺陷
     */
    protected String defectCount;
    protected String defectId;
    private String currentReportId;
    private String standardId;

    /**
     * 缺陷的顶级目录
     */
    protected String picParentFolder = Config.RESULT_PICTURES_FOLDER;

    @Override
    public int getFragmentLayout() {
        return 0;
    }

    protected void setClickDefectData(DefectRecord record) {

    }


    @Override
    protected void lazyLoad() {
        List<DefectRecord> records = null;
        if (TextUtils.equals(defectCount, Config.SINGLE) && !hasStandardSwitchDefectId) {
            records = DefectRecordService.getInstance().queryDefectByDefectId(defectId, bdzId);
        } else if (hasStandardSwitchDefectId) {
            records = DefectRecordService.getInstance().queryDefectSwitchOverId(currentReportId, standardId);
        } else {
            records = DefectRecordService.getInstance().queryDefectByDeviceid(deviceId, bdzId);
        }
        defectRecords.clear();
        if (records != null) {
            defectRecords.addAll(records);
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        deviceId = getArguments().getString(Device.DEVICEID);
        bdzId = getArguments().getString(Bdz.BDZID);
        defectCount = getArguments().getString(Config.DEFECT_COUNT_KEY);
        defectId = getArguments().getString(DefectRecord.DEFECTID);
        hasStandardSwitchDefectId = getArguments().getBoolean(Config.SWITCH_DEFECT);
        standardId = getArguments().getString(Config.CURRENT_STANDARD_ID);
        currentReportId = getArguments().getString(Config.CURRENT_REPORT_ID);
    }


    @Override
    public void onClick(View v, DefectRecord data, int position) {
        selectDefect = data;
        setClickDefectData(data);
    }

    @Override
    public void onLongClick(View v, DefectRecord defectRecord, int position) {

    }

}
