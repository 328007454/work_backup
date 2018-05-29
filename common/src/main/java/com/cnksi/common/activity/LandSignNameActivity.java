package com.cnksi.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.cnksi.common.R;
import com.cnksi.common.databinding.CommonActivityLandsignNameBinding;
import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.utils.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.cnksi.core.utils.Cst.CROP_PICTURE;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月8日 下午6:28:54 TODO
 */
public class LandSignNameActivity extends BaseCoreActivity {

    public static final String TAG = LandSignNameActivity.class.getSimpleName();

    public static final String ACTION_SIGNNAME = "action_signname";
    public static final String SIGNNAME_PATH = "signname_path";
    public static final String HEAD_PATH = "head_path";
    public static final String SIGNER = "signer";


    private String signNamePath;
    private String signHeadPath;
    private String signer;

    private CommonActivityLandsignNameBinding binding;


    private void initOnClick() {
        binding.btnQuxiao.setOnClickListener(view -> LandSignNameActivity.this.finish());

        binding.btnReset.setOnClickListener(view -> binding.pathView.clear());

        binding.btnNext.setOnClickListener(view -> {
            try {
                if (!binding.pathView.isEmpty()) {
                    if (TextUtils.isEmpty(signNamePath)) {
                        Log.e(TAG, "没有收到文件路径");
                    }
                    binding.pathView.saveBitmapToPNG(new File(signNamePath));
                    // takePicture(currentActivity, signHeadPath);
                    view.setEnabled(false);
                    LandSignNameActivity.this.setResult(RESULT_OK);
                    LandSignNameActivity.this.finish();
                } else {
                    ToastUtils.showMessage("您还没有签名，请先签名！");
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }


    @Override
    public int getLayoutResId() {
        return R.layout.common_activity_landsign_name;
    }

    @Override
    public void initUI() {
        binding = (CommonActivityLandsignNameBinding) rootDataBinding;
        binding.tvTitle.setText("签名确认");
        binding.pathView.clear();
        initOnClick();

    }

    @Override
    public void initData() {
        Intent data = getIntent();
        signHeadPath = data.getStringExtra(HEAD_PATH);
        signNamePath = data.getStringExtra(SIGNNAME_PATH);
        signer = data.getStringExtra(SIGNER);
        binding.tvPersonName.setText(signer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                // 拍照
                case 0x6:
                    // 更换照片 切割照片
                    // if (FileUtils.isFileExists(signHeadPath)) {
                    // cropImage(Uri.fromFile(new File(signHeadPath)), ScreenUtils.getScreenHeight(currentActivity),
                    // ScreenUtils.getScreenHeight(currentActivity), CROP_PICTURE, signHeadPath);
                    // }
                    setResult(RESULT_OK);
                    finish();
                    break;
                case CROP_PICTURE:
                    ToastUtils.showMessage("拍摄成功");
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
            }
        } else {
            findViewById(R.id.btn_next).setEnabled(true);
        }

    }


    /**
     * 裁剪图片
     *
     * @param uri
     * @param outputX
     * @param outputY
     * @param requestCode
     */
    protected void cropImage(Uri uri, int outputX, int outputY, int requestCode, String currentImageName) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 10);
        intent.putExtra("aspectY", 9);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(currentImageName)));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // intent.putExtra("noFaceDetection", false);
        startActivityForResult(intent, requestCode);
    }

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        Activity activity;
        String signer = "";
        @NonNull
        String signPath;
        String headPath;
        int requestCode;

        private Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setSigner(String signer) {
            this.signer = signer;
            return this;
        }

        public Builder setSignPath(@NonNull String signPath) {
            this.signPath = signPath;
            return this;
        }

        public Builder setHeadPath(String headPath) {
            this.headPath = headPath;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public void start() {
            Objects.requireNonNull(signPath);
            Intent intent = new Intent(activity, LandSignNameActivity.class);
            intent.putExtra(LandSignNameActivity.SIGNER, signer);
            intent.putExtra(LandSignNameActivity.SIGNNAME_PATH, signPath);
            intent.putExtra(LandSignNameActivity.HEAD_PATH, headPath);
            activity.startActivityForResult(intent, requestCode);
        }
    }

}
