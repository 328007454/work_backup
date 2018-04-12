package com.cnksi.bdzinspection.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ListContentDialogAdapter;
import com.cnksi.bdzinspection.adapter.TaskRemindAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.TaskService;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentListBinding;
import com.cnksi.bdzinspection.model.Task;
import com.cnksi.bdzinspection.model.Users;
import com.cnksi.bdzinspection.utils.CommonUtils;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.bdzinspection.utils.Config.TaskStatus;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.SqliteUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务提醒
 *
 * @author terry
 */
public class TaskRemindFragment extends BaseFragment {
    private TaskRemindAdapter mInspectionTaskAdapter = null;
    /**
     * 巡检任务的数据集合
     */
    public List<Task> mDataList = null;
    /**
     * 主菜单界面点击的巡检类型
     */
    private InspectionType currentSelectInspectionType;

    /**
     * 长按已完成任务的dialog
     */
    private Dialog mFinishOptionDialog = null;
    private ListContentDialogAdapter mListContentDialogAdapter = null;
    private Map<String, String> userMap = new HashMap<>();
    public interface OnFragmentEventListener {
        void updateTaskStatus();

        void startTask(Task task);
    }

    private OnFragmentEventListener mOnFragmentEventListener;

    public void setOnFragmentEventListener(OnFragmentEventListener mOnFragmentEventListener) {
        this.mOnFragmentEventListener = mOnFragmentEventListener;
    }

    View view;
    private String inspectionName;
    XsFragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentListBinding.inflate(inflater);
        initUI();
        lazyLoad();

