package com.cnksi.core.view.pullable;

import com.cnksi.core.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.webkit.WebView;

public class PullableWebView extends WebView implements Pullable {
	private boolean canPullDown = true;
	private boolean canPullUp = true;

	public void setCanPullDown(boolean canPullDown) {
		this.canPullDown = canPullDown;
	}

	public void setCanPullUp(boolean canPullUp) {
		this.canPullUp = canPullUp;
	}

	public PullableWebView(Context context) {
		this(context, null);
	}

	public PullableWebView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PullableWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullableView, defStyle, 0);
		canPullDown = a.getBoolean(R.styleable.PullableView_canPullDown, true);
		canPullUp = a.getBoolean(R.styleable.PullableView_canPullUp, true);
		a.recycle();
	}

	@Override
	public boolean canPullDown() {
		if (canPullDown) {
			if (getScrollY() == 0)
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canPullUp() {
		if (canPullUp) {
			if (getScrollY() >= getContentHeight() * getScale() - getMeasuredHeight())
				return true;
			else
				return false;
		} else {
			return false;
		}
	}
}
