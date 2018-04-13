package com.cnksi.sjjc.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.cnksi.sjjc.R;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/1/17 11:26
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class UnderLineTextView extends AppCompatTextView {
    private Paint dividerPaint;
    private int dividerWidth = 6;
    private int textSelectColor;

    public int getDividerWidth() {
        return dividerWidth;
    }

    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        dividerPaint.setStrokeWidth(dividerWidth);
        postInvalidate();
    }

    public int getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextSelectColor(int textColor) {
        this.textSelectColor = textColor;
        ColorStateList colorStateList = new ColorStateList(new int[][]{{android.R.attr.state_selected}, {-android.R.attr.state_selected}}, new int[]{textSelectColor, getCurrentTextColor()});
        setTextColor(colorStateList);
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        dividerPaint.setColor(lineColor);
        postInvalidate();
    }

    private int lineColor;

    public UnderLineTextView(Context context) {
        super(context);
        init(null);
    }

    public UnderLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UnderLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UnderLineTextView, 0, 0);
        dividerWidth = a.getDimensionPixelSize(R.styleable.UnderLineTextView_sjjclineHeight, 6);
        lineColor = a.getColor(R.styleable.UnderLineTextView_lineColor, Color.RED);
        textSelectColor = a.getColor(R.styleable.UnderLineTextView_textSelectColor, Color.RED);
        a.recycle();
        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);
        dividerPaint.setColor(lineColor);
        ColorStateList colorStateList = new ColorStateList(new int[][]{{android.R.attr.state_selected}, {-android.R.attr.state_selected}}, new int[]{textSelectColor, getCurrentTextColor()});
        setTextColor(colorStateList);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected())
            canvas.drawLine(AutoUtils.getPercentWidthSize(20), getHeight() - dividerWidth, getWidth()-AutoUtils.getPercentWidthSize(20), getHeight() - dividerWidth, dividerPaint);
    }
}
