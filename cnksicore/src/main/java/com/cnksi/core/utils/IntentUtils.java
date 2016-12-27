package com.cnksi.core.utils;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentUtils {

	/**
	 * 打电话
	 * 
	 * @param mContext
	 * @param phoneNumber
	 */
	public static void callPhone(Context mContext, String phoneNumber) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + phoneNumber));
		mContext.startActivity(intent);
	}

	/**
	 * 发送短信
	 * 
	 * @param mContext
	 * @param smsBody
	 * @param phoneNumber
	 */
	public static void sendMessage(Context mContext, String smsBody, String phoneNumber) {
		Uri smsToUri = Uri.parse("smsto:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		mContext.startActivity(intent);
	}

	/**
	 * 发送短信
	 * 
	 * @param mContext
	 * @param smsBody
	 * @param phoneNumber
	 */
	public static void sendMessage(Context mContext, String smsBody) {
		sendMessage(mContext, smsBody, "");
	}

	/**
	 * 打开 doc ppt pdf xls txt html chm 文件
	 * 
	 * @param context
	 * @param filePath
	 */
	public static void openDocumentFile(Context context, String filePath) {
		File file = new File(filePath);
		if (file != null && file.exists()) {
			try {
				/* 取得扩展名 */
				String extension = StringUtils.getExtensionName(filePath);
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.fromFile(new File(filePath));
				intent.setDataAndType(uri, getDataType(extension));
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				CToast.showShort(context, "没有找到应用打开该文件,请下载WPS");
			}
		} else {
			CToast.showShort(context, "文件不存在!");
		}
	}

	public static String getDataType(String filePath) {
		/* 取得扩展名 */
		String extension = StringUtils.getExtensionName(filePath);
		if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
			return "application/vnd.ms-powerpoint";
		} else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
			return "application/vnd.ms-excel";
		} else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
			return "application/msword";
		} else if (extension.equalsIgnoreCase("pdf")) {
			return "application/pdf";
		} else if (extension.equalsIgnoreCase("chm")) {
			return "application/x-chm";
		} else if (extension.equalsIgnoreCase("txt")) {
			return "text/plain";
		}
		return "";
	}

}
