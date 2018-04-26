package com.cnksi.sjjc.sync;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.sjjc.util.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.adapter.SyncInfoAdapter;
import com.cnksi.ksynclib.model.SyncInfo;
import com.cnksi.ksynclib.utils.KNetUtil;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.databinding.ActivityNetworkSyncBinding;
import com.cnksi.sjjc.service.DepartmentService;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    Map<String, SyncInfo> cacheMap = new HashMap<>();
    boolean pinging = false;
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
        binding.lvContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SyncInfo syncInfo = mSyncInfoAdapter.getItem(position);
                if (syncInfo.type == KSync.SYNC_ERROR) {
                    Dialog dialog = new Dialog(currentActivity);
                    dialog.setContentView(View.inflate(currentActivity, R.layout.sync_info_dialog, null));
                    dialog.setCanceledOnTouchOutside(true);
                    TextView tv = (TextView) dialog.findViewById(com.cnksi.ksynclib.R.id.tv_toast);
                    tv.setText(syncInfo.content);
                    tv.setMovementMethod(ScrollingMovementMethod.getInstance());
                    dialog.show();
                }
            }
        });
    }

    public void initKsync() {
        config = KSyncConfig.getInstance().getKNConfig(this);
        binding.tvSerialNumber.setText("设备ID:" + config.getClientid());
        ksync = new KSync(config, handler);
        try {
            HttpUtilsProxy.hack(ksync, config);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
        //ping("ping -i 3 " + url, handler);
    }

    @Override
    public void onClick(View view) {
        hasError = false;
        if (view.getId() == R.id.ibtn_sync_menu) {
            SyncMenuUtils.showMenuPopWindow(currentActivity, binding.ibtnSyncMenu, R.array.NetSyncMenuArray, (parent, view1, position, id) -> {
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

            });
            return;
        }
        if (!KNetUtil.isNetworkConnected(getApplicationContext())) {
            handler.sendMessage(handler.obtainMessage(KSync.SYNC_ERROR, "网络未连接,请检查手机网络!"));
            return;
        }
        switch (view.getId()) {
            //下载数据
            case R.id.tv_download:
                SyncMenuUtils.ShowTipsDialog(currentActivity, KSyncConfig.getInstance().isHaveDept() ? " 确认要从服务器端更新数据么?" : "当前是未登陆状态，仅同步基础数据。\n确认从服务器端更新基础数据？", v -> {
                    cacheMap.clear();
                    if (!KSyncConfig.getInstance().isHaveDept()) {
                        handler.sendMessage(handler.obtainMessage(KSync.SYNC_INFO, "当前没有登陆，本次同步仅同步基础数据！"));
                    }
                    download();
                });
                break;
            //上传数据
            case R.id.tv_upload:
                SyncMenuUtils.ShowTipsDialog(currentActivity, " 确认要将本机数据更新到服务器端么?", v -> {
                    cacheMap.clear();
                    upload();
                });
                break;
            default:
        }
    }

    private void upload() {
        setButtonStyle(false);
        executorService.execute(() -> {
            ksync.uploadData();
            if (config.isUploadFile()) {
                isSyncFile = true;
                if (TextUtils.isEmpty(config.getUploadFolder())) {
                    ksync.uploadFile();
                } else {
                    ksync.uploadFile(config.getUploadFolder());
                }
            }
        });
    }

    private void download() {
        setButtonStyle(false);
        executorService.execute(() -> {
            if (!KSyncConfig.getInstance().isHaveDept()) {
                ksync.download("city", "users", "department", "pad_apk_version");
                DbModel model = null;
                try {
                    model = CustomApplication.getInstance().getDbManager().findDbModelFirst(new SqlInfo("select short_name_pinyin from city"));
                    String firstDownApk = "admin/" + model.getString("short_name_pinyin") + "/apk";
                    ksync.downFile(firstDownApk);
                } catch (DbException e) {
                    e.printStackTrace();
                }

            } else {
                ksync.download();
            }
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
        });
    }

    /**
     * 更改按钮样式
     */
    private void setButtonStyle(boolean enabled) {
        if (!enabled) {
            mSyncInfos.clear();
            mSyncInfoAdapter.notifyDataSetChanged();
            binding.tvInfoLabel.setText("同步日志");
        }
        binding.tvUpload.setEnabled(enabled);
        binding.tvDownload.setEnabled(enabled);
    }

    private SyncInfo getSyncInfo(String s) {
        String key;
        if (s.startsWith("[")) {
            key = s.replaceAll("^\\[[\\d]*%\\]", "");
            if (s.startsWith("[成功]"))
                key = s.substring(4);
        } else if (s.startsWith("下载文件")) {
            key = s.substring(4);
        } else {
            return new SyncInfo(s, KSync.SYNC_INFO);
        }
        key = key.trim();
        if (TextUtils.isEmpty(key)) {
            return new SyncInfo(s, KSync.SYNC_INFO);
        } else {
            SyncInfo info = cacheMap.get(key);
            if (info == null) {
                info = new SyncInfo(s, KSync.SYNC_INFO);
                cacheMap.put(key, info);
                return info;
            }
            info.content = s;
            return null;
        }
    }

    private void showDialogTips() {
        SyncMenuUtils.ShowTipsDialog(currentActivity, "基本数据已同步完成，可以返回开始运维工作；文件在后台下载，不影响使用", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity.finish();
            }
        });
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

    public void ping(final String cmdLine, final Handler handler) {
        if (pinging == true) return;
        pinging = true;
        new Thread(() -> {
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
        }).start();
    }

    public class KHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            Log.d("KHandler", String.valueOf(msg.obj));
            if (isDestroyed()) {
                return;
            }
            SyncInfo info = null;
            switch (msg.what) {
                case KSync.SYNC_ERROR:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_ERROR);
                    setButtonStyle(true);
                    hasError = true;
                    break;
                case KSync.SYNC_INFO:
                    info = getSyncInfo(String.valueOf(msg.obj));
                    break;
                case KSync.SYNC_CONNECTING:
                case KSync.SYNC_START:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_INFO);
                    break;
                case KSync.SYNC_SUCCESS:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_SUCCESS);
//                    CustomApplication.saveDbVersion(ksync.getKnConfig().getDatabase().getVersion());
//                    if ((KSyncConfig.getInstance().isHaveDept())) {
//                        showDialogTips();
//                    }
                    break;
                case KSync.SYNC_PING:
                    setNetwork(msg);
                    return;
                case KSync.SYNC_SERVER_TIME:
                    //去设置时间
                    // setTime(String.valueOf(msg.obj));
                    return;
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
            }
            mSyncInfoAdapter.notifyDataSetChanged();
            binding.lvContainer.setSelection(mSyncInfoAdapter.getCount() - 1);
        }
    }
}
