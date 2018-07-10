package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cnksi.common.Config;
import com.cnksi.common.activity.DeviceSelectActivity;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.activity.BaseSjjcActivity;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBaseBinding;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;
import com.cnksi.sjjc.service.gztz.PmsXianluService;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/3/6 15:22
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Route(path = "/tzqk/activity")
public class TZQKActivity extends BaseSjjcActivity {
    ActivityGztzBaseBinding binding;
    SbjcGztzjl sbgzjl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGztzBaseBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        setTitleText("开关故障跳闸记录");
        getIntentValue();
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
        binding.kgdlqbh.setSelectOnClickListener(v -> {
            List<String> bigIds = DeviceService.getInstance().findBigId("DLQ");
            if (bigIds.isEmpty()) {
                ToastUtils.showMessage("没有找到别名为DLQ的设备大类！");
            }
            DeviceSelectActivity.with(mActivity)
                    .setTitle("选择断路器")
                    .setPmsDeviceType(PMSDeviceType.one)
                    .setBdzId(currentBdzId)
                    .setDeviceBigType(bigIds.toArray(new String[]{}))
                    .setRequestCode(Config.ACTIVITY_CHOSE_DEVICE)
                    .start();
        });
        binding.gztysb.setSelectOnClickListener(v -> {
            KeyValue keyValue = binding.tyfw.getValue();
            if (keyValue == null) {
                ToastUtils.showMessage("请先选择停运范围！");
                return;
            }
            if ("全站".equals(keyValue.getValueStr())) {
                ToastUtils.showMessage("全站设备均停运，无需选择！");
                return;
            }
            List<String> bigIds = new ArrayList<>();
            if ("主变".equals(keyValue.getValueStr())) {
                List<String> s1 = DeviceService.getInstance().findBigId("BYQ");
                List<String> s2 = DeviceService.getInstance().findBigId("ZYB");
                bigIds.addAll(s1);
                bigIds.addAll(s2);
            }
            if (bigIds.isEmpty()) {
                ToastUtils.showMessage("没有找到别名为BYQ、ZYB的设备大类！");
            }
            DeviceSelectActivity.with(mActivity)
                    .setTitle("请选择变压器")
                    .setPmsDeviceType(PMSDeviceType.one)
                    .setBdzId(currentBdzId)
                    .setDeviceBigType(bigIds.toArray(new String[]{}))
                    .setRequestCode(Config.ACTIVITY_CHOSE_DEVICE + 1)
                    .start();

        });
        binding.gzdydj.setType("dydj");
        binding.gzlx.setType("gzlx");
        binding.gzsdtq.setType("gzsdtq");
        binding.gzlb.setType("gzlb");
        binding.tyfw.setType("gztyfw");
        binding.btnSure.setOnClickListener(v -> {
            if (TZQKActivity.this.save(true)) {
                Intent intent = new Intent(mActivity, BHDZQKActivity.class);
                TZQKActivity.this.startActivity(intent);
            }
        });
        binding.sfty.setOnCheckChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.tyfw.setVisibility(View.VISIBLE);
                binding.gztysb.setVisibility(View.VISIBLE);
            } else {
                binding.tyfw.setVisibility(View.GONE);
                binding.gztysb.setVisibility(View.GONE);
            }
        }, true);
        binding.sbxb.setType("sbxb");
        binding.tyfw.setListener(v -> {
            if ("全站".equals(v.getValueStr())) {
                binding.gztysb.setKeyValue(new KeyValue("", ""));
                binding.gztysb.setMustInput(false);
            } else {
                binding.gztysb.setMustInput(true);
            }
        });
        binding.gzxl.setDataProvider(() -> PmsXianluService.getInstance().findXianluByBdz(currentBdzId));
    }

    public void loadData() {
        sbgzjl = GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
        if (sbgzjl == null) {
            sbgzjl = SbjcGztzjl.create(currentReportId, currentBdzId);
        }
        binding.kgdlqbh.setKeyValue(new KeyValue(sbgzjl.dlqbh, sbgzjl.dlqmc));
        binding.dlqtzqk.setValueStr(sbgzjl.bhDlqtzqk);
        binding.yyjjcqk.setValueStr(sbgzjl.bhYyjjcqk);
        binding.sbxb.setKeyValue(new KeyValue(sbgzjl.sbxbK, sbgzjl.sbxb));
        binding.sfdz.setValueStr(sbgzjl.sfdz);
        binding.gzxl.setKeyValue(new KeyValue(sbgzjl.gzxlK, sbgzjl.gzxl));
        binding.kgdzpj.setValueStr(sbgzjl.dzpj);
        binding.sfzngz.setValueStr(sbgzjl.sfzngz);
        binding.gzfssj.setValueStr(sbgzjl.gzfssj);
        binding.gzdydj.setKeyValue(new KeyValue(sbgzjl.gzdydjK, sbgzjl.gzdydj));
        binding.gzlx.setKeyValue(new KeyValue(sbgzjl.gzlxK, sbgzjl.gzlx));
        binding.gzsdtq.setKeyValue(new KeyValue(sbgzjl.gzsdtqK, sbgzjl.gzsdtq));
        binding.gzsfyj.setValueStr(sbgzjl.gzsfyj);
        if (TextUtils.isEmpty(sbgzjl.gzlb)) {
            binding.gzlb.setKeyValue(new KeyValue("01", "交流故障"));
        } else {
            binding.gzlb.setKeyValue(new KeyValue(sbgzjl.gzlbK, sbgzjl.gzlb));
        }
        binding.sftz.setValueStr(sbgzjl.sftz);
        binding.sfty.setValueStr(sbgzjl.sfty);
        binding.tyfw.setKeyValue(new KeyValue(sbgzjl.tyfwK, sbgzjl.tyfw));
        if ("全站".equals(sbgzjl.tyfw)) {
            binding.gztysb.setMustInput(false);
        }
        binding.gztysb.setKeyValue(new KeyValue(sbgzjl.gztysbK, sbgzjl.gztysb));
        binding.dlqjcqk.setValueStr(sbgzjl.dlqjcqk);
        binding.bz.setValueStr(sbgzjl.kgtzBz);
    }

    public static KeyValue NULL = new KeyValue(null, null);

    /**
     * @param isCheck 是否执行检查 返回时不应执行检查 而是直接存储
     * @return
     */
    private boolean save(boolean isCheck) {

        KeyValue dlqbh = binding.kgdlqbh.getValue();
        String dlqtzqk = binding.dlqtzqk.getValueStr();
        String yyjjcqk = binding.yyjjcqk.getValueStr();
        KeyValue sbxb = binding.sbxb.getValue();
        String sfdz = binding.sfdz.getValueStr();
        String kgdzpj = binding.kgdzpj.getValueStr();
        String sfzngz = binding.sfzngz.getValueStr();
        String gzfssj = binding.gzfssj.getValueStr();
        KeyValue gzxl = binding.gzxl.getValue();
        KeyValue gzdydj = binding.gzdydj.getValue();
        KeyValue gzlx = binding.gzlx.getValue();
        KeyValue gzsdtq = binding.gzsdtq.getValue();
        String gzsfyj = binding.gzsfyj.getValueStr();
        KeyValue gzlb = binding.gzlb.getValue();
        String sftz = binding.sftz.getValueStr();
        String sfty = binding.sfty.getValueStr();
        String dlqjcqk = binding.dlqjcqk.getValueStr();
        if (isCheck) {
            if (dlqbh == null || sbxb == null || gzxl == null || StringUtilsExt.isHasOneEmpty(dlqtzqk, yyjjcqk, sfdz, kgdzpj, sfzngz, gzfssj)) {
                ToastUtils.showMessage("请检查带星号的项目是否均已填写！");
                return false;
            }
            if (gzdydj == null || gzlx == null || gzsdtq == null || gzlb == null || StringUtilsExt.isHasOneEmpty(gzsfyj, sfty, sftz, dlqjcqk)) {
                ToastUtils.showMessage("请检查带星号的项目是否均已填写！");
                return false;
            }
        } else {
            dlqbh = nullTo(dlqbh);
            gzdydj = nullTo(gzdydj);
            gzlx = nullTo(gzlx);
            gzsdtq = nullTo(gzsdtq);
            gzlb = nullTo(gzlb);
            sbxb = nullTo(sbxb);
            gzxl = nullTo(gzxl);
        }
        KeyValue tyfw = null;
        KeyValue gztysb = null;
        if ("是".equals(sfty)) {
            tyfw = binding.tyfw.getValue();
            gztysb = binding.gztysb.getValue();
            if (isCheck) {
                if (tyfw == null || (!"全站".equals(tyfw.getValueStr()) && gztysb == null)) {
                    ToastUtils.showMessage("请检查带星号的项目是否均已填写！");
                    return false;
                }
            }
        }
        tyfw = nullTo(tyfw);
        gztysb = nullTo(gztysb);
        String bz = binding.bz.getValueStr();


        //开关跳闸

        sbgzjl.dlqbh = dlqbh.key;
        sbgzjl.dlqmc = dlqbh.getValueStr();
        sbgzjl.kgtzBz = bz;
        sbgzjl.sbxb = sbxb.getValueStr();
        sbgzjl.sbxbK = sbxb.key;
        sbgzjl.sfdz = sfdz;
        sbgzjl.dzpj = kgdzpj;
        sbgzjl.dlqjcqk = dlqjcqk;


        //设备故障记录

        sbgzjl.gzdydj = gzdydj.getValueStr();
        sbgzjl.gzdydjK = gzdydj.key;
        sbgzjl.gzfssj = gzfssj;
        sbgzjl.gzlb = gzlb.getValueStr();
        sbgzjl.gzlbK = gzlb.key;
        sbgzjl.gzlx = gzlx.getValueStr();
        sbgzjl.gzlxK = gzlx.key;
        sbgzjl.gzxl = gzxl.getValueStr();
        sbgzjl.gzxlK = gzxl.key;
        sbgzjl.sfzngz = sfzngz;
        sbgzjl.gzsdtq = gzsdtq.getValueStr();
        sbgzjl.gzsdtqK = gzsdtq.key;
        sbgzjl.gzsfyj = gzsfyj;
        sbgzjl.sfty = sfty;
        sbgzjl.sftz = sftz;
        sbgzjl.tyfw = tyfw.getValueStr();
        sbgzjl.tyfwK = tyfw.key;
        sbgzjl.gztysb = gztysb.getValueStr();
        sbgzjl.gztysbK = gztysb.key;
        sbgzjl.bhYyjjcqk = yyjjcqk;
        sbgzjl.bhDlqtzqk = dlqtzqk;
        try {
            GZTZSbgzjlService.getInstance().saveOrUpdate(sbgzjl);
            Cache.GZTZJL = sbgzjl;
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save(false);
    }

    private KeyValue nullTo(KeyValue keyValue) {
        if (keyValue == null) {
            return NULL;
        } else {
            return keyValue;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) data.getSerializableExtra(DeviceSelectActivity.RESULT_SELECT_KEY);
                    if (model != null) {
                        binding.kgdlqbh.setKeyValue(new KeyValue(model.getString(DeviceService.DEVICE_ID_KEY),
                                model.getString(DeviceService.DEVICE_NAME_KEY)));
                        binding.dlqtzqk.setValueStr(model.getString(DeviceService.DEVICE_NAME_KEY) + "跳闸");
                    }
                    break;
                case Config.ACTIVITY_CHOSE_DEVICE + 1:
                    model = (DbModel) data.getSerializableExtra(DeviceSelectActivity.RESULT_SELECT_KEY);
                    if (model != null) {
                        binding.gztysb.setKeyValue(new KeyValue(model.getString(DeviceService.DEVICE_ID_KEY)
                                , model.getString(DeviceService.DEVICE_NAME_KEY)));
                    }
                    break;
                default:
            }
        }
    }
}
