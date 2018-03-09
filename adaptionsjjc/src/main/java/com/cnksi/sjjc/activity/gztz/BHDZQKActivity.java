package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.core.utils.BitmapUtil;
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
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private boolean sftz;
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
            Intent intent = new Intent(_this, BHDZJLActivity.class);
            startActivity(intent);
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
        binding.ivShowPic.setOnClickListener(v -> showImageDetails(this, StringUtils.addStrToListItem(photos,Config.RESULT_PICTURES_FOLDER), true));
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
                sftz = "是".equals(sbjcGztzjl.sftz);
                binding.chzdzqk.setVisibility(sftz ? View.VISIBLE : View.GONE);


                if (!TextUtils.isEmpty(sbjcGztzjl.dzbhFj))
                    photos = Arrays.asList(StringUtils.NullToBlank(sbjcGztzjl.dzbhFj).split(","));
                else photos = new ArrayList<>();
                showPic();

                //处理ABCO 相别
                int[] visbles = new int[]{0, 0, 0, 0};
                for (char t : sbjcGztzjl.sbxb.toCharArray()) {
                    if (t == 'A') visbles[0] = 1;
                    if (t == 'B') visbles[1] = 1;
                    if (t == 'C') visbles[2] = 1;
                    if (t == 'O') visbles[3] = 1;
                }
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
