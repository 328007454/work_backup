//package com.cnksi.sjjc.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.cnksi.core.fragment.BaseCoreFragment;
//import com.cnksi.sjjc.R;
//
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
///**
// * Created by han on 2016/5/9.
// * 参加人员（frament）
// */
//public class AttendPersonFragment extends BaseCoreFragment {
//    //参加人员页具体信息
//    @ViewInject(R.id.tv_content)
//    private TextView tvContent;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_attend_person, null);
//        x.view().inject(view);
//
//        return view;
//    }
//
//    @Override
//    protected void lazyLoad() {
//
//    }
//
//    @Override
//    protected void initUI() {
//
//    }
//
//    @Override
//    protected void initData() {
//        String content = getArguments().getString("0");
//        tvContent.setText(content);
//    }
//}
