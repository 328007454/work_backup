package com.cnksi.bdzinspection.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityDrawCircleBinding;
import com.cnksi.common.Config;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.PicturePaintView;

import java.io.File;

import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.common.Config.SAVE_DATA;

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
    private XsActivityDrawCircleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_draw_circle);
        initialUI();
        initOnClick();
    }


    private void initialUI() {
        currentImagePath = getIntent().getStringExtra(Config.CURRENT_IMAGE_NAME);
        initBitmap();
    }

    /**
     * 初始化画布
     */
    private void initBitmap() {
        CustomerDialog.showProgress(currentActivity, "正在初始化图片...");
        ExecutorManager.executeTask(new Runnable() {
            @Override
            public void run() {

                int screenWidth = ScreenUtils.getScreenWidth(currentActivity);
                int screenHeight = ScreenUtils.getScreenHeight(currentActivity);
                // 首先压缩图片
                File file = new File(currentImagePath);
                if (file.exists()) {
                    BitmapUtil.compressImage(file.getAbsolutePath(), screenWidth, screenHeight);
                }
                Bitmap bitmapTemp = BitmapUtil.getImageThumbnail(BitmapUtil.postRotateBitmap(currentImagePath, true), screenWidth, screenHeight);
                if (bitmapTemp != null) {
                    mPicturePaintView = new PicturePaintView(currentActivity, bitmapTemp);
                    mHandler.sendEmptyMessage(SAVE_DATA);
                }
            }
        });
    }
    private void initOnClick() {

        binding.btnSaveMark.setOnClickListener(view -> {
            saveMarkAndExit();
        });
        binding.btnAddMark.setOnClickListener(view -> {
            mPicturePaintView.saveMark();
        });
        binding.btnClearMark.setOnClickListener(view -> {
            CustomerDialog.showSelectDialog(currentActivity, "确认要清除所有标记吗?", new CustomerDialog.DialogClickListener() {

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
        CustomerDialog.showProgress(currentActivity, "正在保存图片...");
        ExecutorManager.executeTask(() -> {
            PicturePaintView.saveMark();
            if (BitmapUtil.saveEditPicture(binding.rlCirclePicture, currentImagePath, 80)) {
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
}
