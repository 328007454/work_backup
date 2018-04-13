package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/30 9:21
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "spacing_group")
public class SpacingGroup {
    public static final String ID= "id";
    @Id(column=ID)
    public String id;
    public static final String NAME= "name";
    @Column(column=NAME)
    public String name;
    public static final String NAME_PINYIN= "name_pinyin";
    @Column(column=NAME_PINYIN)
    public String name_pinyin;
    public static final String BDZID= "bdzid";
    @Column(column=BDZID)
    public String bdzid;
    public static final String BDZ_NAME= "bdz_name";
    @Column(column=BDZ_NAME)
    public String bdz_name;
    public static final String DEPT_ID= "dept_id";
    @Column(column=DEPT_ID)
    public String dept_id;
    public static final String DEPT_NAME= "dept_name";
    @Column(column=DEPT_NAME)
    public String dept_name;
    public static final String DEVICE_TYPE= "device_type";
    @Column(column=DEVICE_TYPE)
    public String device_type;
    public static final String SORT= "sort";
    @Column(column=SORT)
    public int sort;
    public static final String LATITUDE= "latitude";
    @Column(column=LATITUDE)
    public String latitude;
    public static final String LONGITUDE= "longitude";
    @Column(column=LONGITUDE)
    public String longitude;
    public static final String CREATER= "creater";
    @Column(column=CREATER)
    public String creater;
    public static final String DLT= "dlt";
    @Column(column=DLT)
    public String dlt="0";
    public static final String UPDATE_TIME= "update_time";
    @Column(column=UPDATE_TIME)
    public String update_time;
    public static final String CREATE_TIME= "create_time";
    @Column(column=CREATE_TIME)
    public String create_time;

    /**
     * 是否是数据库中存在的小室，java代码组装的不算
     */
    public boolean copy = true;
    public SpacingGroup() {
    }

    public SpacingGroup(String name) {
        this.name = name;
        this.copy = false;
    }
}
