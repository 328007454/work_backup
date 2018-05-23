package com.cnksi.bdzinspection.emnu;

/**
     * 工器具停用状态
     */
    public enum ToolStatus {
        normal("正常"), unNormal("不正常"), inTest("试验中"), stop("作废"), test("试验"), overdue("超期"), quarantine("待检");//normal,overdue,quarantine默认处理为正常

        public final String value;

        ToolStatus(String value) {
            this.value = value;
        }

        public static String getValue(String name) {
            if (normal.name().equalsIgnoreCase(name)) {
                return normal.value;
            } else if (unNormal.name().equalsIgnoreCase(name)) {
                return unNormal.value;
            } else if (inTest.name().equalsIgnoreCase(name)) {
                return inTest.value;
            } else if (quarantine.name().equalsIgnoreCase(name)) {
                return quarantine.value;
            } else if (overdue.name().equalsIgnoreCase(name)) {
                return overdue.value;
            } else {
                return "";
            }

        }

        @Override
        public String toString() {
            return name();
        }
    }