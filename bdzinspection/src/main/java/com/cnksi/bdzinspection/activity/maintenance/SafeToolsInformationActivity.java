package com.cnksi.bdzinspection.activity.maintenance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.OperateToolResultService;
import com.cnksi.bdzinspection.daoservice.SafeToolsInfoService;
import com.cnksi.bdzinspection.databinding.XsActivitySafetytoolInformationBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSafetyToolStopBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSafetyToolTestBinding;
import com.cnksi.bdzinspection.databinding.XsSafetyToolResultItemBinding;
import com.cnksi.bdzinspection.emnu.ToolStatus;
import com.cnksi.bdzinspection.model.OperateToolResult;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.PersonListUtils;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Department;
import com.cnksi.common.model.Users;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 工器具详情列表
 * Created by han on 2017/6/29.
 */

public class SafeToolsInformationActivity extends BaseActivity implements View.OnClickListener, PersonListUtils.SelectPersonListener {
    XsActivitySafetytoolInformationBinding informationBinding;
    private DbModel dbModel;
    /**
     * 试验图片拍照集合
     */
    private ArrayList<String> reportPicList = new ArrayList<String>();
    /**
     * 实验结果集合
     */
    private List<OperateToolResult> resultList;
    /**
     * 地市model
     */
    private DbModel cityModel;
    /**
     * 班组所在地市
     */
    private String city;
    /**
     * 图片路径
     */
    private String toolPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        informationBinding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_safetytool_information);

        initialData();
        creatTestDialog();
        creatStopDialog();
    }


    private void initialData() {
        final String toolId = getIntent().getStringExtra(SafeToolsInfor.ID);
        final String dept = getIntent().getStringExtra(Department.DEPT_ID);
        final String bdzId = getIntent().getStringExtra(Bdz.BDZID);
        String title = getIntent().getStringExtra("title");
        informationBinding.tvInclude.tvTitle.setText(title);

        PersonListUtils.getInsance().initPopWindow(currentActivity).initPersonData(dept);
        PersonListUtils.getInsance().setPersonRatioListener(this);

        ExecutorManager.executeTask(() -> {
            cityModel = SafeToolsInfoService.getInstance().findToolCity();
            if (cityModel != null) {
                city = cityModel.getString("short_name_pinyin");
                toolPicPath = Config.GONG_QI_JU + city + "/gqj/";
                if (!FileUtils.isFolderExists(Config.BDZ_INSPECTION_FOLDER + toolPicPath)) {
                    FileUtils.makeDirectory(Config.BDZ_INSPECTION_FOLDER + toolPicPath);
                }
            }
            dbModel = SafeToolsInfoService.getInstance().findToolInfo(dept, toolId, bdzId);
            resultList = OperateToolResultService.getInstance().finAllResults(dept, toolId, bdzId);
            runOnUiThread(() -> initialUI());
        });
    }

    private void initialUI() {
        informationBinding.btnStop.setOnClickListener(this);
        informationBinding.btnTest.setOnClickListener(this);
        informationBinding.tvInclude.ibtnCancel.setOnClickListener(v -> currentActivity.finish());
        addResutUI();

    }

    /**
     * 动态添加试验结果
     */
    private XsSafetyToolResultItemBinding itemBinding;
    int position = 0;

    private void addResutUI() {
        if (null != dbModel) {
            informationBinding.setDbModel(dbModel);
        }
        position = 0;
        informationBinding.llRecordcontainer.removeAllViews();
        for (OperateToolResult result : resultList) {
            itemBinding = XsSafetyToolResultItemBinding.inflate(getLayoutInflater());
            String resultValue = ToolStatus.getValue(result.result);
            itemBinding.txtToolQualify.setText(resultValue);
            itemBinding.txtToolQualify.setTextColor(getResources().getColor(R.color.xs__5dbf19_color));
            itemBinding.txtTestTime.setText(TextUtils.isEmpty(result.operTime) ? "" : DateUtils.getFormatterTime(result.operTime, DateUtils.yyyy_MM_dd));
            String pics = result.pic;
            if (!TextUtils.isEmpty(pics)) {
                String[] toolPics = pics.split(Config.COMMA_SEPARATOR);
                itemBinding.ivReportPhoto.setImageBitmap(BitmapUtil.getImageThumbnail((toolPics == null || toolPics.length == 0) ? "" : Config.BDZ_INSPECTION_FOLDER + toolPics[0], 176, 118));
                if (toolPics != null && toolPics.length > 1) {
                    itemBinding.txtPicCount.setVisibility(View.VISIBLE);
                    itemBinding.txtPicCount.setText(toolPics.length + "");
                }
                itemBinding.ivReportPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentPosition = (int) v.getTag();
                        OperateToolResult operateToolResult = resultList.get(currentPosition);
                        String pics = operateToolResult.pic;
                        if (TextUtils.isEmpty(pics)) {
                            return;
                        }
                        String[] toolPic = pics.split(Config.COMMA_SEPARATOR);
                        if (null != toolPic && !TextUtils.isEmpty(toolPic[0]) && toolPic.length >= 1) {
                            ArrayList<String> mImageUrlList = new ArrayList<>();
                            for (int i = 0, count = toolPic.length; i < count; i++) {
                                mImageUrlList.add(Config.BDZ_INSPECTION_FOLDER + toolPic[i]);
                            }
                            showImageDetails(currentActivity, mImageUrlList, false);
                        }
                    }
                });
            }
            itemBinding.ivReportPhoto.setTag(position);
            informationBinding.llRecordcontainer.addView(itemBinding.getRoot(), position);
            ++position;
        }


    }

    private XsDialogSafetyToolStopBinding stopBinding;
    private Dialog stopDialog;
    private boolean stopClick;

    public void creatStopDialog() {
        stopBinding = XsDialogSafetyToolStopBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        stopDialog = DialogUtils.createDialog(currentActivity, stopBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        stopBinding.rgStop.setOnCheckedChangeListener(stopDialogCheckListener);
        stopBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDialog.dismiss();
            }
        });
        stopBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stopBinding.rbUnqualify.isChecked() && !stopBinding.rbInperiod.isChecked()) {
                    ToastUtils.showMessage( "请选择工器具停用的原因");
                    return;
                }
                String reason = stopBinding.etInputReason.getText().toString().trim();
                if (View.VISIBLE == stopBinding.etInputReason.getVisibility() && TextUtils.isEmpty(reason)) {
                    ToastUtils.showMessage( "请填写工器具不合格的原因");
                    return;
                }
                if (TextUtils.isEmpty(stopBinding.etInputStopperson.getText().toString())) {
                    ToastUtils.showMessage( "请输入操作人员姓名");
                    return;
                }
                stopDialog.dismiss();
                saveStopData();
            }
        });
        stopBinding.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonListUtils.getInsance().setHeight(stopBinding.getRoot().getHeight());
                PersonListUtils.getInsance().setCurrentUser(stopBinding.etInputStopperson.getText().toString().trim());
                stopClick = true;
                PersonListUtils.getInsance().showPopWindow(stopBinding.ivArrow);
            }
        });
    }

    private String stopStatus;
    RadioGroup.OnCheckedChangeListener stopDialogCheckListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            stopBinding.etInputReason.setVisibility(View.GONE);
            if (checkedId == R.id.rb_inperiod) {
                stopStatus = ToolStatus.inTest.name();
                stopBinding.txtTips.setVisibility(View.GONE);

            } else if (checkedId == R.id.rb_overdate) {
                stopStatus = "overdate";

            } else if (checkedId == R.id.rb_unqualify) {
                stopStatus = ToolStatus.stop.name();
                stopBinding.etInputReason.setVisibility(View.VISIBLE);
                stopBinding.txtTips.setVisibility(View.VISIBLE);

            } else {
            }
        }
    };

    private XsDialogSafetyToolTestBinding testBinding;
    private Dialog testDialog;
    private String currentImageName = "";
    private int length;

    public void creatTestDialog() {
        testBinding = XsDialogSafetyToolTestBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        testDialog = DialogUtils.createDialog(currentActivity, testBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        testBinding.rgTest.setOnCheckedChangeListener(testChangeListener);
        testBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDialog.dismiss();
            }
        });
        testBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!testBinding.rbNormal.isChecked() && !testBinding.rbUnnormal.isChecked()) {
                    ToastUtils.showMessage( "请选择试验结果");
                    return;
                }
                testDialog.dismiss();
                saveTestData();
            }
        });
        testBinding.ibtnSelectInspectionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerDialog.showDatePickerDialog(currentActivity, new CustomerDialog.DialogItemClickListener() {
                    @Override
                    public void confirm(String result, int position) {
                        String productTime = dbModel.getString("produce_time");
                        if (!TextUtils.isEmpty(productTime) && !DateUtils.compareDate(result, productTime, DateUtils.yyyy_MM_dd)) {
                            ToastUtils.showMessage( "试验时间必须大于出厂时间");
                            return;
                        }
                        testBinding.txtTestTime.setText(result);
                    }
                });
            }
        });
        testBinding.ibtnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentImageName = toolPicPath + FunctionUtil.getCurrentImageName();
                FunctionUtil.takePicture(currentActivity, currentImageName, Config.BDZ_INSPECTION_FOLDER);
            }
        });
        testBinding.ivReportPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!reportPicList.isEmpty()) {
                    ArrayList<String> mImageUrlList = new ArrayList<>();
                    for (int i = 0, count = reportPicList.size(); i < count; i++) {
                        mImageUrlList.add(Config.BDZ_INSPECTION_FOLDER + reportPicList.get(i));
                    }
                    showImageDetails(currentActivity, mImageUrlList, true);
                }
            }
        });

        testBinding.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonListUtils.getInsance().setHeight(testBinding.getRoot().getHeight());
                PersonListUtils.getInsance().setCurrentUser(testBinding.etInputPerson.getText().toString().trim());
                stopClick = false;
                PersonListUtils.getInsance().showPopWindow(testBinding.ivArrow);
            }
        });

    }

    RadioGroup.OnCheckedChangeListener testChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if (checkedId == R.id.rb_normal) {
                testBinding.txtTips.setVisibility(View.GONE);

            } else if (checkedId == R.id.rb_unnormal) {
                testBinding.txtTips.setVisibility(View.VISIBLE);

            } else {
            }
        }
    };

    /**
     * 加水印在图片上
     */
    private void addWaterTextToBitmap() {
        CustomerDialog.showProgress(currentActivity, "正在处理图片...");
        ExecutorManager.executeTask(new Runnable() {
            @Override
            public void run() {
                Bitmap currentBitmap = BitmapUtil.createScaledBitmapByWidth(BitmapUtil.postRotateBitmap(Config.BDZ_INSPECTION_FOLDER + currentImageName), ScreenUtils.getScreenWidth(currentActivity));
                if (null == currentBitmap) {
                    ToastUtils.showMessage( "很抱歉，因意外需要您再次拍照。");
                    return;
                }
                currentBitmap = BitmapUtil.addText2Bitmap(currentBitmap, DateUtils.getCurrentTime(DateUtils.yyyy_MM_dd_HH_mm_ss), getResources().getDimensionPixelOffset(R.dimen.xs_global_text_size));
                BitmapUtil.saveBitmap(currentBitmap, Config.BDZ_INSPECTION_FOLDER + currentImageName, 60);
                reportPicList.add(currentImageName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomerDialog.dismissProgress();
                        setImage();
                    }
                });
            }
        });
    }

    /**
     * 保存停用数据
     */
    private void saveStopData() {
        String toolId = dbModel.getString("id");
        String name = dbModel.getString("name");
        String reason = stopBinding.etInputReason.getText().toString().trim();
        String stopPerson = stopBinding.etInputStopperson.getText().toString().trim();
        try {
            OperateToolResult toolResult = null;
            if (stopBinding.rbUnqualify.isChecked()) {//选择不合格
                toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, toolId, name, ToolStatus.stop.name(), ToolStatus.unNormal.name(), reason, stopPerson);
                dbModel.add("status", ToolStatus.stop.name());
                SafeToolsInfoService.getInstance().updateStatus(toolId,ToolStatus.stop);
                informationBinding.btnTest.setVisibility(View.GONE);
                informationBinding.btnStop.setVisibility(View.GONE);
            } else {
                toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, toolId, name, ToolStatus.stop.name(), ToolStatus.normal.name(), reason, stopPerson);
                dbModel.add("status", ToolStatus.inTest.name());
                SafeToolsInfoService.getInstance().updateStatus(toolId,ToolStatus.inTest);
                informationBinding.txtToolStatus.setText(ToolStatus.inTest.value);
                informationBinding.btnStop.setVisibility(View.GONE);
                informationBinding.btnTest.setVisibility(View.VISIBLE);
            }
            OperateToolResultService.getInstance().saveOrUpdate(toolResult);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 试验结果上次时间
     */
    private String lastTime;

    /**
     * 试验结果下次时间
     */
    private String nextTime;

    public void saveTestData() {
        String id = dbModel.getString("id");
        String name = dbModel.getString("name");
        String time = testBinding.txtTestTime.getText().toString().trim();
        String pics = StringUtils.arrayListToString(reportPicList);
        String person = testBinding.etInputPerson.getText().toString().trim();
        OperateToolResult toolResult = null;
        try {
            lastTime = testBinding.txtTestTime.getText().toString().trim() + " 00:00:00";
            if ("0".equalsIgnoreCase(dbModel.getString("period"))) {
                nextTime = null;
            } else {
                nextTime = DateCalcUtils.getAfterMonth(lastTime, Integer.valueOf(dbModel.getString("period")));
            }
            if (testBinding.rbNormal.isChecked()) {
                toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, id, name, time, ToolStatus.normal.name(), pics, person, ToolStatus.test.name());
                updateToolInfo(ToolStatus.normal.name(), ToolStatus.normal.name());
                SafeToolsInfoService.getInstance().update(WhereBuilder.b("id", "=", id).and("dlt", "<>", "1"), new KeyValue("status", ToolStatus.normal.name()), new KeyValue("isnormal", ToolStatus.normal.name()),
                        new KeyValue("lastly_check_time", lastTime), new KeyValue("next_check_time", nextTime));
                informationBinding.btnStop.setVisibility(View.VISIBLE);
                informationBinding.btnTest.setVisibility(View.VISIBLE);

            } else if (testBinding.rbUnnormal.isChecked()) {
                toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, id, name, time, ToolStatus.unNormal.name(), pics, person, ToolStatus.test.name());
                updateToolInfo(ToolStatus.stop.name(), ToolStatus.unNormal.name());
                SafeToolsInfoService.getInstance().update(WhereBuilder.b("id", "=", id), new KeyValue("status", ToolStatus.stop.name()), new KeyValue("isnormal", ToolStatus.unNormal.name()));
                informationBinding.btnStop.setVisibility(View.GONE);
                informationBinding.btnTest.setVisibility(View.GONE);
            }
           OperateToolResultService.getInstance().saveOrUpdate(toolResult);
            resultList.add(toolResult);
            addResutUI();
        } catch (DbException e)

        {
            e.printStackTrace();
        }

    }

    private void updateToolInfo(String status, String isnormal) {
        dbModel.add("status", status);
        dbModel.add("isnormal", isnormal);
        dbModel.add("lastly_check_time", lastTime);
        dbModel.add("next_check_time", nextTime);
//        try {
//            XunshiApplication.getDbUtils().saveOrUpdate(dbModel);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:// 拍照后返回
                    addWaterTextToBitmap();
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    if (cancelList != null) {
                        for (String imageUrl : cancelList) {
                            reportPicList.remove(imageUrl.replace(Config.BDZ_INSPECTION_FOLDER, ""));
                        }
                    }
                    setImage();
                default:
                    break;
            }
        }
    }

    public void setImage() {
        if (reportPicList == null || reportPicList.size() == 0) {
            testBinding.ivReportPhoto.setVisibility(View.INVISIBLE);
            testBinding.txtPicCount.setVisibility(View.INVISIBLE);
            return;
        } else {
            testBinding.ivReportPhoto.setVisibility(View.VISIBLE);
            if (reportPicList.size() == 1) {
                testBinding.txtPicCount.setVisibility(View.INVISIBLE);
            } else {
                testBinding.txtPicCount.setVisibility(View.VISIBLE);
                testBinding.txtPicCount.setText(String.valueOf(reportPicList.size()));
            }
        }
        testBinding.ivReportPhoto.setImageBitmap(BitmapUtil.getImageThumbnail(Config.BDZ_INSPECTION_FOLDER + reportPicList.get(0), 220, 148));
    }

    @Override
    public void onClick(View v) {
        String selectPersons = PreferencesUtils.get( Config.SELECT_PERSONS, "");
        int i = v.getId();
        if (i == R.id.btn_stop) {
            String name = dbModel.getString("name");
            CharSequence txtToolName = StringUtils.formatPartTextColor("请选择停用%s的原因", currentActivity.getResources().getColor(R.color.xs_color_5dbf19), name + "工器具");
            stopBinding.txtToolName.setText(txtToolName);
            stopBinding.tvDialogTitle.setText("停用原因");
            stopBinding.rgStop.clearCheck();
            stopBinding.etInputReason.setText("");
            stopBinding.txtTips.setVisibility(View.GONE);
            stopBinding.etInputStopperson.setText(selectPersons);
            stopDialog.show();

        } else if (i == R.id.btn_test) {
            reportPicList.clear();//试验
            testBinding.txtPicCount.setText("");
            testBinding.txtPicCount.setVisibility(View.GONE);
            testBinding.txtTestTime.setText(DateUtils.getCurrentShortTime());
            testBinding.tvDialogTitle.setText("试验信息");
            testBinding.rgTest.clearCheck();
            testBinding.etInputPerson.setText("");
            testBinding.ivReportPhoto.setImageBitmap(null);
            testBinding.txtTips.setVisibility(View.GONE);
            testBinding.etInputPerson.setText(selectPersons);
            testDialog.show();

        } else {
        }
    }

    @Override
    public void getSelectPersons(List<Users> userses) {
        if (userses == null) {
            return;
        }
        List<String> usersList = new ArrayList<>();
        String userName = "";
        for (Users users : userses) {
            usersList.add(users.username);
        }
        if (!usersList.isEmpty()) {
            userName = StringUtils.arrayListToString(usersList);
            PreferencesUtils.put( Config.SELECT_PERSONS, userName);
        }
        if (stopClick) {
            stopBinding.etInputStopperson.setText(userName);
        } else {
            testBinding.etInputPerson.setText(userName);
        }

        PersonListUtils.getInsance().disMissPopWindow();
    }
}
