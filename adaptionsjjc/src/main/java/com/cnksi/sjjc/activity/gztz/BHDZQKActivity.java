package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjl;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzqkBinding;
import com.cnksi.sjjc.inter.SimpleTextWatcher;
import com.cnksi.sjjc.service.gztz.GZTZSbgzjlService;
import com.cnksi.sjjc.util.CalcUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.common.util.KeyValue;
import org.xutils.ex.DbException;
import org.xutils.x;

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
        initData();
    }

    private void initView() {
        binding.btnNext.setOnClickListener(v -> {
            if (save(true)) {
                Intent intent = new Intent(_this, BHDZJLActivity.class);
                startActivity(intent);
            }
        });
        binding.btnPre.setOnClickListener(v -> finish());
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

        binding.ivTakePic.setOnClickListener(v -> {
            FunctionUtils.takePicture(this, imageName = FunctionUtil.getCurrentImageName(this), Config.RESULT_PICTURES_FOLDER);
        });
        binding.ivShowPic.setOnClickListener(v -> showImageDetails(this, StringUtils.addStrToListItem(photos, Config.RESULT_PICTURES_FOLDER), true));
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(() -> {
            sbjcGztzjl = GZTZSbgzjlService.getInstance().findByReportId(currentReportId);
            SbjcGztzjl last = GZTZSbgzjlService.getInstance().findLastByDeviceId(sbjcGztzjl.dlqbh, currentReportId);
            runOnUiThread(() -> {
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


                if (!TextUtils.isEmpty(sbjcGztzjl.dzbhFj))
                    photos = Arrays.asList(StringUtils.NullToBlank(sbjcGztzjl.dzbhFj).split(","));
                else photos = new ArrayList<>();
                showPic();

                //处理ABCO 相别
                int[] visbles = sbjcGztzjl.getXb();
                binding.ljtzcs.setXb(visbles);
                binding.llA.setVisibility(visbles[0] == 1 ? View.VISIBLE : View.GONE);
                binding.llB.setVisibility(visbles[1] == 1 ? View.VISIBLE : View.GONE);
                binding.llC.setVisibility(visbles[2] == 1 ? View.VISIBLE : View.GONE);
                binding.llO.setVisibility(visbles[3] == 1 ? View.VISIBLE : View.GONE);
                binding.chzdzqk.setKeyValue(new KeyValue(sbjcGztzjl.chzdzqkK, sbjcGztzjl.chzdzqk));
                binding.ecgzdl.setText(StringUtils.NullToBlank(sbjcGztzjl.ecgzdl));
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
                binding.gzlbqfx.setValueStr(sbjcGztzjl.gzGzlbqmc);
                binding.gzlbqcj.setValueStr(sbjcGztzjl.gzGzlbcj);
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
        if (sbjcGztzjl.checkXbTzcs(a, b, c, o)) {
            CToast.showShort(this, "请填写各项跳闸次数！");
        }
        HashMap<String, Integer> temp = new HashMap<>();
        temp.put("A", CalcUtils.toInt(a, 0));
        temp.put("B", CalcUtils.toInt(b, 0));
        temp.put("C", CalcUtils.toInt(c, 0));
        temp.put("O", CalcUtils.toInt(o, 0));
        String gxtzcs = JSON.toJSONString(temp);
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
            if ((chtzql == null && sbjcGztzjl.isTz()) || bhmc == null || bhsbmc == null || StringUtils.isHasOneEmpty(gzdl, ljz, ecgzdl, dzsj, zdq, ldkgql)) {
                CToast.showShort(this, "请检查带星号的项目是否均已填写！");
                return false;
            } else {
                chtzql = nullTo(chtzql);
                bhmc = nullTo(bhmc);
                bhsbmc = nullTo(bhsbmc);
            }
        }
        String yqtbhph = binding.yqtbhph.getValueStr();
        String ylbzzph = binding.ylbzzph.getValueStr();
        String yjkxtph = binding.yjkxtph.getValueStr();
        String fjzp = StringUtils.ArrayListToString(photos);
        String bz = binding.etBz.getValueStr();
        KeyValue gzlbq = binding.gzlbqmc.getValue();
        String gzlbqfx = binding.gzlbqfx.getValueStr();
        String gzlbqcj = binding.gzlbqcj.getValueStr();
        if (isCheck) {
            if (gzlbq != null) {
                if (StringUtils.isHasOneEmpty(gzlbqfx, gzlbqcj)) {
                    CToast.showShort(this, "请检查带星号的项目是否均已填写！");
                    return false;
                }
            }
        } else {
            gzlbq = nullTo(gzlbq);
        }
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
            x.image().bind(binding.ivShowPic, Config.RESULT_PICTURES_FOLDER + photos.get(0));
            binding.tvPicNum.setVisibility(View.INVISIBLE);
        } else {
            binding.ivShowPic.setVisibility(View.VISIBLE);
            x.image().bind(binding.ivShowPic, Config.RESULT_PICTURES_FOLDER + photos.get(0));
            binding.tvPicNum.setVisibility(View.VISIBLE);
            binding.tvPicNum.setText(photos.size() + "");
        }
    }

    private KeyValue nullTo(KeyValue keyValue) {
        if (keyValue == null) return NULL;
        else return keyValue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Config.ACTION_IMAGE:
                    CustomerDialog.showProgress(mCurrentActivity, "压缩中...");
                    mFixedThreadPoolExecutor.execute(() -> {
                        BitmapUtil.compressImage(Config.RESULT_PICTURES_FOLDER + imageName, 4);
                        mHandler.post(() -> {
                            CustomerDialog.dismissProgress();
                            photos.add(imageName);
                            showPic();
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

            }
        }
    }
}
