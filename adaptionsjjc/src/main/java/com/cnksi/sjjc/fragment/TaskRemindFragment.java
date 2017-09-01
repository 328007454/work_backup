package com.cnksi.sjjc.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.SqliteUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.AnimalReportActivity;
import com.cnksi.sjjc.activity.CopyAllValueActivity3;
import com.cnksi.sjjc.activity.CopyBaseDataActivity;
import com.cnksi.sjjc.activity.CopyValueReportActivity;
import com.cnksi.sjjc.activity.DifferentialMotionRecordActivity2;
import com.cnksi.sjjc.activity.GetSendLetterActivity;
import com.cnksi.sjjc.activity.GetSendLetterReportActivity;
import com.cnksi.sjjc.activity.HWCWMainActivity;
import com.cnksi.sjjc.activity.HongWaiCeWenReportActivity;
import com.cnksi.sjjc.activity.JZLFenJieKaiGuanReportActivity;
import com.cnksi.sjjc.activity.NewTransformRecordActivity;
import com.cnksi.sjjc.activity.PreventAnimalActivity;
import com.cnksi.sjjc.activity.TaskRemindActivity;
import com.cnksi.sjjc.activity.batteryactivity.BatteryTestActivity;
import com.cnksi.sjjc.activity.batteryactivity.BatteryTestReportActivity;
import com.cnksi.sjjc.activity.indoortempretureactivity.IndoorHumitureReportActivity;
import com.cnksi.sjjc.activity.indoortempretureactivity.NewIndoorHumitureRecordActivity;
import com.cnksi.sjjc.adapter.ListContentDialogAdapter;
import com.cnksi.sjjc.adapter.TaskRemindAdapter;
import com.cnksi.sjjc.bean.Bdz;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.enmu.TaskStatus;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.inter.OnFragmentEventListener;
import com.cnksi.sjjc.service.BdzService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.DialogUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 巡检任务提醒
 *
 * @author terry
 */
public class TaskRemindFragment extends BaseCoreFragment {
    public static final int LOAD_DATA = 0x01;

    @ViewInject(R.id.lv_container)
    private ListView mLvContainer;

    /**
     * 巡检任务的数据集合
     */
    public List<Task> mDataList = null;
    /**
     * 主菜单界面点击的巡检类型
     */
    private InspectionType mInspectionType;
    private ViewFunctionHolder mOPtionsHolder = null;
    private TaskRemindAdapter mInspectionTaskAdapter = null;

    /**
     * 长按已完成任务的dialog
     */
    private Dialog mFinishOptionDialog = null;
    private ListContentDialogAdapter mListContentDialogAdapter = null;
//	private ViewFunctionHolder mOPtionsHolder = null;

    private String currentFunctionModel;


    private OnFragmentEventListener mOnFragmentEventListener;

    public void setOnFragmentEventListener(OnFragmentEventListener mOnFragmentEventListener) {
        this.mOnFragmentEventListener = mOnFragmentEventListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        x.view().inject(view);
        initData();
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (isVisible && isFirstLoad) {
            // 填充各控件的数据
            query();
            isFirstLoad = false;
        }
    }

    @Override
    protected void initUI() {

    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }


    @Override
    protected void initData() {
        query();
    }

