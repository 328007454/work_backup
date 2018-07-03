package com.cnksi.workticket.activity;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.workticket.CalculateDateWork;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;
import com.cnksi.workticket.base.TicketBaseActivity;
import com.cnksi.workticket.bean.Department;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.databinding.ActivityTicketDateBinding;
import com.cnksi.workticket.databinding.TicketDialogTipsBinding;
import com.cnksi.workticket.databinding.TicketWeekDateBinding;
import com.cnksi.workticket.db.BdzService;
import com.cnksi.workticket.db.WorkTicketDbManager;
import com.cnksi.workticket.db.WorkTicketOrderService;
import com.cnksi.workticket.enum_ticket.TicketStatusEnum;
import com.cnksi.workticket.enum_ticket.TicketTimeEnum;
import com.cnksi.workticket.sync.KSyncConfig;
import com.cnksi.workticket.util.DialogUtil;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mr.K 2018/5/11
 * @decrption 该类主要是进行工作预约
 */
public class TicketDateWorkActivity extends TicketBaseActivity {

    private ActivityTicketDateBinding dateBinding;
    private List<DbModel> models;
    private String bdzId;
    private String selectDate;
    private Department department;
    private List<WorkTicketOrder> orders = new ArrayList<>();

    private String a = "A", b = "B";

    private String bdzName;
    /**
     * 预约成功后的对话框
     */
    private Dialog successDialog;

    /**
     * 选择工作类型
     */
    private String selectType;
    /**
     * 开票类型
     */
    private String ticketType = "";
    /**
     * 选择时间区间
     */
    private String seletTimeZone;
    /**
     * 时间区对应的key
     */
    private String selectTimeZoneKey;
    /**
     * 是否选择了时间区间
     */
    private boolean isSelectTimeZone;

    /**
     * 点击继续按钮是预留的状态
     */
    private String goOn = "goon";

