package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekissueBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.DictionaryService;
import com.cnksi.inspe.db.PlustekService;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.DeviceEntity;
import com.cnksi.inspe.db.entity.DictionaryEntity;
import com.cnksi.inspe.db.entity.InspeScoreEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FileUtils;
import com.cnksi.inspe.utils.FunctionUtil;
import com.cnksi.inspe.utils.FunctionUtils;
import com.cnksi.inspe.utils.ImageUtils;
import com.cnksi.inspe.widget.PopItemWindow;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 精益化评价-根据标准检查相关事项，并填写相关检查结果或扣分等
 * <p/>
 * 特殊说明
 * <ol>
 * 责任班组
 * <li>选择检修，责任单位为（"检修"、供电公司）</li>
 * <li>选择运维，责任单位为("供电公司")</li>
 * </ol>
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:25
 */
public class InspePlustekIssueActivity extends AppBaseActivity implements View.OnClickListener {
    private DictionaryService dictionaryService = new DictionaryService();
    private DeviceService deviceService = new DeviceService();
    private PlustekService plustekService = new PlustekService();
    private TeamService teamService = new TeamService();
    private TaskService taskService = new TaskService();

    private SubStationEntity subStationEntity;
    private DeviceEntity deviceEntity;
    private UserEntity expertEntity = getUserService().getUserExpert(RoleType.expert);

    private static final int TAKEPIC_REQUEST = 100;
    private ActivityInspePlustekissueBinding dataBinding;
    private List<DictionaryEntity> natureList;
    private List<DictionaryEntity> reasonList;
    private List<String> natureArray;
    private List<String> reasonArray;
    //检查项
    private PlusteRuleEntity teamRule;//Intent
    private PlusteRuleEntity checkRuleEntity;//选择
    //任务
    private InspecteTaskEntity task;
    //最大扣分值
    private float maxMinus;
    private int maxIntentMinus;//最大扣分会随着选择扣分项不同而动态变化，intent传递过来的扣分为大项可扣总分
    //当前扣分项(单位扣分)(将扣分的float转为int后处理，避免float计算误差导致不正确的数引入)
    private int scoreEntity;
    //当前扣分/
    private int minusScore;

    private GalleryAdapter galleryAdapte;
    //**拍照临时图片地址*/
    private String picTempPath;
    //**拍照保存的地址*/
    private List<String> picList = new ArrayList<>();
    //**删除的地址的图片统一处理*/
    private Set<String> picDeleteList = new HashSet<>();
    private boolean isSave = false;//是否保存项目

