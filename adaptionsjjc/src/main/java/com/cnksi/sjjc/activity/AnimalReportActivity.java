package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Report;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.databinding.AnimalLayoutBinding;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.service.PreventionService;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 防小动物报告界面
 */
public class AnimalReportActivity extends BaseReportActivity {
    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = ANIMATION + 1;

    private String jianChaPics = "";
    private String disHolePics = "";
    private String clearHolePics = "";
    /**
     * 当前报告
     */
    private Report report;
    //
    private PreventionRecord preventionRecord;
    //
    private List<HoleRecord> mHoleList;
    private int discoverHoleCount;

    private AnimalLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentValue();
        initView();
        loadData();
        initOnClick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    @Override
    public View setReportView() {
        binding = AnimalLayoutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    public void initView() {
        binding.tvInspectionPerson.setText(PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                report = ReportService.getInstance().findById(currentReportId);
                preventionRecord = PreventionService.getInstance().findPreventionRecordByReoprtId(currentReportId);
                mHoleList = HoleReportService.getInstance().getCurrentClearRecord(currentReportId, currentBdzId);
            } catch (DbException e) {
                e.printStackTrace(System.out);
            }

            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    private ArrayList<String> clearHole = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                binding.tvInspectionStartTime.setText(report.starttime);
                binding.tvInspectionEndTime.setText(report.endtime);
                String mainPic = preventionRecord.main_controll_images;
                String gaoYaPic = preventionRecord.hyperbaric_images;
                String oneDevicePic = preventionRecord.one_device_images;
                String protectPic = preventionRecord.protect_images;
                String dianLanPic = preventionRecord.cable_images;
                String secondDevicPic = preventionRecord.second_device_images;
                String otherPic = preventionRecord.other_images;

                if (0 == preventionRecord.switchStatus) {
                    binding.tvKaiguangui.setText("正常");
                    //getResources().getColor(R.color.green_color)
                    binding.tvKaiguangui.setTextColor(ContextCompat.getColor(_this, R.color.green_color));
                } else {
                    binding.tvKaiguangui.setText("不正常");
                    binding.tvKaiguangui.setTextColor(Color.RED);
                }
                if (0 == preventionRecord.inroomStatus) {
                    binding.tvShineikongdong.setTextColor(ContextCompat.getColor(_this, R.color.green_color));
                    binding.tvShineikongdong.setText("正常");
                } else {
                    binding.tvShineikongdong.setText("不正常");
                    binding.tvShineikongdong.setTextColor(Color.RED);
                }
                if (0 == preventionRecord.outroomStatus) {
                    binding.tvShiwaikongdong.setTextColor(ContextCompat.getColor(_this, R.color.green_color));
                    binding.tvShiwaikongdong.setText("正常");
                } else {
                    binding.tvShiwaikongdong.setText("不正常");
                    binding.tvShiwaikongdong.setTextColor(Color.RED);
                }
                if (0 == preventionRecord.doorWindowStatus) {
                    binding.tvMenchuang.setTextColor(ContextCompat.getColor(_this, R.color.green_color));
                    binding.tvMenchuang.setText("正常");
                } else {
                    binding.tvMenchuang.setText("不正常");
                    binding.tvMenchuang.setTextColor(Color.RED);
                }
//                if (0==preventionRecord.ratsbaneStatus) {
//                    binding.tvShuyaoqi.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
//                    binding.tvShuyaoqi.setText("正常");
//                } else {
//                    binding.tvShuyaoqi.setText("不正常");
//                    binding.tvShuyaoqi.setTextColor(Color.RED);
//                }
//                if (0==preventionRecord.mousetrapStatus) {
//                    binding.tvBushuqi.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
//                    binding.tvBushuqi.setText("正常");
//                } else {
//                    binding.tvBushuqi.setText("不正常");
//                    binding.tvBushuqi.setTextColor(Color.RED);
//                }

                binding.tvBushuqi.setTextColor(getResources().getColor(R.color.green_color));
                binding.tvBushuqi.setText(preventionRecord.mousetrapInfo);
                jianChaPics = (mainPic == null ? "" : mainPic) + (gaoYaPic == null ? "" : "," + gaoYaPic) +
                        (oneDevicePic == null ? "" : "," + oneDevicePic) + (protectPic == null ? "" : "," + protectPic) +
                        (dianLanPic == null ? "" : "," + dianLanPic) + (secondDevicPic == null ? "" : "," + secondDevicPic) +
                        (otherPic == null ? "" : "," + otherPic);
                if (jianChaPics.startsWith(",")) {
                    jianChaPics = jianChaPics.substring(1, jianChaPics.length());
                }
                for (HoleRecord record : mHoleList) {
                    if (currentReportId.equalsIgnoreCase(record.reportId)) {
                        discoverHoleCount++;
                    }
                    if (!TextUtils.isEmpty(record.hole_images) && currentReportId.equals(record.reportId)) {
                        if (TextUtils.isEmpty(disHolePics)) {
                            disHolePics += record.hole_images;
                        } else {
                            disHolePics = disHolePics + "," + record.hole_images;
                        }

                    }
                    if (!TextUtils.isEmpty(record.clear_images) && currentReportId.equals(record.clear_reportid) && "1".equals(record.status)) {
                        if (TextUtils.isEmpty(clearHolePics)) {
                            clearHolePics += record.clear_images;
                        } else {
                            clearHolePics = clearHolePics + "," + record.clear_images;
                        }

                    }
                }

                setReportPics();
                break;

            default:
                break;
        }
    }

