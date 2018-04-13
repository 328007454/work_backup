package com.cnksi.bdzinspection.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by han on 2016/12/8.
 */

public class FileProgressBar extends ProgressBar {

    private String txtProgress;
    private Paint txtPaint;
    private int mCurrentProgress;
    public FileProgressBar(Context context) {
        this(context, null);
    }

    public FileProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextStyle();
    }

    private void initTextStyle() {
        this.txtPaint = new Paint();
        txtPaint.setColor(Color.WHITE);
        txtPaint.setAntiAlias(false);
        txtPaint.setTextSize(40);

    }

    @Override
    public synchronized void setProgress(int progress) {
        this.mCurrentProgress = progress;
        setText(progress);
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        txtPaint.getTextBounds(txtProgress, 0, txtProgress.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(txtProgress, x, y, txtPaint);
    }

    private void setText(int progress) {
        int i = (progress * 100) / this.getMax();
        this.txtProgress = String.valueOf(i) + "%";
        invalidate();
    }
}
