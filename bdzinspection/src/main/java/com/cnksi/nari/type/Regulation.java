package com.cnksi.nari.type;

/**
 * PMS 离线作业包类型的组织ID
 * @version 1.0
 * @author wastrel
 * @date 2017/7/21 16:39
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public enum Regulation {
    XS("[\"38BDF894-1AD4-4860-BFDB-18C714D5813D\",\"2F83BB92-3CCA-4814-8C25-49F46A2BB6D8\"]"),
    JC("[\"AA5DE1CB-FBB2-4231-82E5-4F8F43201C1C\",\"9241DE67-F795-4BE3-9E90-0F1DAB483397\"]"),
    JX("[\"290A1472-F48B-4350-BA46-582C6090BF54\"]"),
    XSDY("[\"94FC1CD5-2227-4E4A-949C-EA91765B96EE\"]"),
    LSXS("[\"71B75BAD-543F-43CA-9463-5CEA9DE7F8EF\"]"),
    LSJC("[\"EC6377FD-630C-46FD-B3A4-3E43BF0CAE76\"]");
    String code;

    Regulation(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