    private ArrayList<String> jiaChaList;
    private ArrayList<String> disList;
    private ArrayList<String> clearList;

    private void setReportPics() {

        if (!TextUtils.isEmpty(jianChaPics)) {
            jiaChaList = StringUtils.stringToList(jianChaPics);
            if (jiaChaList.size() != 0) {
                binding.tvPicNum.setVisibility(View.VISIBLE);
                binding.imgJianchaprocess.setVisibility(View.VISIBLE);
                binding.tvPicNum.setText(jiaChaList.size() + "");
                if (jiaChaList.size() == 1) {
                    binding.tvPicNum.setVisibility(View.GONE);
                }
                String picName = jiaChaList.get(0);
                Bitmap bmPicture = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + picName);
                if (bmPicture != null) {
                    binding.imgJianchaprocess.setImageBitmap(bmPicture);
                }
            }
        } else {
            binding.tvPicNum.setVisibility(View.GONE);
            binding.imgJianchaprocess.setVisibility(View.INVISIBLE);
        }

        binding.tvDiscoverhole.setText(discoverHoleCount == 0 ? "无" : discoverHoleCount + "");
        if (!TextUtils.isEmpty(disHolePics)) {

            disList = StringUtils.stringToList(disHolePics);
//            binding.tvDiscoverhole.setText(disList.size()+"");
            String picName = disList.get(0);
            binding.imgDiscoverhole.setVisibility(View.VISIBLE);
            Bitmap bmPicture = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + picName);

            if (bmPicture != null) {
                binding.imgDiscoverhole.setImageBitmap(bmPicture);
            }
            if (disList.size() > 1) {
                binding.tvDiscoverholeNum.setVisibility(View.VISIBLE);
                binding.tvDiscoverholeNum.setText(disList.size() + "");
            } else {
                binding.tvDiscoverholeNum.setVisibility(View.INVISIBLE);
            }
        } else {
//            binding.tvDiscoverhole.setText("无");
            binding.tvDiscoverholeNum.setVisibility(View.GONE);
            binding.imgDiscoverhole.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(clearHolePics)) {
            clearList = StringUtils.stringToList(clearHolePics);
            binding.tvClearhole.setText(clearList.size() + "");
            String picName = clearList.get(0);
            binding.imgClearhole.setVisibility(View.VISIBLE);
            Bitmap bmPicture = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + picName);
            if (bmPicture != null) {
                binding.imgClearhole.setImageBitmap(bmPicture);
            }
            if (clearList.size() > 1) {

                binding.tvClearholeNum.setText(clearList.size() + "");
            } else {
                binding.tvClearholeNum.setVisibility(View.INVISIBLE);
            }
        } else {
            binding.tvClearhole.setText("无");
            binding.tvClearholeNum.setVisibility(View.GONE);
            binding.imgClearhole.setVisibility(View.INVISIBLE);
        }
    }

    ArrayList<String> watchPics = null;

    private void initOnClick() {
        binding.tvContinueInspection.setOnClickListener(view -> {
            Intent intent1 = new Intent(_this, PreventAnimalActivity.class);
            AnimalReportActivity.this.startActivity(intent1);
            AnimalReportActivity.this.finish();
        });
        binding.imgJianchaprocess.setOnClickListener(view -> {
            watchPics = jiaChaList;
            AnimalReportActivity.this.save();
        });

        binding.imgDiscoverhole.setOnClickListener(view -> {
            watchPics = disList;
            AnimalReportActivity.this.save();
        });

        binding.imgClearhole.setOnClickListener(view -> {
            watchPics = clearList;
            AnimalReportActivity.this.save();
        });
    }

    private void save() {
        if (watchPics != null && watchPics.size() > 0) {
            showImageDetails(mActivity, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(watchPics, Config.RESULT_PICTURES_FOLDER), false, false);
        }
    }
}
