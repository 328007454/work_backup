package com.cnksi.core.view.pullable;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;

import com.cnksi.core.R;

public class PullableListView extends ListView implements Pullable {

	private boolean canPullDown = true;
	private boolean canPullUp = true;

	public void setCanPullDown(boolean canPullDown) {
		this.canPullDown = canPullDown;
	}

	public void setCanPullUp(boolean canPullUp) {
		this.canPullUp = canPullUp;
	}

	public PullableListView(Context context) {
		this(context, null);
	}

	public PullableListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullableView, defStyle, 0);
		canPullDown = a.getBoolean(R.styleable.PullableView_canPullDown, true);
		canPullUp = a.getBoolean(R.styleable.PullableView_canPullUp, true);
		a.recycle();
	}

	@Override
	public boolean canPullDown() {
		if (canPullDown) {
			if (getCount() == 0) {
				// 没有item的时候也可以下拉刷新
				return true;
			} else if (getFirstVisiblePosition() == 0 && getChildAt(0) != null && getChildAt(0).getTop() >= 0) {
				// 滑到ListView的顶部了
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean canPullUp() {
		if (canPullUp) {
			if (getCount() == 0) {
				// 没有item的时候也可以上拉加载
				return true;
			} else if (getLastVisiblePosition() == (getCount() - 1)) {
				// 滑到底部了
				if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null && getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
					return true;
			}
			return false;
		} else {
			return false;
		}
	}
}
