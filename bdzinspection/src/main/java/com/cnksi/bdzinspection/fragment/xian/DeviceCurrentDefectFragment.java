package com.cnksi.bdzinspection.fragment.xian;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.xian.DeviceDefectAdapter;
import com.cnksi.bdzinspection.databinding.XFragmentDeviceDefectBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.defect.activity.OperateDefectActivity;

import java.util.List;

/**
 * @author Mr.K  on 2018/7/7.
 */

public class DeviceCurrentDefectFragment extends BaseFragment {
    private XFragmentDeviceDefectBinding deviceDefectBinding;
    private DeviceDefectAdapter deviceDefectAdapter;
    private List<DefectRecord> defectRecords;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deviceDefectBinding = XFragmentDeviceDefectBinding.inflate(getLayoutInflater());
        initView();
        searData();
        return deviceDefectBinding.getRoot();

    }

    private void searData() {
        ExecutorManager.executeTaskSerially(() -> {
            defectRecords = DefectRecordService.getInstance().findDefectByDeviceId(currentDeviceId);
            mActivity.runOnUiThread(() -> {
                if (defectRecords != null) {
                    deviceDefectAdapter = new DeviceDefectAdapter(R.layout.x_item_device_defect, defectRecords);
                    deviceDefectBinding.rcyDeviceDefect.setLayoutManager(new LinearLayoutManager(getContext()));
                    deviceDefectBinding.rcyDeviceDefect.setAdapter(deviceDefectAdapter);
                }
                initItemClickCallBack();
            });
        });

    }

    private void initView() {
        getBundleValue();

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
           searData();
        }
        isFirstLoad = false;

    }

    private void initItemClickCallBack() {
        if (deviceDefectAdapter != null) {
            deviceDefectAdapter.setOnItemClickListener((adapter, view, position) -> {
                DefectRecord data = (DefectRecord) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), OperateDefectActivity.class);
                intent.putExtra(Device.DEVICEID, data.deviceid);
                intent.putExtra(Bdz.BDZID, data.bdzid);
                intent.putExtra(Config.DEFECT_COUNT_KEY, Config.SINGLE);
                intent.putExtra(DefectRecord.DEFECTID, data.defectid);
                intent.putExtra(Config.CURRENT_REPORT_ID, data.reportid);
                mActivity.startActivity(intent);
            });
        }
    }
}
