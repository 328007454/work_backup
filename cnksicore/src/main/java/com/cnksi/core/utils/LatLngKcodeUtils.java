package com.cnksi.core.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class LatLngKcodeUtils {

	private static char str[] = new char[34];
	private final static double eps = 1e-8;

	static {
		str[0] = '0';
		str[1] = '1';
		str[2] = '2';
		str[3] = '3';
		str[4] = '4';
		str[5] = '5';
		str[6] = '6';
		str[7] = '7';
		str[8] = '8';
		str[9] = '9';
		str[10] = 'a';
		str[11] = 'b';
		str[12] = 'c';
		str[13] = 'd';
		str[14] = 'e';
		str[15] = 'f';
		str[16] = 'g';
		str[17] = 'h';
		str[18] = 'i';
		str[19] = 'j';
		str[20] = 'k'; // no character L
		str[21] = 'm';
		str[22] = 'n'; // no character O
		str[23] = 'p';
		str[24] = 'q';
		str[25] = 'r';
		str[26] = 's';
		str[27] = 't';
		str[28] = 'u';
		str[29] = 'v';
		str[30] = 'w';
		str[31] = 'x';
		str[32] = 'y';
		str[33] = 'z';
	}

	/**
	 * 判断经纬度是否符合要求
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static String judgementLatLng(double lat, double lon) {

		if (lon >= 140.0 + eps || lon <= 70.0 - eps)
			return "经度在70-140度之间...";
		if (lat >= 75.0 + eps || lat <= 5.0 - eps)
			return "纬度在5-75度之间...";
		return "";
	}

	/**
	 * 经纬度转换成K码
	 * 
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @return
	 */
	public static String latLngConvertToKcode(String lat, String lng) {
		return latLngConvertToKcode(Double.parseDouble(lat), Double.parseDouble(lng));
	}

	/**
	 * 经纬度转换成K码
	 * 
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @return
	 */
	public static String latLngConvertToKcode(double lat, double lng) {
		String ans = "";
		if (lng > 105.0 - eps && lat > 40.0 - eps)
			ans += '5';
		else if (lng > 105.0 - eps && lat <= 40.0 - eps)
			ans += '8';
		if (lng <= 105.0 - eps && lat > 40.0 - eps)
			ans += '6';
		else if (lng <= 105.0 - eps && lat <= 40.0 - eps)
			ans += '7';
		if (lng > 105.0 - eps)
			lng -= 105.0;
		else
			lng -= 70.0;
		if (lat > 40.0 - eps)
			lat -= 40.0;
		else
			lat -= 5.0;
		lng *= 3600 * 10;
		lat *= 3600 * 10;
		int longitude = (int) (lng + 0.5);
		int latitude = (int) (lat + 0.5);
		int idx = 0;
		while (idx < 4) {
			ans += str[longitude % 34];
			longitude /= 34;
			idx++;
		}
		idx = 0;
		while (idx < 4) {
			ans += str[latitude % 34];
			latitude /= 34;
			idx++;
		}
		return ans;
	}

	/**
	 * 经纬度转换成K码
	 * 
	 * @param latlng
	 *
	 * @return
	 */
	public static String latLngConvertToKcode(double[] latlng) {
		if (latlng != null && latlng.length == 2) {
			return latLngConvertToKcode(latlng[0], latlng[1]);
		}
		return "";
	}

	/**
	 * 经纬度转换成K码
	 * 
	 * @param latlng
	 * @return
	 */
	public static String latLngConvertToKcode(String[] latlng) {
		if (latlng != null && latlng.length == 2) {
			return latLngConvertToKcode(latlng[0], latlng[1]);
		}
		return "";
	}

	private static int find(char ch) {
		for (int i = 0; i < 34; i++)
			if (str[i] == ch)
				return i;
		return -1;
	}

	/**
	 * 判断K码是否正确
	 * 
	 * @param kCode
	 * @return
	 */
	public static String judgementKcode(String kCode) {
		if (kCode == null || kCode.length() != 9)
			return "K5码长度为9...";
		if (!(kCode.charAt(0) >= '5' && kCode.charAt(0) <= '8'))
			return "K5码第一位为5-8...";
		for (int i = 1; i < kCode.length(); i++)
			if (find(kCode.charAt(i)) == -1)
				return "K5码第2-9位为数字和小写字母(除l和o)...";
		return "";
	}

	private static double calculate(String kCode, int st, int ed) {
		int _34_ = 1;
		double ans = 0.0;
		for (int i = st; i <= ed; i++) {
			ans += find(kCode.charAt(i)) * _34_;
			_34_ *= 34;
		}
		ans /= 10;
		ans /= 3600;
		return ans;

	}

	/**
	 * K码转换为经纬度
	 * 
	 * @param kCode
	 * @return latlng[0]纬度 latlng[1]经度 返回的是国标GPS经纬度
	 */
	public static double[] kCodeConvertToLatLng(String kCode) {
		double ans[] = new double[2];
		ans[0] = calculate(kCode, 5, 8);
		ans[1] = calculate(kCode, 1, 4);
		if (kCode.charAt(0) == '5') {
			ans[0] += 40;
			ans[1] += 105;
		} else if (kCode.charAt(0) == '6') {
			ans[0] += 40;
			ans[1] += 70;
		} else if (kCode.charAt(0) == '7') {
			ans[0] += 5;
			ans[1] += 70;
		} else if (kCode.charAt(0) == '8') {
			ans[0] += 5;
			ans[1] += 105;
		}
		ans[0] = new BigDecimal(ans[0]).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		ans[1] = new BigDecimal(ans[1]).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
		return ans;
	}

	/**
	 * 将灵图编码转换为经纬度 "3068-4340-1350"
	 * 
	 * @param ltbm
	 * @return String[0] latitude纬度 String[1] longitude经度
	 */
	public static String[] ltm2Latlng(String ltbm) {
		String[] lngLat = new String[2];
		StringBuilder longitude = new StringBuilder();
		StringBuilder latitude = new StringBuilder();
		if (ltbm.length() == 14 && ltbm.charAt(4) == '-' && ltbm.charAt(9) == '-') {
			if (ltbm.charAt(13) < '5') {
				longitude.append("1").append(ltbm.charAt(13));
			} else {
				longitude.append(ltbm.charAt(13));
			}
			longitude.append(ltbm.charAt(7)).append(".").append(ltbm.charAt(8)).append(ltbm.charAt(2)).append(ltbm.charAt(6)).append(ltbm.charAt(10));
			latitude.append(ltbm.charAt(11)).append(ltbm.charAt(1)).append(".").append(ltbm.charAt(12)).append(ltbm.charAt(5)).append(ltbm.charAt(0)).append(ltbm.charAt(3));
		}
		lngLat[0] = latitude.toString();
		lngLat[1] = longitude.toString();
		return lngLat;
	}

	/**
	 * 经纬度转灵图编码 "30.5438", "104.0631"
	 * 
	 * @param latStr
	 *            纬度
	 * @param lngStr
	 * 
	 *            经度
	 * @return
	 */
	public static String latLng2Ltm(String latStr, String lngStr) {
		StringBuilder ltbm = new StringBuilder("");
		double dlongitude = Double.parseDouble(lngStr);
		double llatitude = Double.parseDouble(latStr);
		if (dlongitude < 180 && dlongitude > 0 && llatitude < 90 && llatitude > 0) {
			String longitude = new DecimalFormat("000.000000").format(dlongitude);
			String latitude = new DecimalFormat("00.000000").format(llatitude);
			ltbm.append(latitude.substring(5, 6)).append(latitude.substring(1, 2)).append(longitude.substring(5, 6)).append(latitude.substring(6, 7));
			ltbm.append("-");
			ltbm.append(latitude.substring(4, 5)).append(longitude.substring(6, 7)).append(longitude.substring(2, 3)).append(longitude.substring(4, 5));
			ltbm.append("-");
			ltbm.append(longitude.substring(7, 8)).append(latitude.substring(0, 1)).append(latitude.substring(3, 4)).append(longitude.substring(1, 2));
		}
		return ltbm.toString();
	}
}
