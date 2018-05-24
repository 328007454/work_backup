package com.cnksi.bdzinspection.utils;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.view.NumberSeekBar;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.common.utils.PlaySound;

import java.math.BigDecimal;

public class KeyBoardUtil {

    public static final int KEYBORAD_HIDE = 0;
    public static final int KEYBORAD_SHOW = 1;

    private View mRootContainer;
    private KeyboardView mKeyBoardView;
    private Keyboard mKeyBoardNumber;// 数字键盘
    private TextView mTvMinValue, mTvMaxValue;
    private LinearLayout mRlSeekBarContainer;
    private NumberSeekBar mNumberSeekBar;
    public boolean isNumberKeyBoard = false;// 是否数据键盘
    public boolean isUpperCase = false;// 是否大写
    /**
     * 文本输入框
     */
    private EditText mEditText;
    private Context mContext;
    /**
     * 振动器
     */
    private Vibrator mVibrator;
    /**
     * 加减的单位值
     */
    private String unitValue = "0.01";
    /**
     * 总共多少份
     */
    private int progressOffset = 100;
    /**
     * 是否选择全部的文字
     */
    private boolean isSelectAllOnFocus = false;

    private BigDecimal minValue = null;
    private BigDecimal maxValue = null;
    private BigDecimal progressValue = null;
    private BigDecimal currentProgress = null;
    private WindowManager mWindowManager;
    /**
     * 是不是第一点获取焦点
     */
    private boolean isFirstFocus = false;
    private boolean isOnKey = false;
    private WindowManager.LayoutParams layoutParams;
    public OnKeyBoardStateChangeListener mOnKeyBoardStateChangeListener;

    public interface OnKeyBoardStateChangeListener {
        void onKeyBoardStateChange(int state);

        void onKeyBoardNextInput(EditText editText);
    }

    public void setOnValueChangeListener(OnKeyBoardStateChangeListener mOnKeyBoardStateChangeListener) {
        this.mOnKeyBoardStateChangeListener = mOnKeyBoardStateChangeListener;
    }

    public KeyBoardUtil(View mRootContainer, Context context, EditText mEditText, WindowManager windowManager, WindowManager.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
        this.mWindowManager = windowManager;
        this.mContext = context;
        this.mRootContainer = mRootContainer;
        this.mKeyBoardView = (KeyboardView) mRootContainer.findViewById(R.id.keyboard_view);
        this.mNumberSeekBar = (NumberSeekBar) mRootContainer.findViewById(R.id.number_seekbar);
        this.mRlSeekBarContainer = (LinearLayout) mRootContainer.findViewById(R.id.rl_seekbar_container);
        this.mTvMinValue = (TextView) mRootContainer.findViewById(R.id.tv_min_value);
        this.mTvMaxValue = (TextView) mRootContainer.findViewById(R.id.tv_max_value);
        this.mNumberSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        this.mEditText = mEditText;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mKeyBoardNumber = new Keyboard(context, R.xml.symbols);
        mKeyBoardView.setKeyboard(mKeyBoardNumber);
        mKeyBoardView.setEnabled(true);
        mKeyBoardView.setPreviewEnabled(false);
        mKeyBoardView.setOnKeyboardActionListener(listener);
    }

