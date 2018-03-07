package com.cnksi.core.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnksi.core.R;
import com.cnksi.core.adapter.DividerItemDecoration;
import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.x;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseCoreFragment extends Fragment {
    public static final int LOAD_DATA = 0x1;
    /**
     * 请求失败
     */
    public static final int ERROR_DATA = 0x4;
    /**
     * 振动器
     */
    protected Vibrator mVibrator;

    protected Activity mCurrentActivity = null;
    protected Bundle bundle = null;
    protected Fragment mCurrentFragment = null;
    protected int threadPoolSize = Runtime.getRuntime().availableProcessors();
    // 线程池
    protected ExecutorService mExcutorService = Executors.newFixedThreadPool(threadPoolSize > 3 ? 3 : threadPoolSize);
    /**
     * fragment管理器
     */
    protected FragmentManager mFManager = null;
    protected LayoutInflater mInflater = null;
    /**
     * 自定义Handler
     */
    protected CustomHandler mHandler = new CustomHandler(this);
    /**
     * 是否可见的标志
     */
    protected boolean isVisible;
    /**
     * 标志位，标志已经初始化完成。
     */
    protected boolean isPrepared = false;
    /**
     * 是否是第一次加载
     */
    protected boolean isFirstLoad = true;
    protected boolean isOnDetach = true;
    private boolean injected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mCurrentActivity = getActivity();
        this.mCurrentFragment = this;
        isOnDetach = false;
        bundle = getArguments();
        mInflater = LayoutInflater.from(getActivity());
        mFManager = getChildFragmentManager();
        mVibrator = (Vibrator) mCurrentActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = x.view().inject(this, inflater, container);
        injected = true;

        initUI();

        initData();

        lazyLoad();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {

            x.view().inject(this, this.getView());

            initUI();

            initData();

            lazyLoad();
        }
    }


    public void setFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }


    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见的时候
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 延迟加载
     */
    protected abstract void lazyLoad();

    /**
     * 不可见时
     */
    protected void onInvisible() {
    }


    /**
     * 初始化控件
     */
    protected abstract void initUI();

    /**
     * 查询数据
     */
    protected abstract void initData();

    /**
     * 刷新数据
     *
     * @param msg
     */
    protected void onRefresh(android.os.Message msg) {
        if (!isOnDetach) {
            switch (msg.what) {
                case CoreConfig.NETWORK_UNVISIBLE:
                    CustomerDialog.dismissProgress();
                    CToast.showLong(mCurrentActivity, R.string.network_unvisible_str);
                    break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isOnDetach = true;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        OkHttpUtils.getInstance().cancelTag(this);
        mHandler.removeCallbacks(null);
    }

    /**
     * 设置RecyclerView 的样式
     *
     * @param mRecyclerView
     * @param orientation   LinearLayoutManager.HORIZONTAL/VERTICAL
     */
    protected void setRecyclerViewStyle(RecyclerView mRecyclerView, int orientation) {
        setRecyclerViewStyle(mRecyclerView, orientation, true);
    }

    /**
     * 设置RecyclerView 的样式
     *
     * @param mRecyclerView
     * @param orientation   LinearLayoutManager.HORIZONTAL/VERTICAL
     * @param isAddDivider  是否加上分割线
     */
    protected void setRecyclerViewStyle(RecyclerView mRecyclerView, int orientation, boolean isAddDivider) {
        //添加布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(orientation);
        mRecyclerView.setLayoutManager(layoutManager);
        if (isAddDivider) {
            //添加分割线
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), orientation));
        }
    }

    /**
     * 设置文本控件值
     *
     * @param resId
     * @param content
     */
    protected void setTextContent(View rootView, int resId, String content) {
        if (isAdded()) {
            ((TextView) rootView.findViewById(resId)).setText(content);
        }
    }

    /**
     * 设置文本控件值
     *
     * @param resId
     * @param contentId
     */
    protected void setTextContent(View rootView, int resId, int contentId) {
        if (isAdded()) {
            ((TextView) rootView.findViewById(resId)).setText(contentId);
        }
    }

    /**
     * 自定义Handler
     */
    public static class CustomHandler extends Handler {
        WeakReference<BaseCoreFragment> mFragment;

        public CustomHandler(BaseCoreFragment mFragment) {
            this.mFragment = new WeakReference<BaseCoreFragment>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseCoreFragment mCurrentFragment = mFragment.get();
            if (mCurrentFragment != null) {
                mCurrentFragment.onRefresh(msg);
            }
        }
    }
}
