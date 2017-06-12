package com.cnksi.core.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cnksi.core.R;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.view.datepicker.WheelMain;

import java.util.Calendar;
import java.util.List;

/**
 * 就是自定义的Dialog,不可back或点击外部销毁
 * 
 */
@SuppressLint("InflateParams")
public class CustomerDialog {
	public final static int SELECT_DIALOG = 1;
	public final static int RADIO_DIALOG = 2;
	public final static int UPDATE_DIALOG = 3;
	private static android.app.Dialog mCustomDialog = null;

	/**
	 * 创建一个内容多选对话框
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param items
	 *            数组
	 * @param dialogItemClickListener
	 *            监听点击的内容结果
	 * @return
	 */
	public static android.app.Dialog showListDialog(Activity context, String[] items, final DialogItemClickListener dialogItemClickListener) {
		return ShowDialog(context, items, false, dialogItemClickListener);
	}

	public static android.app.Dialog showListDialog(Activity context, String[] items, boolean isCanceledOnTouchOutside, final DialogItemClickListener dialogItemClickListener) {
		return ShowDialog(context, items, isCanceledOnTouchOutside, dialogItemClickListener);
	}

	public static android.app.Dialog showListDialog(Activity context, List<String> items, final DialogItemClickListener dialogItemClickListener) {
		String[] array = new String[items.size()];
		items.toArray(array);
		return ShowDialog(context, array, false, dialogItemClickListener);
	}

	public static android.app.Dialog showListDialog(Activity context, List<String> items, boolean isCanceledOnTouchOutside, final DialogItemClickListener dialogItemClickListener) {
		String[] array = new String[items.size()];
		items.toArray(array);
		return ShowDialog(context, array, isCanceledOnTouchOutside, dialogItemClickListener);
	}

