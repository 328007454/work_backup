package com.cnksi.sjjc.View;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.cnksi.sjjc.R;


/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/1/17 11:26
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class UnderLineTextView extends AppCompatTextView {
    Paint dividerPaint;
    int dividerWidth = 6;
    int textColor;
    int lineColor;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UnderLineTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UnderLineTextView, 0, 0);
        dividerWidth = a.getDimensionPixelSize(R.styleable.UnderLineTextView_lineHeight, 6);
        lineColor = a.getColor(R.styleable.UnderLineTextView_lineColor, Color.RED);
        textColor = a.getColor(R.styleable.UnderLineTextView_textColor, Color.RED);
        a.recycle();
        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);
        dividerPaint.setColor(lineColor);
        ColorStateList colorStateList = new ColorStateList(new int[][]{{android.R.attr.state_selected}, {-android.R.attr.state_selected}}, new int[]{textColor, getCurrentTextColor()});
        setTextColor(colorStateList);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected())
            canvas.drawLine(0, getHeight() - dividerWidth, getWidth(), getHeight() - dividerWidth, dividerPaint);
    }
}
