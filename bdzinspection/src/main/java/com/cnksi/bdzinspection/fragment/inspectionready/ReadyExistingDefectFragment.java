package com.cnksi.bdzinspection.fragment.inspectionready;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.inspectionready.ReadyExistDefectAdapter;
import com.cnksi.bdzinspection.adapter.inspectionready.ReadyExistDefectAdapter.OnAdapterViewClickListener;
import com.cnksi.bdzinspection.databinding.XsFragmentPinnedHeaderListBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.view.PinnedHeaderListView.OnItemClickListener;
import com.cnksi.defect.activity.OperateDefectActivity;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 巡检前准备 现存缺陷Fragment
 *
 * @author Joe
 */
public class ReadyExistingDefectFragment extends BaseFragment implements OnAdapterViewClickListener {

    public static final int UPDATE_DEVICE_DEFECT_REQUEST_CODE = 0x111;

    private ReadyExistDefectAdapter mExistDefectAdapter = null;
    private LinkedList<String> groupList = null;
    private HashMap<String, ArrayList<DefectRecord>> groupHashMap = null;

    public interface OnFragmentEventListener {
        void onDefectItemClick(Intent intent, int requestCode);
    }

    private OnFragmentEventListener mOnFragmentEventListener;

    public void setOnFragmentEventListener(OnFragmentEventListener mOnFragmentEventListener) {
        this.mOnFragmentEventListener = mOnFragmentEventListener;
    }

    private XsFragmentPinnedHeaderListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentPinnedHeaderListBinding.inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initialUI();
        lazyLoad();
        return binding.getRoot();
    }

    private void initialUI() {
        getBundleValue();
        binding.lvPinnedContainer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                if (mOnFragmentEventListener != null) {
                    DefectRecord mDefectRecord = mExistDefectAdapter.getItem(section, position);
                    Intent intent = new Intent(currentActivity, OperateDefectActivity.class);
                    intent.putExtra(Device.DEVICEID, mDefectRecord.deviceid);
                    intent.putExtra(Bdz.BDZID, mDefectRecord.bdzid);
                    intent.putExtra(Config.DEFECT_COUNT_KEY, Config.SINGLE);
                    intent.putExtra(DefectRecord.DEFECTID, mDefectRecord.defectid);
                    mOnFragmentEventListener.onDefectItemClick(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
                }
            }
        });
        isPrepared = true;
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            isFirstLoad = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            searchData();
        }
    }

    public void searchData() {
        ExecutorManager.executeTask(() -> {
            if (groupList == null) {
                groupList = new LinkedList<>();
                groupHashMap = new HashMap<>();
            } else {
                groupList.clear();
                groupHashMap.clear();
            }
            List<DefectRecord> defectList = DefectRecordService.getInstance().queryCurrentBdzExistDefectList(currentBdzId);
            if (defectList != null && !defectList.isEmpty()) {
                ArrayList<DefectRecord> mCrisisDefectList = new ArrayList<DefectRecord>();
                ArrayList<DefectRecord> mSeriousDefectList = new ArrayList<DefectRecord>();
                ArrayList<DefectRecord> mGeneralDefectList = new ArrayList<DefectRecord>();
                for (DefectRecord mDefectRecord : defectList) {
                    if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                        mCrisisDefectList.add(mDefectRecord);
                    } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                        mSeriousDefectList.add(mDefectRecord);
                    } else if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(mDefectRecord.defectlevel)) {
                        mGeneralDefectList.add(mDefectRecord);
                    }
                }
                if (!mCrisisDefectList.isEmpty()) {
                    groupList.add(Config.CRISIS_LEVEL);
                    groupHashMap.put(Config.CRISIS_LEVEL, mCrisisDefectList);
                }
                if (!mSeriousDefectList.isEmpty()) {
                    groupList.add(Config.SERIOUS_LEVEL);
                    groupHashMap.put(Config.SERIOUS_LEVEL, mSeriousDefectList);
                }
                if (!mGeneralDefectList.isEmpty()) {
                    groupList.add(Config.GENERAL_LEVEL);
                    groupHashMap.put(Config.GENERAL_LEVEL, mGeneralDefectList);
                }
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:

                if (mExistDefectAdapter == null) {
                    mExistDefectAdapter = new ReadyExistDefectAdapter(currentActivity);
                    mExistDefectAdapter.setOnAdapterViewClickListener(this);
                    binding.lvPinnedContainer.setAdapter(mExistDefectAdapter);
                }
                mExistDefectAdapter.setGroupList(groupList);
                mExistDefectAdapter.setGroupMap(groupHashMap);
                break;

            default:
                break;
        }
    }

    @Override
    public void OnAdapterViewClick(View view, DefectRecord mDefect) {

        int i1 = view.getId();
        if (i1 == R.id.iv_defect_image) {
            if (mDefect != null) {
                if (!TextUtils.isEmpty(mDefect.pics)) {
                    ArrayList<String> defectImageList = new ArrayList<String>();
                    String[] defectImageArray = mDefect.pics.split(Config.COMMA_SEPARATOR);
                    for (int i = 0, count = defectImageArray.length; i < count; i++) {
                        defectImageList.add(Config.RESULT_PICTURES_FOLDER + defectImageArray[i]);
                    }
                    showImageDetails(this, defectImageList);
                }
            }


        } else {
        }
    }
}
