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

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsFragmentMotionRuleBinding;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 典型故障和异常处理fragment 采用webview加载后台同步的数据库的html字段
 * */
public class AccidentDealFragment extends BaseFragment {
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

	@Override
	protected void lazyLoad() {	
	}
	
	private void initialData() {
		ExecutorManager.executeTask(() -> {
            String sql = " select db.rules,db.exception_deal_methods from device_bigtype db, (SELECT * from device d left join device_type dt on d.dtid= dt.dtid where  d.deviceid='" + currentDeviceId + "') t where db.bigid = t.bigid ";
            try {
                dbModel = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo(sql));
            } catch (DbException e) {
                e.printStackTrace();

            }
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
		//设置自适应屏幕，两者合用
//		webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小 
//		webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
	}

	@Override
	protected void onRefresh(Message msg) {
		switch (msg.what) {
		case LOAD_DATA:
			if (null!=dbModel&&!TextUtils.isEmpty(dbModel.getString("db.exception_deal_methods"))) {
                webView.loadDataWithBaseURL(null, "<head>\n<body>\n"+dbModel.getString("db.exception_deal_methods")+"\n</body>\n</head>", "text/html", "utf-8", null);
            }
			webView.setWebChromeClient(new WebChromeClient());
			break;
		default:
			break;
		}

	}
	
	@Override
	public void onDestroy() {
		if(webView!=null){
			webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
			webView.clearHistory();
			((ViewGroup)webView.getParent()).removeView(webView);
			webView.destroy();
			webView=null;
		}
		super.onDestroy();
	}

}
