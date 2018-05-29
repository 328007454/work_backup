package com.cnksi.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;

import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.databinding.CommonActivityDrawCircleBinding;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.core.activity.BaseCoreActivity;
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
public class DrawCircleImageActivity extends BaseCoreActivity {
    private PicturePaintView mPicturePaintView;
    /**
     * 图片路径
     */
    private String currentImagePath = "";
    /**
     * 是否保存图片
     */
    private boolean isSavePicture = false;
    private CommonActivityDrawCircleBinding binding;


    @Override
    public int getLayoutResId() {
        return R.layout.common_activity_draw_circle;
    }

    @Override
    public void initUI() {
        binding = (CommonActivityDrawCircleBinding) rootDataBinding;
        currentImagePath = getIntent().getStringExtra(Config.CURRENT_IMAGE_NAME);
        binding.btnSaveMark.setOnClickListener(view -> saveMarkAndExit());
        binding.btnAddMark.setOnClickListener(view -> PicturePaintView.saveMark());
        binding.btnClearMark.setOnClickListener(view -> CustomerDialog.showSelectDialog(mActivity, "确认要清除所有标记吗?", new CustomerDialog.DialogClickListener() {

            @Override
            public void confirm() {
                initBitmap();
            }

            @Override
            public void cancel() {

            }
        }));

    }

    @Override
    public void initData() {
        initBitmap();
    }


    /**
     * 初始化画布
     */
    private void initBitmap() {
        CustomerDialog.showProgress(mActivity, "正在初始化图片...");
        ExecutorManager.executeTask(() -> {

            int screenWidth = ScreenUtils.getScreenWidth(mActivity);
            int screenHeight = ScreenUtils.getScreenHeight(mActivity);
            // 首先压缩图片
            File file = new File(currentImagePath);
            if (file.exists()) {
                BitmapUtil.compressImage(file.getAbsolutePath(), screenWidth, screenHeight);
            }
            Bitmap bitmapTemp = BitmapUtil.getImageThumbnail(BitmapUtil.postRotateBitmap(currentImagePath, true), screenWidth, screenHeight);
            if (bitmapTemp != null) {
                mPicturePaintView = new PicturePaintView(mActivity, bitmapTemp);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }


    @Override
    protected void onRefresh(Message msg) {
        CustomerDialog.dismissProgress();
        switch (msg.what) {
            case SAVE_DATA:

                onBackPressed();

                break;
            case LOAD_DATA:
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
        CustomerDialog.showProgress(mActivity, "正在保存图片...");
        ExecutorManager.executeTask(() -> {
            PicturePaintView.saveMark();
            if (BitmapUtil.saveEditPicture(binding.rlCirclePicture, currentImagePath, 80)) {
                mPicturePaintView.setBitmapNull();
                mHandler.sendEmptyMessage(SAVE_DATA);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isSavePicture) {
            setResult(Activity.RESULT_OK, getIntent());
            this.finish();
        } else {
            saveMarkAndExit();
        }
    }

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        String txtContent = "";
        String path;
        Activity activity;
        int requestCode = LOAD_DATA;

        private Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setTxtContent(String txtContent) {
            this.txtContent = txtContent;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public void start() {
            Intent intent = new Intent(activity, DrawCircleImageActivity.class);
            intent.putExtra(Config.CURRENT_IMAGE_NAME, path);
            intent.putExtra(Config.PICTURE_CONTENT, txtContent);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
