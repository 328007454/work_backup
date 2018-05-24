package com.cnksi.bdzinspection.emnu;

/**
     * 操作票任务类型
     */
    public enum OperateTaskType {
        DBRW("待办任务"), BYRW("本月任务"), BNRW("本年任务");
        private final String value;

        OperateTaskType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }