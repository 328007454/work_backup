package com.cnksi.core.activity;

import java.util.Date;
import java.util.Locale;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cnksi.core.R;
import com.cnksi.core.common.DeviceUtils;
import com.cnksi.core.receiver.SMSBroadcastReceiver;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DESEncrypt;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.IntentUtils;
import com.cnksi.core.utils.KeyBoardUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.CustomerDialog.DialogClickListener;

public class RegisteredActivity extends BaseCoreActivity {

	private TextView mTvDeviceNumber;
	private EditText mEtRegisterCode;
	private Button mBtnRegister;
	private ImageButton mIBtnPhoneRegister;
	private ImageButton mIBtnEmailRegister;
	private ImageButton mIBtnMessageRegister;

	private String deviceNumber = "";
	private DESEncrypt mDesEncrypt = null;
	private String pCode = "";
	private String appName = "";

	private SMSBroadcastReceiver mSMSBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initUI();

		initData();
	}

	private void initUI() {
		mTvDeviceNumber = (TextView) this.findViewById(R.id.tv_device_number);
		mEtRegisterCode = (EditText) this.findViewById(R.id.et_register_code);
		mBtnRegister = (Button) this.findViewById(R.id.btn_register);
		mIBtnEmailRegister = (ImageButton) this.findViewById(R.id.ibtn_email_register);
		mIBtnMessageRegister = (ImageButton) this.findViewById(R.id.ibtn_message_register);
		mIBtnPhoneRegister = (ImageButton) this.findViewById(R.id.ibtn_phone_register);
	}

	private void initData() {

		appName = AppUtils.getAppName(getApplicationContext());
		pCode = FunctionUtils.getMetaValue(getApplicationContext(), "com.cnksi.core.PROGRAM_CODE");
		mTvDeviceNumber.setText(deviceNumber = StringUtils.insertSymbolToString(4, DeviceUtils.getAndroidId(RegisteredActivity.this), CoreConfig.DASH_SEPARATOR));

		// 生成广播处理
		mSMSBroadcastReceiver = new SMSBroadcastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter(SMSBroadcastReceiver.SMS_RECEIVED_ACTION);
		intentFilter.setPriority(Integer.MAX_VALUE);
		// 注册广播
		this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
		mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
			@Override
			public void onReceived(String message) {
				CLog.d(message);
				message = message.replace("-", "").trim();
				if (!TextUtils.isEmpty(message) && message.length() == 16 && StringUtils.isNumberAndLetter(message)) {
					mEtRegisterCode.setText(message);
				}
			}
		});

		mIBtnEmailRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomerDialog.showRadioDialog(mCurrentActivity, getResources().getString(R.string.email_register_format_str, appName), null);
			}
		});
		mIBtnMessageRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtils.sendMessage(mCurrentActivity, getResources().getString(R.string.message_register_format_str, appName, deviceNumber), "18615713357");
			}
		});
		mIBtnPhoneRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CustomerDialog.showSelectDialog(mCurrentActivity, getResources().getString(R.string.tips_str), getResources().getString(R.string.phone_register_hint_str), new DialogClickListener() {

					@Override
					public void confirm() {
						IntentUtils.callPhone(mCurrentActivity, "18615713357");
					}

					@Override
					public void cancel() {

					}
				}, "拨打", getResources().getString(R.string.dialog_cancel_text_str));
			}
		});
		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String registerCode = mEtRegisterCode.getText().toString().trim();
				if (TextUtils.isEmpty(registerCode) || TextUtils.isEmpty(pCode)) {
					CToast.showShort(mCurrentActivity, R.string.please_input_register_code_str);
					return;
				}
				String key = DeviceUtils.getAndroidId(RegisteredActivity.this) + "_" + pCode;
				try {
					mDesEncrypt = DESEncrypt.getIntence(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Date date = mDesEncrypt.verifySerialNO(registerCode, key);
				if (date != null && !DateUtils.compareDate(DateUtils.getCurrentLongTime(), DateUtils.getFormatterTime(date, CoreConfig.dateFormat1), CoreConfig.dateFormat1)) {
					CLog.d(DateUtils.getFormatterTime(date, CoreConfig.dateFormat2));
					PreferencesUtils.put(getApplicationContext(), CoreConfig.REGISTERED_DATE, DateUtils.getFormatterTime(date, CoreConfig.dateFormat2));
					CToast.showShort(mCurrentActivity, "注册成功!");
					RegisteredActivity.this.finish();
				} else {
					CToast.showShort(mCurrentActivity, "注册码无效，请检查!");
				}
				KeyBoardUtils.closeKeybord(mEtRegisterCode, mCurrentActivity);
			}
		});

		mEtRegisterCode.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mEtRegisterCode.removeTextChangedListener(this);// 解除文字改变事件
				String content = s.toString().replace(CoreConfig.DASH_SEPARATOR, "");
				content = StringUtils.insertSymbolToString(4, content, CoreConfig.DASH_SEPARATOR).toUpperCase(Locale.CHINA);
				mEtRegisterCode.setText(content);// 转换
				mEtRegisterCode.setSelection(content.length());// 重新设置光标位置
				mEtRegisterCode.addTextChangedListener(this);// 重新绑
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onBackPressed() {
		compeletlyExitSystem();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mSMSBroadcastReceiver);
		super.onDestroy();
	}
}