    /**
     * 点击保存时候按钮的预留状态
     */
    private String save = "save";
    private boolean isSaved = true;
    private String otherDeptUser = "other_dept_user";
    private boolean isNeedRefreshTimeZone = true;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date;
    }

    @Override
    public void initUI() {
        dateBinding = (ActivityTicketDateBinding) rootDataBinding;
        if (TextUtils.equals(Config.otherDeptUser, otherDeptUser)) {
            dateBinding.rbOtherType.setVisibility(View.GONE);
        } else {
            dateBinding.rbKaiTyppe.setVisibility(View.GONE);
            dateBinding.rbJieType.setVisibility(View.GONE);
            RadioGroup.LayoutParams rl = (RadioGroup.LayoutParams) dateBinding.rbOtherType.getLayoutParams();
            rl.leftMargin = 0;
            dateBinding.rbOtherType.setLayoutParams(rl);
            dateBinding.rbOtherType.setChecked(true);
            ticketType = TicketStatusEnum.other.name();
        }
        initClick();
    }

    @Override
    public void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                department = BdzService.getInstance().getCurrentDept(Config.deptID);
                models = BdzService.getInstance().getBdzData(Config.deptID, Config.otherDeptUser);
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if (TextUtils.isEmpty(department.name)) {
                    dateBinding.txtDeptName.setText(Config.deptName);
                } else {
                    dateBinding.txtDeptName.setText(department.name);
                }
            });
        });
    }


    public void initClick() {
        dateBinding.rgSelectTime.setOnCheckedChangeListener(checkedChangeListener);
        dateBinding.rgTicketType.setOnCheckedChangeListener(checkedChangeListener);
        dateBinding.rgWorkType.setOnCheckedChangeListener(checkedChangeListener);

        dateBinding.txtBdzName.setOnClickListener(v -> new DialogUtil().initBdzDialog(this, models == null ? new ArrayList<>() : models, (view, item, position) -> {
            ToastUtils.showMessage(models.get(position).getString("name"));
            bdzName = models.get(position).getString("name");
            if (!TextUtils.isEmpty(Config.otherDeptUser) && Config.OTHER_DEPT_USER.equalsIgnoreCase(Config.otherDeptUser)) {
                department = BdzService.getInstance().getCurrentDept(models.get(position).getString("dept_id"));
            }
            dateBinding.txtBdzName.setText(models.get(position).getString("name"));
            bdzId = models.get(position).getString("bdzid");
            if (!TextUtils.isEmpty(selectDate)) {
                caculateDataCanBeSaved();
            }
        }));

        dateBinding.ibtnSelectTime.setOnClickListener(v -> showWeekWorkDate());

        dateBinding.save.setOnClickListener(v -> {
            if (!checkInputAllInfor()) {
                return;
            }
            if (isSaved) {
                isSaved = false;
                saveData(save);
            } else {
                ToastUtils.showMessage("正在保存");
            }
        });

        dateBinding.goon.setOnClickListener(v -> {
            if (!checkInputAllInfor()) {
                return;
            }
            if (isSaved) {
                isSaved = false;
                saveData(goOn);
            } else {
                ToastUtils.showMessage("正在保存");
            }
        });

        dateBinding.includeTitle.ticketBack.setOnClickListener(v -> onBackPressed());

        dateBinding.txtSelectTime.setOnClickListener(v -> {
            if (TextUtils.isEmpty(bdzId) || TextUtils.isEmpty(selectType)) {
                ToastUtils.showMessage("请选择变电站和工作类型");
                return;
            }
            new DialogUtil().showDatePickerDialog(this, (result, position) -> {
                boolean sureTime = !TextUtils.isEmpty(result) && DateUtils.compareDate(result, DateUtils.getCurrentShortTime(), DateUtils.yyyy_MM_dd);
                if (!sureTime) {
                    ToastUtils.showMessage("选择时间小于当前时间请重新选择");
                    return;
                }
                dateBinding.rgSelectTime.clearCheck();
                selectDate = result;
                seletTimeZone = "";
                dateBinding.txtSelectTime.setText("时间及日期:  " + result);
                orders = WorkTicketOrderService.getInstance().getSelectDateOrders(department.id, selectDate, Config.deptID);
                caculateDataCanBeSaved();
            });
        });

        dateBinding.rbKaiTyppe.setOnClickListener(v -> {
            if (dateBinding.rbKaiTyppe.isChecked()) {
                if (!ticketType.contains(TicketStatusEnum.kp.name())) {
                    ticketType = ticketType + TicketStatusEnum.kp.name() + ",";
                }
            } else {
                if (ticketType.contains(TicketStatusEnum.kp.name())) {
                    ticketType = ticketType.replace(TicketStatusEnum.kp.name() + ",", "");
                }
            }
        });
        dateBinding.rbJieType.setOnClickListener(v -> {
            if (dateBinding.rbJieType.isChecked()) {
                if (!ticketType.contains(TicketStatusEnum.jp.name())) {
                    ticketType = ticketType + TicketStatusEnum.jp.name() + ",";
                }
            } else {
                if (ticketType.contains(TicketStatusEnum.jp.name())) {
                    ticketType = ticketType.replace(TicketStatusEnum.jp.name() + ",", "");
                }
            }
        });
        dateBinding.rbOtherType.setOnClickListener(v -> {
            if (dateBinding.rbOtherType.isChecked()) {
                if (!ticketType.contains(TicketStatusEnum.other.name())) {
                    ticketType = ticketType + TicketStatusEnum.other.name() + ",";
                }
            } else {
                if (ticketType.contains(TicketStatusEnum.other.name())) {
                    ticketType = ticketType.replace(TicketStatusEnum.other.name() + ",", "");
                }
            }
            setSelectTimeEnAbled();
        });

        dateBinding.includeTitle.ibtSync.setOnClickListener(v -> {
            CustomerDialog.showProgress(this, "正在同步数据，请确保网络畅通");
            KSyncConfig.getInstance().setFailListener(syncSuccess -> {
                if (!syncSuccess) {
                    ToastUtils.showMessage("同步失败");
                } else {
                    ToastUtils.showMessage("同步成功，请继续操作");
                }
                CustomerDialog.dismissProgress();
            }).upload().downLoad();
        });
    }

    Dialog weekDateWorkDialog;


    private void showWeekWorkDate() {
        TicketWeekDateBinding dateBinding = TicketWeekDateBinding.inflate(LayoutInflater.from(getApplicationContext()));
        weekDateWorkDialog = new DialogUtil().createDialog(this, dateBinding.getRoot(), 939, ViewGroup.LayoutParams.WRAP_CONTENT);
        weekDateWorkDialog.show();
        dateBinding.view.getCurrentWeekDateWork(this);
    }


    private boolean checkInputAllInfor() {
        if (TextUtils.isEmpty(dateBinding.txtPeopleName.getText().toString())) {
            ToastUtils.showMessage("请输入工作负责人");
            return false;
        }

        if (TextUtils.isEmpty(dateBinding.txtConnnectionName.getText().toString())) {
            ToastUtils.showMessage("请输入联系方式");
            return false;
        }

        if (TextUtils.isEmpty(dateBinding.txtContentName.getText().toString())) {
            ToastUtils.showMessage("请输入工作内容");
            return false;
        }

        if (TextUtils.isEmpty(seletTimeZone)) {
            ToastUtils.showMessage("请选择时间区间");
            return false;
        }

        if (TextUtils.isEmpty(ticketType)) {
            ToastUtils.showMessage("请选择开票类型");
            return false;
        }

        return true;
    }

    private void clearAllElement() {
        dateBinding.rgWorkType.clearCheck();
        dateBinding.rgTicketType.clearCheck();
        dateBinding.rgSelectTime.clearCheck();
        bdzId = "";
        seletTimeZone = "";
        selectDate = "";
        dateBinding.txtBdzName.setText("");
        isSelectTimeZone = false;
        dateBinding.txtSelectTime.setText("日期及时间");
        dateBinding.txtContentName.setText("");
    }


    private void saveData(String button) {
        KSyncConfig.getInstance().setFailListener(syncSuccess -> {
            if (syncSuccess) {
                if (TicketStatusEnum.kp.name().equalsIgnoreCase(ticketType)) {
                    orders = WorkTicketOrderService.getInstance().getSelectDateOrders(department.id, selectDate, Config.deptID);
                    caculateDataCanBeSaved();
                    if (!isSelectTimeZone) {
                        ToastUtils.showMessage("该时间段下已经有工作了，请重新选择时间");
                        return;
                    }
                }
                WorkTicketOrder order = new WorkTicketOrder(department.id, bdzId, bdzName, selectType, Config.deptName, dateBinding.txtPeopleName.getText().toString(), dateBinding.txtConnnectionName.getText().toString(),
                        dateBinding.txtContentName.getText().toString(), ticketType, selectDate, selectTimeZoneKey, seletTimeZone, Config.userAccount, Config.userName, Config.deptID);
                try {
                    WorkTicketDbManager.getInstance().getTicketManager().saveOrUpdate(order);
                    showDateSuccessDialog(button);
                    upLoadData();
                } catch (DbException e) {
                    e.printStackTrace();
                    isSaved = true;
                }
            } else {
                ToastUtils.showMessage("同步失败，无法保存本次数据，请确保网络畅通");
                isSaved = true;
            }
        });
        KSyncConfig.getInstance().getKNConfig(getApplicationContext()).downLoad();
    }


    private void showDateSuccessDialog(String button) {
        TicketDialogTipsBinding tipsBinding = TicketDialogTipsBinding.inflate(LayoutInflater.from(getApplicationContext()));
        tipsBinding.txtTitle.setText("预约成功提示");
        tipsBinding.imgTips.setBackgroundResource(R.mipmap.ticket_date_success);
        tipsBinding.txtTips.setText("您已成功预约，请安排好工作，临时安排请提前告知，谢谢");
        tipsBinding.yes.setText("确定");
        tipsBinding.no.setText("关闭");
        successDialog = new DialogUtil().createDialog(this, tipsBinding.getRoot(), ScreenUtils.getScreenWidth(this) * 7 / 9, ViewGroup.LayoutParams.WRAP_CONTENT);
        successDialog.setCanceledOnTouchOutside(false);
        tipsBinding.no.setOnClickListener(v -> {
            if (TextUtils.equals(button, goOn)) {
                clearAllElement();
            } else if (TextUtils.equals(button, save)) {
                finish();
            }
            isSaved = true;
            successDialog.dismiss();
        });

        tipsBinding.yes.setOnClickListener(v -> {
            if (TextUtils.equals(button, goOn)) {
                clearAllElement();
            } else if (TextUtils.equals(button, save)) {
                finish();
            }
            isSaved = true;
            successDialog.dismiss();
        });
        successDialog.show();
    }


    private void upLoadData() {

        KSyncConfig.getInstance().setFailListener(syncSuccess -> {
            CustomerDialog.dismissProgress();
            if (syncSuccess) {
                ToastUtils.showMessage("数据同步成功");
            } else {
                ToastUtils.showMessage("数据同步失败");
            }
        }).upload();
    }

    HashMap<String, Integer> deptWorkA = new HashMap<>();
    HashMap<String, Integer> deptWorkB = new HashMap<>();
    HashMap<String, Integer> currentBdzWorkA = new HashMap<>();
    HashMap<String, Integer> currentBdzWorkB = new HashMap<>();

    /**
     * 计算所选的变电站以及类型是否能够被保存
     */
    private void caculateDataCanBeSaved() {
        deptWorkA.clear();
        deptWorkB.clear();
        currentBdzWorkA.clear();
        currentBdzWorkB.clear();
        for (WorkTicketOrder order : orders) {
            if (a.equalsIgnoreCase(order.workType)) {
                CalculateDateWork.getInstance().setWorkTypeCaculate(deptWorkA, order, currentBdzWorkA, dateBinding, bdzId);
            } else if (b.equalsIgnoreCase(order.workType)) {
                CalculateDateWork.getInstance().setWorkTypeCaculate(deptWorkB, order, currentBdzWorkB, dateBinding, bdzId);
            }
        }
        CalculateDateWork.getInstance().refreshSelectTimeStatus(deptWorkA, deptWorkB, currentBdzWorkA, currentBdzWorkB, department, selectType, dateBinding);
    }


    private RadioGroup.OnCheckedChangeListener checkedChangeListener = (RadioGroup radioGroup, int i) -> {
        int i1 = radioGroup.getId();
        if (i1 == R.id.rg_select_time) {
            if (TextUtils.isEmpty(selectDate)) {
                ToastUtils.showMessage("请先选择时间");
                return;
            }
            getSelectTimeGroup(i);
        } else if (i1 == R.id.rg_work_type) {
            selectType = i == R.id.rb_a_typpe ? a : b;
            if (!TextUtils.isEmpty(selectDate)) {
                caculateDataCanBeSaved();
            }
        }
    };

    private void setSelectTimeEnAbled() {
        dateBinding.txtTime1.setEnabled(true);
        dateBinding.txtTime2.setEnabled(true);
        dateBinding.txtTime3.setEnabled(true);
        dateBinding.txtTime4.setEnabled(true);
        dateBinding.txtTime5.setEnabled(true);
        dateBinding.rgSelectTime.clearCheck();
    }

    private void getSelectTimeGroup(int id) {
        isSelectTimeZone = true;
        if (id == R.id.txt_time1) {
            seletTimeZone = TicketTimeEnum.region_10to11.value;
            selectTimeZoneKey = TicketTimeEnum.region_10to11.name();
        } else if (id == R.id.txt_time2) {
            seletTimeZone = TicketTimeEnum.region_11to12.value;
            selectTimeZoneKey = TicketTimeEnum.region_11to12.name();
        } else if (id == R.id.txt_time3) {
            seletTimeZone = TicketTimeEnum.region_14to15.value;
            selectTimeZoneKey = TicketTimeEnum.region_14to15.name();
        } else if (id == R.id.txt_time4) {
            seletTimeZone = TicketTimeEnum.region_15to16.value;
            selectTimeZoneKey = TicketTimeEnum.region_15to16.name();
        } else if (id == R.id.txt_time5) {
            seletTimeZone = TicketTimeEnum.region_16to17.value;
            selectTimeZoneKey = TicketTimeEnum.region_16to17.name();
        }

    }

}
