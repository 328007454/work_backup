package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.BatteryGroup;
import com.cnksi.sjjc.bean.BatteryRecord;
import com.cnksi.sjjc.bean.CopyResult;
import com.cnksi.sjjc.bean.DefectDefine;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceStandards;
import com.cnksi.sjjc.bean.ErrorLogBean;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.ModifyRecord;
import com.cnksi.sjjc.bean.Placed;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportHwcw;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.bean.ReportSignname;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.bean.Spacing;
import com.cnksi.sjjc.bean.StandardStepConfirm;
import com.cnksi.sjjc.bean.SwitchPic;
import com.cnksi.sjjc.bean.Transceiver;
import com.cnksi.sjjc.bean.upload.UploadDefectDefine;
import com.cnksi.sjjc.bean.upload.UploadDevice;
import com.cnksi.sjjc.bean.upload.UploadDeviceStandards;
import com.cnksi.sjjc.bean.upload.UploadSpacing;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 创建索引
 *
 * @author terry
 */
public class IndexService {

    public static IndexService mInstance;

    private IndexService() {
    }

    public static IndexService getInstance() {
        if (mInstance == null) {
            mInstance = new IndexService();
        }
        return mInstance;
    }

    /**
     * 需要创建的索引及表的名称
     */
    private String[] indexNameArray = {"spacing_index", "device_index", "device_part_index", "device_standards_index",
            "defect_define_index"};
    private String[] tableNameArray = {"spacing", "device", "device_part", "device_standards", "defect_define"};
    private String[] indexColumnArray = {"bdzid", "bdzid,spid,name_pinyin", "deviceid", "duid,kind", "staid"};

    public void createIndex() {
        createIndex(indexNameArray, tableNameArray, indexColumnArray);
    }

    public void createIndex(String[] indexNameList, String[] tableNameList, String[] indexColumnList) {
        for (int i = 0, count = indexNameList.length; i < count; i++) {
            String indexName = indexNameList[i];
            try {
                SqlInfo sqlInfo = new SqlInfo("select count (*) indexCount from sqlite_master where type='index' and name = '" + indexName + "'");
                DbModel indexModel = CustomApplication.getInstance().getDbManager().findDbModelFirst(sqlInfo);
                if (TextUtils.isEmpty(indexModel.getString("indexCount")) || indexModel.getInt("indexCount") <= 0) {
                    sqlInfo = new SqlInfo(
                            "create index " + indexName + " on " + tableNameList[i] + "(" + indexColumnList[i] + ")");
                    CustomApplication.getInstance().getDbManager().execNonQuery(sqlInfo);
                }
            } catch (DbException e) {
                e.printStackTrace();
                continue;
            }
        }

        // create index spacing_index on spacing(bdzid);
        //
        // create index device_index on device(bdzid,spid,name_pinyin)
        //
        // create index device_part_index on device_part(deviceid)
        //
        // create index device_standards_index on device_standards(duid,kind)
        //
        // create index defect_define_index on defect_define(staid)
    }

    /**
     * 需要上传的表
     */
    private Class<?>[] TableArray = {ModifyRecord.class, Device.class, Spacing.class, DevicePart.class,
            DeviceStandards.class, Report.class, DefectRecord.class, DefectDefine.class, SwitchPic.class,
            ReportHwcw.class, ErrorLogBean.class, ReportCdbhcl.class, ReportJzlbyqfjkg.class, ReportSnwsd.class, Placed.class,
            Transceiver.class, BatteryRecord.class, HoleRecord.class, PreventionRecord.class, BatteryGroup.class, ReportSignname.class, CopyResult.class, StandardStepConfirm.class
    };

