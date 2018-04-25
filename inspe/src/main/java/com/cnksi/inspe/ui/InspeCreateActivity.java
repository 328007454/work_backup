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
import com.cnksi.inspe.databinding.ActivityInspeCreateBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.db.entity.UserGroupEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.widget.PopItemWindow;

import java.util.ArrayList;
import java.util.List;
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
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:19
 */
@Deprecated
public class InspeCreateActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeCreateBinding dataBinding;
    private BaseQuickAdapter adapter;
    private List<DeviceTypeCheckEntity> list = new ArrayList<>();
    private DeviceService deviceService = new DeviceService();
    private TaskService taskService = new TaskService();
    private InspecteTaskEntity task = new InspecteTaskEntity();
    private UserEntity expertUser = getUserService().getUserExpert(RoleType.expert);
    private List<Integer> checkIds = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_create;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeCreateBinding) rootDataBinding;
        setTitle("添加任务", R.drawable.inspe_left_black_24dp);

        List<DeviceTypeEntity> listTemp = deviceService.getDeviceTypes();
        if (listTemp != null && listTemp.size() > 0) {
            for (DeviceTypeEntity entity : listTemp) {
                list.add(new DeviceTypeCheckEntity(entity, entity.getBigid(), entity.getName()));
            }
        }
        adapter = new DeviceTypeAdapter(R.layout.inspe_devicetypes_item, list);
        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        dataBinding.recyclerView.setAdapter(adapter);

        //点击事件
        dataBinding.teamNameTxt.setOnClickListener(this);
        dataBinding.substationTxt.setOnClickListener(this);
        dataBinding.teamLeaderTxt.setOnClickListener(this);
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
        if (userGroup == null || userGroup.size() == 0) {
            showToast("没有查询到运维班组，请先同步数据!");
            finish();
            return;
        }
        for (int i = 0, size = userGroup.size(); i < size; i++) {
            userGroupArray.add(userGroup.get(i).getName());
        }

        task.setId(UUID.randomUUID().toString().replace("-",""));
        task.setType(TaskType.jyhjc.name());//检查类型
        task.setCheck_type(TaskType.jyhjc.getDesc());
        task.setCheckuser_id(expertUser.getId());//检查人ID
        task.setCheckuser_name(expertUser.getUsername());//检查人
        task.setProgress(TaskProgressType.todo.name());//任务状态
        task.setCreate_person_id(expertUser.getUsername());//创建人ID
        task.setCreate_person_name(expertUser.getUsername());//创建人Name
        task.setDlt(0);//未被删除状态
        task.setInsert_time(DateFormat.dateToDbString(System.currentTimeMillis()));//创建时间
        task.setLast_modify_time(DateFormat.dateToDbString(System.currentTimeMillis()));//修改时间
        task.setPlan_check_time(DateFormat.dateToDbString000(System.currentTimeMillis()));//整改时间
        //未知设置
        task.setCity_id("1");//城市ID
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.teamNameTxt) {//运维班组选择
            new PopItemWindow(this).setListAdapter(userGroupArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (!userGroup.get(position).getId().equals(task.getDept_id())) {
                        dataBinding.teamNameTxt.setText(userGroup.get(position).getName());
                        task.setDept_id(userGroup.get(position).getId());//设置被检查班组
                        task.setDept_name(userGroup.get(position).getName());

                        //清除变电站(不同班组变电站不同)
                        dataBinding.substationTxt.setText(null);
                        task.setBdz_id(null);
                        task.setBdz_name(null);
                        dataBinding.substationTxt.performClick();//自动弹出变电站选择
                    }
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);
        } else if (v.getId() == R.id.substationTxt) {//变电站选择
            if (TextUtils.isEmpty(task.getDept_id())) {
                showToast("请先选择运维班组");
                return;
            }
            final List<SubStationEntity> subStations = deviceService.getSubStations(task.getDept_id());
            if (subStations == null || subStations.size() == 0) {
                showToast("没有查询到变电站，请先同步数据!");
                //finish();有可能用户选择了错误的班组，没有数据，故在此不finish，仅做提示。
                return;
            }
            List<String> array = new ArrayList<>();
            for (int i = 0, size = subStations.size(); i < size; i++) {
                array.add(subStations.get(i).getName());
            }

            new PopItemWindow(this).setListAdapter(array).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.substationTxt.setText(subStations.get(position).getName());
                    task.setBdz_id(subStations.get(position).getBdzid());//设置被检查变电站
                    task.setBdz_name(subStations.get(position).getName());

                    if (TextUtils.isEmpty(dataBinding.teamLeaderTxt.getText())) {
                        dataBinding.teamLeaderTxt.performClick();//自动弹出任务组长选择
                    }
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);
        } else if (v.getId() == R.id.teamLeaderTxt) {//检查任务组长选择
            final List<UserEntity> users = getUserService().getUsers(null, null, RoleType.expert);
            if (users == null || users.size() == 0) {
                showToast("没有查询到专家用户，请先同步数据!");
                finish();
                return;
            }
            List<String> array = new ArrayList<>();
            for (int i = 0, size = users.size(); i < size; i++) {
                array.add(users.get(i).getUsername());
            }
            new PopItemWindow(this).setListAdapter(array).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.teamLeaderTxt.setText(users.get(position).getUsername());
                    task.setGroup_person_id(users.get(position).getId()); //设置任务创建人
                    task.setGroup_person_name(users.get(position).getUsername());
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);
        } else if (v.getId() == R.id.okBtn) {//创建任务

            if (TextUtils.isEmpty(dataBinding.teamNameTxt.getText())) {
                dataBinding.teamNameTxt.performClick();
                showToast("请选择运维班组");
                return;
            }

            if (TextUtils.isEmpty(dataBinding.substationTxt.getText())) {
                dataBinding.substationTxt.performClick();
                showToast("请选择变电站");
                return;
            }
            if (TextUtils.isEmpty(dataBinding.teamLeaderTxt.getText())) {
                dataBinding.teamLeaderTxt.performClick();
                showToast("请选择组长");
                return;
            }

            if (checkIds.size() == 0) {
                showToast("请选择评价设备类型");
                return;
            }

            task.setChecked_device_bigid(ArrayInspeUtils.toListIntegerString(checkIds));

            if (createTask(task, 3)) {
                showToast("任务创建成功");
                finish();
            } else {
                showToast("任务创建失败！");
            }
        }
    }

    private boolean createTask(InspecteTaskEntity task, int index) {
        if (index <= 0) {
            return false;
        }
        //考虑到UUID重复情况(极为罕见)，index考虑到执行SQL错误情况，避免导致死循环
        if (!taskService.insert(task)) {
            task.setId(UUID.randomUUID().toString().replace("-",""));
            return createTask(task, index - 1);
        }

        return true;
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
                }
            });
            helper.setChecked(R.id.deviceTypeNameTxt, item.isChecked);
            helper.setText(R.id.deviceTypeNameTxt, item.typeName);
        }
    }

}
