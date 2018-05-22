package com.cnksi.common.enmu;

public enum SpaceType {
        spacing("间隔"),
        spacing_self("自定义间隔"),
        cabinet("屏柜");
        String zhName;

        SpaceType(String zhName) {
            this.zhName = zhName;
        }

        public static boolean isCabinet(String type) {
            return cabinet.name().equals(type);
        }
    }