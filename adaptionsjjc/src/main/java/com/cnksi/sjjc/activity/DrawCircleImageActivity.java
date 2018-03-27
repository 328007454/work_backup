package com.cnksi.sjjc.activity;

import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.CustomerDialog.DialogClickListener;
import com.cnksi.core.view.PicturePaintView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ActivityDrawCircleBinding;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 缺陷照片标记圆圈的界面
 *
 * @author Oliver
 */
public class DrawCircleImageActivity extends BaseActivity {

    private PicturePaintView mPicturePaintView;
    /**
     * 图片路径
     */
    private String currentImagePath = "";
    /**
     * 是否保存图片
     */
    private boolean isSavePicture = false;

    private ActivityDrawCircleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDrawCircleBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        initView();
        initOnClick();
    }

    @Override
    public void initUI() {

    }


    public void initView() {
        currentImagePath = getIntent().getStringExtra(Config.CURRENT_IMAGE_NAME);
        mTitleBinding.tvTitle.setText("标记图片");
        initBitmap();
    }

    @Override
    public void initData() {

    }

    /**
     * 初始化画布
     */
    private void initBitmap() {
        CustomerDialog.showProgress(_this, "正在初始化图片...");
        ExecutorManager.executeTaskSerially(() -> {
            int screenWidth = ScreenUtils.getScreenWidth(_this);
            int screenHeight = ScreenUtils.getScreenHeight(_this);
            // 首先压缩图片
            File file = new File(currentImagePath);
            if (file.exists()) {
                BitmapUtils.compressImage(file.getAbsolutePath(), screenWidth, screenHeight);
            }
            final Bitmap bitmapTemp = BitmapUtils.getImageThumbnail(BitmapUtils.postRotateBitmap(currentImagePath, true), screenWidth, screenHeight);
            if (bitmapTemp != null) {
                mPicturePaintView = new PicturePaintView(_this, bitmapTemp);
            }
            mHandler.sendEmptyMessage(SAVE_DATA);
        });
    }

    public void initOnClick() {
        mTitleBinding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnSaveMark.setOnClickListener(view -> {
            saveMarkAndExit();
        });
        binding.btnAddMark.setOnClickListener(view -> {
            mPicturePaintView.saveMark();
        });
        binding.btnClearMark.setOnClickListener(view -> {
            CustomerDialog.showSelectDialog(_this, "确认要清除所有标记吗?", new DialogClickListener() {
                @Override
                public void confirm() {
                    initBitmap();
                }

                @Override
                public void cancel() {

                }
            });
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        CustomerDialog.dismissProgress();
        switch (msg.what) {
            case LOAD_DATA:

                onBackPressed();

                break;
            case SAVE_DATA:
                binding.llImageContainer.removeAllViews();
                binding.llImageContainer.addView(mPicturePaintView);
                binding.tvPictureContent.setText(getIntent().getStringExtra(Config.PICTURE_CONTENT));

                break;
            default:
                break;
        }
    }


    /**
     * 保存标记
     */
    private void saveMarkAndExit() {
        isSavePicture = true;
        CustomerDialog.showProgress(_this, "正在保存图片...");
        ExecutorManager.executeTaskSerially(() -> {
            mPicturePaintView.saveMark();
            if (saveEditPicture(binding.rlCirclePicture, currentImagePath, 80)) {
                mPicturePaintView.setBitmapNull();
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSavePicture) {
            setResult(RESULT_OK, getIntent());
            this.finish();
        } else {
            saveMarkAndExit();
        }
    }


    /**
     * 保存手写笔记
     *
     * @return
     */
    public  boolean saveEditPicture(View view, String picturePath, int quality) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            try {
                File file = new File(picturePath);
                if (file.exists()) {
                    file.delete();
                }
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(buffer);
                outputStream.close();
                bos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                view.setDrawingCacheEnabled(false);
            }
        }
        bitmap.recycle();
        bitmap = null;
        return false;
    }
}
