package com.cnksi.inspe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeIssuedetailBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.db.entity.UserGroupEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.ui.fragment.AllIssueFragment;
import com.cnksi.inspe.ui.fragment.MyIssueFragment;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FileUtils;
import com.cnksi.inspe.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 问题详情页面
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 15:21
 */
public class InspeIssueDetailActivity extends AppBaseActivity implements View.OnClickListener {

    /**
     * 启动整改页面
     */
    private static final int ACTIVITY_RECFITY = 100;
    private ActivityInspeIssuedetailBinding dataBinding;
    private TeamRuleResultEntity issueEntity;
    private TeamService teamService = new TeamService();
    private PageDealInterface pageDeal;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_issuedetail;
    }

    private ProgressType progressType;
    private TaskType taskType;
    private GalleryAdapter galleryAdapte;
    private List<String> picList = new ArrayList<>();
    private int position = 0;
    private List<TeamRuleResultEntity> list;
    private String startType = null;

    @Override
    public void initUI() {
        setTitle("问题详情", R.drawable.inspe_left_black_24dp);
        issueEntity = (TeamRuleResultEntity) getIntent().getSerializableExtra("data");
        position = getIntent().getIntExtra("positon", position) + 1;
        startType = getIntent().getStringExtra("start_type");
        if ("all_issue".equals(startType)) {
            list = AllIssueFragment.getList();
        } else if ("my_issue".equals(startType)) {
            list = MyIssueFragment.getList();
        }

        if (issueEntity == null) {
            showToast("参数错误!");
            finish();
            return;
        }

        try {
            progressType = ProgressType.valueOf(issueEntity.getProgress());
            taskType = TaskType.valueOf(issueEntity.getCheck_type());
        } catch (Exception e) {
            showToast("进度参数不正确!");
            finish();
            return;
        }


        dataBinding = (ActivityInspeIssuedetailBinding) rootDataBinding;
        dataBinding.rectifyBtn.setOnClickListener(this);
        dataBinding.checkBtn.setOnClickListener(this);
        dataBinding.allotBtn.setOnClickListener(this);
        dataBinding.nextBtn.setOnClickListener(this);


        galleryAdapte = new GalleryAdapter(picList);
        galleryAdapte.setMode(true);
        galleryAdapte.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArrayList<String> showList = new ArrayList<>(picList.size());
                for (int i = 0, size = picList.size(); i < size; i++) {
                    showList.add(FileUtils.getInpseRootPath() + picList.get(i));
                }
                ImageUtils.showImageDetails(context, position, showList, false, false);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dataBinding.galleryView.setLayoutManager(linearLayoutManager);
        dataBinding.galleryView.setAdapter(galleryAdapte);

    }

    @Override
    public void initData() {

        dataBinding.teamTxt.setText(issueEntity.getDept_name());

        dataBinding.descriptionTxt.setText(issueEntity.getDescription());
        dataBinding.checkNameTxt.setText(issueEntity.getCheck_person_name());
        dataBinding.checkDateTxt.setText(DateFormat.formatYMD(DateFormat.dbdateToLong(issueEntity.getInsert_time())));
        dataBinding.rectifyDateTxt.setText(DateFormat.formatYMD(DateFormat.dbdateToLong(issueEntity.getPlan_improve_time())));

        dataBinding.standardTxt.setText(teamService.getRoleDoc1(issueEntity.getRule_id()));//标准内容
        String docName = teamService.getRoleDocName4(issueEntity.getRule_id());
        if (TextUtils.isEmpty(docName)) {
            dataBinding.fileTxt.setText("");//文件号
        } else {
            dataBinding.fileTxt.setText(" －－《 " + docName + "》");//文件号
        }

        dataBinding.inspeTypeTxt.setText(taskType.getDesc());
        switch (taskType) {
            case bzjs://班组建设检查
                dataBinding.jxhItemView.setVisibility(View.GONE);
                dataBinding.jxhItemLineView.setVisibility(View.GONE);
                break;
            case jyhjc://精益化评价
                dataBinding.convertTxt.setText(issueEntity.getBdz_name());
                dataBinding.deviceNameTxt.setText(issueEntity.device_name);
                break;
            case sbpc://设备排查
                //暂不实现
//                break;
            case sbjc://设备检查
                //暂不实现
//                break;
            default:
                showToast("暂不支持该类型！");
                finish();
                return;
        }

        switch (getUserService().getUser1().getRoleType()) {
            case director:
            case specialty:
                pageDeal = new DirectorDeal();
                break;
            case team_leader:
                pageDeal = new TeamLeaderDeal();
                break;
            case tracker:
                pageDeal = new TrackerDeal();
                break;
            case expert:
            default:
                showToast("你没有权限查看!");
                finish();
                return;
        }

        pageDeal.init(progressType);

        picList.clear();
        if (issueEntity.getImg() != null) {
            String[] picArray = issueEntity.getImg().split(",");
            picList.addAll(Arrays.asList(picArray));
        }
        galleryAdapte.notifyDataSetChanged();


        if (list != null && position < list.size()) {
            dataBinding.nextBtn.setEnabled(true);
        } else {
            dataBinding.nextBtn.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        //防止更新导致状态异常
        progressType = ProgressType.valueOf(issueEntity.getProgress());
        if (v.getId() == R.id.rectifyBtn) {
            pageDeal.recfity(progressType);
        } else if (v.getId() == R.id.checkBtn) {
            pageDeal.checkup(progressType);
        } else if (v.getId() == R.id.allotBtn) {
            pageDeal.allot(progressType);
        } else if (v.getId() == R.id.nextBtn) {
            //下一页
            issueEntity = list.get(position);
            position++;
            initData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RECFITY) {
            if (resultCode == RESULT_OK) {//更新UI
                issueEntity = teamService.getRuleResult(issueEntity.getId());

                //整改图片
//                if (issueEntity.getImg() != null) {
//                    picList.clear();
//                    String[] picArray = issueEntity.getImg().split(",");
//                    picList.addAll(Arrays.asList(picArray));
//                }
                galleryAdapte.notifyDataSetChanged();
                pageDeal.init(ProgressType.valueOf(issueEntity.getProgress()));
            }
        }
    }

    /**
     * 页面接口，由不同角色实现接口
     */
    private interface PageDealInterface {
        /**
         * 初始化
         */
        void init(ProgressType progressType);

        /**
         * 整改
         *
         * @param progressType
         */
        void recfity(ProgressType progressType);

        /**
         * 审核
         *
         * @param progressType
         */
        void checkup(ProgressType progressType);


        /**
         * 安排
         *
         * @param progressType
         */
        void allot(ProgressType progressType);
    }

    /**
     * 维护
     */
    private class TrackerDeal implements PageDealInterface {

        @Override
        public void init(ProgressType progressType) {
            //默认按钮
            dataBinding.rectifyBtn.setVisibility(View.GONE);
            dataBinding.checkBtn.setVisibility(View.GONE);
            dataBinding.allotBtn.setVisibility(View.GONE);
            dataBinding.nextBtn.setVisibility(View.VISIBLE);
            dataBinding.rectifyBtn.setText("整改");
            dataBinding.nextBtn.setText("下一页");

            //整改
            switch (progressType) {
                case wzg://未整改
                    dataBinding.rectifyBtn.setEnabled(getUserService().isUserContains(issueEntity.getImprove_person_id()));
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setText("整改");
                    break;
                case wfp://未分配
                case shwtg://未通过
                    dataBinding.allotBtn.setEnabled(false);
                    dataBinding.allotBtn.setVisibility(View.VISIBLE);
                    dataBinding.allotBtn.setText("分配");
                    break;
                case bzzwsh://班组长未审核
                case zzwsh://主任未审核
                    dataBinding.checkBtn.setEnabled(false);
                    dataBinding.checkBtn.setVisibility(View.VISIBLE);
                    dataBinding.checkBtn.setText("审核");
                    break;
                case ybh://已闭环
                    dataBinding.rectifyBtn.setEnabled(false);
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setText("关闭");
                    break;
            }
        }

        @Override
        public void recfity(ProgressType progressType) {
            //整改-->班组长(未审核)
            startActivityForResult(new Intent(context, InspeRecfityActivity.class).putExtra("data", issueEntity), ACTIVITY_RECFITY);

        }

        @Override
        public void checkup(ProgressType progressType) {
        }

        @Override
        public void allot(ProgressType progressType) {
        }
    }

    /**
     * 班组长
     */
    private class TeamLeaderDeal implements PageDealInterface {

        @Override
        public void init(ProgressType progressType) {
            //默认按钮
            dataBinding.rectifyBtn.setVisibility(View.GONE);
            dataBinding.checkBtn.setVisibility(View.GONE);
            dataBinding.allotBtn.setVisibility(View.GONE);
            dataBinding.nextBtn.setVisibility(View.VISIBLE);

            dataBinding.nextBtn.setText("下一页");

            //整改
            switch (progressType) {
                case wzg://未整改
                    dataBinding.rectifyBtn.setText("整改");
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setEnabled(getUserService().isUserContains(issueEntity.getImprove_person_id()));
//                    break;
                case wfp://未分配
                case shwtg://未通过
                    dataBinding.allotBtn.setText("分配");
                    dataBinding.allotBtn.setEnabled(true);
                    dataBinding.allotBtn.setVisibility(View.VISIBLE);
                    break;
                case bzzwsh://班组长未审核
                    dataBinding.allotBtn.setEnabled(true);
                    dataBinding.checkBtn.setText("审核");
                    dataBinding.checkBtn.setVisibility(View.VISIBLE);
                    break;
                case zzwsh://主任未审核
                case ybh://已闭环
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setEnabled(false);
                    dataBinding.rectifyBtn.setText("关闭");
                    break;
            }
        }

        @Override
        public void recfity(ProgressType progressType) {
            //整改
//            issueEntity.setImprove_description("没问题🙂");
//            issueEntity.setImprove_person_id(getUserService().getUser1().getId());
//            issueEntity.setImprove_person_name(getUserService().getUser1().getUsername());
//            issueEntity.setImprove_time(DateFormat.dateToDbString(System.currentTimeMillis()));
//
//            //①班组长主任审核
//            issueEntity.setProgress(ProgressType.bzzwsh.name());
//            //②自动为主任审核
//            issueEntity.setProgress(ProgressType.zzwsh.name());
//
//            teamService.saveRuleResult(issueEntity);
            startActivityForResult(new Intent(context, InspeRecfityActivity.class).putExtra("data", issueEntity), ACTIVITY_RECFITY);
        }

        @Override
        public void checkup(ProgressType progressType) {
            if (ProgressType.valueOf(issueEntity.getProgress()) == ProgressType.bzzwsh) {
                //审核(班组长)-->审核(主任)
                //弹出对话框


                new AlertDialog.Builder(context)
                        .setTitle("审核").setMessage("您是否同意审核通过?\n")
                        .setPositiveButton("不通过",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //①审核未通过(自动跳转分配)
                                        issueEntity.setProgress(ProgressType.shwtg.name());
                                        teamService.saveRuleResult(issueEntity);
                                        pageDeal.init(ProgressType.shwtg);
                                    }
                                })
                        .setNegativeButton("通过",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //②审核通过
                                        issueEntity.setProgress(ProgressType.zzwsh.name());
                                        teamService.saveRuleResult(issueEntity);
                                        pageDeal.init(ProgressType.zzwsh);
                                    }
                                })
                        // 显示
                        .show();


            }
        }

        @Override
        public void allot(ProgressType progressType) {
            //选择整改用户
            if (progressType == ProgressType.wfp || progressType == ProgressType.shwtg || progressType == ProgressType.wzg) {

                final List<UserEntity> userList = getUserService().getUsers(getUserService().getUser1().getDept_id(), getUserService().getUser1().id, RoleType.tracker);
                String[] userArray = new String[userList.size()];

                for (int i = 0, length = userArray.length; i < length; i++) {
                    userArray[i] = userList.get(i).getUsername();
                }
                AlertDialog dialog = new AlertDialog.Builder(context).setTitle("选择整改人员")
                        .setSingleChoiceItems(userArray, -1, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                issueEntity.setProgress(ProgressType.wzg.name());
                                issueEntity.setImprove_person_id(userList.get(which).getId());//整改人ID
                                issueEntity.setImprove_person_name(userList.get(which).getUsername());//整改人姓名

                                teamService.saveRuleResult(issueEntity);

                                pageDeal.init(ProgressType.wzg);
                            }
                        }).create();
                dialog.show();


            }
        }
    }

    /**
     * 专责/主任
     */
    private class DirectorDeal implements PageDealInterface {

        @Override
        public void init(ProgressType progressType) {
            //默认按钮
            dataBinding.rectifyBtn.setVisibility(View.GONE);
            dataBinding.checkBtn.setVisibility(View.GONE);
            dataBinding.allotBtn.setVisibility(View.VISIBLE);
            dataBinding.nextBtn.setVisibility(View.VISIBLE);
            dataBinding.allotBtn.setVisibility(View.GONE);
            dataBinding.nextBtn.setText("下一页");

            //只有班组建设可以分享
            if (TaskType.bzjs.name().equals(issueEntity.getCheck_type())) {
                dataBinding.allotBtn.setText("分享");
                dataBinding.allotBtn.setVisibility(View.VISIBLE);
            }

            //整改
            switch (progressType) {
                case zzwsh://班组长未审核
                    dataBinding.checkBtn.setEnabled(true);
                    dataBinding.checkBtn.setText("审核");
                    dataBinding.checkBtn.setVisibility(View.VISIBLE);
                    break;
                case wzg://未整改
                case wfp://未分配Btn.setText("分配");
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setEnabled(false);
                    dataBinding.rectifyBtn.setText("分配");
                    break;
                case shwtg://未通过
                case bzzwsh://主任未审核
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setEnabled(false);
                    dataBinding.rectifyBtn.setText("审核");
                    break;
                case ybh://已闭环
                    dataBinding.rectifyBtn.setVisibility(View.VISIBLE);
                    dataBinding.rectifyBtn.setEnabled(false);
                    dataBinding.rectifyBtn.setText("完成");
                    break;
            }
        }

        @Override
        public void recfity(ProgressType progressType) {
        }

        @Override
        public void checkup(ProgressType progressType) {
            //审核
            if (ProgressType.valueOf(issueEntity.getProgress()) == ProgressType.zzwsh) {
                //弹出对话框

                new AlertDialog.Builder(context)
                        .setTitle("审核").setMessage("您是否同意审核通过?\n")
                        .setPositiveButton("不通过",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //①审核未通过(自动跳转分配)
                                        issueEntity.setProgress(ProgressType.shwtg.name());
                                        teamService.saveRuleResult(issueEntity);
                                        pageDeal.init(ProgressType.shwtg);
                                    }
                                })
                        .setNegativeButton("通过",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //②审核通过
                                        issueEntity.setProgress(ProgressType.ybh.name());
                                        teamService.saveRuleResult(issueEntity);
                                        pageDeal.init(ProgressType.ybh);
                                    }
                                })
                        // 显示
                        .show();

            }
        }

        @Override
        public void allot(ProgressType progressType) {
            final List<UserGroupEntity> groupList = getUserService().getUserGroup();
            for (int i = groupList.size() - 1; i > -1; i--) {
                if (teamService.isShared(groupList.get(i).getDept_id(), issueEntity.getTask_id() != null ? issueEntity.getTask_id() : issueEntity.getRule_result_id(), issueEntity.getRule_id())) {
                    groupList.remove(i);
                }
            }
            if (groupList.size() == 0) {
                showToast("所有班组已分享了！");
                return;
            }

            String[] userArray = new String[groupList.size()];


            for (int i = 0, length = userArray.length; i < length; i++) {
                userArray[i] = groupList.get(i).getName();
            }

            final List<String> chioseList = new ArrayList<>();
            final AlertDialog dialog = new AlertDialog.Builder(context).setTitle("选择班组")
                    .setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int which) {
                            d.dismiss();
                            if (chioseList.size() > 0) {
                                String id = issueEntity.getId();
                                //分享
                                for (int i = 0, size = chioseList.size(); i < size; i++) {
                                    //推送
                                    issueEntity.setProgress(ProgressType.wfp.name());
                                    issueEntity.setId(UUID.randomUUID().toString().replace("-", ""));
                                    issueEntity.setDept_id(groupList.get(Integer.valueOf(chioseList.get(i))).getId());//班组ID
                                    issueEntity.setDept_name(groupList.get(Integer.valueOf(chioseList.get(i))).getName());//班组名称
                                    issueEntity.setImprove_person_name(null);//整改人
                                    issueEntity.setImprove_person_id(null);//整改人
                                    issueEntity.setImprove_time(null);//整改时间
                                    issueEntity.setImprove_description(null);//整改描述
                                    issueEntity.setRule_result_id(issueEntity.getTask_id());//任务ID
                                    issueEntity.setTask_id(null);

                                    teamService.saveRuleResult(issueEntity);


                                }
                                //
                                issueEntity = teamService.getRuleResult(id);
//                                pageDeal.init(progressType);

                            }
                        }
                    })
                    .setMultiChoiceItems(userArray, null, new DialogInterface.OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (isChecked) {
                                chioseList.add(which + "");
                            } else {
                                chioseList.remove(which + "");
                            }
                        }
                    }).create();
            dialog.show();

//            AlertDialog dialog = new AlertDialog.Builder(context).setTitle("选择推送班组")
//                    .setSingleChoiceItems(userArray, -1, new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            //推送
//                            issueEntity.setProgress(ProgressType.wfp.name());
//                            issueEntity.setId(UUID.randomUUID().toString());
//                            issueEntity.setBdz_id(groupList.get(which).getDept_id());//班组ID
//                            issueEntity.setBdz_name(groupList.get(which).getDept_name());//班组名称
//                            issueEntity.setImprove_person_name(null);//整改人
//                            issueEntity.setImprove_person_id(null);//整改人
//                            issueEntity.setImprove_time(null);//整改时间
//                            issueEntity.setImprove_description(null);//整改描述
//                            issueEntity.setTask_id(null);//任务ID
//
//                            teamService.saveRuleResult(issueEntity);
//
//                            pageDeal.init(progressType);
//                        }
//                    }).create();
//            dialog.show();


        }
    }


}
