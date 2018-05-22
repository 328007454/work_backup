package com.cnksi.common.enmu;

/**
 * 任务状态
 * @author Wastrel
 */
public enum TaskStatus {
    undo("未巡视"), doing("巡视中"), done("已完成");
    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
