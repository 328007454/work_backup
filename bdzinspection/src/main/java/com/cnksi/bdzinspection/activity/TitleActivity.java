package com.cnksi.bdzinspection.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityTitlebaseBinding;
import com.cnksi.common.base.BaseActivity;

public abstract class TitleActivity extends BaseActivity {
	public ImageView back;
	public TextView title;
	public TextView right;
	private FrameLayout content;
	public XsActivityTitlebaseBinding titlebaseBinding;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		titlebaseBinding = DataBindingUtil.setContentView(this,R.layout.xs_activity_titlebase);
		content = findViewById(R.id.mh);
		back = findViewById(R.id.ibtn_cancel);
		back.setOnClickListener(v -> finish());
		title = findViewById(R.id.tv_title);
		right = findViewById(R.id.right);

		SetContentView(setLayout());
		setTitleText(initialUI());
		initialData();
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
	protected abstract String initialUI();

	/**
	 * 加载数据，该方法调用在initUI之后。
	 */
	protected abstract void initialData();

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

	@Override
	public void finish() {
		releaseResAndSaveData();
		super.finish();
	}

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
