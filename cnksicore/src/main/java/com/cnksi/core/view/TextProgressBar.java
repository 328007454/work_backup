package com.cnksi.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.cnksi.core.R;

/**
 * 带文本提示的进度条
 */
public class TextProgressBar extends ProgressBar {
	private String text;
	private Paint mPaint;
	private Rect rect;
	private Context context;

	public TextProgressBar(Context context) {
		super(context);
		this.context = context;
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initText();
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initText();
	}

	@Override
	public void setProgress(int progress) {
		setText(progress);
		super.setProgress(progress);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
		int x = (getWidth() / 2) - rect.centerX();
		int y = (getHeight() / 2) - rect.centerY();
		canvas.drawText(this.text, x, y, this.mPaint);
	}

	// 初始化，画笔
	private void initText() {
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setColor(Color.WHITE);
		this.mPaint.setTextSize(context.getResources().getDimension(R.dimen.progress_bar_textsize));
		rect = new Rect();

	}

	// 设置文字内容
	public void setText(int progress) {
		int i = (int) ((progress * 1.0f / this.getMax()) * 100);
		this.text = String.valueOf(i) + "%";
	}
}