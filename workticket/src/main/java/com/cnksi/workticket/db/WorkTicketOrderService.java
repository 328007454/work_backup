package com.cnksi.workticket.db;

import com.cnksi.workticket.bean.WorkTicketOrder;

import org.apache.commons.net.telnet.WindowSizeOptionHandler;
import org.xutils.ex.DbException;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.K on 2018/5/7.
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
     * @param deptid
     * @param date
     */

    public List<WorkTicketOrder> getSelectDateOrders(String deptid, String date) {
        List<WorkTicketOrder> orders = new ArrayList<>();

        try {
            orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_DATE, ">", date).and(WorkTicketOrder.DLT, "=", "0").findAll();
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }

    public List<WorkTicketOrder> getFutureWorkOverCurrentTime(String deptid, String currentDate) {
        List<WorkTicketOrder> orders = new ArrayList<>();
        try {
            orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_DATE, ">", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }

    }

    public List<WorkTicketOrder> getHistoryWorkOverCurrentTime(String deptid, String currentDate) {

        List<WorkTicketOrder> orders = new ArrayList<>();
        try {
            orders = WorkTicketDbManager.getInstance().getTicketManager().selector(WorkTicketOrder.class).where(WorkTicketOrder.DEPT_ID, "=", deptid).and(WorkTicketOrder.WORK_DATE, "<", currentDate).and(WorkTicketOrder.DLT, "=", "0").findAll();
            return orders;
        } catch (DbException e) {
            e.printStackTrace();
            return orders;
        }
    }
}
