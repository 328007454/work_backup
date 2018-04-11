package com.cnksi.bdzinspection.adapter;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
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
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.StandardStepConfirm;
import com.cnksi.bdzinspection.model.StandardSwitchover;
import com.cnksi.bdzinspection.model.SwitchPic;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.bdzinspection.utils.SystemConfig;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.StringUtils;
import com.lidroid.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 定期维护Adapter
 */
public class New1RegularSwitchListAdapter2 extends BaseMapListExpandableAdapter<DbModel, DbModel> {


    private static final int CHILD1 = 0;
    private static final int CHILD2 = 1;

    private Map<String, List<EditText>> mapEdittexts = new HashMap<String, List<EditText>>();
    private Map<String, EditText> mapSingleEditText = new HashMap<>();
    private List<EditText> editTexts;
    private static CharSequence CRISIS;
    private static CharSequence SERIOUS;
    private static CharSequence GENERAL;

    private SwitchItemChild1 itemBind1;

    public interface AdapterClickListener {
        void toolClick(View v, DbModel data, int groupPosition, int childPosition);

        void imgClick(View v, DbModel data, int groupPosition, int childPosition);

        void radioClick(View v, DbModel data, int groupPosition, int childPosition);

        void defectClick(View v, DbModel data, int groupPosition, int childPosition);

        void confirmClick(View v, DbModel data, int groupPosition, int childPosition, boolean isParentCheck);

        void onTaskStateChange(boolean isFinish);
    }


    AdapterClickListener clickListener;

    public New1RegularSwitchListAdapter2(Context context) {
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
        if (model.getInt(StandardSwitchover.LEVEL) == 2)
            return CHILD1;
        else return CHILD2;
    }


