package com.cnksi.xscore.xsfragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.xscore.xsapplication.CoreApplication;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

@SuppressWarnings("deprecation")
public class BaseCoreFragment extends Fragment {
	/** 第一次加载数据 */
	public static final int LOAD_DATA = 0x1;
	/** 刷新数据 */
	public static final int REFRESH_DATA = 0x2;
	/** 保存数据 */
	public static final int SAVE_DATA = 0x3;
	/** 请求失败 */
	public static final int ERROR_DATA = 0x4;
	/** Session过期 */
	public static final int OVER_TIME_DATA = 0x5;
	/** 启动照相请求 */
	public static final int ACTION_IMAGE = 0x6;
	/** 网络不可用 */
	public static final int NETWORK_UNVISIBLE = 0x7;
	/** 加载更多 */
	public static final int LOAD_MORE_DATA = 0x8;
	/** 上传进度条 */
	public static final int UPDATE_PROGRESS_BAR = 0x9;
	/** 取消选择的图片 */
	public static final int CANCEL_RESULT_LOAD_IMAGE = 0x10;
	/** 裁剪图片 */
	public static final int CROP_PICTURE = 0x11;
	/** 成功代码 */
	public static final String OK_CODE = "200";
	/** 重新登录界面 */
	public static final String OVER_TIME_CODE = "301";
	/** 成功代码 */
	public static final String MESSAGE = "message";
	/** 状态 */
	public static final String STATUS = "status";
	/** 数据 */
	public static final String DATA = "data";
	/** 数据List */
	public static final String LIST_DATA = "list";

	protected Activity currentActivity = null;
	protected Bundle bundle = null;
	protected Fragment currentFragment = null;
	// 线程池
	protected ExecutorService mFixedThreadPoolExecutor = CoreApplication.getFixedThreadPoolExecutor();
	/** fragment管理器 */
	protected FragmentManager mFManager = null;
	/** 图片显示工具类 */
	protected BitmapUtils mBitmapUtils = null;
	protected LayoutInflater mInflater = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.currentActivity = activity;
		this.currentFragment = this;
		bundle = getArguments();
		mInflater = LayoutInflater.from(activity);
		mFManager = getChildFragmentManager();
	}
	
	@Override
    public void onDetach() {
    	super.onDetach();
    	try {
    	    Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
    	    childFragmentManager.setAccessible(true);
    	    childFragmentManager.set(this, null);

    	} catch (NoSuchFieldException e) {
    	    throw new RuntimeException(e);
    	} catch (IllegalAccessException e) {
    	    throw new RuntimeException(e);
    	}
    }
}
