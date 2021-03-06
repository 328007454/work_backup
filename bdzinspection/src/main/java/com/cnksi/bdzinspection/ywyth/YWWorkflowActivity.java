/**
 *
 */
package com.cnksi.bdzinspection.ywyth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.daoservice.PlanProcessStatusService;
import com.cnksi.bdzinspection.daoservice.ProcessService;
import com.cnksi.bdzinspection.databinding.XsActivityYwythWorkflowBinding;
import com.cnksi.bdzinspection.databinding.XsDialogInputBinding;
import com.cnksi.bdzinspection.databinding.XsRecordAudioDialogBinding;
import com.cnksi.bdzinspection.databinding.XsYunweiliuchengAdapterBinding;
import com.cnksi.bdzinspection.model.PlanProcessStatus;
import com.cnksi.bdzinspection.model.Process;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.MediaRecorderUtils;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.swipemenulist.SwipeMenu;
import com.cnksi.core.view.swipemenulist.SwipeMenuCreator;
import com.cnksi.core.view.swipemenulist.SwipeMenuDragSortListView.OnMenuItemClickListener;
import com.cnksi.core.view.swipemenulist.SwipeMenuItem;
import com.cnksi.core.view.swipemenulist.SwipeMenuLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * @author wastrel
 * @Date 2016-3-24
 */
public class YWWorkflowActivity extends BaseActivity {

    List<PlanProcessStatus> plan;
    private Dialog dialog;
    private int selectIndex = -1;
    private String currentImageName = "";
    private String currentAudioName = "";
    private String currentVideoName = "";

    public final static String PLANDATA = "PLANDATA";
    public final static String PROCESSDATA = "PROCESSDATA";
    List<com.cnksi.bdzinspection.model.Process> dlist;
    private boolean taskStatus = false;
    private YWWorkflowAdapter adapter;
    /**
     * 录音计时
     */
    private RecorderTimer mRecorderTimer = null;
    /**
     * 录音的Dialog
     */
    private Dialog mRecordAudioDialg = null;
    private XsActivityYwythWorkflowBinding binding;

    /*
     * (non-Javadoc)
     * @see com.cnksi.common.base.BaseActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_ywyth_workflow);
        init();
        initOnClick();
    }


    void closeMenu() {
        try {
            Field field = binding.list.getClass().getDeclaredField("mTouchView");
            field.setAccessible(true);
            SwipeMenuLayout mt = (SwipeMenuLayout) field.get(binding.list);
            mt.smoothCloseMenu();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void init() {
        getIntentValue();
        taskStatus = TaskService.getInstance().getTaskStatusForBoolean(currentTaskId);
        binding.tvTitle.setText(currentInspectionTypeName);
        dlist = ProcessService.getInstance().findWorkflowById(currentInspectionType);
        plan = PlanProcessStatusService.getInstance().getProcessStatus(currentTaskId, currentInspectionType);

        if (dlist == null) {
            dlist = new ArrayList<>();
        }
        adapter = new YWWorkflowAdapter(mActivity, dlist);
        binding.list.setAdapter(adapter);
        if ("17".equals(currentInspectionType)) {
            binding.btnReport.setVisibility(View.VISIBLE);
        }

        binding.list.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent(mActivity, YWWorkflowDescActivity.class);
            intent.putExtra(PLANDATA, plan.get(position));
            intent.putExtra(PROCESSDATA, dlist.get(position));
            startActivity(intent);
        });
        if (!taskStatus) {
            createSwipeMenu();
            setOnItemClick();
        } else {
            binding.btnComplete.setText(R.string.xs_back_str);
        }

    }


    XsDialogInputBinding inputBinding;

    private void initOnClick() {
        binding.ibtnCancel.setOnClickListener(view -> onBackPressed());
        binding.btnReport.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, YWBatteryActivity.class);
            startActivity(intent);
        });
        binding.gqj.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, YWGQJActivity.class);
            startActivity(intent);
        });

        binding.btnComplete.setOnClickListener(view -> {
            if (taskStatus) {
                finish();
            } else {
                inputBinding = XsDialogInputBinding.inflate(getLayoutInflater());
                dialog = DialogUtils.createDialog(mActivity, inputBinding.getRoot(), (int) (ScreenUtils.getScreenWidth(mActivity) * 0.9),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();

                inputBinding.btnSure.setOnClickListener(view1 -> {
                    dialog.dismiss();
                    String str = inputBinding.edit.getText().toString();
                    ProcessService.getInstance().saveRemain_Problem(currentTaskId, str);
                    boolean isfinish = true;
                    for (PlanProcessStatus bean : plan) {
                        if ("0".equals(bean.is_selected)) {
                            isfinish = false;
                            break;
                        }
                    }
                    if (isfinish) {
                        TaskService.getInstance().finishTask(currentTaskId);

                    }
                    startActivity(new Intent(mActivity, YunweiReportActivity.class));
                    if (isfinish) {
                        setResult(RESULT_OK);
                        mActivity.finish();

                    }
                });
                inputBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
            }
        });
    }


    public class YWWorkflowAdapter extends SimpleBaseAdapter {

        /**
         * @param context
         */
        public YWWorkflowAdapter(Context context, List<? extends Object> datalist) {
            super(context, datalist);
            // TODO Auto-generated constructor stub

        }

