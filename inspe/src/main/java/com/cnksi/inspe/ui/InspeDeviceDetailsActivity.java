package com.cnksi.inspe.ui;

import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.DeviceStandardTypeAdapter;
import com.cnksi.inspe.adapter.PlustekStandardAdapter;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.adapter.entity.PlustekRule0Entity;
import com.cnksi.inspe.adapter.entity.PlustekRule1Entity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityDeviceDetailsBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.PlustekService;
import com.cnksi.inspe.db.entity.DeviceEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 精益化设备详情界面
 * Created by Mr.K on 2018/4/9.
 */

public class InspeDeviceDetailsActivity extends AppBaseActivity implements DeviceStandardTypeAdapter.OnItemClickListener, View.OnClickListener {
    ActivityDeviceDetailsBinding detailsBinding;
    DeviceStandardTypeAdapter typeAdapter;
    List<PlusteRuleEntity> typeModels = new ArrayList<>();
    String deviceId;
    String deviceBigId;
    String taskId;
    DeviceEntity deviceDbModel;
    PlustekType plustekType;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_device_details;
    }

    @Override
    public void initUI() {
        detailsBinding = (ActivityDeviceDetailsBinding) rootDataBinding;
        detailsBinding.includeInspeTitle.toolbarTitle.setText(getIntent().getStringExtra("deviceName"));
        detailsBinding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        DeviceService service = new DeviceService();
        PlustekService plustekService = new PlustekService();
        deviceId = getIntent().getStringExtra("deviceId");
        deviceBigId = getIntent().getStringExtra("deviceBigId");
        taskId = getIntent().getStringExtra("taskId");
        plustekType = (PlustekType) getIntent().getSerializableExtra("plustek_type");

        ExecutorManager.executeTaskSerially(() -> {
            deviceDbModel = service.getDeviceById(deviceId);
            //typeModels = plustekService.getPlusteRule(deviceBigId, plustekType);//level=1，没有check_type,为了方便显示数据
            typeModels = plustekService.getPlusteRule(deviceBigId, null);

            runOnUiThread(() -> {
                refreshUI();
                if (typeModels != null && typeModels.size() > 0) {
                    PlusteRuleEntity dbModel = typeModels.get(0);
                    plusteRuleEntity1 = dbModel;
                    searchData(deviceBigId, dbModel.getId());
                }

            });
        });
        detailsBinding.tvAddNewDefect.setOnClickListener(this);
        initStandardModule();
    }

    /**
     * 刷新设备检查大的类型
     */
    private void refreshUI() {
        String company = deviceDbModel.getManufacturer();
        String productNum = deviceDbModel.getModel();
        String date = deviceDbModel.getCommissioning_date();// TextUtils.isEmpty(deviceDbModel.getString("commissioning_date")) ? "" : deviceDbModel.getString("commissioning_date");
        detailsBinding.tvManufacturers.setText("生产厂家:" + company);
        detailsBinding.tvProductModel.setText("产品型号" + productNum);
        String dateFormat = StringUtils.cleanString(date);
        if (dateFormat.length() > 10) {
            dateFormat = dateFormat.substring(0, 10);
        }
        detailsBinding.tvProductDate.setText("投产日期：" + dateFormat);
        String picPath = deviceDbModel.getChange_pic();// TextUtils.isEmpty(deviceDbModel.getString("change_pic")) ? (TextUtils.isEmpty(deviceDbModel.getString("pic")) ? "" : deviceDbModel.getString("pic")) : deviceDbModel.getString("pic");
        if (TextUtils.isEmpty(picPath)) {
            picPath = deviceDbModel.getPic();
        }
        detailsBinding.ivDeviceImage.setImageBitmap(BitmapUtils.getImageThumbnail(getPic(picPath), 480, 330));
        typeAdapter = new DeviceStandardTypeAdapter(R.layout.inspe_item_device_part_, typeModels);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        detailsBinding.devicePartRecy.setLayoutManager(layoutManager);
        detailsBinding.devicePartRecy.setAdapter(typeAdapter);
        typeAdapter.setOnItemClickListener(this);
    }

    // 判断设备的图片是否存在，不存在采用默认的图片
    public String getPic(String pic) {
        if (!TextUtils.isEmpty(pic) && new File(Config.BDZ_INSPECTION_FOLDER + pic).exists()) {
            pic = Config.BDZ_INSPECTION_FOLDER + pic;
        } else {
            pic = Config.DEFALUTFOLDER + "device_pic.png";
        }
        return pic;
    }

    private PlusteRuleEntity plusteRuleEntity1;

    @Override
    public void onItemClick(View view, PlusteRuleEntity item, int position) {
        plusteRuleEntity1 = item;
        searchData(deviceBigId, item.getId());
    }

    //标准相关
    private PlustekService plustekService = new PlustekService();
    private List<MultiItemEntity> list = new ArrayList<>();
    private PlustekStandardAdapter adapter;

    private void initStandardModule() {
        adapter = new PlustekStandardAdapter(list);
        final GridLayoutManager manager = new GridLayoutManager(this, 1);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == TeamRoleAdapter.TYPE_1 ? 1 : manager.getSpanCount();
            }
        });
        detailsBinding.deviceStandardLv.setAdapter(adapter);
        detailsBinding.deviceStandardLv.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PlustekRule1Entity data = (PlustekRule1Entity) adapter.getData().get(position);
