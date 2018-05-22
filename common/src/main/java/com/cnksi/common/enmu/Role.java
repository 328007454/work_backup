package com.cnksi.common.enmu;

/**
     * 角色
     *
     * @author Wastrel
     * @date 创建时间：2016年8月17日 下午1:23:43 TODO
     */
    public enum Role {
        worker("工作人"), leader("负责人");
        private final String value;

        Role(String value) {
            this.value = value;
        }
    }