	/**
	 * 创建一个单选对话框
	 * 
	 * @param context
	 * @param toast
	 *            提示消息
	 * @param dialogClickListener
	 *            点击监听
	 * @return
	 */
	public static android.app.Dialog showRadioDialog(Activity context, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), toast, dialogClickListener, RADIO_DIALOG, context.getResources().getString(R.string.dialog_confirm_text_str), context.getResources().getString(R.string.dialog_cancel_text_str));
	}

	/**
	 * 创建一个选择对话框
	 * 
	 * @param context
	 * @param toast
	 *            提示消息
	 * @param dialogClickListener
	 *            点击监听
	 * @return
	 */
	public static android.app.Dialog showSelectDialog(Activity context, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), toast, dialogClickListener, SELECT_DIALOG, context.getResources().getString(R.string.dialog_confirm_text_str), context.getResources().getString(R.string.dialog_cancel_text_str));
	}

	/**
	 * 创建一个选择对话框
	 * 
	 * @param context
	 * @param toastId
	 *            提示消息
	 * @param dialogClickListener
	 *            点击监听
	 * @return
	 */
	public static android.app.Dialog showSelectDialog(Activity context, int toastId, final DialogClickListener dialogClickListener, int confirmTextId, int canceltextId) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), context.getResources().getString(toastId), dialogClickListener, SELECT_DIALOG, context.getResources().getString(confirmTextId), context.getResources().getString(canceltextId));
	}

	public static android.app.Dialog showSelectDialog(Activity context, final DialogClickListener dialogClickListener, int toastId, Object... toast) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), context.getResources().getString(toastId, toast), dialogClickListener, SELECT_DIALOG, context.getResources().getString(R.string.dialog_confirm_text_str), context.getResources().getString(R.string.dialog_cancel_text_str));
	}

	public static android.app.Dialog showSelectDialog(Activity context, String title, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, title, toast, dialogClickListener, SELECT_DIALOG, context.getResources().getString(R.string.dialog_confirm_text_str), context.getResources().getString(R.string.dialog_cancel_text_str));
	}

	public static android.app.Dialog showSelectDialog(Activity context, String title, String toast, final DialogClickListener dialogClickListener, String confirmText, String cancelText) {
		return ShowDialog(context, title, toast, dialogClickListener, SELECT_DIALOG, confirmText, cancelText);
	}

	public static android.app.Dialog showSelectDialog(Activity context, String title, String toast, final DialogClickListener dialogClickListener, int confirmTextId, int canceltextId) {
		return ShowDialog(context, title, toast, dialogClickListener, SELECT_DIALOG, context.getResources().getString(confirmTextId), context.getResources().getString(canceltextId));
	}

	/**
	 * 显示升级提示框
	 * 
	 * @param context
	 * @param title
	 * @param toast
	 * @param dialogClickListener
	 * @return
	 */
	public static android.app.Dialog showUpdateDialog(Activity context, String title, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, title, toast, dialogClickListener, UPDATE_DIALOG, context.getResources().getString(R.string.down_now_str), context.getResources().getString(R.string.next_time_str));
	}

	public static android.app.Dialog showUpdateDialog(Activity context, int title, int toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(title), context.getResources().getString(toast), dialogClickListener, UPDATE_DIALOG, context.getResources().getString(R.string.down_now_str), context.getResources().getString(R.string.next_time_str));
	}

	public static android.app.Dialog showUpdateDialog(Activity context, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), toast, dialogClickListener, UPDATE_DIALOG, context.getResources().getString(R.string.down_now_str), context.getResources().getString(R.string.next_time_str));
	}

	public static android.app.Dialog showUpdateDialog(Activity context, int toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(R.string.pointMessage), context.getResources().getString(toast), dialogClickListener, UPDATE_DIALOG, context.getResources().getString(R.string.down_now_str), context.getResources().getString(R.string.next_time_str));
	}

	public static android.app.Dialog showUpdateDialog(Activity context, int title, String toast, final DialogClickListener dialogClickListener) {
		return ShowDialog(context, context.getResources().getString(title), toast, dialogClickListener, UPDATE_DIALOG, context.getResources().getString(R.string.down_now_str), context.getResources().getString(R.string.next_time_str));
	}

	private static android.app.Dialog ShowDialog(final Activity context, String title, String toast, final DialogClickListener dialogClickListener, final int DialogType, String confirmText, String cancelText) {
		if (context != null && !context.isDestroyed()) {
			final Dialog mCustomDialog = new android.app.Dialog(context, R.style.DialogStyle);
			mCustomDialog.setCancelable(false);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_customer, null);
			mCustomDialog.setContentView(view);
			((TextView) view.findViewById(R.id.tv_title)).setText(title);
			TextView mTvToast = ((TextView) view.findViewById(R.id.tv_toast));
			TextView mTvCancel = (TextView) view.findViewById(R.id.tv_cancel);
			TextView mTvSingleConfirm = (TextView) view.findViewById(R.id.tv_confirm);
			TextView mTvMultipleConfirm = (TextView) view.findViewById(R.id.tv_multiple_confirm);
			mTvToast.setText(Html.fromHtml(toast));
			mTvCancel.setText(cancelText);
			mTvSingleConfirm.setText(confirmText);
			mTvMultipleConfirm.setText(confirmText);
			if (DialogType != RADIO_DIALOG) {
				mTvSingleConfirm.setVisibility(View.GONE);
				view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
				if (DialogType == UPDATE_DIALOG) {
					mTvToast.setGravity(Gravity.LEFT);
				}
			}
			mTvCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mCustomDialog.dismiss();
					if (dialogClickListener != null) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dialogClickListener.cancel();
							}
						}, 200);
					}
				}
			});
			mTvSingleConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mCustomDialog.dismiss();
					if (dialogClickListener != null) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dialogClickListener.confirm();
							}
						}, 200);
					}
				}
			});
			mTvMultipleConfirm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mCustomDialog.dismiss();
					if (dialogClickListener != null) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								dialogClickListener.confirm();
							}
						}, 200);
					}
				}
			});
			Window mWindow = mCustomDialog.getWindow();
			WindowManager.LayoutParams lp = mWindow.getAttributes();
			if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
				lp.width = ScreenUtils.getScreenHeight(context) / 10 * 9;
			} else {
				lp.width = ScreenUtils.getScreenWidth(context) / 10 * 9;
			}
			mWindow.setAttributes(lp);
			mCustomDialog.show();

			return mCustomDialog;
		} else {
			return null;
		}
	}

	private static android.app.Dialog ShowDialog(Activity context, String[] items, boolean isCanceledOnTouchOutside, final DialogItemClickListener dialogClickListener) {
		if (context != null && !context.isDestroyed()) {
			final Dialog mCustomDialog = new android.app.Dialog(context, R.style.DialogStyle);
			mCustomDialog.setCancelable(true);
			mCustomDialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_customer_radio, new ViewGroup(context) {
				@Override
				protected void onLayout(boolean changed, int l, int t, int r, int b) {
				}
			}, false);
			mCustomDialog.setContentView(view);
			// 根据items动态创建
			LinearLayout parent = (LinearLayout) view.findViewById(R.id.dialogLayout);
			parent.removeAllViews();
			int length = items.length;
			for (int i = 0; i < length; i++) {
				LayoutParams params1 = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelOffset(R.dimen.dialog_button_minheight));
				final TextView tv = new TextView(context);
				tv.setLayoutParams(params1);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelOffset(R.dimen.dialog_button_text_size));
				tv.setText(items[i]);
				tv.setTag(i);
				tv.setTextColor(context.getResources().getColor(R.color.dialog_content_text_color));
				tv.setSingleLine(true);
				tv.setGravity(Gravity.CENTER);
				if (i != length - 1) {
					if (i == 0) {
						tv.setBackgroundResource(R.drawable.menudialog_top_selector);
					} else {
						tv.setBackgroundResource(R.drawable.menudialog_center_selector);
					}
				} else {
					if (i == 0) {
						tv.setBackgroundResource(R.drawable.menudialog_bottom3_selector);
					} else {
						tv.setBackgroundResource(R.drawable.menudialog_bottom2_selector);
					}
				}
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mCustomDialog.dismiss();
						dialogClickListener.confirm(tv.getText().toString(), Integer.parseInt(tv.getTag().toString()));
					}
				});
				parent.addView(tv);
				if (i != length - 1) {
					TextView divider = new TextView(context);
					LayoutParams params = new LayoutParams(-1, (int) 1);
					divider.setLayoutParams(params);
					divider.setBackgroundResource(android.R.color.darker_gray);
					parent.addView(divider);
				}
			}
			view.findViewById(R.id.tv_confirm).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mCustomDialog.dismiss();
					dialogClickListener.confirm("", -1);
				}
			});
			Window mWindow = mCustomDialog.getWindow();
			WindowManager.LayoutParams lp = mWindow.getAttributes();
			lp.width = ScreenUtils.getScreenWidth(context);
			lp.height = ScreenUtils.getScreenHeight(context) / 3;
			mWindow.setGravity(Gravity.BOTTOM);
			// 添加动画
			mWindow.setWindowAnimations(R.style.DialogAnim);
			mWindow.setAttributes(lp);
			mCustomDialog.show();
			return mCustomDialog;
		} else {
			return null;
		}
	}

	/**
	 * 提示信息为 正在加载数据...
	 * 
	 * @param context
	 * @return
	 */
	public static android.app.Dialog showProgress(Activity context) {
		return showProgress(context, context.getResources().getString(R.string.loading_data_str), true, false);
	}

	public static android.app.Dialog showProgress(Activity context, int tipsId) {
		return showProgress(context, context.getResources().getString(tipsId), true, false);
	}

	public static android.app.Dialog showProgress(Activity context, String tips) {
		return showProgress(context, tips, true, false);
	}

	public static android.app.Dialog showProgress(Activity context, boolean isShowProgress, boolean isOutcancelble) {
		return showProgress(context, context.getResources().getString(R.string.loading_data_str), isShowProgress, isOutcancelble);
	}

	public static android.app.Dialog showProgress(Activity context, int tipsId, boolean isShowProgress, boolean isOutcancelble) {
		return showProgress(context, context.getResources().getString(tipsId), isShowProgress, isOutcancelble);
	}

	/**
	 * 显示加载dialog
	 * 
	 * @param context
	 * @param tips
	 * @param isShowProgress
	 * @return
	 */
	public static android.app.Dialog showProgress(Activity context, String tips, boolean isShowProgress, boolean isOutCancelable) {
		if (mCustomDialog != null && mCustomDialog.isShowing()) {
			return mCustomDialog;
		} else {
			mCustomDialog = new android.app.Dialog(context, R.style.ImageloadingDialogStyle);
			mCustomDialog.setCancelable(true);
			mCustomDialog.setCanceledOnTouchOutside(isOutCancelable);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, new ViewGroup(context) {
				@Override
				protected void onLayout(boolean changed, int l, int t, int r, int b) {
				}
			}, false);
			mCustomDialog.setContentView(view);
			((TextView) view.findViewById(R.id.tv_tips)).setText(tips);
			ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
			ImageView mIvMap = (ImageView) view.findViewById(R.id.iv_map);
			if (!isShowProgress) {
				mIvMap.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			} else {
				mIvMap.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
			}
			mCustomDialog.show();
			return mCustomDialog;
		}
	}

	public static Dialog showDatePickerDialog(Activity context, final DialogItemClickListener dialogClickListener) {
		return showDatePickerDialog(context, false, dialogClickListener);
	}

	/**
	 * 选时间
	 * 
	 * @param context
	 * @param dialogClickListener
	 * @param flag
	 * @return
	 */
	public static Dialog showDatePickerDialog(Activity context, boolean hasSelectTime, final DialogItemClickListener dialogClickListener) {
		if (context != null && !context.isDestroyed()) {
			View timepickerview = LayoutInflater.from(context).inflate(R.layout.date_picker_layout, new ViewGroup(context) {
				@Override
				protected void onLayout(boolean changed, int l, int t, int r, int b) {

				}
			}, false);
			final WheelMain wheelMain = new WheelMain(context, timepickerview, hasSelectTime);
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int min = calendar.get(Calendar.MINUTE);
			wheelMain.initDateTimePicker(year, month, day, hour, min);
			final Dialog datePickerDialog = new Dialog(context, R.style.DialogStyle);
			View view = LayoutInflater.from(context).inflate(R.layout.date_picker_layout_dialog, new ViewGroup(context) {
				@Override
				protected void onLayout(boolean changed, int l, int t, int r, int b) {

				}
			}, false);
			LinearLayout mLLDateContainer = (LinearLayout) view.findViewById(R.id.ll_date_container);
			mLLDateContainer.addView(timepickerview);

			view.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					datePickerDialog.dismiss();
					dialogClickListener.confirm(wheelMain.getTime(), 0);
				}
			});
			view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					datePickerDialog.dismiss();
				}
			});
			// 设置对话框布局
			datePickerDialog.setContentView(view);

			Window mWindow = datePickerDialog.getWindow();
			WindowManager.LayoutParams lp = mWindow.getAttributes();
			lp.width = ScreenUtils.getScreenWidth(context);
			mWindow.setGravity(Gravity.BOTTOM);
			// 添加动画
			mWindow.setWindowAnimations(R.style.DialogAnim);
			mWindow.setAttributes(lp);
			// 点击对话框外部关闭对话框
			datePickerDialog.setCanceledOnTouchOutside(true);
			// 显示对话框
			datePickerDialog.show();

			return datePickerDialog;
		} else {
			return null;
		}
	}

	/**
	 * 隐藏Dialog
	 */
	public static void dismissProgress() {
		if (mCustomDialog != null && mCustomDialog.isShowing()) {
			mCustomDialog.dismiss();
			mCustomDialog = null;
		}
	}

	public interface DialogClickListener {
		public abstract void confirm();

		public abstract void cancel();
	}

	public interface DialogItemClickListener {
		public abstract void confirm(String result, int position);
	}
}