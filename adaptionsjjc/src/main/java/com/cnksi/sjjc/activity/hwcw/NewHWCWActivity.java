package com.cnksi.sjjc.activity.hwcw;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cnksi.core.utils.CToast;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.databinding.ActivityHwcwNewBinding;
import com.zhy.autolayout.attr.MaxHeightAttr;

/**
 * @author kkk on 2017/12/7.
 */

public class NewHWCWActivity extends BaseActivity {
    ActivityHwcwNewBinding mHwcwNewBinding;
    private int realHeight;
    private ViewWrapper wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHwcwNewBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_hwcw_new, null, false);
        setChildView(mHwcwNewBinding.getRoot());
        flyAction();
    }

    private void flyAction() {


        mHwcwNewBinding.scrRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
                        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        mHwcwNewBinding.containerBaseinfo.measure(width, height);
                        int realWidth = mHwcwNewBinding.containerBaseinfo.getMeasuredWidth();
                        realHeight = mHwcwNewBinding.containerBaseinfo.getMeasuredHeight();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (wrapper == null)
                            wrapper = new ViewWrapper(mHwcwNewBinding.txtAllBdz);
                        if (wrapper != null)
                            ObjectAnimator.ofInt(wrapper, "width", 0).setDuration(1000).start();
                        break;
                    default:
                        break;
                }

                return false;
            }
        });

    }

    public class ViewWrapper {
        private View mTargetView;

        public ViewWrapper(View targetView) {
            this.mTargetView = targetView;
        }

        public int getWidth() {
            return mTargetView.getLayoutParams().width;
        }

        public void setWidth(int height) {
            mTargetView.getLayoutParams().height = height;
            mTargetView.requestLayout();
        }
    }

}
