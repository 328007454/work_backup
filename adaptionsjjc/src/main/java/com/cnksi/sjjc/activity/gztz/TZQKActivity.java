package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjlKgtzjl;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjlSbgzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBaseBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.gztz.GZTZKgtzjlService;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;

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
    SbjcGztzjlKgtzjl kgtzjl;
    SbjcGztzjlSbgzjl sbgzjl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGztzBaseBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        setTitleText("开关故障跳闸记录");
        getIntentValue();
        initView();
        initData();
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
        binding.gztysb.setSelectOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
                intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.one);
                intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
                startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE + 1);
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
        binding.sfty.setOnCheckChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tyfw.setVisibility(View.GONE);
                    binding.gztysb.setVisibility(View.GONE);
                } else {
                    binding.tyfw.setVisibility(View.VISIBLE);
                    binding.gztysb.setVisibility(View.VISIBLE);
                }
            }
        }, true);
    }

    private void initData() {
        SbjcGztzjlKgtzjl kgtzjl = GZTZKgtzjlService.getInstance().findByReportId(currentReportId);
        if (kgtzjl == null) kgtzjl = SbjcGztzjlKgtzjl.create(currentReportId, currentBdzId);
        SbjcGztzjlSbgzjl sbgzjl = GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
        if (sbgzjl == null) sbgzjl = SbjcGztzjlSbgzjl.create(currentReportId);

        binding.kgdlqbh.setKeyValue(new KeyValue(kgtzjl.dlqbh, kgtzjl.dlqmc));
        binding.dlqtzqk.setValueStr(sbgzjl.bh_dlqtzqk);
        binding.yyjjcqk.setValueStr(sbgzjl.bh_yyjjcqk);
        binding.sbxb.setValueStr(kgtzjl.sbxb);
        binding.sfdz.setValueStr(kgtzjl.sfdz);
        binding.kgdzpj.setValueStr(kgtzjl.dzpj);
        binding.sfzngz.setValueStr(sbgzjl.sfzngz);
        binding.gzfssj.setValueStr(sbgzjl.gzfssj);
        binding.gzdydj.setKeyValue(new KeyValue(sbgzjl.gzdydjK, sbgzjl.gzdydj));
        binding.gzlx.setKeyValue(new KeyValue(sbgzjl.gzlxK, sbgzjl.gzlx));
        binding.gzsdtq.setKeyValue(new KeyValue("", sbgzjl.gzsdtq));
        binding.gzsfyj.setValueStr(sbgzjl.gzsfyj);
        binding.gzlb.setKeyValue(new KeyValue(sbgzjl.gzlbK, sbgzjl.gzlb));
        binding.sftz.setValueStr(sbgzjl.sftz);
        binding.sfty.setValueStr(sbgzjl.sfty);
        binding.tyfw.setKeyValue(new KeyValue(sbgzjl.tyfwK, sbgzjl.tyfw));
        binding.gztysb.setKeyValue(new KeyValue(sbgzjl.gztysbK, sbgzjl.gztysb));
        binding.dlqjcqk.setValueStr(kgtzjl.dlqjcqk);
        binding.bz.setValueStr(kgtzjl.bz);
    }

    public void save() {
        KeyValue dlqbh = binding.kgdlqbh.getValue();
        String dlqtzqk = binding.dlqtzqk.getValueStr();
        String yyjjcqk = binding.yyjjcqk.getValueStr();
        String sbxb = binding.sbxb.getValueStr();
        String sfdz = binding.sfdz.getValueStr();
        String kgdzpj = binding.kgdzpj.getValueStr();
        String sfzngz = binding.sfzngz.getValueStr();
        String gzfssj = binding.gzfssj.getValueStr();
        KeyValue gzdydj = binding.gzdydj.getValue();
        KeyValue gzlx = binding.gzlx.getValue();
        KeyValue gzsdtq = binding.gzsdtq.getValue();
        String gzsfyj = binding.gzsfyj.getValueStr();
        KeyValue gzlb = binding.gzlb.getValue();
        String sftz = binding.sftz.getValueStr();
        String sfty = binding.sfty.getValueStr();
        KeyValue tyfw = binding.tyfw.getValue();
        KeyValue gztysb = binding.gztysb.getValue();
        String dlqjcqk = binding.dlqjcqk.getValueStr();
        String bz = binding.bz.getValueStr();
        //todo Check Data

        //开关跳闸
        kgtzjl.dlqbh = dlqbh.key;
        kgtzjl.dlqmc = dlqbh.getValueStr();
        kgtzjl.bz = bz;
        kgtzjl.sbxb = sbxb;
        kgtzjl.sfdz = sfdz;
        kgtzjl.dzpj = kgdzpj;
        kgtzjl.dlqjcqk = dlqjcqk;


        //设备故障记录
        sbgzjl.gzdydj = gzdydj.getValueStr();
        sbgzjl.gzdydjK = gzdydj.key;
        sbgzjl.gzfssj = gzfssj;
        sbgzjl.gzlb = gzlb.getValueStr();
        sbgzjl.gzlbK = gzlb.key;
        sbgzjl.gzlx = gzlx.getValueStr();
        sbgzjl.gzlxK = gzlx.key;
        sbgzjl.sfzngz = sfzngz;
        sbgzjl.gzsdtq = gzsdtq.getValueStr();
        sbgzjl.gzsfyj = gzsfyj;
        sbgzjl.sfty = sfty;
        sbgzjl.sftz = sftz;
        sbgzjl.tyfw = tyfw.getValueStr();
        sbgzjl.tyfwK = tyfw.key;
        sbgzjl.gztysb = gztysb.getValueStr();
        sbgzjl.gztysbK = gztysb.key;
        sbgzjl.bh_yyjjcqk = yyjjcqk;
        sbgzjl.bh_dlqtzqk = dlqtzqk;

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
                case Config.ACTIVITY_CHOSE_DEVICE + 1:
                    model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        binding.gztysb.setKeyValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                    }
                    break;
            }
        }
    }
}