        initOnClick();
        return view;
    }


    private void initUI() {
        getBundleValue();
        inspectionName = bundle.getString(Config.CURRENT_INSPECTION_TYPE_NAME);
        currentSelectInspectionType = InspectionType.get(inspectionName);
        if (mInspectionTaskAdapter == null) {
            mInspectionTaskAdapter = new TaskRemindAdapter(getActivity(),mDataList);
            binding.lvContainer.setAdapter(mInspectionTaskAdapter);
        }
        isPrepared = true;
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                mInspectionTaskAdapter.setList(mDataList);
                mInspectionTaskAdapter.setUserMap(userMap);
                break;
            default:
                break;
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            isFirstLoad = false;
        }
    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }

    /**
     * 查询数据
     */
    public void searchData() {
        mFixedThreadPoolExecutor.execute(() -> {
            try {
                if (currentInspectionType == null) {
                    CToast.showShort(currentActivity, "没有正确的获取巡检类型，请重启程序再尝试！");
                    return;
                }
                if (TextUtils.isEmpty(inspectionName)) {
                    inspectionName = PreferencesUtils.getString(currentActivity, Config.CURRENT_INSPECTION_TYPE_NAME, "full");
                    currentSelectInspectionType = InspectionType.get(inspectionName);
                }
                String deptId = "";
                if (null != currentActivity) {
                    deptId = PreferencesUtils.getString(currentActivity, Config.CURRENT_DEPARTMENT_ID, "");
                }
                String[] accoutArray = currentAcounts.split(",");
                Selector selector = Selector.from(Task.class).expr(" bdzid in (select bdzid  from bdz where dept_id = '" + deptId + "' )");
                selector.expr("and (pms_jh_source ='pms_pc' or " + CommonUtils.buildWhereTaskContainMe(accoutArray) + " or create_account is NULL or create_account = '')");
                // 如果点击待巡视任务时currentInspetionType为null，系统查询所有的任务
                if (Config.UNFINISH_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                    // 未完成
                    selector = selector.and(Task.STATUS, "<>", TaskStatus.done.name()).expr("AND " + Task.SCHEDULE_TIME + ">=" + SqliteUtils.DATE_TIME_START_OF_DAY);
                } else if (Config.FINISHED_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                    // 完成
                    selector = selector.and(Task.STATUS, "=", TaskStatus.done.name());
                } else if (Config.OVER_DUE_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                    // 逾期
                    selector = selector.expr("AND " + Task.SCHEDULE_TIME + "< " + SqliteUtils.DATE_TIME_START_OF_DAY + " AND (status = 'doing' OR status = 'undo') ");
                }
                switch (currentSelectInspectionType) {
                    case professional: // 设备新投
                        selector = selector.and(Task.INSPECTION, "like", "%" + InspectionType.professional.name() + "%");
                        break;
                    case full: // 全面巡检
                        selector = selector.and(Task.INSPECTION, "like", "%" + InspectionType.full.name() + "%");
                        break;
                    case day: // 日常巡检
                        selector = selector.and(Task.INSPECTION, "like", "%" + InspectionType.day.name() + "%");
                        break;
                    case routine: // 例行巡视
                        selector = selector.and(Task.INSPECTION, "like", "%" + InspectionType.routine.name() + "%");
                        break;
                    case special: // 特殊巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + InspectionType.special.name() + "%").and(Task.INSPECTION, "not like", "%" + InspectionType.special_nighttime.name() + "%").and(Task.INSPECTION, "not like", "%" + InspectionType.special_xideng.name() + "%"));
                        break;
                    case special_nighttime: // 夜间巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + InspectionType.special_nighttime.name() + "%"));
                        break;
                    case special_xideng: // 熄灯巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + InspectionType.special_xideng.name() + "%"));
                        break;
                    case switchover: // 定期切换试验 包括蓄电池测试 和红外线测温
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + InspectionType.switchover.name() + "%").or(Task.INSPECTION, "like", "%" + InspectionType.battery.name() + "%"));
                        break;
                    case maintenance: // 定期维护
                        selector = selector.and(Task.INSPECTION, "like", "%" + InspectionType.maintenance.name() + "%");
                        break;
                    case operation:// 运维一体化
                        selector = selector.and(Task.TYPE, "=", InspectionType.operation.name());
                        break;
                    default:
                        break;
                }
                selector = selector.and(Task.DLT, "=", "0").orderBy(Task.SCHEDULE_TIME);

                mDataList = XunshiApplication.getDbUtils().findAll(selector);

                List<Users> users = XunshiApplication.getDbUtils().findAll(Selector.from(Users.class).where(Users.DLT, "=", "0"));
                for (Users user : users) {
                    userMap.put(user.account, user.username);
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 已完成的长按操作Dialog
     */
    XsContentListDialogBinding dialogBinding;
    private void showDefectDialog(Task mTask) {
        if (mListContentDialogAdapter == null) {
            List<String> functionArray = Arrays.asList(getResources().getStringArray(R.array.XS_IsUploadOrDeleteArray));
            mListContentDialogAdapter = new ListContentDialogAdapter(currentActivity, functionArray);
        }
        if (mFinishOptionDialog == null) {
            dialogBinding = XsContentListDialogBinding.inflate(getActivity().getLayoutInflater());
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            mFinishOptionDialog = DialogUtils.createDialog(currentActivity,dialogBinding.getRoot(),dialogWidth,LinearLayout.LayoutParams.WRAP_CONTENT);
        }
       dialogBinding.lvContainer.setAdapter(mListContentDialogAdapter);
        dialogBinding.tvDialogTitle.setText(R.string.xs_task_function_title_str);
        mFinishOptionDialog.show();
        dialogBinding.lvContainer.setOnItemClickListener((parent,view,position,id) ->{
            switch (position) {
                case 0:
                    TaskService.getInstance().isUploadCurrentTask(mTask, true);// 上传
                    break;
                case 1:
                    TaskService.getInstance().isUploadCurrentTask(mTask, false);// 不上传
                    break;
                case 2:
                    TaskService.getInstance().deleteTaskAndReportById(mTask.taskid);// 删除
                    break;
                default:
                    break;
            }
            mInspectionTaskAdapter.notifyDataSetChanged();
            if (mOnFragmentEventListener != null) {
                mOnFragmentEventListener.updateTaskStatus();
                onResume();
            }
            mFinishOptionDialog.dismiss();
        });
    }


    private void initOnClick() {
        binding.lvContainer.setOnItemClickListener((parent,view,position,id) -> {
            Task mTask = (Task) parent.getItemAtPosition(position);
            if (null != mOnFragmentEventListener)
                mOnFragmentEventListener.startTask(mTask);
            else {
                CToast.showShort("数据不完整，请返回重新进入该界面");
            }
        });

        binding.lvContainer.setOnItemLongClickListener((parent,view,position,id) -> {
            Task mTask = (Task) parent.getItemAtPosition(position);
            if (!mTask.isMyCreate()) {
                CToast.showShort(currentActivity, "您不是任务创建者，无法删除");
                return true;
            }
            if (mTask.isPMS()) {
                CToast.showShort(currentActivity, "与PMS关联的计划不能删除！");
                return true;
            }
            if (Config.TaskStatus.done.name().equalsIgnoreCase(mTask.status)) {
                // 已完成的长按弹出dialog选择是上传或是删除
                showDefectDialog(mTask);
            } else {
                if (TaskService.getInstance().deleteTaskAndReportById(mTask.taskid)) {
                    mDataList.remove(mTask);
                    mHandler.sendEmptyMessage(LOAD_DATA);
                    if (mOnFragmentEventListener != null) {
                        mOnFragmentEventListener.updateTaskStatus();
                    }
                }
            }
            return true;
        });
    }
}
