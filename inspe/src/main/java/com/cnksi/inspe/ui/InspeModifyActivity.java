package com.cnksi.inspe.ui;

import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.DeviceTypeCheckEntity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeModifyBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TaskExtendEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.db.entity.UserGroupEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.DateFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 创建检查任务
 * <br>
 * 功能说明：创建检查任务，只有主任、专责、组长职责有权创建任务。
 * <ol>
 * 创建的任务类型包含
 * <li><b>班组建设检查:</b>根据班组建设标准检查相应班</li>
 * <li><b>精益化评价:</b>根据设备标准检查相应设备</li>
 * <li><strike><b>设备排查:</b>2017-03-21本期暂时不做</strike></li>
 * </ol>
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:19
 */
public class InspeModifyActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeModifyBinding dataBinding;
    private BaseQuickAdapter adapter;
    private List<DeviceTypeCheckEntity> list = new ArrayList<>();
    private DeviceService deviceService = new DeviceService();
    private TaskService taskService = new TaskService();
    private InspecteTaskEntity taskEntity;// = new InspecteTaskEntity();
    private UserEntity expertUser = getUserService().getUserExpert(RoleType.expert);
    private Set<Integer> checkIds = new HashSet<>();
    private TaskExtendEntity taskExtendEntity;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_modify;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeModifyBinding) rootDataBinding;
        setTitle("选择评价类型", R.drawable.inspe_left_black_24dp);

        adapter = new DeviceTypeAdapter(R.layout.inspe_devicetypes_item, list);
        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        dataBinding.recyclerView.setAdapter(adapter);

        //点击事件
        dataBinding.okBtn.setOnClickListener(this);

    }

    private List<UserGroupEntity> userGroup = getUserService().getUserGroup();
    private List<String> userGroupArray = new ArrayList<>();

    @Override
    public void initData() {
        if (expertUser == null) {
            showToast("你没有权限创建任务！");
            finish();
            return;
        }

        taskEntity = taskService.getTask(getIntent().getStringExtra("task_id"));
        if (taskEntity == null) {
            showToast("参数错误！");
            finish();
            return;
        }
        //第一次修改
        taskExtendEntity = taskService.getTaskExtend(taskEntity.getId(), expertUser.getId());
        if (taskExtendEntity == null) {
            taskExtendEntity = new TaskExtendEntity();
            taskExtendEntity.setTask_id(taskEntity.getId());
            taskExtendEntity.setDlt(0);
            taskExtendEntity.setId(UUID.randomUUID().toString().replace("-", ""));//可能导致记录覆盖
            taskExtendEntity.setPerson_id(expertUser.getId());
            taskExtendEntity.setPerson_name(expertUser.getUsername());
            taskExtendEntity.setInsert_time(DateFormat.dateToDbString(System.currentTimeMillis()));
        }
        if (!TextUtils.isEmpty(taskEntity.getPersion_device_bigid())) {
            String[] ids = taskEntity.getPersion_device_bigid().split(",");
            for (String bigId : ids) {
                checkIds.add(Integer.valueOf(bigId));
            }
        }

        List<DeviceTypeEntity> listTemp = deviceService.getDeviceTypes();
        if (listTemp != null && listTemp.size() > 0) {
            for (DeviceTypeEntity entity : listTemp) {
                list.add(new DeviceTypeCheckEntity(entity, entity.getBigid(), entity.getName(), checkIds.contains(entity.getBigid())));
            }
        }

        dataBinding.checkTotalTxt.setText(String.format("已选(%d)类", checkIds.size()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.okBtn) {//创建任务
            if (checkIds.size() == 0) {
                showToast("请选择评价设备类型");
                return;
            }
            //修改任务执行时间
            taskEntity.setDo_check_time(DateFormat.dateToDbString(System.currentTimeMillis()));
            taskService.updateTask(taskEntity);

            //修改检查设备类型，及任务状态
            taskExtendEntity.setChecked_device_bigid(ArrayInspeUtils.toListIntegerString(checkIds));
            taskExtendEntity.setProgress(TaskProgressType.doing.name());


            if (taskService.updateTaskExtend(taskExtendEntity)) {
                showToast("操作成功");
                finish();
            } else {
                showToast("操作失败！");
            }
        }
    }

    public class DeviceTypeAdapter extends BaseQuickAdapter<DeviceTypeCheckEntity, BaseViewHolder> {
        public DeviceTypeAdapter(int layoutResId, List<DeviceTypeCheckEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DeviceTypeCheckEntity item) {
            helper.setOnCheckedChangeListener(R.id.deviceTypeNameTxt, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.isChecked = b;
                    if (b) {
                        checkIds.add(item.typeId);
                    } else {
                        checkIds.remove(Integer.valueOf(item.typeId));
                    }
                    dataBinding.checkTotalTxt.setText(String.format("已选(%d)类", checkIds.size()));
                }
            });
            helper.setChecked(R.id.deviceTypeNameTxt, item.isChecked);
            helper.setText(R.id.deviceTypeNameTxt, item.typeName);
        }
    }

}
