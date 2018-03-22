package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.databinding.ActivityTjwtBinding;


/**
 * 图解五通图片详情Activity
 */
public class TJWTActivity extends BaseActivity {
    private ActivityTjwtBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTjwtBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        initView();
    }

    @Override
    public void initUI() {

    }

    /**
     * 初始化UI
     */
    public void initView() {
        Intent intent = getIntent();
        //标题
        String title = intent.getStringExtra("title");
        //路径（此路径只到/lib/wt/xxx.png）
        String pic = intent.getStringExtra("pic");
        //全路径
        String imgUrl = Config.BDZ_INSPECTION_FOLDER + pic;
        mTitleBinding.tvTitle.setText(title);
        binding.loading.setVisibility(View.VISIBLE);
        Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);
        if (bitmap != null) {
            binding.zoomImageView.setImageBitmap(bitmap);
            binding.loading.setVisibility(View.GONE);
        } else {
            binding.loading.setVisibility(View.GONE);
            ToastUtils.showMessage("加载失败");
            binding.loading.setVisibility(View.GONE);
        }
        binding.zoomImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (binding.zoomImageView.getWidth() != 0) {
//                    binding.zoomImageView.setScrollDistance(0, 10000);
//                    binding.zoomImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    @Override
    public void initData() {

    }

}