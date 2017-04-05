package com.cnksi.sjjc.sync;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.adapter.SyncInfoAdapter;
import com.cnksi.ksynclib.model.SyncInfo;
import com.cnksi.ksynclib.utils.KNetUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.databinding.ActivityNetworkSyncBinding;
import com.cnksi.sjjc.service.DepartmentService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cnksi.ksynclib.KSync.SYNC_ERROR;
import static com.cnksi.ksynclib.KSync.SYNC_PING;

public class NetWorkSyncActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int DELETE_FINISHED = 0x111;
    ActivityNetworkSyncBinding binding;

    ArrayList<SyncInfo> mSyncInfos = new ArrayList<>();
    SyncInfoAdapter mSyncInfoAdapter = null;
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    KHandler handler = new KHandler();
    KSync ksync = null;
    KNConfig config = null;
    NetWorkSyncActivity currentActivity;
    String dept_id;
    boolean hasError = false;
    private boolean isSyncFile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        binding = ActivityNetworkSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mSyncInfos.addAll(SyncInfo.getCommonSyncList());
        mSyncInfoAdapter = new SyncInfoAdapter(currentActivity, mSyncInfos);
        binding.lvContainer.setAdapter(mSyncInfoAdapter);
        initKsync();
    }

    public void initKsync() {
        config = KSyncConfig.getInstance().getKNConfig(this);
        binding.tvSerialNumber.setText("设备ID:" + config.getClientid());
        ksync = new KSync(config, handler);
        String deptName = "无";
        dept_id = KSyncConfig.getInstance().getDept_id();
        if (!"-1".equals(dept_id)) {
            Department department = DepartmentService.getInstance().findDepartmentById(dept_id);
            if (department != null)
                deptName = StringUtils.BlankToDefault(department.name, department.dept_name, department.pms_name, dept_id);
        }
        binding.tvDept.setText("当前班组： " + deptName);
        String url = config.getUrl();
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains(":")) {
            url = url.substring(0, url.indexOf(":"));
        }
        ping("ping -i 3 " + url, handler);
    }


    @Override
    public void onClick(View view) {
        hasError = false;
        if (view.getId() == R.id.ibtn_sync_menu) {
            SyncMenuUtils.showMenuPopWindow(currentActivity, binding.ibtnSyncMenu, R.array.NetSyncMenuArray, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:// 切换到USB同步
                            SyncMenuUtils.changeSync(currentActivity);
                            break;
                        case 1:// 删除数据库
                            SyncMenuUtils.dropDb(currentActivity, ksync, handler);
                            break;
                        case 2: // 删除文件
                            SyncMenuUtils.deleteBakFile(currentActivity, ksync, handler);
                            break;
                        case 3: // 上传数据库
                            SyncMenuUtils.uploadDatabase(currentActivity, ksync, handler, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mSyncInfos.clear();
                                    mSyncInfoAdapter.notifyDataSetChanged();
                                }
                            });
                            break;
                        case 4://删除除本班组以外的数据
                            SyncMenuUtils.deleteOtherDepartmentData(currentActivity, handler, dept_id);
                            break;

                        default:
                            break;
                    }

                }
            });
            return;
        }
        if (!KNetUtil.isNetworkConnected(getApplicationContext())) {
            handler.sendMessage(handler.obtainMessage(KSync.SYNC_ERROR, "网络未连接,请检查手机网络!"));
            return;
        }
        switch (view.getId()) {
            case R.id.tv_download: //下载数据
                SyncMenuUtils.ShowTipsDialog(currentActivity, KSyncConfig.getInstance().isHaveDept() ? " 确认要从服务器端更新数据么?" : "当前是未登陆状态，仅同步基础数据。\n确认从服务器端更新基础数据？", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!KSyncConfig.getInstance().isHaveDept()) {
                            handler.sendMessage(handler.obtainMessage(KSync.SYNC_INFO, "当前没有登陆，本次同步仅同步基础数据！"));
                        }
                        download();
                    }
                });
                break;
            case R.id.tv_upload://上传数据
                SyncMenuUtils.ShowTipsDialog(currentActivity, " 确认要将本机数据更新到服务器端么?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upload();
                    }
                });
                break;
            default:
        }
    }


    private void upload() {
        setButtonStyle(false);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ksync.uploadData();
                if (config.isUploadFile()) {
                    isSyncFile = true;
                    if (TextUtils.isEmpty(config.getUploadFolder())) {
                        ksync.uploadFile();
                    } else {
                        ksync.uploadFile(config.getUploadFolder());
                    }
                }
            }
        });
    }

    private void download() {
        setButtonStyle(false);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ksync.download();

                if (config.isDownFile()) {
                    isSyncFile = true;
                    //第一次同步的时候直接同步变电站数据 不需要再次初始化。
//                    KSyncConfig.getInstance().initFolder();
//                    config.configDownFolder(KSyncConfig.getInstance().getDownFolderString());
                    if (TextUtils.isEmpty(config.getDownFolder())) {
                        ksync.downFile();
                    } else {
                        ksync.downFile(config.getDownFolder());
                    }
                }
            }
        });
    }

    /**
     * 更改按钮样式
     */
    private void setButtonStyle(boolean enabled) {
        if (!enabled) {
            mSyncInfos.clear();
            binding.tvInfoLabel.setText("同步日志");
        }
        binding.tvUpload.setEnabled(enabled);
        binding.tvDownload.setEnabled(enabled);
    }

    public class KHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            Log.d("KHandler", String.valueOf(msg.obj));

            SyncInfo info = null;
            switch (msg.what) {
                case KSync.SYNC_ERROR:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_ERROR);
                    setButtonStyle(true);
                    hasError = true;
                    break;
                case KSync.SYNC_INFO:
                case KSync.SYNC_CONNECTING:
                case KSync.SYNC_START:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_INFO);
                    break;
                case KSync.SYNC_SUCCESS:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_SUCCESS);
                    break;
                case KSync.SYNC_PING:
                    setNetwork(msg);
                    break;
                case KSync.SYNC_SERVER_TIME:
                    //去设置时间
                    setTime(String.valueOf(msg.obj));
                    break;
                case DELETE_FINISHED:
                    Toast.makeText(currentActivity, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                    CustomerDialog.dismissProgress();
                    break;
                case KSync.SYNC_FINISH:
                    Toast.makeText(currentActivity, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
                    setButtonStyle(true);
                    break;
            }
            if (info != null) {
                mSyncInfos.add(info);
                mSyncInfoAdapter.notifyDataSetChanged();
                binding.lvContainer.setSelection(mSyncInfoAdapter.getCount() - 1);
            }
        }
    }


    private void setTime(String serverTimeStr) {
        if (!TextUtils.isEmpty(serverTimeStr)) {
            try {
                long serverTime = Long.parseLong(serverTimeStr);
                if (Math.abs(serverTime - System.currentTimeMillis()) > 5 * 60 * 1000) {
                    Toast.makeText(currentActivity, "手机时间不正确，请设置手机时间", Toast.LENGTH_LONG).show();
                    //时间设置
                    Intent mTimeIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                    startActivity(mTimeIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置当前的网络质量
     *
     * @param msg
     */
    private void setNetwork(Message msg) {
        if (msg.obj != null && !TextUtils.isEmpty(String.valueOf(msg.obj))) {
            String pingResult = String.valueOf(msg.obj);
            try {
//                1~30ms：极快，几乎察觉不出有延迟，玩任何游戏速度都特别顺畅
//                31~50ms：良好，可以正常游戏，没有明显的延迟情况
//                51~100ms：普通，对抗类游戏能感觉出明显延迟，稍有停顿
//                100ms：差，无法正常游戏，有卡顿，丢包并掉线现象
                if (pingResult.contains("time=") && pingResult.contains("ms")) {
                    String timeStr = pingResult.substring(pingResult.indexOf("time=") + 5, pingResult.lastIndexOf("ms")).trim();
                    if (!TextUtils.isEmpty(timeStr)) {
                        float time = Float.parseFloat(timeStr);
                        binding.tvNetwork.setTextColor(getResources().getColor(R.color.sync_error_info_text_color));
                        if (time > 1 && time < 30) {
                            binding.tvNetwork.setTextColor(getResources().getColor(R.color.sync_green));
                            binding.tvNetwork.setText("网络情况:极快 " + timeStr);
                        } else if (time > 31 && time < 50) {
                            binding.tvNetwork.setTextColor(getResources().getColor(R.color.sync_orange));
                            binding.tvNetwork.setText("网络情况:良好 " + timeStr);
                        } else if (time > 51 && time < 100) {
                            binding.tvNetwork.setText("网络情况:普通 " + timeStr);
                        } else {
                            binding.tvNetwork.setText("网络情况:差 " + timeStr);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(null);
        pinging = false;
        CustomerDialog.dismissProgress();
        super.onDestroy();
    }

    boolean pinging = false;

    public void ping(final String cmdLine, final Handler handler) {
        if (pinging == true) return;
        pinging = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = Runtime.getRuntime().exec(cmdLine);
                    BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(p.getInputStream()), 8192);
                    do {
                        String s1 = bufferedreader.readLine();
                        if (s1 == null) {
                            bufferedreader.close();
                            p.destroy();
                            break;
                        }
                        handler.sendMessage(handler.obtainMessage(SYNC_PING, s1));
                    } while (pinging);
                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(SYNC_ERROR, "命令失败,错误原因:" + ioexception.getMessage()));
                }
            }
        }).start();
    }
}
