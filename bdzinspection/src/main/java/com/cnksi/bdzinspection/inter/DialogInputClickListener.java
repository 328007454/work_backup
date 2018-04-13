package com.cnksi.bdzinspection.inter;

import android.content.DialogInterface;

public interface DialogInputClickListener {
	/**
	 * @param dialog
	 * @param which
	 * @param result
	 */
	void onClick(DialogInterface dialog, int which, String result);
}