        /*
         * (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            XsYunweiliuchengAdapterBinding itemBinding;
            if (convertView == null) {
                itemBinding = XsYunweiliuchengAdapterBinding.inflate(getLayoutInflater());
                AutoUtils.autoSize(itemBinding.getRoot());
            } else {
                itemBinding = DataBindingUtil.findBinding(convertView);
            }
            final Process bean = (Process) getItem(position);
            itemBinding.textDesc.setText(bean.content);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
            String str = calcSum(plan.get(position));
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(redSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            itemBinding.textDesc.append(spannableString);
            itemBinding.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // TODO Auto-generated method stub

                if (plan.get(position).is_selected == (isChecked ? "1" : "0")) {

                } else {
                    plan.get(position).is_selected = isChecked ? "1" : "0";
                    PlanProcessStatusService.getInstance().saveOrUpdate(plan.get(position));
                }

            });
            if ("1".equals(plan.get(position).is_selected)) {
                itemBinding.check.setChecked(true);
            } else {
                itemBinding.check.setChecked(false);
            }
            if (taskStatus) {
                itemBinding.llck.setVisibility(View.INVISIBLE);
            }
            itemBinding.llck.setOnClickListener(view -> itemBinding.check.toggle());

            return itemBinding.getRoot();
        }


        String calcSum(PlanProcessStatus p) {

            int l = 0;
            if (!StringUtils.isEmpty(p.video)) {
                l = p.video.split(Config.COMMA_SEPARATOR).length;
            }
            if (!StringUtils.isEmpty(p.picture)) {
                l += p.picture.split(Config.COMMA_SEPARATOR).length;
            }
            if (!StringUtils.isEmpty(p.audio)) {
                l += p.audio.split(Config.COMMA_SEPARATOR).length;
            }
            if (l == 0) {
                return "";
            } else {
                return "(" + l + ")";
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.cnksi.common.base.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    /**
     * 创建侧滑菜单
     */
    private void createSwipeMenu() {
        // step 1. create a MenuCreator
        final SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                createMenu(menu);
            }

