package com.cnksi.inspe.ui;

import android.bluetooth.BluetoothClass;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.DeviceStandardTypeAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityDeviceDetailsBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.File;
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
    DbModel deviceDbModel;

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
        deviceId = getIntent().getStringExtra("deviceId");
        deviceBigId = getIntent().getStringExtra("deviceBigId");
        taskId = getIntent().getStringExtra("taskId");
        ExecutorManager.executeTaskSerially(() -> {
            try {
                deviceDbModel  = service.getDeviceById(deviceId);
                typeModels = service.getAllDeviceStandardTypeByBigId(deviceBigId);
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                refreshUI();
            });
        });

    }

    /**
     * 刷新设备检查大的类型
     */
    private void refreshUI() {
        String company = TextUtils.isEmpty(deviceDbModel.getString("manufacturer"))?"":deviceDbModel.getString("manufacturer");
        String productNum = TextUtils.isEmpty(deviceDbModel.getString("model"))?"":deviceDbModel.getString("model");
        String date = TextUtils.isEmpty(deviceDbModel.getString("commissioning_date"))?"":deviceDbModel.getString("commissioning_date");
        detailsBinding.tvManufacturers.setText("生产厂家:"+company);
        detailsBinding.tvProductModel.setText("产品型号"+productNum);
        String dateFormat = StringUtils.cleanString(date);
        if (dateFormat.length() > 10) {
            dateFormat = dateFormat.substring(0, 10);
        }
        detailsBinding.tvProductDate.setText("投产日期：" + dateFormat);
        String picPath = TextUtils.isEmpty(deviceDbModel.getString("change_pic")) ? (TextUtils.isEmpty(deviceDbModel.getString("pic")) ? "" : deviceDbModel.getString("pic")) : deviceDbModel.getString("pic");
        detailsBinding.ivDeviceImage.setImageBitmap(BitmapUtils.getImageThumbnail(getPic(picPath), 480, 330));
        typeAdapter = new DeviceStandardTypeAdapter(R.layout.inspe_item_device_part_, typeModels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.devicePartRecy.setLayoutManager(layoutManager);
        detailsBinding.devicePartRecy.setAdapter(typeAdapter);
        typeAdapter.setOnItemClickListener(this);
    }

    // 判断设备的图片是否存在，不存在采用默认的图片
    public  String getPic(String pic) {
        if (!TextUtils.isEmpty(pic) && new File(Config.BDZ_INSPECTION_FOLDER + pic).exists()) {
            pic = Config.BDZ_INSPECTION_FOLDER + pic;
        } else {
            pic = Config.DEFALUTFOLDER + "device_pic.png";
        }
        return pic;
    }

    @Override
    public void onItemClick(View view, Object item, int position) {

    }
}
