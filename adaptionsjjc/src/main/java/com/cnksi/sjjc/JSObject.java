package com.cnksi.sjjc;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

public class JSObject {

	    private Context context;
		public JSObject(Context context){
			this.context = context;
		}

	public interface wvClientClickListener {
		void signName(String id);
		void takePicture(String id);
		void showIamge(String id);
		void save();
	}

	private wvClientClickListener wvEnventPro = null;
	public void setWvClientClickListener(wvClientClickListener listener) {
		wvEnventPro = listener;
	}

	@JavascriptInterface
	public String  callbakpics(String id, String t){
			if(wvEnventPro != null)
				if ("c".equalsIgnoreCase(t))
					wvEnventPro.takePicture(id);
				else if ("s".equalsIgnoreCase(t))
					wvEnventPro.signName(id);
		return "";
	}

	@JavascriptInterface
	public String lookpics(String id)
	{
		if (!TextUtils.isEmpty(id))
		{
			wvEnventPro.showIamge(id);
		}
		return "";
	}
	@JavascriptInterface
	public String save()
	{

			wvEnventPro.save();

		return "";
	}
}
