package com.cnksi.workticket;

import com.cnksi.workticket.bean.Department;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.databinding.ActivityTicketDateBinding;
import com.cnksi.workticket.enum_ticket.TicketTimeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.K on 2018/6/7.
 * @decrption 按照预约规则计算当前需要预约的工作适合符合该规则，如果符合，就可以预约，不符合就不能预约
 */

public class CalculateDateWork {
    /**
     * @decrption 以下是预约工作票的相关规则：
     * <p>
     * 1.工作类型分为A类和B类 其中约束规定 A = 2B,表示A类工作量是B类的两倍
     * 2.一个变电运维班组客户自定义分为几组，数据库具体体现在departmaent表中的——count_canwork_group字段，默认是2组
     * 3.预约的工作条件：同一个变电站同一天同一时间段A和B类的预约工作总和不能超过 A/2B工作总量，我们把B类工作看作一个单位的工作量，A类就是2个单位工作量
     * 4.外来班组在同一时间段只能预约一个变电站
     **/

    /**
     * 整个班组每个时间段的工作总量：bTo11A表示10：00到11：00A类工作总量，其他以此类推
     */
    private int to11A = 0, to12A = 0, to15A = 0, to16A = 0, to17A = 0;
    private int to11B = 0, to12B = 0, to15B = 0, to16B = 0, to17B = 0;

    private String a = "A";
    private String b = "B";

    /**
     * 一个变电站在同一时间类最大能够承受1个A或者2B的工作，B类单位为1，A就为2；
     */
    private int bdzTotal = 2;

    /**
     * 工作单位
     */
    private int workUnitA = 2;
    private int workUnitB = 1;

    private int workTotal;

    /**
     * 当前变电站A类和B类工作在每个时间段的工作总量：bTo11A表示10：00到11：00A类工作总量，其他以此类推
     */
    private int bTo11A = 0, bTo12A = 0, bTo15A = 0, bTo16A = 0, bTo17A = 0;
    private int bTo11B = 0, bTo12B = 0, bTo15B = 0, bTo16B = 0, bTo17B = 0;

    /**
     * 征对外来班组需要判断，当前是否在该时间段已经预约过工作，如果预约过为true,否则false;
     */
    private boolean select10To11, select11To12, select14To15, select15To16, select16To17;

    private String otherDeptUser = "other_dept_user";

    private static CalculateDateWork instance;

    public static CalculateDateWork getInstance() {
        if (instance == null) {
            instance = new CalculateDateWork();
        }
        return instance;
    }


    public void setWorkTypeCaculate(HashMap<String, Integer> workMap, WorkTicketOrder order, HashMap<String, Integer> bdzWorkTypeMap, ActivityTicketDateBinding dateBinding, String bdzId) {
        select10To11 = false;
        select11To12 = false;
        select14To15 = false;
        select15To16 = false;
        select16To17 = false;
        if (TicketTimeEnum.region_10to11.name().equalsIgnoreCase(order.workKey)) {
            if (Config.otherDeptUser.equalsIgnoreCase(otherDeptUser) && Config.deptID.equalsIgnoreCase(order.workUnitId)) {
                select10To11 = true;
            }
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }
        }

        if (TicketTimeEnum.region_11to12.name().equalsIgnoreCase(order.workKey)) {
            if (Config.otherDeptUser.equalsIgnoreCase(otherDeptUser) && Config.deptID.equalsIgnoreCase(order.workUnitId)) {
                select11To12 = true;
            }
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }

        }
        if (TicketTimeEnum.region_14to15.name().equalsIgnoreCase(order.workKey)) {
            if (Config.otherDeptUser.equalsIgnoreCase(otherDeptUser) && Config.deptID.equalsIgnoreCase(order.workUnitId)) {
                select14To15 = true;
            }
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }
        }

        if (TicketTimeEnum.region_15to16.name().equalsIgnoreCase(order.workKey)) {
            if (Config.otherDeptUser.equalsIgnoreCase(otherDeptUser) && Config.deptID.equalsIgnoreCase(order.workUnitId)) {
                select15To16 = true;
            }
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }

            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }

        }

        if (TicketTimeEnum.region_16to17.name().equalsIgnoreCase(order.workKey)) {
            if (Config.otherDeptUser.equalsIgnoreCase(otherDeptUser) && Config.deptID.equalsIgnoreCase(order.workUnitId)) {
                select16To17 = true;
            }
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }

            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }
        }
    }


    /**
     * @param deptWorkA       预约变电站所属班组A类工作集合
     * @param deptWorkB       预约变电站所属班组A类工作集合
     * @param currentBdzWorkA 当前预约变电站A类工作集合
     * @param currentBdzWorkB 当前预约变电站B类工作集合
     * @param department      预约变电站所属班组
     * @param selectType      当前预约的工作类型：A类/B类
     * @decrption int workTotal = department.workTotal * 2,表示当前班组工作总量
     * @decrption workTotal - 1 >= (to11B + to11A * 2)：workTotal-1无论如何都是奇数，(to11B + to11A * 2)表示数据库里查询出该时间段已经预约的工作量
     * 例如：如果工作总量为6，在10：00--11：00 ，他可以预约3个A ，6个B，如果已经预约了2个A，1个B的话，他只能再预约1个B，如果选择A类工作的话，10-11时间区间就无法选中
     */
    public void refreshSelectTimeStatus(Map<String, Integer> deptWorkA, Map<String, Integer> deptWorkB, Map<String, Integer> currentBdzWorkA, Map<String, Integer> currentBdzWorkB,
                                        Department department, String selectType, ActivityTicketDateBinding dateBinding) {

        if (deptWorkA.containsKey(TicketTimeEnum.region_10to11.name())) {
            to11A = deptWorkA.get(TicketTimeEnum.region_10to11.name());
        }
        if (deptWorkA.containsKey(TicketTimeEnum.region_11to12.name())) {
            to12A = deptWorkA.get(TicketTimeEnum.region_11to12.name());
        }
        if (deptWorkA.containsKey(TicketTimeEnum.region_14to15.name())) {
            to15A = deptWorkA.get(TicketTimeEnum.region_14to15.name());
        }
        if (deptWorkA.containsKey(TicketTimeEnum.region_15to16.name())) {
            to16A = deptWorkA.get(TicketTimeEnum.region_15to16.name());
        }
        if (deptWorkA.containsKey(TicketTimeEnum.region_16to17.name())) {
            to17A = deptWorkA.get(TicketTimeEnum.region_16to17.name());
        }

        if (deptWorkB.containsKey(TicketTimeEnum.region_10to11.name())) {
            to11B = deptWorkB.get(TicketTimeEnum.region_10to11.name());
        }
        if (deptWorkB.containsKey(TicketTimeEnum.region_11to12.name())) {
            to12B = deptWorkB.get(TicketTimeEnum.region_11to12.name());
        }
        if (deptWorkB.containsKey(TicketTimeEnum.region_14to15.name())) {
            to15B = deptWorkB.get(TicketTimeEnum.region_14to15.name());
        }
        if (deptWorkB.containsKey(TicketTimeEnum.region_15to16.name())) {
            to16B = deptWorkB.get(TicketTimeEnum.region_15to16.name());
        }
        if (deptWorkB.containsKey(TicketTimeEnum.region_16to17.name())) {
            to17B = deptWorkB.get(TicketTimeEnum.region_16to17.name());
        }

        if (currentBdzWorkA.containsKey(TicketTimeEnum.region_10to11.name())) {
            bTo11A = currentBdzWorkA.get(TicketTimeEnum.region_10to11.name());
        }
        if (currentBdzWorkA.containsKey(TicketTimeEnum.region_11to12.name())) {
            bTo12A = currentBdzWorkA.get(TicketTimeEnum.region_11to12.name());
        }
        if (currentBdzWorkA.containsKey(TicketTimeEnum.region_14to15.name())) {
            bTo15A = currentBdzWorkA.get(TicketTimeEnum.region_14to15.name());
        }
        if (currentBdzWorkA.containsKey(TicketTimeEnum.region_15to16.name())) {
            bTo16A = currentBdzWorkA.get(TicketTimeEnum.region_15to16.name());
        }
        if (currentBdzWorkA.containsKey(TicketTimeEnum.region_16to17.name())) {
            bTo17A = currentBdzWorkA.get(TicketTimeEnum.region_16to17.name());
        }

        if (currentBdzWorkB.containsKey(TicketTimeEnum.region_10to11.name())) {
            bTo11B = currentBdzWorkB.get(TicketTimeEnum.region_10to11.name());
        }
        if (currentBdzWorkB.containsKey(TicketTimeEnum.region_11to12.name())) {
            bTo12B = currentBdzWorkB.get(TicketTimeEnum.region_11to12.name());
        }
        if (currentBdzWorkB.containsKey(TicketTimeEnum.region_14to15.name())) {
            bTo15B = currentBdzWorkB.get(TicketTimeEnum.region_14to15.name());
        }
        if (currentBdzWorkB.containsKey(TicketTimeEnum.region_15to16.name())) {
            bTo16B = currentBdzWorkB.get(TicketTimeEnum.region_15to16.name());
        }
        if (currentBdzWorkB.containsKey(TicketTimeEnum.region_16to17.name())) {
            bTo17B = currentBdzWorkB.get(TicketTimeEnum.region_16to17.name());
        }
        workTotal = department.groupCount * 2;
        set10To11CheckStatus(selectType, dateBinding);
        set16To17CheckStatus(selectType, dateBinding);
        set15To16CheckStatus(selectType, dateBinding);
        set14To15CheckStatus(selectType, dateBinding);
        set11To12CheckStatus(selectType, dateBinding);
        clearTimeTotal();

    }

    private void clearTimeTotal() {
        bTo11A = 0;
        bTo12A = 0;
        bTo15A = 0;
        bTo16A = 0;
        bTo17A = 0;
        bTo11B = 0;
        bTo12B = 0;
        bTo15B = 0;
        bTo16B = 0;
        bTo17B = 0;
        to11A = 0;
        to12A = 0;
        to15A = 0;
        to16A = 0;
        to17A = 0;
        to11B = 0;
        to12B = 0;
        to15B = 0;
        to16B = 0;
        to17B = 0;
    }

    /**
     * 10:00到11：00时间可选状态
     *
     * @param selectType  选择工作类型
     * @param dateBinding
     */
    private void set10To11CheckStatus(String selectType, ActivityTicketDateBinding dateBinding) {

        if (workTotal > (to11B + to11A * workUnitA)) {
            if (select10To11) {
                dateBinding.txtTime1.setChecked(false);
                dateBinding.txtTime1.setEnabled(false);
                return;
            }

            if (a.equalsIgnoreCase(selectType) && bTo11A * workUnitA + bTo11B + workUnitA <= bdzTotal) {
                dateBinding.txtTime1.setEnabled(true);
            } else if (b.equalsIgnoreCase(selectType) && bTo11A * workUnitA + bTo11B + workUnitB <= bdzTotal) {
                dateBinding.txtTime1.setEnabled(true);
            } else {
                dateBinding.txtTime1.setChecked(false);
                dateBinding.txtTime1.setEnabled(false);
            }
        } else {
            dateBinding.txtTime1.setChecked(false);
            dateBinding.txtTime1.setEnabled(false);
        }
    }

    private void set11To12CheckStatus(String selectType, ActivityTicketDateBinding dateBinding) {
        if (workTotal > (to12B + to12A * workUnitA)) {
            if (select11To12) {
                dateBinding.txtTime2.setChecked(false);
                dateBinding.txtTime2.setEnabled(false);
                return;
            }
            if (a.equalsIgnoreCase(selectType) && bTo12A * workUnitA + bTo12B + workUnitA <= bdzTotal) {
                dateBinding.txtTime2.setEnabled(true);
            } else if (b.equalsIgnoreCase(selectType) && bTo12A * workUnitA + bTo12B + workUnitB <= bdzTotal) {
                dateBinding.txtTime2.setEnabled(true);
            } else {
                dateBinding.txtTime2.setChecked(false);
                dateBinding.txtTime2.setEnabled(false);
            }
        } else {
            dateBinding.txtTime2.setChecked(false);
            dateBinding.txtTime2.setEnabled(false);
        }

    }

    private void set14To15CheckStatus(String selectType, ActivityTicketDateBinding dateBinding) {
        if (workTotal > (to15B + to15A * workUnitA)) {

            if (select14To15) {
                dateBinding.txtTime3.setChecked(false);
                dateBinding.txtTime3.setEnabled(false);
                return;
            }
            if (a.equalsIgnoreCase(selectType) && bTo15A * workUnitA + bTo15B + workUnitA <= bdzTotal) {
                dateBinding.txtTime3.setEnabled(true);
            } else if (b.equalsIgnoreCase(selectType) && bTo15A * workUnitA + bTo15B + workUnitB <= bdzTotal) {
                dateBinding.txtTime3.setEnabled(true);
            } else {
                dateBinding.txtTime3.setChecked(false);
                dateBinding.txtTime3.setEnabled(false);
            }
        } else {
            dateBinding.txtTime3.setChecked(false);
            dateBinding.txtTime3.setEnabled(false);
        }

    }

    private void set15To16CheckStatus(String selectType, ActivityTicketDateBinding dateBinding) {
        if (workTotal > (to16B + to16A * workUnitA)) {
            if (select15To16) {
                dateBinding.txtTime4.setChecked(false);
                dateBinding.txtTime4.setEnabled(false);
                return;
            }
            if (a.equalsIgnoreCase(selectType) && bTo16A * workUnitA + bTo16B + workUnitA <= bdzTotal) {
                dateBinding.txtTime4.setEnabled(true);
            } else if (b.equalsIgnoreCase(selectType) && bTo16A * workUnitA + bTo16B + workUnitB <= bdzTotal) {
                dateBinding.txtTime4.setEnabled(true);
            } else {
                dateBinding.txtTime4.setChecked(false);
                dateBinding.txtTime4.setEnabled(false);
            }
        } else {
            dateBinding.txtTime4.setChecked(false);
            dateBinding.txtTime4.setEnabled(false);
        }

    }

    private void set16To17CheckStatus(String selectType, ActivityTicketDateBinding dateBinding) {
        if (workTotal > (to17B + to17A * workUnitA)) {
            if (select16To17) {
                dateBinding.txtTime5.setChecked(false);
                dateBinding.txtTime5.setEnabled(false);
                return;
            }
            if (a.equalsIgnoreCase(selectType) && bTo17A * workUnitA + bTo17B + workUnitA <= bdzTotal) {
                dateBinding.txtTime5.setEnabled(true);
            } else if (b.equalsIgnoreCase(selectType) && bTo17A * workUnitA + bTo17B + workUnitB <= bdzTotal) {
                dateBinding.txtTime5.setEnabled(true);
            } else {
                dateBinding.txtTime5.setChecked(false);
                dateBinding.txtTime5.setEnabled(false);
            }
        } else {
            dateBinding.txtTime5.setChecked(false);
            dateBinding.txtTime5.setEnabled(false);
        }
    }

}

