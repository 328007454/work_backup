package com.cnksi.sjjc.adapter;


import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceStandards;

import org.xutils.db.table.DbModel;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

/**
 * 抄录数据
 *
 * @author Joe
 *
 */
/**
 * @author luoxy
 *
 */
public class CopyValueElvAdapter extends BaseMapListExpandableAdapter<DevicePart, DbModel> {

	private String  currentDeviceId;
	private HashMap<String,String> copyedMap;
	public CopyValueElvAdapter(Context context,String currentDeviceId) {
		super(context);
		copyedMap= CustomApplication.getInstance().getCopyedMap();
		this.currentDeviceId=currentDeviceId;
	}

	public void setCurrentDeviceId(String currentDeviceId) {
		this.currentDeviceId = currentDeviceId;
	}

	private OnAdapterViewClickListener mOnAdapterViewClickListener;

	public interface OnAdapterViewClickListener {
		public void OnAdapterViewClick(View view, DbModel mDbModel);

		public boolean OnShowKeyboard(EditText et, MotionEvent event, DbModel mDbModel);

		public void OnAdapterViewLongClick(View view, DbModel mDbModel);

		public void OnViewFocusChange(View view, boolean hasFocus, DbModel mDbModel, int groupPosition,
									  int childPosition);

		public void updateSmartKeyBoard(EditText mEditText, DbModel mDbModel);
	}

	public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
		this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
	}

	private boolean isUpdate = false;

	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final DbModel mStandards = getChild(groupPosition, childPosition);
		if (mStandards != null) {
			ViewChildHolder holder = null;
			if (convertView == null) {
				holder = new ViewChildHolder();
				convertView = mInflater.inflate(R.layout.copy_value_child_item, parent, false);
				x.view().inject(holder, convertView);
				holder.watcher = new MyTextChangeListener();
				holder.mEtCopyValue.addTextChangedListener(holder.watcher);
				convertView.setTag(holder);
			} else {
				holder = (ViewChildHolder) convertView.getTag();
			}
			holder.setCurrentDbModelAndPosition(mStandards, groupPosition, childPosition);
			holder.watcher.setEditTextAndPosition(holder.mEtCopyValue, groupPosition, childPosition);
			String copyContent = mStandards.getString(DeviceStandards.DESCRIPTION);
			if (!TextUtils.isEmpty(mStandards.getString(DeviceStandards.UNIT))) {
				copyContent = copyContent + " (" + mStandards.getString(DeviceStandards.UNIT) + ")";
			}
			holder.mTvCopyContent.setText(copyContent);
			String value = mStandards.getString(DefectRecord.VAL);
			//modify by luoxy 采用标准+设备区分抄录数据
			String key=mStandards.getString(DeviceStandards.STAID)+"_"+currentDeviceId;
			String oldValue =copyedMap.get(key);
			value=TextUtils.isEmpty(value) ? oldValue : value;
			holder.mEtCopyValue.setText(value);
			mStandards.add(DefectRecord.VAL, value);
			//增加上次抄录记录
			if (TextUtils.isEmpty(mStandards.getString(DefectRecord.OLDVAL)))
			{
				mStandards.add(DefectRecord.OLDVAL,oldValue);
			}
			// holder.mEtCopyValue.setText( value);
			// // 第二次的时候给Standard 赋值
//			if (TextUtils.isEmpty(value)) {
//				mStandards.add(DefectRecord.VAL, oldValue);
//			}
			if (isUpdate && groupPosition == 0 && childPosition == 0) {
				if (mOnAdapterViewClickListener != null) {
					mOnAdapterViewClickListener.updateSmartKeyBoard(holder.mEtCopyValue, mStandards);
					isUpdate = false;
				}
			}

		}
		return convertView;
	}

	class ViewChildHolder {

		private DbModel mDbModel;

		private int section;
		private int childPosition;

		public void setCurrentDbModelAndPosition(DbModel mDbModel, int section, int childPosition) {
			this.mDbModel = mDbModel;
			this.section = section;
			this.childPosition = childPosition;
		}

		/**
		 * 抄录数据的值
		 */
		@ViewInject(R.id.et_copy_values)
		EditText mEtCopyValue;

		/**
		 * 抄录的内容
		 */
		@ViewInject(R.id.tv_copy_content)
		TextView mTvCopyContent;

		/**
		 * 文本变化时的监听
		 */
		MyTextChangeListener watcher;

		@Event({ R.id.ibtn_history_data, R.id.tv_copy_content })
		private void OnViewClick(View view) {
			if (mOnAdapterViewClickListener != null) {
				mOnAdapterViewClickListener.OnAdapterViewClick(view, mDbModel);
			}
		}

		@Event(value = R.id.et_copy_values,type = View.OnFocusChangeListener.class)
		private boolean OnViewTouch(View view, boolean hasfocus) {
			if (mOnAdapterViewClickListener != null) {
				mOnAdapterViewClickListener.OnViewFocusChange(view, hasfocus, mDbModel, section, childPosition);
				return true;
			}
			return false;
		}

		@Event(value = { R.id.et_copy_values },type = View.OnTouchListener.class)
		private boolean onTouch(View view, MotionEvent event) {
			if (mOnAdapterViewClickListener != null) {
				return mOnAdapterViewClickListener.OnShowKeyboard((EditText) view, event, mDbModel);
			}
			return false;
		}

		@Event(value = { R.id.rl_root_container },type = View.OnLongClickListener.class)
		private boolean OnViewLongClick(View view) {
			if (mOnAdapterViewClickListener != null) {
				mOnAdapterViewClickListener.OnAdapterViewLongClick(view, mDbModel);
			}
			return true;
		}

	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		DevicePart group = getGroup(groupPosition);
		if (group != null) {
			ViewGroupHolder holder = null;
			if (convertView == null) {
				holder = new ViewGroupHolder();
				convertView = mInflater.inflate(R.layout.group_item, parent, false);
				x.view().inject(holder, convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewGroupHolder) convertView.getTag();
			}
			holder.mTvDevicePart.setText(group.name);
		}
		return convertView;
	}

	class ViewGroupHolder {

		@ViewInject(R.id.tv_group_item)
		TextView mTvDevicePart;
	}

	class MyTextChangeListener implements TextWatcher {

		private EditText mEditText;
		private int childPosition;
		private int groupPosition;

		public void setEditTextAndPosition(EditText mEditText, int groupPosition, int childPosition) {
			this.mEditText = mEditText;
			this.childPosition = childPosition;
			this.groupPosition = groupPosition;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			DbModel mStandards = getChild(groupPosition, childPosition);
			String currentValue = mEditText.getText().toString().trim();
//			 String value = TextUtils.isEmpty(mStandards.getString(DefectRecord.VAL)) ? ""
//			 : mStandards.getString(DefectRecord.VAL);
	//		PreferencesUtils.put(mContext, mStandards.getString(DeviceStandards.STAID), currentValue);
	//		String key=mStandards.getString(DeviceStandards.STAID)+"_"+currentDeviceId;
	//		PreferencesUtils.put(mContext, key, currentValue);
			mStandards.add(DefectRecord.VAL, currentValue);
			Selection.setSelection(s, s.length());
		}
	}
}
