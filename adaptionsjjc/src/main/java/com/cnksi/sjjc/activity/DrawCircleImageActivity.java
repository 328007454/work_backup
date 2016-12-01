package com.cnksi.sjjc.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.CustomerDialog.DialogClickListener;
import com.cnksi.core.view.PicturePaintView;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

/**
 * 缺陷照片标记圆圈的界面
 * 
 * @author Oliver
 * 
 */
public class DrawCircleImageActivity extends BaseActivity {

	@ViewInject(R.id.rl_circle_picture)
	private RelativeLayout mRlImageContainer;

	@ViewInject(R.id.ll_image_container)
	private LinearLayout mLLImageContainer;
	/**
	 * 图片描述内容
	 */
	@ViewInject(R.id.tv_picture_content)
	private TextView mTvPictureContent;

	private PicturePaintView mPicturePaintView;
	/**
	 * 图片路径
	 */
	private String currentImagePath = "";
	/**
	 * 是否保存图片
	 */
	private boolean isSavePicture = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setChildView(R.layout.activity_draw_circle);
		x.view().inject(this);

		initUI();
	}

	private void initUI() {
		currentImagePath = getIntent().getStringExtra(Config.CURRENT_IMAGE_NAME);
		tvTitle.setText("标记图片");
		initBitmap();
	}

	/**
	 * 初始化画布
	 */
	private void initBitmap() {
		CustomerDialog.showProgress(_this, "正在初始化图片...");
		mFixedThreadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {

				int screenWidth = ScreenUtils.getScreenWidth(_this);
				int screenHeight = ScreenUtils.getScreenHeight(_this);
				// 首先压缩图片
				File file = new File(currentImagePath);
				if (file.exists()) {
					BitmapUtil.compressImage(file.getAbsolutePath(), 3, 1024l * 1024l);
				}
				Bitmap bitmapTemp = BitmapUtil.getImageThumbnail(BitmapUtil.postRotateBitmap(currentImagePath, true), screenWidth, screenHeight);
				if (bitmapTemp != null) {
					mPicturePaintView = new PicturePaintView(_this, bitmapTemp);
					mHandler.sendEmptyMessage(SAVE_DATA);
				}
			}
		});
	}

	@Event({ R.id.btn_back, R.id.btn_add_mark, R.id.btn_clear_mark, R.id.btn_save_mark })
	private void OnViewClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:

			onBackPressed();

			break;
		case R.id.btn_save_mark:

			saveMarkAndExit();

			break;
		case R.id.btn_add_mark:

			PicturePaintView.saveMark();

			break;
		case R.id.btn_clear_mark:

			CustomerDialog.showSelectDialog(_this, "确认要清除所有标记吗?", new DialogClickListener() {

				@Override
				public void confirm() {
					initBitmap();
				}

				@Override
				public void cancel() {

				}
			});

			break;
		default:
			break;
		}
	}

	@Override
	protected void onRefresh(Message msg) {
		CustomerDialog.dismissProgress();
		switch (msg.what) {
		case LOAD_DATA:

			onBackPressed();

			break;
		case SAVE_DATA:
			mLLImageContainer.removeAllViews();
			mLLImageContainer.addView(mPicturePaintView);
			mTvPictureContent.setText(getIntent().getStringExtra(Config.PICTURE_CONTENT));
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
		CustomerDialog.showProgress(_this, "正在保存图片...");
		mFixedThreadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				PicturePaintView.saveMark();
				if (BitmapUtil.saveEditPicture(mRlImageContainer, currentImagePath, 80)) {
					mHandler.sendEmptyMessage(LOAD_DATA);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (isSavePicture) {
			setResult(RESULT_OK, getIntent());
			this.finish();
		} else {
			saveMarkAndExit();
		}
	}
}
