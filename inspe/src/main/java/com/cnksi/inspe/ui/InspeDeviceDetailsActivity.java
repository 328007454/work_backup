package com.cnksi.inspe.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.DeviceStandardTypeAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityDeviceDetailsBinding;
import com.cnksi.inspe.db.DeviceService;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 精益化设备详情界面
 * Created by Mr.K on 2018/4/9.
 */

public class InspeDeviceDetailsActivity extends AppBaseActivity implements DeviceStandardTypeAdapter.OnItemClickListener {
    ActivityDeviceDetailsBinding detailsBinding;
    DeviceStandardTypeAdapter typeAdapter;
    List<DbModel> typeModels = new ArrayList<>();
    String deviceId;
    String deviceBigId;
    String taskId;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_details;
    }

    @Override
    public void initUI() {
        detailsBinding = (ActivityDeviceDetailsBinding) rootDataBinding;
        detailsBinding.includeInspeTitle.toolbarTitle.setText(getIntent().getStringExtra("deviceName"));
        detailsBinding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);


    }

    @Override
    public void initData() {
        DeviceService service = new DeviceService();
        deviceId = getIntent().getStringExtra("deviceid");
        deviceBigId = getIntent().getStringExtra("deviceBigId");
        taskId = getIntent().getStringExtra("taskId");
        ExecutorManager.executeTaskSerially(() -> {
            try {
                typeModels = service.getAllDeviceStandardTypeByBigId(deviceBigId);
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                refreshAdapter();

            });
        });

    }

    /**
     * 刷新设备检查大的类型
     */
    private void refreshAdapter() {
        typeAdapter = new DeviceStandardTypeAdapter(R.layout.inspe_item_device_part_, typeModels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.devicePartRecy.setLayoutManager(layoutManager);
        detailsBinding.devicePartRecy.setAdapter(typeAdapter);
        typeAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, Object item, int position) {

    }
}
