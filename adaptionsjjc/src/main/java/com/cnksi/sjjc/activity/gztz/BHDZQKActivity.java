package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.model.Device;
import com.cnksi.common.utils.CalcUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzqkBinding;
import com.cnksi.sjjc.inter.SimpleTextWatcher;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.sjjc.activity.gztz.TZQKActivity.NULL;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 10:15
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BHDZQKActivity extends BaseActivity {
    ActivityGztzBhdzqkBinding binding;
    private SbjcGztzjl sbjcGztzjl;
    private String imageName;
    private List<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGztzBhdzqkBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        setTitleText(currentBdzName + "保护动作情况");
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
            if (BHDZQKActivity.this.save(true)) {
                Intent intent = new Intent(_this, BHDZJLActivity.class);
                BHDZQKActivity.this.startActivity(intent);
            }
        });
        binding.btnPre.setOnClickListener(v -> BHDZQKActivity.this.onBackPressed());
        binding.chzdzqk.setType("chzdzqk");
        binding.bhmc.setType("bhmc");
        binding.gxtzcsA.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.ljtzcs.addA(CalcUtils.toInt(s.toString(), 0));
            }
        });
        binding.gxtzcsB.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.ljtzcs.addB(CalcUtils.toInt(s.toString(), 0));
            }
        });
        binding.gxtzcsC.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.ljtzcs.addC(CalcUtils.toInt(s.toString(), 0));
            }
        });
        binding.gxtzcsO.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.ljtzcs.addO(CalcUtils.toInt(s.toString(), 0));
            }
        });

        binding.ivTakePic.setOnClickListener(v -> FunctionUtil.takePicture(BHDZQKActivity.this, imageName = FunctionUtil.getCurrentImageName(BHDZQKActivity.this), Config.RESULT_PICTURES_FOLDER));
        binding.ivShowPic.setOnClickListener(v -> BHDZQKActivity.this.showImageDetails(BHDZQKActivity.this, StringUtils.addStrToListItem(photos, Config.RESULT_PICTURES_FOLDER), true));
        binding.bhsbmc.setSelectOnClickListener(v -> {
            Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
            intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.second);
            intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
            intentDevices.putExtra(Config.TITLE_NAME, "请选择二次设备");
            BHDZQKActivity.this.startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE);
        });
        binding.gzlbqmc.setSelectOnClickListener(v -> {
            Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
            intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.second);
            intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
            String bigIds = DeviceService.getInstance().findBigId("GZLBQ");
            if (bigIds != null) {
                intentDevices.putExtra(AllDeviceListActivity.BIGID, bigIds);
                intentDevices.putExtra(Config.TITLE_NAME, "请选择故障录波器");
            } else {
                ToastUtils.showMessage("没有找到别名为GZLBQ的设备大类！");
            }
            BHDZQKActivity.this.startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE + 1);
        });
    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            sbjcGztzjl = Cache.GZTZJL != null ? Cache.GZTZJL : GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
            SbjcGztzjl last = GZTZSbgzjlService.getInstance().findLastByDeviceId(sbjcGztzjl.dlqbh, currentReportId);
            BHDZQKActivity.this.runOnUiThread(() -> {
                if (last != null) {
                    String s = last.ljtzcs;
                    if (!TextUtils.isEmpty(s)) {
                        binding.ljtzcs.setValuesStr(s);
                    }
                    if (!TextUtils.isEmpty(last.ljz)) {
                        binding.gzdl.setLjz(CalcUtils.String2Float(last.ljz));
                    }
                }
                binding.chzdzqk.setVisibility(sbjcGztzjl.isTz() ? View.VISIBLE : View.GONE);

                if (!TextUtils.isEmpty(sbjcGztzjl.dzbhFj)) {
                    photos = Arrays.asList(StringUtils.NullToDefault(sbjcGztzjl.dzbhFj).split(","));
                } else {
                    photos = new ArrayList<>();
                }
                BHDZQKActivity.this.showPic();

                //处理ABCO 相别
                int[] visbles = sbjcGztzjl.getXb();
                binding.ljtzcs.setXb(visbles);
                binding.llA.setVisibility(visbles[0] == 1 ? View.VISIBLE : View.GONE);
                binding.llB.setVisibility(visbles[1] == 1 ? View.VISIBLE : View.GONE);
                binding.llC.setVisibility(visbles[2] == 1 ? View.VISIBLE : View.GONE);
                binding.llO.setVisibility(visbles[3] == 1 ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(sbjcGztzjl.gxtzcs)) {
                    JSONObject object = JSON.parseObject(sbjcGztzjl.gxtzcs);
                    binding.gxtzcsA.setText(object.getString("A"));
                    binding.gxtzcsB.setText(object.getString("B"));
                    binding.gxtzcsC.setText(object.getString("C"));
                    binding.gxtzcsO.setText(object.getString("O"));
                }
                if (!TextUtils.isEmpty(sbjcGztzjl.ljtzcs)) {
                    binding.ljtzcs.setValuesStr(sbjcGztzjl.ljtzcs);
                }
                if (!TextUtils.isEmpty(sbjcGztzjl.gzdl)) {
                    binding.gzdl.setGzdl(CalcUtils.String2Float(sbjcGztzjl.gzdl));
                }
                if (!TextUtils.isEmpty(sbjcGztzjl.ljz)) {
                    binding.gzdl.setLjz(CalcUtils.String2Float(sbjcGztzjl.ljz));
                }


                binding.chzdzqk.setKeyValue(new KeyValue(sbjcGztzjl.chzdzqkK, sbjcGztzjl.chzdzqk));
                binding.ecgzdl.setText(StringUtils.NullToDefault(sbjcGztzjl.ecgzdl));
                binding.hfsdsj.setValueStr(sbjcGztzjl.hfsdsj);
                binding.zhycdxsj.setValueStr(sbjcGztzjl.zhycdxsj);
                binding.bhsbmc.setKeyValue(new KeyValue(sbjcGztzjl.bhsbmcK, sbjcGztzjl.bhsbmc));
                binding.bhmc.setKeyValue(new KeyValue(sbjcGztzjl.bhmcK, sbjcGztzjl.bhmc));
                binding.dzsj.setValueStr(sbjcGztzjl.bhdzsj);
                binding.zdq.setText(sbjcGztzjl.zdq);
                binding.ldkgqk.setValueStr(sbjcGztzjl.ldkgqk);
                binding.yqtbhph.setValueStr(sbjcGztzjl.yqtbhph);
                binding.ylbzzph.setValueStr(sbjcGztzjl.ylbzzph);
                binding.yjkxtph.setValueStr(sbjcGztzjl.yjkxtph);
                binding.etBz.setValueStr(sbjcGztzjl.dzbhBz);
                binding.gzlbqmc.setKeyValue(new KeyValue(sbjcGztzjl.gzGzlbqmcK, sbjcGztzjl.gzGzlbqmc));
                binding.gzlbqfx.setValueStr(sbjcGztzjl.gzGzlbfx);
                binding.gzlbqcj.setValueStr(sbjcGztzjl.gzGzlbcj);
                if (null != binding.gzlbqmc.getValue() && !TextUtils.isEmpty(binding.gzlbqmc.getValue().getValueStr())) {
                    binding.gzlbqfx.setMustInput(true);
                    binding.gzlbqcj.setMustInput(true);
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save(false);
    }

    private boolean save(boolean isCheck) {
        KeyValue chtzql = binding.chzdzqk.getValue();
        String a = getText(binding.gxtzcsA), b = getText(binding.gxtzcsB), c = getText(binding.gxtzcsC), o = getText(binding.gxtzcsO);
        String gxtzcs;
        if (!sbjcGztzjl.checkXbTzcs(a, b, c, o)) {
            if (isCheck) {
                ToastUtils.showMessage("请填写各项跳闸次数！");
                return false;
            } else {
                gxtzcs = null;
            }
        } else {
            HashMap<String, Integer> temp = new HashMap<>();
            temp.put("A", CalcUtils.toInt(a, 0));
            temp.put("B", CalcUtils.toInt(b, 0));
            temp.put("C", CalcUtils.toInt(c, 0));
            temp.put("O", CalcUtils.toInt(o, 0));
            gxtzcs = JSON.toJSONString(temp);
        }
        String ljtzcs = binding.ljtzcs.getValuesStr();
        String gzdl = binding.gzdl.getGzdl();
        String ljz = binding.gzdl.getLjz();
        String ecgzdl = getText(binding.ecgzdl);

        String hfsdsj = binding.hfsdsj.getValueStr();
        String zhycdxsj = binding.zhycdxsj.getValueStr();

        KeyValue bhsbmc = binding.bhsbmc.getValue();
        KeyValue bhmc = binding.bhmc.getValue();
        String dzsj = binding.dzsj.getValueStr();
        String zdq = getText(binding.zdq);
        String ldkgql = binding.ldkgqk.getValueStr();
        if (isCheck) {
            if ((chtzql == null && sbjcGztzjl.isTz()) || bhmc == null || bhsbmc == null || StringUtilsExt.isHasOneEmpty(gzdl, ljz, ecgzdl, dzsj, zdq, ldkgql)) {
                ToastUtils.showMessage("请检查带星号的项目是否均已填写！");
                return false;
            }
            chtzql = nullTo(chtzql);
        } else {
            chtzql = nullTo(chtzql);
            bhmc = nullTo(bhmc);
            bhsbmc = nullTo(bhsbmc);
        }
        String yqtbhph = binding.yqtbhph.getValueStr();
        String ylbzzph = binding.ylbzzph.getValueStr();
        String yjkxtph = binding.yjkxtph.getValueStr();
        String fjzp = StringUtils.arrayListToString(photos);
        String bz = binding.etBz.getValueStr();
        KeyValue gzlbq = binding.gzlbqmc.getValue();
        String gzlbqfx = binding.gzlbqfx.getValueStr();
        String gzlbqcj = binding.gzlbqcj.getValueStr();
        if (isCheck) {
            if (gzlbq != null) {
                if (StringUtilsExt.isHasOneEmpty(gzlbqfx, gzlbqcj)) {
                    ToastUtils.showMessage("请检查带星号的项目是否均已填写！");
                    return false;
                }
            }
        }
        gzlbq = nullTo(gzlbq);
        sbjcGztzjl.chzdzqk = chtzql.getValueStr();
        sbjcGztzjl.chzdzqkK = chtzql.key;
        sbjcGztzjl.gxtzcs = gxtzcs;
        sbjcGztzjl.ljtzcs = ljtzcs;
        sbjcGztzjl.gzdl = gzdl;
        sbjcGztzjl.ljz = ljz;
        sbjcGztzjl.ecgzdl = ecgzdl;
        sbjcGztzjl.hfsdsj = hfsdsj;
        sbjcGztzjl.zhycdxsj = zhycdxsj;
        sbjcGztzjl.bhsbmc = bhsbmc.getValueStr();
        sbjcGztzjl.bhsbmcK = bhsbmc.key;
        sbjcGztzjl.bhmc = bhmc.getValueStr();
        sbjcGztzjl.bhmcK = bhmc.key;
        sbjcGztzjl.bhdzsj = dzsj;
        sbjcGztzjl.zdq = zdq;
        sbjcGztzjl.ldkgqk = ldkgql;
        sbjcGztzjl.yqtbhph = yqtbhph;
        sbjcGztzjl.ylbzzph = ylbzzph;
        sbjcGztzjl.yjkxtph = yjkxtph;
        sbjcGztzjl.dzbhFj = fjzp;
        sbjcGztzjl.dzbhBz = bz;
        sbjcGztzjl.gzGzlbqmc = gzlbq.getValueStr();
        sbjcGztzjl.gzGzlbqmcK = gzlbq.key;
        sbjcGztzjl.gzGzlbfx = gzlbqfx;
        sbjcGztzjl.gzGzlbcj = gzlbqcj;
        try {
            GZTZSbgzjlService.getInstance().saveOrUpdate(sbjcGztzjl);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showPic() {
        if (photos.size() == 0) {
            binding.ivShowPic.setVisibility(View.INVISIBLE);
            binding.tvPicNum.setVisibility(View.INVISIBLE);
        } else if (photos.size() == 1) {
            binding.ivShowPic.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + photos.get(0));
            if (bitmap != null) {
                binding.ivShowPic.setImageBitmap(bitmap);
            }
            binding.tvPicNum.setVisibility(View.INVISIBLE);
        } else {
            binding.ivShowPic.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + photos.get(0));
            if (bitmap != null) {
                binding.ivShowPic.setImageBitmap(bitmap);
            }
            binding.tvPicNum.setVisibility(View.VISIBLE);
            binding.tvPicNum.setText(photos.size() + "");
        }
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Config.ACTION_IMAGE:
                    CustomerDialog.showProgress(mActivity, "压缩中...");
                    ExecutorManager.executeTaskSerially(() -> {
                        BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + imageName, 4);
                        mHandler.post(() -> {
                            CustomerDialog.dismissProgress();
                            photos.add(imageName);
                            BHDZQKActivity.this.showPic();
                        });
                    });
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String imageUrl : cancelList) {
                        photos.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                    }
                    showPic();
                    break;
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        binding.bhsbmc.setKeyValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                    }
                    break;
                case Config.ACTIVITY_CHOSE_DEVICE + 1:
                    model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        binding.gzlbqmc.setKeyValue(new KeyValue(model.getString(Device.DEVICEID), model.getString(Device.NAME)));
                        binding.gzlbqcj.setMustInput(true);
                        binding.gzlbqfx.setMustInput(true);
                    }
                    break;
            }
        }
    }
}
