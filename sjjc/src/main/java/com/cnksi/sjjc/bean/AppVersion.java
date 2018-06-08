package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by han on 2017/5/27.
 */

@Table(name = "pad_apk_version")
public class AppVersion {
    public static  final  String PADAPKID="pad_apk_id";
    @Column(name = PADAPKID,isId = true)
    public String padApkId;

    public static final String COUNTRYID="county_id";
    @Column(name = COUNTRYID)
    public String countryId;

    public static final String FILENAME="file_name";
    @Column(name = FILENAME)
    public String fileName;

    public static final String DESCRIPTION="description";
    @Column(name = DESCRIPTION)
    public String description;

    public static final String VERSIONCODE="version_code";
    @Column(name = VERSIONCODE)
    public String versionCode;

    public static final String VERSIONNAME="version_name";
    @Column(name = VERSIONNAME)
    public String versionName;

    public static final String CREATDATE="creat_date";
    @Column(name = CREATDATE)
    public String creatDate;

    public static final String LASTMODIFYTIME="last_modify_time";
    @Column(name = LASTMODIFYTIME)
    public String lastModifyTime;

    public static final String DLT="dlt";
    @Column(name = DLT)
    public int dlt;

}