    public void query() {
        mExcutorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    currentFunctionModel = getArguments().getString(Config.CURRENT_FUNCTION_MODEL);
                    mInspectionType = InspectionType.get(getArguments().getString(Config.CURRENT_INSPECTION_TYPE_NAME));
                    // 如果点击待巡视任务时currentInspetionType为null，系统查询所有的任务
                    String deparmentId = "";
                    if (null != mCurrentActivity) {
                        deparmentId=PreferencesUtils.getString(mCurrentActivity, Config.CURRENT_DEPARTMENT_ID, "");
                    }
                    WhereBuilder whereBuilder = WhereBuilder.b().expr("1=1").expr("and bdzid in (select bdzid  from bdz where dept_id = '" + deparmentId + "' ) ");
                    if (Config.UNFINISH_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                        // 未完成
                        whereBuilder = whereBuilder.and(Task.STATUS, "<>", Task.TaskStatus.done.name())
                                .expr("AND " + Task.SCHEDULE_TIME + ">=" + SqliteUtils.DATE_TIME_START_OF_DAY);
                    } else if (Config.FINISHED_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                        // 完成
                        whereBuilder = whereBuilder.and(Task.STATUS, "=", Task.TaskStatus.done.name());
                    } else if (Config.OVER_DUE_MODEL.equalsIgnoreCase(currentFunctionModel)) {
                        // 逾期
                        whereBuilder = whereBuilder.expr("AND " + Task.SCHEDULE_TIME + "< " + SqliteUtils.DATE_TIME_START_OF_DAY
                                + " AND (status = 'doing' OR status = 'undo') ");
                    }
                    if (null != mInspectionType) {
                        whereBuilder.expr("AND " + Task.INSPECTION + " like '%" + mInspectionType.name() + "%'");
                    }
                    mDataList = TaskService.getInstance().selector().and(whereBuilder).orderBy(Task.SCHEDULE_TIME).findAll();
                    // 遍历当前已完成的任务，查询是否有新增缺陷
                    if (mDataList != null) {
                        DbModel model = ReportService.getInstance().findDbModelFirst(new SqlInfo("SELECT group_concat(taskid) as rs FROM report where is_upload='N'"));
                        String rs = model.getString("rs");
                        if (!TextUtils.isEmpty(rs)) {
                            String taskIds[] = rs.split(CoreConfig.COMMA_SEPARATOR);
                            List<String> arr = Arrays.asList(taskIds);
                            for (int i = 0, count = mDataList.size(); i < count; i++) {
                                mDataList.get(i).isUpload = arr.contains(mDataList.get(i).taskid) ? "N" : "Y";
                            }
                        }
                    }
                    mHandler.sendEmptyMessage(LOAD_DATA);

                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:

                if (mDataList == null) {
                    mDataList = new ArrayList<Task>();
                }
                if (mInspectionTaskAdapter == null) {
                    // mCurrentActivity 偶尔为空 引发NullPointException
                    mInspectionTaskAdapter = new TaskRemindAdapter(getActivity(), mDataList, R.layout.inspection_task_item_remind);
                    mInspectionTaskAdapter.setItemClickListener(new ItemClickListener<Task>() {
                        @Override
                        public void itemClick(View v, Task task, int position) {
                            generateReport(task);
                        }

                        @Override
                        public void itemLongClick(View v, Task task, int position) {
                            deleteReport(task);
                        }
                    });
                    mLvContainer.setAdapter(mInspectionTaskAdapter);
                } else {
                    mInspectionTaskAdapter.setList(mDataList);
                }
                break;
        }
    }

    private void deleteReport(Task task) {

        if (Task.TaskStatus.done.name().equalsIgnoreCase(task.status)) {
            // 已完成的长按弹出dialog选择是上传或是删除
            showDefectDialog(task);
        } else {
            if (TaskService.getInstance().deleteTaskAndReportById(task.taskid)) {
                mDataList.remove(task);
                mHandler.sendEmptyMessage(LOAD_DATA);
                if (mOnFragmentEventListener != null) {
                    mOnFragmentEventListener.updateTaskStatused();
                }
            }
        }
    }

    /**
     * 已完成的长按操作Dialog
     */
    private void showDefectDialog(Task mTask) {
        if (mListContentDialogAdapter == null) {
            List<String> functionArray = Arrays.asList(getResources().getStringArray(R.array.IsUploadOrDeleteArray));
            mListContentDialogAdapter = new ListContentDialogAdapter(mCurrentActivity, functionArray, R.layout.dialog_content_child_item);
        }
        if (mFinishOptionDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 9 / 10;
            mFinishOptionDialog = DialogUtils.createDialog(mCurrentActivity, mLvContainer,
                    R.layout.content_list_dialog,
                    mOPtionsHolder == null ? mOPtionsHolder = new ViewFunctionHolder() : mOPtionsHolder, dialogWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
        }
        mOPtionsHolder.mLvContainer.setAdapter(mListContentDialogAdapter);
        mOPtionsHolder.mTvTitle.setText(R.string.task_function_title_str);
        mOPtionsHolder.setCurrentItmeData(mTask);
        mFinishOptionDialog.show();
    }

    // 已完成的dialog的viewHolder
    class ViewFunctionHolder {
        private Task currentTask;

        private void setCurrentItmeData(Task currentTask) {
            this.currentTask = currentTask;
        }

        @ViewInject(R.id.tv_dialog_title)
        TextView mTvTitle;

        @ViewInject(R.id.lv_container)
        ListView mLvContainer;

        @Event(value = {R.id.lv_container}, type = AdapterView.OnItemClickListener.class)
        private void OnItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    TaskService.getInstance().isUploadCurrentTask(currentTask, true);// 上传
                    break;
                case 1:
                    TaskService.getInstance().isUploadCurrentTask(currentTask, false);// 不上传
                    break;
                case 2:
                    TaskService.getInstance().deleteTaskAndReportById(currentTask.taskid);// 删除
                    break;
                default:
                    break;
            }
            if (mOnFragmentEventListener != null) {
                mOnFragmentEventListener.updateTaskStatused();
            }
            mFinishOptionDialog.dismiss();
        }
    }

    /**
     * 生成报告<br/>
     * 有报告使用报告,无报告生成报告
     *
     * @param task
     */
    private void generateReport(Task task) {
        try {

            Report report = ReportService.getInstance().getReportByTask(task.taskid);

            Bdz bdz = BdzService.getInstance().findById(task.bdzid);
            if (!FileUtils.isFileExists(Config.BDZ_INSPECTION_FOLDER + bdz.folder))
                FileUtils.makeDirectory(Config.BDZ_INSPECTION_FOLDER + bdz.folder);
            if (null == report) {
                String loginUser = PreferencesUtils.getString(getContext(), Config.CURRENT_LOGIN_USER, "");
                report = new Report(task.taskid, task.bdzid, task.bdzname, task.inspection, loginUser);
                ReportService.getInstance().saveOrUpdate(report);
            }
            PreferencesUtils.put(getActivity(), Config.CURRENT_TASK_ID, task.taskid);
            PreferencesUtils.put(getActivity(), Config.CURRENT_REPORT_ID, report.reportid);
            PreferencesUtils.put(getActivity(), Config.PICTURE_PREFIX, bdz.folder + "/");
            PreferencesUtils.put(getActivity(), Config.CURRENT_BDZ_ID, task.bdzid);
            PreferencesUtils.put(getActivity(), Config.CURRENT_BDZ_NAME, task.bdzname);
            PreferencesUtils.put(getActivity(), Config.CURRENT_INSPECTION_TYPE, task.inspection);
            PreferencesUtils.put(getActivity(), Config.CURRENT_INSPECTION_NAME, task.inspection_name);

            Intent intent = new Intent();
            if (task.status.equals(TaskStatus.undo.name())) {
                switch (InspectionType.get(task.inspection)) {
                    //红外测温
                    case SBJC_01:
                        intent.setClass(getContext(), HWCWMainActivity.class);
                        break;
                    //保护屏红外成像
                    case SBJC_02:
                        intent.setClass(getContext(), HWCWMainActivity.class);
                        break;
                    //室内温湿度记录
                    case SBJC_03:
//                        intent.setClass(getContext(), IndoorHumitureRecordActivity.class);
                        intent.setClass(getContext(), NewIndoorHumitureRecordActivity.class);
                        break;
                    //差动保护
                    case SBJC_04:
                        intent.setClass(getContext(), DifferentialMotionRecordActivity2.class);
                        break;
                    //交直流分接开关
                    case SBJC_05:
                        intent.setClass(getContext(), NewTransformRecordActivity.class);
                        break;
                    case SBJC_06_gas:
                    case SBJC_06_sf6:
                    case SBJC_06_water:

                        intent.setClass(getContext(), CopyBaseDataActivity.class);
                        break;
                    case SBJC_07:
                        intent.setClass(getContext(), CopyAllValueActivity3.class);
                        break;
                    case SBJC_08:
                        intent.setClass(getContext(), CopyBaseDataActivity.class);
                        break;
                    //防小动物措施检测
                    case SBJC_09:
                        intent.setClass(getContext(), PreventAnimalActivity.class);
                        break;
                    //蓄电池检测
                    case SBJC_10:
                        intent.setClass(getContext(), BatteryTestActivity.class);
                        break;
                    //蓄电池内阻检测
                    case SBJC_11:
                        intent.setClass(getContext(), BatteryTestActivity.class);
                        break;
                    //收发信机测试
                    case SBJC_12:
                        intent.setClass(getContext(), GetSendLetterActivity.class);
                        break;
                    default:
                        throw new RuntimeException("异常的数据检测类型");
                }
            } else {
                switch (InspectionType.get(task.inspection)) {
                    //红外测温
                    case SBJC_01:
                        intent.setClass(getContext(), HongWaiCeWenReportActivity.class);
                        break;
                    //保护屏红外成像
                    case SBJC_02:
                        intent.setClass(getContext(), HongWaiCeWenReportActivity.class);
                        break;
                    //室内温湿度记录
                    case SBJC_03:
//                        intent.setClass(getContext(), JZLFenJieKaiGuanReportActivity.class);
                        intent.setClass(getContext(), IndoorHumitureReportActivity.class);
                        break;
                    //差动保护
                    case SBJC_04:
                        intent.setClass(getContext(), JZLFenJieKaiGuanReportActivity.class);
                        break;
                    //交直流分接开关
                    case SBJC_05:
                        intent.setClass(getContext(), JZLFenJieKaiGuanReportActivity.class);
                        break;
                    //防小动物措施检查
                    case SBJC_09:
                        intent.setClass(getContext(), AnimalReportActivity.class);
                        break;
                    //蓄电池检测
                    case SBJC_10:
                        intent.setClass(getContext(), BatteryTestReportActivity.class);
                        break;
                    //蓄电池内阻检测
                    case SBJC_11:
                        intent.setClass(getContext(), BatteryTestReportActivity.class);
                        break;
                    case SBJC_12:
                        //TODO 跳转收发信机报告
                        intent.setClass(getContext(), GetSendLetterReportActivity.class);
                        break;
                    case SBJC_06_gas:
                    case SBJC_06_sf6:
                    case SBJC_06_water:
                    case SBJC_07:
                    case SBJC_08:
                        intent.setClass(getContext(), CopyValueReportActivity.class);
                        break;
                    default:
                        throw new RuntimeException("异常的数据检测类型");
                }
            }
            mCurrentActivity.startActivityForResult(intent, TaskRemindActivity.FINISH_TASK);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }
}
