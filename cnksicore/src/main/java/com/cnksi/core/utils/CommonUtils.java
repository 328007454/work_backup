package com.cnksi.core.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.core.activity.RegisteredActivity;

public class CommonUtils {
	private static long lastClickTime;

	/**
	 * 是否是快速点击
	 * 
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 判断控件是否有内容
	 * 
	 * @param view
	 * @return
	 */
	public static boolean isViewHasContent(View view) {
		boolean flag = true;
		if (view instanceof TextView) {
			flag = TextUtils.isEmpty(((TextView) view).getText().toString().trim());
		} else if (view instanceof EditText) {
			flag = TextUtils.isEmpty(((TextView) view).getText().toString().trim());
		}
		return !flag;
	}

	/**
	 * 注册项目
	 * 
	 * @param mContext
	 */
	public static void register(Context mContext) {
		String date = PreferencesUtils.getString(mContext, CoreConfig.REGISTERED_DATE, "");
		if (TextUtils.isEmpty(date) || DateUtils.compareDate(DateUtils.getCurrentLongTime(), date, CoreConfig.dateFormat1)) {
			mContext.startActivity(new Intent(mContext, RegisteredActivity.class));
		}
	}
}
