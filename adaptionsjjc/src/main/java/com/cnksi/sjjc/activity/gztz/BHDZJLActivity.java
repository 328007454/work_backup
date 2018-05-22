package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.common.Config;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.common.model.Device;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjlBhdzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzjlBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.gztz.GZTZBhdzjlService;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;
import com.cnksi.sjjc.view.gztz.BhdzjlGroup;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

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
        loadData();

    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    private void initView() {
        binding.btnNext.setOnClickListener(v -> {
            if (saveData(true)) {
                Intent intent = new Intent(_this, GZTZRecordActivity.class);
                startActivity(intent);
            }
        });
        binding.btnPre.setOnClickListener(v -> onBackPressed());
        addOtherDevice();
    }

    public void loadData() {
        sbjcGztzjl = Cache.GZTZJL != null ? Cache.GZTZJL : GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
        ExecutorManager.executeTaskSerially(() -> {
            List<SbjcGztzjlBhdzjl> bhdzjls = GZTZBhdzjlService.getInstance().findByGzjl(sbjcGztzjl.id);
            runOnUiThread(() -> {
                if (bhdzjls != null && bhdzjls.size() > 0) {
                    groups.get(0).setRecord(bhdzjls.get(0));
                    for (int i = 1; i < bhdzjls.size(); i++) {
                        addOtherDevice().setRecord(bhdzjls.get(i));
                    }
                }
                isFirstLoad = false;
            });
        });
        binding.gzjt.setValueStr(sbjcGztzjl.gzjt);
        binding.bhdzqk.setValueStr(sbjcGztzjl.bhdzqk);
    }


    /**
     * 点击设备名称后的+号则增加一次设备整体布局
     */
    public BhdzjlGroup addOtherDevice() {
        BhdzjlGroup group = new BhdzjlGroup(this, binding.itemDevice);
        group.setListener((group1, isBhsb) -> {
            selectGroup = group1;
            Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
            intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, isBhsb ? PMSDeviceType.one : PMSDeviceType.second);
            intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);

            intentDevices.putExtra(Config.TITLE_NAME, isBhsb ? "请选择一次设备" : "请选择二次设备");
            if (isBhsb) {
                if (group1.getBhsb() != null) {
                    intentDevices.putExtra(AllDeviceListActivity.SPCAEID, group1.getBhsb().getString(Device.SPID));
                }
            } else {
                intentDevices.putExtra(Config.SECOND_SPACE_AND_ONE_DEVICE, true);
            }
            startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE + (isBhsb ? 1 : 0));
        });
        groups.add(group);
        return group;
    }

    public void removeView(BhdzjlGroup v) {
        groups.remove(v);
        binding.itemDevice.removeView(v.getRoot());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData(false);
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
                        selectGroup.setBhsb(model);
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

    private boolean saveData(boolean isCheck) {
        List<SbjcGztzjlBhdzjl> rs = new ArrayList<>();
        for (BhdzjlGroup group : groups) {
            SbjcGztzjlBhdzjl temp = group.getRecord();
            if (temp != null) {
                rs.add(temp);
            }
        }
        int i = 0;
        for (SbjcGztzjlBhdzjl r : rs) {
            r.gztzjlId = sbjcGztzjl.id;
            r.reportid = currentReportId;
            if (r.dlt == 0) {
                i++;
            }
        }
        if (i == 0) {
            if (isCheck)
                ToastUtils.showMessage("你至少要选择一组保护设备");
            return false;
        }
        sbjcGztzjl.bhdzqk = binding.bhdzqk.getValueStr();
        sbjcGztzjl.gzjt = binding.gzjt.getValueStr();
        try {
            GZTZSbgzjlService.getInstance().saveOrUpdate(sbjcGztzjl);
            GZTZBhdzjlService.getInstance().saveOrUpdate(rs);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            ToastUtils.showMessage( "保存失败");
        }
        return false;
    }
}


