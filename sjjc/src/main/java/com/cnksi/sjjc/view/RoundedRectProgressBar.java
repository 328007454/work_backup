package com.cnksi.sjjc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.cnksi.sjjc.R;

/**
 * Created by Administrator on 2017/3/23.
 */

public class RoundedRectProgressBar extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int barColor;
    private int backColor;
    private int textColor;
    private float radius;
    int colorLift, colorRight;
    int progress = 0;

    public RoundedRectProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        /*获取自定义参数的颜色值*/
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundedRectProgressBar, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.RoundedRectProgressBar_backColor) {
                backColor = a.getColor(attr, getResources().getColor(R.color.progress_backgroud));

            } else if (attr == R.styleable.RoundedRectProgressBar_barColor) {
                barColor = a.getColor(attr, Color.GREEN);

            } else if (attr == R.styleable.RoundedRectProgressBar_textColor) {
                textColor = a.getColor(attr, Color.WHITE);
            }

        }
        a.recycle();
        colorLift = getResources().getColor(R.color.progress_color_1);
        colorRight = getResources().getColor(R.color.progress_color_2);
    }

    public RoundedRectProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedRectProgressBar(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = this.getMeasuredHeight() / 2;
    }


    Paint paint = new Paint();
    RectF backgroundRectF, progressRectF = new RectF();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        mPaint.setColor(backColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(backgroundRectF, radius, radius, mPaint);
        //进度条
        paint.setStyle(Paint.Style.FILL);
        float x = this.getMeasuredWidth() * progress / 100f;
        LinearGradient gradient = new LinearGradient(0, 0, x, 0, colorLift, colorRight, Shader.TileMode.MIRROR);
        paint.setShader(gradient);
        progressRectF.set(0, 0, x, this.getMeasuredHeight());
        canvas.drawRoundRect(progressRectF, radius, radius, paint);
        //进度
        mPaint.setColor(textColor);
        mPaint.setTextSize(this.getMeasuredHeight() / 1.2f);
        String text = "" + progress + "%";
        x = x - mPaint.measureText(text) - 10;
        float y = this.getMeasuredHeight() / 2f - mPaint.getFontMetrics().ascent / 2f - mPaint.getFontMetrics().descent / 2f;
        canvas.drawText(text, x, y, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backgroundRectF = new RectF(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
    }

    /*设置进度条进度, 外部调用*/
    public void setProgress(int progress) {
        if (progress > 100) {
            this.progress = 100;
        } else if (progress < 0) {
            this.progress = 0;
        } else {
            this.progress = progress;
        }
        postInvalidate();
    }

}
