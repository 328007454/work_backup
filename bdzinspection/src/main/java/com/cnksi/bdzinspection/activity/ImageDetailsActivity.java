package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityImageDetailsBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.Config;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.photo.PhotoView;

import java.io.File;
import java.util.ArrayList;

/**
 * 查看大图的Activity界面。
 */
public class ImageDetailsActivity extends BaseActivity implements OnPageChangeListener {

    /**
     * 得到的图片List
     */
    private ArrayList<String> imageList = null;

    private ArrayList<String> cancelImageList = null;

    private ViewPagerAdapter viewAdapter = null;

    private boolean isShowSelectFlag = false;

    private boolean isSelectFlag = false;

    private int position = 0;

    private String titleName = "";
    /**
     * 当前的位置
     */
    private int currentPosition = 0;

    private XsActivityImageDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mBitmapUtils = BitmapHelp.getInstance().getBitmapUtils(currentActivity);
//        mBitmapConfig = new BitmapDisplayConfig();
//        mBitmapConfig.setBitmapMaxSize(BitmapCommonUtils.getScreenSize(getApplicationContext()));
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_image_details);

        imageList = getIntent().getStringArrayListExtra(Config.IMAGEURL_LIST);
        imageList.remove(Config.CURRENT_IMAGE_NAME);
        currentPosition = position = getIntent().getIntExtra(Config.CURRENT_IMAGE_POSITION, 0);
        titleName = getIntent().getStringExtra(Config.TITLE_NAME);
        binding.includeTitle.tvTitle.setText(TextUtils.isEmpty(titleName) ? "图片查看" : titleName);

        isShowSelectFlag = getIntent().getBooleanExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        if (isShowSelectFlag) {
            binding.ibtnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.ibtnDelete.setVisibility(View.GONE);
        }

        isSelectFlag = getIntent().getBooleanExtra("select", false);
        if (isSelectFlag) {
            binding.includeTitle.tvBatteryTestStep.setVisibility(View.VISIBLE);
            binding.includeTitle.tvBatteryTestStep.setText("确认选择");
        }
        initialData();

        initOnClick();
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.includeTitle.tvBatteryTestStep.setOnClickListener(view -> {
            cancelImageList.add(imageList.get(binding.viewPager.getCurrentItem()));
            onBackPressed();
        });
        binding.ibtnDelete.setOnClickListener(view -> {
            showSureTipsDialog();
        });
    }

    XsDialogTipsBinding tipsBinding;

    private void showSureTipsDialog() {
        if (tipsDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        tipsBinding.tvDialogTitle.setText(R.string.xs_dialog_tips_str);
        tipsBinding.tvDialogContent.setText(R.string.xs_dialog_tips_delete_pic_content);
        tipsBinding.btnSure.setText(R.string.xs_yes_str);
        tipsBinding.btnCancel.setText(R.string.xs_no_str);
        tipsDialog.show();
        tipsBinding.btnSure.setOnClickListener(view -> {
            String imageUrl = imageList.get(currentPosition);
            File file = new File(imageUrl);
            if (file != null && file.exists()) {// if image is exist , delete it,
                file.delete();
            }
            imageList.remove(currentPosition);
            cancelImageList.add(imageUrl);
            if (imageList.size() == 0) {
                onBackPressed();
            }
            viewAdapter.notifyDataSetChanged();
            binding.pageText.setText(String.valueOf(currentPosition + 1) + "/" + imageList.size());
        });

        tipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });
    }

    @SuppressWarnings("deprecation")
    private void initialData() {

        viewAdapter = new ViewPagerAdapter();
        binding.viewPager.setAdapter(viewAdapter);
        binding.viewPager.setCurrentItem(position);
        binding.viewPager.setOnPageChangeListener(this);
        binding.viewPager.setEnabled(false);
        binding.viewPager.setOffscreenPageLimit(3);
        // 设定当前的页数和总页数
        binding.pageText.setText((position + 1) + "/" + imageList.size());
        cancelImageList = new ArrayList<String>();
    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ImageDetailsActivity.this).inflate(R.layout.xs_zoom_image_layout, container, false);
            final PhotoView zoomImageView = (PhotoView) view.findViewById(R.id.zoom_image_view);
            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.loading);
            String imageUrl = imageList.get(position);

            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(imageUrl,ScreenUtils.getScreenWidth(container.getContext()));
            if (bitmap!=null){
                zoomImageView.setImageBitmap(bitmap);
            }else{
                ToastUtils.showMessage( "加载失败");
            }
            progress.setVisibility(View.GONE);
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int currentPage) {
        currentPosition = currentPage;
        // 每当页数发生改变时重新设定一遍当前的页数和总页数
        binding.pageText.setText((currentPage + 1) + "/" + imageList.size());

    }

    @Override
    public void onBackPressed() {
        if (isShowSelectFlag || isSelectFlag) {
            Intent intent = getIntent();
            intent.putStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST, cancelImageList);
            setResult(RESULT_OK, intent);
        }
        this.finish();
    }

}
