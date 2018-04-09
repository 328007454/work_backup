package com.cnksi.inspe.ui;

import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeCreateBinding;

/**
 * 创建检查任务
 * <br>
 * 功能说明：创建检查任务，只有主任、专责、组长职责有权创建任务。
 * <ol>
 * 创建的任务类型包含
 * <li><b>班组建设检查:</b>根据班组建设标准检查相应班</li>
 * <li><b>精益化检查:</b>根据设备标准检查相应设备</li>
 * <li><strike><b>设备排查:</b>2017-03-21本期暂时不做</strike></li>
 * </ol>
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:19
 */
public class InspeCreateActivity extends AppBaseActivity {

    private ActivityInspeCreateBinding dataBinding;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_create;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeCreateBinding) rootDataBinding;
        setTitle("添加任务", R.drawable.inspe_left_black_24dp);
    }

    @Override
    public void initData() {

    }
}
