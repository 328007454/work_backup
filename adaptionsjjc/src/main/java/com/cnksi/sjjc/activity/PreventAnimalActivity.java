package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.ExamineProcessAdapter;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.service.PreventionService;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ironGe on 2016/6/7.
 * <p/>
 * 防小动物措施检查界面
 */
public class PreventAnimalActivity extends BaseActivity {
    private static final int TAKEPIC_REQUEST = LOAD_DATA + 1;
    private static final int VIDEO_REQUEST = TAKEPIC_REQUEST + 1;
    private static final int REFRESH_UI = VIDEO_REQUEST + 1;


    @ViewInject(R.id.tv_find_hole)
    private TextView tvFindHole;

    @ViewInject(R.id.lv_examine_process)
    private ListView lvExamineProcess;

    @ViewInject(R.id.btn_next)
    private Button btnNext;

    private InspectionType mInspectionType;

    private ExamineProcessAdapter adapter;

    private List<HoleRecord> holeRecordList;
    //当前拍照孔洞位置
    private String currentHole;
    private int takePicPosition;
    private String imgName;
    //当前预防记录
    private List<PreventionRecord> data;
    private PreventionRecord preventionRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_prevent_animal);
        x.view().inject(_this);
        getIntentValue();
        mInspectionType = InspectionType.get(currentInspectionType);
        initUI();
        initDatata();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                PreventAnimalActivity.this.finish();
            }
        });

    }

    private void initUI() {
        tvTitle.setText("防小动物措施检查");
        tvRight.setText("现存孔洞");
        tvRight.setVisibility(View.VISIBLE);

        data = new ArrayList<PreventionRecord>();
        adapter = new ExamineProcessAdapter(this, data, R.layout.item_examine_process);
        adapter.setItemClickListener(new ItemClickListener<PreventionRecord>() {
            @Override
            public void itemClick(View v, PreventionRecord preventionRecord, int position) {
                takePicPosition = position;
                if (v.getId() == R.id.iv_take_pic) {
                    currentHole = (String) v.getTag();
                    FunctionUtils.takePicture(mCurrentActivity, imgName = FunctionUtil.getCurrentImageName(_this), Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);
                } else if (v.getId() == R.id.iv_show_pic) {
                    isWatchPics=true;
                    setResultImages();
                    ArrayList<String> listPic = StringUtils.string2List(allPics);
                    showImageDetails(mCurrentActivity, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPic, Config.RESULT_PICTURES_FOLDER), true,false);
                }
            }

            @Override
            public void itemLongClick(View v, PreventionRecord preventionRecord, int position) {

            }
        });
        lvExamineProcess.setAdapter(adapter);
    }

    private void initDatata() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                     preventionRecord = PreventionService.getInstance().findPreventionRecordByReoprtId(currentReportId);
                    holeRecordList = HoleReportService.getInstance().findClearPostion(currentReportId,currentBdzId);
                    if (null == preventionRecord)
                        preventionRecord = new PreventionRecord(currentReportId, currentBdzId, currentBdzName);
                    data.clear();
                    for (int i = 0; i < 7; i++) {
                        data.add(preventionRecord);
                    }
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                adapter.notifyDataSetChanged();
                adapter.setExitHoleRecords(holeRecordList);
                break;
        }

    }


    @Event({R.id.btn_next, R.id.tv_right, R.id.tv_find_hole})
    private void onClickEvent(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_next:
                saveData();
                intent = new Intent(_this, PreventAnimalSecondActivity.class);
                intent.putExtra("PreventionRecord",preventionRecord);
                break;
            case R.id.tv_right:
                intent = new Intent(_this, XianCunHoleActivity.class);
                startActivityForResult(intent,REFRESH_UI);
                return;
            case R.id.tv_find_hole:
                intent = new Intent(_this, DiscoverHoleActivity.class);
                startActivityForResult(intent,REFRESH_UI);
                return;
            default:
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == TAKEPIC_REQUEST) {//拍照返回请求
                File file = new File(Config.RESULT_PICTURES_FOLDER, imgName);
                if (file.exists()) {
                    BitmapUtil.compressImage(file.getAbsolutePath(), 3);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) + "\n" + currentHole + "\n" + PreferencesUtils.getString(_this, Config.CURRENT_LOGIN_USER, "");
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imgName, pictureContent);
                }

            } else if (requestCode == CANCEL_RESULT_LOAD_IMAGE) {//删除照片返回请求
                ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                ArrayList<String> allPicList = StringUtils.string2List(allPics);
                for (String imageUrl : cancleImagList) {
                   allPicList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER,""));
                }
                allPics =StringUtils.ArrayListToString(allPicList);
                finalPics = allPics;
                isDeletePic = true;
                setResultImages();
                mHandler.sendEmptyMessage(LOAD_DATA);
            } else if (requestCode == LOAD_DATA) {
                setResultImages();
                mHandler.sendEmptyMessage(LOAD_DATA);
            }else if(requestCode==REFRESH_UI){
                holeRecordList = HoleReportService.getInstance().findClearPostion(currentReportId,currentBdzId);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }else if(requestCode==TaskRemindActivity.FINISH_TASK){
                setResult(RESULT_OK);
                this.finish();
            }
        }
    }

    private String finalPics;
    private String allPics;
    private boolean isDeletePic = false;
    private boolean isWatchPics = false;

    /**
     * 展示拍完照或者删除照片后展示界面的图片以及图片的数量
     * */
    private void setResultImages() {
        String exitImages = "";
        switch (takePicPosition) {
            case 0:
                if (isDeletePic) {
                    data.get(0).main_controll_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                   allPics = data.get(0).main_controll_images;
                    isWatchPics=false;
                    return;
                }
                exitImages = (null == data.get(0).main_controll_images) ? "" : data.get(0).main_controll_images;
                allPics = data.get(0).main_controll_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;
                break;
            case 1:
                if (isDeletePic) {
                    data.get(0).hyperbaric_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).hyperbaric_images;
                    isWatchPics=false;
                    return;
                }
                exitImages = (null == data.get(0).hyperbaric_images) ? "" : data.get(0).hyperbaric_images;
                allPics = data.get(0).hyperbaric_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;

                break;
            case 2:
                if (isDeletePic) {
                    data.get(0).one_device_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).one_device_images;
                    isWatchPics=false;
                    return;
                }

                exitImages = (null == data.get(0).one_device_images) ? "" : data.get(0).one_device_images;
                allPics = data.get(0).one_device_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;
                break;
            case 3:
                if (isDeletePic) {
                    data.get(0).protect_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).protect_images;
                    isWatchPics=false;
                    return;
                }
                exitImages = (null == data.get(0).protect_images) ? "" : data.get(0).protect_images;
                allPics = data.get(0).protect_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;

                break;
            case 4:
                if (isDeletePic) {
                    data.get(0).cable_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).cable_images;
                    isWatchPics=false;
                    return;
                }

                exitImages = (null == data.get(0).cable_images) ? "" : data.get(0).cable_images;
                allPics = data.get(0).cable_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;

                break;
            case 5:
                if (isDeletePic) {
                    data.get(0).second_device_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).second_device_images;
                    isWatchPics=false;
                    return;
                }
                exitImages = (null == data.get(0).second_device_images) ? "" : data.get(0).second_device_images;
                allPics = data.get(0).second_device_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;
                break;
            case 6:
                if (isDeletePic) {
                    data.get(0).other_images = finalPics;
                    isDeletePic = false;
                    return;
                }
                if(isWatchPics){
                    allPics = data.get(0).other_images;
                    isWatchPics=false;
                    return;
                }
                exitImages = (null == data.get(0).other_images) ? "" : data.get(0).other_images;
                allPics = data.get(0).other_images = ("".equals(exitImages)) ? imgName : exitImages + "," + imgName;
                break;
        }
    }

    /**
     * 保存数据
     * */
    private void saveData(){
        preventionRecord.last_modify_time = DateUtils.getCurrentLongTime();
        try {
            db.saveOrUpdate(preventionRecord);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
