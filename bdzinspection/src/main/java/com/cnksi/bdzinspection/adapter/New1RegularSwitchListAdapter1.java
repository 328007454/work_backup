package com.cnksi.bdzinspection.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.SwitchItemChild1;
import com.cnksi.bdzinspection.databinding.SwitchItemChild2;
import com.cnksi.bdzinspection.databinding.SwitchItemParentBinding;
import com.cnksi.bdzinspection.model.StandardSwitchover;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.SwitchPic;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 定期维护Adapter
 */
public class New1RegularSwitchListAdapter1 extends BaseMapListExpandableAdapter<DbModel, DbModel> {

    private static final int CHILD1 = 0;
    private static final int CHILD2 = 1;
    private static CharSequence CRISIS;
    private static CharSequence SERIOUS;
    private static CharSequence GENERAL;

    public interface AdapterClickListener {
        void toolClick(View v, DbModel data, int position);

        void imgClick(View v, DbModel data, int position);

        void radioClick(View v, DbModel data, int position);

        void defectClick(View v, DbModel data, int position);
    }


    AdapterClickListener clickListener;

    public New1RegularSwitchListAdapter1(Context context) {
        super(context);
        String s;
        CRISIS = StringUtils.changePartTextColor(mContext, s = "[" + Config.CRISIS_LEVEL + "]", R.color.xs_red_color, 0, s.length());
        SERIOUS = StringUtils.changePartTextColor(mContext, s = "[" + Config.SERIOUS_LEVEL + "]", R.color.xs_orange_color, 0, s.length());
        GENERAL = StringUtils.changePartTextColor(mContext, s = "[" + Config.GENERAL_LEVEL + "]", R.color.xs_yellow_color, 0, s.length());
    }

    public void setClickListener(AdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        DbModel model = getChild(groupPosition, childPosition);
        if (model.getInt(StandardSwitchover.LEVEL) == 2) {
            return CHILD1;
        } else {
            return CHILD2;
        }
    }

    Map<String, List<EditText>> editMap = new HashMap<>();

