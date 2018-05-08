package com.cnksi.workticket.activity;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.RadioGroup;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.R;
import com.cnksi.workticket.base.TicketBaseActivity;
import com.cnksi.workticket.bean.Department;
import com.cnksi.workticket.bean.LookUpLocal;
import com.cnksi.workticket.bean.WorkTicketOrder;
import com.cnksi.workticket.databinding.ActivityTicketDateBinding;
import com.cnksi.workticket.databinding.ActivityTicketDateWorkBinding;
import com.cnksi.workticket.db.BdzService;
import com.cnksi.workticket.db.WorkTicketDbManager;
import com.cnksi.workticket.db.WorkTicketOrderService;
import com.cnksi.workticket.enum_ticket.TicketEnum;
import com.cnksi.workticket.enum_ticket.TicketStatusEnum;
import com.cnksi.workticket.enum_ticket.TicketTimeEnum;
import com.cnksi.workticket.sync.KSyncConfig;
import com.cnksi.workticket.util.DialogUtil;
import com.cnksi.workticket.view.TicketDateSelectDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;


public class TicketDateWorkActivity extends TicketBaseActivity {

    private ActivityTicketDateBinding dateBinding;
    private List<DbModel> models;
    private String bdzId;
    private String selectDate;
    private Department department;
    private List<WorkTicketOrder> orders = new ArrayList<>();
    private String bdzName;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_ticket_date;
    }

    @Override
    public void initUI() {
        dateBinding = (ActivityTicketDateBinding) rootDataBinding;
        dateBinding.txtDeptName.setText(TextUtils.isEmpty(Config.deptName) ? "" : Config.deptName);
        initClick();
    }

    @Override
    public void initData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                department = BdzService.getInstance().getCurrentDept(Config.deptID);
                models = BdzService.getInstance().getBdzData(Config.deptID);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }


    public void initClick() {
        dateBinding.rgSelectTime.setOnCheckedChangeListener(checkedChangeListener);
        dateBinding.rgTicketType.setOnCheckedChangeListener(checkedChangeListener);
        dateBinding.rgWorkType.setOnCheckedChangeListener(checkedChangeListener);

        dateBinding.txtBdzName.setOnClickListener(v -> {
            new DialogUtil().initBdzDialog(this, models == null ? new ArrayList<DbModel>() : models, (view, item, position) -> {
                ToastUtils.showMessage(models.get(position).getString("name"));
                bdzName = models.get(position).getString("name");
                dateBinding.txtBdzName.setText(models.get(position).getString("name"));
                bdzId = models.get(position).getString("bdzid");
                if (!TextUtils.isEmpty(selectDate)) {
                    caculateDataCanBeSaved();
                }
            });
        });

        dateBinding.ibtnSelectTime.setOnClickListener(v -> {
            if (TextUtils.isEmpty(bdzId) || TextUtils.isEmpty(selectType)) {
                ToastUtils.showMessage("请选择变电站和工作类型");
                return;
            }
            new DialogUtil().showDatePickerDialog(this, (result, position) -> {
                selectDate = result;
                dateBinding.txtSelectTime.setText("时间及日期:  " + result);
                orders = WorkTicketOrderService.getInstance().getSelectDateOrders(Config.deptID, selectDate);
                caculateDataCanBeSaved();
            });
        });

        dateBinding.save.setOnClickListener(v -> {
            if (TextUtils.isEmpty(seletTimeZone)) {
                ToastUtils.showMessage("请选择时间区间");
                return;
            }
            saveData("save");
        });

        dateBinding.goon.setOnClickListener(v -> {
            if (TextUtils.isEmpty(seletTimeZone)) {
                ToastUtils.showMessage("请选择时间");
                return;
            }
            saveData("goon");
        });

    }

    private void clearAllElement() {
        dateBinding.rgWorkType.clearCheck();
        dateBinding.rgTicketType.clearCheck();
        dateBinding.rgSelectTime.clearCheck();
        bdzId = "";
        dateBinding.txtBdzName.setText("");
        isSelectTimeZone = false;
        dateBinding.txtSelectTime.setText("日期及时间");
    }


    private void saveData(String button) {
        KSyncConfig.getInstance().setFailListener(syncSuccess -> {
            if (syncSuccess) {
                caculateDataCanBeSaved();
                if (!isSelectTimeZone) {
                    ToastUtils.showMessage("所选时间区冲突，请重新选择");
                    return;
                }
                WorkTicketOrder order = new WorkTicketOrder(Config.deptID, bdzId, bdzName, selectType, Config.deptName, dateBinding.txtPeopleName.getText().toString(), dateBinding.txtConnnectionName.getText().toString(),
                        dateBinding.txtContentName.getText().toString(), ticketType, selectDate, selectTimeZoneKey, seletTimeZone, Config.userAccount, Config.userName);
                try {
                    WorkTicketDbManager.getInstance().getTicketManager().saveOrUpdate(order);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals(button, "goon")) {
                    clearAllElement();
                } else if (TextUtils.equals(button, "save")) {
                    finish();
                }
            } else {
                ToastUtils.showMessage("同步失败，无法保存本次数据，请确保网络畅通");
            }
        });
        KSyncConfig.getInstance().getKNConfig(getApplicationContext()).downLoad();
    }

    HashMap<String, Integer> deptWorkA = new HashMap<>();
    HashMap<String, Integer> deptWorkB = new HashMap<>();
    HashMap<String, Integer> currentBdzWorkA = new HashMap<>();
    HashMap<String, Integer> currentBdzWorkB = new HashMap<>();

    private void caculateDataCanBeSaved() {
        deptWorkA.clear();
        deptWorkB.clear();
        currentBdzWorkA.clear();
        currentBdzWorkB.clear();
        for (WorkTicketOrder order : orders) {
            if ("A".equalsIgnoreCase(order.workType)) {
                setWorkTypeCaculate(deptWorkA, order, currentBdzWorkA);
            } else if ("B".equalsIgnoreCase(order.workType)) {
                setWorkTypeCaculate(deptWorkB, order, currentBdzWorkB);
            }
        }
        refreshSelectTimeStatus();
    }

    public void setWorkTypeCaculate(HashMap<String, Integer> workMap, WorkTicketOrder order, HashMap<String, Integer> bdzWorkTypeMap) {
        if ("region_10to11".equalsIgnoreCase(order.workKey)) {
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }
        } else if ("region_11to12".equalsIgnoreCase(order.workKey)) {
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }

        } else if ("region_14to15".equalsIgnoreCase(order.workKey)) {
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }

            }
        } else if ("region_15to16".equalsIgnoreCase(order.workKey)) {
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }

        } else if ("region_16to17".equalsIgnoreCase(order.workKey)) {
            if (workMap.keySet().contains(order.workKey)) {
                workMap.put(order.workKey, workMap.get(order.workKey) + 1);
            } else {
                workMap.put(order.workKey, 1);
            }
            if (bdzId.equalsIgnoreCase(order.bdzId)) {
                if (bdzWorkTypeMap.keySet().contains(order.workKey)) {
                    bdzWorkTypeMap.put(order.workKey, bdzWorkTypeMap.get(order.workKey) + 1);
                } else {
                    bdzWorkTypeMap.put(order.workKey, 1);
                }
            }
        }

    }

    private void refreshSelectTimeStatus() {
        int to11A = 0, to12A = 0, to15A = 0, to16A = 0, to17A = 0;
        int to11B = 0, to12B = 0, to15B = 0, to16B = 0, to17B = 0;

        int bTo11A = 0, bTo12A = 0, bTo15A = 0, bTo16A = 0, bTo17A = 0;
        int bTo11B = 0, bTo12B = 0, bTo15B = 0, bTo16B = 0, bTo17B = 0;

        if (deptWorkA.containsKey("region_10to11")) {
            to11A = deptWorkA.get("region_10to11");
        }
        if (deptWorkA.containsKey("region_11to12")) {
            to12A = deptWorkA.get("region_11to12");
        }
        if (deptWorkA.containsKey("region_14to15")) {
            to15A = deptWorkA.get("region_14to15");
        }
        if (deptWorkA.containsKey("region_15to16")) {
            to16A = deptWorkA.get("region_15to16");
        }
        if (deptWorkA.containsKey("region_16to17")) {
            to17A = deptWorkA.get("region_16to17");
        }

        if (deptWorkB.containsKey("region_10to11")) {
            to11B = deptWorkB.get("region_10to11");
        }
        if (deptWorkB.containsKey("region_11to12")) {
            to12B = deptWorkB.get("region_11to12");
        }
        if (deptWorkB.containsKey("region_14to15")) {
            to15B = deptWorkB.get("region_14to15");
        }
        if (deptWorkB.containsKey("region_15to16")) {
            to16B = deptWorkB.get("region_15to16");
        }
        if (deptWorkB.containsKey("region_16to17")) {
            to17B = deptWorkB.get("region_16to17");
        }

        if (currentBdzWorkA.containsKey("region_10to11")) {
            bTo11A = currentBdzWorkA.get("region_10to11");
        }
        if (currentBdzWorkA.containsKey("region_11to12")) {
            bTo12A = currentBdzWorkA.get("region_11to12");
        }
        if (currentBdzWorkA.containsKey("region_14to15")) {
            bTo15A = currentBdzWorkA.get("region_14to15");
        }
        if (currentBdzWorkA.containsKey("region_15to16")) {
            bTo16A = currentBdzWorkA.get("region_15to16");
        }
        if (currentBdzWorkA.containsKey("region_16to17")) {
            bTo17A = currentBdzWorkA.get("region_16to17");
        }

        if (currentBdzWorkB.containsKey("region_10to11")) {
            bTo11B = currentBdzWorkB.get("region_10to11");
        }
        if (currentBdzWorkB.containsKey("region_11to12")) {
            bTo12B = currentBdzWorkB.get("region_11to12");
        }
        if (currentBdzWorkB.containsKey("region_14to15")) {
            bTo15B = currentBdzWorkB.get("region_14to15");
        }
        if (currentBdzWorkB.containsKey("region_15to16")) {
            bTo16B = currentBdzWorkB.get("region_15to16");
        }
        if (currentBdzWorkB.containsKey("region_16to17")) {
            bTo17B = currentBdzWorkB.get("region_16to17");
        }
        //10:00-11:00按钮
        if (Integer.valueOf(department.groupCount.trim()) * 2 > (to11B + to11A * 2)) {
            if (selectType.equalsIgnoreCase("A") && bTo11A * 2 + bTo11B +2<= 2) {
                dateBinding.txtTime1.setEnabled(true);
            } else if (selectType.equalsIgnoreCase("B") && bTo11A * 2 + bTo11B +1<= 2) {
                dateBinding.txtTime1.setEnabled(true);
            } else {
                dateBinding.txtTime1.setChecked(false);
                dateBinding.txtTime1.setEnabled(false);
            }
        } else {
            dateBinding.txtTime1.setChecked(false);
            dateBinding.txtTime1.setEnabled(false);
        }
        //11：00-12：00
        if (Integer.valueOf(department.groupCount.trim()) * 2 > (to12B + to12A * 2)) {
            if (selectType.equalsIgnoreCase("A") && 2+bTo12A * 2 + bTo12B <= 2) {
                dateBinding.txtTime2.setEnabled(true);
            } else if (selectType.equalsIgnoreCase("B") && 1+bTo12A * 2 + bTo12B <= 2) {
                dateBinding.txtTime2.setEnabled(true);
            } else {
                dateBinding.txtTime2.setChecked(false);
                dateBinding.txtTime2.setEnabled(false);
            }
        } else {
            dateBinding.txtTime2.setChecked(false);
            dateBinding.txtTime2.setEnabled(false);
        }
        //14：00-15：00
        if (Integer.valueOf(department.groupCount.trim()) * 2 > (to15B + to15A * 2)) {
            if (selectType.equalsIgnoreCase("A") && bTo15A * 2 + bTo15B +2<= 2) {
                dateBinding.txtTime3.setEnabled(true);
            } else if (selectType.equalsIgnoreCase("B") && bTo15A * 2 + bTo15B +1<= 2) {
                dateBinding.txtTime3.setEnabled(true);
            } else {
                dateBinding.txtTime3.setChecked(false);
                dateBinding.txtTime3.setEnabled(false);
            }
        } else {
            dateBinding.txtTime3.setChecked(false);
            dateBinding.txtTime3.setEnabled(false);
        }

        //15：00-16：00
        if (Integer.valueOf(department.groupCount.trim()) * 2 > (to16B + to16A * 2)) {
            if (selectType.equalsIgnoreCase("A") && bTo16A * 2 + bTo16B +2<= 2) {
                dateBinding.txtTime4.setEnabled(true);
            } else if (selectType.equalsIgnoreCase("B") && bTo16A * 2 + bTo16B +1<= 2) {
                dateBinding.txtTime4.setEnabled(true);
            } else {
                dateBinding.txtTime4.setChecked(false);
                dateBinding.txtTime4.setEnabled(false);
            }
        } else {
            dateBinding.txtTime4.setChecked(false);
            dateBinding.txtTime4.setEnabled(false);
        }

        //16：00-17：00
        if (Integer.valueOf(department.groupCount.trim()) * 2 > (to17B + to17A * 2)) {
            if (selectType.equalsIgnoreCase("A") && bTo17A * 2 + bTo17B +2<= 2) {
                dateBinding.txtTime5.setEnabled(true);
            } else if (selectType.equalsIgnoreCase("B") && bTo17A * 2 + bTo17B+1 <= 2) {
                dateBinding.txtTime5.setEnabled(true);
            } else {
                dateBinding.txtTime5.setChecked(false);
                dateBinding.txtTime5.setEnabled(false);
            }
        } else {
            dateBinding.txtTime5.setChecked(false);
            dateBinding.txtTime5.setEnabled(false);
        }
    }

    private String selectType;
    private String ticketType;
    private String seletTimeZone;
    private String selectTimeZoneKey;
    private boolean isSelectTimeZone;
    private RadioGroup.OnCheckedChangeListener checkedChangeListener = (RadioGroup radioGroup, int i) -> {
        int i1 = radioGroup.getId();
        if (i1 == R.id.rg_select_time) {
            if (TextUtils.isEmpty(selectDate)) {
                ToastUtils.showMessage("请先选择时间");
                return;
            }
            getSelectTimeGroup(i);
        } else if (i1 == R.id.rg_ticket_type) {
            if (i == R.id.rb_kai_typpe) {
                ticketType = "kp";
            } else if (i == R.id.rb_jie_type) {
                ticketType = "jp";
            } else if (i == R.id.rb_other_type) {
                ticketType = "other";
            }
        } else if (i1 == R.id.rg_work_type) {
            selectType = i == R.id.rb_a_typpe ? "A" : "B";
            if (!TextUtils.isEmpty(selectDate)) {
                caculateDataCanBeSaved();
            }
        }
    };

    private void getSelectTimeGroup(int id) {
        isSelectTimeZone = true;
        if (id == R.id.txt_time1) {
            ToastUtils.showMessage("10:00-11:00");
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
