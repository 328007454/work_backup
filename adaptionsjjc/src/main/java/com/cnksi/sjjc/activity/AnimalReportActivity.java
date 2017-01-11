package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.service.PreventionService;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 防小动物报告界面
 */
public class AnimalReportActivity extends BaseReportActivity {
    public static final int ANIMATION = 0X100;
    public static final int VIBRATOR = ANIMATION + 1;
    /**
     * 巡检开始时间
     */
    @ViewInject(R.id.tv_inspection_start_time)
    private TextView mTvInspectionStartTime;

    /**
     * 巡检结束时间
     */
    @ViewInject(R.id.tv_inspection_end_time)
    private TextView mTvInspectionEndTime;
    /**
     * 巡检人员
     */
    @ViewInject(R.id.tv_inspection_person)
    private TextView mTvInspectionPerson;

    //开关柜孔洞
    @ViewInject(R.id.tv_kaiguangui)
    private TextView tvKaiGuan;
    //室内孔洞
    @ViewInject(R.id.tv_shineikongdong)
    private TextView tvInroom;
    //室外孔洞
    @ViewInject(R.id.tv_shiwaikongdong)
    private TextView tvOutRoom;
    //鼠药放置情况
    @ViewInject(R.id.tv_menchuang)
    private TextView tvMen;
    //捕鼠器fangzhiqingk
    @ViewInject(R.id.tv_shuyaoqi)
    private TextView tvShuYao;
    //门窗严密
    @ViewInject(R.id.tv_bushuqi)
    private TextView tvBuShu;
    //发现孔洞
    @ViewInject(R.id.tv_discoverhole)
    private TextView tvDiscover;
    //清除孔洞
    @ViewInject(R.id.tv_clearhole)
    private TextView tvClear;

