package com.cnksi.sjjc.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.CopyDeviceItemAdapter;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.processor.CopyDataInterface;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Map;

/**
 * 抄录数据
 *
 * @author Oliver
 *
 */
public class CopyValueFragment extends BaseCoreFragment {

	@ViewInject(R.id.gv_container)
	private GridView mGvContainer;

	private CopyDeviceItemAdapter mDeviceItemAdapter = null;
	private List<DbModel> dataList = null;

	private DbModel mCurrentSelectedDevice = null;
	private int mCurrentSelectedPosition = 0;

	/**
	 * 当前巡检类型
	 */
	protected String currentInspectionType = "";
	/**
	 * 当前巡检类型Name
	 */
	protected String currentInspectionTypeName = InspectionType.full.toString();
	/**
	 * 当前巡检变电站
	 */
	protected String currentBdzId = "";
	/**
	 * 当前的功能选项
	 */
	protected String currentFunctionModel = "";

	/**
	 * 当前变电站名称
	 */
	protected String currentBdzName = "";
	/**
	 * 当前的任务id
	 */
	protected String currentTaskId = "";
	/**
	 * 当前的报告id
	 */
	protected String currentReportId = "";
    /**
     * 处理器。
     */
	protected CopyDataInterface processor;
	private Map<String,Boolean> copyMap;

	public void setProcessor(CopyDataInterface processor)
    {
        this.processor=processor;
    }

	public void setCopyedMap(Map<String,Boolean> copyedMap) {
		this.copyMap=copyedMap;
		if (mDeviceItemAdapter != null) {
			mDeviceItemAdapter.setCopyedMap(copyedMap);
		}
	}

	/**
	 * 是否是事故后，验收，新投
	 */
	private boolean isAcceptAccidentNewProduct = false;
	private String deviceIdList = null;

	private OnItemClickedListener mOnItemClickedListener;

	public interface OnItemClickedListener {
		public void OnItemClicked(DbModel mDevice, int position);

		public void loadDataFinish(DbModel mDevice, boolean isOnItemClicked);
	}

	public void setOnItemClickedListener(OnItemClickedListener mOnItemClickedListener) {
		this.mOnItemClickedListener = mOnItemClickedListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_gridlist, container, false);
		x.view().inject(this, view);

		initUI();

		lazyLoad();

		return view;
	}
@Override
protected void initUI() {

		getBundleValue();
		mGvContainer.setNumColumns(2);
		isPrepared = true;
        mGvContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSelectedDevice = (DbModel) parent.getItemAtPosition((int) id);
                if (mOnItemClickedListener != null && mCurrentSelectedDevice != null) {
                    mOnItemClickedListener.OnItemClicked(mCurrentSelectedDevice, position);
                }
                if (mDeviceItemAdapter != null) {
                    mDeviceItemAdapter.setCurrentSelectedPosition(mCurrentSelectedPosition = position);
                }
            }
        });
	}
	protected void getBundleValue() {
		currentFunctionModel = bundle.getString(Config.CURRENT_FUNCTION_MODEL);
		currentBdzId = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_BDZ_ID, "");
		currentBdzName = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_BDZ_NAME, "");
		currentInspectionTypeName = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_INSPECTION_TYPE_NAME,
				"");
		currentInspectionType = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_INSPECTION_TYPE, "");
		currentTaskId = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_TASK_ID, "");
		currentReportId = PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_REPORT_ID, "");
	}

	@Override
	protected void initData() {

	}

	@Override
	protected void lazyLoad() {
		if (isPrepared && isVisible && isFirstLoad) {
			// 填充各控件的数据
			searchData();
			isFirstLoad = false;
		}
	}

	public void searchData() {
		mExcutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					dataList =processor.findAllDeviceHasCopyValue(currentFunctionModel,currentBdzId);
				} catch (DbException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(LOAD_DATA);

			}
		});
	}

	@Override
	protected void onRefresh(Message msg) {
		switch (msg.what) {
		case LOAD_DATA:
			if (mDeviceItemAdapter == null) {
				mDeviceItemAdapter = new CopyDeviceItemAdapter(mCurrentActivity, dataList);
				mDeviceItemAdapter.setCopyedMap(copyMap);
				mOnItemClickedListener.loadDataFinish(
						(dataList != null && !dataList.isEmpty()) ? dataList.get(mCurrentSelectedPosition) : null,
						false);
				mGvContainer.setAdapter(mDeviceItemAdapter);
			} else {
				mDeviceItemAdapter.setList(dataList);
			}

			break;
		case ERROR_DATA:

			// 将点击的Item置顶
			mGvContainer.setSelected(true);
			mGvContainer.setSelection(mCurrentSelectedPosition);

			break;
		default:
			break;
		}
	}



	public void setCurrentSelection() {
		// 延迟刷新界面 立即刷新界面不会置顶
		mHandler.sendEmptyMessageDelayed(ERROR_DATA, 100);
	}

	/**
	 * 滑动到对应的界面刷新对应界面的第一个设备的抄录数据
	 */
	public void loadFirstDeviceCopyData(int position, boolean isClickPreOrNext) {
		if (position < 0)
			position = 0;
		if (dataList != null && position >= dataList.size())
			position = dataList.size() - 1;
		if (isClickPreOrNext) {
			mCurrentSelectedPosition = position;
		}
		if ( dataList != null && !dataList.isEmpty()) {
			if (mGvContainer != null && isClickPreOrNext) {
				mGvContainer.performItemClick(mGvContainer.getChildAt(mCurrentSelectedPosition),
						mCurrentSelectedPosition, mGvContainer.getItemIdAtPosition(mCurrentSelectedPosition));
			} else {
				mOnItemClickedListener.loadDataFinish(dataList.get(mCurrentSelectedPosition), false);
			}
		} else {
			mOnItemClickedListener.loadDataFinish(null, false);
		}
	}

}
