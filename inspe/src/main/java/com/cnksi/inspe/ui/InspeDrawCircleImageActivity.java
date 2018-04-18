package com.cnksi.inspe.ui;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.PicturePaintView;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeDrawCircleImageBinding;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.InspeConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class InspeDrawCircleImageActivity extends AppBaseActivity {


    private PicturePaintView mPicturePaintView;
    /**
     * 图片路径
     */
    private String currentImagePath = "";
    /**
     * 是否保存图片
     */
    private boolean isSavePicture = false;
    private MyHandler mHandler;

    ActivityInspeDrawCircleImageBinding binding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_draw_circle_image;
    }

    @Override
    public void initUI() {
        binding = (ActivityInspeDrawCircleImageBinding) rootDataBinding;
        binding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);
        mHandler = new MyHandler();
        initOnClick();
    }

    @Override
    public void initData() {
        currentImagePath = getIntent().getStringExtra(Config.CURRENT_IMAGE_NAME);
        binding.includeInspeTitle.toolbarTitle.setText("标记图片");
        initBitmap();
    }


    /**
     * 初始化画布
     */
    private void initBitmap() {
        CustomerDialog.showProgress(this, "正在初始化图片...");
        ExecutorManager.executeTaskSerially(() -> {
            int screenWidth = ScreenUtils.getScreenWidth(this);
            int screenHeight = ScreenUtils.getScreenHeight(this);
            // 首先压缩图片
            File file = new File(currentImagePath);
            if (file.exists()) {
                BitmapUtils.compressImage(file.getAbsolutePath(), screenWidth, screenHeight);
            }
            final Bitmap bitmapTemp = BitmapUtils.getImageThumbnail(BitmapUtils.postRotateBitmap(currentImagePath, true), screenWidth, screenHeight);
            if (bitmapTemp != null) {
                mPicturePaintView = new PicturePaintView(this, bitmapTemp);
            }
            mHandler.sendEmptyMessage(InspeConfig.SAVE_DATA);
        });
    }

    public void initOnClick() {
        binding.includeInspeTitle.toolbarBackBtn.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnSaveMark.setOnClickListener(view -> {
            saveMarkAndExit();
        });
        binding.btnAddMark.setOnClickListener(view -> {
            mPicturePaintView.saveMark();
        });
        binding.btnClearMark.setOnClickListener(view -> {
            CustomerDialog.showSelectDialog(this, "确认要清除所有标记吗?", new CustomerDialog.DialogClickListener() {
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


    /**
     * 保存标记
     */
    private void saveMarkAndExit() {
        isSavePicture = true;
        CustomerDialog.showProgress(this, "正在保存图片...");
        ExecutorManager.executeTaskSerially(() -> {
            mPicturePaintView.saveMark();
            if (saveEditPicture(binding.rlCirclePicture, currentImagePath, 80)) {
                mPicturePaintView.setBitmapNull();
                mHandler.sendEmptyMessage(InspeConfig.LOAD_DATA);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacksAndMessages(0);
        mHandler = null;
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
    public boolean saveEditPicture(View view, String picturePath, int quality) {
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

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            CustomerDialog.dismissProgress();
            switch (msg.what) {
                case InspeConfig.LOAD_DATA:
                    onBackPressed();
                    break;
                case InspeConfig.SAVE_DATA:
                    binding.llImageContainer.removeAllViews();
                    binding.llImageContainer.addView(mPicturePaintView);
                    binding.tvPictureContent.setText(getIntent().getStringExtra(Config.PICTURE_CONTENT));
                    break;
                default:
                    break;
            }
        }
    }
}