    /**
     * 生成需要上传的数据库
     *
     * @param dbManager
     */
    public void generateUploadData(DbManager dbManager) {
        try {
            CustomApplication.getInstance().getDbManager().getTable(ModifyRecord.class).tableIsExist(dbManager);
            dbManager.dropDb();
            createTableIfNotExist(CustomApplication.getInstance().getDbManager(),CustomApplication.getInstance().getDbManager().getTable(ModifyRecord.class));
        } catch (DbException e) {
            e.printStackTrace();
        }
        // 得到所有的允许上传的报告id
        String uploadReportSql = "(select r.reportid from report r left join task t on t.taskid = r.taskid where (t.STATUS = 'done' or t.STATUS = 'doing') and r.is_upload='Y')";
        for (Class<?> tableClass : TableArray) {
            try {
                String selectionSql = "(select distinct(modify_id) from modify_record where table_name = '"
                        + CustomApplication.getInstance().getDbManager().getTable(tableClass).getName() + "')";
                Selector selector = null;
                if (Device.class.equals(tableClass)) {
                    tableClass = UploadDevice.class;
                    selector =CustomApplication.getInstance().getDbManager().selector(tableClass).expr("deviceid in " + selectionSql);
                } else if (Spacing.class.equals(tableClass)) {
                    tableClass = UploadSpacing.class;
                    // selector =dbManager.selector(tableClass).expr("spid in " + selectionSql);
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass);
                } else if (DevicePart.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("duid in " + selectionSql);
                } else if (DeviceStandards.class.equals(tableClass)) {
                    tableClass = UploadDeviceStandards.class;
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("staid in " + selectionSql);
                } else if (DefectDefine.class.equals(tableClass)) {
                    tableClass = UploadDefectDefine.class;
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("defectid in " + selectionSql);
                } else if (Report.class.equals(tableClass)
                        || SwitchPic.class.equals(tableClass)
                        || PreventionRecord.class.equals(tableClass)
                        || BatteryRecord.class.equals(tableClass)
                        || StandardStepConfirm.class.equals(tableClass)
                        || Transceiver.class.equals(tableClass) || BatteryGroup.class.equals(tableClass) || CopyResult.class.equals(tableClass)
                        ) {
                    selector =CustomApplication.getInstance().getDbManager().selector(tableClass).expr("reportid in " + uploadReportSql);
                } else if (HoleRecord.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("reportid in " + uploadReportSql + " or " + HoleRecord.CLEAR_REPORTID + " in " + uploadReportSql);
                }
                //同步defect record表
                else if (DefectRecord.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("reportid in " + uploadReportSql + " or defectid in " + selectionSql);
                } else if (Placed.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("report_id in " + uploadReportSql);
                } else if (ReportHwcw.class.equals(tableClass)
                        || ReportSignname.class.equals(tableClass) ||
                        ReportCdbhcl.class.equals(tableClass) || ReportJzlbyqfjkg.class.equals(tableClass) || ReportSnwsd.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("report_id in " + uploadReportSql);
                }
                //新增错误日志
                else if (ErrorLogBean.class.equals(tableClass)) {
                    selector = CustomApplication.getInstance().getDbManager().selector(tableClass).expr("id in " + selectionSql);
                }
                createTableIfNotExist(dbManager, dbManager.getTable(tableClass));
                List<?> entities = null;
                if (selector != null) {
                    entities = selector.findAll();
                } else {
                    entities = CustomApplication.getInstance().getDbManager().findAll(tableClass);
                }
                if (entities != null && !entities.isEmpty())
                    if (tableClass.equals(ModifyRecord.class)) {
                        dbManager.save(entities);
                    } else {
                        dbManager.saveOrUpdate(entities);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    protected void createTableIfNotExist(DbManager dbManager, TableEntity<?> table) throws DbException {
        if (!table.tableIsExist(dbManager)) {
            synchronized (table.getClass()) {
                if (!table.tableIsExist(dbManager)) {
                    SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(table);
                    dbManager.execNonQuery(sqlInfo);
//					String execAfterTableCreated = table.getOnCreated();
//					if (!TextUtils.isEmpty(execAfterTableCreated)) {
//						execNonQuery(execAfterTableCreated);
//					}
//					table.setCheckedDatabase(true);
//					DbManager.TableCreateListener listener = this.getDaoConfig().getTableCreateListener();
//					if (listener != null) {
//						listener.onTableCreated(this, table);
//					}
                }
            }
        }
    }

}
