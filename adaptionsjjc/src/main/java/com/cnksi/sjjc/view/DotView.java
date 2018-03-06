package com.cnksi.sjjc.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.cnksi.sjjc.R;

public class DotView extends View {
	private Paint p;
	private int width;
	private int height;
	private int dash;

	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DotView(Context context) {
		super(context);
		init(context);
	}
	@SuppressWarnings("unchecked")
	private void init(Context context){
		p = new Paint(Paint.ANTI_ALIAS_FLAG);
		p.setStyle(Style.FILL);
//		context.getResources().getColor(R.color.gray_stroke_color)
		p.setColor(ContextCompat.getColor(context,R.color.gray_stroke_color));

		final float scale = context.getResources().getDisplayMetrics().density;
		dash=(int) (6 * scale + 0.5f);
	}
	@Override
	protected void onDraw(Canvas canvas) {

		if(width>10){
			for(int i=0;i<width;i+=dash){
				canvas.drawLine(i, 0, i+=dash, 0, p);
			}
		}
		super.onDraw(canvas);
	}

	@Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
        p.setStrokeWidth(height);
        this.postInvalidate();
    }
}
