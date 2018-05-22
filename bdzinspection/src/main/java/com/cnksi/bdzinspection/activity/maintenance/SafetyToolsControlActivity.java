package com.cnksi.bdzinspection.activity.maintenance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.adapter.ItemClickListener;
import com.cnksi.bdzinspection.adapter.SafeToolsInfoAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.SafeToolsInfoService;
import com.cnksi.bdzinspection.databinding.XsActivitySafetyToolsBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSafetyToolStopBinding;
import com.cnksi.bdzinspection.databinding.XsDialogSafetyToolTestBinding;
import com.cnksi.bdzinspection.model.OperateToolResult;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.PersonListUtils;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.bdzinspection.view.keyboard.QWERKeyBoardUtils;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Department;
import com.cnksi.common.model.Users;
import com.cnksi.xscore.xsutils.BitmapUtil;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.cnksi.xscore.xsview.CustomerDialog;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 工器具维护管理页面
 * Created by han on 2017/6/28.
 */

public class SafetyToolsControlActivity extends BaseActivity implements ItemClickListener, PersonListUtils.SelectPersonListener {

    private XsActivitySafetyToolsBinding toolsBinding;
    private SafeToolsInfoAdapter infoAdapter;
    /**
     * 工器具集合
     */
    private List<DbModel> toolsInforList = new ArrayList<>();
    /**
     * 试验报告集合
     */
    private ArrayList<String> reportPicList = new ArrayList<String>();
    /**
     * 选择项（全部、一个月内、超期、试验中以及搜索输入字符）
     */
    private String selectItem = "";
    /**
     * item点击对象
     */
    private DbModel stopDbmodel;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 传递的变电站id
     */
    private String currentBdzId;
    /**
     * 工器具试用
     */
    private XsDialogSafetyToolTestBinding testBinding;
    /**
     * 工器具试用dialog
     */
    private Dialog testDialog;
    /**
     * 试验拍照名
     */
    private String currentImageName = "";
    /**
     * 是不是第一次进入，再次返回刷新数据
     */
    private boolean isFirstEnter = true;
    /**
     * 是否显示复选框
     */
    private boolean isShowCbox;
    /**
     * 是否全选
     */
    private boolean isSelectAll;
    /**
     * 选择工器具的集合
     */
    private HashSet<DbModel> selectmodels = new HashSet<>();
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
    /**
     * 是否是批量操作
     */
    private boolean isBatchOperate;