    String standardId = "";

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int childType = getChildType(groupPosition, childPosition);
        final DbModel model = getChild(groupPosition, childPosition);
        switch (childType) {
            case CHILD1:
                standardId = model.getString(StandardSwitchover.ID);
                SwitchItemChild1 itemBind1;
                if (convertView == null) {
                    itemBind1 = DataBindingUtil.inflate(mInflater, R.layout.xs_item_switch_child1, null, false);
                } else {
                    itemBind1 = DataBindingUtil.getBinding(convertView);
                }
                itemBind1.tvNo.setText(model.getString(StandardSwitchover.DISPLAYNUM));
                itemBind1.tvDesc.setText(model.getString(StandardSwitchover.DESCRIPTION));
                String pics = model.getString(SwitchPic.PIC);
                String voice = model.getString(SwitchPic.VOICE);
                itemBind1.llPics.setVisibility(!TextUtils.isEmpty(pics) || !TextUtils.isEmpty(voice) ? View.VISIBLE : View.GONE);

                if (!TextUtils.isEmpty(pics)) {
                    itemBind1.rlPicContainer.setVisibility(View.VISIBLE);
                    String[] picsArray = pics.split(Config.COMMA_SEPARATOR);
                    if (picsArray != null && picsArray.length > 1) {
                        itemBind1.tvImgCount.setVisibility(View.VISIBLE);
                        itemBind1.tvImgCount.setText(String.valueOf(picsArray.length));
                    } else {
                        itemBind1.tvImgCount.setVisibility(View.GONE);
                    }
                } else {
                    itemBind1.rlPicContainer.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(voice)) {
                    itemBind1.tvAudio.setVisibility(View.VISIBLE);
                    setTextViewRecordTime(itemBind1.tvAudio, voice);
                } else {
                    itemBind1.tvAudio.setVisibility(View.GONE);
                }
                String defectLevel = model.getString(DefectRecord.DEFECTLEVEL);
                itemBind1.tvDesc.setOnClickListener(clickListener != null ? new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.defectClick(v, model, groupPosition << 16 | childPosition);
                    }
                } : null);
                if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
                    itemBind1.tvDesc.append(CRISIS);
                } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
                    itemBind1.tvDesc.append(SERIOUS);
                } else if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
                    itemBind1.tvDesc.append(GENERAL);
                } else {
                    itemBind1.tvDesc.setOnClickListener(null);
                }
                if (clickListener == null) {
                    itemBind1.ivRegular.setOnClickListener(null);
                    itemBind1.ibtnPicture.setOnClickListener(null);
                    itemBind1.tvAudio.setOnClickListener(null);

                } else {
                    itemBind1.ivRegular.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.toolClick(v, model, groupPosition << 16 | childPosition);
                        }
                    });
                    itemBind1.ibtnPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.imgClick(v, model, groupPosition << 16 | childPosition);
                        }
                    });
                    itemBind1.tvAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.radioClick(v, model, groupPosition << 16 | childPosition);
                        }
                    });
                }
                return itemBind1.getRoot();
            case CHILD2:
                SwitchItemChild2 itemBind2;
                if (convertView == null) {
                    itemBind2 = DataBindingUtil.inflate(mInflater, R.layout.xs_item_switch_child2, null, false);
                } else {
                    itemBind2 = DataBindingUtil.getBinding(convertView);
                }
                itemBind2.tvCopyUnit.setVisibility(View.VISIBLE);
                itemBind2.tvDesc.setText(model.getString(StandardSwitchover.DISPLAYNUM) + model.getString(StandardSwitchover.DESCRIPTION));
                itemBind2.etCopyValues.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                if (itemBind2.etCopyValues.getTag() != null) {
                    MyTextChangeListener textWatchListener = (MyTextChangeListener) itemBind2.etCopyValues.getTag();
                    if (textWatchListener.isAttach) {
                        itemBind2.etCopyValues.removeTextChangedListener(textWatchListener);
                        textWatchListener.isAttach = false;
                    }
                }
                if ("1".equals(model.getString(StandardSwitchover.ISCOPY))) {

                    itemBind2.llCopy.setVisibility(View.VISIBLE);
                    String unit = model.getString(StandardSwitchover.UNIT);
                    if (TextUtils.isEmpty(unit)) {
                        itemBind2.tvCopyUnit.setVisibility(View.GONE);
                    } else {
                        itemBind2.tvCopyUnit.setText("(" + unit.trim() + ")");
                    }
                    itemBind2.etCopyValues.setText(model.getString(DefectRecord.VAL));
                    MyTextChangeListener textWatchListener;
                    if (itemBind2.etCopyValues.getTag() == null) {
                        textWatchListener = new MyTextChangeListener();
                        itemBind2.etCopyValues.setTag(textWatchListener);
                    } else {
                        textWatchListener = (MyTextChangeListener) itemBind2.etCopyValues.getTag();
                    }
                    textWatchListener.setEditTextAndDbModel(itemBind2.etCopyValues, model);
                    itemBind2.etCopyValues.addTextChangedListener(textWatchListener);
                    textWatchListener.isAttach = true;
                } else {
                    itemBind2.llCopy.setVisibility(View.INVISIBLE);

                }
                return itemBind2.getRoot();
            default:
        }
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        SwitchItemParentBinding binding;
        DbModel model = getGroup(groupPosition);
        if (convertView == null) {
            binding = DataBindingUtil.inflate(mInflater, R.layout.xs_item_switch_parent, null, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }
        binding.proName.setText(model.getString(StandardSwitchover.DISPLAYNUM) + model.getString(StandardSwitchover.DESCRIPTION));
        binding.ivArrow.setImageResource(isExpanded ? R.drawable.xs_icon_up : R.drawable.xs_icon_down);
        return binding.getRoot();
    }


    HashMap<String, String> audioLengthMap = new HashMap<>();

    private void setTextViewRecordTime(final TextView tv, final String audioPath) {
        String s = audioLengthMap.get(audioPath);
        if (s == null) {
            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final String rs = MediaRecorderUtils.getInstance().getDurationSuc(mContext, Config.AUDIO_FOLDER + audioPath) + "”";
                    audioLengthMap.put(audioPath, rs);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(rs);
                        }
                    });
                }
            });
        } else {
            tv.setText(s);
        }

    }


    class MyTextChangeListener implements TextWatcher {
        private boolean isAttach = false;
        private EditText mEditText;
        private DbModel model;
        private String beforeText;

        public void setEditTextAndDbModel(EditText mEditText, DbModel model) {
            this.mEditText = mEditText;
            this.model = model;
            this.beforeText = mEditText.getText().toString();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtilsExt.hasEmoji(s.toString())) {
                mEditText.setText(TextUtils.isEmpty(beforeText) ? "" : beforeText);
                ToastUtils.showMessage("请不要输入表情符号");
                return;
            }
            String value = model.getString(DefectRecord.VAL) == null ? "" : model.getString(DefectRecord.VAL);
            String currentValue = mEditText.getText().toString().trim();
            if (!value.equals(currentValue)) {
                model.add(DefectRecord.VAL, currentValue);
                model.add(DefectRecord.OLDVAL, value);
            }
            mEditText.setSelection(s.length());
        }

    }
}
