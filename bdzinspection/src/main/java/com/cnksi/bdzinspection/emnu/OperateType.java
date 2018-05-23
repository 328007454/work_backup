package com.cnksi.bdzinspection.emnu;

/**
     * 操作票类型
     */
    public enum OperateType {
        dr("单人操作"), jh("监护下操作"), jx("检修人员操作");
        private final String value;

        OperateType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }