package com.cnksi.bdzinspection.czp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.activity.SignNameActivity;
import com.cnksi.bdzinspection.czp.adapter.OperateWorkItemAdapter;
import com.cnksi.bdzinspection.daoservice.OperateItemService;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsActivityOperateTaskCheckedBinding;
import com.cnksi.bdzinspection.emnu.OperateTaskStatus;
import com.cnksi.bdzinspection.model.OperateItem;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.common.Config;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 审核操作票界面
 *
 * @author Oliver
 */
public class OperateTaskCheckedActivity extends BaseActivity {

    public static final int CZR_SIGN_CODE = 0X111;
    public static final int CZFZR_SIGN_CODE = CZR_SIGN_CODE + 1;
    public static final int ZBFZR_SIGN_CODE = CZFZR_SIGN_CODE + 1;


    private List<OperateItem> dataList = null;
    private OperateWorkItemAdapter mAdapter = null;
    /**
     * 操作任务Id
     */
    private String currentOperateId = "1";
    private OperateTick mCurrentOperateTick;
    private boolean isUpdateStatus = false;
    private boolean scrollFlag = false;// 标记是否滑动
    private int lastVisibleItemPosition = 0;// 标记上次滑动位置

    private XsActivityOperateTaskCheckedBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_operate_task_checked);

        initialUI();
        initialData();
        initOnClick();
    }

    private void initialUI() {
        currentOperateId = getIntent().getStringExtra(Config.CURRENT_TASK_ID);
        isUpdateStatus = getIntent().getBooleanExtra(Config.IS_FROM_BATTERY, false);
        binding.includeTitle.tvTitle.setText(R.string.xs_check_operate_tickets_str);
    }

    private void initialData() {
        ExecutorManager.executeTask(() -> {
            try {
                mCurrentOperateTick = OperateTicketService.getInstance().findById(currentOperateId);
            } catch (DbException e) {
                e.printStackTrace();
            }
            dataList = OperateItemService.getInstance().findAllOperateItemByTaskId(currentOperateId);
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
        binding.lvContainer.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                switch (scrollState) {
                    // 当不滚动时
                    case OnScrollListener.SCROLL_STATE_IDLE:// 是当屏幕停止滚动时
                        scrollFlag = false;
                        // 判断滚动到底部
                        if (binding.lvContainer.getLastVisiblePosition() == (binding.lvContainer.getCount() - 1)) {

                        }
                        // 判断滚动到顶部
                        if (binding.lvContainer.getFirstVisiblePosition() == 0) {

                        }
                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        scrollFlag = true;
                        break;
                    case OnScrollListener.SCROLL_STATE_FLING:// 是当用户由于之前划动屏幕并抬起手指，屏幕产生惯性滑动时
                        scrollFlag = false;
                        break;
                    default:
                        break;
                }
            }

            /**
             * firstVisibleItem：当前能看见的第一个列表项ID（从0开始） visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 当开始滑动且ListView底部的Y轴点超出屏幕最大范围时，显示或隐藏顶部按钮
                if (scrollFlag) {
                    if (firstVisibleItem > lastVisibleItemPosition) {// 上滑
                        if (binding.llOperateInfoContainer.getVisibility() == View.VISIBLE) {
                            binding.llOperateInfoContainer.setVisibility(View.GONE);
                            binding.tvOperateInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xs_ic_gray_drop_down, 0);
                        }
                    } else if (firstVisibleItem < lastVisibleItemPosition) {// 下滑

                    } else {
                        return;
                    }
                    lastVisibleItemPosition = firstVisibleItem;
                }
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
                    mAdapter.isCheckedOperate = true;
                    binding.lvContainer.setAdapter(mAdapter);
                } else {
                    mAdapter.setList(dataList);
                }
                fillContent();

                break;
            default:
                break;
        }
    }

    /**
     * 填充数据
     */
    private void fillContent() {
        if (mCurrentOperateTick != null) {
            binding.tvOperateContent.setText(mCurrentOperateTick.task);
            binding.tvDepartment.setText(mCurrentOperateTick.unit);
            binding.tvCode.setText(mCurrentOperateTick.code);
            String content = dataList == null ? getString(R.string.xs_czp_all_operate_item_format_str, String.valueOf(0)) : getString(R.string.xs_czp_all_operate_item_format_str, String.valueOf(dataList.size()));
            int endOffset = dataList == null ? 1 : String.valueOf(dataList.size()).length();
            binding.tvOperateItemCount.setText(StringUtils.changePartTextColor(currentActivity, content, R.color.xs_red_color, 1, 1 + endOffset));
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_czr)) {
                binding.ivCzrSign.setImageBitmap(BitmapUtil.getImageThumbnailByWidth(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_czr, ScreenUtils.getScreenWidth(currentActivity) / 3));
            }
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_jhr)) {
                binding.ivCzfzrSign.setImageBitmap(BitmapUtil.getImageThumbnailByWidth(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_jhr, ScreenUtils.getScreenWidth(currentActivity) / 3));
            }
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_ywfzr)) {
                binding.ivZbfzrSign.setImageBitmap(BitmapUtil.getImageThumbnailByWidth(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_ywfzr, ScreenUtils.getScreenWidth(currentActivity) / 3));
            }
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_czr) && !TextUtils.isEmpty(mCurrentOperateTick.person_jhr) && !TextUtils.isEmpty(mCurrentOperateTick.person_ywfzr)) {
                binding.btnConfirm.setEnabled(true);
                binding.btnConfirm.setBackgroundResource(R.drawable.xs_red_button_background_selector);
            }
        }
    }

    private void initOnClick() {
        binding.tvOperateInfo.setOnClickListener(view -> {
            if (binding.llOperateInfoContainer.getVisibility() == View.VISIBLE) {
                binding.llOperateInfoContainer.setVisibility(View.GONE);
                binding.tvOperateInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xs_ic_gray_drop_down, 0);
            } else {
                binding.llOperateInfoContainer.setVisibility(View.VISIBLE);
                binding.tvOperateInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xs_ic_gray_up, 0);
            }
        });

        binding.includeTitle.ibtnCancel.setOnClickListener(view -> OperateTaskCheckedActivity.this.onBackPressed());

        binding.btnConfirm.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, OperateTaskDetailsActivity.class);
            intent.putExtra(Config.CURRENT_TASK_ID, currentOperateId);
            intent.putExtra(Config.IS_FROM_BATTERY, true);
            OperateTaskCheckedActivity.this.startActivity(intent);
            OperateTaskCheckedActivity.this.finish();
        });

        binding.ivCzfzrSign.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_czr)) {
                OperateTaskCheckedActivity.this.showImageDetails(currentActivity, Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_czr);
            }
        });

        binding.tvCzrSignContainer.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, SignNameActivity.class);
            OperateTaskCheckedActivity.this.startActivityForResult(intent, CZR_SIGN_CODE);
        });

        binding.ivCzfzrSign.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_jhr)) {
                OperateTaskCheckedActivity.this.showImageDetails(currentActivity, Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_jhr);
            }
        });

        binding.tvJhrContainer.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, SignNameActivity.class);
            OperateTaskCheckedActivity.this.startActivityForResult(intent, CZFZR_SIGN_CODE);
        });

        binding.tvYwfzrContainer.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, SignNameActivity.class);
            OperateTaskCheckedActivity.this.startActivityForResult(intent, ZBFZR_SIGN_CODE);
        });
        binding.ivZbfzrSign.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_ywfzr)) {
                OperateTaskCheckedActivity.this.showImageDetails(currentActivity, Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_ywfzr);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CZR_SIGN_CODE:
                    // 删除之前的签名图片
                    FileUtils.deleteFile(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_czr);
                    mCurrentOperateTick.person_czr = initSignPicture(data, binding.ivCzrSign);
                    try {
                        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.PERSON_CZR);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case CZFZR_SIGN_CODE:
                    FileUtils.deleteFile(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_jhr);
                    mCurrentOperateTick.person_jhr = initSignPicture(data, binding.ivCzfzrSign);
                    try {
                        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.PERSON_JHR);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                case ZBFZR_SIGN_CODE:
                    FileUtils.deleteFile(Config.SIGN_PICTURE_FOLDER + mCurrentOperateTick.person_ywfzr);
                    mCurrentOperateTick.person_ywfzr = initSignPicture(data, binding.ivZbfzrSign);
                    try {
                        OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.PERSON_YWFZR);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(mCurrentOperateTick.person_czr) && !TextUtils.isEmpty(mCurrentOperateTick.person_jhr) && !TextUtils.isEmpty(mCurrentOperateTick.person_ywfzr)) {
                binding.btnConfirm.setEnabled(true);
                binding.btnConfirm.setBackgroundResource(R.drawable.xs_red_button_background_selector);
                mCurrentOperateTick.status = OperateTaskStatus.wwc.name();
                try {
                    OperateTicketService.getInstance().update(mCurrentOperateTick, OperateTick.STATUS);
                    isUpdateStatus = true;
                } catch (DbException e) {
                    e.printStackTrace();
                    isUpdateStatus = false;
                }
            }
        }
    }

    /**
     * 初始化签名布局
     *
     * @param data
     * @param mImageView
     * @return
     */
    private String initSignPicture(Intent data, ImageView mImageView) {
        String signPicName = "";
        ArrayList<String> signNameList = data.getStringArrayListExtra(Config.CURRENT_FILENAME);
        if (signNameList != null && signNameList.size() > 0) {
            signPicName = signNameList.get(0);
            mImageView.setImageBitmap(BitmapUtil.getImageThumbnailByWidth(Config.SIGN_PICTURE_FOLDER + signPicName, ScreenUtils.getScreenWidth(currentActivity) / 3));
        }
        return signPicName;
    }

    @Override
    public void onBackPressed() {
        if (isUpdateStatus) {
            Intent intent = new Intent(currentActivity, OperateTaskListActivity.class);
            intent.putExtra(Config.IS_FROM_BATTERY, true);
            startActivity(intent);
        }
        this.finish();
    }

}
