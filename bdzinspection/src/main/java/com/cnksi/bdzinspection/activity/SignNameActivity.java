package com.cnksi.bdzinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivitySignNameBinding;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.common.Config;
import com.cnksi.core.view.LinePathView;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

/**
 * 签字
 *
 * @author Oliver
 */
public class SignNameActivity extends BaseActivity {
    private static final String LAYOUT_LINEARLAYOUT = "LinearLayout";
    private static final String LAYOUT_FRAMELAYOUT = "FrameLayout";
    private static final String LAYOUT_RELATIVELAYOUT = "RelativeLayout";

    /**
     * 签名的布局
     */
    private LinePathView mLinePathView;
    /**
     * 签名图片名称
     */
    private String mCurrentSignName = "";
    /**
     * 签字的集合
     */
    private ArrayList<String> mAllSignNameList = new ArrayList<String>();
    /**
     * 签字的个数
     */
    private int signNameCount = 1;
    /**
     * 当前签名的个数
     */
    private int currentSignIndex = 0;
    /**
     * 需要签名的名字集合
     */
    private ArrayList<String> mPersonNameList = null;

    private XsActivitySignNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_sign_name);
        initialUI();
        initOnClick();
    }


    /**
     * 适配布局控件
     */
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (name.equals(LAYOUT_FRAMELAYOUT)) {
            view = new AutoFrameLayout(context, attrs);
        }

        if (name.equals(LAYOUT_LINEARLAYOUT)) {
            view = new AutoLinearLayout(context, attrs);
        }

        if (name.equals(LAYOUT_RELATIVELAYOUT)) {
            view = new AutoRelativeLayout(context, attrs);
        }

        if (view != null) return view;

        return super.onCreateView(name, context, attrs);
    }

    protected void initialUI() {
        mPersonNameList = getIntent().getStringArrayListExtra(Config.SIGN_NAME_LIST);
        if (mPersonNameList != null) {
            signNameCount = mPersonNameList.size();
        }
        if (signNameCount > 1) {
            binding.btnSave.setText(R.string.xs_next_str);
        }
        mLinePathView = new LinePathView(currentActivity);

        binding.llSignContainer.addView(mLinePathView, 0);
        mLinePathView.setBackColor(Color.WHITE);
        if (mPersonNameList != null && !mPersonNameList.isEmpty()) {
            binding.tvPersonName.setText(mPersonNameList.get(0));
        }
    }

    private void initOnClick() {
        binding.btnCancel.setOnClickListener(view -> finish());
        binding.btnReset.setOnClickListener(view -> mLinePathView.clear());
        binding.btnSave.setOnClickListener(view -> save());
    }

    private void save() {
        if (!mLinePathView.getTouched()) {
            ToastUtils.showMessage( "请签名!");
            return;
        }
        try {
            mLinePathView.save(Config.SIGN_PICTURE_FOLDER + (mCurrentSignName = FunctionUtil.getCurrentImageName()),
                    false, 10);

            currentSignIndex++;
            mAllSignNameList.add(mCurrentSignName);
            if (signNameCount > 1) {
                if (currentSignIndex == signNameCount - 1) {
                    binding.btnSave.setText(R.string.xs_save_str);
                }
                if (currentSignIndex == signNameCount) {
                    Intent intent = getIntent();
                    intent.putStringArrayListExtra(Config.CURRENT_FILENAME, mAllSignNameList);
                    setResult(RESULT_OK, intent);
                    this.finish();
                } else {
                    if (mPersonNameList != null && !mPersonNameList.isEmpty()) {
                        binding.tvPersonName.setText(mPersonNameList.get(currentSignIndex));
                    }
                    mLinePathView.clear();
                }
            } else {
                Intent intent = getIntent();
                intent.putStringArrayListExtra(Config.CURRENT_FILENAME, mAllSignNameList);
                setResult(RESULT_OK, intent);
                this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception

        }
    }
}
