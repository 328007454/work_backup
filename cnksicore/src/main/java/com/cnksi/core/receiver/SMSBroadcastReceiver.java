package com.cnksi.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.cnksi.core.utils.CLog;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
			SmsMessage[] messages = getMessagesFromIntent(intent);
			for (SmsMessage message : messages) {
				CLog.d(message.getOriginatingAddress() + " : " + message.getDisplayOriginatingAddress() + " : " + message.getDisplayMessageBody() + " : " + message.getTimestampMillis());
				String smsContent = message.getDisplayMessageBody();
				String sender = message.getOriginatingAddress();
				if (!TextUtils.isEmpty(sender) && sender.contains("18615713357")) {
					if (mMessageListener != null) {
						mMessageListener.onReceived(smsContent);
					}
					abortBroadcast();
				}
			}
		}
	}

	public final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		byte[][] pduObjs = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		return msgs;
	}

	private MessageListener mMessageListener;

	public SMSBroadcastReceiver() {
		super();
	}

	// 回调接口
	public interface MessageListener {
		public void onReceived(String message);
	}

	public void setOnReceivedMessageListener(MessageListener messageListener) {
		this.mMessageListener = messageListener;
	}
}