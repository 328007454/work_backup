package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.common.Config;
import com.cnksi.common.databinding.DialogTipsBinding;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.XianCunHoleAdapter;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.databinding.ActivityPreventAnimalBinding;
import com.cnksi.sjjc.inter.ItemClickListenerPicture;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.util.FunctionUtil;
import com.cnksi.sjjc.util.FunctionUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * Created by han on 2016/6/13.
 * 防小动物措施检查现存孔洞界面
 */
public class XianCunHoleActivity extends BaseTitleActivity implements ItemClickListenerPicture {


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
    //点击当前清除拍照时对应的HoleRecord
    private HoleRecord item;
    //判断是否是返回键
    private boolean isBack = false;
    private ActivityPreventAnimalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreventAnimalBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
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


    public void initView() {
        mTitleBinding.tvTitle.setText("现存孔洞");
        binding.btnNext.setVisibility(View.VISIBLE);
        binding.btnNext.setText("提交");
        binding.reContainer.setVisibility(View.GONE);
        mTitleBinding.btnBack.setOnClickListener(view -> {
            isBack = true;
            saveData();
        });
    }


    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            holeRecords = HoleReportService.getInstance().getAllHoleRecord(currentReportId, currentBdzId);
            if (holeRecords != null && !holeRecords.isEmpty()) {
                for (HoleRecord record : holeRecords) {
                    clearPicMap.put(record.id, record);
                }

            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });


    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mXCHoleAdapter == null) {
                    mXCHoleAdapter = new XianCunHoleAdapter(mActivity, holeRecords, R.layout.xiancun_hole_adapter);
                    binding.lvExamineProcess.setAdapter(mXCHoleAdapter);
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
                    BitmapUtils.compressImage(file.getAbsolutePath(), 3);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm) + "\n" + currentHole + "\n" + PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "");
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imgName, pictureContent);
                }
            } else if (requestCode == Config.CANCEL_RESULT_LOAD_IMAGE) {//删除照片请求
                ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                ArrayList<String> allPicList = StringUtils.stringToList(item.clear_images);
                for (String imageUrl : cancleImagList) {
                    allPicList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                }
                item.clear_images = StringUtils.arrayListToString(allPicList);
                clearPicMap.put(item.id, item);
                mHandler.sendEmptyMessage(LOAD_DATA);
            } else if (requestCode == LOAD_DATA) {
                if (TextUtils.isEmpty(item.clear_images)) {
                    item.clear_images = "" + imgName;
                } else {
                    item.clear_images = item.clear_images + "," + imgName;
                }
                item.clear_reportid = currentReportId;
                clearPicMap.put(item.id, item);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        }
    }
    /**
     * 现存孔洞清除孔洞时候的拍照
     */
    @Override
    public void itemClick(View v, Object o, int position, View iView, View view) {
        switch (v.getId()) {
            case R.id.iv_take_pic:
                item = (HoleRecord) o;
                currentHole = item.location + "_" + item.hole_detail;
                FunctionUtils.takePicture(this, imgName = FunctionUtil.getCurrentImageName(mActivity), Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);
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
                if (!TextUtils.isEmpty(item.clear_images)) {
                    showClearAllPicDialog();
                }
                break;
            case R.id.img_clearhole_pic://删除清除孔洞照片
                ArrayList<String> listPicClear = null;
                item = (HoleRecord) o;
                if (!TextUtils.isEmpty(item.clear_images)) {
                    listPicClear = StringUtils.stringToList(item.clear_images);
                    showImageDetails(mActivity, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPicClear, Config.RESULT_PICTURES_FOLDER), true);
                }

                break;
            case R.id.img_discoverhole_pic://查看清除孔洞的照片
                ArrayList<String> listPicDis = null;
                item = (HoleRecord) o;
                if (!TextUtils.isEmpty(item.hole_images)) {
                    listPicDis = StringUtils.stringToList(item.hole_images);
                    showImageDetails(mActivity, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER), false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 清除所有的展示的清除孔洞照片
     */
    DialogTipsBinding mTipsBinding;

    private void showClearAllPicDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        if (mClearDialog == null) {
            mTipsBinding = DialogTipsBinding.inflate(LayoutInflater.from(getApplicationContext()));
            mClearDialog = DialogUtils.creatDialog(mActivity, mTipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mTipsBinding.tvDialogContent.setText("是否删除该孔洞所有清除后照片");
        mTitleBinding.tvTitle.setText("提示");
        mClearDialog.show();
        mTipsBinding.btnCancel.setOnClickListener(view -> mClearDialog.dismiss());

        mTipsBinding.btnSure.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(item.clear_images)) {
                item.clear_images = "";
                item.status = "0";
                clearPicMap.put(item.id, item);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
            mClearDialog.dismiss();
        });
    }

    private String clearPosition = "";
    private Dialog mClearDialog;

    private void initOnClick() {
        binding.btnNext.setOnClickListener(view -> {
            for (HoleRecord record : holeRecords) {
                if (!TextUtils.isEmpty(record.clear_images) && currentReportId.equals(record.clear_reportid)) {
                    clearPosition += record.location + "_" + record.hole_detail + "\n";
                }
            }
            XianCunHoleActivity.this.showClearHoleDialog(clearPosition);
            clearPosition = "";
        });
    }

    /**
     * 清除部分清除孔洞的照片
     */
    private void showClearHoleDialog(final String clearPosition) {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        if (mClearDialog == null) {
            mTipsBinding = DialogTipsBinding.inflate(LayoutInflater.from(getApplicationContext()));
            mClearDialog = DialogUtils.creatDialog(mActivity, mTipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (!TextUtils.isEmpty(clearPosition)) {
            mTipsBinding.tvDialogContent.setText("是否清除以下孔洞：\n" + clearPosition);
        } else {
            mTipsBinding.tvDialogContent.setText("目前没有清除的孔洞：\n" + clearPosition);
        }
        mClearDialog.show();
        mTipsBinding.tvDialogTitle.setText("确认清除孔洞");
        mTipsBinding.btnCancel.setOnClickListener(view -> mClearDialog.dismiss());

        mTipsBinding.btnSure.setOnClickListener(view -> {
            XianCunHoleActivity.this.saveData();
            mClearDialog.dismiss();

        });

    }

    /**
     * 保存数据
     */
    private void saveData() {
        List<HoleRecord> saveList = new ArrayList<>();
        for (Map.Entry<String, HoleRecord> entry : clearPicMap.entrySet()) {

            if (!isBack) {
                if (!TextUtils.isEmpty(entry.getValue().clear_images)) {
                    entry.getValue().status = "1";
                } else {
                    entry.getValue().status = "0";
                }
            }
            saveList.add(entry.getValue());

        }
        try {
            HoleReportService.getInstance().saveOrUpdate(saveList);
        } catch (DbException e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK);
        finish();
    }
}
