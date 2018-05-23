package com.cnksi.bdzinspection.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.CopyMaintenanceDeviceAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.databinding.XsActivityCopyAll3Binding;
import com.cnksi.bdzinspection.databinding.XsMaintenanceCopyItemBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.utils.CopyViewUtil;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * Created by han on 2017/9/1.
 */

public class CopyMaintenanceDeviceActivity extends BaseActivity implements CopyViewUtil.KeyBordListener {
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;

    private boolean isSpread = true;
    private boolean isFinish;
    private CopyMaintenanceDeviceAdapter deviceAdapter;

    List<CopyItem> copyItems = null;
    List<CopyResult> copyResults = null;

    private HashMap<String, String> copyHashSet = new HashMap<String, String>();
    private CopyItem currentItem = null;
    private XsActivityCopyAll3Binding binding;

    private CountDownTimer timer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            binding.shadomTip1.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            binding.shadom1.setVisibility(View.GONE);
            clickIndex = 0;
            mAfterTime = 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_copy_all3);
        setDeviceListDisplay();
        initialUI();
        initialData();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
        binding.tvTitle.setText(currentInspectionTypeName + "记录");
        binding.tvBatteryTestStep.setVisibility(View.GONE);
        binding.llKeyboardHelpLayout.setVisibility(View.GONE);
        binding.tvBatteryTestStep.setBackgroundResource(R.drawable.xs_red_button_background_selector);
        deviceAdapter = new CopyMaintenanceDeviceAdapter(this, copyItems, R.layout.xs_device_item);
        binding.gvContainer.setAdapter(deviceAdapter);
        deviceAdapter.setItemClickListener(new ItemClickListener<CopyItem>() {
            @Override
            public void onItemClick(View v, CopyItem copyItem, int position) {
                if (isSpread) {
                    setDeviceListDisplay();
                }
                if (!showShadom()) {
                    if (null != copyItem) {
                        saveAll();
                        deviceAdapter.setCurrentSelectedPosition(position);
                        currentItem = copyItem;
                        if (!deviceAdapter.isLast()) {
                            isFinish = false;
                            binding.btnNext.setText(R.string.xs_next);
                        }
                        setCurrentDevice(position);

                    } else {
                        binding.copyContainer.removeAllViews();
                    }
                }
            }
        });
    }

    private HashMap<String, CopyResult> resultHashMap = new HashMap<>();

    private void initialData() {
        ExecutorManager.executeTask(() -> {

            try {
                copyItems = CopyItemService.getInstance().findAllMaintenanceHasCopyValue(currentInspectionType, currentBdzId);
                copyResults = CopyResultService.getInstance().findAllMaintenanceCopyResult(currentReportId, currentInspectionType, currentBdzId);
                for (CopyItem item : copyItems) {
                    for (CopyResult result : copyResults) {
                        if (item.id.equalsIgnoreCase(result.item_id)) {
                            resultHashMap.put(item.id, result);
                            copyHashSet.put(item.id, result.valSpecial);
                        }
                    }
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
                copyItems = new ArrayList<CopyItem>();
            }
        });
    }

    private XsMaintenanceCopyItemBinding XsMaintenanceCopyItemBinding;

    private void setCurrentDevice(final int position) {
        binding.copyContainer.removeAllViews();
        binding.gvContainer.smoothScrollToPosition(position);
        if (null != copyItems && !copyItems.isEmpty()) {
            currentItem = copyItems.get(position);
        } else {
            return;
        }
        XsMaintenanceCopyItemBinding = com.cnksi.bdzinspection.databinding.XsMaintenanceCopyItemBinding.inflate(getLayoutInflater());
        binding.copyContainer.addView(XsMaintenanceCopyItemBinding.getRoot());
        if (null != resultHashMap.get(currentItem.id)) {
            String value = resultHashMap.get(currentItem.id).valSpecial;
            XsMaintenanceCopyItemBinding.etCopyValues.setText(TextUtils.isEmpty(value) ? "" : value);
        }
        XsMaintenanceCopyItemBinding.etCopyValues.requestFocus();
        XsMaintenanceCopyItemBinding.tvGroupItem.setText(currentItem.description);
        XsMaintenanceCopyItemBinding.tvCopyContent.setText("抄录" + currentItem.description);
        XsMaintenanceCopyItemBinding.etCopyValues.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                copyHashSet.put(currentItem.id, value);
            }
        });

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                setCurrentDevice(0);
                deviceAdapter.setList(copyItems);
                deviceAdapter.setCopyDevice(copyHashSet);
                deviceAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems) {

    }

    @Override
    public void hideKeyBord() {

    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {

    }

    public boolean showShadom() {
        long mCurrentTime = System.currentTimeMillis();
        if (0 == clickIndex) {
            mAfterTime = mCurrentTime;
        }
        long diffTime = mCurrentTime - mAfterTime;
        clickIndex++;
        if (1000 >= diffTime && 3 <= clickIndex) {
            binding.shadom1.setVisibility(View.VISIBLE);
            ToastUtils.showMessageLong( "不要作弊哟，5秒后继续操作。");
            timer.start();
            return true;
        } else {
            if (500 <= diffTime) {
                mAfterTime = 0;
                clickIndex = 0;
            }
            return false;
        }
    }


    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlCopyAllValueContainer.getLayoutParams();
        if (!isSpread) {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_narrow);
            binding.copyContainer.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_amplify);
            binding.copyContainer.setVisibility(View.VISIBLE);
            params.height = AutoUtils.getPercentHeightSizeBigger((int) getResources().getDimension(R.dimen.xs_copy_all_value_container_height_px));
        }
        binding.rlCopyAllValueContainer.setLayoutParams(params);
        isSpread = !isSpread;
    }

    private void initOnClick() {
        binding.tvBatteryTestStep.setOnClickListener(view -> {
            saveAll();
        });
        binding.ibtnCancel.setOnClickListener(view -> {
            saveAll();
            onBackPressed();
        });
        binding.ibtnSpread.setOnClickListener(view -> {
            setDeviceListDisplay();
        });

        binding.btnPre.setOnClickListener(view -> {
            saveAll();
            if (!deviceAdapter.isFirst()) {
                deviceAdapter.pre();
            }
            if (isFinish) {
                isFinish = false;
                binding.btnNext.setText(R.string.xs_next);
            }
        });
        binding.btnNext.setOnClickListener(view -> {
            saveBtnNextData();
        });

    }

    private void saveBtnNextData() {
        saveAll();
        if (!isFinish) {
            if (deviceAdapter.isLast()) {
                binding.btnNext.setText(R.string.xs_finish_str);
                isFinish = true;
            } else {
                deviceAdapter.next();
            }
        } else {
            finish();
        }
    }


    public void saveAll() {
        if (null != currentItem && !TextUtils.isEmpty(currentItem.id)) {
            CopyResult result = resultHashMap.get(currentItem.id);
            if (null != result) {
                result.valSpecial = copyHashSet.get(currentItem.id);
            } else {
                result = new CopyResult(currentReportId, currentItem.id, currentBdzId, currentItem.device_name, currentItem.deviceid, currentItem.type_key, currentItem.description, copyHashSet.get(currentItem.id), currentItem.remark, currentItem.unit);
                resultHashMap.put(currentItem.id, result);
            }
            try {
                XunshiApplication.getDbUtils().saveOrUpdate(result);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != XsMaintenanceCopyItemBinding) {
            XsMaintenanceCopyItemBinding.etCopyValues.clearFocus();
        }
        KeyBoardUtils.closeKeybord(currentActivity);
    }
}