    public void setCurrentEditText(EditText mEditText, String minValue, String maxValue, String unit) {
        this.mEditText = mEditText;
        // 如果没有值 根据要求给定默认的上下限值
        if (TextUtils.isEmpty(minValue)) {
            if (unit != null) {
                if (unit.contains("压力") || unit.contains("MPa")) {
                    minValue = "0.00";
                } else if (unit.contains("电流")) {
                    minValue = "0.00";
                } else {
                    minValue = "0";
                }
            }

        }
        if (TextUtils.isEmpty(maxValue)) {
            if (unit != null) {
                if (unit.contains("压力") || unit.contains("MPa")) {
                    maxValue = "1.00";
                } else if (unit.contains("电流")) {
                    maxValue = "2.00";
                } else {
                    maxValue = "100";
                }
            }
        }
        String currentValue = this.mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(currentValue)) {
            mEditText.setSelection(currentValue.length());
        }
        setCurrentEditText(currentValue, minValue, maxValue);
    }

    public void setCurrentEditText(EditText mEditText, String minValue, String maxValue) {
        this.mEditText = mEditText;
        String currentValue = this.mEditText.getText().toString().trim();
        setCurrentEditText(currentValue, minValue, maxValue);
        String value = mEditText.getText().toString();
        if (!TextUtils.isEmpty(value)) {
            mEditText.setSelection(value.length());
        }
    }

    public void setCurrentEditText(String currentValue, String minValue, String maxValue) {
        isFirstFocus = true;
        if (TextUtils.isEmpty(minValue)) {
            minValue = "0.0";
        }
        if (TextUtils.isEmpty(maxValue)) {
            maxValue = "100.0";
        }
        mRlSeekBarContainer.setVisibility(View.GONE);
        this.mTvMinValue.setText(minValue);
        this.mTvMaxValue.setText(maxValue);
        this.minValue = new BigDecimal(minValue);
        this.maxValue = new BigDecimal(maxValue);
        float allValue = this.maxValue.subtract(this.minValue).floatValue();

        if (allValue >= 10) {
            progressOffset = 1;
            unitValue = "1";
        } else if (allValue >= 5 && allValue < 10) {
            progressOffset = 10;
            unitValue = "0.1";
        } else {
            progressOffset = 100;
            unitValue = "0.01";
        }
        int progressMax = (int) (allValue * progressOffset);
        this.progressValue = new BigDecimal(String.valueOf(progressMax));
        currentProgress = new BigDecimal("0");
        try {
            if (!TextUtils.isEmpty(currentValue)) {
                currentProgress = (new BigDecimal(currentValue).subtract(this.minValue))
                        .multiply(new BigDecimal(progressOffset));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mNumberSeekBar.setMax(progressMax);
        if (currentProgress.intValue() >= 0) {
            this.mNumberSeekBar.setProgress(currentProgress.intValue());
        }
        this.mNumberSeekBar.setCurrentMinMaxValue(minValue, maxValue);
    }

    public void setUnitValue(String unitValue) {
        this.unitValue = unitValue;
    }

    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

        private boolean isHandChange = false;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isHandChange = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isHandChange = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mEditText != null && maxValue.floatValue() > minValue.floatValue() && progressValue != null
                    && progressValue.floatValue() > 0) {
                float allValue = maxValue.subtract(minValue).floatValue();
                BigDecimal dAllValue = new BigDecimal(String.valueOf(allValue));
                BigDecimal unitValue = dAllValue.divide(progressValue);
                BigDecimal resultValue = unitValue.multiply(new BigDecimal(String.valueOf(seekBar.getProgress())))
                        .add(minValue);
                BigDecimal currentValue = unitValue.multiply(currentProgress).add(minValue);
                Editable editable = mEditText.getText();
                try {
                    if (isFirstFocus) {
                        isFirstFocus = false;
                    } else {
                        editable.delete(0, editable.toString().length());
                        if (resultValue.floatValue() >= 0f) {
                            if (resultValue.compareTo(currentValue) == -1 && !isHandChange && !isOnKey) {
                                editable.insert(0, String.valueOf(currentValue.floatValue()));
                            } else {
                                editable.insert(0, String.valueOf(resultValue.floatValue()));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOnKey = false;
            }
        }
    };

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
            if (mEditText != null) {
                mVibrator.vibrate(30);
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();
                String content = editable.subSequence(start, end).toString();
                isSelectAllOnFocus = !TextUtils.isEmpty(content);
            }
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (mEditText != null) {
                isOnKey = true;
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
                    hideKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                    PlaySound.getIntance(mContext).play(R.raw.delete);
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                        if (isSelectAllOnFocus) {
                            editable.delete(0, editable.toString().length());
                        }
                    }
                } else if (primaryCode == 666666) {
                    // TODO: 将现有的值加上0.01
                    PlaySound.getIntance(mContext).play(R.raw.up);
                    changeValue(editable, true);

                } else if (primaryCode == 888888) {
                    // TODO: 将现有的值减去0.01
                    PlaySound.getIntance(mContext).play(R.raw.down);
                    changeValue(editable, false);
                } else if (primaryCode == EditorInfo.IME_ACTION_NEXT) {
                    if (null != mOnKeyBoardStateChangeListener) {
                        mOnKeyBoardStateChangeListener.onKeyBoardNextInput(mEditText);
                    }
                } else {
                    PlaySound.getIntance(mContext).play(R.raw.click);
                    if (primaryCode == 46) {
                        if (TextUtils.isEmpty(editable.toString())) {
                            editable.insert(start, "0.");
                        } else {
                            if (!editable.toString().contains(".")) {
                                editable.insert(start, Character.toString((char) primaryCode));
                            }
                        }
                    } else {
                        if (isSelectAllOnFocus) {
                            editable.delete(0, editable.toString().length());
                        }
                        editable.insert(start, Character.toString((char) primaryCode));
                    }
                }
            }
        }
    };

    /**
     * 点击键盘上的上下 改变值
     *
     * @param editable
     * @param isPlus
     */
    private void changeValue(Editable editable, boolean isPlus) {
        try {
            BigDecimal b1 = TextUtils.isEmpty(editable.toString()) ? this.minValue
                    : new BigDecimal(editable.toString());
            BigDecimal b2 = new BigDecimal(unitValue);
            BigDecimal value;
            if (isPlus) {
                value = b1.add(b2);
            } else {
                value = b1.subtract(b2);
                if (value.compareTo(new BigDecimal(unitValue)) == -1) {
                    value = new BigDecimal("0.0");
                }
            }
            editable.delete(0, editable.toString().length());
            editable.insert(0, String.valueOf(value));
            if (mNumberSeekBar != null) {
                try {
                    BigDecimal currentProgress = (value.subtract(this.minValue))
                            .multiply(new BigDecimal(progressOffset));
                    if (currentProgress.intValue() >= 0 && (currentProgress.compareTo(progressValue) == -1
                            || currentProgress.compareTo(progressValue) == 0)) {
                        this.mNumberSeekBar.setProgress(currentProgress.intValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKeyboard() {
        int visibility = mRootContainer.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mWindowManager.addView(mRootContainer, layoutParams);
            mRootContainer.setVisibility(View.VISIBLE);
            mRootContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xsdialog_show));
        }
        if (mOnKeyBoardStateChangeListener != null) {
            mOnKeyBoardStateChangeListener.onKeyBoardStateChange(KEYBORAD_SHOW);
        }
    }

    public void hideKeyboard() {
        int visibility = mRootContainer.getVisibility();
        if (visibility == View.VISIBLE) {
            mRootContainer.setVisibility(View.GONE);
            mRootContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.xsdialog_dismiss));
        }
        if (mOnKeyBoardStateChangeListener != null) {
            mOnKeyBoardStateChangeListener.onKeyBoardStateChange(KEYBORAD_HIDE);
        }
        if (mEditText != null) {
            mEditText.clearFocus();
            KeyBoardUtils.closeKeybord(mEditText, mContext);
            mWindowManager.removeViewImmediate(mRootContainer);
        }

    }
}
