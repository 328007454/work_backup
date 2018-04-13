package com.cnksi.bdzinspection.czp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.czp.adapter.OperateWorkItemAdapter;
import com.cnksi.bdzinspection.daoservice.OperateItemService;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateWorkBinding;
import com.cnksi.bdzinspection.databinding.XsDialogOperateTipsBinding;
import com.cnksi.bdzinspection.fragment.Camera2VideoFragment;
import com.cnksi.bdzinspection.fragment.CameraVideoFragment;
import com.cnksi.bdzinspection.model.OperateItem;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.bdzinspection.utils.CommonUtils;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.OperateTaskStatus;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.xscore.xscommon.ScreenManager;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;

import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class OperateWorkActivity extends BaseActivity {

    private AnimationDrawable mRecordAnimation;

    private String currentOperateId;
    private List<OperateItem> dataList = null;
    private OperateWorkItemAdapter mAdapter = null;
    private int currentOperateItemPosition = -1;
    private OperateTick mCurrentOperateTick;
    /**
     * 录音文件的名称
     */
    private String audioFileName = "", videoFileName = "";
    private Camera2VideoFragment mFragment2;
    private CameraVideoFragment mFragment;
    // 是否正在录像
    private boolean isRecordingVideo;
    // 是否正在录音
    private boolean isRecordingAudio;
    private XsActivityOperateWorkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_operate_work);


        initUI();
        initData();
        initOnClick();
    }

    private void initUI() {
        binding.includeTitle.tvTitle.setText(R.string.xs_operate_task_str);
        binding.includeTitle.ibtnExit.setVisibility(View.VISIBLE);
        binding.includeTitle.ibtnExit.setImageResource(R.drawable.xs_ic_pause);
        currentOperateId = getIntent().getStringExtra(Config.CURRENT_TASK_ID);
        mRecordAnimation = (AnimationDrawable) binding.ivRecordAudio.getDrawable();
    }

    private void initData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                mCurrentOperateTick = OperateTicketService.getInstance().findById(currentOperateId);
                dataList = OperateItemService.getInstance().findAllOperateItemByTaskId(currentOperateId);
                String currentTime = DateUtils.getCurrentLongTime();
                if (dataList != null) {
                    for (int i = 0, count = dataList.size(); i < count; i++) {
                        OperateItem item = dataList.get(i);
                        if (TextUtils.isEmpty(item.spend_time)) {
                            if (TextUtils.isEmpty(item.time_start)) {
                                item.time_start = currentTime;
                            }
                            currentOperateItemPosition = i;
                            break;
                        }
                    }
                }
                if (TextUtils.isEmpty(mCurrentOperateTick.time_operate_start)) {
                    mCurrentOperateTick.time_operate_start = currentTime;
                    OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.TIME_OPERATE_START);
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });

        binding.rlFunctionContainer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                if (mAdapter == null) {
                    mAdapter = new OperateWorkItemAdapter(currentActivity, dataList);
                    mAdapter.currentOperateItemPosition = currentOperateItemPosition;
                    binding.lvContainer.setAdapter(mAdapter);
                    binding.lvContainer.setSelection(currentOperateItemPosition);
                } else {
                    mAdapter.setList(dataList);
                }
                if (!OperateTaskStatus.yzt.name().equalsIgnoreCase(mCurrentOperateTick.status)) {
                    recordAudio(true);
                } else {
                    binding.rlFunctionContainer.setVisibility(View.VISIBLE);
                    binding.ibtnStart.setVisibility(View.VISIBLE);
                    binding.llFunctionContainer.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> onBackPressed());

        binding.rlAudio.setOnClickListener(view -> recordAudio(true));

        binding.rlVideo.setOnClickListener(view -> recordVideo(true));

        binding.btnConfirm.setOnClickListener(view -> completeOperateTask());

        binding.ibtnStopAudio.setOnClickListener(view -> recordAudio(false));

        binding.ibtnStopVideo.setOnClickListener(view -> recordVideo(false));

        binding.ibtnStart.setOnClickListener(view -> {
            startOperateTask();
        });
        binding.btnPause.setOnClickListener(view -> {
            pauseOperateTask();
        });

        binding.btnStop.setOnClickListener(view -> {
            DialogUtils.showSureTipsDialog(currentActivity, binding.llRootContainer,
                    getString(R.string.xs_sure_stop_operate_task_str), new OnViewClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopOperateTask();
                        }
                    });
        });

        binding.btnCancel.setOnClickListener(view -> {
            binding.rlFunctionContainer.setVisibility(View.GONE);
        });
        binding.includeTitle.ibtnExit.setOnClickListener(view -> {
            binding.rlFunctionContainer.setVisibility(View.VISIBLE);
        });


        binding.lvContainer.setOnItemClickListener((parent,view,position,id) -> {
            OperateItem item = (OperateItem) parent.getItemAtPosition(position);
            if (position == currentOperateItemPosition) {
                showOperateContentTips(item);
            } else if (TextUtils.isEmpty(item.time_start)) {
                CToast.showShort(currentActivity, "你正在进行越项操作，请注意!");
                TTSUtils.getInstance().startSpeaking("你正在进行越项操作，请注意!");
            }
        });

        binding.lvContainer.setOnItemLongClickListener((parent,view,position,id) ->{
            binding.rlFunctionContainer.setVisibility(View.VISIBLE);
            return true;
        });
    }


    /**
     * 显示操作项目内容
     */

    XsDialogOperateTipsBinding tipsBinding;

    private void showOperateContentTips(OperateItem item) {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            tipsBinding = XsDialogOperateTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        tipsBinding.tvDialogContent.setText(item.content);
        tipsDialog.show();

        tipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });

        tipsBinding.btnSure.setOnClickListener(view -> {
            // TODO:保存改操作项目的耗时并开始下一条操作项目
            item.time_end = DateUtils.getCurrentLongTime();
            item.spend_time = CommonUtils.getTimeDifference(item.time_start, item.time_end);
            OperateItemService.getInstance().update(item, OperateItem.TIME_END, OperateItem.SPEND_TIME,
                    OperateItem.TIME_START);
            currentOperateItemPosition++;
            if (currentOperateItemPosition < dataList.size()) {
                OperateItem nextItem = dataList.get(currentOperateItemPosition);
                nextItem.time_start = item.time_end;
                OperateItemService.getInstance().update(nextItem, OperateItem.TIME_START);
            } else {
                // TODO:最后一个
                binding.btnConfirm.setEnabled(true);
                binding.btnConfirm.setBackgroundResource(R.drawable.xs_grass_green_button_background_selector);
            }
            mAdapter.setCurrentOperateItemPosition(currentOperateItemPosition);
            binding.lvContainer.setSelection(currentOperateItemPosition);
        });
    }


    /**
     * 开始录音
     */
    private void recordAudio(boolean isStart) {
        if (isRecordingVideo) {
            CToast.showShort(currentActivity, "请先停止录像再录音~");
            return;
        }
        if (isStart) {
            isRecordingAudio = true;
            mRecordAnimation.start();
            if (!MediaRecorderUtils.getInstance().isRecording()) {
                audioFileName = DateUtils.getCurrentTime(CoreConfig.dateFormat6) + Config.AMR_POSTFIX;
                MediaRecorderUtils.getInstance().startRecordAudio(Config.AUDIO_FOLDER + audioFileName);
                binding.tvAudioTime.start();
                binding.tvAudioTime.setBase(SystemClock.elapsedRealtime());
            }
        } else {
            isRecordingAudio = false;
            mRecordAnimation.stop();
            MediaRecorderUtils.getInstance().stopRecordAudio();
            binding.tvAudioTime.stop();
            saveAudio();
        }
        setAudioVideoLayout(isStart, true);
    }

    /**
     * 设置录音录像显示
     */
    private void setAudioVideoLayout(boolean isRecording, boolean isRecordAudio) {
        if (isRecordAudio) {
            binding.tvAudioTime.setBase(SystemClock.elapsedRealtime());
            binding.tvAudioTime.start();
            binding.rlAudioContainer.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            binding.ibtnStopAudio.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            binding.tvStartAudio.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        } else {
            binding.tvVideoTime.setBase(SystemClock.elapsedRealtime());
            binding.tvVideoTime.start();
            binding.rlVideoContainer.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            binding.ibtnStopVideo.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            binding.tvStartVideo.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            binding.llContainer.setVisibility(isRecording ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (OperateTaskStatus.yzt.name().equalsIgnoreCase(mCurrentOperateTick.status)) {
            Intent intent = new Intent(currentActivity, OperateTaskListActivity.class);
            intent.putExtra(Config.IS_FROM_BATTERY, true);
            startActivity(intent);
        } else {
            if (isRecordingAudio) {
                recordAudio(false);
            }
            if (isRecordingVideo) {
                recordVideo(false);
            }
        }
        this.finish();
    }

    /**
     * 保存录音
     */
    private void saveAudio() {
        if (TextUtils.isEmpty(mCurrentOperateTick.voice)) {
            mCurrentOperateTick.voice = audioFileName;
        } else if (mCurrentOperateTick.voice.endsWith(CoreConfig.COMMA_SEPARATOR)
                && !mCurrentOperateTick.voice.contains(audioFileName)) {
            mCurrentOperateTick.voice += audioFileName;
        } else if (!mCurrentOperateTick.voice.contains(audioFileName)) {
            mCurrentOperateTick.voice += (CoreConfig.COMMA_SEPARATOR + audioFileName);
        }
        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.VOICE);
    }

    /**
     * 保存录像
     */
    private void saveVideo() {
        if (TextUtils.isEmpty(mCurrentOperateTick.video)) {
            mCurrentOperateTick.video = videoFileName;
        } else if (mCurrentOperateTick.video.endsWith(CoreConfig.COMMA_SEPARATOR)
                && !mCurrentOperateTick.video.contains(videoFileName)) {
            mCurrentOperateTick.video += videoFileName;
        } else if (!mCurrentOperateTick.video.contains(videoFileName)) {
            mCurrentOperateTick.video += (CoreConfig.COMMA_SEPARATOR + videoFileName);
        }
        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.VIDEO);
    }

    /**
     * 停止操作票
     */
    private void stopOperateTask() {
        if (isRecordingAudio) {
            recordAudio(false);
        }
        if (isRecordingVideo) {
            recordVideo(false);
        }
        mCurrentOperateTick.status = OperateTaskStatus.ytz.name();
        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.STATUS, OperateTick.VOICE,
                OperateTick.VIDEO);
        Intent intent = new Intent(currentActivity, OperateTaskListActivity.class);
        intent.putExtra(Config.IS_FROM_BATTERY, true);
        startActivity(intent);
        this.finish();
    }

    /**
     * 暂停操作票
     */
    private void pauseOperateTask() {
        binding.ibtnStart.setVisibility(View.VISIBLE);
        binding.llFunctionContainer.setVisibility(View.GONE);
        // TODO: 停止录音 录像
        if (isRecordingAudio) {
            recordAudio(false);
        }
        if (isRecordingVideo) {
            recordVideo(false);
        }
        mCurrentOperateTick.status = OperateTaskStatus.yzt.name();
        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.STATUS, OperateTick.VOICE,
                OperateTick.VIDEO);
        // 暂停状态记录结束时间
        OperateItem mOperateItem = dataList.get(currentOperateItemPosition);
        mOperateItem.time_end = DateUtils.getCurrentLongTime();
        OperateItemService.getInstance().update(mOperateItem, OperateItem.TIME_END);
    }

    /**
     * 开始操作票
     */
    private void startOperateTask() {
        binding.rlFunctionContainer.setVisibility(View.GONE);
        binding.ibtnStart.setVisibility(View.GONE);
        binding.llFunctionContainer.setVisibility(View.VISIBLE);
        // TODO: 继续录音 计时
        recordAudio(true);
        if (!OperateTaskStatus.wwc.name().equalsIgnoreCase(mCurrentOperateTick.status)) {
            mCurrentOperateTick.status = OperateTaskStatus.wwc.name();
            OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.STATUS, OperateTick.VOICE,
                    OperateTick.VIDEO);
        }
        // 重新开始（继续）记录开始时间
        OperateItem mOperateItem = dataList.get(currentOperateItemPosition);
        mOperateItem.time_start = DateUtils.getCurrentLongTime();
        OperateItemService.getInstance().update(mOperateItem, OperateItem.TIME_START);
    }

    /**
     * 完成操作票
     */
    private void completeOperateTask() {
        if (isRecordingAudio) {
            recordAudio(false);
        }
        if (isRecordingVideo) {
            recordVideo(false);
        }
        mCurrentOperateTick.time_operate_end = DateUtils.getCurrentLongTime();
        mCurrentOperateTick.status = OperateTaskStatus.ywc.name();
        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.STATUS,
                OperateTick.TIME_OPERATE_END);
        // TODO:完成操作
        Intent intent = new Intent(currentActivity, OperateTicketReportActivity.class);
        intent.putExtra(Config.CURRENT_TASK_ID, currentOperateId);
        startActivity(intent);
        ScreenManager.getInstance().popActivity(OperateTaskDetailsActivity.class);
        this.finish();

    }

    /**
     * 开始录像
     */
    private void recordVideo(boolean isStart) {
        setAudioVideoLayout(isStart, false);
        if (isStart) {
            isRecordingVideo = true;
            binding.tvVideoTime.setBase(SystemClock.elapsedRealtime());
            binding.tvVideoTime.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFragment2 = Camera2VideoFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.ll_container, mFragment2).commit();
            } else {
                mFragment = CameraVideoFragment.newInstance();
                getFragmentManager().beginTransaction().replace(R.id.ll_container, mFragment).commit();
            }
        } else {
            isRecordingVideo = false;
            binding.tvVideoTime.stop();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                videoFileName = mFragment2.stopRecordingVideo();
                getFragmentManager().beginTransaction().remove(mFragment2).commit();
            } else {
                videoFileName = mFragment.stopRecordingVideo();
                getFragmentManager().beginTransaction().remove(mFragment).commit();
            }
            if (!TextUtils.isEmpty(videoFileName)) {
                saveVideo();
            }
        }
        recordAudio(!isStart);
    }
}