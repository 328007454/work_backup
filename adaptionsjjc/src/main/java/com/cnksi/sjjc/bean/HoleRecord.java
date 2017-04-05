package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by han on 2016/6/14.
 */
@Table(name = "sbjc_hole")
public class HoleRecord extends BaseModel{
    /**
     * Id
     */
    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id = UUID.randomUUID().toString();
    /**
     * 报告Id
     */
    public static final String REPORT_ID = "reportid";
    @Column(name = REPORT_ID)
    public String reportId;
    /**
     * 变电站id
     */
    public static final String BDZ_ID = "bdzid";
    @Column(name = BDZ_ID)
    public String bdzid;

    /**
     * 变电站名字
     */
    public static final String BDZ = "bdz_name";
    @Column(name = BDZ)
    public String bdz;

    /**
     * 位置
     */
    public static final String LOCATION = "location";
    @Column(name = LOCATION)
    public String location;
    /**
     * 孔洞详细位置
     */
    public static final String HOLE_DETAIL = "hole_detail";
    @Column(name = HOLE_DETAIL)
    public String hole_detail;

    /**
     * 孔洞图片
     */
    public static final String HOLE_IMAGES = "hole_images";
    @Column(name = HOLE_IMAGES)
    public String hole_images;

    /**
     * 状态0 没有清楚  1 已经清除
     */
    public static final String STATUS = "status";
    @Column(name = STATUS)
    public String status;

    /**
     * 清除报告id
     */
    public static final String CLEAR_REPORTID = "clear_reportid";
    @Column(name = CLEAR_REPORTID)
    public String clear_reportid;
    /**
     * 清除照片
     */
    public static final String CLEAR_IMAGES = "clear_images";
    @Column(name = CLEAR_IMAGES)
    public String clear_images;

    /**
     *备注
     */
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;
    /**
     *发现问题
     */
    public static final String PROBLEM = "problem";
    @Column(name = PROBLEM)
    public String problem;

    /**
     *更新时间
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;


    public HoleRecord(){}

    public HoleRecord(String currentReportId, String currentBdzId, String currentBdzName, String position, String morePosition,String allPic,String problem) {
        this.reportId=currentReportId;
        this.bdzid = currentBdzId;
        this.bdz = currentBdzName;
        this.location = position;
        this.hole_detail = morePosition;
        this.last_modify_time = DateUtils.getCurrentLongTime();
        this.dlt = "0";
        this.status ="0";
        this.hole_images = allPic;
        this.problem= problem;
        this.id = UUID.randomUUID().toString();
    }
}
