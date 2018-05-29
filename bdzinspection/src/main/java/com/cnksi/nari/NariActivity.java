package com.cnksi.nari;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.bdzinspection.activity.TaskRemindActivity;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityNariBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSelectBinding;
import com.cnksi.bdzinspection.inter.GrantPermissionListener;
import com.cnksi.bdzinspection.utils.MyUUID;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.listener.OnViewClickListener;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.Users;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.XZip;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
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
import com.cnksi.sync.KSyncConfig;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_nari);
        account = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "").split(",")[0];
    }


    public void initialUI() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> initialData());
        adapter = new ItemAdapter(null);
        binding.ibtnCancel.setOnClickListener(v -> finish());
        binding.listZyb.setAdapter(adapter);
        binding.btnVpn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.sgcc.vpn_client", "com.sgcc.vpn_client.MainActivity");
                intent.setComponent(componentName);
                NariActivity.this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                ToastUtils.showMessage("没有安装VPN客户端。");
            }

        });
        binding.btnDownAll.setOnClickListener(v -> {
            final BDPackage[] nodowns = adapter.getStatus(PackageStatus.nodown);
            if (nodowns.length == 0) {
                NariActivity.this.Toast("没有需要下载的离线作业包！");
                return;
            }
            DialogUtils.showSureTipsDialog(mActivity, null, "本次将会下载" + nodowns.length + "个离线作业包！", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    downLoad(nodowns);
                }
            });
        });
        binding.btnUploadAll.setOnClickListener(v -> {
            final BDPackage[] dones = adapter.getStatus(PackageStatus.done);
            if (dones.length == 0) {
                NariActivity.this.Toast("没有需要上传的离线作业包！");
                return;
            }
            DialogUtils.showSureTipsDialog(mActivity, null, "本次将会有" + dones.length + "个离线作业包上传到PMS！", new OnViewClickListener() {
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
            Collections.sort(result, (o1, o2) -> o1.createTime.compareTo(o2.createTime));
            runOnUiThread(() -> {
                adapter.setList(result);
                binding.swipeRefreshLayout.setRefreshing(false);
            });
            isNeedUpdateStatus = false;
        }
    }

    public void initialData() {
        initData(false);
    }

    public void initData(final boolean isRefreshStatus) {
        if (!isInit) {
            ToastUtils.showMessage("初始化失败。请确认是正常操作流程!");
            return;
        }
        ExecutorManager.executeTask(() -> {
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
                    NariActivity.this.Toast(tips);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (NARIHelper.isNetWorkException(e)) {
                    NariActivity.this.Toast("网络错误!参考错误代码：" + e.getMessage());
                } else {
                    NariActivity.this.Toast("请求发生错误：" + e.getMessage());
                }
                LogUtil.writeLog("Nari", e);
                e.printStackTrace();
                LogUtil.writeLog("Nari", e);
                NariActivity.this.Toast("数据错误！参考错误代码：" + e.getClass().getSimpleName());
            } catch (PMSException e) {
                e.printStackTrace();
                LogUtil.writeLog("Nari", e);
                NariActivity.this.Toast("PMS异常：" + e.getMessage());
            }
            final List<BDPackage> result = new ArrayList<>(packageHashMap.values());

            Collections.sort(result, (o1, o2) -> o1.createTime.compareTo(o2.createTime));
            NariActivity.this.runOnUiThread(() -> {
                adapter.setList(result);
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        });
    }

    @Override
    public void allPermissionsGranted() {
        initialUI();
        init();
        initialData();
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
            NARIHelper.init(mActivity, users.account, AESUtil.decode(users.pwd), Config.NARI_BASEFOLDER);
        } catch (Exception e) {
            ToastUtils.showMessage("密码解密失败！");
            e.printStackTrace();
            return;
        }
        isInit = true;
    }

    private void Toast(final String str) {
        runOnUiThread(() -> {
            if (toast == null) {
                toast = Toast.makeText(mActivity, "", Toast.LENGTH_LONG);
            }
            toast.setText(str);
            toast.show();
        });
    }


    private boolean genData(BDPackage bdPackage, Report report) {
        try {

            DbModel model =UserService.getInstance().findDbModelFirst(new SqlInfo("select * from users where account='" + account + "'"));
//查询巡视人员的IDS 和Name
            SqlInfo sqlInfo = new SqlInfo("SELECT distinct u.account  account ,rs.name,u.pms_id FROM report_signname rs " +
                    "LEFT JOIN (SELECT * from users where dept_id=?) u ON rs.name = u.username WHERE report_id = ? ;");
            sqlInfo.addBindArg(new KeyValue("", model.getString("dept_id")));
            sqlInfo.addBindArg(new KeyValue("", model.getString(report.reportid)));
            List<DbModel> users = UserService.getInstance().findDbModelAll(sqlInfo);
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
        CustomerDialog.showProgress(mActivity, "正在上传...");
        ExecutorManager.executeTask(() -> {
            for (BDPackage bdPackage : bdPackages) {
                Report report;
                try {
                    report = ReportService.getInstance().getReportByTask(bdPackage.taskId);
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
                        ReportService.getInstance().update(report, "submittime");
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    KSyncConfig.getInstance().upload();
                }
            }
            dismissLoading();
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
        final Dialog dialog = CustomerDialog.showProgress(mActivity, "正在下载第1个,共" + bdPackages.length + "个...");
        ExecutorManager.executeTask(() -> {
            int i = 1;
            for (BDPackage bdPackage : bdPackages) {
                File f = new File(bdPackage.getDatabasePath());
                if (f.exists() && f.length() > 5 * 1024) {
                    NariActivity.this.createTask(bdPackage);
                } else {
                    ResultSet<String> rs = NARIHelper.downloadBDPackage(bdPackage);
                    if (rs.getStatus() == ResultSet.SUCCESS) {
                        NariActivity.this.Toast("下载完成！");
                        bdPackage.downloadTime = DateUtils.getCurrentLongTime();
                        NariActivity.this.createTask(bdPackage);
                    } else {
                        if (NARIHelper.isNetWorkException(rs.getException())) {
                            NariActivity.this.Toast("网络错误!参考错误异常：" + rs.getException().getMessage());
                        } else {
                            NariActivity.this.Toast(rs.getDesc() + " 参考错误：" + rs.getException().getMessage());
                        }
                    }
                }
                i++;
                if (i <= bdPackages.length) {
                    final int finalI = i;
                    NariActivity.this.runOnUiThread(() -> ((TextView) dialog.findViewById(R.id.tv_tips)).setText("正在下载第" + finalI + "个, 共" + bdPackages.length + "个..."));
                }
            }
            runOnUiThread(() -> adapter.notifyDataSetChanged());
            dismissLoading();
        });
    }

    private void startTask(BDPackage bdPackage) {
        if (!TextUtils.isEmpty(bdPackage.taskId)) {
            Intent intent = new Intent(mActivity, TaskRemindActivity.class);
            intent.putExtra("task_id", bdPackage.taskId);
            startActivity(intent);
            return;
        } else {
            ToastUtils.showMessage("数据异常，该离线作业包还未绑定任务！");
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
                t = new Task(tid, bdz.bdzid, bdz.name, xsType.name(), xsType.zhName, xsjh.JHSJ, TaskStatus.undo.name(), account);
                t.pmsJhSource = "pms_app";
                t.pmsJhid = xsjh.OBJ_ID;
            } catch (DbException e) {
                e.printStackTrace();
                Toast("查询变电站出错！");
                return false;
            }
            if (t != null) {
                try {
                    TaskService.getInstance().saveOrUpdate(t);
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
        runOnUiThread(() -> CustomerDialog.dismissProgress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class ItemAdapter extends BaseAdapter<BDPackage> {

        public ItemAdapter(Collection<BDPackage> data) {
            super(mActivity, data, R.layout.xs_item_list_nari);
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

                holder.setText(R.id.tv_date_start, TextUtils.isEmpty(item.startTime) ? "" : DateUtils.getFormatterTime(item.startTime, DateUtils.yyyy_MM_dd_HH_mm_ss));
            }

            holder.setText(R.id.tv_date, TextUtils.isEmpty(item.createTime) ? "" : item.createTime);
            holder.getRootView().setOnLongClickListener(view -> {
                int width = ScreenUtils.getScreenWidth(NariActivity.this.getApplicationContext()) * 9 / 10;
                XsDialogSelectBinding selectBinding = XsDialogSelectBinding.inflate(NariActivity.this.getLayoutInflater());
                Dialog dialog = DialogUtils.createDialog(mActivity, selectBinding.getRoot(), width, LinearLayout.LayoutParams.WRAP_CONTENT);
                selectBinding.tvDialogContent.setText(R.string.xs_pms_task_delete_tips);
                dialog.show();
                selectBinding.btnCancel.setOnClickListener(view1 -> dialog.dismiss());
                selectBinding.btnSure.setOnClickListener(view1 -> {
                    if (selectBinding.checkboxLeft.isChecked() || selectBinding.checkboxRight.isChecked()) {
                        if (selectBinding.checkboxLeft.isChecked()) {
                            NariActivity.this.deltePacage(item, 0, dialog);
                        } else {
                            NariActivity.this.deltePacage(item, 1, dialog);
                        }
                    } else {
                        ToastUtils.showMessage("请选择需要删除的范围");
                    }

                });
                return true;
            });
            holder.getRootView().setOnClickListener(v -> {
                PackageStatus packageStatus = PackageStatus.find(item.status);
                if (packageStatus == null) {
                    Toast("离线作业包状态错误！");
                    return;
                }
                switch (packageStatus) {
                    case nodown:
                        //ToDown;
                        DialogUtils.showSureTipsDialog(mActivity, null, "是否要下载该离线作业包？", new OnViewClickListener() {
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
                        DialogUtils.showSureTipsDialog(mActivity, null, StringUtils.formatPartTextColor("是否要上传PMS离线作业包？\n%s", Color.RED, "说明：暂时上传内容不包含缺陷和抄录信息"), new OnViewClickListener() {
                            @Override
                            public void onClick(View v) {
                                upload(item);
                            }
                        });
                        break;
                    case upload_error:
                        DialogUtils.showSureTipsDialog(mActivity, null, StringUtils.formatPartTextColor("是否要重新上传PMS离线作业包？\n%s", Color.RED, "说明：暂时上传内容不包含缺陷和抄录信息"), new OnViewClickListener() {
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
            });
        }
    }

    private String delResult;

    private void deltePacage(BDPackage item, int delteNum, Dialog dialog) {
        CustomerDialog.showProgress(mActivity, "正在删除该离线任务，请稍后...");
        ExecutorManager.executeTask(() -> {
            try {
                if (delteNum == 1) {
                    NariActivity.this.deleteLocalData(item);
                    delResult = "本地删除成功";
                } else {
                    delResult = NARIHelper.deletePackageFromServer(item.packageID);
                    FileUtils.deleteAllFiles(Config.NARI_BASEFOLDER + item.packageID);
                    NariActivity.this.deleteLocalData(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                delResult = "任务删除失败,请检查网络，稍后重试。";
            }
            NariActivity.this.runOnUiThread(() -> {
                ToastUtils.showMessage(delResult);
                dialog.dismiss();
                List<BDPackage> packages = (List<BDPackage>) adapter.getData();
                packages.remove(item);
                adapter.setList(packages);
                NariActivity.this.dismissLoading();
            });
        });
    }

    public void deleteLocalData(BDPackage item) {
        try {
            BaseService.getInstance(BDPackage.class).logicDeleteById(item.packageID);
            if (!TextUtils.isEmpty(item.status) && PackageStatus.undo.name().equalsIgnoreCase(item.status)) {
                TaskService.getInstance().update(WhereBuilder.b(Task.PMS_JHID, "=", item.pmsJhid), new KeyValue("dlt", "1"));
                ReportService.getInstance().update( WhereBuilder.b(Report.PMS_JHID, "=", item.pmsJhid), new KeyValue("dlt", "1"));
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