    private QWERKeyBoardUtils keyBoardUtils;

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            toolsBinding.btAll.setCompoundDrawables(null, null, null, null);
            toolsBinding.btAll.setPadding(0, 0, 0, 0);
            toolsBinding.btInmaintenance.setCompoundDrawables(null, null, null, null);
            toolsBinding.btInmaintenance.setPadding(0, 0, 0, 0);
            toolsBinding.btOverdate.setCompoundDrawables(null, null, null, null);
            toolsBinding.btOverdate.setPadding(0, 0, 0, 0);
            toolsBinding.btInmonth.setCompoundDrawables(null, null, null, null);
            toolsBinding.btInmonth.setPadding(0, 0, 0, 0);
            if (null != toolsInforList && !toolsInforList.isEmpty()) {
                toolsInforList.clear();
            }
            toolsBinding.cbAll.setChecked(false);
            if (infoAdapter != null) {
                isSelectAll = false;
                infoAdapter.setClearCheckMap();
                infoAdapter.setSelectedAll(isSelectAll);
                infoAdapter.notifyDataSetChanged();
            }
            selectmodels.clear();
            if (checkedId == R.id.bt_all) {
                toolsBinding.btAll.setPadding(0, 0, 20, 0);
                toolsBinding.btAll.setCompoundDrawablesWithIntrinsicBounds(null, null, currentActivity.getResources().getDrawable(R.drawable.xs_icon_check), null);
                selectItem = "";
                initData("");

            } else if (checkedId == R.id.bt_inmaintenance) {
                toolsBinding.btInmaintenance.setPadding(0, 0, 20, 0);
                toolsBinding.btInmaintenance.setCompoundDrawablesWithIntrinsicBounds(null, null, currentActivity.getResources().getDrawable(R.drawable.xs_icon_check), null);
                group.check(R.id.bt_inmaintenance);
                selectItem = String.valueOf(checkedId);
                initData(String.valueOf(checkedId));

            } else if (checkedId == R.id.bt_overdate) {
                toolsBinding.btOverdate.setPadding(0, 0, 20, 0);
                toolsBinding.btOverdate.setCompoundDrawablesWithIntrinsicBounds(null, null, currentActivity.getResources().getDrawable(R.drawable.xs_icon_check), null);
                group.check(R.id.bt_overdate);//超期
                selectItem = String.valueOf(checkedId);
                initData(String.valueOf(checkedId));

            } else if (checkedId == R.id.bt_inmonth) {
                toolsBinding.btInmonth.setPadding(0, 0, 20, 0);
                toolsBinding.btInmonth.setCompoundDrawablesWithIntrinsicBounds(null, null, currentActivity.getResources().getDrawable(R.drawable.xs_icon_check), null);
                initData(String.valueOf(checkedId));
                selectItem = String.valueOf(checkedId);
                group.check(R.id.bt_inmonth);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolsBinding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_safety_tools);
        deptId = getIntent().getStringExtra(Department.DEPT_ID);
        currentBdzId = getIntent().getStringExtra(Bdz.BDZID);
        initUI();
    }


    public void initUI() {
        getIntentValue();
        PersonListUtils.getInsance().initPopWindow(currentActivity).initPersonData(deptId);
        PersonListUtils.getInsance().setPersonRatioListener(this);
        toolsBinding.lvTools.setVerticalScrollBarEnabled(false);
        toolsBinding.btnStop.setVisibility(View.GONE);
        toolsBinding.btnCancel.setVisibility(View.GONE);
        toolsBinding.llCbContainer.setVisibility(View.GONE);
        toolsBinding.btnTest.setText("批量操作");
        toolsBinding.rgContainer.setOnCheckedChangeListener(checkedChangeListener);
        infoAdapter = new SafeToolsInfoAdapter(toolsBinding.lvTools, toolsInforList, R.layout.xs_item_safe_toolsinfo_adapter);
        toolsBinding.lvTools.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        toolsBinding.lvTools.setAdapter(infoAdapter);
        infoAdapter.setListener(this);
        keyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        keyBoardUtils.init(toolsBinding.keyoardContainer,
                new QWERKeyBoardUtils.keyWordChangeListener() {
                    @Override
                    public void onChange(View view, String oldKey, String newKey) {
                        toolsBinding.rgContainer.clearCheck();
                        keyboardClick(view, oldKey, newKey);
                    }
                });
        toolsBinding.rgContainer.check(R.id.bt_all);
        creatStopDialog();
        creatTestDialog();
        toolsBinding.titleInclude.ibtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity.finish();
            }
        });
        initToolCityData();
    }

    private void initToolCityData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                cityModel = SafeToolsInfoService.getInstance().findToolCity();
                if (cityModel != null) {
                    city = cityModel.getString("short_name_pinyin");
                    toolPicPath = Config.GONG_QI_JU + city + "/gqj/";
                    if (!FileUtils.isFolderExists(Config.BDZ_INSPECTION_FOLDER + toolPicPath)) {
                        FileUtils.makeDirectory(Config.BDZ_INSPECTION_FOLDER + toolPicPath);
                    }
                }
            }
        });
    }

    private void keyboardClick(View view, String oldKey, String newKey) {
        int i = view.getId();
        if (i == R.id.tv_keyboard_item) {
            toolsInforList.clear();
            infoAdapter.notifyDataSetChanged();
            selectItem = newKey;
            initData(selectItem);

        } else if (i == R.id.tv_keyboard_words) {
            // 删除字符
        } else if (i == R.id.ibtn_keyboard_delete) {
            toolsInforList.clear();
            infoAdapter.notifyDataSetChanged();
            if (TextUtils.isEmpty(newKey)) {
                toolsBinding.rgContainer.check(R.id.bt_all);
            }
            selectItem = newKey;
            initData(selectItem);

        } else {
        }

    }


    public void initData(final String keyWord) {

        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(keyWord) && String.valueOf(R.id.bt_inmonth).equalsIgnoreCase(keyWord)) {
                    toolsInforList = SafeToolsInfoService.getInstance().findInMonthTools(deptId, DateUtils.getCurrentLongTime(), currentBdzId);
                } else if (!TextUtils.isEmpty(keyWord) && String.valueOf(R.id.bt_inmaintenance).equalsIgnoreCase(keyWord)) {
                    toolsInforList = SafeToolsInfoService.getInstance().findInPeriodTools(deptId, currentBdzId);
                } else if (!TextUtils.isEmpty(keyWord) && String.valueOf(R.id.bt_overdate).equalsIgnoreCase(keyWord)) {
                    toolsInforList = SafeToolsInfoService.getInstance().findOverDateTools(deptId, DateUtils.getCurrentLongTime(), currentBdzId);
                } else {
                    toolsInforList = SafeToolsInfoService.getInstance().findAllTools(deptId, keyWord, currentBdzId);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (null != toolsInforList && !toolsInforList.isEmpty()) {
//                            String title = TextUtils.isEmpty(toolsInforList.get(0).getString("bdz_name")) ? toolsInforList.get(0).getString("dept_name") : toolsInforList.get(0).getString("bdz_name");
//                            toolsBinding.titleInclude.tvTitle.setText(title);
//                        } else {
                        String title = getIntent().getStringExtra(Config.TITLE_NAME);
                        if (!TextUtils.isEmpty(title)) {
                            toolsBinding.titleInclude.tvTitle.setText(title);
                        }
//                        }
                        infoAdapter.setList(toolsInforList);
                    }
                });
            }
        });
    }

    private XsDialogSafetyToolStopBinding stopBinding;
    private Dialog stopDialog;
    private boolean stopClick;

    /**
     * 停用对话框
     */
    public void creatStopDialog() {
        stopBinding = XsDialogSafetyToolStopBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        stopDialog = DialogUtils.createDialog(currentActivity, stopBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        stopDialog.setCanceledOnTouchOutside(false);
        stopBinding.rgStop.setOnCheckedChangeListener(stopDialogCheckListener);
        stopBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDialog.dismiss();
                if (!isBatchOperate) {
                    selectmodels.clear();
                }
            }
        });
        stopBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stopBinding.rbUnqualify.isChecked() && !stopBinding.rbInperiod.isChecked()) {
                    CToast.showShort(currentActivity, "请选择工器具停用的原因");
                    return;
                }
                String reason = stopBinding.etInputReason.getText().toString().trim();
                if (View.VISIBLE == stopBinding.etInputReason.getVisibility() && TextUtils.isEmpty(reason)) {
                    CToast.showShort(currentActivity, "请填写工器具不合格的原因");
                    return;
                }
                if (TextUtils.isEmpty(stopBinding.etInputStopperson.getText().toString())) {
                    CToast.showShort(currentActivity, "请输入操作人员姓名");
                    return;
                }
                stopDialog.dismiss();
                isSelectAll = false;
                infoAdapter.setSelectedAll(isSelectAll);
                infoAdapter.setClearCheckMap();
                toolsBinding.cbAll.setChecked(false);
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
                stopStatus = Config.ToolStatus.inTest.name();
                stopBinding.txtTips.setVisibility(View.GONE);

            } else if (checkedId == R.id.rb_overdate) {// TODO: 2017/7/6

            } else if (checkedId == R.id.rb_unqualify) {
                stopStatus = Config.ToolStatus.unNormal.name();
                stopBinding.etInputReason.setVisibility(View.VISIBLE);
                stopBinding.txtTips.setVisibility(View.VISIBLE);

            } else {
            }
        }
    };

    public void creatTestDialog() {
        testBinding = XsDialogSafetyToolTestBinding.inflate(getLayoutInflater());
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        testDialog = DialogUtils.createDialog(currentActivity, testBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        testDialog.setCanceledOnTouchOutside(false);
        testBinding.rgTest.setOnCheckedChangeListener(testChangeListener);
        testBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testBinding.txtTips.setVisibility(View.GONE);
                testDialog.dismiss();
                if (!isBatchOperate) {
                    selectmodels.clear();
                }
            }
        });
        testBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!testBinding.rbNormal.isChecked() && !testBinding.rbUnnormal.isChecked()) {
                    CToast.showShort(currentActivity, "请选择试验结果");
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
                        testBinding.txtTestTime.setText(result);
                    }
                });
            }
        });
        testBinding.ibtnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtils.takePicture(currentActivity, currentImageName = toolPicPath + FunctionUtils.getCurrentImageName(), Config.BDZ_INSPECTION_FOLDER);
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
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap currentBitmap = BitmapUtil.createScaledBitmapByWidth(BitmapUtil.postRotateBitmap(Config.BDZ_INSPECTION_FOLDER + currentImageName), ScreenUtils.getScreenWidth(currentActivity));
                if (null == currentBitmap) {
                    CToast.showShort(currentActivity, "很抱歉，因意外需要您再次拍照。");
                    return;
                }
                currentBitmap = BitmapUtil.addText2Bitmap(currentBitmap, DateUtils.getCurrentTime(CoreConfig.dateFormat2), getResources().getDimensionPixelOffset(R.dimen.xs_global_text_size));
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
     * 停用保存数据
     */
    private void saveStopData() {
        final List<OperateToolResult> resultList = new ArrayList<>();
        if (selectmodels.size() > 50) {
            CustomerDialog.showProgress(currentActivity);
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (DbModel dbModel : selectmodels) {
                    String toolId = dbModel.getString("id");
                    String name = dbModel.getString("name");
                    String reason = stopBinding.etInputReason.getText().toString().trim();
                    String stopPerson = stopBinding.etInputStopperson.getText().toString().trim();
                    try {
                        OperateToolResult toolResult = null;
                        if (stopBinding.rbUnqualify.isChecked()) {
                            toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, toolId, name, Config.ToolStatus.stop.name(), Config.ToolStatus.unNormal.name(), reason, stopPerson);
                            XunshiApplication.getDbUtils().update(SafeToolsInfor.class, WhereBuilder.b("id", "=", toolId), new KeyValue("status", Config.ToolStatus.stop.name()));
                        } else {
                            toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, toolId, name, Config.ToolStatus.stop.name(), Config.ToolStatus.normal.name(), reason, stopPerson);
                            XunshiApplication.getDbUtils().update(SafeToolsInfor.class, WhereBuilder.b("id", "=", toolId), new KeyValue("status", Config.ToolStatus.inTest.name()));
                        }
                        resultList.add(toolResult);
                        SafeToolsInfoService.getInstance().saveOrUpdate(resultList);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomerDialog.dismissProgress();
                        selectmodels.clear();
                        infoAdapter.setSelectedAll(false);
                        infoAdapter.setClearCheckMap();
                        infoAdapter.notifyDataSetChanged();
                        isSelectAll = false;
                        toolsBinding.cbAll.setChecked(isSelectAll);
                        initData(selectItem);
                    }
                });
            }
        });
    }

    /**
     * 保存试验的结果
     */
    private String nextTime;

    public void saveTestData() {
        final String pics = StringUtils.ArrayListToString(reportPicList);
        final String person = testBinding.etInputPerson.getText().toString().trim();
        final String time = testBinding.txtTestTime.getText().toString().trim();
        final List<OperateToolResult> resultList = new ArrayList<>();
        if (selectmodels.size() > 50) {
            CustomerDialog.showProgress(currentActivity);
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (DbModel dbModel : selectmodels) {
                    String id = dbModel.getString("id");
                    String name = dbModel.getString("name");
                    OperateToolResult toolResult = null;
                    try {
                        if (testBinding.rbNormal.isChecked()) {
                            toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, id, name, time, Config.ToolStatus.normal.name(), pics, person, Config.ToolStatus.test.name());
                            String lastTime = testBinding.txtTestTime.getText().toString().trim() + " 00:00:00";
                            if (TextUtils.isEmpty(dbModel.getString("period"))) {
                                CToast.showShort(currentActivity, "请配置试验周期");
                                continue;
                            }
                            if (dbModel.getString("period").equalsIgnoreCase("0")) {
                                nextTime = null;
                            } else {
                                nextTime = DateUtils.getAfterMonth(lastTime, Integer.valueOf(dbModel.getString("period")));
                            }
                            XunshiApplication.getDbUtils().update(SafeToolsInfor.class, WhereBuilder.b("id", "=", id).and("dlt", "<>", "1"), new KeyValue("status", Config.ToolStatus.normal.name()), new KeyValue("isnormal", Config.ToolStatus.normal.name()),
                                    new KeyValue("lastly_check_time", lastTime), new KeyValue("next_check_time", nextTime));
                        } else if (testBinding.rbUnnormal.isChecked()) {
                            toolResult = new OperateToolResult(currentReportId, currentBdzName, currentBdzId, id, name, time, Config.ToolStatus.unNormal.name(), pics, person, Config.ToolStatus.test.name());
                            XunshiApplication.getDbUtils().update(SafeToolsInfor.class, WhereBuilder.b("id", "=", id),new KeyValue("status", Config.ToolStatus.stop.name()), new KeyValue("isnormal", Config.ToolStatus.unNormal.name()));
                        }
                        resultList.add(toolResult);
                        SafeToolsInfoService.getInstance().saveOrUpdate(resultList);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CustomerDialog.dismissProgress();
                        selectmodels.clear();
                        infoAdapter.setSelectedAll(false);
                        infoAdapter.setClearCheckMap();
                        infoAdapter.notifyDataSetChanged();
                        isSelectAll = false;
                        toolsBinding.cbAll.setChecked(isSelectAll);
                        initData(selectItem);
                    }
                });
            }
        });

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

    /**
     * 试验拍照后操作imageview
     */

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
    public void onClick(View v, Object data, int position) {
        stopDbmodel = (DbModel) data;
        String selectPersons = PreferencesUtils.getString(getApplicationContext(), Config.SELECT_PERSONS, "");
        int i = v.getId();
        if (i == R.id.txt_stop) {
            CharSequence txtToolName = StringUtils.formatPartTextColor("请选择停用%s的原因", currentActivity.getResources().getColor(R.color.xs_color_5dbf19), "工器具");
            stopBinding.txtToolName.setText(txtToolName);
            selectmodels.add(stopDbmodel);
            stopBinding.rgStop.clearCheck();
            stopBinding.etInputReason.setText("");
            stopBinding.txtTips.setVisibility(View.GONE);
            stopBinding.etInputStopperson.setText(selectPersons);
            stopDialog.show();

        } else if (i == R.id.txt_test) {
            selectmodels.add(stopDbmodel);
            testBinding.txtPicCount.setVisibility(View.GONE);
            reportPicList.clear();
            testBinding.txtTestTime.setText(DateUtils.getCurrentShortTime());
            testBinding.rgTest.clearCheck();
            testBinding.etInputPerson.setText("");
            testBinding.ivReportPhoto.setImageBitmap(null);
            testBinding.txtTips.setVisibility(View.GONE);
            testBinding.etInputPerson.setText(selectPersons);
            testDialog.show();

        } else if (i == R.id.cb_selected || i == R.id.ll_root) {
            if (isShowCbox) {
                infoAdapter.setCheckMap(v, selectmodels);
                if (infoAdapter.getSelecCBMap().size() < toolsInforList.size()) {
                    infoAdapter.setSelectedAll(false);
                    toolsBinding.cbAll.setChecked(false);
                    isSelectAll = false;
                } else {
                    infoAdapter.setSelectedAll(true);
                    toolsBinding.cbAll.setChecked(true);
                    isSelectAll = true;
                }
            } else {
                Intent intent = new Intent(currentActivity, SafeToolsInformationActivity.class);
                DbModel dbModel = toolsInforList.get(position);
                String title = dbModel.getString("name");
                intent.putExtra("title", title);
                intent.putExtra(SafeToolsInfor.ID, dbModel.getString("id"));
                intent.putExtra(Bdz.BDZID, currentBdzId);
                intent.putExtra(Department.DEPT_ID, deptId);
                startActivity(intent);
            }

        } else {
        }
    }


    /**
     * @param view activity 控件点击事件
     *             批量操作、试验、停用、全选、取消
     */

    public void show(View view) {
        int selectToolCount = 0;
        String selectPersons = PreferencesUtils.getString(getApplicationContext(), Config.SELECT_PERSONS, "");
        int i = view.getId();
        if (i == R.id.btn_stop) {
            selectToolCount = selectmodels.size();
            CharSequence txtToolName = StringUtils.formatPartTextColor("请选择停用%s的原因", currentActivity.getResources().getColor(R.color.xs_color_5dbf19), String.valueOf(selectToolCount) + "个工器具");
            stopBinding.txtToolName.setText(txtToolName);
            stopBinding.rgStop.clearCheck();
            stopBinding.etInputReason.setText("");
            stopBinding.etInputStopperson.setText(selectPersons);
            if (!selectmodels.isEmpty()) {
                stopBinding.txtTips.setVisibility(View.GONE);
                stopDialog.show();
            } else {
                CToast.showShort(currentActivity, "请选择需要停用的工器具！");
            }

        } else if (i == R.id.btn_test) {
            if (!isShowCbox) {
                toolsBinding.btnStop.setVisibility(View.VISIBLE);
                toolsBinding.btnTest.setText("试 验");
                toolsBinding.llCbContainer.setVisibility(View.VISIBLE);
                toolsBinding.btnCancel.setVisibility(View.VISIBLE);
                infoAdapter.setRootViewDrag(false);
                infoAdapter.setSelected(!isShowCbox);
                isShowCbox = !isShowCbox;
                isBatchOperate = true;
            } else if (!selectmodels.isEmpty()) {
                testBinding.txtPicCount.setText("");
                testBinding.txtPicCount.setVisibility(View.GONE);
                reportPicList.clear();
                testBinding.txtTestTime.setText(DateUtils.getCurrentShortTime());
                testBinding.rgTest.clearCheck();
                testBinding.etInputPerson.setText("");
                testBinding.etInputPerson.setText(selectPersons);
                testBinding.ivReportPhoto.setImageBitmap(null);
                testBinding.txtTips.setVisibility(View.GONE);
                testDialog.show();
            } else {
                CToast.showShort(currentActivity, "请选择需要试验的工器具！");
            }

        } else if (i == R.id.btn_cancel) {
            toolsBinding.btnCancel.setVisibility(View.GONE);
            toolsBinding.btnStop.setVisibility(View.GONE);
            toolsBinding.btnTest.setText("批量操作");
            toolsBinding.cbAll.setChecked(false);
            toolsBinding.llCbContainer.setVisibility(View.GONE);
            infoAdapter.setClearCheckMap();
            infoAdapter.setSelectedAll(false);
            infoAdapter.setRootViewDrag(true);
            infoAdapter.setSelected(!isShowCbox);
            isShowCbox = !isShowCbox;
            isSelectAll = false;
            selectmodels.clear();
            isBatchOperate = false;

        } else if (i == R.id.cb_all) {
            isShowCbox = true;
            if (!isSelectAll) {
                selectmodels.clear();
                selectmodels.addAll(toolsInforList);
            } else {
                infoAdapter.setClearCheckMap();
                selectmodels.clear();
            }
            infoAdapter.setSelectedAll(!isSelectAll);
            infoAdapter.setSelected(isShowCbox);
            isSelectAll = !isSelectAll;

        } else {
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstEnter) {
            initData(selectItem);
        }
        isFirstEnter = false;
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
            userName = StringUtils.ArrayListToString(usersList);
            PreferencesUtils.put(getApplicationContext(), Config.SELECT_PERSONS, userName);
        }
        if (stopClick) {
            stopBinding.etInputStopperson.setText(userName);
        } else {
            testBinding.etInputPerson.setText(userName);
        }

        PersonListUtils.getInsance().disMissPopWindow();

    }
}