//                adapter.getMulti
                PlusteRuleEntity entity = data.rule;

                Intent intent = new Intent(context, InspePlustekIssueActivity.class);
                intent.putExtra("info_txt", (deviceDbModel.getName() + " " + plusteRuleEntity1.name + "-" + entity.getName()));//设备名称+一级+二级（如:**避雷器 技术资料-安装技术文件）
                intent.putExtra("data", entity);//计算可扣分数
                intent.putExtra("task_id", taskId);//任务ID
                intent.putExtra("device_id", deviceId);//设备ID
                intent.putExtra("plustek_type", plustekType);
                //计算出可以被扣得分值
                intent.putExtra("max_minus", 10f);
                startActivity(intent);

            }
        });
    }

    /**
     * 查询设备标准 level=2
     *
     * @param bigId
     * @param pid
     */
    private void searchData(String bigId, String pid) {
        //List<PlusteRuleEntity> ruleList = plustekService.getPlusteRule(bigId, plustekType, pid);//level=2，没有check_type,为了方便显示数据
        List<PlusteRuleEntity> ruleList = plustekService.getPlusteRule(bigId, null, pid);
        list.clear();
        adapter.notifyDataSetChanged();

        if (ruleList == null) {
            return;
        }

        for (int i = 0, size = ruleList.size(); i < size; i++) {
            PlusteRuleEntity entity = ruleList.get(i);
            PlustekRule0Entity rule0 = new PlustekRule0Entity(entity, i);

            List<PlusteRuleEntity> ruleItemList = plustekService.getPlusteRule(bigId, plustekType, entity.getId());
            for (int j = 0, jSize = ruleItemList.size(); j < jSize; j++) {
                rule0.addSubItem(new PlustekRule1Entity(ruleItemList.get(j), j));
            }

            list.add(rule0);
        }

        adapter.expand(0);
        adapter.notifyDataSetChanged();
//        adapter.expandAll();

    }


    @Override
    protected void onStart() {
        super.onStart();
        updateIssueCount();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_add_new_defect) {
            //访问问题列表
            if (deviceIssueTotal > 0) {
                startActivity(new Intent(context, InspePlustekIssueListActivity.class).putExtra("task_id", taskId).putExtra("device_id", deviceId));
            } else {
                showToast("没有任何问题");
            }
        }
    }

    private int deviceIssueTotal = 0;//问题统计

    //更新错误问题
    private void updateIssueCount() {
        deviceIssueTotal = plustekService.getIssueTotal(taskId, deviceId);

        SpannableStringBuilder builder = new SpannableStringBuilder("已记录问题:" + deviceIssueTotal + "个");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xFFFD5F54);
        builder.setSpan(colorSpan, 6, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        detailsBinding.tvAddNewDefect.setText(builder);
    }
}
