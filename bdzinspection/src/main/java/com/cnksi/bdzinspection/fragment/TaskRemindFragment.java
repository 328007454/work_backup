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
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentListBinding;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.daoservice.UserService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.Users;
import com.cnksi.common.utils.CommonUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.SqliteUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.common.enmu.InspectionType.day;
import static com.cnksi.common.enmu.InspectionType.full;
import static com.cnksi.common.enmu.InspectionType.maintenance;
import static com.cnksi.common.enmu.InspectionType.operation;
import static com.cnksi.common.enmu.InspectionType.professional;
import static com.cnksi.common.enmu.InspectionType.routine;
import static com.cnksi.common.enmu.InspectionType.special;
import static com.cnksi.common.enmu.InspectionType.special_nighttime;
import static com.cnksi.common.enmu.InspectionType.special_xideng;
import static com.cnksi.common.enmu.InspectionType.switchover;

/**
 * Created by Mr.K on 2018/4/12.
 */

public class TaskRemindFragment extends BaseFragment {
    XsFragmentListBinding binding;

    private TaskRemindAdapter mInspectionTaskAdapter;
    private String inspectionName;
    public List<Task> mDataList;
    private Map<String, String> userMap = new HashMap<>();
    /**
     * 主菜单界面点击的巡检类型
     */
    private InspectionType currentSelectInspectionType;
    /**
     * 长按已完成任务的dialog
     */
    private Dialog mFinishOptionDialog = null;
    private ListContentDialogAdapter mListContentDialogAdapter = null;
    private boolean updateTask;

    public void updateTask(boolean updateTask) {
        this.updateTask = updateTask;
    }

    public interface OnFragmentEventListener {
        void updateTaskStatus();

        void startTask(Task task);
    }

    private TaskRemindFragment.OnFragmentEventListener mOnFragmentEventListener;

    public void setOnFragmentEventListener(TaskRemindFragment.OnFragmentEventListener mOnFragmentEventListener) {
        this.mOnFragmentEventListener = mOnFragmentEventListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentListBinding.inflate(inflater);
        initialUI();
        return binding.getRoot();
    }

    private void initialUI() {
        getBundleValue();
        inspectionName = bundle.getString(Config.CURRENT_INSPECTION_TYPE_NAME);
        currentSelectInspectionType = InspectionType.get(inspectionName);
        if (mInspectionTaskAdapter == null) {
            mInspectionTaskAdapter = new TaskRemindAdapter(getActivity(), mDataList);
            binding.lvContainer.setAdapter(mInspectionTaskAdapter);
        }
        searchData();
        initOnClick();
    }


    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {

//         searchData();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (updateTask) {
            searchData();
        }
    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }

    public void searchData() {

        ExecutorManager.executeTask(() -> {
            try {
                if (currentInspectionType == null) {
                    ToastUtils.showMessage( "没有正确的获取巡检类型，请重启程序再尝试！");
                    return;
                }
                if (TextUtils.isEmpty(inspectionName)) {
                    inspectionName = PreferencesUtils.get(Config.CURRENT_INSPECTION_TYPE_NAME, "full");
                    currentSelectInspectionType = InspectionType.get(inspectionName);
                }
                String deptId = "";
                if (null != currentActivity) {
                    deptId = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, "");
                }
                String[] accoutArray = currentAcounts.split(",");
                Selector selector =TaskService.getInstance().selector().expr(" and  bdzid in (select bdzid  from bdz where dept_id = '" + deptId + "' )");
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
                        selector = selector.and(Task.INSPECTION, "like", "%" + professional.name() + "%");
                        break;
                    case full: // 全面巡检
                        selector = selector.and(Task.INSPECTION, "like", "%" + full.name() + "%");
                        break;
                    case day: // 日常巡检
                        selector = selector.and(Task.INSPECTION, "like", "%" + day.name() + "%");
                        break;
                    case routine: // 例行巡视
                        selector = selector.and(Task.INSPECTION, "like", "%" + routine.name() + "%");
                        break;
                    case special: // 特殊巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + special.name() + "%").and(Task.INSPECTION, "not like", "%" + special_nighttime.name() + "%").and(Task.INSPECTION, "not like", "%" + special_xideng.name() + "%"));
                        break;
                    case special_nighttime: // 夜间巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + special_nighttime.name() + "%"));
                        break;
                    case special_xideng: // 熄灯巡检
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + special_xideng.name() + "%"));
                        break;
                    case switchover: // 定期切换试验 包括蓄电池测试 和红外线测温
                        selector = selector.and(WhereBuilder.b(Task.INSPECTION, "like", "%" + switchover.name() + "%").or(Task.INSPECTION, "like", "%" + InspectionType.battery.name() + "%"));
                        break;
                    case maintenance: // 定期维护
                        selector = selector.and(Task.INSPECTION, "like", "%" + maintenance.name() + "%");
                        break;
                    case operation:// 运维一体化
                        selector = selector.and(Task.TYPE, "=", operation.name());
                        break;
                    default:
                        break;
                }
                selector = selector.orderBy(Task.SCHEDULE_TIME);

                mDataList =selector.findAll();

                List<Users> users = UserService.getInstance().findAll();
                for (Users user : users) {
                    userMap.put(user.account, user.username);
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }

        });
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
            mFinishOptionDialog = DialogUtils.createDialog(currentActivity, dialogBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        dialogBinding.lvContainer.setAdapter(mListContentDialogAdapter);
        dialogBinding.tvDialogTitle.setText(R.string.xs_task_function_title_str);
        mFinishOptionDialog.show();
        dialogBinding.lvContainer.setOnItemClickListener((parent, view, position, id) -> {
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
        binding.lvContainer.setOnItemClickListener((parent, view, position, id) -> {
            Task mTask = (Task) parent.getItemAtPosition(position);
            if (null != mOnFragmentEventListener) {
                mOnFragmentEventListener.startTask(mTask);
            } else {
                ToastUtils.showMessage("数据不完整，请返回重新进入该界面");
            }
        });

        binding.lvContainer.setOnItemLongClickListener((parent, view, position, id) -> {
            Task mTask = (Task) parent.getItemAtPosition(position);
            if (!mTask.isMyCreate()) {
                ToastUtils.showMessage( "您不是任务创建者，无法删除");
                return true;
            }
            if (mTask.isPMS()) {
                ToastUtils.showMessage( "与PMS关联的计划不能删除！");
                return true;
            }
            if (TaskStatus.done.name().equalsIgnoreCase(mTask.status)) {
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
