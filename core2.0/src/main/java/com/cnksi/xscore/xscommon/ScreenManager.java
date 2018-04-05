package com.cnksi.xscore.xscommon;

import android.app.Activity;

import java.util.Stack;

public class ScreenManager {

	private  Stack<Activity> activityStack;

	private ScreenManager() {
		if (activityStack == null) {
			activityStack = new Stack<>();
		}
	}

	private static class SingleTonHolder {
		private static final ScreenManager INSTANCE = new ScreenManager();
	}

	public static ScreenManager getInstance() {
		return SingleTonHolder.INSTANCE;
	}

	// 退出栈顶Activity
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
		}
	}

	// 获得当前栈顶Activity
	public Activity currentActivity() {
		Activity activity = null;
		if (activityStack != null && activityStack.size() > 0) {
			activity = activityStack.lastElement();
		}
		return activity;
	}

	// 将当前Activity推入栈中
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	// 退出栈中所有Activity
	public void popAllActivityExceptOne(Class<?> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}
	
	public boolean hasActivity(Class<?> cls) {
		boolean flag = false;
		if (activityStack != null && !activityStack.isEmpty()) {
			for (Activity tempActivity : activityStack) {
				if (tempActivity.getClass().equals(cls)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public void popActivity(Class<?> cls) {
		if (activityStack != null && !activityStack.isEmpty()) {
			for (Activity tempActivity : activityStack) {
				if (tempActivity.getClass().equals(cls)) {
					popActivity(tempActivity);
				}
			}
		}
	}

	public void popActivityList(Class<?>... clsList) {
		if (activityStack != null && !activityStack.isEmpty()) {
			for (Class<?> cls : clsList) {
				popActivity(cls);
			}
		}
	}
}