package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityLandsignNameBinding;

import java.io.File;
import java.io.IOException;

import static com.cnksi.core.utils.Cst.CROP_PICTURE;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月8日 下午6:28:54 TODO
 */
public class LandSignNameActivity extends TitleActivity {

    public static final String TAG = LandSignNameActivity.class.getSimpleName();

    public static final String ACTION_SIGNNAME = "action_signname";
    public static final String SIGNNAME_PATH = "signname_path";
    public static final String HEAD_PATH = "head_path";
    public static final String SIGNER = "signer";


    private String signNamePath;
    private String signHeadPath;
    private String signer;

    private XsActivityLandsignNameBinding binding;

    @Override
    protected int setLayout() {
        return R.layout.xs_activity_landsign_name;
    }

    @Override
    protected String initialUI() {
        binding = (XsActivityLandsignNameBinding) getDataBinding();
        binding.pathView.clear();
        initOnClick();
        return "签名确认";
    }

    @Override
    protected void initialData() {

        Intent data = getIntent();
        signHeadPath = data.getStringExtra(HEAD_PATH);
        signNamePath = data.getStringExtra(SIGNNAME_PATH);
        signer = data.getStringExtra(SIGNER);
        binding.tvPersonName.setText(signer);
    }

    private void initOnClick() {
        binding.btnQuxiao.setOnClickListener(view -> {
            finish();
        });

        binding.btnReset.setOnClickListener(view -> {
            binding.pathView.clear();
        });

        binding.btnNext.setOnClickListener(view -> {
            try {
                if (!binding.pathView.isEmpty()) {
                    if (TextUtils.isEmpty(signNamePath)) {
                        Log.e(TAG, "没有收到文件路径");
                    }
                    binding.pathView.saveBitmapToPNG(new File(signNamePath));
                    // takePicture(currentActivity, signHeadPath);
                    view.setEnabled(false);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast("您还没有签名，请先签名！");
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void releaseResAndSaveData() {
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
                    Toast("拍摄成功");
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
            }
        } else {
            findViewById(R.id.btn_next).setEnabled(true);
        }

    }


    public void Toast(String str) {
        Toast.makeText(currentActivity, str, Toast.LENGTH_SHORT).show();
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

}
