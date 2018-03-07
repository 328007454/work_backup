package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.databinding.ActivityGztzBaseBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/6 15:22
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TZQKActivity extends BaseActivity {
    ActivityGztzBaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGztzBaseBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        setTitleText("开关故障跳闸记录");
        getIntentValue();
        initView();
    }

    private void initView() {
        binding.kgdlqbh.setSelectOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
                intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.one);
                intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
                startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE);
            }
        });
        binding.gzdydj.setType("dydj");
        binding.gzlx.setType("gzlx");
        binding.gzsdtq.setType("gzsdtq");
        binding.gzlb.setType("gzlb");
        binding.tyfw.setType("gztyfw");
        binding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_this, BHDZQKActivity.class);
                startActivity(intent);
            }
        });
    }


    public void save() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        binding.kgdlqbh.setKeyValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                    }
                    break;
            }
        }
    }
}
