package com.cnksi.bdzinspection.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cnksi.bdzinspection.activity.DownloadOperationTickActivity;

public class ReceivedBroardCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, final Intent intent) {
		String action = intent.getAction();
		if ("com.cnksi.bdzinspection.action.RECEIVE_COMPLETED".equals(action)) {
			Intent newIntent = new Intent(context, DownloadOperationTickActivity.class);
			newIntent.putExtra("filePath", intent.getStringExtra("filePath"));
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
			context.startActivity(newIntent);
		}
	}
}
