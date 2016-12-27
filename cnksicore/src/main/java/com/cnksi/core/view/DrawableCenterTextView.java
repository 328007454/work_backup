package com.cnksi.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * drawableLeft与文本一起居中显示
 * 
 * 
 */
public class DrawableCenterTextView extends TextView {

	public DrawableCenterTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DrawableCenterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DrawableCenterTextView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable[] drawables = getCompoundDrawables();
		if (drawables != null) {
			Drawable drawableLeft = drawables[0];
			if (drawableLeft != null) {
				float textWidth = getPaint().measureText(getText().toString());// 文本的宽度
				int drawablePadding = getCompoundDrawablePadding();// drawablePadding值
				int drawableWidth = drawableLeft.getIntrinsicWidth();// icon的宽度
				float bodyWidth = textWidth + drawableWidth + drawablePadding;// icon和文字都居中
				canvas.translate((getWidth() - bodyWidth) / 2 - drawablePadding, 0);
			}
		}
		super.onDraw(canvas);
	}

}
