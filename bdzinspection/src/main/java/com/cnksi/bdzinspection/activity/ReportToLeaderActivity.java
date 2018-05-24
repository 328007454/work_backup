package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ReportLeaderAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityReportToLeaderBinding;
import com.cnksi.bdzinspection.model.TestPerson;
import com.cnksi.bdzinspection.utils.DefectLevelUtils;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 上报界面
 *
 * @author terry
 */
public class ReportToLeaderActivity extends BaseActivity {

    private ReportLeaderAdapter mReportLeaderAdapter = null;
    private ArrayList<TestPerson> dataList = null;
    private XsActivityReportToLeaderBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_report_to_leader);
        initialUI();
        initOnClick();
    }


    private void initialUI() {

        getIntentValue();

        binding.includeTitle.tvTitle.setText(R.string.xs_select_leader_str);
        String defectDescription = getIntent().getStringExtra(DefectRecord.DESCRIPTION);
        String defectLevel = getIntent().getStringExtra(DefectRecord.DEFECTLEVEL);
        defectLevel = DefectLevelUtils.convert2DefectLevel(defectLevel);
        String deviceName = getIntent().getStringExtra(Config.CURRENT_DEVICE_NAME);
        binding.etReportContent.setText(getString(R.string.xs_report_defect_description_format_str, currentBdzName, deviceName, defectLevel, defectDescription));


        dataList = TestPerson.getPersonList();
        mHandler.sendEmptyMessage(LOAD_DATA);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:

                if (mReportLeaderAdapter == null) {
                    mReportLeaderAdapter = new ReportLeaderAdapter(currentActivity, dataList);
                    binding.lvContainer.setAdapter(mReportLeaderAdapter);
                } else {
                    mReportLeaderAdapter.setList(dataList);
                }


                break;

            default:
                break;
        }
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            this.finish();
        });
        binding.btnCancel.setOnClickListener(view -> finish());

        binding.btnReport.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.send);
            List<String> phoneNumberList = new ArrayList<String>();
            phoneNumberList.add("13981308155");
            sendMessage(phoneNumberList, binding.etReportContent.getText().toString().trim());
        });

        binding.lvContainer.setOnItemClickListener((parent, view, position, id) -> {
            TestPerson mPerson = (TestPerson) parent.getItemAtPosition(position);

            mPerson.isChecked = !mPerson.isChecked;
            mReportLeaderAdapter.notifyDataSetChanged();
        });
    }


    /**
     * 发送短信
     *
     * @param phoneNumList 电话号码
     * @param msgContent   短信内容
     */
    private void sendMessage(List<String> phoneNumList, String msgContent) {
        Intent sentIntent = new Intent("SENT_SMS_ACTION");
        PendingIntent sentPI = PendingIntent.getBroadcast(currentActivity, 0, sentIntent, 0);
        currentActivity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ToastUtils.showMessage("短信发送成功");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        ToastUtils.showMessage("短信发送失败");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                    default:
                        break;
                }
            }
        }, new IntentFilter("SENT_SMS_ACTION"));

        // create the deilverIntent parameter
        Intent deliverIntent = new Intent("DELIVERED_SMS_ACTION");
        PendingIntent deliverPI = PendingIntent.getBroadcast(currentActivity, 0, deliverIntent, 0);
        currentActivity.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                ToastUtils.showMessage("收信人已经成功接收");
            }
        }, new IntentFilter("SENT_SMS_ACTION"));

        // 直接调用短信接口发短信
        SmsManager smsManager = SmsManager.getDefault();
        for (int i = 0, count = phoneNumList.size(); i < count; i++) {
            List<String> divideContents = smsManager.divideMessage(msgContent);
            for (String text : divideContents) {
                String phoneNumber = phoneNumList.get(i);
                if (!phoneNumber.startsWith("+86")) {
                    phoneNumber = "+86" + phoneNumber;
                }
                smsManager.sendTextMessage(phoneNumber, null, text, sentPI, deliverPI);
            }
        }
    }

}
