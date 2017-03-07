package com.cnksi.sjjc.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.view.photo.PhotoView;
import com.cnksi.sjjc.R;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;

/**
 * 图解五通图片详情Activity
 */
public class TJWTActivity extends BaseActivity {
    /**图片容器*/
    @ViewInject(R.id.zoom_image_view)
    private PhotoView zoomImageView;
    /**进度条*/
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
        tvTitle.setText("图片详情");

        String imgUrl = Environment.getExternalStorageDirectory().getAbsolutePath()+"/aa.png";
        mBitmapUtils.bind(zoomImageView,imgUrl,getLargeImageOptions(), new Callback.ProgressCallback<Drawable>() {
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

            }
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