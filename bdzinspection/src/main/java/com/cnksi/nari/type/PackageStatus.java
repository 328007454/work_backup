package com.cnksi.nari.type;

/**
 * 离线作业包任务状态
 * @version 1.0
 * @auth wastrel
 * @date 2017/8/1 11:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public enum PackageStatus {
    undo("未完成"),
    nodown("未下载"),
    done("已完成"),
    upload("已上传"),
    upload_error("上传失败"),
    notopt("不支持");
    public String zhName;

    PackageStatus(String zhName) {
        this.zhName = zhName;
    }

    public boolean equals(String s) {
        return this.name().equals(s);
    }

    public static PackageStatus find(String str) {
        for (PackageStatus status : PackageStatus.values()) {
            if (status.name().equals(str)) {
                return status;
            }
        }
        return null;
    }

}
