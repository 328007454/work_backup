package com.cnksi.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.cnksi.common.R;

/**
 * Created by Mr.K on 2018/7/10.
 */

public class LineGridView extends GridView {
    public LineGridView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public LineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO 自动生成的方法存根
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int column = getNumColumns();
        int childCount = getChildCount();
        Paint localPaint;
        localPaint = new Paint();
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(3);
        localPaint.setColor(getContext().getResources().getColor(R.color.color_f3f4f4));
//        for (int i = 0; i < childCount; i++) {
//            View cellView = getChildAt(i);
//            if ((i + 1) % column == 0) {
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom(), cellView.getRight(), cellView.getBottom(), localPaint);
//            } else if ((i + 1) > (childCount - (childCount % column))) {
//                canvas.drawLine(cellView.getRight(), cellView.getTop() + 45, cellView.getRight(), cellView.getBottom() - 45, localPaint);
//            } else {
//                canvas.drawLine(cellView.getRight(), cellView.getTop() + 45, cellView.getRight(), cellView.getBottom() - 45, localPaint);
//                canvas.drawLine(cellView.getLeft(), cellView.getBottom() + 45, cellView.getRight(), cellView.getBottom() - 45, localPaint);
//            }
//        }
//        if (childCount % column != 0) {
//            for (int j = 0; j < (column - childCount % column); j++) {
//                View lastView = getChildAt(childCount - 1);
//                canvas.drawLine(lastView.getRight() + lastView.getWidth() * j, lastView.getTop() + 45, lastView.getRight() + lastView.getWidth() * j, lastView.getBottom() - 45, localPaint);
//            }
//        }

        int childPosition = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if ((i + 1) % column == 0) {
                if (childPosition < column) {
                    if ((childPosition + 1) % column == 0) {
                        continue;
                    }
                    canvas.drawLine(childView.getRight(), getTop() + 45, getRight(), getBottom() - 45, localPaint);
                } else if (childPosition >= column) {

                    canvas.drawLine(childView.getLeft(), getTop(), getRight(), getTop(), localPaint);
                    if ((childPosition + 1) % column == 0) {
                        continue;
                    }
                    canvas.drawLine(childView.getRight(), getTop() + 45, getRight(), getBottom() - 45, localPaint);
                }
            }else{

            }

        }
    }
}

