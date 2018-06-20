package com.cnksi.common.model;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.cnksi.common.Config;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.utils.MyUUID;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Table(name = "task")
public class Task extends BaseModel {

    // 任务编号
    public static final String TASKID = "taskid";
    // 巡检类型
    public static final String INSPECTION = "inspection";
    // 变电站ID
    public static final String BDZID = "bdzid";
    // 变电站名称
    public static final String BDZNAME = "bdzname";
    // 计划巡视日期
    public static final String SCHEDULE_TIME = "schedule_time";
    // 计划完成日期
    public static final String SCHEDULE_ENDTIME = "schedule_endtime";
    // 计划状态 undo=未开始，doing=巡视中，done=已完成
    public static final String STATUS = "STATUS";
    // 巡检类型名称
    public static final String INSPECTION_NAME = "inspection_name";
    // 是否是运维一体化
    public static final String TYPE = "type";
    // PMS 计划ID
    public static final String PMS_JHID = "pms_jhid";
    // PMS 计划来源
    public static final String PMS_JH_SOURCE = "pms_jh_source";
    /**
     * 选择设备
     */
    public static final String SELECTED_DEVICEID = "selected_deviceid";
    /**
     * 任务创建者
     */
    public static final String CRAETE_ACCOUNT = "create_account";
    /**
     * 任务组成员
     */
    public static final String MEMBERS_ACCOUNT = "members_account";
    /**
     * 是否作废；0：表示正常 ，1：表示作废
     */
    public static final String IS_INVALID = "is_invalid";

    /**
     * 创建者姓名
     */
    public static final String CREATEPERSONNAME = "create_person_name";

    /**
     * 创建者姓名
     */
    public static final String DEPT_ID = "dept_id";

    @Column(name = TASKID, isId = true)
    public String taskid;
    @Column(name = INSPECTION)
    public String inspection;
    @Column(name = BDZID)
    public String bdzid;
    @Column(name = BDZNAME)
    public String bdzname;
    @Column(name = SCHEDULE_TIME)
    public String schedule_time;
    @Column(name = SCHEDULE_ENDTIME)
    public String schedule_endtime;
    @Column(name = STATUS)
    public String status;
    @Column(name = INSPECTION_NAME)
    public String inspection_name;
    @Column(name = TYPE)
    public String type;
    @Column(name = PMS_JHID)
    public String pmsJhid;
    @Column(name = PMS_JH_SOURCE)
    public String pmsJhSource;
    @Column(name = SELECTED_DEVICEID)
    public String selected_deviceid;
    @Column(name = CRAETE_ACCOUNT)
    public String createAccount;
    @Column(name = CREATEPERSONNAME)
    public String createPersonName;
    @Column(name = MEMBERS_ACCOUNT)
    public String membersAccount;
    @Column(name = IS_INVALID)
    public int isInvalid;

    @Column(name = DEPT_ID)
    public String deptId;


    public transient boolean hasNewDefect = false;


    public transient String isUpload = "Y";
    public String remark;

    public Task() {
    }

    public Task(String taskid) {
        this.taskid = taskid;
        this.status = TaskStatus.done.name();
    }

    public Task(String taskid, String bdzid, String bdzname, String inspection, String inspection_name, String schedule_time, String status) {
        this.taskid = taskid;
        this.inspection = inspection;
        this.bdzid = bdzid;
        this.bdzname = bdzname;
        this.schedule_time = schedule_time;
        this.status = status;
        this.inspection_name = inspection_name;
    }

    public Task(String taskid, String bdzid, String bdzname, String inspection, String inspection_name,
                String schedule_time, String status, String creatUser, String membersUser) {
        this.taskid = taskid;
        this.inspection = inspection;
        this.bdzid = bdzid;
        this.bdzname = bdzname;
        this.schedule_time = schedule_time;
        this.status = status;
        this.inspection_name = inspection_name;
        this.createAccount = creatUser;
        this.membersAccount = membersUser;
    }

