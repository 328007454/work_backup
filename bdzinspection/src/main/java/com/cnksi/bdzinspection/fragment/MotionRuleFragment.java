package com.cnksi.bdzinspection.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.databinding.XsFragmentMotionRuleBinding;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.db.table.DbModel;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 运行规定界面展示   采用webview加载后台同步的数据库的html字段
 * */
public class MotionRuleFragment extends BaseFragment {
	private WebView webView;
	private WebSettings webSettings;
	private DbModel dbModel = null;
	private XsFragmentMotionRuleBinding binding;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = XsFragmentMotionRuleBinding.inflate(inflater);
		getBundleValue();
		initialUI();
		initialData();
		return binding.getRoot();
	}

	

	private void initialData() {
		ExecutorManager.executeTask(() -> {
            dbModel = DeviceService.getInstance().findAccidentDeal(currentDeviceId);
            mHandler.sendEmptyMessage(LOAD_DATA);
        });

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initialUI() {
		webView = new WebView(currentActivity);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		webView.setLayoutParams(layoutParams);
		binding.layoutMotion.addView(webView);
		webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小 
//		webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
	}

	@Override
	protected void lazyLoad() {

	}

	@Override
	protected void onRefresh(Message msg) {
		switch (msg.what) {
		case LOAD_DATA:
			if (null!=dbModel&&!TextUtils.isEmpty(dbModel.getString("db.rules"))) {
                webView.loadDataWithBaseURL(null, "<head>\n<body>\n" + dbModel.getString("db.rules") + "\n</body>\n</head>", "text/html", "utf-8", null);
            }
			webView.setWebChromeClient(new WebChromeClient());
			break;

		default:
			break;
		}

	}

}
