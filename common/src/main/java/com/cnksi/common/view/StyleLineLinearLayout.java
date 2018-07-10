package com.cnksi.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cnksi.common.R;


/**
 * @decrption 自定义linearlayout可以画边线
 * @author Mr.K  on 2018/4/24.
 */

public class StyleLineLinearLayout extends LinearLayout {

    private int height;
    private int width;
    private Paint paint;
    private int leftLineColor;
    private int rightLineColor;
    private int topLineColor;
    private int bottomLineColor;
    private boolean leftLineVisible;
    private boolean rightLineVisible;
    private boolean topLineVisible;
    private boolean bottomLineVisible;
    private int marginLeft;
    private int marginRight;
    private int marginBottom;
    private int marginTop;
    private int lineSize;

    private int bottomLineMarginBottom;

    private Path path = new Path();

    public StyleLineLinearLayout(Context context) {
        this(context, null);
    }

    public StyleLineLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StyleLineLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StyleLineLinearLayout);
        leftLineColor = array.getColor(R.styleable.StyleLineLinearLayout_left_line_color, Color.GRAY);
        rightLineColor = array.getColor(R.styleable.StyleLineLinearLayout_right_line_color, Color.GRAY);
        topLineColor = array.getColor(R.styleable.StyleLineLinearLayout_top_line_color, Color.RED);
        bottomLineColor = array.getColor(R.styleable.StyleLineLinearLayout_bottom_line_color, Color.GRAY);
        bottomLineVisible = array.getBoolean(R.styleable.StyleLineLinearLayout_bottom_line_visible, false);
        topLineVisible = array.getBoolean(R.styleable.StyleLineLinearLayout_top_line_visible, false);
        rightLineVisible = array.getBoolean(R.styleable.StyleLineLinearLayout_right_line_visible, false);
        leftLineVisible = array.getBoolean(R.styleable.StyleLineLinearLayout_left_line_visible, false);
        marginBottom = array.getInt(R.styleable.StyleLineLinearLayout_margin_bottom_line_size, 0);
        marginTop = array.getInt(R.styleable.StyleLineLinearLayout_margin_top_line_size, 0);
        marginRight = array.getInt(R.styleable.StyleLineLinearLayout_margin_right_line_size, 0);
        marginLeft = array.getInt(R.styleable.StyleLineLinearLayout_margin_left_line_size, 0);
        lineSize = array.getInt(R.styleable.StyleLineLinearLayout_line_size, 2);

        bottomLineMarginBottom = array.getInt(R.styleable.StyleLineLinearLayout_margin_bottom_bottom_line_size, 0);
        array.recycle();
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(bottomLineColor);
        paint.setAntiAlias(false);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        setWillNotDraw(false);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (bottomLineVisible) {
            path.reset();
            paint.setStrokeWidth(lineSize);
            paint.setColor(bottomLineColor);
            path.moveTo(marginLeft, height - bottomLineMarginBottom);
            path.lineTo(width - marginRight, height - bottomLineMarginBottom);
            canvas.drawPath(path, paint);
        }

        if (topLineVisible) {
            path.reset();
            paint.setStrokeWidth(lineSize);
            paint.setColor(topLineColor);
            path.moveTo(marginLeft, 0);
            path.lineTo(width - marginRight, 0);
            canvas.drawPath(path, paint);
        }

        if (leftLineVisible) {
            path.reset();
            paint.setStrokeWidth(lineSize);
            paint.setColor(leftLineColor);
            path.moveTo(0,  marginTop);
            path.lineTo(0, height - marginBottom);
            canvas.drawPath(path, paint);
        }

        if (rightLineVisible) {
            path.reset();
            paint.setStrokeWidth(lineSize);
            paint.setColor(rightLineColor);
            path.moveTo(0,  marginTop);
            path.lineTo(0, height - marginBottom);
            canvas.drawPath(path, paint);
        }
    }


}
