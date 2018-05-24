package com.cnksi.bdzinspection.view.regularmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;

public class ArcMenu extends ViewGroup implements OnClickListener {

    private static final String TAG = "quw.ArcMenu";
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BUTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BUTTOM = 3;

    public static final int STATUS_CLOSE = 0;
    public static final int STATUS_OPEN = 1;

    private int mPosition = POS_RIGHT_BUTTOM;
    private int mRadius = 0;
    private int mStatus = STATUS_CLOSE;
    /**
     * 启动菜单的主按钮
     */
    private View mCButton;
    private OnMenuItemClickListener mMenuItemClickListener;
    // listview、gridview等中用知道 ArcMenu加载的position
    private int location;

    /**
     * 使用的是listView、gridview知道这个自定义的view所在的第几个positoin
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener l, int location) {
        this.mMenuItemClickListener = l;
        this.location = location;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener l) {
        this.mMenuItemClickListener = l;
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        // 设置默认半径为100dp，根据需要自行修改
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        /**
         * 获取xml文件中设置的属性值
         */
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.XS_ArcMenu, defStyle, 0);
        mPosition = typedArray.getInt(R.styleable.XS_ArcMenu_position, POS_RIGHT_BUTTOM);
        mRadius = (int) typedArray.getDimension(R.styleable.XS_ArcMenu_radius, mRadius);
        // 一定记得释放
        typedArray.recycle();
        Log.i(TAG, "mPosition = " + mPosition + ", mRadius = " + mRadius);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context) {
        this(context, null, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            // 主要对child的大家进行计算
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
        }

    }

    private void layoutButton() {
        /**
         * 计算所有按钮的初始放置位置
         */
        int count = getChildCount();
        mCButton = getChildAt(count - 1);
        int l = 0;
        int t = 0;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();
        switch (mPosition) {
            case POS_LEFT_TOP:

                break;

            case POS_LEFT_BUTTOM:
                t = getMeasuredHeight() - height;
                break;

            case POS_RIGHT_TOP:
                l = getMeasuredWidth() - width;
                break;

            case POS_RIGHT_BUTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;

        }

        /** 放置菜单按钮**/
        mCButton.layout(l, t, l + width, t + height);
        mCButton.setClickable(true);
        mCButton.setFocusable(true);
        mCButton.setOnClickListener(this);

        /**
         * 放置子按钮
         */
        for (int i = 0; i < count - 1; i++) {
            View childView = getChildAt(i);
            childView.layout(l, t, l + width, t + height);
            childView.setVisibility(View.GONE);
            final int pos = i;
            childView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    clickItemAnim(pos);
                    changeStatus();
                    if (mMenuItemClickListener != null) {
                        mMenuItemClickListener.onItemClick(v, pos);

                    }
                }
            });
        }
    }

    public interface OnMenuItemClickListener {
        void onItemClick(View view, int position);

    }

    public void closeMenu() {
        if (mStatus == STATUS_OPEN) {
            toggleMenu();
        }
    }

    public void forbidden(boolean isForbidden) {
        this.isForbidden = isForbidden;
    }

    boolean isForbidden;

    @Override
    public void onClick(View v) {
        if (isForbidden) {
            return;
        } else {
            rotateCButton();
            toggleMenu();
        }
    }

    private void toggleMenu() {
        int itemCount = getChildCount() - 1;
        for (int i = 0; i < itemCount; i++) {
            final View child = getChildAt(i);
            child.setVisibility(View.VISIBLE);
            child.setClickable(true);
            child.setFocusable(true);
            /**
             * 计算出子按钮位移距离
             */
            int x = (int) (mRadius * Math.sin(Math.PI / 2 / (itemCount - 1) * i));
            int y = (int) (mRadius * Math.cos(Math.PI / 2 / (itemCount - 1) * i));

            /**
             * 判断出位移方向
             */
            int xflag = 1;
            int yflag = 1;
            if (mPosition == POS_RIGHT_TOP || mPosition == POS_RIGHT_BUTTOM) {
                xflag = -1;
            }
            if (mPosition == POS_LEFT_BUTTOM || mPosition == POS_RIGHT_BUTTOM) {
                yflag = -1;
            }
            ObjectAnimator transXAnimator = null;
            ObjectAnimator transYAnimator = null;
            ObjectAnimator rotationAnimator = null;
            AnimatorSet animatorSet = new AnimatorSet();
            if (mStatus == STATUS_CLOSE) {// 设置弹出动画
                transXAnimator = ObjectAnimator.ofFloat(child, "translationX", 0, x * xflag);
                transYAnimator = ObjectAnimator.ofFloat(child, "translationY", 0, y * yflag);

            } else {// 设置收回的动画
                transXAnimator = ObjectAnimator.ofFloat(child, "translationX", x * xflag, 0);
                transYAnimator = ObjectAnimator.ofFloat(child, "translationY", y * yflag, 0);
            }
            rotationAnimator = ObjectAnimator.ofFloat(child, "rotation", 0, 720f);
            animatorSet.playTogether(transXAnimator, transYAnimator, rotationAnimator);
            animatorSet.setDuration(300);

            animatorSet.setStartDelay(25 * i);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mStatus == STATUS_CLOSE) {
                        child.setVisibility(View.GONE);
                        child.setClickable(false);
                        child.setFocusable(false);
                    }
                    super.onAnimationEnd(animation);
                }
            });
            animatorSet.start();

        }
        changeStatus();
    }

    private void clickItemAnim(int pos) {
        int itemCount = getChildCount() - 1;
        for (int i = 0; i < itemCount; i++) {

            final View childView = getChildAt(i);
            AnimatorSet animatorSet = null;
            if (i == pos) {
                animatorSet = scaleBigAnim(childView);
            } else {
                animatorSet = scaleSmallAnim(childView);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    /**
                     * 属性动画在执行之后会将View的各个属性确实更改，必须将他们还原
                     */
                    childView.setScaleX(1.0f);
                    childView.setScaleY(1.0f);
                    childView.setAlpha(1.0f);
                    childView.setX(0f);
                    childView.setTranslationX(0f);
                    childView.setTranslationY(0f);
                    if (mStatus == STATUS_CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                    super.onAnimationEnd(animation);
                }
            });
            animatorSet.start();
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    private AnimatorSet scaleBigAnim(final View v) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", 2.0f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", 2.0f, 1.0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setDuration(500);

        return animatorSet;
    }

    private AnimatorSet scaleSmallAnim(View v) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(v, "scaleX", 1.0f, 0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(v, "scaleY", 1.0f, 0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1.0f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.setDuration(500);
        return animatorSet;
    }

    private void changeStatus() {
        mStatus = (mStatus == STATUS_CLOSE ? STATUS_OPEN : STATUS_CLOSE);
    }

    public int getMenuStatus() {
        return mStatus;
    }

    private void rotateCButton() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mCButton, "rotation", 0, 360f);
        anim.setDuration(500);
        anim.start();
    }

}