    //检查图片
    @ViewInject(R.id.img_jianchaprocess)
    private ImageView imgJianCha;
    //检查图片数量
    @ViewInject(R.id.tv_pic_num)
    private TextView tvJianChaNum;
    //发现孔洞
    @ViewInject(R.id.img_discoverhole)
    private ImageView imgDisHole;
    //发现孔洞数量
    @ViewInject(R.id.tv_discoverhole_num)
    private TextView tvDisNum;
    //清除孔洞
    @ViewInject(R.id.img_clearhole)
    private ImageView imgClearHole;
    //清除孔洞数量
    @ViewInject(R.id.tv_clearhole_num)
    private TextView tvClearNum;
    private String jianChaPics = "";
    private String disHolePics = "";
    private String clearHolePics = "";
    /**
     * 当前报告
     */
    private Report report;
    //
    private PreventionRecord preventionRecord  ;
    //
    private List<HoleRecord> mHoleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentValue();
        initUI();
        initData();
    }

    @Override
    public View setReportView() {
        return getLayoutInflater().inflate(R.layout.animal_layout,null);
    }

    private void initUI() {
        mTvInspectionPerson.setText(PreferencesUtils.get(_this,Config.CURRENT_LOGIN_USER,""));

    }

    private void initData() {
        mExcutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    report = db.findById(Report.class, currentReportId);
                    preventionRecord = PreventionService.getInstance().findPreventionRecordByReoprtId(currentReportId);
                    mHoleList = HoleReportService.getInstance().getCurrentClearRecord(currentReportId, currentBdzId);
                } catch (DbException e) {
                    e.printStackTrace(System.out);
                }

                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    private ArrayList<String> clearHole = new ArrayList<>();
    @Override
    protected void onRefresh(Message msg) {
       super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                mTvInspectionStartTime.setText(report.starttime);
                mTvInspectionEndTime.setText(report.endtime);
                String mainPic = preventionRecord.main_controll_images;
                String gaoYaPic = preventionRecord.hyperbaric_images;
                String oneDevicePic = preventionRecord.one_device_images;
                String protectPic = preventionRecord.protect_images;
                String dianLanPic = preventionRecord.cable_images;
                String secondDevicPic = preventionRecord.second_device_images;
                String otherPic = preventionRecord.other_images;
                if (0==preventionRecord.switchStatus) {
                    tvKaiGuan.setText("正常");
                    //getResources().getColor(R.color.green_color)
                    tvKaiGuan.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                } else {
                    tvKaiGuan.setText("不正常");
                    tvKaiGuan.setTextColor(Color.RED);
                }
                if (0==preventionRecord.inroomStatus) {
                    tvInroom.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                    tvInroom.setText("正常");
                } else {
                    tvInroom.setText("不正常");
                    tvInroom.setTextColor(Color.RED);
                }
                if (0==preventionRecord.outroomStatus) {
                    tvOutRoom.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                    tvOutRoom.setText("正常");
                } else {
                    tvOutRoom.setText("不正常");
                    tvOutRoom.setTextColor(Color.RED);
                }
                if (0==preventionRecord.doorWindowStatus) {
                    tvMen.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                    tvMen.setText("正常");
                } else {
                    tvMen.setText("不正常");
                    tvMen.setTextColor(Color.RED);
                }
                if (0==preventionRecord.ratsbaneStatus) {
                    tvShuYao.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                    tvShuYao.setText("正常");
                } else {
                    tvShuYao.setText("不正常");
                    tvShuYao.setTextColor(Color.RED);
                }
                if (0==preventionRecord.mousetrapStatus) {
                    tvBuShu.setTextColor(ContextCompat.getColor(_this,R.color.green_color));
                    tvBuShu.setText("正常");
                } else {
                    tvBuShu.setText("不正常");
                    tvBuShu.setTextColor(Color.RED);
                }
                jianChaPics = (mainPic == null ? "" : mainPic) + (gaoYaPic == null ? "" : "," + gaoYaPic) +
                        (oneDevicePic == null ? "" : "," + oneDevicePic) + (protectPic == null ? "" : "," + protectPic) +
                        (dianLanPic == null ? "" : "," + dianLanPic) + (secondDevicPic == null ? "" : "," + secondDevicPic) +
                        (otherPic == null ? "" : "," + otherPic);
                if(jianChaPics.startsWith(",")){
                    jianChaPics = jianChaPics.substring(1,jianChaPics.length());
                }
                for (HoleRecord record : mHoleList) {
                    if (!TextUtils.isEmpty(record.hole_images)&&currentReportId.equals(record.reportId)) {
                        if (TextUtils.isEmpty(disHolePics)) {
                            disHolePics += record.hole_images;
                        } else {
                            disHolePics = disHolePics + "," + record.hole_images;
                        }

                    }
                    if (!TextUtils.isEmpty(record.clear_images)&&currentReportId.equals(record.clear_reportid)&&"1".equals(record.status)) {
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
    private  ArrayList<String> disList;
    private ArrayList<String>  clearList;
    private void setReportPics() {
        if (!TextUtils.isEmpty(jianChaPics)) {
             jiaChaList = StringUtils.string2List(jianChaPics);
            if(jiaChaList.size()!=0){
                tvJianChaNum.setVisibility(View.VISIBLE);
                imgJianCha.setVisibility(View.VISIBLE);
                tvJianChaNum.setText(jiaChaList.size()+"");
                if(jiaChaList.size()==1){
                    tvJianChaNum.setVisibility(View.GONE);
                }
                String picName = jiaChaList.get(0);
                Bitmap bmPicture = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + picName);
                if (bmPicture != null) {
                    imgJianCha.setImageBitmap(bmPicture);
                }
            }
        }else {
            tvJianChaNum.setVisibility(View.GONE);
            imgJianCha.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(disHolePics)) {

           disList = StringUtils.string2List(disHolePics);
            tvDiscover.setText(disList.size()+"");
            String picName = disList.get(0);
            imgDisHole.setVisibility(View.VISIBLE);
            Bitmap bmPicture = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + picName);
            if (bmPicture != null) {
                imgDisHole.setImageBitmap(bmPicture);
            }
            if(disList.size()>1){
                tvDisNum.setVisibility(View.VISIBLE);
                tvDisNum.setText(disList.size()+"");
            }else {
                tvDisNum.setVisibility(View.INVISIBLE);
            }
        }else {
            tvDiscover.setText("无");
            tvDisNum.setVisibility(View.GONE);
            imgDisHole.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(clearHolePics)) {
             clearList = StringUtils.string2List(clearHolePics);
            tvClear.setText(clearList.size()+"");
            String picName = clearList.get(0);
            imgClearHole.setVisibility(View.VISIBLE);
            Bitmap bmPicture = BitmapUtil.getOptimizedBitmap(Config.RESULT_PICTURES_FOLDER + picName);
            if (bmPicture != null) {
                imgClearHole.setImageBitmap(bmPicture);
            }
            if(clearList.size()>1){

                tvClearNum.setText(clearList.size()+"");
            }else{
                tvClearNum.setVisibility(View.INVISIBLE);
            }
        }else{
            tvClear.setText("无");
            tvClearNum.setVisibility(View.GONE);
            imgClearHole.setVisibility(View.INVISIBLE);
        }
    }

    @Event({ R.id.tv_continue_inspection,R.id.img_jianchaprocess,R.id.img_discoverhole,R.id.img_clearhole,R.id.tv_continue_inspection})
    private void onViewClick(View view) {
        ArrayList<String> watchPics =null;
        switch (view.getId()) {
            case R.id.tv_continue_inspection:
                Intent intent1 = new Intent(_this, PreventAnimalActivity.class);
                startActivity(intent1);
                this.finish();
                break;
            case R.id.img_jianchaprocess:
                watchPics=jiaChaList;
                break;
            case R.id.img_discoverhole:
                watchPics =disList;
                break;
            case R.id.img_clearhole:
                watchPics = clearList;
                break;

            default:
                break;
        }
        if(watchPics!=null&&watchPics.size()>0){

        showImageDetails(mCurrentActivity, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(watchPics, Config.RESULT_PICTURES_FOLDER), false,false);
    }

    }
}
