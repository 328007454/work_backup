package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
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
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FileUtils;
import com.cnksi.inspe.utils.FunctionUtils;
import com.cnksi.inspe.utils.ImageUtils;
import com.cnksi.inspe.utils.InspeConfig;
import com.cnksi.inspe.widget.BreakWordDialog;
import com.cnksi.inspe.widget.PopItemWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 精益化评价-根据标准检查相关事项，并填写相关检查结果或扣分等
 * <br/>
 * 特殊说明
 * <ol>
 * 责任班组
 * <li>选择检修，责任单位为（"检修"、供电公司）</li>
 * <li>选择运维，责任单位为("供电公司")</li>
 * </ol>
 * <ol>
 * 启动参数包含{@link IntentKey}
 * <li>{@link IntentKey#TASK_ID}:任务ID</li>
 * <li>{@link IntentKey#PLUSTEK_TYPE}:检查类型</li>
 * <li>{@link IntentKey#DEVICE_ID}:设备ID</li>
 * <li>{@link IntentKey#RULE_ID_3}:标准ID3</li>
 * <li>{@link IntentKey#RULE_RESULT_ID}:问题ID</li>
 * <li>{@link IntentKey#CONTENT}:说明内容</li>
 * </ol>
 * <ol>
 * 启动模式及参数{@link StartMode}
 * <li>{@link StartMode#DEFAULT}:问题创建(默认)，参数:{任务ID、设备ID、标准ID3}</li>
 * <li>{@link StartMode#MODIFY}:问题修改，参数:{任务ID、设备ID、问题ID、说明内容}</li>
 * <li>{@link StartMode#COPY}:问题拷贝，参数:{任务ID、设备ID、问题ID、说明内容}</li>
 * <li>{@link StartMode#NOPMS}:无设备信息(台账))，参数:{任务ID、设备ID}</li>
 * </ol>
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:25
 */
public class InspePlustekIssueActivity extends AppBaseActivity implements View.OnClickListener {
    private ActivityInspePlustekissueBinding dataBinding;

    private DictionaryService dictionaryService = new DictionaryService();
    private DeviceService deviceService = new DeviceService();
    private PlustekService plustekService = new PlustekService();
    private TeamService teamService = new TeamService();
    private TaskService taskService = new TaskService();

    private static final int TAKEPIC_REQUEST = 100;
    private List<DictionaryEntity> natureList;
    private List<DictionaryEntity> reasonList;
    private List<String> natureArray;
    private List<String> reasonArray;

    private float maxMinus; //最大扣分值
    private int maxIntentMinus;//最大扣分会随着选择扣分项不同而动态变化，intent传递过来的扣分为大项可扣总分
    private int scoreEntity; //当前扣分项(单位扣分)(将扣分的float转为int后处理，避免float计算误差导致不正确的数引入)
    private int minusScore;  //当前扣分/
    final int SCAN_NUM = 100;//扣分放大倍数

    private GalleryAdapter galleryAdapte;
    private String picTempPath;  //**拍照临时图片地址*/
    private List<String> picList = new ArrayList<>();  //**拍照保存的地址*/
    private Set<String> picDeleteList = new HashSet<>(); //**删除的地址的图片统一处理*/
    private boolean isSave = false;//是否保存项目

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
        dataBinding.issueEdit.setOnClickListener(this);
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


    private List<String> listArray;
    private List<String> listBranchArray = new ArrayList<>(); //责任部门
    private List<String> listTeamArray = new ArrayList<>(); //责任班组


    private int startMode = StartMode.DEFAULT; //页面启动模式 */
    private String taskId;//任务ID
    private String deviceId;//设备ID
    private String ruleId3;//标准ID(level=3)
    private String ruleResultId;//错误
    private String content;//描述内容

    private UserEntity expertEntity = getUserService().getUserExpert(RoleType.expert);
    private PlustekType plustekType;//精益化检查类型

    private InspecteTaskEntity taskEntity;//任务
    private DeviceEntity deviceEntity;//设备
    private SubStationEntity subStationEntity;//变电站

    private TeamRuleResultEntity ruleResultEntity; //问题记录
    private PlusteRuleEntity rule3Entity;// //检查项 Intent
    private List<PlusteRuleEntity> list;
    private PlusteRuleEntity rule4Entity;//选择

    @Override
    public void initData() {
        startMode = getIntent().getIntExtra(IntentKey.START_MODE, StartMode.DEFAULT);
        taskId = getIntent().getStringExtra(IntentKey.TASK_ID);
        plustekType = (PlustekType) getIntent().getSerializableExtra(IntentKey.PLUSTEK_TYPE);
        deviceId = getIntent().getStringExtra(IntentKey.DEVICE_ID);
        ruleId3 = getIntent().getStringExtra(IntentKey.RULE_ID_3);
        ruleResultId = getIntent().getStringExtra(IntentKey.RULE_RESULT_ID);
        content = getIntent().getStringExtra(IntentKey.CONTENT);
        if (expertEntity == null) {
            showToast("非法权限!");
            finish();
            return;
        }

        if (!initDictionary()) {
            showToast("没有查询到字典，请先同步数据!");
            finish();
            return;
        }
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(deviceId)) {//公共字段,taskId、deviceId
            showToast("参数错误!");
            finish();
            return;
        }
        taskEntity = taskService.getTask(taskId);
        deviceEntity = deviceService.getDeviceById(deviceId);//设备
        if (taskEntity == null || deviceEntity == null) {
            showToast("参数错误!");
            finish();
            return;
        }
        subStationEntity = deviceService.getSubStation(taskEntity.getBdz_id());//变电站
        if (subStationEntity == null) {
            showToast("参数错误!");
            finish();
            return;
        }

//         * <li>{@link StartMode#DEFAULT}:问题创建(默认)，参数:{任务ID、设备ID、标准ID3、说明内容}</li>
//         * <li>{@link StartMode#MODIFY}:问题修改，参数:{任务ID、设备ID、问题ID、说明内容}</li>
//         * <li>{@link StartMode#COPY}:问题拷贝，参数:{任务ID、设备ID、问题ID、说明内容}</li>
//         * <li>{@link StartMode#NOPMS}:无设备信息(台账))，参数:{任务ID、设备ID}</li>
        switch (startMode) {
            case StartMode.DEFAULT:
                if (TextUtils.isEmpty(ruleId3)) {
                    showToast("参数错误!");
                    finish();
                    return;
                } else {
                    list = plustekService.getPlusteRule(taskId, deviceEntity.getBigid(), null, ruleId3);
                    rule3Entity = plustekService.getIssue(ruleId3);
                    if (list == null || list.size() == 0) {
                        showToast("参数错误!");
                        finish();
                        return;
                    }
                    maxIntentMinus = (int) (plustekService.getStandaredMaxDult(list.get(0).getId(), deviceId) * SCAN_NUM);
                    listArray = new ArrayList<>();
                    for (PlusteRuleEntity entity : list) {
                        listArray.add(entity.getName());
                    }

                    String[] leve12Name = plustekService.getLeve1_2Name(list.get(0).getId());
                    if (leve12Name != null && leve12Name.length > 1) {
                        content = deviceEntity.getName() + " " + leve12Name[0] + "-" + leve12Name[1];
                    }

                    initCreateIssue(taskId, deviceId, ruleId3);
                }
                break;
            case StartMode.MODIFY:
                if (TextUtils.isEmpty(ruleResultId) || TextUtils.isEmpty(content)) {
                    showToast("参数错误!");
                    finish();
                    return;
                }
                ruleResultEntity = teamService.getRuleResult(ruleResultId);//获取问题
                maxIntentMinus = (int) ((plustekService.getStandaredMaxDult(ruleResultEntity.getRule_id(), deviceId) - ruleResultEntity.getDeduct_score()) * SCAN_NUM);
                if (ruleResultEntity == null) {
                    showToast("参数错误!");
                    finish();
                    return;
                }
                initEditIssue(taskId, deviceId, ruleResultId, content);

                break;
            case StartMode.COPY:
                if (TextUtils.isEmpty(ruleResultId) || TextUtils.isEmpty(content)) {
                    showToast("参数错误!");
                    finish();
                    return;
                }
                ruleResultEntity = teamService.getRuleResult(ruleResultId);
                maxIntentMinus = (int) (plustekService.getStandaredMaxDult(ruleResultEntity.getRule_id(), deviceId) * SCAN_NUM);
                if (ruleResultEntity == null) {
                    showToast("参数错误!");
                    finish();
                    return;
                }
                initCopyIssue(taskId, deviceId, ruleResultId, content);
                break;
            case StartMode.NOPMS:
                initNoPmsIssue(taskId, deviceId);
                break;
            default:
                break;
        }
    }

    /**
     * 字典初始化,初始化失败直接提示并关闭Activity
     */
    private boolean initDictionary() {
        natureList = dictionaryService.getDictonaryIssueNature();//问题性质-字典
        reasonList = dictionaryService.getDictonaryIssueReason();//问题产生原因-字典
        if (natureList == null || natureList.size() == 0 || reasonList == null || reasonList.size() == 0) {
            return false;
        }
        natureArray = new ArrayList<>(natureList.size());
        reasonArray = new ArrayList<>(reasonList.size());
        for (int i = 0, size = natureList.size(); i < size; i++) {
            natureArray.add(natureList.get(i).getV());
        }
        for (int i = 0, size = reasonList.size(); i < size; i++) {
            reasonArray.add(reasonList.get(i).getV());
        }

        return true;
    }

    /**
     * 问题复制
     */
    private void initCopyIssue(String taskId, String deviceId, String ruleResultId, String content) {
        rule4Entity = plustekService.getIssue(ruleResultEntity.getRule_id());
        ruleResultEntity.setId(null);

        dataBinding.issueInfoTxt.setOnClickListener(null);
        dataBinding.issueEdit.setOnClickListener(null);

        dataBinding.issueInfoTxt.setText(rule4Entity.getName());
        dataBinding.issueEdit.setText(ruleResultEntity.getDescription());
        dataBinding.issueNatureTxt.setText(ruleResultEntity.getProblem_nature());
        dataBinding.blameTeamTxt.setText(ruleResultEntity.getCharge_group());
        dataBinding.blameBranchTxt.setText(ruleResultEntity.getCharge_unit());
        dataBinding.issueReasonTxt.setText(ruleResultEntity.getProduce_reason());
        dataBinding.suggestEdit.setText(ruleResultEntity.getSuggest_deal_way());

        minusScore = 0;
        scoreEntity = (int) (ruleResultEntity.getDeduct_score() * SCAN_NUM);
        if (!TextUtils.isEmpty(rule4Entity.getScore_content())) {
            InspeScoreEntity scoreObject = JSON.parseObject(rule4Entity.getScore_content(), InspeScoreEntity.class);
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
        subStationEntity = deviceService.getSubStation(ruleResultEntity.getBdz_id());
        taskEntity = taskService.getTask(ruleResultEntity.getTask_id());


        if (!TextUtils.isEmpty(rule4Entity.getScore_charge())) {
            String[] branchs = rule4Entity.getScore_charge().split(",");

            listTeamArray.addAll(Arrays.asList(branchs));
            String team = ruleResultEntity.getCharge_group();
            if (team.contains("检修")) {
                listBranchArray.add(subStationEntity.getPower_company());
                listBranchArray.add(taskEntity.getDept_name());
            } else if (team.contains("运维")) {
                listBranchArray.add(subStationEntity.getPower_company());
            }

        }
    }

    /**
     * 设备无PMS
     */
    private void initNoPmsIssue(String taskId, String deviceId) {

    }

    /**
     * 问题编辑初始化
     */
    private void initEditIssue(String taskId, String deviceId, String ruleResultId, String content) {
        if (rule4Entity == null) {//避免重复查询
            rule4Entity = plustekService.getIssue(ruleResultEntity.getRule_id());
        }
        if (rule4Entity == null) {
            showToast("未查询到相关标准");
            finish();
            return;
        }
        dataBinding.issueInfoTxt.setOnClickListener(null);
        dataBinding.issueInfoTxt.setText(rule4Entity.getName());
        dataBinding.issueEdit.setText(ruleResultEntity.getDescription());
        dataBinding.issueNatureTxt.setText(ruleResultEntity.getProblem_nature());
        dataBinding.blameTeamTxt.setText(ruleResultEntity.getCharge_group());
        dataBinding.blameBranchTxt.setText(ruleResultEntity.getCharge_unit());
        dataBinding.issueReasonTxt.setText(ruleResultEntity.getProduce_reason());
        dataBinding.suggestEdit.setText(ruleResultEntity.getSuggest_deal_way());

        if (!TextUtils.isEmpty(ruleResultEntity.getImg())) {
            picList.addAll(Arrays.asList(ruleResultEntity.getImg().split(",")));
            galleryAdapte.notifyDataSetChanged();
        }

        //扣分，修改扣分需要加上本身的扣分
        minusScore = 0;
        scoreEntity = (int) (ruleResultEntity.getDeduct_score() * SCAN_NUM);
        if (!TextUtils.isEmpty(rule4Entity.getScore_content())) {
            InspeScoreEntity scoreObject = JSON.parseObject(rule4Entity.getScore_content(), InspeScoreEntity.class);
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
        subStationEntity = deviceService.getSubStation(ruleResultEntity.getBdz_id());
        taskEntity = taskService.getTask(ruleResultEntity.getTask_id());

        if (!TextUtils.isEmpty(rule4Entity.getScore_charge())) {
            String[] branchs = rule4Entity.getScore_charge().split(",");

            listTeamArray.addAll(Arrays.asList(branchs));
            String team = ruleResultEntity.getCharge_group();
            if (team.contains("检修")) {
                listBranchArray.add(subStationEntity.getPower_company());
                listBranchArray.add(taskEntity.getDept_name());
            } else if (team.contains("运维")) {
                listBranchArray.add(subStationEntity.getPower_company());
            }

        }
    }

    private void initCreateIssue(String taskId, String deviceId, String ruleId3) {

        //描述
        dataBinding.contextTxt.setText(content);
        //初始化
        dataBinding.issueNatureTxt.setText(natureArray.get(0));//问题性质，默认一般
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKEPIC_REQUEST) {
                BitmapUtils.compressImage(FileUtils.getInpseRootPath() + picTempPath, 4);
                String picContent = DateUtils.getFormatterTime(new Date(), InspeConfig.dateFormatYMDHM )+ "\n" + deviceEntity.name + "\n" + taskEntity.checkuser_name;
                drawCircle(FileUtils.getInpseRootPath() + picTempPath, picContent);
            }else if (requestCode==InspeConfig.LOAD_DATA){
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
                String picName = FileUtils.createInpseImgLongName(taskEntity);//生成图片名称
                picTempPath = FileUtils.getInpseImgPath(taskEntity);
                FunctionUtils.takePicture(this, picName, FileUtils.getInpseRootPath() + picTempPath, TAKEPIC_REQUEST);
                //文件相对地址
                picTempPath = picTempPath + picName;
            } else {
                showToast("目前仅支持上传3张图片");
            }
        } else if (i == R.id.issueInfoTxt) {//错误问题
            new PopItemWindow(this).setListAdapter(listArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    PlusteRuleEntity entity = list.get(position);
                    //判断是否为统一选择，同一选择不处理
                    if (rule4Entity == entity) {
                        return;
                    }
                    rule4Entity = entity;

                    //判断上次选择是否有记录问题，如果有需要将图片冲删除列表中移除
                    if (ruleResultEntity != null) {
                        for (String picName : picList) {//先添加后移除，清理新添加的图片
                            picDeleteList.add(picName);
                        }
                        if (!TextUtils.isEmpty(ruleResultEntity.getImg())) {
                            String[] imsg = ruleResultEntity.getImg().split(",");
                            for (String pic : imsg) {
                                picDeleteList.remove(pic);//将删除的图片从列表中移除
                            }
                        }
                        picList.clear();
                        galleryAdapte.notifyDataSetChanged();
                    }

                    ruleResultEntity = teamService.getRuleResult(list.get(position).getId(), taskEntity.getId());//查询数据库是否对该问题进行过记录
                    if (ruleResultEntity != null) {//初始化修改UI
                        //对图片进行销毁
                        for (String picName : picList) {//先添加后移除，清理新添加的图片
                            picDeleteList.add(picName);
                        }
                        picDeleteList.clear();
                        galleryAdapte.notifyDataSetChanged();

                        maxIntentMinus = (int) ((plustekService.getStandaredMaxDult(ruleResultEntity.getRule_id(), deviceId) + ruleResultEntity.getDeduct_score()) * SCAN_NUM);
                        initEditIssue(taskId, deviceId, ruleResultId, content);
                        return;
                    }
                    //问题描述
                    dataBinding.issueInfoTxt.setText(entity.getName());
                    //dataBinding.issueEdit.setText(entity.getName().replaceAll("的{0,},{0,}，{0,}[扣].*分.*", "").replaceAll("[0-9].]{0,}[a-z)]{0,}[a-z]{0,}", ""));

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
                                listBranchArray.add(taskEntity.getDept_name());

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
        } else if (i == R.id.issueEdit) {
            new BreakWordDialog(this).setBreakList(rule3Entity.getName(), rule4Entity.getName()).setOnCheckListener(new BreakWordDialog.OnCheckedListener() {
                @Override
                public void onChecked(String msg) {
                    dataBinding.issueEdit.setText(msg);
                }
            }).show();
        } else if (i == R.id.addBtn) {//+扣分
            setScoreTxt(scoreEntity);
        } else if (i == R.id.minuxBtn) {//-扣分
            setScoreTxt(-scoreEntity);
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
                        listBranchArray.add(taskEntity.getDept_name());
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
                dataBinding.issueEdit.performClick();
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
//            } else if (TextUtils.isEmpty(dataBinding.blameTeamTxt.getText())) {
//                showToast("请选择责任班组");
//                dataBinding.blameTeamTxt.performClick();
//                return;
//            } else if (TextUtils.isEmpty(dataBinding.blameBranchTxt.getText())) {
//                showToast("请选择责任单位");
//                dataBinding.blameBranchTxt.performClick();
//                return;
            } else if (TextUtils.isEmpty(dataBinding.issueReasonTxt.getText())) {
                showToast("请选择产生原因");
                dataBinding.issueReasonTxt.performClick();
                return;
//                } else if (TextUtils.isEmpty(dataBinding.suggestEdit.getText().toString().trim())) {
//                    showToast("请输入处理措施");
//                    dataBinding.suggestEdit.performClick();
//                    return;
            } else if (picList.size() == 0) {
                showToast("请对问题拍照处理");
            }

            createIssue();//满足输入，创建错误
        }

    }

    /**
     * 创建错误
     */
    private void createIssue() {
        if (ruleResultEntity == null) {
            ruleResultEntity = new TeamRuleResultEntity();
        }
        //创建ID
        if (ruleResultEntity.getId() == null) {//创建或覆盖
            ruleResultEntity.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        //任务ID
        ruleResultEntity.setTask_id(taskEntity.id);
        //标准ID
        ruleResultEntity.setRule_id(rule4Entity.getId());
        ruleResultEntity.setRule_name(rule4Entity.getName());
        //被检查班组ID
        ruleResultEntity.setDept_id(taskEntity.getDept_id());
        ruleResultEntity.setDept_name(taskEntity.getDept_name());
        //检查人ID
        ruleResultEntity.setCheck_person_id(expertEntity.getId());
        ruleResultEntity.setCheck_person_name(expertEntity.getUsername());
        ruleResultEntity.setCheck_type(taskEntity.getType());

        //精益化评价独有
        //问题性质
        ruleResultEntity.setProblem_nature(dataBinding.issueNatureTxt.getText().toString());
        //问题原因
        ruleResultEntity.setProduce_reason(dataBinding.issueReasonTxt.getText().toString());
        //责任班组
        ruleResultEntity.setCharge_group(dataBinding.blameTeamTxt.getText().toString());
        //责任单位
        ruleResultEntity.setCharge_unit(dataBinding.blameBranchTxt.getText().toString());
        //建议处理方式
        ruleResultEntity.setSuggest_deal_way(dataBinding.suggestEdit.getText().toString().trim());

        ruleResultEntity.setBdz_id(subStationEntity.getBdzid());//变电站ID
        ruleResultEntity.setBdz_name(subStationEntity.getName());//变电站Name
        if (TextUtils.isEmpty(ruleResultEntity.getDevice_id())) {//修改不用设置，因为deviceEntity is null.
            ruleResultEntity.setDevice_id(deviceEntity.getDeviceid());//设备ID
            ruleResultEntity.setDevice_name(deviceEntity.getName());//设备Name
            ruleResultEntity.setDevice_bigtype(deviceEntity.getBigid());//设备大类
        }

        //问题类型(记录类型：问题（answer）、普通记录（normal）)
        ruleResultEntity.setRecord_type(RecordType.answer.name());
        //扣分情况
        ruleResultEntity.setDeduct_score(minusScore * 1.0f / SCAN_NUM);//放大数据后对数据进行缩小
        //问题描述
        ruleResultEntity.setDescription(dataBinding.issueEdit.getText().toString().trim());
        //扣分原因jsonArray
//                ruleResultEntity.setReason(JSON.toJSONString(new InspeScoreEntity[]{scoreBean}));
        //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
        ruleResultEntity.setProgress(ProgressType.wfp.name());
        //整改期限
        ruleResultEntity.setPlan_improve_time(DateFormat.dateToDbString000(System.currentTimeMillis()));//默认当天


        //图片(疑问？是否需要上传)
        ruleResultEntity.setImg(ArrayInspeUtils.toListString(picList));
        //检查时间
        String datetime = DateFormat.dateToDbString(System.currentTimeMillis());
        if (ruleResultEntity.getCreate_time() == null) {
            ruleResultEntity.setCreate_time(datetime);
        }
        if (ruleResultEntity.getInsert_time() == null) {
            ruleResultEntity.setInsert_time(datetime);
        }
        ruleResultEntity.setLast_modify_time(datetime);

        //更新任务状态
        if (taskEntity.getProgress() == null || TaskProgressType.valueOf(taskEntity.getProgress()) == TaskProgressType.todo) {
            taskEntity.setProgress(TaskProgressType.doing.name());
            taskService.updateTask(taskEntity);
        }

        if (teamService.saveRuleResult(ruleResultEntity)) {
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
            if (ruleResultEntity != null) {//重新编辑
                for (String pic : picList) {
                    picDeleteList.add(pic);//添加保存的图片
                }
                if (!TextUtils.isEmpty(ruleResultEntity.getImg())) {
                    String[] imsg = ruleResultEntity.getImg().split(",");
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

    /**
     * Intent参数KEY
     */
    public interface IntentKey {
        /**
         * 页面启动模式-KEY
         */
        String START_MODE = "start_mode";
        /**
         * 检查类型
         */
        String PLUSTEK_TYPE = "plustek_type";
        /**
         * 任务ID
         */
        String TASK_ID = "task_id";
        /**
         * 设备ID
         */
        String DEVICE_ID = "device_id";
        /**
         * 标准ID(level=3)
         */
        String RULE_ID_3 = "rule_id_3";
        /**
         * 问题ID
         */
        String RULE_RESULT_ID = "rule_ruslt_id";
        /**
         * 描述内容
         */
        String CONTENT = "content";
    }

    /**
     * 启动模式
     */
    public interface StartMode {
        /**
         * 默认，创建问题
         */
        int DEFAULT = 0;
        /**
         * 修改记录的问题
         */
        int MODIFY = 1;
        /**
         * 复制问题
         */
        int COPY = 2;
        /**
         * 无PMS台账设备记录
         */
        int NOPMS = 3;
    }
}
