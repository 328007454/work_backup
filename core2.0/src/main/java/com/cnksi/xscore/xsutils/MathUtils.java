package com.cnksi.xscore.xsutils;

import java.math.BigDecimal;

/**
 * 常用的加减乘除方法 保证精度
 * 
 * @author Oliver
 * 
 */
public class MathUtils {

	private static final int DEF_DIV_SCALE = 10;

	private MathUtils() {
		throw new AssertionError();
	}

	/**
	 * 加法 保证精度
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double add(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.add(b2).doubleValue();
	}

	public static float add(float f1, float f2) {
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.add(b2).floatValue();
	}

	/**
	 * 减法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double sub(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();
	}

	public static float sub(float f1, float f2) {
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.subtract(b2).floatValue();
	}

	/**
	 * 乘法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double mul(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.multiply(b2).doubleValue();
	}

	public static float mul(float f1, float f2) {
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.multiply(b2).floatValue();
	}

	/**
	 * 除法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double div(double d1, double d2) {
		return div(d1, d2, DEF_DIV_SCALE);
	}

	public static float div(float f1, float f2) {
		return div(f1, f2, DEF_DIV_SCALE);
	}

	/**
	 * 除法
	 * 
	 * @param d1
	 * @param d2
	 * @param scale
	 * @return
	 */
	public static double div(double d1, double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static float div(float f1, float f2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Float.toString(f1));
		BigDecimal b2 = new BigDecimal(Float.toString(f2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
	}

}
