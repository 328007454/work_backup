package com.cnksi.nari;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsActivityNariBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSelectBinding;
import com.cnksi.bdzinspection.inter.GrantPermissionListener;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.MyUUID;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.PermissionUtil;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.bdzinspection.utils.XZip;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.Users;
import com.cnksi.nari.model.BDPackage;
import com.cnksi.nari.model.XSJH;
import com.cnksi.nari.type.PackageStatus;
import com.cnksi.nari.type.Regulation;
import com.cnksi.nari.type.XSType;
import com.cnksi.nari.utils.AESUtil;
import com.cnksi.nari.utils.LogUtil;
import com.cnksi.nari.utils.NariDataManager;
import com.cnksi.nari.utils.PMSException;
import com.cnksi.nari.utils.ResultSet;
import com.cnksi.xscore.xsutils.CLog;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.cnksi.xscore.xsview.CustomerDialog;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/8/2 10:06
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NariActivity extends BaseActivity implements GrantPermissionListener {
    public static boolean isNeedUpdateStatus = false;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    XsActivityNariBinding binding;
    String account;
    ItemAdapter adapter;
    boolean isInit = false;
    Toast toast;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
//            UpDataToReportManager manager = UpDataToReportManager.Stub.asInterface(binder);
//            try {
//                manager.upDataWithUpPMS();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void bindServiceFun() {
        Intent intent = new Intent();
        intent.setPackage("com.cnksi.sjjc");
        intent.setAction("android.intent.action.LoadData");
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_nari);
        account = PreferencesUtils.getString(currentActivity, Config.CURRENT_LOGIN_ACCOUNT, "").split(",")[0];
        PermissionUtil.getInstance().setGrantPermissionListener(this).checkPermissions(this, Config.permissions);
    }

    public void initUI() {
        binding.swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        adapter = new ItemAdapter(null);
        binding.ibtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.listZyb.setAdapter(adapter);
        binding.btnVpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.sgcc.vpn_client", "com.sgcc.vpn_client.MainActivity");
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    CToast.showShort(currentActivity, "没有安装VPN客户端。");
                }

            }
        });
        binding.btnDownAll.setOnClickListener(v -> {
            final BDPackage[] nodowns = adapter.getStatus(PackageStatus.nodown);
            if (nodowns.length == 0) {
                Toast("没有需要下载的离线作业包！");
                return;
            }
            DialogUtils.showSureTipsDialog(currentActivity, null, "本次将会下载" + nodowns.length + "个离线作业包！", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    downLoad(nodowns);
                }
            });
        });
        binding.btnUploadAll.setOnClickListener(v -> {
            final BDPackage[] dones = adapter.getStatus(PackageStatus.done);
            bindServiceFun();
            if (dones.length == 0) {
                Toast("没有需要上传的离线作业包！");
                return;
            }
            DialogUtils.showSureTipsDialog(currentActivity, null, "本次将会有" + dones.length + "个离线作业包上传到PMS！", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    upload(dones);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedUpdateStatus) {
            HashMap<String, BDPackage> packageHashMap = NariDataManager.getPackageByUser(account);
            final List<BDPackage> result = new ArrayList<>(packageHashMap.values());
            Collections.sort(result, new Comparator<BDPackage>() {
                @Override
                public int compare(BDPackage o1, BDPackage o2) {
                    return o1.createTime.compareTo(o2.createTime);
                }
            });
            runOnUiThread(() -> {
                adapter.setList(result);
                binding.swipeRefreshLayout.setRefreshing(false);
            });
            isNeedUpdateStatus = false;
        }
    }

    public void initData() {
        initData(false);
    }

    public void initData(final boolean isRefreshStatus) {
        if (!isInit) {
            CToast.showShort(currentActivity, "初始化失败。请确认是正常操作流程!");
            return;
        }
        mFixedThreadPoolExecutor.execute(() -> {
            HashMap<String, BDPackage> packageHashMap = NariDataManager.getPackageByUser(account);
            try {
                List<BDPackage> bdPackages = NARIHelper.getPackage(Regulation.XS);
                String tips = "没有新的离线作业包！";
                if (bdPackages != null && bdPackages.size() > 0) {
                    List<BDPackage> saveList = new ArrayList<>();
                    for (BDPackage bdPackage : bdPackages) {
                        BDPackage t = packageHashMap.get(bdPackage.packageID);
                        if (t == null) {
                            bdPackage.user = account;
                            saveList.add(bdPackage);
                            packageHashMap.put(bdPackage.packageID, bdPackage);
                        } else {
                            if (PackageStatus.upload.equals(t.status)) {
                                t.status = PackageStatus.upload_error.name();
                                NariDataManager.getPackageManager().saveOrUpdate(t);
                            }
                        }
                    }
                    if (saveList.size() > 0) {
                        tips = "获取到" + saveList.size() + "个新的离线作业包";
                        NariDataManager.getPackageManager().saveOrUpdate(saveList);
                    }
                }
                if (!isRefreshStatus) {
                    Toast(tips);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (NARIHelper.isNetWorkException(e)) {
                    Toast("网络错误!参考错误代码：" + e.getMessage());
                } else {
                    Toast("请求发生错误：" + e.getMessage());
                }
                LogUtil.writeLog("Nari", e);
                e.printStackTrace();
                LogUtil.writeLog("Nari", e);
                Toast("数据错误！参考错误代码：" + e.getClass().getSimpleName());
            } catch (PMSException e) {
                e.printStackTrace();
                LogUtil.writeLog("Nari", e);
                Toast("PMS异常：" + e.getMessage());
            }
            final List<BDPackage> result = new ArrayList<>(packageHashMap.values());

            Collections.sort(result, (o1, o2) -> o1.createTime.compareTo(o2.createTime));
            runOnUiThread(() -> {
                adapter.setList(result);
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        });
    }

    @Override
    public void allPermissionsGranted() {
        initUI();
        init();
        initData();
    }

    public void init() {
        if (TextUtils.isEmpty(account)) {
            Toast("没有获取到当前登录用户！请重写进入！");
            return;
        }
        Users users = UserService.getInstance().findUserByAccount(account);
        if (users == null) {
            Toast("没有查询到当前用户(" + account + ")的详细信息！");
            return;
        }
        try {
            NARIHelper.init(currentActivity, users.account, AESUtil.decode(users.pwd), Config.NARI_BASEFOLDER);
        } catch (Exception e) {
            CToast.showShort(currentActivity, "密码解密失败！");
            e.printStackTrace();
            return;
        }
        isInit = true;
    }

    private void Toast(final String str) {
        runOnUiThread(() -> {
            if (toast == null) {
                toast = Toast.makeText(currentActivity, "", Toast.LENGTH_LONG);
            }
            toast.setText(str);
            toast.show();
        });
    }


    private boolean genData(BDPackage bdPackage, Report report) {
        try {

            DbModel model = XunshiApplication.getDbUtils().findDbModelFirst(new SqlInfo("select * from users where account='" + account + "'"));
//查询巡视人员的IDS 和Name
            SqlInfo sqlInfo = new SqlInfo("SELECT distinct u.account  account ,rs.name,u.pms_id FROM report_signname rs " +
                    "LEFT JOIN (SELECT * from users where dept_id=?) u ON rs.name = u.username WHERE report_id = ? ;");
            sqlInfo.addBindArg(new KeyValue("", model.getString("dept_id")));
            sqlInfo.addBindArg(new KeyValue("", model.getString(report.reportid)));
            List<DbModel> users = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            String ids = "";
            String names = "";
            //逗号分隔ID和name
            if (users != null) {
                for (DbModel user : users) {
                    String id = user.getString("pms_id");
                    String name = user.getString("name");
                    if (!TextUtils.isEmpty(id)) {
                        ids = ids + id + ",";
                    }
                    if (!TextUtils.isEmpty(name)) {
                        names = names + name + ",";
                    }
                }
            }
            if (ids.length() > 0) {
                ids = ids.substring(0, ids.length() - 1);
            }
            //如果没有取到当前报告的人员。则取当前登录人的Id和Name。
            if (names.length() > 0) {
                names = names.substring(0, names.length() - 1);
            } else {
                names = model.getString("pms_name");
                ids = model.getString("pms_id");
            }
            DataUtils dataUtils = new DataUtils(model, ids, names, bdPackage, report);
            String base = Config.NARI_BASEFOLDER + bdPackage.packageID + "/uploadFile/";
            String fileName = base + "data.xml";
            String zipFile = fileName.replace("xml", "zip");
            dataUtils.Build(fileName);
            CLog.e("生成data.xml成功");
            File f = new File(zipFile);
            if (f.exists()) {
                f.delete();
            }
            if (!(f = new File(base, "data.json")).exists()) {
                f.createNewFile();
            }
            XZip.createZip(zipFile, f, new File(fileName));
            Toast("生成数据包成功！");
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.writeLog("Nari", e);
            Toast("初始化上传数据失败！参考错误：" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.writeLog("Nari", e);
            Toast("生成数据失败！");
        }
        return false;
    }


    private void upload(final BDPackage... bdPackages) {
        CustomerDialog.showProgress(currentActivity, "正在上传...");
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (BDPackage bdPackage : bdPackages) {
                    Report report;
                    try {
                        report = XunshiApplication.getDbUtils().selector(Report.class).where(Report.TASK_ID, "=", bdPackage.taskId).findFirst();
                    } catch (DbException e) {
                        e.printStackTrace();
                        LogUtil.writeLog("Nari", e);
                        Toast("查询报告失败！参考异常：" + e.getMessage());
                        return;
                    }
                    if (report == null) {
                        Toast("没有查询到关联的报告!");
                        return;
                    }
                    String zipFile = Config.NARI_BASEFOLDER + bdPackage.packageID + "/uploadFile/data.zip";
                    final File f = new File(zipFile);
                    if (f.exists()) {
                        f.delete();
                    }
                    genData(bdPackage, report);
                    if (uploadFile(f, bdPackage)) {
                        Toast("上传数据包成功");
                        bdPackage.status = PackageStatus.upload.name();
                        try {
                            NariDataManager.getPackageManager().update(bdPackage, "status");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        //更新提交时间
                        report.submittime = DateUtils.getCurrentLongTime();
                        try {
                            XunshiApplication.getDbUtils().update(report, "submittime");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
                dismissLoading();
            }
        });

    }

    private boolean uploadFile(File zipFile, BDPackage bdPackage) {
        try {
            if (NARIHelper.uploadPackageFile(zipFile, bdPackage.packageID)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.writeLog("Nari", e);
            if (e.getMessage().contains("1140")) {
                Toast("该任务已经上传过或服务器状态异常，无法上传");
            } else if (NARIHelper.isNetWorkException(e)) {
                Toast("网络错误！参考代码：" + e.getMessage());
            } else {
                Toast("上传错误！参考代码:" + e.getMessage());
            }
        }
        return false;
    }


    private void downLoad(final BDPackage... bdPackages) {
        final Dialog dialog = CustomerDialog.showProgress(currentActivity, "正在下载第1个,共" + bdPackages.length + "个...");
        mFixedThreadPoolExecutor.execute(() -> {
            int i = 1;
            for (BDPackage bdPackage : bdPackages) {
                File f = new File(bdPackage.getDatabasePath());
                if (f.exists() && f.length() > 5 * 1024) {
                    createTask(bdPackage);
                } else {
                    ResultSet<String> rs = NARIHelper.downloadBDPackage(bdPackage);
                    if (rs.getStatus() == ResultSet.SUCCESS) {
                        Toast("下载完成！");
                        bdPackage.downloadTime = DateUtils.getCurrentLongTime();
                        createTask(bdPackage);
                    } else {
                        if (NARIHelper.isNetWorkException(rs.getException())) {
                            Toast("网络错误!参考错误异常：" + rs.getException().getMessage());
                        } else {
                            Toast(rs.getDesc() + " 参考错误：" + rs.getException().getMessage());
                        }
                    }
                }
                i++;
                if (i <= bdPackages.length) {
                    final int finalI = i;
                    runOnUiThread(() -> ((TextView) dialog.findViewById(com.cnksi.xscore.R.id.tv_tips)).setText("正在下载第" + finalI + "个, 共" + bdPackages.length + "个..."));
                }
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
            dismissLoading();
        });
    }

    private void startTask(BDPackage bdPackage) {
        if (!TextUtils.isEmpty(bdPackage.taskId)) {
            Intent intent = new Intent(currentActivity, TaskRemindActivity.class);
            intent.putExtra("task_id", bdPackage.taskId);
            startActivity(intent);
            return;
        } else {
            CToast.showShort(currentActivity, "数据异常，该离线作业包还未绑定任务！");
        }
    }

    private boolean createTask(BDPackage bdPackage) {
        File f = new File(bdPackage.getDatabasePath());
        if (!f.exists()) {
            Toast("数据库文件不存在，请确认是否下载出错！");
            return false;
        }
        try {
            XSJH xsjh = NariDataManager.parseDb3(f);
            XSType xsType = XSType.find(xsjh.XSLX);
            bdPackage.inspectionType = xsType.zhName;
            bdPackage.pmsJhid = xsjh.OBJ_ID;
            bdPackage.startTime = xsjh.JHSJ;
            if (xsType == null || !xsType.isValid) {
                Toast("暂时不支持该类型计划:" + xsType.zhName);
                bdPackage.status = PackageStatus.notopt.name();
                NariDataManager.getPackageManager().saveOrUpdate(bdPackage);
                return true;
            }
            String tid = MyUUID.id(4);
            Task t;
            try {
                Bdz bdz = BdzService.getInstance().findByPmsId(xsjh.BDZ);
                if (bdz == null) {
                    Toast("没有查询到巡视计划的变电站！");
                    return false;
                }
                t = new Task(tid, bdz.bdzid, bdz.name, xsType.name(), xsType.zhName, xsjh.JHSJ, Config.TaskStatus.undo.name(), account);
                t.pmsJhSource = "pms_app";
                t.pmsJhid = xsjh.OBJ_ID;
            } catch (DbException e) {
                e.printStackTrace();
                Toast("查询变电站出错！");
                return false;
            }
            if (t != null) {
                XunshiApplication.getDbUtils().addColumn(Task.class, Task.PMS_JHID);
                try {
                    XunshiApplication.getDbUtils().save(t);
                    bdPackage.taskId = tid;
                    bdPackage.status = PackageStatus.undo.name();
                    NariDataManager.getPackageManager().saveOrUpdate(bdPackage);
                    Toast("任务生成成功");
                    return true;
                } catch (DbException e) {
                    e.printStackTrace();
                    LogUtil.writeLog("Nari", e);
                    Toast("保存任务出错");
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.writeLog("Nari", e);
            Toast("解析数据失败！");
        }
        return false;
    }

    private void dismissLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomerDialog.dismissProgress();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mConnection);
        } catch (Exception e) {

        }

    }

    class ItemAdapter extends BaseAdapter<BDPackage> {

        public ItemAdapter(Collection<BDPackage> data) {
            super(currentActivity, data, R.layout.xs_item_list_nari);
        }

        public BDPackage[] getStatus(PackageStatus status) {
            ArrayList<BDPackage> rs = new ArrayList<>();
            for (BDPackage bdPackage : data) {
                if (status.equals(bdPackage.status)) {
                    rs.add(bdPackage);
                }
            }
            return rs.toArray(new BDPackage[0]);
        }


        @Override
        public void convert(ViewHolder holder, final BDPackage item, int position) {
            String packageName = "";
            String taskStatus = item.status;
            try {
                packageName = item.packageName.substring(0, item.packageName.length() - 17);
            } catch (Exception ex) {
                packageName = TextUtils.isEmpty(item.packageName) ? "" : item.packageName;
            }
            TextView txtStatus = holder.getView(R.id.tv_status);
            PackageStatus status = PackageStatus.find(taskStatus);
            if (PackageStatus.nodown == status) {
                holder.getView(R.id.view_task_status).setBackgroundResource(R.drawable.xs_pms_task_notdownload_color_ff7575);
                txtStatus.setTextColor(getResources().getColor(R.color.xs__ff7575_color));
            } else if (PackageStatus.undo == status) {
                holder.getView(R.id.view_task_status).setBackgroundResource(R.drawable.xs_pms_task_notfinish_color_889fff);
                txtStatus.setTextColor(getResources().getColor(R.color.xs__889fff_color));
            } else if (PackageStatus.done == status) {
                holder.getView(R.id.view_task_status).setBackgroundResource(R.drawable.xs_pms_taskfinished_color_01ce7f);
                txtStatus.setTextColor(getResources().getColor(R.color.xs__01ce7f_color));
            } else {
                holder.getView(R.id.view_task_status).setBackgroundResource(R.drawable.xs_pms_task_notop_color_888888);
                txtStatus.setTextColor(getResources().getColor(R.color.xs__888888_color));
            }
            txtStatus.setText(status.zhName);
            holder.setText(R.id.tv_packageName, packageName);
            holder.setText(R.id.tv_user, "PMS账号:" + item.user);
            if (TextUtils.isEmpty(item.startTime)) {
                holder.getView(R.id.layout_start).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.layout_start).setVisibility(View.VISIBLE);

                holder.setText(R.id.tv_date_start, TextUtils.isEmpty(item.startTime) ? "" : DateUtils.formatDateTime(item.startTime, CoreConfig.dateFormat2));
            }

            holder.setText(R.id.tv_date, TextUtils.isEmpty(item.createTime) ? "" : item.createTime);
            holder.getRootView().setOnLongClickListener(view -> {
                int width = ScreenUtils.getScreenWidth(getApplicationContext()) * 9 / 10;
                XsDialogSelectBinding selectBinding = XsDialogSelectBinding.inflate(getLayoutInflater());
                Dialog dialog = DialogUtils.createDialog(currentActivity, selectBinding.getRoot(), width, LinearLayout.LayoutParams.WRAP_CONTENT);
                selectBinding.tvDialogContent.setText(R.string.xs_pms_task_delete_tips);
                dialog.show();
                selectBinding.btnCancel.setOnClickListener(view1 -> dialog.dismiss());
                selectBinding.btnSure.setOnClickListener(view1 -> {
                    if (selectBinding.checkboxLeft.isChecked() || selectBinding.checkboxRight.isChecked()) {
                        if (selectBinding.checkboxLeft.isChecked()) {
                            deltePacage(item, 0, dialog);
                        } else {
                            deltePacage(item, 1, dialog);
                        }
                    } else {
                        CToast.showShort(currentActivity, "请选择需要删除的范围");
                    }

                });
                return true;
            });
            holder.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PackageStatus packageStatus = PackageStatus.find(item.status);
                    if (packageStatus == null) {
                        Toast("离线作业包状态错误！");
                        return;
                    }
                    switch (packageStatus) {
                        case nodown:
                            //ToDown;
                            DialogUtils.showSureTipsDialog(currentActivity, null, "是否要下载该离线作业包？", new OnViewClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downLoad(item);
                                }
                            });
                            break;
                        case undo:
                            startTask(item);
                            break;
                        case done:
                            DialogUtils.showSureTipsDialog(currentActivity, null, StringUtils.formatPartTextColor("是否要上传PMS离线作业包？\n%s", Color.RED, "说明：暂时上传内容不包含缺陷和抄录信息"), new OnViewClickListener() {
                                @Override
                                public void onClick(View v) {
                                    upload(item);
                                }
                            });
                            break;
                        case upload_error:
                            DialogUtils.showSureTipsDialog(currentActivity, null, StringUtils.formatPartTextColor("是否要重新上传PMS离线作业包？\n%s", Color.RED, "说明：暂时上传内容不包含缺陷和抄录信息"), new OnViewClickListener() {
                                @Override
                                public void onClick(View v) {
                                    upload(item);
                                }
                            });
                            break;
                        case upload:
                            Toast("该记录已经上传到PMS，快去PMS查看吧！");
                            break;
                        case notopt:
                            Toast("暂时不支持" + item.inspectionType + "类型作业计划！");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private String delResult;

    private void deltePacage(BDPackage item, int delteNum, Dialog dialog) {
        CustomerDialog.showProgress(currentActivity, "正在删除该离线任务，请稍后...");
        mFixedThreadPoolExecutor.execute(() -> {
            try {
                if (delteNum == 1) {
                    deleteLocalData(item);
                    delResult = "本地删除成功";
                } else {
                    delResult = NARIHelper.deletePackageFromServer(item.packageID);
                    FileUtils.deleteAllFiles(Config.NARI_BASEFOLDER + item.packageID);
                    deleteLocalData(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                delResult = "任务删除失败,请检查网络，稍后重试。";
            }
            runOnUiThread(() -> {
                CToast.showShort(currentActivity, delResult);
                dialog.dismiss();
                List<BDPackage> packages = (List<BDPackage>) adapter.getData();
                packages.remove(item);
                adapter.setList(packages);
                dismissLoading();
            });
        });
    }

    public void deleteLocalData(BDPackage item) {
        try {
            XunshiApplication.getDbUtils().deleteById(BDPackage.class, item.packageID);
            if (!TextUtils.isEmpty(item.status) && PackageStatus.undo.name().equalsIgnoreCase(item.status)) {
                XunshiApplication.getDbUtils().update(Task.class, WhereBuilder.b(Task.PMS_JHID, "=", item.pmsJhid), new KeyValue("dlt", "1"));
                XunshiApplication.getDbUtils().update(Report.class, WhereBuilder.b(Report.PMS_JHID, "=", item.pmsJhid), new KeyValue("dlt", "1"));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
