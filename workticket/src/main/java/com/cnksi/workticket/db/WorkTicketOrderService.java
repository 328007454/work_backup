package com.cnksi.workticket.db;

import android.text.TextUtils;

import com.cnksi.workticket.Config;
import com.cnksi.workticket.bean.WorkTicketOrder;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.K  on 2018/5/7.
 * @decrption 预约结果service
 */

public class WorkTicketOrderService {
    private static WorkTicketOrderService orderService;

    public static WorkTicketOrderService getInstance() {
        if (orderService == null) {
            orderService = new WorkTicketOrderService();
        }

        return orderService;
    }

    /**
     * 根据选择的日期查询改日期下该班组的任务
     *
     * @param deptid     部门id
     * @param date       时间
     * @param userDeptId 人员所在的部门
     */

    public List<WorkTicketOrder> getSelectDateOrders(String deptid, String date, String userDeptId) {
        String otherDeptUser = "other_dept_user";
        List<WorkTicketOrder> orders = new ArrayList<>();
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        try {
            if (TextUtils.equals(Config.otherDeptUser, otherDeptUser)) {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DLT, "=", "0").expr("and (dept_id = '"+deptid+"'or  work_unit_id = '"+ userDeptId+"') ").and(WorkTicketOrder.WORK_DATE, ">", startDate).and(WorkTicketOrder.WORK_DATE, "<", endDate).findAll();
            } else {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_DATE, ">", startDate).and(WorkTicketOrder.WORK_DATE, "<", endDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            }
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }

    /**
     * @param deptid      部门id
     * @param currentDate 当前时间
     * @return 预约结果
     */
    public List<WorkTicketOrder> getFutureWorkOverCurrentTime(String deptid, String currentDate, String account) {
        List<WorkTicketOrder> orders = new ArrayList<>();
        try {
            if (Config.otherDeptUser.contains("team_leader")) {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_UNIT_ID,"=",deptid).and(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            } else {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.CREATE_PERSON_ACCOUNT, "=", account).and(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            }
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }

    }

    /**
     * @param deptid      部门id
     * @param currentDate 当前时间
     * @return 获取历史预约时间的预约任务
     */

    public List<WorkTicketOrder> getHistoryWorkOverCurrentTime(String deptid, String currentDate, String account) {

        List<WorkTicketOrder> orders = new ArrayList<>();
        try {
            if (Config.otherDeptUser.contains("team_leader")) {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_UNIT_ID,"=",deptid).and(WorkTicketOrder.WORK_DATE, "<", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            } else {
                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.CREATE_PERSON_ACCOUNT, "=", account).and(WorkTicketOrder.WORK_DATE, "<", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            }
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }

    /**
     * @param account        登陆人员账号
     * @param deptId         部门id
     * @param currentDate    当前时间
     * @param currentMaxDate 当日最大时间
     * @return 返回两者时间之间的预约任务
     */

    public List<WorkTicketOrder> getCurrentDateWork(String account, String deptId, String currentDate, String currentMaxDate) {
        List<WorkTicketOrder> orders = new ArrayList<>();

        try {
//            if (!TextUtils.isEmpty(Config.otherDeptUser) && Config.otherDeptUser.contains("team_leader")) {
//                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptId).and(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.WORK_DATE, "<", currentMaxDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
//            } else {
//                orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.CREATE_PERSON_ACCOUNT, "=", account).and(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.WORK_DATE, "<", currentMaxDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
//            }
            orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.WORK_DATE, "<", currentMaxDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }

    /**
     * @param startDate 开始时间 当日最小时间
     * @param endDate   结束时间 为当前最大的时间
     * @return 一周时间内的预约任务
     */

    public List<WorkTicketOrder> getWeekDateWork(String startDate, String endDate) {
        endDate = endDate.substring(0, 10) + " 23:59:59";
        List<WorkTicketOrder> orders = new ArrayList<>();
        try {
            orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.WORK_DATE, ">", startDate).and(WorkTicketOrder.WORK_DATE, "<", endDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }
}
