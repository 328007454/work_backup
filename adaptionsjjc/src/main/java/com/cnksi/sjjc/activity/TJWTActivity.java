package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.view.photo.PhotoView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 图解五通图片详情Activity
 */
public class TJWTActivity extends BaseActivity {
    /**
     * 图片容器
     */
    @ViewInject(R.id.zoom_image_view)
    private PhotoView zoomImageView;
    /**
     * 进度条
     */
    @ViewInject(R.id.loading)
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_tjwt);

        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        Intent intent = getIntent();
        //标题
        String title = intent.getStringExtra("title");
        //路径（此路径只到/lib/wt/xxx.png）
        String pic = intent.getStringExtra("pic");
        //全路径
        String imgUrl = Config.BDZ_INSPECTION_FOLDER + pic;
        tvTitle.setText(title);

        Bitmap bitmap = BitmapFactory.decodeFile(imgUrl);
        zoomImageView.setImageBitmap(bitmap);
        x.image().bind(zoomImageView, imgUrl,getLargeImageOptions(), new Callback.ProgressCallback<Drawable>() {

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onSuccess(Drawable result) {
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progress.setVisibility(View.GONE);
                CToast.showShort(_this, "加载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progress.setVisibility(View.GONE);
                CToast.showShort(_this, "取消加载");
            }

            @Override
            public void onFinished() {
                progress.setVisibility(View.GONE);
            }
        });

        zoomImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(zoomImageView.getWidth()!=0) {
                    zoomImageView.setScrollDistance(0, 10000);
//                    zoomImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

    }

    /**
     * 获得显示大图的图像配置，大图不适合内存缓存
     */
    private ImageOptions getLargeImageOptions() {
        return new ImageOptions.Builder()
                .setSize(-1, -1).setUseMemCache(false).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                // .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //  .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                // .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                //    .setLoadingDrawableId(R.mipmap.ic_default_pic)
                .setFailureDrawableId(R.mipmap.ic_default_pic)
                .build();
    }
}