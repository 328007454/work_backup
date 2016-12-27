package com.cnksi.core.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;

/*
 * 画圆
 * 
 * 无论拖动也好，拉伸也好，其实都是重新画圆，
 * 只是不改变画圆需要的某些属性进行绘制，这样看起来就行是移动或拖动的
 */
public class DrawCircle {

	private Point rDotsPoint;// 圆心
	private int radius = 0;// 半径
	private Double dtance = 0.0;// 当前点到圆心的距离
	private int downState;
	private Point downPoint = new Point();

	public Point eventPoint = new Point();
	public Paint paint;// 声明画笔
	/**
	 * 屏幕宽度
	 */
	private int screenWidth = 0;

	public DrawCircle(int screenWidth) {
		this.screenWidth = screenWidth;
		// 设置画笔
		paint = new Paint();
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(5);// 笔宽5像素
		paint.setColor(Color.RED);// 设置为红笔
		paint.setAntiAlias(true);// 锯齿不显示
		rDotsPoint = new Point();
	}

	public void onTouchDown(Point point) {
		downPoint.set(point.x, point.y);

		if (radius != 0) {
			// 计算当前所点击的点到圆心的距离
			dtance = Math.sqrt((downPoint.x - rDotsPoint.x) * (downPoint.x - rDotsPoint.x) + (downPoint.y - rDotsPoint.y) * (downPoint.y - rDotsPoint.y));
			// 如果距离半径减20和加20个范围内，则认为用户点击在圆上
			if (dtance >= radius - 20 && dtance <= radius + 20) {
				downState = 1;
				// 如果距离小于半径，则认为用户点击在圆内
			} else if (dtance < radius) {
				downState = -1;
				// 当前点击的点在园外
			} else if (dtance > radius) {
				downState = 0;
			}
		} else {
			downState = 0;
		}
	}

	public void onTouchMove(Point point) {

		switch (downState) {
		// 如果在圆内，则重新设定圆心坐标
		case -1:
			rDotsPoint.set(point.x, point.y);
			break;
		// 如果在圆上，则圆心坐标不变，重新设定圆的半径
		case 1:
			if (radius < screenWidth / 3) {
				radius = (int) Math.sqrt((point.x - rDotsPoint.x) * (point.x - rDotsPoint.x) + (point.y - rDotsPoint.y) * (point.y - rDotsPoint.y));
			}
			break;
		// 如果在圆外，重新画圆
		default:
			rDotsPoint.set(downPoint.x, downPoint.y);
			radius = (int) Math.sqrt((point.x - rDotsPoint.x) * (point.x - rDotsPoint.x) + (point.y - rDotsPoint.y) * (point.y - rDotsPoint.y));
			break;
		}
	}

	public void drawCircle(Canvas canvas) {
		canvas.drawCircle(rDotsPoint.x, rDotsPoint.y, radius, paint);// 画圆
	}

	public void drawOval(Canvas canvas, RectF rectf) {
		rectf.set(rDotsPoint.x - 50, rDotsPoint.y - 25, rDotsPoint.x + 50, rDotsPoint.y + 25);
		canvas.drawOval(rectf, paint);
	}

}
