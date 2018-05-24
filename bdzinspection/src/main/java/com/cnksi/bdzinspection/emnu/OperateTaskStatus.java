package com.cnksi.bdzinspection.emnu;

/**
     * 操作票任务状态
     */
    public enum OperateTaskStatus {
        dsh("待审核"), wwc("未完成"), yzt("已暂停"), ywc("已完成"), ytz("已停止");
        private final String value;

        OperateTaskStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }