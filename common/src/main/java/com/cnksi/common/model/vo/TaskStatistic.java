package com.cnksi.common.model.vo;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/3/23 15:58
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TaskStatistic {
    //今日任务数量
    long todayTaskCount = 0;
    //今日已完成
    long todayTaskFinish = 0;
    //本月任务
    long monthTaskCount = 0;
    //本月以完成
    long monthTaskFinish = 0;

    public long getTodayTaskCount() {
        return todayTaskCount;
    }

    public void setTodayTaskCount(long todayTaskCount) {
        this.todayTaskCount = todayTaskCount;
    }

    public long getTodayTaskFinish() {
        return todayTaskFinish;
    }

    public void setTodayTaskFinish(long todayTaskFinish) {
        this.todayTaskFinish = todayTaskFinish;
    }

    public long getMonthTaskCount() {
        return monthTaskCount;
    }

    public void setMonthTaskCount(long monthTaskCount) {
        this.monthTaskCount = monthTaskCount;
    }

    public long getMonthTaskFinish() {
        return monthTaskFinish;
    }

    public void setMonthTaskFinish(long monthTaskFinish) {
        this.monthTaskFinish = monthTaskFinish;
    }

}