    public Task(String taskid, String bdzid, String bdzname, String inspection, String inspection_name,
                String schedule_time, String status, String createAccount) {
        this.taskid = taskid;
        this.inspection = inspection;
        this.bdzid = bdzid;
        this.bdzname = bdzname;
        this.schedule_time = schedule_time;
        this.status = status;
        this.inspection_name = inspection_name;
        this.createAccount = createAccount;
    }

    public Task(Bdz bdz, String inspectionType, String currentAcounts, String inspectionValue, List<DbModel> allSelectUser, String selectDeviceIds, String type) {
        this.taskid = MyUUID.id(4);
        this.inspection = inspectionType;
        this.bdzid = bdz.bdzid;
        this.bdzname = bdz.name;
        this.deptId = bdz.deptId;
        this.schedule_time = DateUtils.getCurrentLongTime();
        this.inspection_name = inspectionValue;
        String[] accounts = currentAcounts.split(",");
        for (int accountSize = 0; accountSize < accounts.length; accountSize++) {

        }
        StringBuilder createUsers = new StringBuilder();
        StringBuilder memberUsers = new StringBuilder();
        StringBuilder createUserNames = new StringBuilder();
        for (int userSize = 0; userSize < allSelectUser.size(); userSize++) {
            DbModel model = allSelectUser.get(userSize);
            if (userSize <= accounts.length - 1) {
                if (userSize == 0) {
                    createUsers.append(model.getString(Users.ACCOUNT));
                    createUserNames.append(model.getString(Users.USERNAME));
                } else {
                    createUsers.append(",").append(model.getString(Users.ACCOUNT));
                    createUserNames.append(",").append(model.getString(Users.USERNAME));
                }
            } else {
                if (userSize == accounts.length) {
                    memberUsers.append(model.getString(Users.ACCOUNT));
                } else {
                    memberUsers.append(",").append(model.getString(Users.ACCOUNT));
                }
            }
        }
        this.createAccount = createUsers.toString();
        this.membersAccount = memberUsers.toString();
        this.createPersonName = createUserNames.toString();
        this.dlt = "0";
        this.status = TaskStatus.undo.name();
        this.selected_deviceid = selectDeviceIds;
        this.type = type;
    }

    /**
     * 该计划是否完成，true =完成
     *
     * @return
     */
    public boolean isFinish() {
        return status != null && status.equals("done");
    }

    /**
     * 获取计划时间
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public Date getScheduleTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(schedule_time);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    public boolean isPMS() {
        return !TextUtils.isEmpty(pmsJhid);
    }

    public String taskOrgin(Task task) {
        if (!TextUtils.isEmpty(task.pmsJhSource)) {
            if (task.pmsJhSource.equalsIgnoreCase("pms_pc")) {
                return "[PMS]";
            } else if (task.pmsJhSource.equalsIgnoreCase("pms_app")) {
                return "[离线作业]";
            } else {
                return "";
            }
        } else if (TextUtils.isEmpty(task.pmsJhSource) && !TextUtils.isEmpty(task.pmsJhid)) {
            return "[离线作业]";
        } else {
            return "";
        }

    }

    public boolean isFromPc() {
        return "pms_pc".equals(pmsJhSource);
    }

    public void setCurrentTaskPeople(String creatUser, String memberUser) {
        this.createAccount = creatUser;
        this.membersAccount = memberUser;
    }

    public boolean isMyCreate() {
        String[] accounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "").split(",");
        if (TextUtils.isEmpty(createAccount)) {
            return false;
        }
        List<String> createAccounts = Arrays.asList(createAccount.split(","));
        for (String s : accounts) {
            if (createAccounts.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMember() {
        if (isMyCreate()) {
            return false;
        }
        String[] accounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "").split(",");
        if (TextUtils.isEmpty(membersAccount)) {
            return false;
        }
        List<String> memberAccounts = Arrays.asList(membersAccount.split(","));
        for (String s : accounts) {
            if (memberAccounts.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGroupTask() {
        return !StringUtilsExt.isEmptys(membersAccount, createAccount);
    }


}
