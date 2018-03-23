package com.cnksi.sjjc.sync;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.adapter.PopMenuAdapter;
import com.cnksi.ksynclib.utils.CommonUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.util.FileUtil;

import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.cnksi.core.utils.ScreenUtils.getScreenHeight;
import static com.cnksi.core.utils.ScreenUtils.getScreenWidth;
import static com.cnksi.ksynclib.activity.KSyncAJActivity.DELETE_FINISHED;
import static com.cnksi.sjjc.sync.SyncMenuUtils.DeleteModel.createBDZID;
import static com.cnksi.sjjc.sync.SyncMenuUtils.DeleteModel.createBDZ_ID;
import static com.cnksi.sjjc.sync.SyncMenuUtils.DeleteModel.createDEPT_ID;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/15 10:56
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SyncMenuUtils {

    /**
     * 显示菜单
     */
    public static PopupWindow showMenuPopWindow(final Activity mActivity, View view, int menuResId, final AdapterView.OnItemClickListener mOnItemClickListener) {
        // 动态加载弹出框布局
        View contentView = mActivity.getLayoutInflater().inflate(R.layout.sync_pop_menu, null, false);
        ListView mLvMenu = (ListView) contentView.findViewById(R.id.lv_menu);
        PopMenuAdapter mPopMenuAdapter = new PopMenuAdapter(mActivity, Arrays.asList(mActivity.getResources().getStringArray(menuResId)));
        mLvMenu.setAdapter(mPopMenuAdapter);
        // 设置菜单栏布局文件和显示大小
        final PopupWindow mMenuPopwindow = new PopupWindow(contentView, getScreenWidth(mActivity) / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置菜单栏背景为透明(不设置无法正常获取焦点)
        mMenuPopwindow.setBackgroundDrawable(new ColorDrawable());
        // 设置菜单栏动画
        mMenuPopwindow.setAnimationStyle(com.cnksi.ksynclib.R.style.PopwindowAnimtionStyle);
        // 设置菜单栏能够获取焦点
        mMenuPopwindow.setFocusable(true);
        // 更新设置
        mMenuPopwindow.update();
        mLvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(parent, view, position, id);
                }
                mMenuPopwindow.dismiss();
            }
        });
        mMenuPopwindow.showAsDropDown(view);
        return mMenuPopwindow;
    }

    /**
     * 显示提示dialog
     *
     * @param context
     * @param toast
     * @return
     */
    public static Dialog ShowTipsDialog(final Activity context, String toast, final View.OnClickListener okClickListener) {
        if (context != null) {
            final Dialog mCustomDialog = new Dialog(context, com.cnksi.ksynclib.R.style.SyncDialogStyle);
            mCustomDialog.setCancelable(false);
            View view = LayoutInflater.from(context).inflate(com.cnksi.ksynclib.R.layout.sync_dialog_customer, null);
            mCustomDialog.setContentView(view);
            TextView mTvToast = ((TextView) view.findViewById(com.cnksi.ksynclib.R.id.tv_toast));
            TextView mTvCancel = (TextView) view.findViewById(com.cnksi.ksynclib.R.id.tv_cancel);
            TextView mTvMultipleConfirm = (TextView) view.findViewById(com.cnksi.ksynclib.R.id.tv_multiple_confirm);
            mTvToast.setText(toast);
            mTvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCustomDialog.dismiss();
                }
            });
            mTvMultipleConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCustomDialog.dismiss();
                    if (okClickListener != null) {
                        okClickListener.onClick(view);
                    }
                }
            });
            Window mWindow = mCustomDialog.getWindow();
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
                lp.width = getScreenHeight(context) / 10 * 9;
            } else {
                lp.width = getScreenWidth(context) / 10 * 9;
            }
            mWindow.setAttributes(lp);
            mCustomDialog.show();
            return mCustomDialog;
        } else {
            return null;
        }
    }

    public static void changeSync(Activity mActivity) {
        if (mActivity instanceof NetWorkSyncActivity)
            KSyncConfig.getInstance().startUsbWorkSync(mActivity);
        else KSyncConfig.getInstance().startNetWorkSync(mActivity);
        mActivity.finish();
    }

    public static void dropDb(final Activity mActivity, final KSync ksync, final Handler handler) {
        ShowTipsDialog(mActivity, "确认删除数据库么?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerDialog.showProgress(mActivity, "删除中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ksync.dropDb();
                            handler.sendMessage(handler.obtainMessage(DELETE_FINISHED, "数据库删除完成"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }


    public static void deleteBakFile(final Activity mActivity, final KSync ksync, final Handler handler) {
        ShowTipsDialog(mActivity, "确认删除逾期文件么?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerDialog.showProgress(mActivity, "删除中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 删除两个月之前的备份文件
                        long time = 60l * 24l * 60l * 60l * 1000l;
                        Set<String> uploadFolder = KSyncConfig.getInstance().getUploadFolder();
                        Set<String> downFolder = KSyncConfig.getInstance().getDownFolder();
                        uploadFolder.removeAll(downFolder);//如果该文件夹是下载文件夹则忽略
                        for (String s : uploadFolder) {
                            FileUtil.deleteBakFiles(ksync.getKnConfig().getBaseFolder() + s, time);
                        }
                        handler.sendMessage(handler.obtainMessage(DELETE_FINISHED, "文件删除完成"));
                    }
                }).start();
            }
        });
    }


    public static void uploadDatabase(final Activity mActivity, final KSync ksync, final Handler handler, final View.OnClickListener listener) {
        ShowTipsDialog(mActivity, "确认上传数据库么?", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                upLoadData(mActivity,ksync,handler);

            }
        });
    }

    private static void upLoadData(final  Activity mActivity,final KSync ksync,final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceId = ((TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                String uploadFileName = deviceId + "-" + CommonUtils.getCurrenTime() + ".db";
                boolean isSuccess = CommonUtils.copyFile(ksync.getKnConfig().getDbDir() + File.separator + ksync.getKnConfig().getDbName(), ksync.getKnConfig().getDbDir() + File.separator + uploadFileName, true);
                if (isSuccess) {
                    File uploadFile = new File(ksync.getKnConfig().getDbDir(), uploadFileName);
                    ksync.uploadFile(uploadFile);
                    FileUtils.deleteFile(uploadFile.getAbsolutePath());
                    handler.sendMessage(handler.obtainMessage(DELETE_FINISHED, "文件上传完成"));
                } else {
                    handler.sendMessage(handler.obtainMessage(DELETE_FINISHED, "文件上传失败"));
                }
            }
        }).start();
    }

    private static List<DeleteModel> tables = new ArrayList<>();


    public static void deleteOtherDepartmentData(final Activity activity, final Handler handler, final String dept_id) {
        if (TextUtils.isEmpty(dept_id) || "-1".equals(dept_id)) {
            ToastUtils.showMessageLong("当前没有班组，请在登陆后的界面点击进入同步界面操作！");
            return;
        }
        SyncMenuUtils.ShowTipsDialog(activity, "是否要删除当前班组以外的数据？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerDialog.showProgress(activity, "删除中！");
                new Thread((new Runnable() {
                    @Override
                    public void run() {
                        if (tables.size() == 0) initDeleteForDeptId();
                        int delRows = 0;
                        for (DeleteModel model : tables) {
                            try {
                                int count = CustomApplication.getInstance().getDbManager().executeUpdateDelete(model.getDeleteSql(dept_id));
                                CLog.w("delete " + model.tbl + " " + count + " rows");
                                delRows += count;
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendMessage(handler.obtainMessage(DELETE_FINISHED, "删除完成，共" + delRows + "条记录！"));
                    }
                })).start();

            }
        });
    }


    private static void initDeleteForDeptId() {
        tables.add(createBDZID("battery"));
        tables.add(createBDZID("copy_item"));
        tables.add(createBDZID("copy_result"));
        tables.add(createBDZID("defect_record"));
        tables.add(createBDZID("device"));
        tables.add(createBDZID("device_csyq_azwz"));
        tables.add(createBDZID("placed"));
        tables.add(createBDZID("report"));
        tables.add(createBDZID("sbjc_battery"));
        tables.add(createBDZID("sbjc_battery_group"));
        tables.add(createBDZID("sbjc_hole"));
        tables.add(createBDZID("sbjc_prevention"));
        tables.add(createBDZID("sbjc_transceiver"));
        tables.add(createBDZID("spacing"));
        tables.add(createBDZID("standard_special"));
        tables.add(createBDZID("standard_step_confirm"));
        tables.add(createBDZID("standard_switchover"));
        tables.add(createBDZID("switch_menu"));
        tables.add(createBDZID("switch_pic"));
        tables.add(createBDZID("tool"));

        tables.add(createDEPT_ID("bdz"));
        tables.add(createDEPT_ID("report_signname"));

        tables.add(createBDZ_ID("report_cdbhcl"));
        tables.add(createBDZ_ID("report_hwcw"));
        tables.add(createBDZ_ID("report_hwcw_frd"));
        tables.add(createBDZ_ID("report_jzlbyqfjkg"));
        tables.add(createBDZ_ID("report_snwsd"));
    }

    /**
     * 清除不是本班组的数据Model
     */
    static class DeleteModel {
        private String tbl;
        private String condition;

        public DeleteModel(String tbl, String condition) {
            this.tbl = tbl;
            this.condition = condition;
        }

        public static DeleteModel createBDZID(String clz) {
            return new DeleteModel(clz, CONDITION_BDZID);
        }

        public static DeleteModel createBDZ_ID(String clz) {
            return new DeleteModel(clz, CONDITION_BDZ_ID);
        }

        public static DeleteModel createDEPT_ID(String clz) {
            return new DeleteModel(clz, CONDITION_DEPT_ID);
        }

        static String CONDITION_BDZID = "bdzid NOT IN(SELECT bdzid FROM bdz WHERE dept_id='%s')";
        static String CONDITION_BDZ_ID = "bdz_id not in(select bdzid from bdz where dept_id='%s')";
        static String CONDITION_DEPT_ID = " dept_id <> '%s'";


        public String getCondition(String dept_id) {
            return String.format(condition, dept_id);
        }

        public String getDeleteSql(String dept_id) {
            return "delete from `" + tbl + "` where " + getCondition(dept_id);
        }

    }

}
