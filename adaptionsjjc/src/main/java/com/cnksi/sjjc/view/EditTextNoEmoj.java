package com.cnksi.sjjc.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.cnksi.common.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;

/**
 * Created by kkk on 2017/9/21.
 */

public class EditTextNoEmoj extends AppCompatEditText {
    public EditTextNoEmoj(Context context) {
        super(context);
        addOnTextChangeListen();
    }
    private String inputText;

    public EditTextNoEmoj(Context context, AttributeSet attrs) {
        super(context,attrs);
        addOnTextChangeListen();
    }

    public EditTextNoEmoj(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addOnTextChangeListen();

    }

    private void addOnTextChangeListen() {
        setBackground(getResources().getDrawable(R.color.white));
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {
                    inputText = sequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(StringUtils.hasEmoji(editable.toString())){
                   setText(inputText);
                    ToastUtils.showMessage("请不要输入表情符号");
                }
            }
        });
    }


}
