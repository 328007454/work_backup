package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityTitlebaseBinding;

public abstract class TitleActivity extends BaseActivity {
	public ImageView back;
	public TextView title;
	public TextView right;
	private FrameLayout content;
	private Activity _this;
	public XsActivityTitlebaseBinding titlebaseBinding;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_this = this;
		titlebaseBinding = DataBindingUtil.setContentView(this,R.layout.xs_activity_titlebase);
		content = (FrameLayout) findViewById(R.id.mh);
		back = (ImageView) findViewById(R.id.ibtn_cancel);
		back.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            _this.finish();
        });
		title = (TextView) findViewById(R.id.tv_title);
		right = (TextView) findViewById(R.id.right);

		SetContentView(setLayout());
		setTitleText(initUI());
		initData();
	}

	/**
	 * 设置标题栏下方布局
	 *
	 * @return
	 */
	protected abstract int setLayout();

	/**
	 * 初始化UI
	 *
	 * @return 返回标题
	 */
	protected abstract String initUI();

	/**
	 * 加载数据，该方法调用在initUI之后。
	 */
	protected abstract void initData();

	/**
	 * 释放资源 在onDestroy中调用 该函数在super.onDestroy()之前调用
	 *
	 */

	protected abstract void releaseResAndSaveData();
	private ViewDataBinding dataBinding;
	private void SetContentView(int rsid) {
		content.removeAllViews();
		dataBinding = DataBindingUtil.inflate(getLayoutInflater(),rsid,null,false);
		content.addView(dataBinding.getRoot());
	}

	public  ViewDataBinding getDataBinding(){
		return  dataBinding;
	}

	protected void setTitleText(String t) {
		title.setText(t);
	}

	protected void setLeftButtonInvisable() {
		back.setVisibility(View.INVISIBLE);
	}

	/**
	 * 重定义back按钮的功能 如果为NULL 将取消back的点击事件
	 *
	 * @param listener
	 */
	protected void setBackListener(@Nullable OnClickListener listener) {
		back.setOnClickListener(listener);
	}

	@Override
	public void finish() {
		releaseResAndSaveData();
		super.finish();
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
