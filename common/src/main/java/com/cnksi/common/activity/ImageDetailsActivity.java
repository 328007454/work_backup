package com.cnksi.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.databinding.ActivityImageDetailsBinding;
import com.cnksi.common.listener.AbstractPageChangeListener;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.photo.PhotoView;

import java.io.File;
import java.util.ArrayList;

import static com.cnksi.common.Config.CANCEL_IMAGEURL_LIST;
import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.common.Config.CURRENT_IMAGE_POSITION;
import static com.cnksi.common.Config.IMAGEURL_LIST;
import static com.cnksi.common.Config.IS_DELETE_FILE;
import static com.cnksi.common.Config.IS_SHOW_PHOTO_FLAG;

/**
 * 查看大图的Activity界面。
 *
 * @author Wastrel
 */
public class ImageDetailsActivity extends BaseTitleActivity  {

    /**
     * 得到的图片List
     */
    private ArrayList<String> imageList = null;

    private ArrayList<String> cancelImageList = null;

    private ViewPagerAdapter viewAdapter = null;

    private boolean isShowDeleteFlag = false;
    private boolean isSelectFlag = false;
    private int position = 0;

    private String titleName = "";
    private Boolean isDeleteFile = true;
    /**
     * 当前的位置
     */
    private int currentPosition = 0;

    private ActivityImageDetailsBinding mImageDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected View getChildContentView() {
        mImageDetailsBinding = ActivityImageDetailsBinding.inflate(getLayoutInflater());
        return mImageDetailsBinding.getRoot();
    }


    @Override
    public void initUI() {
        imageList = getIntent().getStringArrayListExtra(IMAGEURL_LIST);
        currentPosition = position = getIntent().getIntExtra(CURRENT_IMAGE_POSITION, 0);
        isShowDeleteFlag = getIntent().getBooleanExtra(IS_SHOW_PHOTO_FLAG, true);
        isDeleteFile = getIntent().getBooleanExtra(IS_DELETE_FILE, true);
        titleName = getIntent().getStringExtra(Config.TITLE_NAME);
        setTitleText(TextUtils.isEmpty(titleName) ? "图片查看" : titleName);
        if (isShowDeleteFlag) {
            mImageDetailsBinding.ibtnDelete.setVisibility(View.VISIBLE);
        } else {
            mImageDetailsBinding.ibtnDelete.setVisibility(View.GONE);
        }
        initOnClick();
        isSelectFlag = getIntent().getBooleanExtra("select", false);
        if (isSelectFlag) {
            mTitleBinding.tvRight.setVisibility(View.VISIBLE);
            mTitleBinding.tvRight.setText("确认选择");
        }
    }

    @Override
    public void initData() {
        loadData();
    }


    private void initOnClick() {
        mTitleBinding.tvRight.setOnClickListener(view -> {
            cancelImageList.add(imageList.get(mImageDetailsBinding.viewPager.getCurrentItem()));
            onBackPressed();
        });
        mImageDetailsBinding.ibtnDelete.setOnClickListener(view -> showSureTipsDialog());
    }


    private void showSureTipsDialog() {

        DialogUtils.createTipsDialog(mActivity, getString(R.string.dialog_tips_delete_pic_content)
                , (v) -> {
                    String imageUrl = imageList.get(currentPosition);
                    if (isDeleteFile) {
                        File file = new File(imageUrl);
                        if (file.isFile() && file.exists()) {
                            file.delete();
                        }
                    }
                    imageList.remove(currentPosition);
                    cancelImageList.add(imageUrl);
                    if (imageList.size() == 0) {
                        ImageDetailsActivity.this.onBackPressed();
                    }
                    viewAdapter.notifyDataSetChanged();

                    mImageDetailsBinding.pageText.setText(String.valueOf(currentPosition + 1) + "/" + imageList.size());
                }, true).show();
    }

    @SuppressWarnings("deprecation")
    public void loadData() {
        viewAdapter = new ViewPagerAdapter();
        mImageDetailsBinding.viewPager.setAdapter(viewAdapter);
        mImageDetailsBinding.viewPager.setCurrentItem(position);
        mImageDetailsBinding.viewPager.setOnPageChangeListener(new AbstractPageChangeListener(){

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
        });
        mImageDetailsBinding.viewPager.setEnabled(false);
        mImageDetailsBinding.viewPager.setOffscreenPageLimit(3);
        // 设定当前的页数和总页数
        mImageDetailsBinding.pageText.setText((position + 1) + "/" + imageList.size());
        if (imageList.size() > 0) {
            try {
                if (isShowDeleteFlag) {
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
            final PhotoView zoomImageView = view.findViewById(R.id.zoom_image_view);
            final ProgressBar progress = view.findViewById(R.id.loading);
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
    public void onBackPressed() {
        if (isShowDeleteFlag || isSelectFlag) {
            Intent intent = getIntent();
            intent.putStringArrayListExtra(CANCEL_IMAGEURL_LIST, cancelImageList);
            setResult(RESULT_OK, intent);
        }
        this.finish();
    }


    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {

        Activity context;
        int position = 0;
        ArrayList<String> imageUrlList = new ArrayList<>();
        boolean isDeleteFile = false;
        boolean isShowDelete = false;
        boolean isSelect = false;
        int requestCode = CANCEL_RESULT_LOAD_IMAGE;
        String title = "查看图片";

        public Builder(Activity context) {
            this.context = context;
        }

        public Builder setPosition(int position) {
            this.position = position;
            return this;
        }

        public Builder setImageUrlList(ArrayList<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
            return this;
        }

        public Builder setDeleteFile(boolean deleteFile) {
            isDeleteFile = deleteFile;
            return this;
        }

        public Builder setShowDelete(boolean showDelete) {
            isShowDelete = showDelete;
            return this;
        }

        public Builder setSelect(boolean select) {
            isSelect = select;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public void start() {
            Intent intent = new Intent(context, ImageDetailsActivity.class);
            intent.putExtra(CURRENT_IMAGE_POSITION, position);
            if (imageUrlList != null) {
                intent.putStringArrayListExtra(IMAGEURL_LIST, imageUrlList);
            }
            intent.putExtra(IS_DELETE_FILE, isDeleteFile);
            intent.putExtra(IS_SHOW_PHOTO_FLAG, isShowDelete);
            intent.putExtra(Config.TITLE_NAME, title);
            intent.putExtra("select", isSelect);
            context.startActivityForResult(intent, requestCode);
        }
    }


}
