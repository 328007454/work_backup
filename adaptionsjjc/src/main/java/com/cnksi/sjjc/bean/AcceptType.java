package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * 验收类型
 */
@Table(name = "accept_type")
public class AcceptType implements Serializable {
    /**
     * 验收类型ID
     */
    @Column(name = "accept_type_id", isId = true)
    public String acceptTypeId = UUID.randomUUID().toString();
    /**
     * 验收类型设备类型Id
     */
    @Column(name = "device_type_id")
    public String deviceTypeId;

    /**
     * 验收类型名称
     */
    @Column(name = "accept_type_name")
    public String acceptTypeName;
    /**
     * 验收类型基本信息（参加人员，验收要求，异常处置）采用json存储<br/>
     * <p>
     * {
     * "参加人员": "变压器可研初设审查由所属管辖单位运检部、变电运维室、变电检修室、省评价中心等设备专责参与审查",
     * "验收要求": "变压器可研初设审查验收需由变压器专业技术人员提前对可研报告、初设资料等文件参与审查，并提出相关意见",
     * "异常处置": "验收发现质量问题时，验收人员应以'出厂验收缺陷整改反馈单'（见通用细则附录A4）的形式及时告知物资部门、生产厂家，提出整改意见。并填入'出厂验收记录'"
     * }
     * </p>
     */
    @Column(name = "accept_base_info")
    public String acceptBaseInfo;

    public AcceptType() {
    }

    public AcceptType(String deviceTypeId, String acceptTypeName, String acceptBaseInfo) {
        this.deviceTypeId = deviceTypeId;
        this.acceptTypeName = acceptTypeName;
        this.acceptBaseInfo = acceptBaseInfo;
    }
}

