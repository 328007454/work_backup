package com.cnksi.sjjc.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.util.FileUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class MainActivity extends BaseActivity {

    @ViewInject(R.id.out_sdcard)
    private TextView txtOutScard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        txtOutScard.setText(FileUtil.getOutCardPath());
    }

}
