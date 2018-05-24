package com.cnksi.sjjc.view.gztz;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.sjjc.activity.gztz.BHDZJLActivity;
import com.cnksi.sjjc.bean.gztz.BhyjBean;
import com.cnksi.sjjc.bean.gztz.SbjcGztzjlBhdzjl;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlSbBinding;
import com.cnksi.sjjc.inter.SimpleTextWatcher;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.sjjc.activity.gztz.TZQKActivity.NULL;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/8 21:34
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BhdzjlGroup {
    public GztzItemBhdzjlSbBinding binding;
    BHDZJLActivity activity;
    List<BhdzjlYjGroup> lists = new ArrayList<>();
    private onSelctListener listener;
    private SbjcGztzjlBhdzjl record;
    private DbModel bhsb;

    public BhdzjlGroup(BHDZJLActivity activity, ViewGroup group) {
        this.activity = activity;
        binding = GztzItemBhdzjlSbBinding.inflate(activity.getLayoutInflater(), group, true);
        addOtherYJLX();
        if (group.getChildCount() == 1) {
            binding.sbmc.setVisible(View.VISIBLE, View.GONE);
        } else {
            binding.sbmc.setVisible(View.GONE, View.VISIBLE);
        }
        binding.sbmc.setSelectOnClickListener(view -> listener.onSelectListener(BhdzjlGroup.this, false));
        binding.bhsbmc.setSelectOnClickListener(v -> listener.onSelectListener(BhdzjlGroup.this, true));
        binding.sbmc.getAddButton().setOnClickListener(view -> activity.addOtherDevice());

        binding.sbmc.getDeleteButton().setOnClickListener(view -> activity.removeView(BhdzjlGroup.this));
        binding.bhqdsj.tvValue.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                rebulidStr();
            }
        });
    }

    public void setRecord(SbjcGztzjlBhdzjl bhdzjl) {
        this.record = bhdzjl;
        binding.bhsbmc.setKeyValue(new KeyValue(record.bhSbmcK, record.bhSbmc));
        binding.sbmc.setKeyValue(new KeyValue(record.sbmcK, record.sbmc));
        binding.bhqdsj.setValueStr(record.bhqdsj);
        binding.bhdzpj.setValueStr(record.bhdzpj);
        if (!TextUtils.isEmpty(record.bhyjlxjdzsj)) {
            List<BhyjBean> bhyjBeans = JSON.parseArray(record.bhyjlxjdzsj, BhyjBean.class);
            if (!bhyjBeans.isEmpty()) {
                lists.get(0).setBhyjBean(bhyjBeans.get(0));
            }
            for (int i = 1; i < bhyjBeans.size(); i++) {
                addOtherYJLX().setBhyjBean(bhyjBeans.get(i));
            }
        }
    }

    public SbjcGztzjlBhdzjl getRecord() {
        KeyValue sbmc = binding.sbmc.getValue();
        KeyValue bhsbmc = binding.bhsbmc.getValue();
        String bhdzpj = binding.bhdzpj.getValueStr();
        String bhqdsj = binding.bhqdsj.getValueStr();
        List<BhyjBean> list = new ArrayList<>();
        for (BhdzjlYjGroup group : lists) {
            BhyjBean t = group.getBhyjBean();
            if (t != null) {
                list.add(t);
            }
        }
        String yjlx = JSON.toJSONString(list);
        if (sbmc == null && bhsbmc == null && TextUtils.isEmpty(yjlx)) {
            if (record != null) {
                record.dlt = 1;
            }
            return record;
        }
        sbmc = nullTo(sbmc);
        bhsbmc = nullTo(bhsbmc);

        if (record == null) {
            record = SbjcGztzjlBhdzjl.create();
        }
        record.sbmc = sbmc.getValueStr();
        record.sbmcK = sbmc.key;
        record.bhSbmc = bhsbmc.getValueStr();
        record.bhSbmcK = bhsbmc.key;
        record.bhdzpj = bhdzpj;
        record.bhqdsj = bhqdsj;
        record.bhyjlxjdzsj = yjlx;
        return record;
    }

    private KeyValue nullTo(KeyValue keyValue) {
        if (keyValue == null) {
            return NULL;
        } else {
            return keyValue;
        }
    }

    public BhdzjlYjGroup addOtherYJLX() {
        BhdzjlYjGroup group = new BhdzjlYjGroup(activity, this);
        lists.add(group);
        return group;
    }

    public void setListener(onSelctListener listener) {
        this.listener = listener;
    }

    public void removeView(BhdzjlYjGroup group) {
        lists.remove(group);
        binding.yjlx.removeView(group.getRoot());
    }

    public View getRoot() {
        return binding.getRoot();
    }

    public void setDeviceSelectValue(KeyValue value) {
        binding.sbmc.setKeyValue(value);
    }

    public void setBHDeviceSelectValue(KeyValue value) {
        binding.bhsbmc.setKeyValue(value);
    }

    public DbModel getBhsb() {
        return bhsb;
    }

    public void setBhsb(DbModel bhsb) {
        this.bhsb = bhsb;
    }

    public interface onSelctListener {
        void onSelectListener(BhdzjlGroup group, boolean isBhsb);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String bhxh = binding.bhsbmc.getValueStr();
        if (!TextUtils.isEmpty(bhxh)) {
            builder.append(bhxh).append("保护").append(StringUtilsExt.nullTo(binding.bhqdsj.getValueStr(), "  "))
                    .append("启动保护,");
            for (BhdzjlYjGroup list : lists) {
                builder.append(list.toString());
            }
            return builder.toString();
        }
        return "";
    }

    public void rebulidStr() {
        activity.rebuildStr();
    }
}
