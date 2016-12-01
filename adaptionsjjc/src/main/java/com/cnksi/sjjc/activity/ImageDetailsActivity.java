package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.HackyViewPager;
import com.cnksi.core.view.photo.PhotoView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.util.DialogUtils;

import org.xutils.common.Callback;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

/**
 * 查看大图的Activity界面。
 */
public class ImageDetailsActivity extends BaseActivity implements OnPageChangeListener {

    @ViewInject(R.id.ll_root_container)
    private LinearLayout mLLRootContainer;
    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;
    /**
     * 用于管理图片的滑动
     */
    @ViewInject(R.id.view_pager)
    private HackyViewPager mViewPager;

    /**
     * 显示当前图片的页数
     */
    @ViewInject(R.id.page_text)
    private TextView pageText;
    /**
     *文件名称
     */
    @ViewInject(R.id.filename)
    private TextView fileName;
    /**
     * 删除按钮
     */
    @ViewInject(R.id.ibtn_delete)
    private ImageButton mIbtnDelete;

    /**
     * 得到的图片List
     */
    private ArrayList<String> imageList = null;

    private ArrayList<String> cancelImageList = null;

    private ViewPagerAdapter viewAdapter = null;

    private boolean isShowSelectFlag = false;

    private int position = 0;

    private String titleName = "";
    private Boolean isDeleteFile=true;
    /**
     * 当前的位置
     */
    private int currentPosition = 0;

    private ViewCompleteHolder holder = null;
    /**
     * 提示对话框
     */
    protected Dialog tipsDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_image_details);
        x.view().inject(this);

        imageList = getIntent().getStringArrayListExtra(Config.IMAGEURL_LIST);
        imageList.remove(Config.CURRENT_IMAGE_NAME);
        currentPosition = position = getIntent().getIntExtra(Config.CURRENT_IMAGE_POSITION, 0);
        isShowSelectFlag = getIntent().getBooleanExtra(Config.IS_SHOW_PHOTO_FLAG, true);
        isDeleteFile=getIntent().getBooleanExtra(Config.IS_DELETE_FILE,true);
        titleName = getIntent().getStringExtra(Config.TITLE_NAME);
        mTvTitle.setText(TextUtils.isEmpty(titleName) ? "图片查看" : titleName);

        if (isShowSelectFlag) {
            mIbtnDelete.setVisibility(View.VISIBLE);
        } else {
            mIbtnDelete.setVisibility(View.GONE);
        }
        initData();
    }

    @Event({R.id.btn_back, R.id.ibtn_delete})
    private void OnViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:

                onBackPressed();

                break;
            case R.id.ibtn_delete:
                showSureTipsDialog();
                break;
            default:
                break;
        }
    }

    private void showSureTipsDialog() {
        if (tipsDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            tipsDialog = DialogUtils.createDialog(_this, mLLRootContainer, R.layout.dialog_tips, holder == null ? holder = new ViewCompleteHolder() : holder, dialogWidth, dialogHeight, false);
        }
        holder.mTvDialogTile.setText(R.string.dialog_tips_str);
        holder.mTvDialogContent.setText(R.string.dialog_tips_delete_pic_content);
        holder.mBtnSure.setText(R.string.yes_str);
        holder.mBtnCancel.setText(R.string.no_str);
        tipsDialog.show();
    }

    class ViewCompleteHolder {

        @ViewInject(R.id.tv_dialog_title)
        private TextView mTvDialogTile;

        @ViewInject(R.id.tv_dialog_content)
        private TextView mTvDialogContent;

        @ViewInject(R.id.btn_sure)
        private Button mBtnSure;
        @ViewInject(R.id.btn_cancel)
        private Button mBtnCancel;

        @Event({R.id.btn_sure, R.id.btn_cancel})
        private void OnViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sure:

                    String imageUrl = imageList.get(currentPosition);
                    if (isDeleteFile) {
                        File file = new File(imageUrl);
                        if ( file.isFile() && file.exists()) {// if image is exist , delete it,
                            file.delete();
                        }
                    }
                    imageList.remove(currentPosition);
                    cancelImageList.add(imageUrl);
                    if (imageList.size() == 0) {
                        onBackPressed();
                    }
                    viewAdapter.notifyDataSetChanged();
                    pageText.setText(String.valueOf(currentPosition + 1) + "/" + imageList.size());

                case R.id.btn_cancel:
                    tipsDialog.dismiss();

                    break;

                default:
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initData() {

        viewAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(viewAdapter);
        mViewPager.setCurrentItem(position);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setEnabled(false);
        mViewPager.setOffscreenPageLimit(3);
        // 设定当前的页数和总页数
        pageText.setText((position + 1) + "/" + imageList.size());
        if (imageList.size()>0)
        {
            try{
                if(getIntent().getBooleanExtra(Config.CANCEL_IMAGEURL_LIST,true)){
                    fileName.setText(new File(imageList.get(position).trim()).getName());
                }else{
                    fileName.setVisibility(View.GONE);
                }

            }catch (Exception e){

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

            mBitmapUtils.bind(zoomImageView, imageUrl, CustomApplication.getLargeImageOptions(),
                    new Callback.ProgressCallback<Drawable>() {
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

                        }
                    });
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
        pageText.setText((currentPage + 1) + "/" + imageList.size());
        if (imageList.size()>0)
        {
            try{
                fileName.setText(new File(imageList.get(currentPosition).trim()).getName());
            }catch (Exception e){

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

}
