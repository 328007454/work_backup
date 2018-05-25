package com.cnksi.common.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.databinding.ActivityImageDetailsBinding;
import com.cnksi.common.databinding.DialogTipsBinding;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.activity.BaseCoreActivity;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.photo.PhotoView;

import java.io.File;
import java.util.ArrayList;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;

/**
 * 查看大图的Activity界面。
 */
public class ImageDetailsActivity extends BaseCoreActivity implements OnPageChangeListener {

    /**
     * 得到的图片List
     */
    private ArrayList<String> imageList = null;

    private ArrayList<String> cancelImageList = null;

    private ViewPagerAdapter viewAdapter = null;

    private boolean isShowSelectFlag = false;

    private int position = 0;

    private String titleName = "";
    private Boolean isDeleteFile = true;
    /**
     * 当前的位置
     */
    private int currentPosition = 0;
    /**
     * 提示对话框
     */
    protected Dialog tipsDialog = null;
    private ActivityImageDetailsBinding mImageDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void getRootDataBinding() {
        mImageDetailsBinding = ActivityImageDetailsBinding.inflate(getLayoutInflater());
        imageList = getIntent().getStringArrayListExtra(Config.IMAGEURL_LIST);
        currentPosition = position = getIntent().getIntExtra(Config.CURRENT_IMAGE_POSITION, 0);
        isShowSelectFlag = getIntent().getBooleanExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        isDeleteFile = getIntent().getBooleanExtra(Config.IS_DELETE_FILE, true);
        titleName = getIntent().getStringExtra(Config.TITLE_NAME);
        mImageDetailsBinding.includeTitle.tvTitle.setText(TextUtils.isEmpty(titleName) ? "图片查看" : titleName);
        if (isShowSelectFlag) {
            mImageDetailsBinding.ibtnDelete.setVisibility(View.VISIBLE);
        } else {
            mImageDetailsBinding.ibtnDelete.setVisibility(View.GONE);
        }
        initView();
        loadData();
    }

    @Override
    public int getLayoutResId() {
        return 0;
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    public void initView() {
        initOnClick();
    }

    private void initOnClick() {
        mImageDetailsBinding.includeTitle.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        mImageDetailsBinding.ibtnDelete.setOnClickListener(view -> {
            showSureTipsDialog();
        });
    }


    DialogTipsBinding mTipsBinding;

    private void showSureTipsDialog() {
        if (tipsDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(getApplicationContext()) * 9 / 10;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            mTipsBinding = DialogTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.creatDialog(mActivity, mTipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        mTipsBinding.tvDialogTitle.setText(R.string.dialog_tips_str);
        mTipsBinding.tvDialogContent.setText(R.string.dialog_tips_delete_pic_content);
        mTipsBinding.btnSure.setText(R.string.yes_str);
        mTipsBinding.btnCancel.setText(R.string.no_str);
        tipsDialog.show();
        mTipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });
        mTipsBinding.btnSure.setOnClickListener(view -> {
            String imageUrl = imageList.get(currentPosition);
            if (isDeleteFile) {
                File file = new File(imageUrl);
                if (file.isFile() && file.exists()) {// if image is exist , delete it,
                    file.delete();
                }
            }
            imageList.remove(currentPosition);
            cancelImageList.add(imageUrl);
            if (imageList.size() == 0) {
                onBackPressed();
            }
            viewAdapter.notifyDataSetChanged();

            mImageDetailsBinding.pageText.setText(String.valueOf(currentPosition + 1) + "/" + imageList.size());
        });
    }

    @SuppressWarnings("deprecation")
    public void loadData() {

        viewAdapter = new ViewPagerAdapter();
        mImageDetailsBinding.viewPager.setAdapter(viewAdapter);
        mImageDetailsBinding.viewPager.setCurrentItem(position);
        mImageDetailsBinding.viewPager.setOnPageChangeListener(this);
        mImageDetailsBinding.viewPager.setEnabled(false);
        mImageDetailsBinding.viewPager.setOffscreenPageLimit(3);
        // 设定当前的页数和总页数
        mImageDetailsBinding.pageText.setText((position + 1) + "/" + imageList.size());
        if (imageList.size() > 0) {
            try {
                if (getIntent().getBooleanExtra(Config.CANCEL_IMAGEURL_LIST, true)) {
                    mImageDetailsBinding.filename.setText(new File(imageList.get(position).trim()).getName());
                } else {
                    mImageDetailsBinding.filename.setVisibility(View.GONE);
                }

            } catch (Exception e) {

            }

        }
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
            View view = LayoutInflater.from(ImageDetailsActivity.this).inflate(R.layout.zoom_image_layout, container, false);
            final PhotoView zoomImageView = (PhotoView) view.findViewById(R.id.zoom_image_view);
            final ProgressBar progress = (ProgressBar) view.findViewById(R.id.loading);
            final String imageUrl = imageList.get(position);
            progress.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapUtils.getImageThumbnailByHeight(imageUrl, 1920);
            if (null != bitmap) {
                progress.setVisibility(View.GONE);
                zoomImageView.setImageBitmap(bitmap);
            } else {
                progress.setVisibility(View.GONE);
                ToastUtils.showMessage("加载失败");
            }
            container.addView(view, 0);
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
        mImageDetailsBinding.pageText.setText((currentPage + 1) + "/" + imageList.size());
        if (imageList.size() > 0) {
            try {
                mImageDetailsBinding.filename.setText(new File(imageList.get(currentPosition).trim()).getName());
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onBackPressed() {
        if (isShowSelectFlag) {
            Intent intent = getIntent();
            intent.putStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST, cancelImageList);
            setResult(RESULT_OK, intent);
        }
        this.finish();
    }


    public static void startImageActivity(Activity context, int position, ArrayList<String> mImageUrlList, boolean isDeleteFile, boolean isShowDelete) {
        Intent intent = new Intent(context, ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_DELETE_FILE, isDeleteFile);
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.startActivityForResult(intent, CANCEL_RESULT_LOAD_IMAGE);

    }


}