    //检查记录
    private TeamRuleResultEntity teamRuleResult;
    private PlustekType plustekType;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustekissue;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspePlustekissueBinding) rootDataBinding;
        setTitle("问题记录", R.drawable.inspe_left_black_24dp);

        dataBinding.addBtn.setOnClickListener(this);
        dataBinding.minuxBtn.setOnClickListener(this);
        dataBinding.dateBtn.setOnClickListener(this);
        dataBinding.dateTxt.setOnClickListener(this);
        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.issueInfoTxt.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
        //
        dataBinding.issueNatureTxt.setOnClickListener(this);
        dataBinding.blameTeamTxt.setOnClickListener(this);
        dataBinding.blameBranchTxt.setOnClickListener(this);
        dataBinding.issueReasonTxt.setOnClickListener(this);

        galleryAdapte = new GalleryAdapter(picList);
        galleryAdapte.setOnDeleteListener(new GalleryAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String entity, int possion) {
                picDeleteList.add(picList.remove(possion));//将图片添加进删除列表
                galleryAdapte.notifyDataSetChanged();
            }
        });
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

    private List<PlusteRuleEntity> list;
    private List<String> listArray;
    //责任部门
    private List<String> listBranchArray = new ArrayList<>();
    //责任班组
    private List<String> listTeamArray = new ArrayList<>();

    private String deviceId;

    final int SCAN_NUM = 100;//扣分放大倍数

    @Override
    public void initData() {
        teamRuleResult = (TeamRuleResultEntity) getIntent().getSerializableExtra("edit_data");

        //初始化功能部分
        natureList = dictionaryService.getDictonaryIssueNature();
        reasonList = dictionaryService.getDictonaryIssueReason();
        if (natureList == null || natureList.size() == 0 || reasonList == null || reasonList.size() == 0) {
            showToast("没有查询到字典，请先同步数据!");
            finish();
            return;
        }
        natureArray = new ArrayList<>(natureList.size());
        reasonArray = new ArrayList<>(reasonList.size());
        for (int i = 0, size = natureList.size(); i < size; i++) {
            natureArray.add(natureList.get(i).getV());
        }
        for (int i = 0, size = reasonList.size(); i < size; i++) {
            reasonArray.add(reasonList.get(i).getV());
        }


        if (teamRuleResult == null) {
            initCreateIssue();
        } else {
            initEditIssue();
        }
        //描述
        String info = getIntent().getStringExtra("info_txt");
        if (TextUtils.isEmpty(info) && list != null && list.size() > 0 && deviceEntity != null) {
            String[] names = plustekService.getLeve1_2Name(list.get(0).getId());
            if (names != null && names.length > 1) {
                info = deviceEntity.getName() + " " + names[0] + "-" + names[1];
            }
        }
        dataBinding.contextTxt.setText(info);

    }

    private void initEditIssue() {
        if (checkRuleEntity == null) {//避免重复查询
            checkRuleEntity = plustekService.getIssue(teamRuleResult.getRule_id());
        }

        maxIntentMinus = (int) (SCAN_NUM * plustekService.getStandaredMaxDult(teamRuleResult.getId(), deviceId));
        if (checkRuleEntity == null) {
            showToast("未查询到相关标准");
            finish();
            return;
        }
        dataBinding.issueInfoTxt.setOnClickListener(null);
        dataBinding.issueInfoTxt.setText(checkRuleEntity.getName());
        dataBinding.issueEdit.setText(teamRuleResult.getDescription());
        dataBinding.issueNatureTxt.setText(teamRuleResult.getProblem_nature());
        dataBinding.blameTeamTxt.setText(teamRuleResult.getCharge_group());
        dataBinding.blameBranchTxt.setText(teamRuleResult.getCharge_unit());
        dataBinding.issueReasonTxt.setText(teamRuleResult.getProduce_reason());
        dataBinding.suggestEdit.setText(teamRuleResult.getSuggest_deal_way());

        if (!TextUtils.isEmpty(teamRuleResult.getImg())) {
            picList.addAll(Arrays.asList(teamRuleResult.getImg().split(",")));
            galleryAdapte.notifyDataSetChanged();
        }

        //扣分，修改扣分需要加上本身的扣分
        minusScore = 0;
        scoreEntity = (int) (teamRuleResult.getDeduct_score() * SCAN_NUM);
        if (!TextUtils.isEmpty(checkRuleEntity.getScore_content())) {
            InspeScoreEntity scoreObject = JSON.parseObject(checkRuleEntity.getScore_content(), InspeScoreEntity.class);
            int maxV = (int) (scoreObject.max_decuct_score * SCAN_NUM);
            if (maxV > 0) {
                maxMinus = Math.min(maxIntentMinus, maxV) + scoreEntity;
            } else {
                maxMinus = maxIntentMinus + scoreEntity;
            }
        } else {
            maxMinus = maxIntentMinus;
        }

        setScoreTxt(scoreEntity);

        //责任班组、责任部门
        listTeamArray = new ArrayList<>();
        listBranchArray = new ArrayList<>();
        subStationEntity = deviceService.getSubStation(teamRuleResult.getBdz_id());
        task = taskService.getTask(teamRuleResult.getTask_id());

        if (!TextUtils.isEmpty(checkRuleEntity.getScore_charge())) {
            String[] branchs = checkRuleEntity.getScore_charge().split(",");

            listTeamArray.addAll(Arrays.asList(branchs));
            String team = teamRuleResult.getCharge_group();
            if (team.contains("检修")) {
                listBranchArray.add(subStationEntity.getPower_company());
                listBranchArray.add(task.getDept_name());
            } else if (team.contains("运维")) {
                listBranchArray.add(subStationEntity.getPower_company());
            }

        }
    }

    private void initCreateIssue() {
        Object teamRuleObject = getIntent().getSerializableExtra("data");


        deviceId = getIntent().getStringExtra("device_id");
        String taskId = getIntent().getStringExtra("task_id");
        plustekType = (PlustekType) getIntent().getSerializableExtra("plustek_type");

        if (teamRuleObject != null && teamRuleObject instanceof PlusteRuleEntity) {
            teamRule = (PlusteRuleEntity) teamRuleObject;
            list = plustekService.getPlusteRule(teamRule.getBigid(), null, teamRule.getId());//检查类型在第1.2.3有用，4级无用
            if (list != null && list.size() > 0) {
                listArray = new ArrayList<>(list.size());
                maxIntentMinus = (int) (SCAN_NUM * plustekService.getStandaredMaxDult(list.get(0).getId(), deviceId));//获取可扣分值
                for (int i = 0, size = list.size(); i < size; i++) {
                    listArray.add(list.get(i).getName());
                }
            }
        }
        if (!TextUtils.isEmpty(taskId)) {
            task = taskService.getTask(taskId);
            if (task != null) {
                subStationEntity = deviceService.getSubStation(task.getBdz_id());
                deviceEntity = deviceService.getDeviceById(deviceId);
            }
        }

        if (teamRule == null || task == null || subStationEntity == null || deviceEntity == null) {
            showToast("参数错误!");
            finish();
            return;
        }


        if (expertEntity == null) {
            showToast("非法权限!");
            finish();
            return;
        }

        //初始化
        dataBinding.issueNatureTxt.setText(natureArray.get(0));//问题性质，默认一般
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKEPIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                BitmapUtils.compressImage(FileUtils.getInpseRootPath() + picTempPath, 4);
                picList.add(picTempPath);
                galleryAdapte.notifyDataSetChanged();
            }
        }
    }

    /**
     * 扣分显示<p/>
     * 根据最大扣分值来作为判断
     *
     * @param value
     */

    private void setScoreTxt(int value) {
        boolean isAdd = true;
        int nextValue = minusScore + value;
        if (nextValue > maxMinus) {
            isAdd = false;
            dataBinding.addBtn.setEnabled(false);
        } else if (nextValue == maxMinus || scoreEntity == 0) {
            dataBinding.addBtn.setEnabled(false);
        } else {
            dataBinding.addBtn.setEnabled(true);
        }

        if (nextValue < scoreEntity) {
            isAdd = false;
            dataBinding.minuxBtn.setEnabled(false);
        } else if (nextValue == scoreEntity) {
            dataBinding.minuxBtn.setEnabled(false);
        } else {
            dataBinding.minuxBtn.setEnabled(true);
        }

        if (isAdd) {
            minusScore = nextValue;
        }

        dataBinding.minusScoreEdit.setText(String.format("%.2f", (minusScore * 1.0f / SCAN_NUM)));

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cameraBtn) {//拍照
            if (picList.size() < 3) {
                String picName = FileUtils.createInpseImgLongName(task);//生成图片名称
                picTempPath = FileUtils.getInpseImgPath(task);
                FunctionUtils.takePicture(this, picName, FileUtils.getInpseRootPath() + picTempPath, TAKEPIC_REQUEST);
                //文件相对地址
                picTempPath = picTempPath + picName;
            } else {
                showToast("目前仅支持上传3张图片");
            }
        } else if (i == R.id.addBtn) {//+扣分
            setScoreTxt(scoreEntity);
        } else if (i == R.id.minuxBtn) {//-扣分
            setScoreTxt(-scoreEntity);
        } else if (i == R.id.issueInfoTxt) {//错误问题
            new PopItemWindow(this).setListAdapter(listArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    PlusteRuleEntity entity = list.get(position);
                    //判断是否为统一选择，同一选择不处理
                    if (checkRuleEntity == entity) {
                        return;
                    }
                    checkRuleEntity = entity;

                    //判断上次选择是否有记录问题，如果有需要将图片冲删除列表中移除
                    if (teamRuleResult != null) {
                        for (String picName : picList) {//先添加后移除，清理新添加的图片
                            picDeleteList.add(picName);
                        }
                        if (!TextUtils.isEmpty(teamRuleResult.getImg())) {
                            String[] imsg = teamRuleResult.getImg().split(",");
                            for (String pic : imsg) {
                                picDeleteList.remove(pic);//将删除的图片从列表中移除
                            }
                        }
                        picList.clear();
                        galleryAdapte.notifyDataSetChanged();
                    }

                    teamRuleResult = teamService.getRuleResult(list.get(position).getId(), task.getId());//查询数据库是否对该问题进行过记录
                    if (teamRuleResult != null) {//初始化修改UI
                        //对图片进行销毁
                        for (String picName : picList) {//先添加后移除，清理新添加的图片
                            picDeleteList.add(picName);
                        }
                        picDeleteList.clear();
                        galleryAdapte.notifyDataSetChanged();

                        initEditIssue();
                        return;
                    }
                    //问题描述
                    dataBinding.issueInfoTxt.setText(entity.getName());
                    dataBinding.issueEdit.setText(entity.getName().replaceAll("的{0,},{0,}，{0,}[扣].*分.*", "").replaceAll("[0-9].]{0,}[a-z)]{0,}[a-z]{0,}", ""));

                    //扣分
                    minusScore = 0;
                    scoreEntity = (int) (entity.getScore() * SCAN_NUM);
                    if (!TextUtils.isEmpty(entity.getScore_content())) {
                        InspeScoreEntity scoreObject = JSON.parseObject(entity.getScore_content(), InspeScoreEntity.class);
                        int maxV = (int) (scoreObject.max_decuct_score * SCAN_NUM);
                        if (maxV > 0) {
                            maxMinus = Math.min(maxIntentMinus, maxV);
                        } else {
                            maxMinus = maxIntentMinus;
                        }
                    } else {
                        maxMinus = maxIntentMinus;
                    }
                    setScoreTxt(scoreEntity);

                    //检修班组、检修部门
                    listTeamArray.clear();
                    listBranchArray.clear();
                    if (!TextUtils.isEmpty(entity.getScore_charge())) {
                        String[] branchs = entity.getScore_charge().split(",");

                        listTeamArray.addAll(Arrays.asList(branchs));
                        if (branchs.length > 1) {
                            dataBinding.blameTeamTxt.setText(null);
                            dataBinding.blameBranchTxt.setText(null);
                        } else {
                            String team = branchs[0];
                            dataBinding.blameTeamTxt.setText(team);

                            if (team.contains("检修")) {
                                //运维或供电公司
                                dataBinding.blameBranchTxt.setText(null);
                                listBranchArray.add(subStationEntity.getPower_company());
                                listBranchArray.add(task.getDept_name());

                            } else if (team.contains("运维")) {
                                //供电公司
                                dataBinding.blameBranchTxt.setText(subStationEntity.getPower_company());
                                listBranchArray.add(subStationEntity.getPower_company());
                            }

                        }
                    } else {
                        dataBinding.blameBranchTxt.setText(null);
                    }
                }
            }).setPopWindowWidth(view.getWidth()).showAsDropDown(view);
        } else if (i == R.id.issueNatureTxt) {//问题性质(独立),一般、严重、危急
            new PopItemWindow(this).setListAdapter(natureArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.issueNatureTxt.setText(natureList.get(position).getV());
                }
            }).setPopWindowWidth(view.getWidth()).showAsDropDown(view);
        } else if (i == R.id.blameTeamTxt) {//问题班组
            new PopItemWindow(this).setListAdapter(listTeamArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    String team = listTeamArray.get(position);
                    dataBinding.blameTeamTxt.setText(team);
                    listBranchArray.clear();
                    if (team.contains("检修")) {
                        //运维或供电公司
                        dataBinding.blameBranchTxt.setText(null);
                        listBranchArray.add(subStationEntity.getPower_company());
                        listBranchArray.add(task.getDept_name());
                        dataBinding.blameBranchTxt.performClick();

                    } else if (team.contains("运维")) {
                        //供电公司
                        dataBinding.blameBranchTxt.setText(subStationEntity.getPower_company());
                        listBranchArray.add(subStationEntity.getPower_company());
                    }
                }
            }).setPopWindowWidth(view.getWidth()).showAsDropDown(view);
        } else if (i == R.id.blameBranchTxt) {//责任单位
            if (listBranchArray.size() > 0) {
                new PopItemWindow(this).setListAdapter(listBranchArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        dataBinding.blameBranchTxt.setText(listBranchArray.get(position));
                    }
                }).setPopWindowWidth(view.getWidth()).showAsDropDown(view);
            } else {
                showToast("请先选择扣分原因");
            }
        } else if (i == R.id.issueReasonTxt) {//问题产生原因(独立)
            new PopItemWindow(this).setListAdapter(reasonArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.issueReasonTxt.setText(reasonList.get(position).getV());
                }
            }).setPopWindowWidth(view.getWidth()).showAsDropDown(view);
        } else if (i == R.id.okBtn) {//确定
            if (TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                dataBinding.issueInfoTxt.performClick();
                showToast("请选择扣分原因");
                return;
            } else if (TextUtils.isEmpty(dataBinding.issueEdit.getText().toString().trim())) {
                dataBinding.issueEdit.requestFocus();
                showToast("请输入问题描述");
                return;
//                } else if (TextUtils.isEmpty(dataBinding.dateTxt.getText().toString().trim())) {
//                    showToast("请输入整改日期");
//                    dataBinding.dateBtn.performClick();
//                    return;
            } else if (TextUtils.isEmpty(dataBinding.issueNatureTxt.getText())) {
                showToast("请选择问题性质");
                dataBinding.issueNatureTxt.performClick();
                return;
            } else if (TextUtils.isEmpty(dataBinding.blameTeamTxt.getText())) {
                showToast("请选择责任班组");
                dataBinding.blameTeamTxt.performClick();
                return;
            } else if (TextUtils.isEmpty(dataBinding.blameBranchTxt.getText())) {
                showToast("请选择责任单位");
                dataBinding.blameBranchTxt.performClick();
                return;
            } else if (TextUtils.isEmpty(dataBinding.issueReasonTxt.getText())) {
                showToast("请选择产生原因");
                dataBinding.issueReasonTxt.performClick();
                return;
//                } else if (TextUtils.isEmpty(dataBinding.suggestEdit.getText().toString().trim())) {
//                    showToast("请输入处理措施");
//                    dataBinding.suggestEdit.performClick();
//                    return;
            }

            createIssue();//满足输入，创建错误
        }

    }

    /**
     * 创建错误
     */
    private void createIssue() {
        if (teamRuleResult == null) {
            teamRuleResult = new TeamRuleResultEntity();
        }
        //创建ID
        if (teamRuleResult.getId() == null) {//创建或覆盖
            teamRuleResult.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        //任务ID
        teamRuleResult.setTask_id(task.id);
        //标准ID
        teamRuleResult.setRule_id(checkRuleEntity.getId());
        teamRuleResult.setRule_name(checkRuleEntity.getName());
        //被检查班组ID
        teamRuleResult.setDept_id(task.getDept_id());
        teamRuleResult.setDept_name(task.getDept_name());
        //检查人ID
        teamRuleResult.setCheck_person_id(expertEntity.getId());
        teamRuleResult.setCheck_person_name(expertEntity.getUsername());
        teamRuleResult.setCheck_type(task.getType());

        //精益化评价独有
        //问题性质
        teamRuleResult.setProblem_nature(dataBinding.issueNatureTxt.getText().toString());
        //问题原因
        teamRuleResult.setProduce_reason(dataBinding.issueReasonTxt.getText().toString());
        //责任班组
        teamRuleResult.setCharge_group(dataBinding.blameTeamTxt.getText().toString());
        //责任单位
        teamRuleResult.setCharge_unit(dataBinding.blameBranchTxt.getText().toString());
        //建议处理方式
        teamRuleResult.setSuggest_deal_way(dataBinding.suggestEdit.getText().toString().trim());

        teamRuleResult.setBdz_id(subStationEntity.getBdzid());//变电站ID
        teamRuleResult.setBdz_name(subStationEntity.getName());//变电站Name
        if (TextUtils.isEmpty(teamRuleResult.getDevice_id())) {//修改不用设置，因为deviceEntity is null.
            teamRuleResult.setDevice_id(deviceEntity.getDeviceid());//设备ID
            teamRuleResult.setDevice_name(deviceEntity.getName());//设备Name
            teamRuleResult.setDevice_bigtype(deviceEntity.getBigid());//设备大类
        }

        //问题类型(记录类型：问题（answer）、普通记录（normal）)
        teamRuleResult.setRecord_type(RecordType.answer.name());
        //扣分情况
        teamRuleResult.setDeduct_score(minusScore * 1.0f / SCAN_NUM);//放大数据后对数据进行缩小
        //问题描述
        teamRuleResult.setDescription(dataBinding.issueEdit.getText().toString().trim());
        //扣分原因jsonArray
//                teamRuleResult.setReason(JSON.toJSONString(new InspeScoreEntity[]{scoreBean}));
        //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
        teamRuleResult.setProgress(ProgressType.wfp.name());
        //整改期限
        teamRuleResult.setPlan_improve_time(DateFormat.dateToDbString000(System.currentTimeMillis()));//默认当天


        //图片(疑问？是否需要上传)
        teamRuleResult.setImg(ArrayInspeUtils.toListString(picList));
        //检查时间
        String datetime = DateFormat.dateToDbString(System.currentTimeMillis());
        if (teamRuleResult.getCreate_time() == null) {
            teamRuleResult.setCreate_time(datetime);
        }
        if (teamRuleResult.getInsert_time() == null) {
            teamRuleResult.setInsert_time(datetime);
        }
        teamRuleResult.setLast_modify_time(datetime);

        //更新任务状态
        if (task.getProgress() == null || TaskProgressType.valueOf(task.getProgress()) == TaskProgressType.todo) {
            task.setProgress(TaskProgressType.doing.name());
            taskService.updateTask(task);
        }

        if (teamService.saveRuleResult(teamRuleResult)) {
            showToast("操作成功");
            isSave = true;
            finish();
        } else {
            showToast("保存数据库失败！");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSave) {//保存成功
            for (String pic : picList) {
                picDeleteList.remove(pic);//移除
            }
        } else {
            if (teamRuleResult != null) {//重新编辑
                for (String pic : picList) {
                    picDeleteList.add(pic);//添加保存的图片
                }
                if (!TextUtils.isEmpty(teamRuleResult.getImg())) {
                    String[] imsg = teamRuleResult.getImg().split(",");
                    for (String pic : imsg) {
                        picDeleteList.remove(pic);//将删除的图片从列表中移除
                    }
                }
            } else {//取消
                for (String pic : picList) {
                    picDeleteList.add(pic);//将图片列表添加进删除文件夹
                }
            }
        }

        //删除文件
        for (String fileName : picDeleteList) {
            File file = new File(FileUtils.getInpseRootPath(), fileName);
            if (file.exists()) {
                file.delete();
            }
        }

    }
}