            private void createMenu(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
                item1.setBackground(R.drawable.xs_swipe_menu_record_defect_background_selector);
                item1.setWidth(AutoUtils.getPercentWidthSize( 75));
                item1.setIcon(R.drawable.xs_icon_paizhao);
                item1.setTitle(R.string.xs_yw_take_picture);
                item1.setTitleColor(getResources().getColor(android.R.color.white));
                item1.setTitleSize(getResources().getDimension(R.dimen.xs_swipe_menu_text_size));
                menu.addMenuItem(item1);

                SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
                item2.setBackground(R.drawable.xs_swipe_menu_reference_photo_background_selector);
                item2.setWidth(AutoUtils.getPercentWidthSize( 75));
                item2.setIcon(R.drawable.xs_icon_luying);
                item2.setTitle(R.string.xs_yw_take_voice);
                item2.setTitleColor(getResources().getColor(android.R.color.white));
                item2.setTitleSize(getResources().getDimension(R.dimen.xs_swipe_menu_text_size));
                menu.addMenuItem(item2);

                SwipeMenuItem item3 = new SwipeMenuItem(getApplicationContext());
                item3.setBackground(R.drawable.xs_swipe_menu_reference_standard_background_selector);
                item3.setWidth(AutoUtils.getPercentWidthSize( 75));
                item3.setIcon(R.drawable.xs_icon_luxiang);
                item3.setTitle(R.string.xs_yw_take_vedio);
                item3.setTitleColor(getResources().getColor(android.R.color.white));
                item3.setTitleSize(getResources().getDimension(R.dimen.xs_swipe_menu_text_size));
                menu.addMenuItem(item3);
            }
        };
        // set creator
        binding.list.setMenuCreator(creator);
    }

    /**
     * 设置ListView的侧滑菜单的点击事件
     */
    private void setOnItemClick() {

        binding.list.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            Intent intent = null;

            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                closeMenu();
                selectIndex = position;
                switch (index) {
                    case 0: // 拍照
                        FunctionUtil.takePicture(mActivity,
                                (currentImageName = FunctionUtil.getCurrentImageName(mActivity)),
                                Config.CUSTOMER_PICTURES_FOLDER);
                        break;
                    case 1:
                        // 录音
                        if (!MediaRecorderUtils.getInstance().isRecording()) {

                            currentAudioName = DateUtils.getCurrentTime(DateUtils.yyyyMMddHHmmssSSS) + Config.AMR_POSTFIX;
                            MediaRecorderUtils.getInstance().startRecordAudio(Config.AUDIO_FOLDER + currentAudioName);
                            String str = plan.get(selectIndex).audio;
                            plan.get(selectIndex).audio = StringUtils.isEmpty(str) ? currentAudioName
                                    : str + "," + currentAudioName;
                            adapter.notifyDataSetChanged();
                            PlanProcessStatusService.getInstance().saveOrUpdate(plan.get(selectIndex));
                            if (mRecorderTimer == null) {
                                mRecorderTimer = new RecorderTimer(11000, 1000);
                            }
                            mRecorderTimer.start();
                            showRecordAudioDialog();

                        } else {
                            ToastUtils.showMessage("当前正在录音");
                        }
                        break;
                    case 2:// 录像
                        takeVideo(Config.VIDEO_FOLDER + (currentVideoName = DateUtils.getCurrentTime(DateUtils.yyyyMMddHHmmssSSS)
                                + Config.MP4_POSTFIX));

                        break;
                    default:
                }

            }

        });

    }

    /**
     * 显示录音时间的dialog
     */
    private XsRecordAudioDialogBinding audioDialogBinding;

    private void showRecordAudioDialog() {
        if (mRecordAudioDialg == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) / 2;
            audioDialogBinding = XsRecordAudioDialogBinding.inflate(getLayoutInflater());
            mRecordAudioDialg = DialogUtils.createDialog(mActivity, audioDialogBinding.getRoot(), dialogWidth, dialogWidth);
            mRecordAudioDialg.setOnDismissListener(dialog -> {
                MediaRecorderUtils.getInstance().stopRecordAudio();
                if (mRecorderTimer != null) {
                    mRecorderTimer.cancel();
                }
            });
        }
        audioDialogBinding.llDialogContainer.setOnClickListener(view -> mRecordAudioDialg.dismiss());
        mRecordAudioDialg.show();
    }

    /*
     * (non-Javadoc)
     * @see com.cnksi.core.activity.BaseCoreActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (RESULT_OK == resultCode) {
            String str = "";
            switch (requestCode) {
                case ACTION_IMAGE:
                    str = plan.get(selectIndex).picture;
                    plan.get(selectIndex).picture = StringUtils.isEmpty(str) ? currentImageName
                            : str + "," + currentImageName;
                    PlanProcessStatusService.getInstance().saveOrUpdate(plan.get(selectIndex));
                    adapter.notifyDataSetChanged();
                    break;
                case ACTION_RECORDVIDEO:
                    str = plan.get(selectIndex).video;
                    plan.get(selectIndex).video = StringUtils.isEmpty(str) ? currentVideoName
                            : str + "," + currentVideoName;
                    PlanProcessStatusService.getInstance().saveOrUpdate(plan.get(selectIndex));
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class RecorderTimer extends CountDownTimer {

        public RecorderTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (mRecordAudioDialg != null) {
                mRecordAudioDialg.dismiss();
                mRecordAudioDialg = null;
            }
        }
    }
}
