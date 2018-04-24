package com.cnksi.inspe.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.layoutm.FlowLayoutManager;
import com.cnksi.inspe.databinding.WidgetBreakwardDialogBinding;
import com.cnksi.inspe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分词选择对话框
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/17 09:26
 */
public class KeyBoadWordDialog extends AlertDialog implements View.OnClickListener {
    private WidgetBreakwardDialogBinding dataBinding;

    public KeyBoadWordDialog(Context context) {
        super(context);
        initCreate(context);
    }

    public KeyBoadWordDialog(Context context, int themeResId) {
        super(context, themeResId);
        initCreate(context);
    }

    public KeyBoadWordDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initCreate(context);
    }

    private String lastWord;

    private void onCheckedWord(int position, String word, boolean check) {
//        if (word.equals(lastWord)) {
//            String inputValue = dataBinding.issueEdit.getText().toString();
//            StringBuffer stringBuffer = new StringBuffer();
//
//            int selection = dataBinding.issueEdit.getSelectionStart() + word.length();
//            stringBuffer.append(inputValue.substring(0, dataBinding.issueEdit.getSelectionStart()))
//                    .append(word)
//                    .append(inputValue.substring(dataBinding.issueEdit.getSelectionEnd(), inputValue.length()));
//
//            dataBinding.issueEdit.setSelection(selection);
//            dataBinding.issueEdit.setText(stringBuffer.toString());
//        }

        String inputValue = dataBinding.issueEdit.getText().toString();
        StringBuffer stringBuffer = new StringBuffer();
        int selection = dataBinding.issueEdit.getSelectionStart() + word.length();
        stringBuffer.append(inputValue.substring(0, dataBinding.issueEdit.getSelectionStart()))
                .append(word)
                .append(inputValue.substring(dataBinding.issueEdit.getSelectionEnd(), inputValue.length()));

        dataBinding.issueEdit.setText(stringBuffer.toString());
        dataBinding.issueEdit.setSelection(selection);

        lastWord = word;
    }

    InputMethodManager imm = null;

    private void initCreate(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.widget_breakward_dialog, null, false);
        dataBinding.okBtn.setOnClickListener(this);
        dataBinding.scoreDescTxt.setVisibility(View.GONE);
        dataBinding.standardDescTxt.setVisibility(View.GONE);
        dataBinding.issueEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        FlowLayoutManager standardWordManager = new FlowLayoutManager();
        dataBinding.standardWordRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            int space = context.getResources().getDimensionPixelSize(R.dimen.inspe_12px);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = space;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
            }
        });
        dataBinding.standardWordRecycler.setLayoutManager(standardWordManager);

        FlowLayoutManager scoreWordManager = new FlowLayoutManager();
        dataBinding.scoreWordRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            int space = context.getResources().getDimensionPixelSize(R.dimen.inspe_12px);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = space;
                outRect.left = space;
                outRect.right = space;
                outRect.bottom = space;
            }
        });
        dataBinding.scoreWordRecycler.setLayoutManager(scoreWordManager);

        standardAdapter = new BreakWordAdapter(R.layout.inspe_breakword_item, standardList, new OnBreakCheckedListener() {
            @Override
            public void onCheckedLine(String line) {
            }

            @Override
            public void onCheckedWord(int position, String word, boolean check) {
                KeyBoadWordDialog.this.onCheckedWord(position, word, check);
            }
        });
        scoreAdapter = new BreakWordAdapter(R.layout.inspe_breakword_item, scoreList, new OnBreakCheckedListener() {
            @Override
            public void onCheckedLine(String line) {
            }

            @Override
            public void onCheckedWord(int position, String word, boolean check) {
                KeyBoadWordDialog.this.onCheckedWord(position, word, check);
            }
        });
        dataBinding.standardWordRecycler.setAdapter(standardAdapter);
        dataBinding.scoreWordRecycler.setAdapter(scoreAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dataBinding.getRoot());
        setCanceledOnTouchOutside(false);//点击透明处不关闭

        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private BreakWordAdapter standardAdapter;
    private BreakWordAdapter scoreAdapter;
    private List<BreakItemBean> standardList = new ArrayList<>();
    private List<BreakItemBean> scoreList = new ArrayList<>();
    private String standardWord = "";
    private String scoreWord = "";

    /**
     * 设置风格字符串
     * @param standarWord
     * @param scoreWord
     * @return
     */
    public KeyBoadWordDialog setBreakList(String standarWord, String scoreWord, String oldWord) {
        dataBinding.standardDescTxt.setText(standarWord);
        dataBinding.scoreDescTxt.setText(scoreWord);
        dataBinding.issueEdit.setText(oldWord);
        dataBinding.issueEdit.setSelection(dataBinding.issueEdit.getText().length());

        for (String msg : StringUtils.breakWord(standarWord)) {
            if (!TextUtils.isEmpty(msg)) {
                standardList.add(new BreakItemBean(msg));
            }
        }
        for (String msg : StringUtils.breakWord(scoreWord)) {
            if (!TextUtils.isEmpty(msg)) {
                scoreList.add(new BreakItemBean(msg));
            }
        }
        standardAdapter.notifyDataSetChanged();
        scoreAdapter.notifyDataSetChanged();

        return this;
    }

    private OnCheckedListener onCheckedListener;

    public KeyBoadWordDialog setOnCheckListener(OnCheckedListener listener) {
        onCheckedListener = listener;
        return this;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.okBtn) {
            dismiss();
            if (onCheckedListener != null) {
                onCheckedListener.onChecked(dataBinding.issueEdit.getText().toString().trim());
            }

        } else {
        }
    }

    public interface OnCheckedListener {
        void onChecked(String msg);
    }

    public class BreakWordAdapter extends BaseQuickAdapter<BreakItemBean, BaseViewHolder> {
        List<BreakItemBean> list;

        public BreakWordAdapter(int layoutResId, List<BreakItemBean> data, OnBreakCheckedListener listener) {
            super(layoutResId, data);
            list = data;
            this.onBreakCheckedListener = listener;
        }

        private OnBreakCheckedListener onBreakCheckedListener;

        @Override
        protected void convert(BaseViewHolder helper, BreakItemBean item) {
            helper.setOnCheckedChangeListener(R.id.nameTxt, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.isChecked = b;
                    if (b) {
                        imm.hideSoftInputFromWindow(dataBinding.okBtn.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        onBreakCheckedListener.onCheckedWord(helper.getAdapterPosition(), item.name, b);
                        compoundButton.setChecked(false);
                    }
                }
            });

            helper.setText(R.id.nameTxt, item.name);
            helper.setChecked(R.id.nameTxt, item.isChecked);
        }


    }

    public interface OnBreakCheckedListener {
        void onCheckedLine(String line);

        void onCheckedWord(int position, String word, boolean check);
    }

    class BreakItemBean {
        public boolean isChecked;
        public String name;

        public BreakItemBean() {
        }

        public BreakItemBean(String name) {
            this.name = name;
        }
    }

    private List<CheckShowEntity> checkShowEntityList = new ArrayList<>();

    private void addWord(int type, int index, String value) {
        checkShowEntityList.add(new CheckShowEntity(type, index, value));
    }

    private void delWord(int type, int index, String value) {
        for (CheckShowEntity entity : checkShowEntityList) {
            if (entity.type == type && entity.index == index) {
                checkShowEntityList.remove(entity);
                break;
            }
        }
    }

    private String getCheckWord() {
        StringBuffer stringBuffer = new StringBuffer();
        for (CheckShowEntity bean : checkShowEntityList) {
            stringBuffer.append(bean.value);
        }

        return stringBuffer.toString();
    }

    class CheckShowEntity {
        public int type;//类型
        public int index;//list编号
        public String value;//值

        public CheckShowEntity(int type, int index, String value) {
            this.type = type;
            this.index = index;
            this.value = value;
        }
    }


}
