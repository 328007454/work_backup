package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.common.Config;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DiscoverHoleAdapter;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.databinding.ActivityDiscoverHoleBinding;
import com.cnksi.sjjc.service.HoleReportService;
import com.cnksi.sjjc.util.CoreConfig;
import com.cnksi.sjjc.util.FunctionUtil;
import com.cnksi.sjjc.util.FunctionUtils;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by han on 2016/6/13.
 * 发现孔洞
 */
public class DiscoverHoleActivity extends BaseActivity {
    //所有孔洞位置集合
    private List<String> areaList;
    private ListView positionLv;
    private Dialog positionDialog;

    private static final int TAKEPIC_REQUEST = LOAD_DATA + 1;
    private static final int VIDEO_REQUEST = TAKEPIC_REQUEST + 1;
    private static final int REFRESH_UI = VIDEO_REQUEST + 1;
    private String imgName;
    private ArrayList<String> picList = new ArrayList<>();

    private Bitmap bmPicture;
    private ActivityDiscoverHoleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiscoverHoleBinding.inflate(getLayoutInflater());
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
        mTitleBinding.tvTitle.setText("发现孔洞");
        binding.tvHoleNum.setVisibility(View.GONE);
    }

    public void loadData() {
        areaList = Arrays.asList(getResources().getStringArray(R.array.AnimalPositionArray));
        mHandler.sendEmptyMessage(LOAD_DATA);
    }


    private void initPostionDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = areaList.size() > 8 ? DisplayUtils.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(this, binding.llContainerPic, R.layout.content_list_dialog, false);
        positionLv = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, "请选择孔洞位置");
        DiscoverHoleAdapter adapter = new DiscoverHoleAdapter(this, areaList, R.layout.dialog_content_child_item);
        //设置adapter的listView点击事件
        adapter.setItemClickListener(new ItemClickListener<String>() {

            @Override
            public void itemClick(View v, String s, int position) {
                binding.tvDiscoverholePosition.setText(s);
                binding.tvDiscoverholePosition.setTextColor(_this.getResources().getColor(R.color.green_color));
                positionDialog.dismiss();
            }

            @Override
            public void itemLongClick(View v, String s, int position) {

            }
        });
        positionLv.setAdapter(adapter);
        positionDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                initPostionDialog();
                break;
            default:
                break;
        }

    }

    private void initOnClick() {
        binding.positionContainer.setOnClickListener(view -> {
            if (positionDialog != null) {
                positionDialog.show();
            }
        });
        binding.ivTakePic.setOnClickListener(view -> {
            FunctionUtils.takePicture(this, imgName = FunctionUtil.getCurrentImageName(_this), Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);
        });
        binding.imgDiscoverholePic.setOnClickListener(view -> {
            showImageDetails(_this, 0, com.cnksi.core.utils.StringUtils.addStrToListItem(picList, Config.RESULT_PICTURES_FOLDER), true, false);
        });
        binding.btnSave.setOnClickListener(view -> {
            String picAll = StringUtils.arrayListToString(picList);
            if (TextUtils.isEmpty(binding.tvDiscoverholePosition.getText().toString())) {
                ToastUtils.showMessage("请选择发现位置");
                return;
            }
            if (TextUtils.isEmpty(binding.etInputInforposition.getText().toString())) {
                ToastUtils.showMessage("请输入详细位置");
                return;
            }
            if (TextUtils.isEmpty(binding.etInputProblem.getText().toString())) {
                ToastUtils.showMessage("请输入问题描述");
                return;
            }
            HoleRecord holeRecord = new HoleRecord(currentReportId, currentBdzId, currentBdzName, binding.tvDiscoverholePosition.getText().toString(),
                    binding.etInputInforposition.getText().toString(), picAll, binding.etInputProblem.getText().toString());
            try {
                HoleReportService.getInstance().saveOrUpdate(holeRecord);
            } catch (DbException e) {
                e.printStackTrace();
            }
            setResult(RESULT_OK);
            this.finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bm = null;
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKEPIC_REQUEST) {
                File file = new File(Config.RESULT_PICTURES_FOLDER, imgName);
                if (file.exists()) {
                    BitmapUtils.compressImage(file.getAbsolutePath(), 3);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8) + "\n" + binding.tvDiscoverholePosition.getText()
                            + binding.etInputInforposition.getText().toString() + "\n" + PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "");
                    drawCircle(Config.RESULT_PICTURES_FOLDER + imgName, pictureContent);
                    picList.add(imgName);
                }

            } else if (requestCode == CANCEL_RESULT_LOAD_IMAGE) {
                ArrayList<String> cancleImagList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                for (String imageUrl : cancleImagList) {
                    picList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                }
                showPicture();
            } else if (requestCode == LOAD_DATA) {
                bm = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + imgName);
                binding.imgDiscoverholePic.setImageBitmap(bm);
                showPicture();
            }
        }
    }

    @Override
    public void drawCircle(String pictureName, String pictureContent) {
        Intent intent = new Intent(_this, DrawCircleImageActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_NAME, pictureName);
        intent.putExtra(Config.PICTURE_CONTENT, pictureContent);
        startActivityForResult(intent, LOAD_DATA);
    }

    private void showPicture() {
        if (picList.size() != 0) {
            imgName = picList.get(0);
            bmPicture = BitmapUtils.compressImage(Config.RESULT_PICTURES_FOLDER + imgName);
            if (picList.size() == 1) {
                binding.tvHoleNum.setVisibility(View.GONE);
            } else if (picList.size() != 0 && picList.size() > 1) {
                binding.tvHoleNum.setVisibility(View.VISIBLE);
                binding.tvHoleNum.setText(picList.size() + "");
            }
            binding.imgDiscoverholePic.setVisibility(View.VISIBLE);
            binding.imgDiscoverholePic.setImageBitmap(bmPicture);
        } else if (picList.size() == 0) {
            binding.tvHoleNum.setVisibility(View.GONE);
            binding.imgDiscoverholePic.setVisibility(View.GONE);
        }
    }

}
