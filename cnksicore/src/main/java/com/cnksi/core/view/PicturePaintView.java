package com.cnksi.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PicturePaintView extends View {

    /**
     * 字体大小
     */
    private static final float textSize = 28f;
    private DrawCircle drawCircle = null;
    private Point eventPoint;
    /**
     * bitmap对应的canvas
     */
    private Canvas floorCanvas;
    private Canvas surfaceCanvas;
    /**
     * 底层与表层bitmap
     */
    private Bitmap floorBitmap;
    private Bitmap surfaceBitmap;
    /**
     * 背景图片
     */
    private Bitmap bitmap;
    private boolean flag = true;

    /**
     * 屏幕宽度
     */
    private int screenWidth = 1024;
    private int screenHeight = 1024;
    /**
     * 画笔
     */
    private Paint paint;

    public Bitmap getCachebBitmap() {
        return floorBitmap;
    }

    public PicturePaintView(Context context) {
        super(context);
    }

    public PicturePaintView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PicturePaintView(Context context, AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
    }

    public PicturePaintView(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
//        screenWidth = ScreenUtils.getScreenWidth(context);
//        screenHeight = ScreenUtils.getScreenHeight(context);

        init(context);
    }


    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {

        eventPoint = new Point();
        drawCircle = new DrawCircle(screenWidth);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);

        // 表面bitmap。置于底层bitmap之上，用于赋值绘制当前的所画的图形；需要设置为透明，否则覆盖底部bitmap
        surfaceBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        surfaceCanvas = new Canvas(surfaceBitmap);
        surfaceCanvas.drawColor(Color.TRANSPARENT);
        // 底层bitmap与canvas，
        floorBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Config.ARGB_8888);
        floorCanvas = new Canvas(floorBitmap);
        floorCanvas.drawBitmap(this.bitmap, this.getMatrix(), paint);
    }

    /**
     * 保存笔记
     */
    public void saveMark() {
        // 如果重新选择了图形，则需要将表层bitmap上的图像绘制到底层bitmap上进行保存
        floorCanvas.drawBitmap(surfaceBitmap, 0, 0, null);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (flag) {
            invalidate();
            flag = false;
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(floorBitmap, 0, 0, null);
//        drawCircle.drawCircle(surfaceCanvas);
        canvas.drawBitmap(surfaceBitmap, 0, 0, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        eventPoint.set((int) event.getX(), (int) event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                drawCircle.onTouchDown(eventPoint);

                break;
            case MotionEvent.ACTION_MOVE:
                drawCircle.onTouchMove(eventPoint);
            /*
             * 拖动过程中不停的将bitmap的颜色设置为透明（清空表层bitmap） 否则整个拖动过程的轨迹都会画出来
			 */
                surfaceBitmap.eraseColor(Color.TRANSPARENT);
                invalidate();
                break;
        }
        return true;
    }


    public void  setBitmapNull(){
        if(null !=floorCanvas){
            floorCanvas = null;
        }

        if (surfaceCanvas != null) {
            surfaceCanvas = null;
        }
        if (null != surfaceBitmap && !surfaceBitmap.isRecycled()) {
            surfaceBitmap.recycle();
            surfaceBitmap = null;
        }
        if (null != floorBitmap && !floorBitmap.isRecycled()) {
            floorBitmap.recycle();
            floorBitmap = null;
        }
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}