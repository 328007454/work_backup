package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzjlBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.view.gztz.BhdzjlGroup;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 故障保护动作记录第三页
 *
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 10:50
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BHDZJLActivity extends BaseActivity {
    ActivityGztzBhdzjlBinding binding;
    List<BhdzjlGroup> groups = new ArrayList<>();
    BhdzjlGroup selectGroup;
    SbjcGztzjl sbjcGztzjl;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentValue();
        binding = ActivityGztzBhdzjlBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        setTitleText(currentBdzName + "保护动作记录");
        initView();
    }

    private void initView() {
        binding.btnNext.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(_this, GZTZRecordActivity.class);
            startActivity(intent);
        });
        addOtherDevice();
    }

    private void initData(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirstLoad = false;
    }

    private void saveData() {

    }

    /**
     * 点击设备名称后的+号则增加一次设备整体布局
     */
    public void addOtherDevice() {
        BhdzjlGroup group = new BhdzjlGroup(this, binding.itemDevice);
        group.setListener((group1, isBhsb) -> {
            selectGroup = group1;
            Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
            intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.one);
            intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
            startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE + (isBhsb ? 1 : 0));
        });
        groups.add(group);
    }

    public void removeView(BhdzjlGroup v) {
        groups.remove(v);
        binding.itemDevice.removeView(v.getRoot());
    }


    public void rebuildStr() {
        if (!isFirstLoad) {
            StringBuilder bhdzqk = new StringBuilder();
            bhdzqk.append(currentBdzName).append("XXX线路").append("故障,");
            for (BhdzjlGroup group : groups) {
                bhdzqk.append(group.toString());
            }
            if (sbjcGztzjl.isTz()) {
                bhdzqk.append("开关跳闸，").append(sbjcGztzjl.chzdzqk).append(",");
            } else bhdzqk.append("开关未跳闸，");
            StringBuilder gzjt = new StringBuilder(bhdzqk.toString());

            bhdzqk.append("二次故障电流").append(sbjcGztzjl.ecgzdl).append("A，")
                    .append("一次故障电流").append(sbjcGztzjl.gzdl).append("KA");

            gzjt.append("故障相别").append(sbjcGztzjl.sbxb);
            gzjt.append("故障一次电流").append(sbjcGztzjl.gzdl).append("A，")
                    .append("二次值").append(sbjcGztzjl.ecgzdl).append("KA,");
            gzjt.append(sbjcGztzjl.getXbGzjt());
            gzjt.append("累积故障电流").append(sbjcGztzjl.ljz).append("KA,");
            gzjt.append(sbjcGztzjl.getXbLjGzjt());
            binding.bhdzqk.setValueStr(bhdzqk.toString());
            binding.gzjt.setValueStr(gzjt.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        selectGroup.setDeviceSelectValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                    }
                    rebuildStr();
                    break;
                //选择保护设备
                case Config.ACTIVITY_CHOSE_DEVICE + 1:
                    model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        selectGroup.setBHDeviceSelectValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                    }
                    rebuildStr();
                    break;
            }
        }
    }
}