    /**
     * 高16位表示GroupPosition 低16位ChildPosition
     */
    int nextCheckPosition = -1;
    private int parentPosition;
    private String standardId;

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int childType = getChildType(groupPosition, childPosition);
        final DbModel model = getChild(groupPosition, childPosition);
        final int currentPosition = groupPosition << 16 | childPosition;
        switch (childType) {
            case CHILD1:
                standardId = model.getString(StandardSwitchover.ID);
                if (convertView == null) {
                    itemBind1 = DataBindingUtil.inflate(mInflater, R.layout.xs_item_switch_child1, null, false);
                } else {
                    itemBind1 = DataBindingUtil.getBinding(convertView);
                }
                if (SystemConfig.EVERY_PARENT_STEP.equalsIgnoreCase(SystemConfig.getSwitchMenuConfirmStyle())) {
                    itemBind1.check.setVisibility(View.VISIBLE);
                } else {
                    itemBind1.check.setVisibility(View.GONE);
                }
                if (nextCheckPosition > currentPosition) {
                    itemBind1.ivRegular.setEnabled(true);
                    itemBind1.check.setEnabled(false);
                    itemBind1.check.setBackgroundResource(R.drawable.xs_ic_confirm_sure);
                } else if (nextCheckPosition == currentPosition) {
                    itemBind1.ivRegular.setEnabled(true);
                    itemBind1.check.setEnabled(true);
                    itemBind1.check.setBackgroundResource(R.drawable.xs_ic_confirm_half);
                } else {
                    itemBind1.ivRegular.setEnabled(false);
                    itemBind1.check.setEnabled(false);
                    itemBind1.check.setBackgroundResource(R.drawable.xs_ic_confirm_none);
                }

                itemBind1.tvNo.setText(model.getString(StandardSwitchover.DISPLAYNUM));
                itemBind1.tvDesc.setText(model.getString(StandardSwitchover.DESCRIPTION));
                String pics = model.getString(SwitchPic.PIC);
                String voice = model.getString(SwitchPic.VOICE);
                itemBind1.llPics.setVisibility(!TextUtils.isEmpty(pics) || !TextUtils.isEmpty(voice) ? View.VISIBLE : View.GONE);
                parentPosition = childPosition;
                if (null == mapEdittexts.get(standardId)) {
                    editTexts = new ArrayList<>();
                    mapEdittexts.put(standardId, editTexts);
                }

                if (!TextUtils.isEmpty(pics)) {
                    itemBind1.rlPicContainer.setVisibility(View.VISIBLE);
                    String[] picsArray = pics.split(CoreConfig.COMMA_SEPARATOR);
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
                        clickListener.defectClick(v, model, groupPosition, childPosition);
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
                    itemBind1.check.setOnClickListener(null);

                } else {
                    itemBind1.ivRegular.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.toolClick(v, model, groupPosition, childPosition);
                        }
                    });
                    itemBind1.ibtnPicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.imgClick(v, model, groupPosition, childPosition);
                        }
                    });
                    itemBind1.tvAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.radioClick(v, model, groupPosition, childPosition);
                        }
                    });
                    itemBind1.check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickListener.confirmClick(v, model, groupPosition, childPosition, true);
                        }
                    });
                }
                return itemBind1.getRoot();
            case CHILD2:
                final SwitchItemChild2 itemBind2;
                if (convertView == null) {
                    itemBind2 = DataBindingUtil.inflate(mInflater, R.layout.xs_item_switch_child2, null, false);
                } else {
                    itemBind2 = DataBindingUtil.getBinding(convertView);
                }
                if (SystemConfig.EVERY_STEP.equalsIgnoreCase(SystemConfig.getSwitchMenuConfirmStyle())) {
                    itemBind2.check.setVisibility(View.VISIBLE);
                    mapSingleEditText.put(model.getString(StandardSwitchover.ID), itemBind2.etCopyValues);
                } else {
                    itemBind2.check.setVisibility(View.GONE);
                }
                if (nextCheckPosition > currentPosition) {
                    itemBind2.check.setEnabled(false);
                    itemBind2.check.setBackgroundResource(R.drawable.xs_ic_confirm_sure);
                } else if (nextCheckPosition == currentPosition) {
                    itemBind2.check.setEnabled(true);
                    itemBind2.check.setBackgroundResource(R.drawable.xs_ic_confirm_half);
                } else {
                    itemBind2.check.setEnabled(false);
                    itemBind2.check.setBackgroundResource(R.drawable.xs_ic_confirm_none);
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
                    List<DbModel> models = getChildList(groupPosition);
                    editTexts = mapEdittexts.get(standardId);
                    editTexts.add(itemBind2.etCopyValues);
                    itemBind2.etCopyValues.setText(model.getString(DefectRecord.VAL));
                    MyTextChangeListener textWatchListener;
                    if (itemBind2.etCopyValues.getTag() == null) {
                        textWatchListener = new MyTextChangeListener();
                        itemBind2.etCopyValues.setTag(textWatchListener);
                    } else {
                        textWatchListener = (MyTextChangeListener) itemBind2.etCopyValues.getTag();
                    }
                    textWatchListener.setEditText(itemBind2.etCopyValues);
                    textWatchListener.setEditTextAndDbModel(groupPosition, childPosition);
                    itemBind2.etCopyValues.addTextChangedListener(textWatchListener);
                    textWatchListener.isAttach = true;
                } else {
                    itemBind2.llCopy.setVisibility(View.INVISIBLE);
                }
                itemBind2.check.setOnClickListener(view -> {
                    clickListener.confirmClick(view, model, groupPosition, childPosition, false);
                });
                setAnimationEditexts(mapEdittexts);
                return itemBind2.getRoot();
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

    private void setAnimationEditexts(Map<String, List<EditText>> mapEdittexts) {
        this.mapEdittexts = mapEdittexts;
    }

    public Map<String, List<EditText>> getAnimationEditexts() {
        return mapEdittexts;
    }

    public Map<String, EditText> getSingleEditText() {
        return mapSingleEditText;
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


    public void setData(LinkedList<DbModel> group, HashMap<DbModel, ArrayList<DbModel>> childs) {
        setGroupList(group);
        setGroupMap(childs);
    }


    public void findNextCheckPosition() {
        String level = "2";
        for (int i = 0; i < getGroupCount(); i++) {
            for (int j = 0; j < getChildrenCount(i); j++) {
                DbModel model = getChild(i, j);
                if (SystemConfig.EVERY_STEP.equalsIgnoreCase(SystemConfig.getSwitchMenuConfirmStyle()))
                    level = "3";
                if (level.equals(model.getString(StandardSwitchover.LEVEL)) && TextUtils.isEmpty(model.getString(StandardStepConfirm.CONFIRMDATE))) {
                    nextCheckPosition = i << 16 | j;
                    clickListener.onTaskStateChange(false);
                    return;
                }
            }
        }
        nextCheckPosition = Integer.MAX_VALUE;
        clickListener.onTaskStateChange(true);
    }

    @Override
    public void notifyDataSetChanged() {
        findNextCheckPosition();
        super.notifyDataSetChanged();
    }

    class MyTextChangeListener implements TextWatcher {
        private boolean isAttach = false;
        private EditText mEditText;
        private DbModel model;
        private int groupPosition;
        private int childPosition;
        private String beforeText;

        //        public void setEditTextAndDbModel(EditText mEditText, DbModel model) {
//            this.mEditText = mEditText;
//            this.model = model;
//
//        }
        public void setEditText(EditText editText) {
            this.mEditText = editText;
        }

        public void setEditTextAndDbModel(int groupPosition, int childPosition) {
            this.groupPosition = groupPosition;
            this.childPosition = childPosition;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            this.beforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtils.hasEmoji(s.toString())) {
                mEditText.setText(TextUtils.isEmpty(beforeText) ? "" : beforeText);
                CToast.showShort(mContext, "请不要输入表情符号");
                return;
            }
            DbModel mDbmodel = getChild(groupPosition, childPosition);
            String value = TextUtils.isEmpty(mDbmodel.getString(DefectRecord.VAL)) ? "" : mDbmodel.getString(DefectRecord.VAL);
            String currentValue = s.toString().trim();
            if (!value.equals(currentValue)) {
                mDbmodel.add(DefectRecord.VAL, currentValue);
                mDbmodel.add(DefectRecord.OLDVAL, value);
            }
            Selection.setSelection(s, s.length());
        }

    }
}
