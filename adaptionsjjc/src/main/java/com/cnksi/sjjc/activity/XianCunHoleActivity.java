package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.XianCunHoleAdapter;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.inter.ItemClickListenerPicture;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 2016/6/13.
 * 防小动物措施检查现存孔洞界面
 */
public class XianCunHoleActivity extends BaseActivity implements ItemClickListenerPicture {

    @ViewInject(R.id.lv_examine_process)
    private ListView lvHole;
    @ViewInject(R.id.btn_next)
    private Button btNext;

    @ViewInject(R.id.re_container)
    private RelativeLayout reContainer;

    private XianCunHoleAdapter mXCHoleAdapter;
    private List<String> picList = new ArrayList<>();
    private List<HoleRecord> holeRecords;
    private HashMap<String, HoleRecord> clearPicMap = new HashMap<>();

    //拍照文件名
    private String imgName;

    //当前拍照孔洞位置
    private String currentHole;
    private static final int TAKEPIC_REQUEST = LOAD_DATA + 1;
    private static final int VIDEO_REQUEST = TAKEPIC_REQUEST + 1;
    private static final int REFRESH_UI = VIDEO_REQUEST + 1;
    //点击当前清除拍照时对应的HolrRecord
    private HoleRecord item;
    //判断是否是返回键
    private boolean isBack=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_prevent_animal);
        getIntentValue();
        initUI();
        initData();
    }

    private void initUI() {
        tvTitle.setText("现存孔洞");
        btNext.setVisibility(View.VISIBLE);
        btNext.setText("提交");
        reContainer.setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBack=true;
                saveData();
            }
        });
    }


    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                holeRecords = HoleReportService.getInstance().getAllHoleRecord(currentReportId,currentBdzId);
                if(holeRecords!=null&&!holeRecords.isEmpty()){
                    for(HoleRecord record:holeRecords){
                        clearPicMap.put(record.id,record);
                    }

                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });


    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mXCHoleAdapter == null) {
                    mXCHoleAdapter = new XianCunHoleAdapter(_this, holeRecords, R.layout.xiancun_hole_adapter);
                    lvHole.setAdapter(mXCHoleAdapter);
                    mXCHoleAdapter.setItemClickListener(this);
                } else {
                    mXCHoleAdapter.setList(holeRecords);
                }
                break;
            default:
                break;
        }
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

            } else if (requestCode == CANCEL_RESULT_LOAD_IMAGE) {//删除照片请求
                ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                ArrayList<String> allPicList = StringUtils.string2List(item.clear_images);
                for (String imageUrl : cancleImagList) {
                    allPicList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER,""));
                }
                item.clear_images =StringUtils.ArrayListToString(allPicList);
                clearPicMap.put(item.id, item);
                mHandler.sendEmptyMessage(LOAD_DATA);
            } else if (requestCode == LOAD_DATA) {
                if(TextUtils.isEmpty(item.clear_images)){
                    item.clear_images=""+imgName;
                }else{
                   item.clear_images=item.clear_images+","+imgName;
                }
                item.clear_reportid = currentReportId;
                clearPicMap.put(item.id, item);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        }
    }

    /**
     * 现存孔洞清除孔洞时候的拍照
     * */
    @Override
    public void itemClick(View v, Object o, int position, View iView, View view) {
        switch (v.getId()) {
            case R.id.iv_take_pic:
                item = (HoleRecord) o;
                currentHole= item.location+"_"+item.hole_detail;
                FunctionUtils.takePicture(this, imgName = FunctionUtil.getCurrentImageName(_this), Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    public void itemClick(View v, Object o, int position) {
        switch (v.getId()) {
            case R.id.iv_delet_pic://删除清除孔洞所有
                item = (HoleRecord) o;
                if(!TextUtils.isEmpty(item.clear_images)){
                    showClearAllPicDialog();
                }
                break;
            case R.id.img_clearhole_pic://删除清除孔洞照片
                ArrayList<String> listPicClear = null;
                item = (HoleRecord) o;
                if (!TextUtils.isEmpty(item.clear_images)) {
                    listPicClear = StringUtils.string2List(item.clear_images);
                    showImageDetails(mCurrentActivity,0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPicClear, Config.RESULT_PICTURES_FOLDER),true);
                }

                break;
            case R.id.img_discoverhole_pic://查看清除孔洞的照片
                ArrayList<String> listPicDis = null;
                item = (HoleRecord) o;
                if (!TextUtils.isEmpty(item.hole_images)) {
                    listPicDis = StringUtils.string2List(item.hole_images);
                    showImageDetails(mCurrentActivity,0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER),false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 清除所有的展示的清除孔洞照片
     * */
    private void showClearAllPicDialog(){
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 9/ 10;
        if (mClearHolder == null) {
            mClearHolder = new ClearHolder();
        }
        if (mClearDialog == null) {
            mClearDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_tips, mClearHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
        mClearHolder.tvContent.setText("是否删除该孔洞所有清除后照片");
        mClearHolder.tvTitle.setText("提示");
            mClearDialog.show();
        mClearHolder.btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearDialog.dismiss();
            }
        });

        mClearHolder.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(item.clear_images)) {
                    item.clear_images = "";
                    item.status = "0";
                    clearPicMap.put(item.id, item);
                    mHandler.sendEmptyMessage(LOAD_DATA);
                }
                mClearDialog.dismiss();
            }
        });
    }
    private String clearPosition = "";
    private Dialog mClearDialog;
    private ClearHolder mClearHolder;

    @Event(R.id.btn_next)
    private void clickEvent(View view) {
        for (HoleRecord record : holeRecords) {
            if (!TextUtils.isEmpty(record.clear_images)&&currentReportId.equals(record.clear_reportid)) {
                clearPosition += record.location + "_" + record.hole_detail + "\n";
            }
        }
        showClearHoleDialog(clearPosition);
        clearPosition="";

    }

    /**
     * 清除部分清除孔洞的照片
     *
     * */
    private void showClearHoleDialog(final String clearPosition) {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 9/10;
        if (mClearHolder == null) {
            mClearHolder = new ClearHolder();
        }
        if (mClearDialog == null) {
            mClearDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_tips, mClearHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        }
        if(!TextUtils.isEmpty(clearPosition)){
            mClearHolder.tvContent.setText("是否清除以下孔洞：\n"+clearPosition);

        }else{
            mClearHolder.tvContent.setText("目前没有清除的孔洞：\n"+clearPosition);
        }
        mClearDialog.show();
        mClearHolder.tvTitle.setText("确认清除孔洞");
        mClearHolder.btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearDialog.dismiss();
            }
        });

        mClearHolder.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               saveData();
                mClearDialog.dismiss();

            }
        });

    }

    class ClearHolder {
        @ViewInject(R.id.tv_dialog_title)
        private TextView tvTitle;
        @ViewInject(R.id.tv_dialog_content)
        private TextView tvContent;

        @ViewInject(R.id.btn_cancel)
        private Button btCancle;
        @ViewInject(R.id.btn_sure)
        private Button btFinish;

    }

    /**
     * 保存数据
     * */
    private void saveData(){
        for (Map.Entry<String, HoleRecord> entry : clearPicMap.entrySet()) {
            try {
                if(!isBack){
                    if(!TextUtils.isEmpty(entry.getValue().clear_images)){
                        entry.getValue().status="1";
                    }else{
                        entry.getValue().status="0";
                    }
                }
                db.saveOrUpdate(entry.getValue());
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        XianCunHoleActivity.this.setResult(RESULT_OK);
        XianCunHoleActivity.this.finish();
    }
}
